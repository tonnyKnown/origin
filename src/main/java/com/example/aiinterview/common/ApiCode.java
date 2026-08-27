package com.example.aiinterview.common;

/**
 * 统一错误码常量。HTTP 状态码与响应体 Result.code 保持一致。
 */
public final class ApiCode {

    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;
    public static final int INTERNAL_ERROR = 500;
    public static final int UPSTREAM_ERROR = 502;

    private ApiCode() {
    }
}
