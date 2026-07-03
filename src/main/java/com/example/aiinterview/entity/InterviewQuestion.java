package com.example.aiinterview.entity;

import java.time.LocalDateTime;

public class InterviewQuestion {

    private Long id;

    private Long interviewId;

    private Integer questionIndex;

    private String questionType;

    private String questionContent;

    private String referenceAnswer;

    private String scoringRule;

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
