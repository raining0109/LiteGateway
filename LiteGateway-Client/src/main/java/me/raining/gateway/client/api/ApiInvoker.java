package me.raining.gateway.client.api;

import java.lang.annotation.*;

/**
 * @author raining
 * @version 1.0.0
 * @description 我们规定，必须在服务的方法上强制声明
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiInvoker {
    String path();
}