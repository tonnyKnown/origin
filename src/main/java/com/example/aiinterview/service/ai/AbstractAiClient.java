package com.example.aiinterview.service.ai;

import com.example.aiinterview.common.ApiCode;
import com.example.aiinterview.common.exception.BusinessException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI 子客户端公共基类：模型调用与输出解析。
 * 解析失败一律显式抛出业务异常，绝不静默返回兜底假数据。
 */
public abstract class AbstractAiClient {

    protected static final String MOCK_PREFIX = "【本地模拟】";

    protected final AiModelHolder modelHolder;

    protected AbstractAiClient(AiModelHolder modelHolder) {
        this.modelHolder = modelHolder;
    }

    /** 大模型是否可用（已配置 API Key）。 */
    protected boolean isAvailable() {
        return modelHolder.isAvailable();
    }

    /** 当前模型名称（未配置 Key 时返回配置值，便于审计）。 */
    protected String modelName() {
        return modelHolder.modelName();
    }

    protected String call(String prompt) {
        return modelHolder.model().generate(prompt);
    }

    protected BusinessException parseFail(String what) {
        return new BusinessException(ApiCode.UPSTREAM_ERROR,
                "大模型返回的" + what + "格式异常，无法解析，请重试。");
    }

    /** 解析 [start, end) 之间的内容，缺失即抛异常。 */
    protected String requireField(String text, String start, String end, String label) {
        int startIndex = text.indexOf(start);
        int endIndex = end == null ? -1 : text.indexOf(end, Math.max(startIndex, 0));
        if (startIndex < 0 || (end != null && (endIndex <= startIndex))) {
            throw parseFail(label);
        }
        String value = text.substring(startIndex + start.length(), endIndex).trim();
        if (value.isEmpty()) {
            throw parseFail(label);
        }
        return value;
    }

    /** 解析 start 之后到文本末尾的内容，缺失即抛异常。 */
    protected String requireTail(String text, String start, String label) {
        int startIndex = text.indexOf(start);
        if (startIndex < 0) {
            throw parseFail(label);
        }
        String value = text.substring(startIndex + start.length()).trim();
        if (value.isEmpty()) {
            throw parseFail(label);
        }
        return value;
    }

    /** 宽松解析：字段可缺失，返回 fallback（用于非关键、允许为空的字段）。 */
    protected String optionalField(String text, String start, String end, String fallback) {
        int startIndex = text.indexOf(start);
        int endIndex = end == null ? -1 : text.indexOf(end, Math.max(startIndex, 0));
        if (startIndex >= 0 && (end == null || (endIndex > startIndex))) {
            String value = text.substring(startIndex + start.length(), endIndex < 0 ? text.length() : endIndex).trim();
            return value.isEmpty() ? fallback : value;
        }
        return fallback;
    }

    /** 解析“分数：”标签后的 0-100 整数，缺失或越界即抛异常。 */
    protected int requireScore(String text, String label, Pattern pattern, int maxScore) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            int score = Integer.parseInt(matcher.group(1));
            if (score >= 0 && score <= maxScore) {
                return score;
            }
        }
        throw parseFail(label);
    }

    protected List<String> splitFocus(String focusText) {
        if (!StringUtils.hasText(focusText)) {
            return List.of();
        }
        String[] parts = focusText.split("[;；、,，\\n]");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String item = part.trim();
            if (!item.isBlank()) {
                result.add(item);
            }
        }
        return result;
    }
}
