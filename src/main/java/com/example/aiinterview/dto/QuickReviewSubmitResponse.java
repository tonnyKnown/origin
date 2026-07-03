package com.example.aiinterview.dto;

import java.util.List;

public record QuickReviewSubmitResponse(
        Long paperId,
        Integer totalScore,
        Integer fullScore,
        Integer correctCount,
        Integer questionCount,
        List<QuickReviewQuestionResultResponse> questions
) {
}
