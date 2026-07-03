package com.example.aiinterview.entity;

import java.time.LocalDateTime;

public class QuickReviewEvidence {

    /**
     * 证据来源类型：MANUAL、INTERVIEW、QUICK_REVIEW。
     */
    private String sourceType;

    /**
     * 证据关联的知识点或问题主题。
     */
    private String topic;

    /**
     * 证据详情，通常由问题、用户答案、评分、点评和建议拼装而成。
     */
    private String detail;

    /**
     * 证据中的得分；没有评分来源时为空。
     */
    private Integer score;

    /**
     * 手动题库中的掌握程度：LOW、MEDIUM、HIGH；非手动来源可为空。
     */
    private String understandingLevel;

    /**
     * 证据最近更新时间，用于后续按新旧程度排序或加权。
     */
    private LocalDateTime updatedAt;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getUnderstandingLevel() {
        return understandingLevel;
    }

    public void setUnderstandingLevel(String understandingLevel) {
        this.understandingLevel = understandingLevel;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
