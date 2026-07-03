package com.example.aiinterview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitAnswerRequest(
        @NotNull(message = "题目ID不能为空")
        Long questionId,

        @NotBlank(message = "用户答案不能为空")
        String userAnswer
) {
}
