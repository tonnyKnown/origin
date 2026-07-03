package com.example.aiinterview.dto;

import jakarta.validation.constraints.NotBlank;

public record QuickReviewAnswerRequest(
        @NotBlank(message = "题目内容不能为空")
        String questionContent,

        @NotBlank(message = "参考答案不能为空")
        String referenceAnswer,

        @NotBlank(message = "用户答案不能为空")
        String userAnswer
) {
}
