package com.example.aiinterview.controller;

import com.example.aiinterview.common.Result;
import com.example.aiinterview.dto.AnswerScoreResponse;
import com.example.aiinterview.dto.InterviewHistoryDetailResponse;
import com.example.aiinterview.dto.InterviewHistoryItemResponse;
import com.example.aiinterview.dto.InterviewHistoryPageResponse;
import com.example.aiinterview.dto.QuestionResponse;
import com.example.aiinterview.dto.ReAnswerRequest;
import com.example.aiinterview.dto.ReAnswerResponse;
import com.example.aiinterview.dto.StartInterviewRequest;
import com.example.aiinterview.dto.SubmitAnswerRequest;
import com.example.aiinterview.dto.SummaryResponse;
import com.example.aiinterview.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/start")
    public Result<QuestionResponse> start(@Valid @RequestBody StartInterviewRequest request) {
        if (request.directionId() != null) {
            return Result.ok(interviewService.startByDirection(request.directionId()));
        }
        return Result.ok(interviewService.start(request.positionType()));
    }

    @PostMapping("/review/start")
    public Result<QuestionResponse> startReview() {
        return Result.ok(interviewService.startReview());
    }

    @PostMapping("/{interviewId}/answer")
    public Result<AnswerScoreResponse> submitAnswer(@PathVariable Long interviewId,
                                                     @Valid @RequestBody SubmitAnswerRequest request) {
        return Result.ok(interviewService.submitAnswer(interviewId, request.questionId(), request.userAnswer()));
    }

    @PostMapping("/{interviewId}/next")
    public Result<QuestionResponse> nextQuestion(@PathVariable Long interviewId) {
        return Result.ok(interviewService.nextQuestion(interviewId));
    }

    @PostMapping("/{interviewId}/continue")
    public Result<QuestionResponse> continueInterview(@PathVariable Long interviewId) {
        return Result.ok(interviewService.continueInterview(interviewId));
    }

    @PostMapping("/{interviewId}/cancel")
    public Result<Void> cancelInterview(@PathVariable Long interviewId) {
        interviewService.cancelInterview(interviewId);
        return Result.ok(null);
    }

    @GetMapping("/{interviewId}/summary")
    public Result<SummaryResponse> summary(@PathVariable Long interviewId) {
        return Result.ok(interviewService.summary(interviewId));
    }

    @GetMapping("/history")
    public Result<InterviewHistoryPageResponse> history(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(required = false) String direction) {
        return Result.ok(interviewService.historyPage(page, size, direction));
    }

    @GetMapping("/history/{interviewId}")
    public Result<InterviewHistoryDetailResponse> historyDetail(@PathVariable Long interviewId) {
        return Result.ok(interviewService.historyDetail(interviewId));
    }

    @PutMapping("/{interviewId}/questions/{questionId}/answer")
    public Result<ReAnswerResponse> reAnswer(@PathVariable Long interviewId,
                                              @PathVariable Long questionId,
                                              @Valid @RequestBody ReAnswerRequest request) {
        return Result.ok(interviewService.reAnswer(interviewId, questionId, request.userAnswer()));
    }

    @DeleteMapping("/history/{interviewId}")
    public Result<Void> deleteHistory(@PathVariable Long interviewId) {
        interviewService.deleteHistory(interviewId);
        return Result.ok(null);
    }
}