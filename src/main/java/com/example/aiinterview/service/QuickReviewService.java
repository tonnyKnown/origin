package com.example.aiinterview.service;

import com.example.aiinterview.dto.QuickReviewQuestionResponse;
import com.example.aiinterview.dto.QuickReviewQuestionResultResponse;
import com.example.aiinterview.dto.QuickReviewPaperDetailResponse;
import com.example.aiinterview.dto.QuickReviewPaperSummaryResponse;
import com.example.aiinterview.dto.QuickReviewScoreResponse;
import com.example.aiinterview.dto.QuickReviewStartResponse;
import com.example.aiinterview.dto.QuickReviewSubmitRequest;
import com.example.aiinterview.dto.QuickReviewSubmitResponse;
import com.example.aiinterview.entity.QuickReviewEvidence;
import com.example.aiinterview.entity.QuickReviewPaper;
import com.example.aiinterview.entity.QuickReviewQuestionItem;
import com.example.aiinterview.entity.UserProfile;
import com.example.aiinterview.repository.QuickReviewEvidenceRepository;
import com.example.aiinterview.repository.QuickReviewPaperRepository;
import com.example.aiinterview.repository.QuickReviewQuestionRepository;
import com.example.aiinterview.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuickReviewService {

    private static final int QUESTION_COUNT = 10;

    private final QuickReviewEvidenceRepository evidenceRepository;

    private final QuickReviewPaperRepository paperRepository;

    private final QuickReviewQuestionRepository questionRepository;

    private final UserProfileRepository userProfileRepository;

    private final AiInterviewClient aiInterviewClient;

    public QuickReviewService(QuickReviewEvidenceRepository evidenceRepository,
                              QuickReviewPaperRepository paperRepository,
                              QuickReviewQuestionRepository questionRepository,
                              UserProfileRepository userProfileRepository,
                              AiInterviewClient aiInterviewClient) {
        this.evidenceRepository = evidenceRepository;
        this.paperRepository = paperRepository;
        this.questionRepository = questionRepository;
        this.userProfileRepository = userProfileRepository;
        this.aiInterviewClient = aiInterviewClient;
    }

    @Transactional
    public QuickReviewStartResponse start() {
        UserProfile profile = userProfileRepository.findActive();
        if (profile == null) {
            throw new IllegalStateException("请先在首页点击“更新用户画像”，再生成快速复盘试卷。");
        }

        // 快速复盘只消费已维护好的画像，不在这里临时生成画像。
        List<String> weakPoints = splitWeakPoints(profile.getWeakPoints());
        List<String> learningSuggestions = splitWeakPoints(profile.getLearningSuggestions());
        AiInterviewClient.QuickReviewResult result = aiInterviewClient.generateQuickReviewFromProfile(
                profile.getProfileSummary(),
                weakPoints,
                learningSuggestions,
                QUESTION_COUNT
        );

        QuickReviewPaper paper = new QuickReviewPaper();
        paper.setUserProfile(profile.getProfileSummary());
        paper.setWeakPoints(String.join("；", result.weakPoints()));
        paper.setEvidenceCount(profile.getEvidenceCount());
        paper.setQuestionCount(result.questions().size());
        paper.setTotalScore(0);
        paper.setStatus("RUNNING");
        paperRepository.insert(paper);

        // 先落库题目再返回前端，后续整卷提交才能稳定按题号批改和回看。
        for (AiInterviewClient.QuickReviewQuestion question : result.questions()) {
            QuickReviewQuestionItem item = new QuickReviewQuestionItem();
            item.setPaperId(paper.getId());
            item.setQuestionIndex(question.questionIndex());
            item.setKnowledgePoint(question.knowledgePoint());
            item.setQuestionContent(question.questionContent());
            item.setBlankAnswer(question.blankAnswer());
            item.setReferenceAnswer(question.referenceAnswer());
            item.setExplanation(question.explanation());
            questionRepository.insert(item);
        }

        return new QuickReviewStartResponse(
                paper.getId(),
                result.userProfile(),
                result.weakPoints(),
                result.questions().stream()
                        .map(this::toQuestionResponse)
                        .toList(),
                profile.getEvidenceCount()
        );
    }

    @Transactional
    public QuickReviewSubmitResponse submit(Long paperId, QuickReviewSubmitRequest request) {
        QuickReviewPaper paper = requirePaper(paperId);
        List<QuickReviewQuestionItem> questions = questionRepository.findByPaperIdOrderByQuestionIndexAsc(paperId);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("试卷题目不存在");
        }

        Map<Integer, String> answerMap = request.answers().stream()
                .collect(Collectors.toMap(
                        QuickReviewSubmitRequest.AnswerItem::questionIndex,
                        item -> item.userAnswer() == null ? "" : item.userAnswer().trim(),
                        (left, right) -> right
                ));

        // 整卷提交时逐题调用评分逻辑，最后统一更新试卷总分和状态。
        int totalScore = 0;
        int correctCount = 0;
        List<QuickReviewQuestionResultResponse> results = new ArrayList<>();
        for (QuickReviewQuestionItem question : questions) {
            String userAnswer = answerMap.getOrDefault(question.getQuestionIndex(), "");
            QuickReviewQuestionItem scored = scoreQuestion(question, userAnswer);
            questionRepository.updateAnswer(scored);
            totalScore += scored.getScore() == null ? 0 : scored.getScore();
            if (Boolean.TRUE.equals(scored.getCorrect())) {
                correctCount++;
            }
            results.add(toQuestionResult(scored));
        }

        paper.setTotalScore(totalScore);
        paper.setStatus("SUBMITTED");
        paperRepository.finish(paper);

        return new QuickReviewSubmitResponse(
                paperId,
                totalScore,
                questions.size() * 20,
                correctCount,
                questions.size(),
                results
        );
    }

    @Transactional(readOnly = true)
    public List<QuickReviewPaperSummaryResponse> listHistory(int page, int size) {
        int normalizedPage = Math.max(page, 1);
        int normalizedSize = Math.min(Math.max(size, 1), 50);
        int offset = (normalizedPage - 1) * normalizedSize;
        return paperRepository.findPageOrderByCreatedAtDesc(offset, normalizedSize).stream()
                .map(this::toPaperSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuickReviewPaperDetailResponse getHistoryDetail(Long paperId) {
        QuickReviewPaper paper = requirePaper(paperId);
        List<QuickReviewQuestionItem> questions = questionRepository.findByPaperIdOrderByQuestionIndexAsc(paperId);
        int correctCount = (int) questions.stream()
                .filter(question -> Boolean.TRUE.equals(question.getCorrect()))
                .count();
        return new QuickReviewPaperDetailResponse(
                paper.getId(),
                paper.getUserProfile(),
                splitWeakPoints(paper.getWeakPoints()),
                paper.getEvidenceCount(),
                paper.getTotalScore(),
                questions.size() * 20,
                correctCount,
                questions.size(),
                paper.getStatus(),
                paper.getSubmittedAt(),
                paper.getCreatedAt(),
                questions.stream().map(this::toQuestionResult).toList()
        );
    }

    public QuickReviewScoreResponse score(String questionContent, String referenceAnswer, String userAnswer) {
        String normalizedAnswer = userAnswer == null ? "" : userAnswer.trim();
        if (!StringUtils.hasText(normalizedAnswer)) {
            throw new IllegalArgumentException("答案不能为空");
        }
        AiInterviewClient.QuickReviewScore score = aiInterviewClient.scoreQuickReviewAnswer(
                questionContent,
                referenceAnswer,
                normalizedAnswer
        );
        return new QuickReviewScoreResponse(
                score.score(),
                score.correct(),
                score.comment(),
                score.suggestion()
        );
    }

    private QuickReviewQuestionItem scoreQuestion(QuickReviewQuestionItem question, String userAnswer) {
        question.setUserAnswer(userAnswer);
        if (!StringUtils.hasText(userAnswer)) {
            // 未作答题不调用大模型，直接按 0 分沉淀，避免空答案污染评分证据。
            question.setScore(0);
            question.setCorrect(false);
            question.setAiComment("未作答，无法覆盖核心知识点。");
            question.setSuggestion("先回看标准答案和解析，再用自己的话补齐这个空。");
            return question;
        }

        AiInterviewClient.QuickReviewScore score = aiInterviewClient.scoreQuickReviewAnswer(
                question.getQuestionContent(),
                StringUtils.hasText(question.getBlankAnswer()) ? question.getBlankAnswer() : question.getReferenceAnswer(),
                userAnswer
        );
        question.setScore(score.score());
        question.setCorrect(score.correct());
        question.setAiComment(score.comment());
        question.setSuggestion(score.suggestion());
        return question;
    }

    private QuickReviewQuestionResponse toQuestionResponse(AiInterviewClient.QuickReviewQuestion question) {
        return new QuickReviewQuestionResponse(
                question.questionIndex(),
                question.knowledgePoint(),
                question.questionContent(),
                question.blankAnswer(),
                question.referenceAnswer(),
                question.explanation()
        );
    }

    private QuickReviewQuestionResultResponse toQuestionResult(QuickReviewQuestionItem question) {
        return new QuickReviewQuestionResultResponse(
                question.getQuestionIndex(),
                question.getKnowledgePoint(),
                question.getQuestionContent(),
                question.getBlankAnswer(),
                question.getReferenceAnswer(),
                question.getExplanation(),
                question.getUserAnswer(),
                question.getScore(),
                question.getCorrect(),
                question.getAiComment(),
                question.getSuggestion()
        );
    }

    private QuickReviewPaperSummaryResponse toPaperSummary(QuickReviewPaper paper) {
        int questionCount = paper.getQuestionCount() == null ? 0 : paper.getQuestionCount();
        return new QuickReviewPaperSummaryResponse(
                paper.getId(),
                paper.getUserProfile(),
                splitWeakPoints(paper.getWeakPoints()),
                paper.getTotalScore(),
                questionCount * 20,
                questionCount,
                paper.getStatus(),
                paper.getSubmittedAt(),
                paper.getCreatedAt()
        );
    }

    private QuickReviewPaper requirePaper(Long paperId) {
        QuickReviewPaper paper = paperRepository.findById(paperId);
        if (paper == null) {
            throw new IllegalArgumentException("复盘试卷不存在");
        }
        return paper;
    }

    private List<String> splitWeakPoints(String weakPoints) {
        if (!StringUtils.hasText(weakPoints)) {
            return Collections.emptyList();
        }
        return List.of(weakPoints.split("[;；]")).stream()
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
    }

    private String buildEvidenceText(List<QuickReviewEvidence> evidenceList) {
        if (evidenceList.isEmpty()) {
            return """
                    暂无明确历史弱点。请按 Java 后端通用高频薄弱点生成画像：
                    分布式事务、缓存异常、JVM 排查、消息可靠性、MySQL 索引、Spring AOP、线程池。
                    """;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < evidenceList.size(); i++) {
            QuickReviewEvidence evidence = evidenceList.get(i);
            builder.append(i + 1)
                    .append(". 来源=").append(safe(evidence.getSourceType()))
                    .append("；主题=").append(safe(evidence.getTopic()))
                    .append("；得分=").append(evidence.getScore() == null ? "-" : evidence.getScore())
                    .append("；掌握度=").append(safe(evidence.getUnderstandingLevel()))
                    .append("；详情=").append(safe(evidence.getDetail()))
                    .append("\n");
        }
        return builder.toString();
    }

    private String safe(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim();
    }
}
