package com.example.aiinterview.common.exception;

import com.example.aiinterview.common.Result;
import lombok.extern.slf4j.Slf4j;
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
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("参数验证失败");
        log.warn("参数验证失败: {}", message);
        return Result.error(400, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数错误: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        // 静态资源未命中（例如浏览器自动探测的 /.well-known/*、/favicon.ico），
        // 属于预期行为，按 DEBUG 级别记录并直接返回 404，避免污染 error 日志。
        log.debug("静态资源未找到: {}", e.getResourcePath());
        return Result.error(404, "资源不存在");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        // 没有匹配的 Controller 路由时的 404 分支，通常是前端刷新页面或请求路径错误。
        log.warn("请求路径不存在: {} {}", e.getHttpMethod(), e.getRequestURL());
        return Result.error(404, "请求路径不存在");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统异常");
    }
}