package me.raining.gateway.core.filter;

import me.raining.gateway.core.context.GatewayContext;

/**
 * @author raining
 * @version 1.0.0
 * @description 过滤器链工厂 用于生成过滤器链
 */
public interface FilterChainFactory {

    /**
     * 构建过滤器链条
     * @param ctx 网关上下文
     */
    GatewayFilterChain buildFilterChain(GatewayContext ctx) throws Exception;

    /**
     * 通过过滤器ID获取过滤器
     * @param filterId 过滤器ID
     */
    <T> T getFilter(String filterId) throws Exception;

}
