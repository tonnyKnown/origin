package com.example.aiinterview.entity;

import java.time.LocalDateTime;

public class InterviewAnswer {

    /**
     * 面试作答记录主键。
     */
    private Long id;

    /**
     * 所属面试会话 ID。
     */
    private Long interviewId;

    /**
     * 对应面试题 ID。
     */
    private Long questionId;

    /**
     * 用户本次提交的答案内容。
     */
    private String userAnswer;

    /**
     * AI 给出的本题得分。
     */
    private Integer score;

    /**
     * AI 对用户答案的要点摘要。
     */
    private String answerSummary;

    /**
     * AI 对本题作答质量的点评。
     */
    private String aiComment;

    /**
     * 针对本题的改进建议。
     */
    private String suggestion;

    /**
     * 作答首次创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 作答最近一次修改或重新评分时间。
     */
    private LocalDateTime updatedAt;

    /**
     * 当前题目答案的修改次数。
     */
    private Integer revisionCount = 0;

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

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
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

    public String getAnswerSummary() {
        return answerSummary;
    }

    public void setAnswerSummary(String answerSummary) {
        this.answerSummary = answerSummary;
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

    public Integer getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(Integer revisionCount) {
        this.revisionCount = revisionCount;
    }
}
