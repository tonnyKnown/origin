package com.example.aiinterview.controller;

import com.example.aiinterview.common.Result;
import com.example.aiinterview.dto.InterviewDirectionRequest;
import com.example.aiinterview.dto.InterviewDirectionResponse;
import com.example.aiinterview.service.InterviewDirectionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/interview-directions")
public class InterviewDirectionController {

    private final InterviewDirectionService directionService;

    public InterviewDirectionController(InterviewDirectionService directionService) {
        this.directionService = directionService;
    }

    @GetMapping("/tree")
    public Result<List<InterviewDirectionResponse>> enabledTree() {
        return Result.ok(directionService.enabledTree());
    }

    @GetMapping("/admin/tree")
    public Result<List<InterviewDirectionResponse>> adminTree() {
        return Result.ok(directionService.adminTree());
    }

    @PostMapping("/admin")
    public Result<InterviewDirectionResponse> create(@Valid @RequestBody InterviewDirectionRequest request) {
        return Result.ok(directionService.create(request));
    }

    @PutMapping("/admin/{id}")
    public Result<InterviewDirectionResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody InterviewDirectionRequest request) {
        return Result.ok(directionService.update(id, request));
    }

    @PutMapping("/admin/{id}/enabled")
    public Result<Void> updateEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        directionService.updateEnabled(id, enabled);
        return Result.ok(null);
    }

    @DeleteMapping("/admin/{id}")
    public Result<Void> disable(@PathVariable Long id) {
        directionService.disable(id);
        return Result.ok(null);
    }
}