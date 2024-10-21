package me.raining.gateway.core.filter.flow;

import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.common.config.Rule;
import me.raining.gateway.core.context.GatewayContext;
import me.raining.gateway.core.filter.Filter;
import me.raining.gateway.core.filter.FilterAspect;

import java.util.Iterator;
import java.util.Set;

import static me.raining.gateway.common.constant.FilterConst.*;

/**
 * @author raining
 * @version 1.0.0
 * @description 流量控制过滤器 实现令牌桶和漏桶算法
 */
@Slf4j
@FilterAspect(id=FLOW_CTL_FILTER_ID,
        name = FLOW_CTL_FILTER_NAME,
        order = FLOW_CTL_FILTER_ORDER)
public class FlowControlFilter implements Filter {
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        Rule rule = ctx.getRule();
        if(rule != null){
            //获取流控规则
            Set<Rule.FlowControlConfig> flowControlConfigs = rule.getFlowControlConfigs();
            Iterator iterator = flowControlConfigs.iterator();
            Rule.FlowControlConfig flowControlConfig;
            while (iterator.hasNext()){
                GatewayFlowControlRule flowControlRule = null;
                flowControlConfig = (Rule.FlowControlConfig)iterator.next();
                if(flowControlConfig == null){
                    continue;
                }
                String path = ctx.getRequest().getPath();
                if(flowControlConfig.getType().equalsIgnoreCase(FLOW_CTL_TYPE_PATH)
                        && path.equals(flowControlConfig.getValue())){
                    flowControlRule = FlowControlByPathRule.getInstance(rule.getServiceId(),path);
                }else if(flowControlConfig.getType().equalsIgnoreCase(FLOW_CTL_TYPE_SERVICE)){
                    //TODO 后续实现基于服务的流控
                }
                if(flowControlRule != null){
                    //执行流量控制
                    flowControlRule.doFlowControlFilter(flowControlConfig,rule.getServiceId());
                }
            }
        }
    }
}
