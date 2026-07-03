package com.example.aiinterview.entity;

import java.time.LocalDateTime;

public class ManualQuestion {

    private Long id;

    private String questionContent;

    private String understandingLevel;

    private String aiAnswer;

    private LocalDateTime answeredAt;

    private String manualRemark;

    private LocalDateTime remarkUpdatedAt;

    private String selfTestAnswer;

    private Integer selfTestScore;

    private String selfTestComment;

    private String selfTestSuggestion;

    private LocalDateTime selfTestAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getUnderstandingLevel() {
        return understandingLevel;
    }

    public void setUnderstandingLevel(String understandingLevel) {
        this.understandingLevel = understandingLevel;
    }

    public String getAiAnswer() {
        return aiAnswer;
    }

    public void setAiAnswer(String aiAnswer) {
        this.aiAnswer = aiAnswer;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(LocalDateTime answeredAt) {
        this.answeredAt = answeredAt;
    }

    public String getManualRemark() {
        return manualRemark;
    }

    public void setManualRemark(String manualRemark) {
        this.manualRemark = manualRemark;
    }

    public LocalDateTime getRemarkUpdatedAt() {
        return remarkUpdatedAt;
    }

    public void setRemarkUpdatedAt(LocalDateTime remarkUpdatedAt) {
        this.remarkUpdatedAt = remarkUpdatedAt;
    }

    public String getSelfTestAnswer() {
        return selfTestAnswer;
    }

    public void setSelfTestAnswer(String selfTestAnswer) {
        this.selfTestAnswer = selfTestAnswer;
    }

    public Integer getSelfTestScore() {
        return selfTestScore;
    }

    public void setSelfTestScore(Integer selfTestScore) {
        this.selfTestScore = selfTestScore;
    }

    public String getSelfTestComment() {
        return selfTestComment;
    }

    public void setSelfTestComment(String selfTestComment) {
        this.selfTestComment = selfTestComment;
    }

    public String getSelfTestSuggestion() {
        return selfTestSuggestion;
    }

    public void setSelfTestSuggestion(String selfTestSuggestion) {
        this.selfTestSuggestion = selfTestSuggestion;
    }

    public LocalDateTime getSelfTestAt() {
        return selfTestAt;
    }

    public void setSelfTestAt(LocalDateTime selfTestAt) {
        this.selfTestAt = selfTestAt;
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
