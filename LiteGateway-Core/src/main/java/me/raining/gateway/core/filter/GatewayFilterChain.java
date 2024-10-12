package me.raining.gateway.core.filter;

import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.core.context.GatewayContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author raining
 * @version 1.0.0
 * @description 过滤器链，用于存储实现的过滤器的信息 并且按照顺序进行执行
 */
@Slf4j
public class GatewayFilterChain {

    private final List<Filter> filters = new ArrayList<>();


    public GatewayFilterChain addFilter(Filter filter){
        filters.add(filter);
        return this;
    }
    public GatewayFilterChain addFilterList(List<Filter> filter){
        filters.addAll(filter);
        return this;
    }


    /**
     * 执行过滤器处理流程
     * @param ctx 网关上下文
     */
    public GatewayContext doFilter(GatewayContext ctx) throws Exception {
        if(filters.isEmpty()){
            return ctx;
        }
        try {
            for(Filter fl: filters){
                fl.doFilter(ctx);
                if (ctx.isTerminated()){
                    break;
                }
            }
        }catch (Exception e){
            log.error("执行过滤器发生异常,异常信息：{}",e.getMessage());
            throw e;
        }
        return ctx;
    }
}
