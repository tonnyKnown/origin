package com.example.aiinterview.entity;

import java.time.LocalDateTime;

public class QuickReviewQuestionItem {

    /**
     * 快速复盘题目主键。
     */
    private Long id;

    /**
     * 所属快速复盘试卷 ID。
     */
    private Long paperId;

    /**
     * 试卷内题号，从 1 开始。
     */
    private Integer questionIndex;

    /**
     * 本题考察的知识点。
     */
    private String knowledgePoint;

    /**
     * 填空题题干，空位使用下划线展示。
     */
    private String questionContent;

    /**
     * 填空题标准答案，通常是较短的关键词或术语。
     */
    private String blankAnswer;

    /**
     * 参考答案或扩展答案，用于辅助 AI 评分。
     */
    private String referenceAnswer;

    /**
     * 本题解析，用于学生查看错因和知识点理解。
     */
    private String explanation;

    /**
     * 用户提交的填空答案。
     */
    private String userAnswer;

    /**
     * AI 语义评分结果，单题满分通常为 20。
     */
    private Integer score;

    /**
     * 是否判定为命中核心答案。
     */
    private Boolean correct;

    /**
     * AI 对用户答案的点评。
     */
    private String aiComment;

    /**
     * AI 给出的后续练习或理解建议。
     */
    private String suggestion;

    /**
     * 用户提交该题答案的时间。
     */
    private LocalDateTime answeredAt;

    /**
     * 题目创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 题目最近更新时间。
     */
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Integer getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(Integer questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getKnowledgePoint() {
        return knowledgePoint;
    }

    public void setKnowledgePoint(String knowledgePoint) {
        this.knowledgePoint = knowledgePoint;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getBlankAnswer() {
        return blankAnswer;
    }

    public void setBlankAnswer(String blankAnswer) {
        this.blankAnswer = blankAnswer;
    }

    public String getReferenceAnswer() {
        return referenceAnswer;
    }

    public void setReferenceAnswer(String referenceAnswer) {
        this.referenceAnswer = referenceAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public String getAiComment() {
        return aiComment;
    }

    public void setAiComment(String aiComment) {
        this.aiComment = aiComment;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(LocalDateTime answeredAt) {
        this.answeredAt = answeredAt;
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
