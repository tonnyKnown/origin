package com.example.aiinterview.controller;

import com.example.aiinterview.common.Result;
import com.example.aiinterview.dto.QuickReviewAnswerRequest;
import com.example.aiinterview.dto.QuickReviewPaperDetailResponse;
import com.example.aiinterview.dto.QuickReviewPaperSummaryResponse;
import com.example.aiinterview.dto.QuickReviewScoreResponse;
import com.example.aiinterview.dto.QuickReviewStartResponse;
import com.example.aiinterview.dto.QuickReviewSubmitRequest;
import com.example.aiinterview.dto.QuickReviewSubmitResponse;
import com.example.aiinterview.service.QuickReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quick-review")
public class QuickReviewController {

    private final QuickReviewService quickReviewService;

    public QuickReviewController(QuickReviewService quickReviewService) {
        this.quickReviewService = quickReviewService;
    }

    @PostMapping("/start")
    public Result<QuickReviewStartResponse> start() {
        return Result.ok(quickReviewService.start());
    }

    @PostMapping("/score")
    public Result<QuickReviewScoreResponse> score(@Valid @RequestBody QuickReviewAnswerRequest request) {
        return Result.ok(quickReviewService.score(
                request.questionContent(),
                request.referenceAnswer(),
                request.userAnswer()
        ));
    }

    @PostMapping("/{paperId}/submit")
    public Result<QuickReviewSubmitResponse> submit(@PathVariable Long paperId,
                                                    @Valid @RequestBody QuickReviewSubmitRequest request) {
        return Result.ok(quickReviewService.submit(paperId, request));
    }

    @GetMapping("/history")
    public Result<List<QuickReviewPaperSummaryResponse>> history(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        return Result.ok(quickReviewService.listHistory(page, size));
    }

    @GetMapping("/history/{paperId}")
    public Result<QuickReviewPaperDetailResponse> historyDetail(@PathVariable Long paperId) {
        return Result.ok(quickReviewService.getHistoryDetail(paperId));
    }
}