package com.example.aiinterview.service.ai;

import com.example.aiinterview.common.ApiCode;
import com.example.aiinterview.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 快速复盘（填空题试卷）相关的大模型调用。
 */
@Slf4j
@Service
public class AiQuickReviewClient extends AbstractAiClient {

    private static final Pattern ITEM_PATTERN = Pattern.compile(
            "ITEM\\s+(\\d+)\\s*\\RPOINT:\\s*(.*?)\\s*\\RQUESTION:\\s*(.*?)\\s*\\RANSWER:\\s*(.*?)\\s*\\RREFERENCE:\\s*(.*?)\\s*\\REXPLANATION:\\s*(.*?)(?=\\RITEM\\s+\\d+|\\z)",
            Pattern.DOTALL
    );

    public AiQuickReviewClient(AiModelHolder modelHolder) {
        super(modelHolder);
    }

    public record QuickReviewQuestion(Integer questionIndex,
                                      String knowledgePoint,
                                      String questionContent,
                                      String blankAnswer,
                                      String referenceAnswer,
                                      String explanation) {
    }

    public record QuickReviewResult(String userProfile, List<String> weakPoints, List<QuickReviewQuestion> questions) {
    }

    /**
     * 基于已维护的用户画像生成填空题试卷。
     * 不允许模型重新改写画像，保证“画像维护”和“生成试卷”职责分离。
     */
    public QuickReviewResult generateQuickReviewFromProfile(String profileSummary,
                                                            List<String> weakPoints,
                                                            List<String> learningSuggestions,
                                                            int questionCount) {
        if (!modelHolder.isAvailable()) {
            return mockQuickReview(questionCount);
        }

        String prompt = """
                你是技术复盘教练。请基于已经维护好的用户画像生成填空题试卷。
                目标是通过题海式练习加深理解，不是重新生成用户画像。
                要求：
                1. 生成 %d 道填空题，每题只考一个关键知识点。
                2. 填空题必须有明确空位，用 ____ 表示。
                3. 标准答案要短，适合学生填写。
                4. 解析要说明为什么这个空这样填，帮助加深理解。
                5. 严格使用 ASCII 标签输出，不要输出 Markdown，不要输出多余内容。

                当前用户画像：
                %s

                薄弱点：
                %s

                学习建议：
                %s

                输出格式：
                PROFILE: 复用当前画像，不要改写画像
                FOCUS: 薄弱点; 薄弱点; 薄弱点
                ITEM 1
                POINT: ...
                QUESTION: ...
                ANSWER: ...
                REFERENCE: ...
                EXPLANATION: ...
                ITEM 2
                POINT: ...
                QUESTION: ...
                ANSWER: ...
                REFERENCE: ...
                EXPLANATION: ...
                """.formatted(
                questionCount,
                profileSummary,
                String.join("; ", weakPoints),
                String.join("; ", learningSuggestions)
        );

        String output = call(prompt);
        List<QuickReviewQuestion> questions = parseQuickReviewQuestions(output, questionCount);
        if (questions.isEmpty()) {
            throw parseFail("填空题");
        }
        // FOCUS 解析失败时回退到画像里的真实薄弱点（不是编造数据）。
        List<String> focus = splitFocus(optionalField(output, "FOCUS:", "ITEM 1", ""));
        return new QuickReviewResult(profileSummary, focus.isEmpty() ? weakPoints : focus, questions);
    }

    /**
     * 基于历史弱点证据直接生成画像和填空题试卷。
     */
    public QuickReviewResult generateQuickReview(String evidenceText, int questionCount) {
        if (!modelHolder.isAvailable()) {
            return mockQuickReview(questionCount);
        }

        String prompt = """
                你是一个技术面试复盘教练。请根据用户的历史弱点证据，生成用户画像，并围绕薄弱点生成填空题。
                目标是通过题海式练习加深理解，不是重新做开放问答。

                要求：
                1. 先总结用户画像，指出知识短板、易混概念、练习优先级。
                2. 输出 3 到 6 个薄弱点，用分号分隔。
                3. 生成 %d 道填空题，每题只考一个关键知识点。
                4. 填空题要有明确空位，用 ____ 表示。
                5. 标准答案要短，适合学生填写。
                6. 解析要说明为什么这个空这样填，帮助加深理解。
                7. 严格使用下面的 ASCII 标签输出，不要输出 Markdown，不要输出多余内容。

                历史弱点证据：
                %s

                输出格式：
                PROFILE: ...
                FOCUS: 薄弱点1; 薄弱点2; 薄弱点3
                ITEM 1
                POINT: ...
                QUESTION: ...
                ANSWER: ...
                REFERENCE: ...
                EXPLANATION: ...
                ITEM 2
                POINT: ...
                QUESTION: ...
                ANSWER: ...
                REFERENCE: ...
                EXPLANATION: ...
                """.formatted(questionCount, evidenceText);

        String output = call(prompt);
        List<QuickReviewQuestion> questions = parseQuickReviewQuestions(output, questionCount);
        if (questions.isEmpty()) {
            // 不再静默退回本地兜底题：解析失败必须显式报错，让用户重试。
            throw parseFail("填空题");
        }
        String profile = requireField(output, "PROFILE:", "FOCUS:", "PROFILE");
        List<String> weakPoints = splitFocus(requireField(output, "FOCUS:", "ITEM 1", "FOCUS"));
        return new QuickReviewResult(profile, weakPoints, questions);
    }

    /** 匹配模型按 ITEM/POINT/QUESTION/ANSWER/REFERENCE/EXPLANATION 输出的结构化题目块。 */
    private List<QuickReviewQuestion> parseQuickReviewQuestions(String output, int questionCount) {
        List<QuickReviewQuestion> questions = new ArrayList<>();
        Matcher matcher = ITEM_PATTERN.matcher(output);
        while (matcher.find() && questions.size() < questionCount) {
            questions.add(new QuickReviewQuestion(
                    questions.size() + 1,
                    matcher.group(2).trim(),
                    matcher.group(3).trim(),
                    matcher.group(4).trim(),
                    matcher.group(5).trim(),
                    matcher.group(6).trim()
            ));
        }
        return questions;
    }

    /** 无 API Key 时的本地模拟试卷，题目明确标注模拟来源。 */
    private QuickReviewResult mockQuickReview(int questionCount) {
        List<String> weakPoints = List.of("分布式事务模式", "缓存异常场景", "JVM 排查链路", "消息可靠性", "数据库索引优化");
        List<QuickReviewQuestion> questions = new ArrayList<>();
        String[][] samples = {
                {"分布式事务", "AT 模式的一阶段会先执行业务 SQL，并记录 ____ 用于二阶段回滚。", "undo_log", "AT 模式通过 undo_log 保存回滚前镜像和回滚后镜像。", "undo_log 是 AT 能自动回滚的关键。"},
                {"TCC", "TCC 的三个阶段分别是 Try、Confirm 和 ____。", "Cancel", "TCC 通过 Try 预留资源，Confirm 确认提交，Cancel 取消释放资源。", "Cancel 是失败补偿路径。"},
                {"缓存穿透", "缓存穿透通常指查询一个缓存和数据库都不存在的数据，可以用布隆过滤器或缓存 ____ 缓解。", "空值", "不存在的数据短暂缓存空值，可以避免请求持续打到数据库。", "空值缓存要设置较短过期时间。"},
                {"缓存击穿", "热点 Key 过期导致大量请求同时打到数据库，这类问题叫缓存 ____。", "击穿", "缓存击穿的核心是单个热点 Key 失效。", "它和雪崩的区别是影响范围更集中。"},
                {"JVM", "排查 Full GC 频繁时，通常先看 GC 日志、堆内存占用和对象 ____。", "分布", "对象分布能帮助判断哪些对象占用内存以及是否存在泄漏。", "排查链路要从现象到证据。"},
                {"消息可靠性", "MQ 保证可靠投递通常要关注生产者确认、Broker 持久化和消费者 ____。", "幂等", "消费者幂等可以避免重复消息造成业务副作用。", "可靠性不是只看发送成功。"},
                {"MySQL 索引", "联合索引使用时要遵循最左 ____ 原则。", "前缀", "最左前缀原则决定了联合索引能否被有效利用。", "查询条件顺序和范围查询都会影响索引使用。"},
                {"事务隔离", "MySQL InnoDB 默认隔离级别是 ____。", "可重复读", "InnoDB 默认 REPEATABLE READ，并通过 MVCC 和锁机制处理一致性。", "这是事务题的高频基础点。"},
                {"AOP", "Spring AOP 默认主要基于动态代理，接口优先使用 ____ 代理。", "JDK", "有接口时 Spring AOP 通常使用 JDK 动态代理。", "无接口时通常使用 CGLIB。"},
                {"线程池", "线程池拒绝策略会在线程数达到上限且 ____ 已满时触发。", "队列", "线程池无法继续接收任务时才触发拒绝策略。", "要同时看核心线程、最大线程和队列。"}
        };
        for (int i = 0; i < Math.min(questionCount, samples.length); i++) {
            String[] sample = samples[i];
            questions.add(new QuickReviewQuestion(i + 1, sample[0], sample[1], sample[2], sample[3], sample[4]));
        }
        String mockPrefix = MOCK_PREFIX;
        questions.replaceAll(question -> new QuickReviewQuestion(
                question.questionIndex(),
                question.knowledgePoint(),
                mockPrefix + question.questionContent(),
                question.blankAnswer(),
                question.referenceAnswer(),
                question.explanation()
        ));
        return new QuickReviewResult(
                MOCK_PREFIX + "当前画像显示：用户适合用填空题强化概念边界，优先补齐分布式事务、缓存、JVM、消息可靠性和数据库索引等高频薄弱点。",
                weakPoints,
                questions
        );
    }
}
