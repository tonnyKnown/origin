package com.example.aiinterview.service.impl;

import com.example.aiinterview.common.exception.BusinessException;
import com.example.aiinterview.dto.UserProfileResponse;
import com.example.aiinterview.dto.UserProfileVersionResponse;
import com.example.aiinterview.entity.QuickReviewEvidence;
import com.example.aiinterview.entity.UserProfile;
import com.example.aiinterview.entity.UserProfileVersion;
import com.example.aiinterview.repository.QuickReviewEvidenceRepository;
import com.example.aiinterview.repository.UserProfileRepository;
import com.example.aiinterview.repository.UserProfileVersionRepository;
import com.example.aiinterview.service.UserProfileService;
import com.example.aiinterview.service.ai.AiProfileClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final QuickReviewEvidenceRepository evidenceRepository;
    private final UserProfileRepository profileRepository;
    private final UserProfileVersionRepository versionRepository;
    private final AiProfileClient aiProfileClient;

    public UserProfileServiceImpl(QuickReviewEvidenceRepository evidenceRepository,
                                  UserProfileRepository profileRepository,
                                  UserProfileVersionRepository versionRepository,
                                  AiProfileClient aiProfileClient) {
        this.evidenceRepository = evidenceRepository;
        this.profileRepository = profileRepository;
        this.versionRepository = versionRepository;
        this.aiProfileClient = aiProfileClient;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse current() {
        UserProfile profile = profileRepository.findActive();
        if (profile == null) {
            return new UserProfileResponse(
                    null,
                    "暂无用户画像，请在首页点击\"更新用户画像\"。",
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    0,
                    0,
                    "EMPTY",
                    null,
                    false
            );
        }
        return toResponse(profile, true);
    }

    @Override
    @Transactional
    public UserProfileResponse refresh() {
        log.info("Refreshing user profile (model available={})", aiProfileClient.isAvailable());

        List<QuickReviewEvidence> evidenceList = loadEvidence();
        String evidenceText = buildEvidenceText(evidenceList);
        AiProfileClient.UserProfileResult result = aiProfileClient.generateUserProfile(evidenceText);

        UserProfile profile = profileRepository.findActive();
        int nextVersion = profile == null || profile.getVersion() == null ? 1 : profile.getVersion() + 1;
        if (profile == null) {
            profile = new UserProfile();
            profile.setStatus("ACTIVE");
            profile.setVersion(nextVersion);
            fillProfile(profile, result, evidenceList.size());
            profileRepository.insert(profile);
        } else {
            profile.setVersion(nextVersion);
            fillProfile(profile, result, evidenceList.size());
            profileRepository.update(profile);
        }

        UserProfileVersion version = new UserProfileVersion();
        version.setProfileId(profile.getId());
        version.setVersion(nextVersion);
        version.setProfileSummary(profile.getProfileSummary());
        version.setWeakPoints(profile.getWeakPoints());
        version.setStrengthPoints(profile.getStrengthPoints());
        version.setLearningSuggestions(profile.getLearningSuggestions());
        version.setEvidenceSnapshot(evidenceText);
        version.setEvidenceCount(evidenceList.size());
        version.setModelName(aiProfileClient.isAvailable() ? aiProfileClient.modelName() : "mock");
        versionRepository.insert(version);

        log.info("User profile refreshed: version={}", nextVersion);
        return toResponse(profile, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileVersionResponse> versions(int size) {
        int normalizedSize = Math.min(Math.max(size, 1), 20);
        return versionRepository.findLatest(normalizedSize).stream()
                .map(version -> new UserProfileVersionResponse(
                        version.getId(),
                        version.getVersion(),
                        version.getProfileSummary(),
                        splitList(version.getWeakPoints()),
                        splitList(version.getStrengthPoints()),
                        splitList(version.getLearningSuggestions()),
                        version.getEvidenceCount(),
                        version.getModelName(),
                        version.getCreatedAt()
                ))
                .toList();
    }

    private List<QuickReviewEvidence> loadEvidence() {
        List<QuickReviewEvidence> evidenceList = new ArrayList<>();
        evidenceList.addAll(evidenceRepository.findManualWeakEvidence());
        evidenceList.addAll(evidenceRepository.findInterviewWeakEvidence());
        evidenceList.addAll(evidenceRepository.findQuickReviewWeakEvidence());
        return evidenceList;
    }

    private void fillProfile(UserProfile profile, AiProfileClient.UserProfileResult result, int evidenceCount) {
        profile.setProfileSummary(result.profileSummary());
        profile.setWeakPoints(joinList(result.weakPoints()));
        profile.setStrengthPoints(joinList(result.strengthPoints()));
        profile.setLearningSuggestions(joinList(result.learningSuggestions()));
        profile.setEvidenceCount(evidenceCount);
    }

    private UserProfileResponse toResponse(UserProfile profile, boolean exists) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getProfileSummary(),
                splitList(profile.getWeakPoints()),
                splitList(profile.getStrengthPoints()),
                splitList(profile.getLearningSuggestions()),
                profile.getEvidenceCount(),
                profile.getVersion(),
                profile.getStatus(),
                profile.getUpdatedAt(),
                exists
        );
    }

    private String buildEvidenceText(List<QuickReviewEvidence> evidenceList) {
        if (evidenceList.isEmpty()) {
            return "暂无明确历史证据。请基于通用技术方向学习画像给出保守建议，并提示用户补充面试和自测记录。";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < evidenceList.size(); i++) {
            QuickReviewEvidence evidence = evidenceList.get(i);
            builder.append(i + 1)
                    .append(". 来源=").append(safe(evidence.getSourceType()))
                    .append("；主题=").append(safe(evidence.getTopic()))
                    .append("；得分=").append(evidence.getScore() == null ? "-" : evidence.getScore())
                    .append("；掌握度=").append(safe(evidence.getUnderstandingLevel()))
                    .append("；详情=").append(safe(evidence.getDetail()))
                    .append("\n");
        }
        return builder.toString();
    }

    private String joinList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .toList()
                .stream()
                .reduce((left, right) -> left + "；" + right)
                .orElse("");
    }

    private List<String> splitList(String value) {
        if (!StringUtils.hasText(value)) {
            return Collections.emptyList();
        }
        return List.of(value.split("[;；]")).stream()
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
    }

    private String safe(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim();
    }
}
