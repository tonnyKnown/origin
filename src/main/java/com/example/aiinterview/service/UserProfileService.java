package com.example.aiinterview.service;

import com.example.aiinterview.dto.UserProfileResponse;
import com.example.aiinterview.dto.UserProfileVersionResponse;
import com.example.aiinterview.entity.QuickReviewEvidence;
import com.example.aiinterview.entity.UserProfile;
import com.example.aiinterview.entity.UserProfileVersion;
import com.example.aiinterview.repository.QuickReviewEvidenceRepository;
import com.example.aiinterview.repository.UserProfileRepository;
import com.example.aiinterview.repository.UserProfileVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserProfileService {

    private final QuickReviewEvidenceRepository evidenceRepository;

    private final UserProfileRepository profileRepository;

    private final UserProfileVersionRepository versionRepository;

    private final AiInterviewClient aiInterviewClient;

    public UserProfileService(QuickReviewEvidenceRepository evidenceRepository,
                              UserProfileRepository profileRepository,
                              UserProfileVersionRepository versionRepository,
                              AiInterviewClient aiInterviewClient) {
        this.evidenceRepository = evidenceRepository;
        this.profileRepository = profileRepository;
        this.versionRepository = versionRepository;
        this.aiInterviewClient = aiInterviewClient;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse current() {
        UserProfile profile = profileRepository.findActive();
        if (profile == null) {
            return new UserProfileResponse(
                    null,
                    "暂无用户画像，请在首页点击“更新用户画像”。",
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

    @Transactional
    public UserProfileResponse refresh() {
        if (!aiInterviewClient.isAvailable()) {
            throw new IllegalStateException("请先配置大模型 API Key，再更新用户画像。");
        }

        // 画像只在用户主动触发时刷新，避免进入复盘页时隐式消耗大模型额度。
        List<QuickReviewEvidence> evidenceList = loadEvidence();
        String evidenceText = buildEvidenceText(evidenceList);
        AiInterviewClient.UserProfileResult result = aiInterviewClient.generateUserProfile(evidenceText);

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

        // 主表保存当前生效画像，版本表保存生成时的证据快照，便于回看画像变化原因。
        UserProfileVersion version = new UserProfileVersion();
        version.setProfileId(profile.getId());
        version.setVersion(nextVersion);
        version.setProfileSummary(profile.getProfileSummary());
        version.setWeakPoints(profile.getWeakPoints());
        version.setStrengthPoints(profile.getStrengthPoints());
        version.setLearningSuggestions(profile.getLearningSuggestions());
        version.setEvidenceSnapshot(evidenceText);
        version.setEvidenceCount(evidenceList.size());
        version.setModelName(aiInterviewClient.modelName());
        versionRepository.insert(version);

        return toResponse(profile, true);
    }

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
        // 画像证据来自三条学习闭环：手动题库、真实面试、填空题复盘结果。
        evidenceList.addAll(evidenceRepository.findManualWeakEvidence());
        evidenceList.addAll(evidenceRepository.findInterviewWeakEvidence());
        evidenceList.addAll(evidenceRepository.findQuickReviewWeakEvidence());
        return evidenceList;
    }

    private void fillProfile(UserProfile profile, AiInterviewClient.UserProfileResult result, int evidenceCount) {
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
            // 没有历史证据时仍允许模型给出保守画像，但提示用户补充真实训练数据。
            return "暂无明确历史证据。请基于通用 Java 后端学习画像给出保守建议，并提示用户补充面试和自测记录。";
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
