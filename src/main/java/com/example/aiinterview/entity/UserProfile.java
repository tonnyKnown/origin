package com.example.aiinterview.entity;

import java.time.LocalDateTime;

public class UserProfile {

    /**
     * 当前生效用户画像主键。
     */
    private Long id;

    /**
     * AI 生成的用户能力画像摘要。
     */
    private String profileSummary;

    /**
     * 当前识别出的薄弱点列表，使用分号分隔存储。
     */
    private String weakPoints;

    /**
     * 当前识别出的优势点列表，使用分号分隔存储。
     */
    private String strengthPoints;

    /**
     * 针对当前画像生成的学习建议列表，使用分号分隔存储。
     */
    private String learningSuggestions;

    /**
     * 本版本画像生成时参考的证据数量。
     */
    private Integer evidenceCount;

    /**
     * 当前画像版本号，每次用户主动刷新后递增。
     */
    private Integer version;

    /**
     * 画像状态，当前仅使用 ACTIVE 表示生效画像。
     */
    private String status;

    /**
     * 画像首次创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 画像最近一次更新时间。
     */
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProfileSummary() {
        return profileSummary;
    }

    public void setProfileSummary(String profileSummary) {
        this.profileSummary = profileSummary;
    }

    public String getWeakPoints() {
        return weakPoints;
    }

    public void setWeakPoints(String weakPoints) {
        this.weakPoints = weakPoints;
    }

    public String getStrengthPoints() {
        return strengthPoints;
    }

    public void setStrengthPoints(String strengthPoints) {
        this.strengthPoints = strengthPoints;
    }

    public String getLearningSuggestions() {
        return learningSuggestions;
    }

    public void setLearningSuggestions(String learningSuggestions) {
        this.learningSuggestions = learningSuggestions;
    }

    public Integer getEvidenceCount() {
        return evidenceCount;
    }

    public void setEvidenceCount(Integer evidenceCount) {
        this.evidenceCount = evidenceCount;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
