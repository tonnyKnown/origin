package com.example.aiinterview.controller;

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
    public QuestionResponse start(@Valid @RequestBody StartInterviewRequest request) {
        if (request.directionId() != null) {
            return interviewService.startByDirection(request.directionId());
        }
        return interviewService.start(request.positionType());
    }

    @PostMapping("/review/start")
    public QuestionResponse startReview() {
        return interviewService.startReview();
    }

    @PostMapping("/{interviewId}/answer")
    public AnswerScoreResponse submitAnswer(@PathVariable Long interviewId,
                                            @Valid @RequestBody SubmitAnswerRequest request) {
        return interviewService.submitAnswer(interviewId, request.questionId(), request.userAnswer());
    }

    @PostMapping("/{interviewId}/next")
    public QuestionResponse nextQuestion(@PathVariable Long interviewId) {
        return interviewService.nextQuestion(interviewId);
    }

    @PostMapping("/{interviewId}/continue")
    public QuestionResponse continueInterview(@PathVariable Long interviewId) {
        return interviewService.continueInterview(interviewId);
    }

    @PostMapping("/{interviewId}/cancel")
    public void cancelInterview(@PathVariable Long interviewId) {
        interviewService.cancelInterview(interviewId);
    }

    @GetMapping("/{interviewId}/summary")
    public SummaryResponse summary(@PathVariable Long interviewId) {
        return interviewService.summary(interviewId);
    }

    @GetMapping("/history")
    public InterviewHistoryPageResponse history(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return interviewService.historyPage(page, size);
    }

    @GetMapping("/history/{interviewId}")
    public InterviewHistoryDetailResponse historyDetail(@PathVariable Long interviewId) {
        return interviewService.historyDetail(interviewId);
    }

    @PutMapping("/{interviewId}/questions/{questionId}/answer")
    public ReAnswerResponse reAnswer(@PathVariable Long interviewId,
                                     @PathVariable Long questionId,
                                     @Valid @RequestBody ReAnswerRequest request) {
        return interviewService.reAnswer(interviewId, questionId, request.userAnswer());
    }

    @DeleteMapping("/history/{interviewId}")
    public void deleteHistory(@PathVariable Long interviewId) {
        interviewService.deleteHistory(interviewId);
    }
}
