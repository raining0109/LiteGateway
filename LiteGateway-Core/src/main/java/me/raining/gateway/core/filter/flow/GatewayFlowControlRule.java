package me.raining.gateway.core.filter.flow;

import me.raining.gateway.common.config.Rule;

/**
 * @author raining
 * @version 1.0.0
 * @description 网关流控规则接口
 */
public interface GatewayFlowControlRule {

    /**
     * 执行流控规则过滤器
     */
    void doFlowControlFilter(Rule.FlowControlConfig flowControlConfig, String serviceId);
}
