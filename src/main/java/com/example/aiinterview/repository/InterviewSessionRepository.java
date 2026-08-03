package com.example.aiinterview.repository;

import com.example.aiinterview.entity.InterviewSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InterviewSessionRepository {

    InterviewSession findById(Long id);

    List<InterviewSession> findAllByOrderByCreatedAtDesc();

    List<InterviewSession> findPageOrderByCreatedAtDesc(@Param("offset") int offset, @Param("size") int size);

    long countAll();

    List<InterviewSession> findPageByDirectionOrderByCreatedAtDesc(@Param("direction") String direction,
                                                                   @Param("offset") int offset,
                                                                   @Param("size") int size);

    long countByDirection(@Param("direction") String direction);

    int insert(InterviewSession session);

    int update(InterviewSession session);

    int deleteById(Long id);
}
