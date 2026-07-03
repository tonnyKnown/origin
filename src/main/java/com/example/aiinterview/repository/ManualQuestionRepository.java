package com.example.aiinterview.repository;

import com.example.aiinterview.entity.ManualQuestion;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ManualQuestionRepository {

    @Select("""
            SELECT id, question_content, understanding_level, ai_answer, answered_at,
                   manual_remark, remark_updated_at,
                   self_test_answer, self_test_score, self_test_comment, self_test_suggestion, self_test_at,
                   created_at, updated_at
            FROM manual_question
            WHERE id = #{id}
            """)
    ManualQuestion findById(Long id);

    @Select("""
            SELECT id, question_content, understanding_level, ai_answer, answered_at,
                   manual_remark, remark_updated_at,
                   self_test_answer, self_test_score, self_test_comment, self_test_suggestion, self_test_at,
                   created_at, updated_at
            FROM manual_question
            ORDER BY created_at DESC
            """)
    List<ManualQuestion> findAllOrderByCreatedAtDesc();

    @Select("""
            SELECT COUNT(*)
            FROM manual_question
            """)
    long countAll();

    @Select("""
            SELECT id, question_content, understanding_level, ai_answer, answered_at,
                   manual_remark, remark_updated_at,
                   self_test_answer, self_test_score, self_test_comment, self_test_suggestion, self_test_at,
                   created_at, updated_at
            FROM manual_question
            ORDER BY
                CASE understanding_level
                    WHEN 'LOW' THEN 1
                    WHEN 'MEDIUM' THEN 2
                    ELSE 3
                END,
                updated_at DESC,
                created_at DESC
            LIMIT 1 OFFSET #{offset}
            """)
    ManualQuestion findReviewQuestionByOffset(@Param("offset") int offset);

    @Select("""
            SELECT id, question_content, understanding_level, ai_answer, answered_at,
                   manual_remark, remark_updated_at,
                   self_test_answer, self_test_score, self_test_comment, self_test_suggestion, self_test_at,
                   created_at, updated_at
            FROM manual_question
            WHERE (#{keyword} IS NULL OR #{keyword} = '' OR question_content LIKE CONCAT('%', #{keyword}, '%'))
              AND (#{understandingLevel} IS NULL OR #{understandingLevel} = '' OR understanding_level = #{understandingLevel})
            ORDER BY created_at DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    List<ManualQuestion> findPage(@Param("keyword") String keyword,
                                  @Param("understandingLevel") String understandingLevel,
                                  @Param("offset") int offset,
                                  @Param("size") int size);

    @Select("""
            SELECT COUNT(*)
            FROM manual_question
            WHERE (#{keyword} IS NULL OR #{keyword} = '' OR question_content LIKE CONCAT('%', #{keyword}, '%'))
              AND (#{understandingLevel} IS NULL OR #{understandingLevel} = '' OR understanding_level = #{understandingLevel})
            """)
    long countByKeyword(@Param("keyword") String keyword,
                        @Param("understandingLevel") String understandingLevel);

    @Insert("""
            INSERT INTO manual_question (
                question_content, understanding_level, ai_answer, answered_at, created_at, updated_at
            ) VALUES (
                #{questionContent}, #{understandingLevel}, #{aiAnswer}, #{answeredAt}, NOW(), NOW()
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ManualQuestion manualQuestion);

    @Update("""
            UPDATE manual_question
            SET ai_answer = #{aiAnswer},
                answered_at = NOW(),
                updated_at = NOW()
            WHERE id = #{id}
            """)
    int updateAnswer(@Param("id") Long id, @Param("aiAnswer") String aiAnswer);

    @Update("""
            UPDATE manual_question
            SET manual_remark = #{manualRemark},
                remark_updated_at = NOW(),
                updated_at = NOW()
            WHERE id = #{id}
            """)
    int updateManualRemark(@Param("id") Long id, @Param("manualRemark") String manualRemark);

    @Update("""
            UPDATE manual_question
            SET self_test_answer = #{userAnswer},
                self_test_score = #{score},
                self_test_comment = #{comment},
                self_test_suggestion = #{suggestion},
                self_test_at = NOW(),
                updated_at = NOW()
            WHERE id = #{id}
            """)
    int updateSelfTest(@Param("id") Long id,
                       @Param("userAnswer") String userAnswer,
                       @Param("score") Integer score,
                       @Param("comment") String comment,
                       @Param("suggestion") String suggestion);

    @Update("""
            UPDATE manual_question
            SET understanding_level = #{understandingLevel},
                updated_at = NOW()
            WHERE id = #{id}
            """)
    int updateUnderstandingLevel(@Param("id") Long id,
                                 @Param("understandingLevel") String understandingLevel);
}
