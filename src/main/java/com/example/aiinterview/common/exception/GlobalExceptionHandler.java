package com.example.aiinterview.common.exception;

import com.example.aiinterview.common.ApiCode;
import com.example.aiinterview.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return ResponseEntity.status(e.getCode()).body(Result.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Result<Void>> handleDuplicateKeyException(DuplicateKeyException e) {
        // 通常是并发重复提交命中唯一键（如 interview_answer.question_id），转成可理解的冲突提示。
        log.warn("唯一键冲突（可能为并发重复提交）: {}", e.getMessage());
        return ResponseEntity.status(ApiCode.CONFLICT)
                .body(Result.error(ApiCode.CONFLICT, "操作冲突：请勿重复提交同一内容"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("参数验证失败");
        log.warn("参数验证失败: {}", message);
        return ResponseEntity.status(ApiCode.BAD_REQUEST).body(Result.error(ApiCode.BAD_REQUEST, message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数错误: {}", e.getMessage());
        return ResponseEntity.status(ApiCode.BAD_REQUEST).body(Result.error(ApiCode.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
        // 静态资源未命中（例如浏览器自动探测的 /.well-known/*、/favicon.ico），
        // 属于预期行为，按 DEBUG 级别记录并直接返回 404，避免污染 error 日志。
        log.debug("静态资源未找到: {}", e.getResourcePath());
        return ResponseEntity.status(ApiCode.NOT_FOUND).body(Result.error(ApiCode.NOT_FOUND, "资源不存在"));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        // 没有匹配的 Controller 路由时的 404 分支，通常是前端刷新页面或请求路径错误。
        log.warn("请求路径不存在: {} {}", e.getHttpMethod(), e.getRequestURL());
        return ResponseEntity.status(ApiCode.NOT_FOUND).body(Result.error(ApiCode.NOT_FOUND, "请求路径不存在"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.status(ApiCode.INTERNAL_ERROR).body(Result.error(ApiCode.INTERNAL_ERROR, "系统异常"));
    }
}
