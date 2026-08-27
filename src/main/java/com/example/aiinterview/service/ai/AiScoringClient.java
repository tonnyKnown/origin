package com.example.aiinterview.service.ai;

import com.example.aiinterview.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * 评分相关的大模型调用：模拟面试评分、手动题库自测评分、快练填空题批改。
 */
@Slf4j
@Service
public class AiScoringClient extends AbstractAiClient {

    private static final Pattern CHINESE_SCORE = Pattern.compile("分数：\\s*(\\d{1,2})");
    private static final Pattern ASCII_SCORE = Pattern.compile("SCORE:\\s*(\\d{1,2})");

    public AiScoringClient(AiModelHolder modelHolder) {
        super(modelHolder);
    }

    public record ScoreResult(Integer score, String answerSummary, String aiComment, String suggestion) {
    }

    public record QuickReviewScore(Integer score, Boolean correct, String comment, String suggestion) {
    }

    public ScoreResult scoreAnswer(String question, String referenceAnswer, String scoringRule, String userAnswer) {
        if (!modelHolder.isAvailable()) {
            return mockScore(userAnswer);
        }

        String prompt = """
                你是一名严格但客观的技术面试官。请根据题目、参考答案、评分标准，对用户回答打分。
                每题满分20分，只能给0到20之间的整数。

                题目：
                %s

                参考答案：
                %s

                评分标准：
                %s

                用户回答：
                %s

                请严格按以下格式输出，不要输出多余内容：
                分数：整数
                本题总结：...
                点评：...
                建议：...
                """.formatted(question, referenceAnswer, scoringRule, userAnswer);

        String output = call(prompt);
        int score = requireScore(output, "分数", CHINESE_SCORE, 20);
        return new ScoreResult(
                score,
                requireField(output, "本题总结：", "点评：", "本题总结"),
                requireField(output, "点评：", "建议：", "点评"),
                requireTail(output, "建议：", "建议")
        );
    }

    public ScoreResult scoreManualSelfTest(String questionContent, String userAnswer) {
        if (!modelHolder.isAvailable()) {
            return mockScore(userAnswer);
        }

        String prompt = """
                你是一名严格但客观的技术面试官。请只根据用户自己提出的问题和用户自测答案，给出评分。
                每题满分20分，只能给0到20之间的整数。
                评分依据必须来自“问题本身”要求的考点，不要额外扩展问题之外的技术点，不要因为用户没有回答问题之外的内容而扣分。
                点评和建议也只能围绕这个问题展开，不要分散到无关知识点。

                问题：
                %s

                用户自测答案：
                %s

                请严格按以下格式输出，不要输出多余内容：
                分数：整数
                本题总结：...
                点评：...
                建议：...
                """.formatted(questionContent, userAnswer);

        String output = call(prompt);
        int score = requireScore(output, "分数", CHINESE_SCORE, 20);
        return new ScoreResult(
                score,
                requireField(output, "本题总结：", "点评：", "本题总结"),
                requireField(output, "点评：", "建议：", "点评"),
                requireTail(output, "建议：", "建议")
        );
    }

    public QuickReviewScore scoreQuickReviewAnswer(String questionContent, String referenceAnswer, String userAnswer) {
        if (!modelHolder.isAvailable()) {
            boolean correct = referenceAnswer != null
                    && userAnswer != null
                    && referenceAnswer.trim().equalsIgnoreCase(userAnswer.trim());
            int score = correct ? 20 : Math.max(6, Math.min(16, userAnswer == null ? 0 : userAnswer.trim().length() * 2));
            return new QuickReviewScore(
                    score,
                    correct,
                    correct ? MOCK_PREFIX + "回答命中标准答案。" : MOCK_PREFIX + "回答与标准答案还有差距，需要回到核心概念重新理解。",
                    MOCK_PREFIX + "建议先记住标准答案，再用自己的话解释它为什么成立。"
            );
        }

        String prompt = """
                你是技术复盘教练。请批改一道填空题，判断用户答案是否命中核心含义。
                满分 20 分。答案允许同义表达，但必须覆盖关键概念。
                严格使用下面 ASCII 标签输出，不要输出多余内容。

                QUESTION:
                %s

                REFERENCE:
                %s

                USER_ANSWER:
                %s

                输出格式：
                SCORE: 整数
                CORRECT: true 或 false
                COMMENT: ...
                SUGGESTION: ...
                """.formatted(questionContent, referenceAnswer, userAnswer);

        String output = call(prompt);
        int score = requireScore(output, "SCORE", ASCII_SCORE, 20);
        String correctText = requireField(output, "CORRECT:", "COMMENT:", "CORRECT");
        boolean correct;
        if (correctText.toLowerCase().contains("true")) {
            correct = true;
        } else if (correctText.toLowerCase().contains("false")) {
            correct = false;
        } else {
            throw parseFail("CORRECT");
        }
        return new QuickReviewScore(
                score,
                correct,
                requireField(output, "COMMENT:", "SUGGESTION:", "COMMENT"),
                requireTail(output, "SUGGESTION:", "SUGGESTION")
        );
    }

    private ScoreResult mockScore(String userAnswer) {
        int length = userAnswer == null ? 0 : userAnswer.length();
        int score = Math.min(20, Math.max(8, length / 12));
        return new ScoreResult(
                score,
                MOCK_PREFIX + "本题模拟总结：回答覆盖了部分核心概念，但细节和案例还可以继续补充。",
                MOCK_PREFIX + "本地模拟评分：回答已覆盖部分要点，真实评分需要配置 DEEPSEEK_API_KEY。",
                MOCK_PREFIX + "建议补充更具体的业务场景、关键参数、异常处理和性能数据。"
        );
    }
}
