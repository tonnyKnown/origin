package com.example.aiinterview.dto;

public record AnswerScoreResponse(
        Long interviewId,
        Long questionId,
        Integer questionIndex,
        String questionType,
        Integer score,
        String referenceAnswer,
        String answerSummary,
        String aiComment,
        String suggestion,
        boolean finished
) {
}
