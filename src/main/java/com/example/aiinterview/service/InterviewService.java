package com.example.aiinterview.service;

import com.example.aiinterview.dto.AnswerScoreResponse;
import com.example.aiinterview.dto.InterviewHistoryDetailResponse;
import com.example.aiinterview.dto.InterviewHistoryItemResponse;
import com.example.aiinterview.dto.InterviewHistoryPageResponse;
import com.example.aiinterview.dto.QuestionResponse;
import com.example.aiinterview.dto.ReAnswerResponse;
import com.example.aiinterview.dto.SummaryResponse;

import java.util.List;

public interface InterviewService {

    QuestionResponse start(String positionType);

    QuestionResponse startByDirection(Long directionId);

    QuestionResponse startReview();

    AnswerScoreResponse submitAnswer(Long interviewId, Long questionId, String userAnswer);

    QuestionResponse nextQuestion(Long interviewId);

    QuestionResponse continueInterview(Long interviewId);

    void cancelInterview(Long interviewId);

    SummaryResponse summary(Long interviewId);

    List<InterviewHistoryItemResponse> history();

    InterviewHistoryPageResponse historyPage(int page, int size, String direction);

    InterviewHistoryPageResponse historyPage(int page, int size);

    InterviewHistoryDetailResponse historyDetail(Long interviewId);

    ReAnswerResponse reAnswer(Long interviewId, Long questionId, String userAnswer);

    void deleteHistory(Long interviewId);
}