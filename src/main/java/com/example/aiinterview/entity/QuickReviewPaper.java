package com.example.aiinterview.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuickReviewPaper {

    /** 快速复盘试卷主键 */
    private Long id;

    /** 生成试卷时使用的用户画像快照 */
    private String userProfile;

    /** 生成试卷时命中的薄弱点列表，使用分号分隔存储 */
    private String weakPoints;

    /** 生成画像时参考的证据数量快照 */
    private Integer evidenceCount;

    /** 试卷题目数量 */
    private Integer questionCount;

    /** 整卷提交后的总分 */
    private Integer totalScore;

    /** 试卷状态：RUNNING 未提交，SUBMITTED 已提交 */
    private String status;

    /** 整卷提交时间 */
    private LocalDateTime submittedAt;

    /** 试卷创建时间 */
    private LocalDateTime createdAt;

    /** 试卷最近更新时间 */
    private LocalDateTime updatedAt;
}
