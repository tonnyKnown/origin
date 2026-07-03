package com.example.aiinterview.dto;

import jakarta.validation.constraints.NotBlank;

public record ManualQuestionTagRequest(
        @NotBlank(message = "标签不能为空")
        String understandingLevel
) {
}
