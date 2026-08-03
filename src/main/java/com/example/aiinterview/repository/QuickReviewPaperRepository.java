package com.example.aiinterview.repository;

import com.example.aiinterview.entity.QuickReviewPaper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuickReviewPaperRepository {

    QuickReviewPaper findById(Long id);

    List<QuickReviewPaper> findPageOrderByCreatedAtDesc(@Param("offset") int offset, @Param("size") int size);

    int insert(QuickReviewPaper paper);

    int finish(QuickReviewPaper paper);
}
