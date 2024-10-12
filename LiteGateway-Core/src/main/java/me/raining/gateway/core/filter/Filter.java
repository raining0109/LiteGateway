package me.raining.gateway.core.filter;

import me.raining.gateway.core.context.GatewayContext;

/**
 * @author raining
 * @version 1.0.0
 * @description Filter接口 过滤器顶层接口
 */
public interface Filter {
    void doFilter(GatewayContext ctx) throws  Exception;

    default int getOrder(){
        FilterAspect annotation = this.getClass().getAnnotation(FilterAspect.class);
        if(annotation != null){
            return annotation.order();
        }
        return Integer.MAX_VALUE;
    };
}
