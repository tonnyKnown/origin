package com.example.aiinterview.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserProfileResponse(
        Long profileId,
        String profileSummary,
        List<String> weakPoints,
        List<String> strengthPoints,
        List<String> learningSuggestions,
        Integer evidenceCount,
        Integer version,
        String status,
        LocalDateTime updatedAt,
        boolean exists
) {
}
