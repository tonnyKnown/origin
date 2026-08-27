package com.example.aiinterview.common.filter;

import com.example.aiinterview.common.ApiCode;
import com.example.aiinterview.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 可选的接口鉴权过滤器。
 * 当 app.auth.token 为空时完全不拦截（本地开发默认）；配置后要求所有请求携带
 * 正确的 X-API-Token 请求头，否则返回 401。
 * 注意：开启后前端调用需自行在请求中带上该请求头。
 */
@Slf4j
@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final String TOKEN_HEADER = "X-API-Token";

    private final String expectedToken;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthFilter(@Value("${app.auth.token:}") String token) {
        this.expectedToken = token;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!StringUtils.hasText(expectedToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        String provided = request.getHeader(TOKEN_HEADER);
        if (expectedToken.equals(provided)) {
            filterChain.doFilter(request, response);
            return;
        }

        log.warn("接口鉴权失败: path={}, remoteAddr={}", request.getRequestURI(), request.getRemoteAddr());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(
                Result.error(ApiCode.UNAUTHORIZED, "未授权：缺少或错误的 X-API-Token")));
    }
}
