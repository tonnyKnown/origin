package com.example.aiinterview.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewAnswer {

    /** 面试作答记录主键 */
    private Long id;

    /** 所属面试会话 ID */
    private Long interviewId;

    /** 对应面试题 ID */
    private Long questionId;

    /** 用户本次提交的答案内容 */
    private String userAnswer;

    /** AI 给出的本题得分 */
    private Integer score;

    /** AI 对用户答案的要点摘要 */
    private String answerSummary;

    /** AI 对本题作答质量的点评 */
    private String aiComment;

    /** 针对本题的改进建议 */
    private String suggestion;

    /** 作答首次创建时间 */
    private LocalDateTime createdAt;

    /** 作答最近一次修改或重新评分时间 */
    private LocalDateTime updatedAt;

    /** 当前题目答案的修改次数 */
    private Integer revisionCount = 0;
}
