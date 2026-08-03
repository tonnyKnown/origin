package com.example.aiinterview.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理浏览器/爬虫/DevTools 主动探测的路由，
 * 避免 NoResourceFoundException 进入全局异常处理器污染日志。
 */
@RestController
@RequestMapping
public class WellnessController {

    @GetMapping("/.well-known/appspecific/com.chrome.devtools.json")
    public ResponseEntity<Void> chromeDevToolsAppSpecific() {
        // Chrome DevTools 会自动探测该路径，返回 204 表示"没有配置"即可，
        // 比走 404 链路更安静，也不会触发全局异常处理器。
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/.well-known/**")
    public ResponseEntity<Void> anyWellKnown() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        // 未提供 favicon 时避免触发 NoResourceFoundException；
        // 如果后续需要 favicon，放 static/favicon.ico 并删掉本接口即可。
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/healthz")
    public ResponseEntity<String> healthz() {
        // 轻量健康检查接口，用于反向代理、容器探针和自动化探测。
        return ResponseEntity.ok("ok");
    }
}
