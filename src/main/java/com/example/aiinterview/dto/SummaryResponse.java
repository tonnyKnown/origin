package com.example.aiinterview.dto;

import java.util.List;

public record SummaryResponse(
        Long interviewId,
        String positionType,
        Integer totalScore,
        String overallComment,
        String improvementAdvice,
        List<QuestionSummary> questions
) {

    public record QuestionSummary(
            Long questionId,
            Integer questionIndex,
            String questionType,
            String questionContent,
            String referenceAnswer,
            String userAnswer,
            Integer score,
            String answerSummary,
            String aiComment,
            String suggestion
    ) {
    }
}
