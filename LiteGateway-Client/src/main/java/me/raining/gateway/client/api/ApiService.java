package me.raining.gateway.client.api;

/**
 * @author raining
 * @version 1.0.0
 * @description 服务定义
 */

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiService {

    String serviceId();

    String version() default "1.0.0";

    ApiProtocol protocol();

    String patternPath();
}
