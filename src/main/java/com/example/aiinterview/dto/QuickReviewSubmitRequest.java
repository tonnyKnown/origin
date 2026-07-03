package com.example.aiinterview.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QuickReviewSubmitRequest(
        @NotEmpty(message = "答案列表不能为空")
        List<@Valid AnswerItem> answers
) {

    public record AnswerItem(
            @NotNull(message = "题号不能为空")
            Integer questionIndex,

            String userAnswer
    ) {
    }
}
