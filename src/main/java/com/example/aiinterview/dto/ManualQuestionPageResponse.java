package com.example.aiinterview.dto;

import java.util.List;

public record ManualQuestionPageResponse(
        List<ManualQuestionResponse> records,
        long total,
        int page,
        int size,
        int totalPages,
        String keyword,
        String understandingLevel
) {
}
