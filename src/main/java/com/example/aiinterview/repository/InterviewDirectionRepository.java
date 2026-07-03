package com.example.aiinterview.repository;

import com.example.aiinterview.entity.InterviewDirection;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface InterviewDirectionRepository {

    @Select("""
            SELECT id, parent_id, name, level, sort_order, enabled, description, created_at, updated_at
            FROM interview_direction
            WHERE id = #{id}
            """)
    InterviewDirection findById(Long id);

    @Select("""
            SELECT id, parent_id, name, level, sort_order, enabled, description, created_at, updated_at
            FROM interview_direction
            ORDER BY level ASC, sort_order ASC, id ASC
            """)
    List<InterviewDirection> findAll();

    @Select("""
            SELECT id, parent_id, name, level, sort_order, enabled, description, created_at, updated_at
            FROM interview_direction
            WHERE enabled = TRUE
            ORDER BY level ASC, sort_order ASC, id ASC
            """)
    List<InterviewDirection> findEnabled();

    @Select("""
            SELECT COUNT(*)
            FROM interview_direction
            WHERE parent_id = #{parentId}
            """)
    long countChildren(Long parentId);

    @Insert("""
            INSERT INTO interview_direction (
                parent_id, name, level, sort_order, enabled, description, created_at, updated_at
            ) VALUES (
                #{parentId}, #{name}, #{level}, #{sortOrder}, #{enabled}, #{description}, NOW(), NOW()
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InterviewDirection direction);

    @Update("""
            UPDATE interview_direction
            SET name = #{name},
                sort_order = #{sortOrder},
                enabled = #{enabled},
                description = #{description},
                updated_at = NOW()
            WHERE id = #{id}
            """)
    int update(InterviewDirection direction);

    @Update("""
            UPDATE interview_direction
            SET enabled = #{enabled},
                updated_at = NOW()
            WHERE id = #{id}
            """)
    int updateEnabled(@Param("id") Long id, @Param("enabled") Boolean enabled);
}
