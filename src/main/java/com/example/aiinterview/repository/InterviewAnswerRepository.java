package com.example.aiinterview.repository;

import com.example.aiinterview.entity.InterviewAnswer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InterviewAnswerRepository {

    InterviewAnswer findByQuestionId(Long questionId);

    List<InterviewAnswer> findByInterviewIdOrderByIdAsc(Long interviewId);

    int insert(InterviewAnswer answer);

    int updateAnswerContent(InterviewAnswer answer);

    int deleteByInterviewId(Long interviewId);
}
