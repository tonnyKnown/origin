package com.example.aiinterview.repository;

import com.example.aiinterview.entity.InterviewQuestion;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InterviewQuestionRepository {

    @Select("""
            SELECT id, interview_id, question_index, question_type,
                   question_content, reference_answer, scoring_rule, created_at
            FROM interview_question
            WHERE id = #{id}
            """)
    InterviewQuestion findById(Long id);

    @Select("""
            SELECT id, interview_id, question_index, question_type,
                   question_content, reference_answer, scoring_rule, created_at
            FROM interview_question
            WHERE interview_id = #{interviewId} AND question_index = #{questionIndex}
            """)
    InterviewQuestion findByInterviewIdAndQuestionIndex(Long interviewId, Integer questionIndex);

    @Select("""
            SELECT id, interview_id, question_index, question_type,
                   question_content, reference_answer, scoring_rule, created_at
            FROM interview_question
            WHERE interview_id = #{interviewId}
            ORDER BY question_index ASC
            """)
    List<InterviewQuestion> findByInterviewIdOrderByQuestionIndexAsc(Long interviewId);

    @Insert("""
            INSERT INTO interview_question (
                interview_id, question_index, question_type,
                question_content, reference_answer, scoring_rule, created_at
            ) VALUES (
                #{interviewId}, #{questionIndex}, #{questionType},
                #{questionContent}, #{referenceAnswer}, #{scoringRule}, NOW()
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InterviewQuestion question);

    @Delete("""
            DELETE FROM interview_question
            WHERE interview_id = #{interviewId}
            """)
    int deleteByInterviewId(Long interviewId);
}
