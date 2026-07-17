package com.example.aiinterview.service.impl;

import com.example.aiinterview.dto.InterviewDirectionRequest;
import com.example.aiinterview.dto.InterviewDirectionResponse;
import com.example.aiinterview.entity.InterviewDirection;
import com.example.aiinterview.repository.InterviewDirectionRepository;
import com.example.aiinterview.service.InterviewDirectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InterviewDirectionServiceImpl implements InterviewDirectionService {

    private static final int MAX_LEVEL = 3;

    private final InterviewDirectionRepository directionRepository;

    public InterviewDirectionServiceImpl(InterviewDirectionRepository directionRepository) {
        this.directionRepository = directionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewDirectionResponse> enabledTree() {
        return buildTree(directionRepository.findEnabled());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewDirectionResponse> adminTree() {
        return buildTree(directionRepository.findAll());
    }

    @Override
    @Transactional
    public InterviewDirectionResponse create(InterviewDirectionRequest request) {
        InterviewDirection direction = new InterviewDirection();
        String name = normalizeName(request.name());
        direction.setParentId(request.parentId());
        direction.setName(name);
        direction.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        direction.setEnabled(request.enabled() == null || request.enabled());
        direction.setDescription(normalizeText(request.description()));

        if (request.parentId() == null) {
            direction.setLevel(1);
        } else {
            InterviewDirection parent = getDirection(request.parentId());
            if (parent.getLevel() >= MAX_LEVEL) {
                throw new IllegalArgumentException("面试方向最多只支持3层");
            }
            direction.setLevel(parent.getLevel() + 1);
        }
        ensureUniqueSiblingName(direction.getParentId(), name, null);

        directionRepository.insert(direction);
        log.info("Created interview direction: id={}, name={}", direction.getId(), direction.getName());
        return toResponse(direction, List.of());
    }

    @Override
    @Transactional
    public InterviewDirectionResponse update(Long id, InterviewDirectionRequest request) {
        InterviewDirection direction = getDirection(id);
        String name = normalizeName(request.name());
        ensureUniqueSiblingName(direction.getParentId(), name, id);
        direction.setName(name);
        direction.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        direction.setEnabled(request.enabled() == null || request.enabled());
        direction.setDescription(normalizeText(request.description()));
        directionRepository.update(direction);
        log.info("Updated interview direction: id={}, name={}", id, name);
        return toResponse(direction, List.of());
    }

    @Override
    @Transactional
    public void updateEnabled(Long id, boolean enabled) {
        getDirection(id);
        directionRepository.updateEnabled(id, enabled);
        log.info("Updated interview direction enabled: id={}, enabled={}", id, enabled);
    }

    @Override
    @Transactional
    public void disable(Long id) {
        getDirection(id);
        directionRepository.updateEnabled(id, false);
        log.info("Disabled interview direction: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public String buildDirectionPath(Long id) {
        InterviewDirection current = getDirection(id);
        List<String> names = new ArrayList<>();
        while (current != null) {
            names.add(0, current.getName());
            current = current.getParentId() == null ? null : directionRepository.findById(current.getParentId());
        }
        return String.join(" / ", names);
    }

    private void ensureUniqueSiblingName(Long parentId, String name, Long excludeId) {
        if (directionRepository.countByParentAndNameExcludingId(parentId, name, excludeId) > 0) {
            throw new IllegalArgumentException("同一父级下已存在同名方向");
        }
    }

    private InterviewDirection getDirection(Long id) {
        InterviewDirection direction = directionRepository.findById(id);
        if (direction == null) {
            throw new IllegalArgumentException("面试方向不存在");
        }
        return direction;
    }

    private List<InterviewDirectionResponse> buildTree(List<InterviewDirection> directions) {
        Map<Long, List<InterviewDirection>> childrenMap = new HashMap<>();
        for (InterviewDirection direction : directions) {
            childrenMap.computeIfAbsent(direction.getParentId(), key -> new ArrayList<>()).add(direction);
        }
        childrenMap.values().forEach(children -> children.sort(Comparator
                .comparing(InterviewDirection::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(InterviewDirection::getId)));
        return buildChildren(null, childrenMap);
    }

    private List<InterviewDirectionResponse> buildChildren(Long parentId, Map<Long, List<InterviewDirection>> childrenMap) {
        return childrenMap.getOrDefault(parentId, List.of()).stream()
                .map(direction -> toResponse(direction, buildChildren(direction.getId(), childrenMap)))
                .toList();
    }

    private InterviewDirectionResponse toResponse(InterviewDirection direction, List<InterviewDirectionResponse> children) {
        return new InterviewDirectionResponse(
                direction.getId(),
                direction.getParentId(),
                direction.getName(),
                direction.getLevel(),
                direction.getSortOrder(),
                direction.getEnabled(),
                direction.getDescription(),
                children
        );
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.trim();
    }
}