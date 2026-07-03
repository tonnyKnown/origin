package com.example.aiinterview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiInterviewApplication {

    private static final Logger log = LoggerFactory.getLogger(AiInterviewApplication.class);

    public static void main(String[] args) {
        log.info("项目启动中0.0");
        SpringApplication.run(AiInterviewApplication.class, args);
        log.info("项目启动完成0.0");
    }
}
