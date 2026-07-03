package com.example.aiinterview.entity;

import java.time.LocalDateTime;

public class InterviewSession {

    /**
     * 面试会话主键。
     */
    private Long id;

    /**
     * 面试方向或模式名称，例如“软件开发方向 / Java / Java 后端开发”或“复习面试”。
     */
    private String positionType;

    /**
     * 当前进行到的题号，从 1 开始。
     */
    private Integer currentIndex = 1;

    /**
     * 当前会话累计总分。
     */
    private Integer totalScore = 0;

    /**
     * 会话状态：RUNNING 进行中，FINISHED 已完成，CANCELLED 已取消。
     */
    private String status = "RUNNING";

    /**
     * 面试完成后的整体点评。
     */
    private String overallComment;

    /**
     * 面试完成后的整体改进建议。
     */
    private String improvementAdvice;

    /**
     * 会话完成或取消时间。
     */
    private LocalDateTime finishedAt;

    /**
     * 会话创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 会话最近更新时间。
     */
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPositionType() {
        return positionType;
    }

    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    public Integer getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(Integer currentIndex) {
        this.currentIndex = currentIndex;
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

    public String getOverallComment() {
        return overallComment;
    }

    public void setOverallComment(String overallComment) {
        this.overallComment = overallComment;
    }

    public String getImprovementAdvice() {
        return improvementAdvice;
    }

    public void setImprovementAdvice(String improvementAdvice) {
        this.improvementAdvice = improvementAdvice;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
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
