package me.raining.gateway.core.filter.monitor;

import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.core.context.GatewayContext;
import me.raining.gateway.core.filter.Filter;
import me.raining.gateway.core.filter.FilterAspect;

import static me.raining.gateway.common.constant.FilterConst.*;

/**
 * @author raining
 * @version 1.0.0
 * @description Prometheus入口过滤器
 */
@Slf4j
@FilterAspect(id = MONITOR_FILTER_ID,
        name = MONITOR_FILTER_NAME,
        order = MONITOR_FILTER_ORDER)
public class MonitorFilter implements Filter {
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        ctx.setTimerSample(Timer.start());
    }
}