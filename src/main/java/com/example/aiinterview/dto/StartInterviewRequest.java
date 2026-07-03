package com.example.aiinterview.dto;

import jakarta.validation.constraints.NotBlank;

public record StartInterviewRequest(
        @NotBlank(message = "面试方向不能为空")
        String positionType
) {
}
