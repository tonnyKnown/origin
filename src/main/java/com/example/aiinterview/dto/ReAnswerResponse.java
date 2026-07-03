package com.example.aiinterview.dto;

public record ReAnswerResponse(
        Long interviewId,
        Long questionId,
        Integer questionIndex,
        String questionType,
        Integer score,
        String referenceAnswer,
        String answerSummary,
        String aiComment,
        String suggestion,
        Integer totalScore
) {
}
