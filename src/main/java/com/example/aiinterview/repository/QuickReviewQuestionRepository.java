package com.example.aiinterview.repository;

import com.example.aiinterview.entity.QuickReviewQuestionItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuickReviewQuestionRepository {

    List<QuickReviewQuestionItem> findByPaperIdOrderByQuestionIndexAsc(Long paperId);

    QuickReviewQuestionItem findByPaperIdAndQuestionIndex(@Param("paperId") Long paperId,
                                                          @Param("questionIndex") Integer questionIndex);

    int insert(QuickReviewQuestionItem question);

    int updateAnswer(QuickReviewQuestionItem question);
}
