package com.example.aiinterview.service.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * 统一持有大模型客户端实例：各 AI 子客户端共享同一个 ChatModel，
 * 避免 @Value 配置在多个类里重复出现。
 */
@Slf4j
@Component
public class AiModelHolder {

    private final ChatLanguageModel chatModel;
    private final String modelName;

    public AiModelHolder(@Value("${ai.deepseek.api-key:}") String apiKey,
                         @Value("${ai.deepseek.base-url:https://api.deepseek.com/v1}") String baseUrl,
                         @Value("${ai.deepseek.model-name:deepseek-chat}") String modelName) {
        this(modelName, StringUtils.hasText(apiKey)
                ? OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .modelName(modelName)
                    .timeout(Duration.ofSeconds(60))
                    .build()
                : null);
        if (StringUtils.hasText(apiKey)) {
            log.info("DeepSeek chat model enabled. model={}, baseUrl={}", modelName, baseUrl);
        } else {
            log.warn("DeepSeek API key is not configured. Set DEEPSEEK_API_KEY in environment variables or project .env.");
        }
    }

    /** 测试用：直接注入模型实例与名称。 */
    AiModelHolder(String modelName, ChatLanguageModel chatModel) {
        this.modelName = modelName;
        this.chatModel = chatModel;
    }

    public boolean isAvailable() {
        return chatModel != null;
    }

    public String modelName() {
        return modelName;
    }

    /**
     * 未配置 API Key 时返回 null，调用方需要先判断 isAvailable()。
     */
    public ChatLanguageModel model() {
        return chatModel;
    }
}
