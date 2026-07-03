package com.example.aiinterview.dto;

import java.util.List;

public record InterviewHistoryPageResponse(
        List<InterviewHistoryItemResponse> records,
        long total,
        int page,
        int size,
        int totalPages
) {
}
