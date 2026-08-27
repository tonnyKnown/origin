package com.example.aiinterview.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 面试出题相关的大模型调用：题目生成、面试总结、手动题库 AI 回答。
 */
@Slf4j
@Service
public class AiQuestionClient extends AbstractAiClient {

    public AiQuestionClient(AiModelHolder modelHolder) {
        super(modelHolder);
    }

    public record GeneratedQuestion(String questionContent, String referenceAnswer, String scoringRule) {
    }

    /**
     * 按方向生成一道面试题。方向画像由 DirectionProfile 按赛道解析，
     * 不再依赖硬编码的 Java 简历。
     */
    public GeneratedQuestion generateQuestion(String positionType, int questionIndex, String questionType,
                                              String askedQuestions, int totalQuestions) {
        if (!modelHolder.isAvailable()) {
            return mockQuestion(positionType, questionIndex, questionType);
        }

        DirectionProfile profile = DirectionProfile.resolve(positionType);
        String prompt = """
                你是一名资深技术面试官。请为候选人生成一道“%s”方向的问答题。
                当前是第%d题，总共%d题。
                当前题型：%s。
                题型说明：%s
                出题要求：
                1. 题目必须围绕该方向的技术栈和岗位要求，不要出方向之外的题目。
                2. 该方向的出题关注点：%s
                3. 已问过的问题如下，不能重复：
                %s

                请严格按以下格式输出，不要输出多余内容：
                题目：...
                参考答案：...
                评分标准：...
                """.formatted(positionType, questionIndex, totalQuestions, questionType,
                profile.categoryFocus(questionType), profile.skillScope(),
                StringUtils.hasText(askedQuestions) ? askedQuestions : "（暂无）");

        String output = call(prompt);
        return new GeneratedQuestion(
                requireField(output, "题目：", "参考答案：", "题目"),
                requireField(output, "参考答案：", "评分标准：", "参考答案"),
                requireTail(output, "评分标准：", "评分标准")
        );
    }

    public String summarize(String positionType, String detail) {
        if (!modelHolder.isAvailable()) {
            return MOCK_PREFIX + "整体完成了全部题目的回答。建议继续加强核心原理、生产实践、性能优化和系统设计表达。";
        }

        String prompt = """
                你是一名资深面试官。请基于以下%s面试记录，给出简洁的整体评价和学习建议。
                要求包含：能力优势、主要短板、后续提升建议。控制在300字以内。

                面试记录：
                %s
                """.formatted(positionType, detail);
        return call(prompt);
    }

    public String answerManualQuestion(String questionContent) {
        if (!modelHolder.isAvailable()) {
            return MOCK_PREFIX + "这个问题需要先明确核心概念，再说明关键流程、常见风险和实际落地方案。建议结合具体业务场景补充一到两个例子。";
        }

        String prompt = """
                你是一名资深技术专家，请简洁回答用户提出的问题。
                要求：
                1. 控制在300字以内。
                2. 先直接给结论，再补充关键原因或步骤。
                3. 不要输出无关寒暄。

                用户问题：
                %s
                """.formatted(questionContent);
        return call(prompt);
    }

    private GeneratedQuestion mockQuestion(String positionType, int index, String questionType) {
        if (StringUtils.hasText(questionType)) {
            String question = switch (questionType) {
                case "BASIC" -> "请说明动态代理的两种常见实现方式，并结合框架（如 Spring AOP）解释代理对象如何增强目标方法。";
                case "CONCURRENCY" -> "线上服务出现频繁 Full GC 和接口 RT 升高，你会如何结合内存、GC 日志、线程栈和系统命令定位问题？";
                case "MIDDLEWARE" -> "一个查询接口涉及数据库、缓存和搜索，你会如何设计查询链路，并处理缓存一致性、慢查询和数据同步问题？";
                case "PROJECT" -> "请结合项目经验，说明一次接口性能优化或慢查询治理的完整排查过程，包括使用的关键工具和结论。";
                default -> "如果要在一个业务系统中落地 AI Agent 能力，请说明 Embedding、Prompt 工程、Function Calling 分别承担什么角色，以及如何保证可用性和安全边界。";
            };
            return new GeneratedQuestion(
                    MOCK_PREFIX + "[" + positionType + "][" + questionType + "] " + question,
                    "参考答案应覆盖相关技术点、核心原理、关键流程、常见风险和落地经验。",
                    "满分20分：核心概念准确8分，方案和流程完整5分，项目实践和排查经验5分，表达清晰与风险意识2分。"
            );
        }
        String[] questions = {
                "常用集合类的底层结构是什么？什么场景下需要换用其他实现？",
                "请说明 volatile 的作用和局限性，它能否保证复合操作的原子性？",
                "缓存击穿、穿透、雪崩分别是什么？生产环境分别如何解决？",
                "如果线上接口偶发 RT 飙高，你会如何定位问题？",
                "请设计一个高并发场景的核心链路，说明限流、削峰和一致性方案。"
        };
        String question = questions[Math.min(index - 1, questions.length - 1)];
        return new GeneratedQuestion(
                MOCK_PREFIX + "[" + positionType + "][" + questionType + "] " + question,
                "参考答案应覆盖核心概念、关键流程、常见坑和实践取舍。",
                "满分20分：概念准确8分，方案完整5分，实践经验4分，风险和权衡3分。"
        );
    }
}
