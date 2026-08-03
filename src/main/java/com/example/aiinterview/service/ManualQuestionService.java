package com.example.aiinterview.service;

import com.example.aiinterview.dto.ManualQuestionPageResponse;
import com.example.aiinterview.dto.ManualQuestionResponse;

import java.util.List;

public interface ManualQuestionService {

    ManualQuestionResponse create(String questionContent);

    List<ManualQuestionResponse> list();

    ManualQuestionPageResponse page(int page, int size, String keyword, String understandingLevel);

    ManualQuestionResponse answer(Long id);

    ManualQuestionResponse updateManualRemark(Long id, String manualRemark);

    ManualQuestionResponse selfTest(Long id, String userAnswer);

    ManualQuestionResponse updateUnderstandingLevel(Long id, String understandingLevel);

    ManualQuestionResponse updateQuestionContent(Long id, String questionContent);
}
