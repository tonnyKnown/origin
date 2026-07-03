package com.example.aiinterview.dto;

import java.util.List;

public record QuickReviewStartResponse(
        Long paperId,
        String userProfile,
        List<String> weakPoints,
        List<QuickReviewQuestionResponse> questions,
        Integer evidenceCount
) {
}
