package com.example.aiinterview.entity;

import java.time.LocalDateTime;

public class ManualQuestion {

    /**
     * 手动题库记录主键。
     */
    private Long id;

    /**
     * 用户手动录入或提问的问题内容。
     */
    private String questionContent;

    /**
     * 用户对该问题的掌握程度：LOW、MEDIUM、HIGH。
     */
    private String understandingLevel;

    /**
     * AI 针对手动问题生成的参考答案。
     */
    private String aiAnswer;

    /**
     * AI 回答生成时间或用户提问完成时间。
     */
    private LocalDateTime answeredAt;

    /**
     * 用户对该问题的个人备注。
     */
    private String manualRemark;

    /**
     * 备注最近更新时间。
     */
    private LocalDateTime remarkUpdatedAt;

    /**
     * 用户自测时填写的答案。
     */
    private String selfTestAnswer;

    /**
     * 自测评分，通常用于衡量该题掌握情况。
     */
    private Integer selfTestScore;

    /**
     * AI 对自测答案的点评。
     */
    private String selfTestComment;

    /**
     * AI 对自测答案给出的学习建议。
     */
    private String selfTestSuggestion;

    /**
     * 最近一次自测时间。
     */
    private LocalDateTime selfTestAt;

    /**
     * 题库记录创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 题库记录最近更新时间。
     */
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
