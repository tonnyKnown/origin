package com.example.aiinterview.repository;

import com.example.aiinterview.entity.ManualQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ManualQuestionRepository {

    ManualQuestion findById(Long id);

    List<ManualQuestion> findAllOrderByCreatedAtDesc();

    long countAll();

    ManualQuestion findReviewQuestionByOffset(@Param("offset") int offset);

    List<ManualQuestion> findPage(@Param("keyword") String keyword,
                                  @Param("understandingLevel") String understandingLevel,
                                  @Param("offset") int offset,
                                  @Param("size") int size);

    long countByKeyword(@Param("keyword") String keyword,
                        @Param("understandingLevel") String understandingLevel);

    int insert(ManualQuestion manualQuestion);

    int updateAnswer(@Param("id") Long id, @Param("aiAnswer") String aiAnswer);

    int updateManualRemark(@Param("id") Long id, @Param("manualRemark") String manualRemark);

    int updateSelfTest(@Param("id") Long id,
                       @Param("userAnswer") String userAnswer,
                       @Param("score") Integer score,
                       @Param("comment") String comment,
                       @Param("suggestion") String suggestion);

    int updateUnderstandingLevel(@Param("id") Long id,
                                 @Param("understandingLevel") String understandingLevel);

    int updateQuestionContent(@Param("id") Long id,
                              @Param("questionContent") String questionContent);
}
