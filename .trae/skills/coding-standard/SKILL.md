---
name: "coding-standard"
description: "Enforces coding standards for Spring Boot projects. Invoke when creating new code, refactoring, or reviewing code to ensure consistency and best practices."
---

# Coding Standard

## Overview

This skill enforces consistent coding standards for the AI Interview System project. All code must follow these guidelines.

## Tech Stack

| Component | Version |
|-----------|---------|
| Java | 17 |
| Spring Boot | 3.3.1 |
| MyBatis | 3.0.3 |
| LangChain4j | 0.35.0 |
| MySQL | 8.0+ |

## Package Structure

```
com.example.aiinterview/
├── common/              # Common components
│   ├── Result.java      # Unified response wrapper
│   └── exception/       # Global exception handling
│       ├── BusinessException.java
│       └── GlobalExceptionHandler.java
├── config/              # Configuration classes
├── controller/          # REST API endpoints
├── service/             # Business logic (interfaces)
├── service/impl/        # Business logic (implementations)
├── repository/          # Data access layer (MyBatis Mappers)
├── entity/              # Database entities
├── dto/                 # Data transfer objects
├── util/                # Utility classes
└── AiInterviewApplication.java
```

## Naming Conventions

### General Rules
- **PascalCase**: Classes, records, enums, interfaces
- **camelCase**: Methods, variables, parameters
- **UPPER_SNAKE_CASE**: Constants
- **snake_case**: Database tables, columns

### File Naming
- Controller: `XxxController.java`
- Service Interface: `XxxService.java`
- Service Implementation: `XxxServiceImpl.java`
- Repository/Mapper: `XxxRepository.java`
- Entity: `Xxx.java` (matches table name, singular)
- DTO Request: `XxxRequest.java`
- DTO Response: `XxxResponse.java`
- Exception: `XxxException.java`

## Code Style

### 1. Controller Layer

**Rules:**
- Use `@RestController` with base path `/api/xxx`
- Constructor injection only (no `@Autowired`)
- Validate request bodies with `@Valid`
- Use `@PathVariable`, `@RequestParam`, `@RequestBody` appropriately
- Return `Result<T>` wrapper for all responses
- Handle pagination with `page` and `size` parameters

**Example:**
```java
@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping("/history")
    public Result<InterviewHistoryPageResponse> history(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(interviewService.historyPage(page, size));
    }

    @PostMapping("/start")
    public Result<QuestionResponse> start(@Valid @RequestBody StartInterviewRequest request) {
        return Result.ok(interviewService.start(request));
    }
}
```

### 2. Service Layer

**Rules:**
- Define interface first, then implementation
- Use `@Service` annotation on implementation
- Handle business logic and transactions
- Use `@Transactional` for write operations
- Throw custom exceptions for business errors
- Log important operations

**Example:**
```java
public interface InterviewService {
    QuestionResponse start(StartInterviewRequest request);
    AnswerScoreResponse submitAnswer(Long interviewId, Long questionId, String userAnswer);
}

@Service
public class InterviewServiceImpl implements InterviewService {
    // Implementation
}
```

### 3. Repository Layer

**Rules:**
- Use MyBatis `@Mapper` annotation
- Define SQL in XML or use annotations
- Use parameter annotations `@Param`
- Follow MyBatis best practices

**Example:**
```java
@Mapper
public interface InterviewSessionRepository {
    InterviewSession findById(Long id);
    void insert(InterviewSession session);
    void update(InterviewSession session);
    List<InterviewSession> findByPage(@Param("page") int page, @Param("size") int size);
}
```

### 4. DTO Layer

**Rules:**
- Use Java Records for DTOs (immutable)
- Add validation annotations from `jakarta.validation`
- Request DTOs: Input parameters
- Response DTOs: Output data
- Page Response: Must contain `list`, `total`, `pageNum`, `pageSize`

**Example:**
```java
public record StartInterviewRequest(
        @NotNull(message = "职位类型不能为空")
        String positionType,
        
        Long directionId
) {}

public record InterviewHistoryPageResponse(
        List<InterviewHistoryItemResponse> list,
        long total,
        int pageNum,
        int pageSize
) {}
```

### 5. Entity Layer

**Rules:**
- Use Lombok `@Data` for convenience
- Use `@Table` annotation for table mapping
- Use `@Id` for primary key
- Use `@Column` for column mapping if needed
- Follow camelCase to snake_case mapping (configured in MyBatis)

**Example:**
```java
@Data
@Table(name = "interview_session")
public class InterviewSession {
    @Id
    private Long id;
    private String positionType;
    private String status;
    private LocalDateTime createdAt;
}
```

## Response Wrapper

### Standard Response Format

All API responses must use the `Result<T>` wrapper:

```java
public class Result<T> {
    private int code;
    private String message;
    private T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    // Getters
}
```

### Usage

```java
// Success with data
return Result.ok(user);

// Success with message and data
return Result.ok("操作成功", user);

// Error
return Result.error(400, "参数错误");
return Result.error(404, "资源不存在");
```

## Exception Handling

### Custom Exceptions

Define custom exceptions for business errors:

```java
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
```

### Global Exception Handler

Use `@RestControllerAdvice` for global exception handling:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("参数验证失败");
        return Result.error(400, message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统异常");
    }
}
```

## Logging

### Rules
- Use `@Slf4j` from Lombok
- Log levels:
  - `DEBUG`: Detailed debug information
  - `INFO`: General flow information
  - `WARN`: Unexpected but recoverable situations
  - `ERROR`: Errors that require attention

### Example

```java
@Slf4j
@Service
public class InterviewServiceImpl {
    
    public QuestionResponse start(StartInterviewRequest request) {
        log.info("Starting interview for position: {}", request.positionType());
        try {
            // Logic
        } catch (Exception e) {
            log.error("Failed to start interview: {}", e.getMessage(), e);
            throw new BusinessException(500, "面试启动失败");
        }
    }
}
```

## Security

### Rules
- Validate all inputs
- Sanitize user inputs to prevent XSS
- Use parameterized queries (MyBatis handles this)
- Never log sensitive information (passwords, tokens)
- Validate file uploads (size, type)

## Testing

### Rules
- Write unit tests for business logic
- Use Spring Boot Test
- Mock external dependencies
- Follow AAA pattern (Arrange, Act, Assert)

## Best Practices Checklist

- [ ] Controller returns `Result<T>`
- [ ] Service has interface and implementation
- [ ] DTOs use validation annotations
- [ ] Custom exceptions for business errors
- [ ] Global exception handler configured
- [ ] Constructor injection used
- [ ] Proper logging implemented
- [ ] SQL uses parameterized queries
- [ ] No sensitive data in logs
- [ ] Code follows naming conventions