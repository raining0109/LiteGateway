package me.raining.gateway.core.filter.loadbalance;

import me.raining.gateway.common.config.ServiceInstance;
import me.raining.gateway.core.context.GatewayContext;

/**
 * @author raining
 * @version 1.0.0
 * @description 负载均衡顶级接口
 */
public interface LoadBalanceGatewayRule {

    /**
     * 通过上下文参数获取服务实例
     */
    ServiceInstance choose(GatewayContext ctx, boolean gray);

    /**
     * 通过服务ID拿到对应的服务实例
     */
    ServiceInstance choose(String serviceId, boolean gray);

}
