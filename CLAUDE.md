# CLAUDE.md — ai_interviewer 项目指引

## 项目简介

AI 面试助手（ai-interview-system）：面向技术面试训练的 Web 系统。按面试方向 AI 生成题目（每场 5 题：BASIC / CONCURRENCY / MIDDLEWARE / PROJECT / ARCHITECTURE），调用大模型评分点评，保存面试历史；另含手动题库、自测练习、快练复盘（Quick Review）、用户画像（UserProfile）模块。前端为 Spring Boot 托管的静态 HTML 页面，无前端框架。

## 技术栈

- Java 17 + Spring Boot 3.3.1（spring-boot-starter-web / validation）
- MyBatis 3.0.3（XML mapper，位于 `src/main/resources/mapper/`）
- MySQL（`schema.sql` 启动时自动执行，`spring.sql.init.mode: always` + `continue-on-error: true`，幂等 CREATE TABLE IF NOT EXISTS）
- LangChain4j 0.35.0（OpenAI 兼容客户端，默认接 DeepSeek）
- Lombok 1.18.38

## 构建与运行

```bash
# 构建（跳过测试）
mvn clean package -DskipTests

# 运行（默认端口 8080，绑定 0.0.0.0）
mvn spring-boot:run
# 或 java -jar target/ai-interview-system-1.0.0.jar
```

启动前置：
- MySQL 可用（默认 `jdbc:mysql://localhost:3306/example_db`，可用环境变量 `MYSQL_URL / MYSQL_USERNAME / MYSQL_PASSWORD` 覆盖）
- `DEEPSEEK_API_KEY`（未配置时 AI 功能降级为 mock 问答，服务仍可启动；可放在项目根 `.env` 或 `application-local.yml`）
- 可选环境变量：`DEEPSEEK_BASE_URL`（默认 https://api.deepseek.com/v1）、`DEEPSEEK_MODEL`（默认 deepseek-chat）

## 代码结构

```
src/main/java/com/example/aiinterview/
├── AiInterviewApplication.java   # 启动类
├── common/                       # Result 统一响应、BusinessException、GlobalExceptionHandler
├── controller/                   # REST 接口（/api/interview、/api/directions、/api/manual-questions、/api/quick-review、/api/profile、/api/wellness）
├── dto/                          # 请求/响应 record 类
├── entity/                       # 数据库实体（与 schema.sql 表一一对应）
├── repository/                   # MyBatis 接口 + resources/mapper/*.xml
└── service/                      # 业务接口与 impl/；AiInterviewClient 封装 LLM 调用
```

## 核心模块

| 模块 | Controller | 说明 |
| --- | --- | --- |
| 模拟面试 | InterviewController | start（按方向或 positionType）/ answer / next / continue / cancel / summary / history |
| 面试方向 | InterviewDirectionController | 方向管理（后台维护） |
| 手动题库 | ManualQuestionController | 录题、AI 生成参考答案、自测评分、备注、掌握标记 |
| 快练复盘 | QuickReviewController | 从历史低分题生成试卷、提交评分、证据（Evidence）记录 |
| 用户画像 | UserProfileController | 画像与版本（version）管理 |
| Wellness | WellnessController | 健康/状态接口 |

## 数据表

interview_session、interview_direction、interview_question、interview_answer、manual_question、quick_review_paper、quick_review_question（另有 quick_review_evidence）、user_profile、user_profile_version。均建在 schema.sql 中。

## 约定与注意事项

- 分层严格：Controller（参数校验 @Valid）→ Service（业务编排）→ Repository（MyBatis）；DTO 全部用 Java record。
- 统一响应格式 `Result<T>`；业务异常抛 `BusinessException`，由 GlobalExceptionHandler 兜底。
- MyBatis 开启 `map-underscore-to-camel-case`，XML SQL 写在 mapper 文件而非注解。
- AiInterviewClient 在无 API key 时自动降级 mock，不要在构造期抛异常。
- 前端页面在 `src/main/resources/static/`（index.html、history.html、manual-questions.html、quick-review.html、admin-directions.html、common-theme.css），改页面后无需编译，刷新即可。
- `gpu_monitor/` 与 `generate_tts.py` 是独立的运维/工具脚本，与主应用无关。
- 配置覆盖优先级：环境变量 > `.env` / `application-local.yml` > application.yml 默认值。
