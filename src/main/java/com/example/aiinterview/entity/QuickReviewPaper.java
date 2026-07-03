package com.example.aiinterview.entity;

import java.time.LocalDateTime;

public class QuickReviewPaper {

    /**
     * 快速复盘试卷主键。
     */
    private Long id;

    /**
     * 生成试卷时使用的用户画像快照。
     */
    private String userProfile;

    /**
     * 生成试卷时命中的薄弱点列表，使用分号分隔存储。
     */
    private String weakPoints;

    /**
     * 生成画像时参考的证据数量快照。
     */
    private Integer evidenceCount;

    /**
     * 试卷题目数量。
     */
    private Integer questionCount;

    /**
     * 整卷提交后的总分。
     */
    private Integer totalScore;

    /**
     * 试卷状态：RUNNING 未提交，SUBMITTED 已提交。
     */
    private String status;

    /**
     * 整卷提交时间。
     */
    private LocalDateTime submittedAt;

    /**
     * 试卷创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 试卷最近更新时间。
     */
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getWeakPoints() {
        return weakPoints;
    }

    public void setWeakPoints(String weakPoints) {
        this.weakPoints = weakPoints;
    }

    public Integer getEvidenceCount() {
        return evidenceCount;
    }

    public void setEvidenceCount(Integer evidenceCount) {
        this.evidenceCount = evidenceCount;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
