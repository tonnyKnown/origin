package com.example.aiinterview.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewSession {

    /** 面试会话主键 */
    private Long id;

    /** 面试方向或模式名称，例如"软件开发方向 / Java / Java 后端开发"或"复习面试" */
    private String positionType;

    /** 当前进行到的题号，从 1 开始 */
    private Integer currentIndex = 1;

    /** 当前会话累计总分 */
    private Integer totalScore = 0;

    /** 会话状态：RUNNING 进行中，FINISHED 已完成，CANCELLED 已取消 */
    private String status = "RUNNING";

    /** 面试完成后的整体点评 */
    private String overallComment;

    /** 面试完成后的整体改进建议 */
    private String improvementAdvice;

    /** 会话完成或取消时间 */
    private LocalDateTime finishedAt;

    /** 会话创建时间 */
    private LocalDateTime createdAt;

    /** 会话最近更新时间 */
    private LocalDateTime updatedAt;
}
