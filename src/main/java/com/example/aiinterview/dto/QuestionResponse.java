package com.example.aiinterview.dto;

public record QuestionResponse(
        Long interviewId,
        Long questionId,
        Integer questionIndex,
        String questionType,
        Integer totalQuestions,
        String questionContent
) {
}
