package com.example.aiinterview.entity;

import java.time.LocalDateTime;

public class UserProfileVersion {

    /**
     * 用户画像版本记录主键。
     */
    private Long id;

    /**
     * 对应 user_profile 主表 ID。
     */
    private Long profileId;

    /**
     * 画像版本号，与主表刷新后的版本号一致。
     */
    private Integer version;

    /**
     * 该版本的用户能力画像摘要。
     */
    private String profileSummary;

    /**
     * 该版本识别出的薄弱点列表，使用分号分隔存储。
     */
    private String weakPoints;

    /**
     * 该版本识别出的优势点列表，使用分号分隔存储。
     */
    private String strengthPoints;

    /**
     * 该版本对应的学习建议列表，使用分号分隔存储。
     */
    private String learningSuggestions;

    /**
     * 生成该版本画像时输入给大模型的证据快照。
     */
    private String evidenceSnapshot;

    /**
     * 生成该版本画像时参考的证据数量。
     */
    private Integer evidenceCount;

    /**
     * 生成该版本画像所使用的大模型名称。
     */
    private String modelName;

    /**
     * 版本记录创建时间。
     */
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public String getEvidenceSnapshot() {
        return evidenceSnapshot;
    }

    public void setEvidenceSnapshot(String evidenceSnapshot) {
        this.evidenceSnapshot = evidenceSnapshot;
    }

    public Integer getEvidenceCount() {
        return evidenceCount;
    }

    public void setEvidenceCount(Integer evidenceCount) {
        this.evidenceCount = evidenceCount;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
