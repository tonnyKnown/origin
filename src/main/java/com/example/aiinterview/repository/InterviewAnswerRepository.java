package com.example.aiinterview.repository;

import com.example.aiinterview.entity.InterviewAnswer;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface InterviewAnswerRepository {

    @Select("""
            SELECT id, interview_id, question_id, user_answer, score,
                   answer_summary, ai_comment, suggestion, created_at, updated_at, revision_count
            FROM interview_answer
            WHERE question_id = #{questionId}
            """)
    InterviewAnswer findByQuestionId(Long questionId);

    @Select("""
            SELECT id, interview_id, question_id, user_answer, score,
                   answer_summary, ai_comment, suggestion, created_at, updated_at, revision_count
            FROM interview_answer
            WHERE interview_id = #{interviewId}
            ORDER BY id ASC
            """)
    List<InterviewAnswer> findByInterviewIdOrderByIdAsc(Long interviewId);

    @Insert("""
            INSERT INTO interview_answer (
                interview_id, question_id, user_answer, score,
                answer_summary, ai_comment, suggestion, created_at, updated_at, revision_count
            ) VALUES (
                #{interviewId}, #{questionId}, #{userAnswer}, #{score},
                #{answerSummary}, #{aiComment}, #{suggestion}, NOW(), NOW(), 0
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InterviewAnswer answer);

    @Update("""
            UPDATE interview_answer
            SET user_answer = #{userAnswer},
                score = #{score},
                answer_summary = #{answerSummary},
                ai_comment = #{aiComment},
                suggestion = #{suggestion},
                updated_at = NOW(),
                revision_count = revision_count + 1
            WHERE id = #{id}
            """)
    int updateAnswerContent(InterviewAnswer answer);

    @Delete("""
            DELETE FROM interview_answer
            WHERE interview_id = #{interviewId}
            """)
    int deleteByInterviewId(Long interviewId);
}
