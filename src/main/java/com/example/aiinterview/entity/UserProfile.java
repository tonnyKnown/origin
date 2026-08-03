package com.example.aiinterview.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfile {

    /** 当前生效用户画像主键 */
    private Long id;

    /** AI 生成的用户能力画像摘要 */
    private String profileSummary;

    /** 当前识别出的薄弱点列表，使用分号分隔存储 */
    private String weakPoints;

    /** 当前识别出的优势点列表，使用分号分隔存储 */
    private String strengthPoints;

    /** 针对当前画像生成的学习建议列表，使用分号分隔存储 */
    private String learningSuggestions;

    /** 本版本画像生成时参考的证据数量 */
    private Integer evidenceCount;

    /** 当前画像版本号，每次用户主动刷新后递增 */
    private Integer version;

    /** 画像状态，当前仅使用 ACTIVE 表示生效画像 */
    private String status;

    /** 画像首次创建时间 */
    private LocalDateTime createdAt;

    /** 画像最近一次更新时间 */
    private LocalDateTime updatedAt;
}
