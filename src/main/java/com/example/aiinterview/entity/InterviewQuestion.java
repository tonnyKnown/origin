package com.example.aiinterview.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewQuestion {

    /** 面试题主键 */
    private Long id;

    /** 所属面试会话 ID */
    private Long interviewId;

    /** 当前会话内的题号，从 1 开始 */
    private Integer questionIndex;

    /** 题目类型，例如普通方向面试题或复习面试题 */
    private String questionType;

    /** 面试题正文 */
    private String questionContent;

    /** AI 生成或沉淀的参考答案 */
    private String referenceAnswer;

    /** AI 评分规则，用于后续作答评分 */
    private String scoringRule;

    /** 题目创建时间 */
    private LocalDateTime createdAt;
}
