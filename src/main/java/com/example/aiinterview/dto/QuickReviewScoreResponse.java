package com.example.aiinterview.dto;

public record QuickReviewScoreResponse(
        Integer score,
        Boolean correct,
        String comment,
        String suggestion
) {
}
