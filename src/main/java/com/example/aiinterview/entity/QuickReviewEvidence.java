package com.example.aiinterview.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuickReviewEvidence {

    /** 证据来源类型：MANUAL、INTERVIEW、QUICK_REVIEW */
    private String sourceType;

    /** 证据关联的知识点或问题主题 */
    private String topic;

    /** 证据详情 */
    private String detail;

    /** 证据中的得分；没有评分来源时为空 */
    private Integer score;

    /** 手动题库中的掌握程度：LOW、MEDIUM、HIGH；非手动来源可为空 */
    private String understandingLevel;

    /** 证据最近更新时间 */
    private LocalDateTime updatedAt;
}
