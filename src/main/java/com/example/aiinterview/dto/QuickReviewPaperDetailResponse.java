package com.example.aiinterview.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QuickReviewPaperDetailResponse(
        Long paperId,
        String userProfile,
        List<String> weakPoints,
        Integer evidenceCount,
        Integer totalScore,
        Integer fullScore,
        Integer correctCount,
        Integer questionCount,
        String status,
        LocalDateTime submittedAt,
        LocalDateTime createdAt,
        List<QuickReviewQuestionResultResponse> questions
) {
}
