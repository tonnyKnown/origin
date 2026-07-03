package com.example.aiinterview.entity;

import java.time.LocalDateTime;

public class InterviewQuestion {

    /**
     * 面试题主键。
     */
    private Long id;

    /**
     * 所属面试会话 ID。
     */
    private Long interviewId;

    /**
     * 当前会话内的题号，从 1 开始。
     */
    private Integer questionIndex;

    /**
     * 题目类型，例如普通方向面试题或复习面试题。
     */
    private String questionType;

    /**
     * 面试题正文。
     */
    private String questionContent;

    /**
     * AI 生成或沉淀的参考答案。
     */
    private String referenceAnswer;

    /**
     * AI 评分规则，用于后续作答评分。
     */
    private String scoringRule;

    /**
     * 题目创建时间。
     */
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Integer getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(Integer questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getReferenceAnswer() {
        return referenceAnswer;
    }

    public void setReferenceAnswer(String referenceAnswer) {
        this.referenceAnswer = referenceAnswer;
    }

    public String getScoringRule() {
        return scoringRule;
    }

    public void setScoringRule(String scoringRule) {
        this.scoringRule = scoringRule;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
