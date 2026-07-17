package com.example.aiinterview.service;

import com.example.aiinterview.dto.InterviewDirectionRequest;
import com.example.aiinterview.dto.InterviewDirectionResponse;

import java.util.List;

public interface InterviewDirectionService {

    List<InterviewDirectionResponse> enabledTree();

    List<InterviewDirectionResponse> adminTree();

    InterviewDirectionResponse create(InterviewDirectionRequest request);

    InterviewDirectionResponse update(Long id, InterviewDirectionRequest request);

    void updateEnabled(Long id, boolean enabled);

    void disable(Long id);

    String buildDirectionPath(Long id);
}