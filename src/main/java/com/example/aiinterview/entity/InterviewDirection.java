package com.example.aiinterview.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewDirection {

    /** 面试方向节点主键 */
    private Long id;

    /** 父级方向节点 ID；顶层职业赛道为空 */
    private Long parentId;

    /** 方向节点名称 */
    private String name;

    /** 节点层级：1 职业赛道，2 技术领域，3 岗位画像 */
    private Integer level;

    /** 同一父节点下的展示排序值，越小越靠前 */
    private Integer sortOrder;

    /** 是否启用；禁用后不参与首页方向选择 */
    private Boolean enabled;

    /** 方向说明 */
    private String description;

    /** 节点创建时间 */
    private LocalDateTime createdAt;

    /** 节点最近更新时间 */
    private LocalDateTime updatedAt;
}
