package me.raining.gateway.dubbo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author raining
 * @version 1.0.0
 * @description 启动类
 */
@SpringBootApplication
@EnableDubbo
public class DubboWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboWebApplication.class, args);
    }
}