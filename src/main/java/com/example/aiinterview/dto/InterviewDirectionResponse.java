package com.example.aiinterview.dto;

import java.util.List;

public record InterviewDirectionResponse(
        Long id,
        Long parentId,
        String name,
        Integer level,
        Integer sortOrder,
        Boolean enabled,
        String description,
        List<InterviewDirectionResponse> children
) {
}
