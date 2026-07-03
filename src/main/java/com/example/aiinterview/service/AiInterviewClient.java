package com.example.aiinterview.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiInterviewClient {

    private final ChatLanguageModel chatModel;

    public AiInterviewClient(
            @Value("${ai.deepseek.api-key:}") String apiKey,
            @Value("${ai.deepseek.base-url:https://api.deepseek.com/v1}") String baseUrl,
            @Value("${ai.deepseek.model-name:deepseek-chat}") String modelName) {
        if (StringUtils.hasText(apiKey)) {
            this.chatModel = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .modelName(modelName)
                    .timeout(Duration.ofSeconds(60))
                    .build();
        } else {
            this.chatModel = null;
        }
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
}
