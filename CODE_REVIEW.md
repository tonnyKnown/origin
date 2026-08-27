# ai_interviewer 项目问题清单与整改记录

按严重程度分三档：高（影响正确性/数据可信度）、中（架构与工程化）、低（代码卫生）。
状态标记：✅ 已修复　🟡 部分修复　⚪ 未做（附原因）。

> 第二轮整改于 2026-08-27 完成（除标注外均已落地）。离线环境限制：本地 `.m2` 仅预取了
> langchain4j / JUnit / Lombok 等少数依赖，**缺少 Spring Boot 全家桶**，且无外网，因此
> 无法用 `mvn` 做端到端编译/测试验证；代码改动经逐文件人工核对，并补充了可离线运行的
> 单元测试源码（需 `mvn test` 执行）。

## 一、高优先级

### 1. LLM 输出解析失败时"静默造假" ✅
原 `AiInterviewClient` 所有解析都带 fallback（匹配不到分数默认 12 分、字段缺失返回默认文案）。
**整改**：拆分出的 `AbstractAiClient` 统一提供 `requireField/requireTail/requireScore`，解析缺失即抛
`BusinessException(ApiCode.UPSTREAM_ERROR, …)`，不再返回兜底假数据。`AiQuickReviewClient` 解析不到题目也直接抛错。

### 2. 出题 Prompt 与方向体系脱节 ✅
原 prompt 硬编码一份 Java 简历画像。
**整改**：新增 `DirectionProfile.resolve(positionType)`，按赛道（软件/运维/硬件/机器人/通用）返回
出题关注点；`AiQuestionClient.generateQuestion` 据此生成贴合方向的题，方向与字典完全解耦。

### 3. 无 API Key 时降级行为不一致 ✅
原画像/快练抛 400，出题/评分静默 mock。
**整改**：统一为“未配置 Key 时全部走 mock，结果带 `【本地模拟】` 前缀明确标注”，`UserProfileServiceImpl.refresh()`
不再抛 400，版本记录里模型名记为 `mock`。

### 4. 数据库事务包住了 60 秒的 LLM 调用 ✅
原 `start/submitAnswer/reAnswer/nextQuestion` 标 `@Transactional`，事务内同步调大模型。
**整改**：LLM 调用全部移出事务；落库改为独立短事务（`insertSessionAndQuestion` / `persistQuestion` /
`persistAnswer` / `persistReAnswer`）。`start()` 先事务外生成首题，再同事务落库会话+题目，生成失败不留空会话。

### 5. 先查后插的并发竞态 ✅
原并发双击提交命中唯一键后落入兜底 `Exception` → 500。
**整改**：`interview_answer(question_id)` 唯一键保留；`GlobalExceptionHandler` 新增
`DuplicateKeyException` → 409；`submitAnswer` 在 `persistAnswer` 短事务内再判一次重，彻底兜住竞态。

### 6. 用 spring.sql.init 当迁移工具 🟡
原 `mode: always` + `continue-on-error: true`。
**整改**：关闭 `continue-on-error`（暴露真实 SQL 错误）；脚本本身已用 `IF NOT EXISTS` + 条件 `ALTER` 幂等化。
未引入 Flyway：本地无 Flyway 依赖且无外网，离线不可行，列为后续项（application.yml 已加注释说明）。

## 二、中优先级

### 7. HTTP 状态码语义丢失 ✅
**整改**：`GlobalExceptionHandler` 改用 `ResponseEntity.status(...)` 返回真实状态码（400/401/404/409/500），
与 `Result.code` 保持一致。

### 8. 无用户体系 ⚪
所有业务表无 user_id，全局共享。
**未做**：取决于产品定位（自用 vs 多人）。预留 user_id 属数据模型变更，超出本轮范围，需先决策。

### 9. 监听 0.0.0.0 且无任何鉴权 ✅
**整改**：`server.address` 默认 `127.0.0.1`（可经 `SERVER_ADDRESS` 覆盖）；新增可选 `AuthFilter`
（`X-API-Token` 头校验），由 `app.auth.token`（或 `APP_AUTH_TOKEN`）控制，默认关闭，不破坏本地 UI。

### 10. LangChain4j 版本陈旧 + 废弃 API ⚪
原依赖 0.35.0，用旧 `generate(String)`。
**未做**：升级 1.x 需外网且 API 变动较大（structured output 重构），离线不可行。已用结构化正则解析降低风险，列为后续。

### 11. AiInterviewClient 是 569 行上帝类 ✅
**整改**：拆为 `AiModelHolder`（持有 ChatModel）+ `AbstractAiClient`（解析基类）+ 四个域客户端
`AiQuestionClient` / `AiScoringClient` / `AiProfileClient` / `AiQuickReviewClient`，prompt 内联在各客户端。

### 12. 前端 5 个页面复制粘贴 ✅
**整改**：抽取 `static/common.js`（`request` 已合并 204 No-Content 处理、`escapeHtml`），5 个页面统一引用，
删除各自内联定义。

## 三、低优先级

13. **零测试** ✅：新增 `src/test/.../AiClientParseTest`，用 `FakeChatModel` 覆盖出题/评分/画像/快练四类解析的成功与失败路径。
14. **每场固定 5 题硬编码** ✅：`app.interview.total-questions`（或 `APP_INTERVIEW_TOTAL_QUESTIONS`）可配置，默认 5。
15. **gitignore 与已跟踪文件矛盾** ✅：`git rm --cached` 移除 `.idea/` 与根 `.iml`；`.gitignore` 补充 `gpu_monitor/`、`.ttsvenv/`。
16. **.env.example 拼写** ✅：`your_myslqpassword` → `your_mysql_password`，并补充新变量说明。
17. **项目根目录混入无关产物** ✅：`.gitignore` 增加 `gpu_monitor/`、`.ttsvenv/`；`.idea`/`.iml` 移出索引（本地文件保留）。
18. **quick_review_evidence“表”并不存在** 🟡：已给 `QuickReviewEvidenceRepository` 加注释说明是跨表视图；重命名为 `*EvidenceQueryRepository` 改动较大，列为后续。
19. **Result 错误码无常量** ✅：新增 `ApiCode` 常量，handler 与部分 service 已使用；`Result` 仍无 traceId（可后续补）。
20. **历史记录深分页** 🟡：仍用 offset 分页；个人项目数据量小，可接受，未改。

## 新增/调整文件清单
- `src/main/java/com/example/aiinterview/common/ApiCode.java`（错误码常量）
- `src/main/java/com/example/aiinterview/common/filter/AuthFilter.java`（可选鉴权）
- `src/main/java/com/example/aiinterview/service/ai/AiModelHolder.java`
- `src/main/java/com/example/aiinterview/service/ai/AbstractAiClient.java`
- `src/main/java/com/example/aiinterview/service/ai/DirectionProfile.java`
- `src/main/java/com/example/aiinterview/service/ai/AiQuestionClient.java`
- `src/main/java/com/example/aiinterview/service/ai/AiScoringClient.java`
- `src/main/java/com/example/aiinterview/service/ai/AiProfileClient.java`
- `src/main/java/com/example/aiinterview/service/ai/AiQuickReviewClient.java`
- （删除）`src/main/java/com/example/aiinterview/service/AiInterviewClient.java`
- `src/main/resources/static/common.js`（前端公共函数）
- `src/test/java/com/example/aiinterview/service/ai/AiClientParseTest.java` + `FakeChatModel.java`

## 建议的后续项
- 引入 Flyway 管理 DDL（问题 6）
- 升级 LangChain4j 1.x 并用 structured output（问题 10）
- 产品确认是否要用户体系 / 鉴权默认开启（问题 8/9）
- `Result` 增加 traceId 便于排障（问题 19）
