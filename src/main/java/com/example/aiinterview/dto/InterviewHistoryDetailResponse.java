package com.example.aiinterview.dto;

import java.time.LocalDateTime;
import java.util.List;

public record InterviewHistoryDetailResponse(
        Long interviewId,
        String positionType,
        Integer totalScore,
        String status,
        String overallComment,
        String improvementAdvice,
        LocalDateTime createdAt,
        LocalDateTime finishedAt,
        List<SummaryResponse.QuestionSummary> questions
) {
}
