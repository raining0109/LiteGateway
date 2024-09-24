package me.raining.gateway.register.center.api;

import me.raining.gateway.common.config.ServiceDefinition;
import me.raining.gateway.common.config.ServiceInstance;

import java.util.Set;

/**
 * @author raining
 * @version 1.0.0
 * @description 注册中心的监听器 用来监听注册中心的变化
 * 这里的逻辑和配置中心一样
 */
public interface RegisterCenterListener {

    void onChange(ServiceDefinition serviceDefinition,
                  Set<ServiceInstance> serviceInstanceSet);
}
