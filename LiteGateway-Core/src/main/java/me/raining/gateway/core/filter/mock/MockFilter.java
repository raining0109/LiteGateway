package me.raining.gateway.core.filter.mock;

import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.common.config.Rule;
import me.raining.gateway.common.utils.JSONUtil;
import me.raining.gateway.core.context.GatewayContext;
import me.raining.gateway.core.filter.Filter;
import me.raining.gateway.core.filter.FilterAspect;
import me.raining.gateway.core.helper.ResponseHelper;
import me.raining.gateway.core.response.LiteGatewayResponse;

import java.util.Map;

import static me.raining.gateway.common.constant.FilterConst.*;

/**
 * @author raining
 * @version 1.0.0
 * @description 前端mock过滤器
 */
@Slf4j
@FilterAspect(id=MOCK_FILTER_ID,
        name = MOCK_FILTER_NAME,
        order = MOCK_FILTER_ORDER)
public class MockFilter implements Filter {
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        //如果没有配置mock 直接返回
        Rule.FilterConfig config = ctx.getRule().getFilterConfig(MOCK_FILTER_ID);
        if (config == null) {
            return;
        }
        //解析
        Map<String, String> map = JSONUtil.parse(config.getConfig(), Map.class);
        String value = map.get(ctx.getRequest().getMethod().name() + " " + ctx.getRequest().getPath());
        //不为空说明命中了mock规则
        if (value != null) {
            ctx.setResponse(LiteGatewayResponse.buildGatewayResponse(value));
            ctx.written();
            //数据写回
            ResponseHelper.writeResponse(ctx);
            log.info("mock {} {} {}", ctx.getRequest().getMethod(), ctx.getRequest().getPath(), value);
            ctx.terminated();
        }
    }
}
