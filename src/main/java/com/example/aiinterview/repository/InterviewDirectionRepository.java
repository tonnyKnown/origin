package com.example.aiinterview.repository;

import com.example.aiinterview.entity.InterviewDirection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InterviewDirectionRepository {

    InterviewDirection findById(Long id);

    List<InterviewDirection> findAll();

    List<InterviewDirection> findEnabled();

    long countChildren(Long parentId);

    long countByParentAndNameExcludingId(@Param("parentId") Long parentId,
                                         @Param("name") String name,
                                         @Param("excludeId") Long excludeId);

    int insert(InterviewDirection direction);

    int update(InterviewDirection direction);

    int updateEnabled(@Param("id") Long id, @Param("enabled") Boolean enabled);
}
