package com.example.aiinterview.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QuickReviewPaperSummaryResponse(
        Long paperId,
        String userProfile,
        List<String> weakPoints,
        Integer totalScore,
        Integer fullScore,
        Integer questionCount,
        String status,
        LocalDateTime submittedAt,
        LocalDateTime createdAt
) {
}
