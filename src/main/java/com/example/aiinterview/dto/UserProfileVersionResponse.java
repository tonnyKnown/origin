package com.example.aiinterview.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserProfileVersionResponse(
        Long versionId,
        Integer version,
        String profileSummary,
        List<String> weakPoints,
        List<String> strengthPoints,
        List<String> learningSuggestions,
        Integer evidenceCount,
        String modelName,
        LocalDateTime createdAt
) {
}
