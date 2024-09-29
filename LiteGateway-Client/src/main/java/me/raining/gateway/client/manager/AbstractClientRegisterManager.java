package me.raining.gateway.client.manager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.client.api.ApiProperties;
import me.raining.gateway.register.center.api.RegisterCenter;
import me.raining.gateway.common.config.ServiceDefinition;
import me.raining.gateway.common.config.ServiceInstance;
import java.util.ServiceLoader;

/**
 * @author raining
 * @version 1.0.0
 * @description 抽象注册中心管理类
 */
@Slf4j
public abstract class AbstractClientRegisterManager {

    @Getter
    private ApiProperties apiProperties;

    private RegisterCenter registerCenter;

    protected AbstractClientRegisterManager(ApiProperties apiProperties) {
        this.apiProperties = apiProperties;

        //初始化注册中心对象
        ServiceLoader<RegisterCenter> serviceLoader = ServiceLoader.load(RegisterCenter.class);
        //获取注册中心实现 如果没有就报错
        registerCenter = serviceLoader.findFirst().orElseThrow(() -> {
            log.error("not found RegisterCenter impl");
            return new RuntimeException("not found RegisterCenter impl");
        });
        //注册中心初始化代码
        registerCenter.init(apiProperties.getRegisterAddress(), apiProperties.getEnv());
    }

    /**
     * 提供给子类让子类进行服务注册
     * @param serviceDefinition  服务定义
     * @param serviceInstance  服务实例
     */
    protected void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        //直接调用注册中心的api
        registerCenter.register(serviceDefinition, serviceInstance);
    }
}
