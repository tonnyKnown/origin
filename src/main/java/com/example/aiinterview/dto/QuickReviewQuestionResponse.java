package com.example.aiinterview.dto;

public record QuickReviewQuestionResponse(
        Integer questionIndex,
        String knowledgePoint,
        String questionContent,
        String blankAnswer,
        String referenceAnswer,
        String explanation
) {
}
