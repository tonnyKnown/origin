package com.example.aiinterview.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileVersion {

    /** 用户画像版本记录主键 */
    private Long id;

    /** 对应 user_profile 主表 ID */
    private Long profileId;

    /** 画像版本号，与主表刷新后的版本号一致 */
    private Integer version;

    /** 该版本的用户能力画像摘要 */
    private String profileSummary;

    /** 该版本识别出的薄弱点列表 */
    private String weakPoints;

    /** 该版本识别出的优势点列表 */
    private String strengthPoints;

    /** 该版本对应的学习建议列表 */
    private String learningSuggestions;

    /** 生成该版本画像时输入给大模型的证据快照 */
    private String evidenceSnapshot;

    /** 生成该版本画像时参考的证据数量 */
    private Integer evidenceCount;

    /** 生成该版本画像所使用的大模型名称 */
    private String modelName;

    /** 版本记录创建时间 */
    private LocalDateTime createdAt;
}
