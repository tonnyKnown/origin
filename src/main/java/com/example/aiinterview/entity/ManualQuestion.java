package com.example.aiinterview.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ManualQuestion {

    /** 手动题库记录主键 */
    private Long id;

    /** 用户手动录入或提问的问题内容 */
    private String questionContent;

    /** 用户对该问题的掌握程度：LOW、MEDIUM、HIGH */
    private String understandingLevel;

    /** AI 针对手动问题生成的参考答案 */
    private String aiAnswer;

    /** AI 回答生成时间或用户提问完成时间 */
    private LocalDateTime answeredAt;

    /** 用户对该问题的个人备注 */
    private String manualRemark;

    /** 备注最近更新时间 */
    private LocalDateTime remarkUpdatedAt;

    /** 用户自测时填写的答案 */
    private String selfTestAnswer;

    /** 自测评分 */
    private Integer selfTestScore;

    /** AI 对自测答案的点评 */
    private String selfTestComment;

    /** AI 对自测答案给出的学习建议 */
    private String selfTestSuggestion;

    /** 最近一次自测时间 */
    private LocalDateTime selfTestAt;

    /** 题库记录创建时间 */
    private LocalDateTime createdAt;

    /** 题库记录最近更新时间 */
    private LocalDateTime updatedAt;
}
