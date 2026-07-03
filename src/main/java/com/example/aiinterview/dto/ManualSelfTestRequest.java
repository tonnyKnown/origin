package com.example.aiinterview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ManualSelfTestRequest(
        @NotBlank(message = "自测答案不能为空")
        @Size(max = 10000, message = "自测答案不能超过10000个字符")
        String userAnswer
) {
}
