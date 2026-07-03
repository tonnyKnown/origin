package com.example.aiinterview.dto;

import java.time.LocalDateTime;

public record ManualQuestionResponse(
        Long id,
        String questionContent,
        String understandingLevel,
        String aiAnswer,
        LocalDateTime answeredAt,
        String manualRemark,
        LocalDateTime remarkUpdatedAt,
        String selfTestAnswer,
        Integer selfTestScore,
        String selfTestComment,
        String selfTestSuggestion,
        LocalDateTime selfTestAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
