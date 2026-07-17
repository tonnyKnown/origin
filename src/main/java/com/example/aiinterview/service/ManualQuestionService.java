package com.example.aiinterview.service;

import com.example.aiinterview.dto.ManualQuestionPageResponse;
import com.example.aiinterview.dto.ManualQuestionResponse;
import com.example.aiinterview.entity.ManualQuestion;
import com.example.aiinterview.repository.ManualQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ManualQuestionService {

    private final ManualQuestionRepository manualQuestionRepository;

    private final AiInterviewClient aiInterviewClient;

    public ManualQuestionService(ManualQuestionRepository manualQuestionRepository,
                                 AiInterviewClient aiInterviewClient) {
        this.manualQuestionRepository = manualQuestionRepository;
        this.aiInterviewClient = aiInterviewClient;
    }

    @Transactional
    public ManualQuestionResponse create(String questionContent) {
        ManualQuestion manualQuestion = new ManualQuestion();
        manualQuestion.setQuestionContent(questionContent.trim());
        manualQuestion.setUnderstandingLevel("HIGH");
        manualQuestionRepository.insert(manualQuestion);
        return toResponse(manualQuestion);
    }

    @Transactional(readOnly = true)
    public List<ManualQuestionResponse> list() {
        return manualQuestionRepository.findAllOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ManualQuestionPageResponse page(int page, int size, String keyword, String understandingLevel) {
        int normalizedPage = Math.max(page, 1);
        int normalizedSize = Math.min(Math.max(size, 1), 50);
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        String normalizedLevel = normalizeUnderstandingLevel(understandingLevel, true);
        int offset = (normalizedPage - 1) * normalizedSize;
        long total = manualQuestionRepository.countByKeyword(normalizedKeyword, normalizedLevel);
        List<ManualQuestionResponse> records = manualQuestionRepository.findPage(normalizedKeyword, normalizedLevel, offset, normalizedSize).stream()
                .map(this::toResponse)
                .toList();
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / normalizedSize);
        return new ManualQuestionPageResponse(records, total, normalizedPage, normalizedSize, totalPages, normalizedKeyword, normalizedLevel);
    }

    @Transactional
    public ManualQuestionResponse answer(Long id) {
        ManualQuestion manualQuestion = getQuestion(id);
        String aiAnswer = aiInterviewClient.answerManualQuestion(manualQuestion.getQuestionContent());
        manualQuestionRepository.updateAnswer(id, aiAnswer);
        ManualQuestion updated = getQuestion(id);
        return toResponse(updated);
    }

    @Transactional
    public ManualQuestionResponse updateManualRemark(Long id, String manualRemark) {
        getQuestion(id);
        String normalizedRemark = manualRemark == null ? "" : manualRemark.trim();
        manualQuestionRepository.updateManualRemark(id, normalizedRemark);
        ManualQuestion updated = getQuestion(id);
        return toResponse(updated);
    }

    @Transactional
    public ManualQuestionResponse selfTest(Long id, String userAnswer) {
        ManualQuestion manualQuestion = getQuestion(id);
        String normalizedAnswer = userAnswer == null ? "" : userAnswer.trim();
        AiInterviewClient.ScoreResult scoreResult = aiInterviewClient.scoreManualSelfTest(
                manualQuestion.getQuestionContent(),
                normalizedAnswer
        );
        manualQuestionRepository.updateSelfTest(
                id,
                normalizedAnswer,
                scoreResult.score(),
                scoreResult.aiComment(),
                scoreResult.suggestion()
        );
        ManualQuestion updated = getQuestion(id);
        return toResponse(updated);
    }

    @Transactional
    public ManualQuestionResponse updateUnderstandingLevel(Long id, String understandingLevel) {
        getQuestion(id);
        String normalizedLevel = normalizeUnderstandingLevel(understandingLevel, false);
        manualQuestionRepository.updateUnderstandingLevel(id, normalizedLevel);
        ManualQuestion updated = getQuestion(id);
        return toResponse(updated);
    }

    @Transactional
    public ManualQuestionResponse updateQuestionContent(Long id, String questionContent) {
        getQuestion(id);
        String normalizedContent = questionContent == null ? "" : questionContent.trim();
        if (normalizedContent.isBlank()) {
            throw new IllegalArgumentException("问题内容不能为空");
        }
        manualQuestionRepository.updateQuestionContent(id, normalizedContent);
        ManualQuestion updated = getQuestion(id);
        return toResponse(updated);
    }

    private String normalizeUnderstandingLevel(String understandingLevel, boolean allowBlank) {
        String value = understandingLevel == null ? "" : understandingLevel.trim().toUpperCase();
        if (value.isBlank() && allowBlank) {
            return "";
        }
        return switch (value) {
            case "HIGH", "MEDIUM", "LOW" -> value;
            default -> throw new IllegalArgumentException("标签只能是HIGH、MEDIUM、LOW");
        };
    }

    private ManualQuestion getQuestion(Long id) {
        ManualQuestion manualQuestion = manualQuestionRepository.findById(id);
        if (manualQuestion == null) {
            throw new IllegalArgumentException("问题记录不存在");
        }
        return manualQuestion;
    }

    private ManualQuestionResponse toResponse(ManualQuestion manualQuestion) {
        return new ManualQuestionResponse(
                manualQuestion.getId(),
                manualQuestion.getQuestionContent(),
                manualQuestion.getUnderstandingLevel(),
                manualQuestion.getAiAnswer(),
                manualQuestion.getAnsweredAt(),
                manualQuestion.getManualRemark(),
                manualQuestion.getRemarkUpdatedAt(),
                manualQuestion.getSelfTestAnswer(),
                manualQuestion.getSelfTestScore(),
                manualQuestion.getSelfTestComment(),
                manualQuestion.getSelfTestSuggestion(),
                manualQuestion.getSelfTestAt(),
                manualQuestion.getCreatedAt(),
                manualQuestion.getUpdatedAt()
        );
    }
}
