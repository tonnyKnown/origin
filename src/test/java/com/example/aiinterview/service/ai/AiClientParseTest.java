package com.example.aiinterview.service.ai;

import com.example.aiinterview.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证拆分后的 AI 子客户端在“大模型返回格式正确”时能正确解析，
 * 在“格式异常”时显式抛出 BusinessException 而不是静默返回兜底假数据。
 */
class AiClientParseTest {

    @Test
    void scoreAnswer_parsesWellFormedOutput() {
        FakeChatModel fake = new FakeChatModel();
        fake.setAnswer("分数：18\n本题总结：回答到位\n点评：不错\n建议：多练习");
        AiScoringClient client = new AiScoringClient(new AiModelHolder("fake", fake));

        AiScoringClient.ScoreResult result = client.scoreAnswer("题目", "参考答案", "评分标准", "用户回答");

        assertEquals(18, result.score());
        assertEquals("回答到位", result.answerSummary());
        assertEquals("不错", result.aiComment());
        assertEquals("多练习", result.suggestion());
    }

    @Test
    void scoreAnswer_throwsWhenScoreMissing() {
        FakeChatModel fake = new FakeChatModel();
        fake.setAnswer("本题总结：回答到位\n点评：不错\n建议：多练习");
        AiScoringClient client = new AiScoringClient(new AiModelHolder("fake", fake));

        assertThrows(BusinessException.class, () -> client.scoreAnswer("题目", "参考答案", "评分标准", "用户回答"));
    }

    @Test
    void generateQuestion_parsesWellFormedOutput() {
        FakeChatModel fake = new FakeChatModel();
        fake.setAnswer("题目：什么是动态代理\n参考答案：JDK 与 CGLIB\n评分标准：概念与场景");
        AiQuestionClient client = new AiQuestionClient(new AiModelHolder("fake", fake));

        AiQuestionClient.GeneratedQuestion q = client.generateQuestion("软件开发方向 / Java / Java 后端开发", 1, "BASIC", "", 5);

        assertEquals("什么是动态代理", q.questionContent());
        assertEquals("JDK 与 CGLIB", q.referenceAnswer());
        assertEquals("概念与场景", q.scoringRule());
    }

    @Test
    void generateQuestion_throwsWhenMalformed() {
        FakeChatModel fake = new FakeChatModel();
        fake.setAnswer("完全不是预期格式的文本");
        AiQuestionClient client = new AiQuestionClient(new AiModelHolder("fake", fake));

        assertThrows(BusinessException.class,
                () -> client.generateQuestion("软件开发方向 / Java / Java 后端开发", 1, "BASIC", "", 5));
    }

    @Test
    void generateUserProfile_parsesWeakAndStrength() {
        FakeChatModel fake = new FakeChatModel();
        fake.setAnswer("PROFILE: 当前能力中等\nWEAK: 并发; 缓存; JVM\nSTRENGTH: 基础扎实\nSUGGESTION: 多刷题; 看源码");
        AiProfileClient client = new AiProfileClient(new AiModelHolder("fake", fake));

        AiProfileClient.UserProfileResult result = client.generateUserProfile("证据文本");

        assertEquals("当前能力中等", result.profileSummary());
        assertEquals(3, result.weakPoints().size());
        assertEquals("基础扎实", result.strengthPoints().get(0));
        assertEquals(2, result.learningSuggestions().size());
    }

    @Test
    void generateUserProfile_throwsWhenProfileMissing() {
        FakeChatModel fake = new FakeChatModel();
        fake.setAnswer("WEAK: 并发\nSTRENGTH: 基础\nSUGGESTION: 多刷题");
        AiProfileClient client = new AiProfileClient(new AiModelHolder("fake", fake));

        assertThrows(BusinessException.class, () -> client.generateUserProfile("证据文本"));
    }

    @Test
    void generateQuickReviewFromProfile_parsesItems() {
        FakeChatModel fake = new FakeChatModel();
        fake.setAnswer("""
                PROFILE: 模拟画像
                FOCUS: 分布式事务; 缓存
                ITEM 1
                POINT: 分布式事务
                QUESTION: AT 模式记录 ____ 用于回滚
                ANSWER: undo_log
                REFERENCE: undo_log 是关键
                EXPLANATION: 说明原因
                ITEM 2
                POINT: 缓存
                QUESTION: 缓存穿透可用 ____ 缓解
                ANSWER: 布隆过滤器
                REFERENCE: 布隆过滤器
                EXPLANATION: 原因
                """);
        AiQuickReviewClient client = new AiQuickReviewClient(new AiModelHolder("fake", fake));

        AiQuickReviewClient.QuickReviewResult result = client.generateQuickReviewFromProfile("模拟画像",
                java.util.List.of("分布式事务"), java.util.List.of("多刷题"), 10);

        assertEquals(2, result.questions().size());
        assertEquals("undo_log", result.questions().get(0).blankAnswer());
        assertEquals("布隆过滤器", result.questions().get(1).blankAnswer());
    }

    @Test
    void generateQuickReviewFromProfile_throwsWhenNoItem() {
        FakeChatModel fake = new FakeChatModel();
        fake.setAnswer("PROFILE: 模拟画像\nFOCUS: 分布式事务");
        AiQuickReviewClient client = new AiQuickReviewClient(new AiModelHolder("fake", fake));

        assertThrows(BusinessException.class, () -> client.generateQuickReviewFromProfile("模拟画像",
                java.util.List.of("分布式事务"), java.util.List.of("多刷题"), 10));
    }
}
