package com.example.aiinterview.service;

import com.example.aiinterview.dto.QuickReviewPaperDetailResponse;
import com.example.aiinterview.dto.QuickReviewPaperSummaryResponse;
import com.example.aiinterview.dto.QuickReviewScoreResponse;
import com.example.aiinterview.dto.QuickReviewStartResponse;
import com.example.aiinterview.dto.QuickReviewSubmitRequest;
import com.example.aiinterview.dto.QuickReviewSubmitResponse;

import java.util.List;

public interface QuickReviewService {

    QuickReviewStartResponse start();

    QuickReviewSubmitResponse submit(Long paperId, QuickReviewSubmitRequest request);

    List<QuickReviewPaperSummaryResponse> listHistory(int page, int size);

    QuickReviewPaperDetailResponse getHistoryDetail(Long paperId);

    QuickReviewScoreResponse score(String questionContent, String referenceAnswer, String userAnswer);
}
