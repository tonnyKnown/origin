package com.example.aiinterview.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户画像生成的大模型调用。
 */
@Slf4j
@Service
public class AiProfileClient extends AbstractAiClient {

    public AiProfileClient(AiModelHolder modelHolder) {
        super(modelHolder);
    }

    public record UserProfileResult(String profileSummary,
                                    List<String> weakPoints,
                                    List<String> strengthPoints,
                                    List<String> learningSuggestions) {
    }

    public UserProfileResult generateUserProfile(String evidenceText) {
        if (!modelHolder.isAvailable()) {
            return mockProfile();
        }

        // 画像是后续出题的核心输入，因此要求模型输出固定 ASCII 标签，降低解析不稳定性。
        String prompt = """
                你是技术学习画像分析师。请只根据下面的学习证据生成用户画像，不要编造不存在的经历。
                输出必须使用 ASCII 标签，便于系统解析；不要输出 Markdown，不要输出多余内容。

                要求：
                1. PROFILE 控制在 300 字以内，说明当前能力状态、主要短板和训练优先级。
                2. WEAK 输出 3 到 8 个薄弱点，用英文分号 ; 分隔。
                3. STRENGTH 输出 1 到 5 个优势点，没有明确证据可写“暂无明确优势证据”。
                4. SUGGESTION 输出 3 到 6 条学习建议，用英文分号 ; 分隔。

                学习证据：
                %s

                输出格式：
                PROFILE: ...
                WEAK: ...
                STRENGTH: ...
                SUGGESTION: ...
                """.formatted(evidenceText);

        String output = call(prompt);
        // PROFILE 与 WEAK 是画像的核心字段，缺失即视为解析失败；
        // STRENGTH / SUGGESTION 允许为空，按空列表处理而不是编造兜底。
        return new UserProfileResult(
                requireField(output, "PROFILE:", "WEAK:", "PROFILE"),
                splitFocus(requireField(output, "WEAK:", "STRENGTH:", "WEAK")),
                splitFocus(optionalField(output, "STRENGTH:", "SUGGESTION:", "")),
                splitFocus(optionalTail(output, "SUGGESTION:", ""))
        );
    }

    /** 无 API Key 时的本地模拟画像，明确标注模拟来源。 */
    private UserProfileResult mockProfile() {
        return new UserProfileResult(
                MOCK_PREFIX + "暂无稳定的用户画像：当前缺少足够的学习证据，建议先完成几场模拟面试、"
                        + "手动题库自测或快速复盘，再更新画像。",
                List.of("核心概念边界", "生产实践案例", "性能排查链路", "系统设计表达"),
                List.of(),
                List.of("按方向梳理高频知识点", "每道错题补充落地案例", "定期复盘低分题")
        );
    }

    private String optionalTail(String text, String start, String fallback) {
        int startIndex = text.indexOf(start);
        if (startIndex >= 0) {
            return text.substring(startIndex + start.length()).trim();
        }
        return fallback;
    }
}
