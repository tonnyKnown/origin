package com.example.aiinterview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InterviewDirectionRequest(
        Long parentId,
        @NotBlank(message = "方向名称不能为空")
        @Size(max = 100, message = "方向名称不能超过100个字符")
        String name,
        Integer sortOrder,
        Boolean enabled,
        @Size(max = 500, message = "方向说明不能超过500个字符")
        String description
) {
}
