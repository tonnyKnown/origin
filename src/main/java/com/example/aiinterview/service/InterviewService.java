package com.example.aiinterview.service;

import com.example.aiinterview.dto.AnswerScoreResponse;
import com.example.aiinterview.dto.InterviewHistoryDetailResponse;
import com.example.aiinterview.dto.InterviewHistoryItemResponse;
import com.example.aiinterview.dto.InterviewHistoryPageResponse;
import com.example.aiinterview.dto.QuestionResponse;
import com.example.aiinterview.dto.ReAnswerResponse;
import com.example.aiinterview.dto.SummaryResponse;
import com.example.aiinterview.entity.InterviewAnswer;
import com.example.aiinterview.entity.InterviewQuestion;
import com.example.aiinterview.entity.InterviewSession;
import com.example.aiinterview.entity.ManualQuestion;
import com.example.aiinterview.repository.InterviewAnswerRepository;
import com.example.aiinterview.repository.InterviewQuestionRepository;
import com.example.aiinterview.repository.InterviewSessionRepository;
import com.example.aiinterview.repository.ManualQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class InterviewService {

    private static final int TOTAL_QUESTIONS = 5;
    private static final String REVIEW_POSITION_TYPE = "REVIEW";
    private static final String REVIEW_QUESTION_TYPE = "REVIEW";
    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_FINISHED = "FINISHED";
    private static final String STATUS_CANCELLED = "CANCELLED";

    private final InterviewSessionRepository sessionRepository;
    private final InterviewQuestionRepository questionRepository;
    private final InterviewAnswerRepository answerRepository;
    private final ManualQuestionRepository manualQuestionRepository;
    private final InterviewDirectionService directionService;
    private final AiInterviewClient aiInterviewClient;

    public InterviewService(InterviewSessionRepository sessionRepository,
                            InterviewQuestionRepository questionRepository,
                            InterviewAnswerRepository answerRepository,
                            ManualQuestionRepository manualQuestionRepository,
                            InterviewDirectionService directionService,
                            AiInterviewClient aiInterviewClient) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.manualQuestionRepository = manualQuestionRepository;
        this.directionService = directionService;
        this.aiInterviewClient = aiInterviewClient;
    }

    @Transactional
    public QuestionResponse start(String positionType) {
        if (!StringUtils.hasText(positionType)) {
            throw new IllegalArgumentException("面试方向不能为空");
        }
        InterviewSession session = new InterviewSession();
        session.setPositionType(positionType.trim());
        session.setCurrentIndex(1);
        session.setTotalScore(0);
        session.setStatus(STATUS_RUNNING);
        sessionRepository.insert(session);

        InterviewQuestion question = createQuestion(session, 1);
        return toQuestionResponse(session, question);
    }

    @Transactional
    public QuestionResponse startByDirection(Long directionId) {
        if (directionId == null) {
            throw new IllegalArgumentException("面试方向不能为空");
        }
        return start(directionService.buildDirectionPath(directionId));
    }

    @Transactional
    public QuestionResponse startReview() {
        return start(REVIEW_POSITION_TYPE);
    }

    @Transactional
    public AnswerScoreResponse submitAnswer(Long interviewId, Long questionId, String userAnswer) {
        InterviewSession session = getSession(interviewId);
        ensureRunning(session);

        InterviewQuestion question = questionRepository.findById(questionId);
        if (question == null) {
            throw new IllegalArgumentException("题目不存在");
        }
        if (!question.getInterviewId().equals(interviewId)) {
            throw new IllegalArgumentException("题目不属于当前面试");
        }
        if (answerRepository.findByQuestionId(questionId) != null) {
            throw new IllegalStateException("该题已经提交过答案");
        }

        AiInterviewClient.ScoreResult scoreResult = aiInterviewClient.scoreAnswer(
                question.getQuestionContent(),
                question.getReferenceAnswer(),
                question.getScoringRule(),
                userAnswer
        );

        InterviewAnswer answer = new InterviewAnswer();
        answer.setInterviewId(interviewId);
        answer.setQuestionId(questionId);
        answer.setUserAnswer(userAnswer);
        answer.setScore(scoreResult.score());
        answer.setAnswerSummary(scoreResult.answerSummary());
        answer.setAiComment(scoreResult.aiComment());
        answer.setSuggestion(scoreResult.suggestion());
        answerRepository.insert(answer);

        int totalScore = answerRepository.findByInterviewIdOrderByIdAsc(interviewId).stream()
                .mapToInt(InterviewAnswer::getScore)
                .sum();
        session.setTotalScore(totalScore);

        boolean finished = question.getQuestionIndex() >= TOTAL_QUESTIONS;
        if (finished) {
            session.setStatus(STATUS_FINISHED);
            session.setFinishedAt(LocalDateTime.now());
            List<SummaryResponse.QuestionSummary> summaries = buildQuestionSummaries(interviewId);
            session.setOverallComment(buildOverallComment(session.getTotalScore(), summaries));
            session.setImprovementAdvice(buildAdvice(summaries));
        } else {
            session.setCurrentIndex(question.getQuestionIndex() + 1);
        }
        sessionRepository.update(session);

        return new AnswerScoreResponse(
                interviewId,
                questionId,
                question.getQuestionIndex(),
                question.getQuestionType(),
                scoreResult.score(),
                question.getReferenceAnswer(),
                scoreResult.answerSummary(),
                scoreResult.aiComment(),
                scoreResult.suggestion(),
                finished
        );
    }

    @Transactional
    public QuestionResponse nextQuestion(Long interviewId) {
        InterviewSession session = getSession(interviewId);
        ensureRunning(session);

        int nextIndex = session.getCurrentIndex();
        InterviewQuestion question = questionRepository.findByInterviewIdAndQuestionIndex(interviewId, nextIndex);
        if (question == null) {
            question = createQuestion(session, nextIndex);
        }

        return toQuestionResponse(session, question);
    }

    @Transactional
    public QuestionResponse continueInterview(Long interviewId) {
        return nextQuestion(interviewId);
    }

    @Transactional
    public void cancelInterview(Long interviewId) {
        InterviewSession session = getSession(interviewId);
        if (STATUS_FINISHED.equals(session.getStatus())) {
            throw new IllegalStateException("面试已经完成，不能取消");
        }
        if (STATUS_CANCELLED.equals(session.getStatus())) {
            return;
        }
        session.setStatus(STATUS_CANCELLED);
        session.setFinishedAt(LocalDateTime.now());
        session.setOverallComment("本次面试已由用户取消。");
        session.setImprovementAdvice("已取消的面试不会继续生成题目，可重新开始一次新的面试。");
        sessionRepository.update(session);
    }

    @Transactional(readOnly = true)
    public SummaryResponse summary(Long interviewId) {
        InterviewSession session = getSession(interviewId);
        List<SummaryResponse.QuestionSummary> questionSummaries = buildQuestionSummaries(interviewId);
        String overall = session.getOverallComment();
        String advice = session.getImprovementAdvice();
        if (overall == null || overall.isBlank()) {
            overall = buildOverallComment(session.getTotalScore(), questionSummaries);
        }
        if (advice == null || advice.isBlank()) {
            advice = buildAdvice(questionSummaries);
        }

        return new SummaryResponse(
                interviewId,
                session.getPositionType(),
                session.getTotalScore(),
                overall,
                advice,
                questionSummaries
        );
    }

    @Transactional(readOnly = true)
    public List<InterviewHistoryItemResponse> history() {
        return sessionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(session -> new InterviewHistoryItemResponse(
                        session.getId(),
                        session.getPositionType(),
                        session.getTotalScore(),
                        session.getStatus(),
                        session.getCreatedAt(),
                        session.getFinishedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public InterviewHistoryPageResponse historyPage(int page, int size) {
        int normalizedPage = Math.max(page, 1);
        int normalizedSize = Math.min(Math.max(size, 1), 50);
        int offset = (normalizedPage - 1) * normalizedSize;
        long total = sessionRepository.countAll();
        List<InterviewHistoryItemResponse> records = sessionRepository.findPageOrderByCreatedAtDesc(offset, normalizedSize).stream()
                .map(session -> new InterviewHistoryItemResponse(
                        session.getId(),
                        session.getPositionType(),
                        session.getTotalScore(),
                        session.getStatus(),
                        session.getCreatedAt(),
                        session.getFinishedAt()
                ))
                .toList();
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / normalizedSize);
        return new InterviewHistoryPageResponse(records, total, normalizedPage, normalizedSize, totalPages);
    }

    @Transactional(readOnly = true)
    public InterviewHistoryDetailResponse historyDetail(Long interviewId) {
        InterviewSession session = getSession(interviewId);
        List<SummaryResponse.QuestionSummary> questionSummaries = buildQuestionSummaries(interviewId);
        return new InterviewHistoryDetailResponse(
                session.getId(),
                session.getPositionType(),
                session.getTotalScore(),
                session.getStatus(),
                session.getOverallComment(),
                session.getImprovementAdvice(),
                session.getCreatedAt(),
                session.getFinishedAt(),
                questionSummaries
        );
    }

    @Transactional
    public ReAnswerResponse reAnswer(Long interviewId, Long questionId, String userAnswer) {
        InterviewSession session = getSession(interviewId);
        InterviewQuestion question = questionRepository.findById(questionId);
        if (question == null) {
            throw new IllegalArgumentException("题目不存在");
        }
        if (!question.getInterviewId().equals(interviewId)) {
            throw new IllegalArgumentException("题目不属于当前面试");
        }

        InterviewAnswer answer = answerRepository.findByQuestionId(questionId);
        if (answer == null) {
            throw new IllegalStateException("该题还没有原始答案，不能重新回答");
        }

        AiInterviewClient.ScoreResult scoreResult = aiInterviewClient.scoreAnswer(
                question.getQuestionContent(),
                question.getReferenceAnswer(),
                question.getScoringRule(),
                userAnswer
        );

        answer.setUserAnswer(userAnswer);
        answer.setScore(scoreResult.score());
        answer.setAnswerSummary(scoreResult.answerSummary());
        answer.setAiComment(scoreResult.aiComment());
        answer.setSuggestion(scoreResult.suggestion());
        answerRepository.updateAnswerContent(answer);

        int totalScore = answerRepository.findByInterviewIdOrderByIdAsc(interviewId).stream()
                .mapToInt(InterviewAnswer::getScore)
                .sum();
        session.setTotalScore(totalScore);

        List<SummaryResponse.QuestionSummary> summaries = buildQuestionSummaries(interviewId);
        session.setOverallComment(buildOverallComment(totalScore, summaries));
        session.setImprovementAdvice(buildAdvice(summaries));
        sessionRepository.update(session);

        return new ReAnswerResponse(
                interviewId,
                questionId,
                question.getQuestionIndex(),
                question.getQuestionType(),
                scoreResult.score(),
                question.getReferenceAnswer(),
                scoreResult.answerSummary(),
                scoreResult.aiComment(),
                scoreResult.suggestion(),
                totalScore
        );
    }

    @Transactional
    public void deleteHistory(Long interviewId) {
        getSession(interviewId);
        answerRepository.deleteByInterviewId(interviewId);
        questionRepository.deleteByInterviewId(interviewId);
        sessionRepository.deleteById(interviewId);
    }

    private InterviewQuestion createQuestion(InterviewSession session, int questionIndex) {
        if (isReviewInterview(session.getPositionType())) {
            return createReviewQuestion(session, questionIndex);
        }

        String askedQuestions = questionRepository.findByInterviewIdOrderByQuestionIndexAsc(session.getId()).stream()
                .map(InterviewQuestion::getQuestionContent)
                .collect(Collectors.joining("\n"));
        String questionType = resolveQuestionType(questionIndex);
        AiInterviewClient.GeneratedQuestion generatedQuestion = aiInterviewClient.generateQuestion(
                session.getPositionType(),
                questionIndex,
                questionType,
                askedQuestions
        );

        InterviewQuestion question = new InterviewQuestion();
        question.setInterviewId(session.getId());
        question.setQuestionIndex(questionIndex);
        question.setQuestionType(questionType);
        question.setQuestionContent(generatedQuestion.questionContent());
        question.setReferenceAnswer(generatedQuestion.referenceAnswer());
        question.setScoringRule(generatedQuestion.scoringRule());
        questionRepository.insert(question);
        return question;
    }

    private InterviewQuestion createReviewQuestion(InterviewSession session, int questionIndex) {
        long manualQuestionCount = manualQuestionRepository.countAll();
        if (manualQuestionCount == 0) {
            throw new IllegalStateException("请先在手动提问记录中添加题目，再开始复习面试");
        }

        int offset = (int) ((questionIndex - 1) % manualQuestionCount);
        ManualQuestion manualQuestion = manualQuestionRepository.findReviewQuestionByOffset(offset);
        if (manualQuestion == null) {
            throw new IllegalStateException("没有可用于复习的手动题目");
        }

        String referenceAnswer = manualQuestion.getAiAnswer();
        if (referenceAnswer == null || referenceAnswer.isBlank()) {
            referenceAnswer = aiInterviewClient.answerManualQuestion(manualQuestion.getQuestionContent());
            manualQuestionRepository.updateAnswer(manualQuestion.getId(), referenceAnswer);
        }

        InterviewQuestion question = new InterviewQuestion();
        question.setInterviewId(session.getId());
        question.setQuestionIndex(questionIndex);
        question.setQuestionType(REVIEW_QUESTION_TYPE);
        question.setQuestionContent(manualQuestion.getQuestionContent());
        question.setReferenceAnswer(referenceAnswer);
        question.setScoringRule("满分20分：核心概念准确8分，回答完整6分，结合实际场景4分，表达清晰2分。评分必须围绕该手动题目本身展开。");
        questionRepository.insert(question);
        return question;
    }

    private InterviewSession getSession(Long interviewId) {
        InterviewSession session = sessionRepository.findById(interviewId);
        if (session == null) {
            throw new IllegalArgumentException("面试不存在");
        }
        return session;
    }

    private QuestionResponse toQuestionResponse(InterviewSession session, InterviewQuestion question) {
        return new QuestionResponse(
                session.getId(),
                question.getId(),
                question.getQuestionIndex(),
                question.getQuestionType(),
                TOTAL_QUESTIONS,
                question.getQuestionContent()
        );
    }

    private List<SummaryResponse.QuestionSummary> buildQuestionSummaries(Long interviewId) {
        List<InterviewQuestion> questions = questionRepository.findByInterviewIdOrderByQuestionIndexAsc(interviewId);
        List<InterviewAnswer> answers = answerRepository.findByInterviewIdOrderByIdAsc(interviewId);
        Map<Long, InterviewAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(InterviewAnswer::getQuestionId, answer -> answer));

        return questions.stream()
                .sorted(Comparator.comparing(InterviewQuestion::getQuestionIndex))
                .map(question -> {
                    InterviewAnswer answer = answerMap.get(question.getId());
                    return new SummaryResponse.QuestionSummary(
                            question.getId(),
                            question.getQuestionIndex(),
                            question.getQuestionType(),
                            question.getQuestionContent(),
                            question.getReferenceAnswer(),
                            answer == null ? "" : answer.getUserAnswer(),
                            answer == null ? 0 : answer.getScore(),
                            answer == null ? "未作答" : answer.getAnswerSummary(),
                            answer == null ? "未作答" : answer.getAiComment(),
                            answer == null ? "请完成该题后查看建议" : answer.getSuggestion()
                    );
                })
                .toList();
    }

    private String buildSummaryDetail(List<SummaryResponse.QuestionSummary> questionSummaries) {
        return questionSummaries.stream()
                .map(item -> "第%d题：%s\n得分：%d\n回答：%s\n点评：%s\n建议：%s".formatted(
                        item.questionIndex(),
                        item.questionContent(),
                        item.score(),
                        item.userAnswer(),
                        item.aiComment(),
                        item.suggestion()
                ))
                .collect(Collectors.joining("\n\n"));
    }

    private String resolveQuestionType(int questionIndex) {
        return switch (questionIndex) {
            case 1 -> "BASIC";
            case 2 -> "CONCURRENCY";
            case 3 -> "MIDDLEWARE";
            case 4 -> "PROJECT";
            default -> "ARCHITECTURE";
        };
    }

    private boolean isReviewInterview(String positionType) {
        return REVIEW_POSITION_TYPE.equalsIgnoreCase(positionType);
    }

    private void ensureRunning(InterviewSession session) {
        if (STATUS_FINISHED.equals(session.getStatus())) {
            throw new IllegalStateException("面试已经结束");
        }
        if (STATUS_CANCELLED.equals(session.getStatus())) {
            throw new IllegalStateException("面试已经取消");
        }
    }

    private String buildOverallComment(Integer totalScore, List<SummaryResponse.QuestionSummary> questions) {
        long excellentCount = questions.stream().filter(question -> question.score() >= 16).count();
        long weakCount = questions.stream().filter(question -> question.score() < 14).count();
        if (totalScore >= 85) {
            return "整体表现优秀，基础知识、工程实践和架构表达都比较完整，高分题数量：" + excellentCount + "。";
        }
        if (totalScore >= 70) {
            return "整体表现良好，能够覆盖主要考点，但仍有" + weakCount + "道题需要补充细节和项目化表达。";
        }
        return "整体表现需要加强，建议优先复盘低分题，补齐Java基础、并发、中间件和架构设计的核心知识链路。";
    }

    private String buildAdvice(List<SummaryResponse.QuestionSummary> questions) {
        List<String> lowScoreItems = new ArrayList<>();
        for (SummaryResponse.QuestionSummary question : questions) {
            if (question.score() < 14) {
                lowScoreItems.add("第" + question.questionIndex() + "题需要重点复盘：" + question.suggestion());
            }
        }
        if (lowScoreItems.isEmpty()) {
            return "整体表现较稳定，建议继续补充真实项目案例、性能数据和架构取舍，让回答更有说服力。";
        }
        return String.join("\n", lowScoreItems);
    }
}
