package com.example.aiinterview.dto;

import jakarta.validation.constraints.Size;

public record StartInterviewRequest(
        Long directionId,

        @Size(max = 200, message = "面试方向不能超过200个字符")
        String positionType
) {
}
