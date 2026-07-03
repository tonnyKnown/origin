package com.example.aiinterview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ManualQuestionRequest(
        @NotBlank(message = "问题不能为空")
        @Size(max = 2000, message = "问题不能超过2000个字符")
        String questionContent
) {
}
