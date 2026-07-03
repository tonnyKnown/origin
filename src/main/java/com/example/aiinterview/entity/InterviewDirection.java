package com.example.aiinterview.entity;

import java.time.LocalDateTime;

public class InterviewDirection {

    /**
     * 面试方向节点主键。
     */
    private Long id;

    /**
     * 父级方向节点 ID；顶层职业赛道为空。
     */
    private Long parentId;

    /**
     * 方向节点名称，例如职业赛道、技术领域或岗位画像名称。
     */
    private String name;

    /**
     * 节点层级：1 职业赛道，2 技术领域，3 岗位画像。
     */
    private Integer level;

    /**
     * 同一父节点下的展示排序值，越小越靠前。
     */
    private Integer sortOrder;

    /**
     * 是否启用；禁用后不参与首页方向选择。
     */
    private Boolean enabled;

    /**
     * 方向说明，用于管理页维护和后续扩展描述。
     */
    private String description;

    /**
     * 节点创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 节点最近更新时间。
     */
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
