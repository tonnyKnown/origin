package com.example.aiinterview.repository;

import com.example.aiinterview.entity.QuickReviewPaper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface QuickReviewPaperRepository {

    @Select("""
            SELECT id, user_profile, weak_points, evidence_count, question_count,
                   total_score, status, submitted_at, created_at, updated_at
            FROM quick_review_paper
            WHERE id = #{id}
            """)
    QuickReviewPaper findById(Long id);

    @Select("""
            SELECT id, user_profile, weak_points, evidence_count, question_count,
                   total_score, status, submitted_at, created_at, updated_at
            FROM quick_review_paper
            ORDER BY created_at DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    List<QuickReviewPaper> findPageOrderByCreatedAtDesc(int offset, int size);

    @Insert("""
            INSERT INTO quick_review_paper (
                user_profile, weak_points, evidence_count, question_count,
                total_score, status, submitted_at, created_at, updated_at
            ) VALUES (
                #{userProfile}, #{weakPoints}, #{evidenceCount}, #{questionCount},
                #{totalScore}, #{status}, #{submittedAt}, NOW(), NOW()
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(QuickReviewPaper paper);

    @Update("""
            UPDATE quick_review_paper
            SET total_score = #{totalScore},
                status = #{status},
                submitted_at = NOW(),
                updated_at = NOW()
            WHERE id = #{id}
            """)
    int finish(QuickReviewPaper paper);
}
