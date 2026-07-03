package com.example.aiinterview.controller;

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
public class InterviewDirectionController {

    private final InterviewDirectionService directionService;

    public InterviewDirectionController(InterviewDirectionService directionService) {
        this.directionService = directionService;
    }

    @GetMapping("/api/interview-directions/tree")
    public List<InterviewDirectionResponse> enabledTree() {
        return directionService.enabledTree();
    }

    @GetMapping("/api/admin/interview-directions/tree")
    public List<InterviewDirectionResponse> adminTree() {
        return directionService.adminTree();
    }

    @PostMapping("/api/admin/interview-directions")
    public InterviewDirectionResponse create(@Valid @RequestBody InterviewDirectionRequest request) {
        return directionService.create(request);
    }

    @PutMapping("/api/admin/interview-directions/{id}")
    public InterviewDirectionResponse update(@PathVariable Long id,
                                             @Valid @RequestBody InterviewDirectionRequest request) {
        return directionService.update(id, request);
    }

    @PutMapping("/api/admin/interview-directions/{id}/enabled")
    public void updateEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        directionService.updateEnabled(id, enabled);
    }

    @DeleteMapping("/api/admin/interview-directions/{id}")
    public void disable(@PathVariable Long id) {
        directionService.disable(id);
    }
}
