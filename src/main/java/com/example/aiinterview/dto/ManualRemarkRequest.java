package com.example.aiinterview.dto;

import jakarta.validation.constraints.Size;

public record ManualRemarkRequest(
        @Size(max = 5000, message = "备注不能超过5000个字符")
        String manualRemark
) {
}
