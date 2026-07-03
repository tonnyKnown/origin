package com.example.aiinterview.dto;

import java.time.LocalDateTime;

public record InterviewHistoryItemResponse(
        Long interviewId,
        String positionType,
        Integer totalScore,
        String status,
        LocalDateTime createdAt,
        LocalDateTime finishedAt
) {
}
