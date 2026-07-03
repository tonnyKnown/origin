package com.example.aiinterview.repository;

import com.example.aiinterview.entity.InterviewSession;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface InterviewSessionRepository {

    @Select("""
            SELECT id, position_type, current_index, total_score, status,
                   overall_comment, improvement_advice, finished_at, created_at, updated_at
            FROM interview_session
            WHERE id = #{id}
            """)
    InterviewSession findById(Long id);

    @Select("""
            SELECT id, position_type, current_index, total_score, status,
                   overall_comment, improvement_advice, finished_at, created_at, updated_at
            FROM interview_session
            ORDER BY created_at DESC
            """)
    List<InterviewSession> findAllByOrderByCreatedAtDesc();

    @Select("""
            SELECT id, position_type, current_index, total_score, status,
                   overall_comment, improvement_advice, finished_at, created_at, updated_at
            FROM interview_session
            ORDER BY created_at DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    List<InterviewSession> findPageOrderByCreatedAtDesc(int offset, int size);

    @Select("""
            SELECT COUNT(*)
            FROM interview_session
            """)
    long countAll();

    @Insert("""
            INSERT INTO interview_session (
                position_type, current_index, total_score, status,
                overall_comment, improvement_advice, finished_at, created_at, updated_at
            ) VALUES (
                #{positionType}, #{currentIndex}, #{totalScore}, #{status},
                #{overallComment}, #{improvementAdvice}, #{finishedAt}, NOW(), NOW()
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InterviewSession session);

    @Update("""
            UPDATE interview_session
            SET position_type = #{positionType},
                current_index = #{currentIndex},
                total_score = #{totalScore},
                status = #{status},
                overall_comment = #{overallComment},
                improvement_advice = #{improvementAdvice},
                finished_at = #{finishedAt},
                updated_at = NOW()
            WHERE id = #{id}
            """)
    int update(InterviewSession session);

    @Delete("""
            DELETE FROM interview_session
            WHERE id = #{id}
            """)
    int deleteById(Long id);
}
