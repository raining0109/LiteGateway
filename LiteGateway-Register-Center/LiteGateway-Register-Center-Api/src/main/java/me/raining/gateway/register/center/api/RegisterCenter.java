package me.raining.gateway.register.center.api;

import me.raining.gateway.common.config.ServiceDefinition;
import me.raining.gateway.common.config.ServiceInstance;

/**
 * @author raining
 * @version 1.0.0
 * @description 注册中心API接口
 * 与配置中心类似的逻辑，但是多了注册和注销
 */
public interface RegisterCenter {

    /**
     * 初始化
     */
    void init(String registerAddress, String env);

    /**
     * 注册
     * @param serviceDefinition 服务定义信息
     * @param serviceInstance 服务实例信息
     */
    void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance);

    /**
     * 注销
     */
    void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance);

    /**
     * 订阅所有服务变更
     */
    void subscribeAllServices(RegisterCenterListener registerCenterListener);
}
