package com.example.aiinterview.dto;

public record QuickReviewQuestionResultResponse(
        Integer questionIndex,
        String knowledgePoint,
        String questionContent,
        String blankAnswer,
        String referenceAnswer,
        String explanation,
        String userAnswer,
        Integer score,
        Boolean correct,
        String comment,
        String suggestion
) {
}
