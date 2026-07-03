package com.example.aiinterview.controller;

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
    public QuickReviewStartResponse start() {
        return quickReviewService.start();
    }

    // 保留单题评分接口用于兼容旧页面或调试，当前页面主流程使用整卷提交。
    @PostMapping("/score")
    public QuickReviewScoreResponse score(@Valid @RequestBody QuickReviewAnswerRequest request) {
        return quickReviewService.score(
                request.questionContent(),
                request.referenceAnswer(),
                request.userAnswer()
        );
    }

    // 填空题训练按试卷整体提交，后端统一统计总分和每题反馈。
    @PostMapping("/{paperId}/submit")
    public QuickReviewSubmitResponse submit(@PathVariable Long paperId,
                                            @Valid @RequestBody QuickReviewSubmitRequest request) {
        return quickReviewService.submit(paperId, request);
    }

    @GetMapping("/history")
    public List<QuickReviewPaperSummaryResponse> history(@RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "20") int size) {
        return quickReviewService.listHistory(page, size);
    }

    @GetMapping("/history/{paperId}")
    public QuickReviewPaperDetailResponse historyDetail(@PathVariable Long paperId) {
        return quickReviewService.getHistoryDetail(paperId);
    }
}
