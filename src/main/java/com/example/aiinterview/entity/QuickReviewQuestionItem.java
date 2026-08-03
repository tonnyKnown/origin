package com.example.aiinterview.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuickReviewQuestionItem {

    /** 快速复盘题目主键 */
    private Long id;

    /** 所属快速复盘试卷 ID */
    private Long paperId;

    /** 试卷内题号，从 1 开始 */
    private Integer questionIndex;

    /** 本题考察的知识点 */
    private String knowledgePoint;

    /** 填空题题干，空位使用下划线展示 */
    private String questionContent;

    /** 填空题标准答案 */
    private String blankAnswer;

    /** 参考答案或扩展答案，用于辅助 AI 评分 */
    private String referenceAnswer;

    /** 本题解析 */
    private String explanation;

    /** 用户提交的填空答案 */
    private String userAnswer;

    /** AI 语义评分结果，单题满分通常为 20 */
    private Integer score;

    /** 是否判定为命中核心答案 */
    private Boolean correct;

    /** AI 对用户答案的点评 */
    private String aiComment;

    /** AI 给出的后续练习或理解建议 */
    private String suggestion;

    /** 用户提交该题答案的时间 */
    private LocalDateTime answeredAt;

    /** 题目创建时间 */
    private LocalDateTime createdAt;

    /** 题目最近更新时间 */
    private LocalDateTime updatedAt;
}
