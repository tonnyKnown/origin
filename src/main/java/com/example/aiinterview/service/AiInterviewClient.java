package com.example.aiinterview.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiInterviewClient {

    private static final Logger log = LoggerFactory.getLogger(AiInterviewClient.class);

    private final ChatLanguageModel chatModel;

    private final String modelName;

    public AiInterviewClient(
            @Value("${ai.deepseek.api-key:}") String apiKey,
            @Value("${ai.deepseek.base-url:https://api.deepseek.com/v1}") String baseUrl,
            @Value("${ai.deepseek.model-name:deepseek-chat}") String modelName) {
        this.modelName = modelName;
        if (StringUtils.hasText(apiKey)) {
            this.chatModel = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .modelName(modelName)
                    .timeout(Duration.ofSeconds(60))
                    .build();
            log.info("DeepSeek chat model enabled. model={}, baseUrl={}", modelName, baseUrl);
        } else {
            this.chatModel = null;
            log.warn("DeepSeek API key is not configured. Set DEEPSEEK_API_KEY in environment variables or project .env.");
        }
    }

    public boolean isAvailable() {
        return chatModel != null;
    }

    public String modelName() {
        return modelName;
    }

    public GeneratedQuestion generateQuestion(String positionType, int questionIndex, String questionType, String askedQuestions) {
        if (chatModel == null) {
            return mockQuestion(positionType, questionIndex, questionType);
        }

        String prompt = """
                请优先依据下面这份候选人简历能力画像出题，题目要贴近简历，不要泛泛而谈：
                1. Java核心：反射、泛型、代理、面向对象、常用数据结构与算法。
                2. Spring体系：Spring Boot、Spring Cloud、IOC、AOP、声明式事务、Spring MVC、SSH整合。
                3. 持久层与数据库：MyBatis懒加载、Oracle、MySQL、SQL优化、慢查询分析。
                4. 缓存与中间件：Redis、MongoDB、RabbitMQ、Kafka、缓存一致性、消息可靠性。
                5. 搜索能力：Solr全文检索、Elasticsearch使用经验。
                6. JVM与排查：JVM内存模型、垃圾回收、JVM调优、Linux部署和问题排查。
                7. 工程协作：Git、SVN、Maven。
                8. AI方向：AI辅助编程、Agent、Embedding、Prompt工程、LLM、Function Calling、MCP。

                简历匹配后的题型映射：
                - BASIC：Java核心，优先考察反射、泛型、动态代理、集合、数据结构与算法。
                - CONCURRENCY：JVM/并发/排查，优先考察内存模型、GC、JUC、线程池、JVM调优。
                - MIDDLEWARE：数据库/缓存/消息/搜索，优先考察MyBatis、MySQL/Oracle优化、Redis、MongoDB、RabbitMQ/Kafka、Solr/ES。
                - PROJECT：项目实战，优先考察Spring Boot/Spring Cloud、事务、AOP、Linux部署、慢查询和性能问题定位。
                - ARCHITECTURE：架构和AI应用，优先考察微服务高可用、消息一致性、搜索架构、Agent/Embedding/Function Calling/MCP落地。

                你是一名资深Java技术面试官，请为候选人生成一道%s方向的问答题。
                当前是第%d题，总共5题。
                当前题型：%s。
                题型说明：
                - BASIC：Java基础八股题，重点考察集合、泛型、异常、IO、反射、类加载等基础知识。
                - CONCURRENCY：JVM/并发八股题，重点考察线程池、synchronized、volatile、AQS、JUC、GC等。
                - MIDDLEWARE：数据库/缓存/中间件八股题，重点考察MySQL、Redis、MQ、缓存一致性等。
                - PROJECT：项目实战题，重点考察线上排查、性能优化、工程落地和业务经验。
                - ARCHITECTURE：架构设计题，重点考察微服务、分布式事务、高可用、高并发设计。
                已问过的问题如下，不能重复：
                %s

                请严格按以下格式输出，不要输出多余内容：
                题目：...
                参考答案：...
                评分标准：...
                """.formatted(positionType, questionIndex, questionType, askedQuestions);

        String output = chatModel.generate(prompt);
        return new GeneratedQuestion(
                extract(output, "题目：", "参考答案：", "请结合项目经验说明一个技术问题。"),
                extract(output, "参考答案：", "评分标准：", "需要覆盖核心概念、关键流程、边界场景和实践经验。"),
                extractTail(output, "评分标准：", "满分20分，概念8分，实践6分，表达4分，风险意识2分。")
        );
    }

    public ScoreResult scoreAnswer(String question, String referenceAnswer, String scoringRule, String userAnswer) {
        if (chatModel == null) {
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

        String output = chatModel.generate(prompt);
        int score = extractScore(output);
        return new ScoreResult(
                score,
                extract(output, "本题总结：", "点评：", "本题已完成评分，回答覆盖了部分核心要点。"),
                extract(output, "点评：", "建议：", "回答已完成评分。"),
                extractTail(output, "建议：", "建议补充更具体的落地实践、异常场景和性能优化思路。")
        );
    }

    public String summarize(String positionType, String detail) {
        if (chatModel == null) {
            return "整体完成了5道题的回答。建议继续加强核心原理、生产实践、性能优化和系统设计表达。";
        }

        String prompt = """
                你是一名资深面试官。请基于以下%s面试记录，给出简洁的整体评价和学习建议。
                要求包含：能力优势、主要短板、后续提升建议。控制在300字以内。

                面试记录：
                %s
                """.formatted(positionType, detail);
        return chatModel.generate(prompt);
    }

    public String answerManualQuestion(String questionContent) {
        if (chatModel == null) {
            return "本地模拟回答：这个问题需要先明确核心概念，再说明关键流程、常见风险和实际落地方案。建议结合具体业务场景补充一到两个例子。";
        }

        String prompt = """
                你是一名资深Java技术专家，请简洁回答用户提出的问题。
                要求：
                1. 控制在300字以内。
                2. 先直接给结论，再补充关键原因或步骤。
                3. 不要输出无关寒暄。

                用户问题：
                %s
                """.formatted(questionContent);
        return chatModel.generate(prompt);
    }

    public ScoreResult scoreManualSelfTest(String questionContent, String userAnswer) {
        if (chatModel == null) {
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
                本题总结：..
                点评：..
                建议：..
                """.formatted(questionContent, userAnswer);

        String output = chatModel.generate(prompt);
        int score = extractScore(output);
        return new ScoreResult(
                score,
                extract(output, "本题总结：", "点评：", "本题已完成自测评分。"),
                extract(output, "点评：", "建议：", "自测答案已完成评分。"),
                extractTail(output, "建议：", "建议补充核心概念、关键流程、边界场景和项目实践。")
        );
    }

    public UserProfileResult generateUserProfile(String evidenceText) {
        if (chatModel == null) {
            throw new IllegalStateException("请先配置大模型 API Key，再更新用户画像。");
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

        String output = chatModel.generate(prompt);
        return new UserProfileResult(
                extractAscii(output, "PROFILE:", "WEAK:", "暂未形成稳定画像，请先完成面试、手动题库自测或快速复盘。"),
                splitFocus(extractAscii(output, "WEAK:", "STRENGTH:", "")),
                splitFocus(extractAscii(output, "STRENGTH:", "SUGGESTION:", "")),
                splitFocus(extractAsciiTail(output, "SUGGESTION:", ""))
        );
    }

    public QuickReviewResult generateQuickReviewFromProfile(String profileSummary,
                                                            List<String> weakPoints,
                                                            List<String> learningSuggestions,
                                                            int questionCount) {
        if (chatModel == null) {
            throw new IllegalStateException("请先配置大模型 API Key，再基于用户画像生成复盘试卷。");
        }

        // 这里基于已有画像出题，不允许模型重新改写画像，保证“画像维护”和“生成试卷”职责分离。
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

        String output = chatModel.generate(prompt);
        List<QuickReviewQuestion> questions = parseQuickReviewQuestions(output, questionCount);
        if (questions.isEmpty()) {
            // 正式画像试卷不使用本地兜底题，避免用户误以为题目来自当前画像。
            throw new IllegalStateException("大模型没有返回有效的填空题，请重新生成试卷。");
        }
        List<String> focus = splitFocus(extractAscii(output, "FOCUS:", "ITEM 1", String.join("; ", weakPoints)));
        return new QuickReviewResult(profileSummary, focus.isEmpty() ? weakPoints : focus, questions);
    }

    public QuickReviewResult generateQuickReview(String evidenceText, int questionCount) {
        if (chatModel == null) {
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

        String output = chatModel.generate(prompt);
        String profile = extractAscii(output, "PROFILE:", "FOCUS:", "用户需要围绕高频错题做填空强化。");
        String focusText = extractAscii(output, "FOCUS:", "ITEM 1", "");
        List<String> weakPoints = splitFocus(focusText);
        List<QuickReviewQuestion> questions = parseQuickReviewQuestions(output, questionCount);
        if (questions.isEmpty()) {
            return mockQuickReview(questionCount);
        }
        return new QuickReviewResult(profile, weakPoints, questions);
    }

    public QuickReviewScore scoreQuickReviewAnswer(String questionContent, String referenceAnswer, String userAnswer) {
        if (chatModel == null) {
            boolean correct = referenceAnswer != null
                    && userAnswer != null
                    && referenceAnswer.trim().equalsIgnoreCase(userAnswer.trim());
            int score = correct ? 20 : Math.max(6, Math.min(16, userAnswer == null ? 0 : userAnswer.trim().length() * 2));
            return new QuickReviewScore(
                    score,
                    correct,
                    correct ? "回答命中标准答案。" : "回答与标准答案还有差距，需要回到核心概念重新理解。",
                    "建议先记住标准答案，再用自己的话解释它为什么成立。"
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

        String output = chatModel.generate(prompt);
        int score = extractAsciiScore(output);
        boolean correct = extractAscii(output, "CORRECT:", "COMMENT:", "false").toLowerCase().contains("true");
        return new QuickReviewScore(
                score,
                correct,
                extractAscii(output, "COMMENT:", "SUGGESTION:", "已完成批改。"),
                extractAsciiTail(output, "SUGGESTION:", "建议继续围绕该知识点做相似填空题。")
        );
    }

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
        return new QuickReviewResult(
                "当前画像显示：用户适合用填空题强化概念边界，优先补齐分布式事务、缓存、JVM、消息可靠性和数据库索引等高频薄弱点。",
                weakPoints,
                questions
        );
    }

    private List<QuickReviewQuestion> parseQuickReviewQuestions(String output, int questionCount) {
        List<QuickReviewQuestion> questions = new ArrayList<>();
        // 匹配模型按 ITEM/POINT/QUESTION/ANSWER/REFERENCE/EXPLANATION 输出的结构化题目块。
        Pattern pattern = Pattern.compile(
                "ITEM\\s+(\\d+)\\s*\\RPOINT:\\s*(.*?)\\s*\\RQUESTION:\\s*(.*?)\\s*\\RANSWER:\\s*(.*?)\\s*\\RREFERENCE:\\s*(.*?)\\s*\\REXPLANATION:\\s*(.*?)(?=\\RITEM\\s+\\d+|\\z)",
                Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(output);
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

    private List<String> splitFocus(String focusText) {
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

    private String extractAscii(String text, String start, String end, String fallback) {
        int startIndex = text.indexOf(start);
        int endIndex = text.indexOf(end, Math.max(startIndex, 0));
        if (startIndex >= 0 && endIndex > startIndex) {
            return text.substring(startIndex + start.length(), endIndex).trim();
        }
        return fallback;
    }

    private String extractAsciiTail(String text, String start, String fallback) {
        int startIndex = text.indexOf(start);
        if (startIndex >= 0) {
            return text.substring(startIndex + start.length()).trim();
        }
        return fallback;
    }

    private int extractAsciiScore(String text) {
        Matcher matcher = Pattern.compile("SCORE:\\s*(\\d{1,2})").matcher(text);
        if (matcher.find()) {
            return Math.min(20, Math.max(0, Integer.parseInt(matcher.group(1))));
        }
        return 12;
    }

    private GeneratedQuestion mockQuestion(String positionType, int index, String questionType) {
        if (StringUtils.hasText(questionType)) {
            String question = switch (questionType) {
                case "BASIC" -> "请说明Java动态代理的两种常见实现方式，并结合Spring AOP解释代理对象是如何增强目标方法的。";
                case "CONCURRENCY" -> "线上Java服务出现频繁Full GC和接口RT升高，你会如何结合JVM内存、GC日志、线程栈和Linux命令定位问题？";
                case "MIDDLEWARE" -> "一个订单查询接口涉及MySQL、Redis、MongoDB和Elasticsearch/Solr，你会如何设计查询链路，并处理缓存一致性、慢查询和搜索数据同步问题？";
                case "PROJECT" -> "请结合Spring Boot/Spring Cloud项目经验，说明一次接口性能优化或慢查询治理的完整排查过程，包括MyBatis、事务、AOP或Linux排查手段。";
                default -> "如果要在Java微服务系统中落地AI Agent能力，请说明Embedding、Prompt工程、Function Calling和MCP分别承担什么角色，以及如何保证可用性和安全边界。";
            };
            return new GeneratedQuestion(
                    "[" + positionType + "][" + questionType + "] " + question,
                    "参考答案应覆盖简历相关技术点、核心原理、关键流程、常见风险、线上排查方法和项目落地经验。",
                    "满分20分：核心概念准确8分，方案和流程完整5分，项目实践和排查经验5分，表达清晰与风险意识2分。"
            );
        }
        String[] questions = {
                "HashMap在JDK 1.8中的底层结构是什么？为什么链表长度达到阈值后要转红黑树？",
                "请说明volatile的作用和局限性，它能否保证复合操作的原子性？",
                "Redis缓存击穿、穿透、雪崩分别是什么？生产环境分别如何解决？",
                "如果线上接口偶发RT飙高，你会如何定位问题？请结合JVM、数据库和下游依赖说明。",
                "请设计一个高并发秒杀系统，说明限流、库存扣减、异步下单和一致性方案。"
        };
        String question = questions[Math.min(index - 1, questions.length - 1)];
        return new GeneratedQuestion(
                "[" + positionType + "][" + questionType + "] " + question,
                "参考答案应覆盖核心概念、关键流程、常见坑、线上实践和取舍理由。",
                "满分20分：概念准确8分，方案完整5分，实践经验4分，风险和权衡3分。"
        );
    }

    private ScoreResult mockScore(String userAnswer) {
        int length = userAnswer == null ? 0 : userAnswer.length();
        int score = Math.min(20, Math.max(8, length / 12));
        return new ScoreResult(score, "本题模拟总结：回答覆盖了部分核心概念，但细节和案例还可以继续补充。", "本地模拟评分：回答已覆盖部分要点，真实评分需要配置DEEPSEEK_API_KEY。", "建议补充更具体的业务场景、关键参数、异常处理和性能数据。");
    }

    private String extract(String text, String start, String end, String fallback) {
        int startIndex = text.indexOf(start);
        int endIndex = text.indexOf(end);
        if (startIndex >= 0 && endIndex > startIndex) {
            return text.substring(startIndex + start.length(), endIndex).trim();
        }
        return fallback;
    }

    private String extractTail(String text, String start, String fallback) {
        int startIndex = text.indexOf(start);
        if (startIndex >= 0) {
            return text.substring(startIndex + start.length()).trim();
        }
        return fallback;
    }

    private int extractScore(String text) {
        Matcher matcher = Pattern.compile("分数：\\s*(\\d{1,2})").matcher(text);
        if (matcher.find()) {
            return Math.min(20, Math.max(0, Integer.parseInt(matcher.group(1))));
        }
        return 12;
    }

    public record GeneratedQuestion(String questionContent, String referenceAnswer, String scoringRule) {
    }

    public record ScoreResult(Integer score, String answerSummary, String aiComment, String suggestion) {
    }

    public record QuickReviewResult(String userProfile, List<String> weakPoints, List<QuickReviewQuestion> questions) {
    }

    public record QuickReviewQuestion(Integer questionIndex,
                                      String knowledgePoint,
                                      String questionContent,
                                      String blankAnswer,
                                      String referenceAnswer,
                                      String explanation) {
    }

    public record QuickReviewScore(Integer score, Boolean correct, String comment, String suggestion) {
    }

    public record UserProfileResult(String profileSummary,
                                    List<String> weakPoints,
                                    List<String> strengthPoints,
                                    List<String> learningSuggestions) {
    }
}
