package com.example.aiinterview.repository;

import com.example.aiinterview.entity.InterviewQuestion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InterviewQuestionRepository {

    InterviewQuestion findById(Long id);

    InterviewQuestion findByInterviewIdAndQuestionIndex(Long interviewId, Integer questionIndex);

    List<InterviewQuestion> findByInterviewIdOrderByQuestionIndexAsc(Long interviewId);

    int insert(InterviewQuestion question);

    int deleteByInterviewId(Long interviewId);
}
