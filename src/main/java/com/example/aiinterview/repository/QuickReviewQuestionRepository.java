package com.example.aiinterview.repository;

import com.example.aiinterview.entity.QuickReviewQuestionItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface QuickReviewQuestionRepository {

    @Select("""
            SELECT id, paper_id, question_index, knowledge_point, question_content,
                   blank_answer, reference_answer, explanation, user_answer,
                   score, correct, ai_comment, suggestion, answered_at, created_at, updated_at
            FROM quick_review_question
            WHERE paper_id = #{paperId}
            ORDER BY question_index ASC
            """)
    List<QuickReviewQuestionItem> findByPaperIdOrderByQuestionIndexAsc(Long paperId);

    @Select("""
            SELECT id, paper_id, question_index, knowledge_point, question_content,
                   blank_answer, reference_answer, explanation, user_answer,
                   score, correct, ai_comment, suggestion, answered_at, created_at, updated_at
            FROM quick_review_question
            WHERE paper_id = #{paperId} AND question_index = #{questionIndex}
            """)
    QuickReviewQuestionItem findByPaperIdAndQuestionIndex(@Param("paperId") Long paperId,
                                                          @Param("questionIndex") Integer questionIndex);

    @Insert("""
            INSERT INTO quick_review_question (
                paper_id, question_index, knowledge_point, question_content,
                blank_answer, reference_answer, explanation, user_answer,
                score, correct, ai_comment, suggestion, answered_at, created_at, updated_at
            ) VALUES (
                #{paperId}, #{questionIndex}, #{knowledgePoint}, #{questionContent},
                #{blankAnswer}, #{referenceAnswer}, #{explanation}, #{userAnswer},
                #{score}, #{correct}, #{aiComment}, #{suggestion}, #{answeredAt}, NOW(), NOW()
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(QuickReviewQuestionItem question);

    @Update("""
            UPDATE quick_review_question
            SET user_answer = #{userAnswer},
                score = #{score},
                correct = #{correct},
                ai_comment = #{aiComment},
                suggestion = #{suggestion},
                answered_at = NOW(),
                updated_at = NOW()
            WHERE paper_id = #{paperId}
              AND question_index = #{questionIndex}
            """)
    int updateAnswer(QuickReviewQuestionItem question);
}
