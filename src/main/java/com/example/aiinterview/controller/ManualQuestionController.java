package com.example.aiinterview.controller;

import com.example.aiinterview.dto.ManualQuestionRequest;
import com.example.aiinterview.dto.ManualQuestionPageResponse;
import com.example.aiinterview.dto.ManualQuestionResponse;
import com.example.aiinterview.dto.ManualQuestionTagRequest;
import com.example.aiinterview.dto.ManualRemarkRequest;
import com.example.aiinterview.dto.ManualSelfTestRequest;
import com.example.aiinterview.service.ManualQuestionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manual-questions")
public class ManualQuestionController {

    private final ManualQuestionService manualQuestionService;

    public ManualQuestionController(ManualQuestionService manualQuestionService) {
        this.manualQuestionService = manualQuestionService;
    }

    @PostMapping
    public ManualQuestionResponse create(@Valid @RequestBody ManualQuestionRequest request) {
        return manualQuestionService.create(request.questionContent());
    }

    @GetMapping
    public ManualQuestionPageResponse list(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(defaultValue = "") String keyword,
                                           @RequestParam(defaultValue = "") String understandingLevel) {
        return manualQuestionService.page(page, size, keyword, understandingLevel);
    }

    @PostMapping("/{id}/answer")
    public ManualQuestionResponse answer(@PathVariable Long id) {
        return manualQuestionService.answer(id);
    }

    @PutMapping("/{id}/remark")
    public ManualQuestionResponse updateManualRemark(@PathVariable Long id,
                                                     @Valid @RequestBody ManualRemarkRequest request) {
        return manualQuestionService.updateManualRemark(id, request.manualRemark());
    }

    @PostMapping("/{id}/self-test")
    public ManualQuestionResponse selfTest(@PathVariable Long id,
                                           @Valid @RequestBody ManualSelfTestRequest request) {
        return manualQuestionService.selfTest(id, request.userAnswer());
    }

    @PutMapping("/{id}/tag")
    public ManualQuestionResponse updateTag(@PathVariable Long id,
                                            @Valid @RequestBody ManualQuestionTagRequest request) {
        return manualQuestionService.updateUnderstandingLevel(id, request.understandingLevel());
    }
}
