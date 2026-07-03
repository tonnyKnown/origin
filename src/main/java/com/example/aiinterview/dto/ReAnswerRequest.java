package com.example.aiinterview.dto;

import jakarta.validation.constraints.NotBlank;

public record ReAnswerRequest(
        @NotBlank(message = "用户答案不能为空")
        String userAnswer
) {
}
