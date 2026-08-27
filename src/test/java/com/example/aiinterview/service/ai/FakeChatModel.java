package com.example.aiinterview.service.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;

import java.util.List;
import java.util.function.Supplier;

/**
 * 测试用假模型：忽略输入 prompt，按预设返回固定文本，用于验证 AI 客户端的解析逻辑。
 */
public class FakeChatModel implements ChatLanguageModel {

    private Supplier<String> answerSupplier = () -> "OK";

    public void setAnswer(String answer) {
        this.answerSupplier = () -> answer;
    }

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages) {
        return Response.from(AiMessage.from(answerSupplier.get()));
    }
}
