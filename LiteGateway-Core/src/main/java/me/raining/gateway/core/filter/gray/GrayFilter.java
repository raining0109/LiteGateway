package me.raining.gateway.core.filter.gray;

import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.core.context.GatewayContext;
import me.raining.gateway.core.filter.Filter;
import me.raining.gateway.core.filter.FilterAspect;

import static me.raining.gateway.common.constant.FilterConst.*;

/**
 * @author raining
 * @version 1.0.0
 * @description 灰度发布过滤器
 */
@Slf4j
@FilterAspect(id = GRAY_FILTER_ID,
        name = GRAY_FILTER_NAME,
        order = GRAY_FILTER_ORDER)
public class GrayFilter implements Filter {
    public static final String GRAY = "true";
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        //测试灰度功能待时候使用  我们可以手动指定其是否为灰度流量
        String gray = ctx.getRequest().getHeaders().get("gray");
        if (GRAY.equals(gray)) {
            ctx.setGray(true);
            return;
        }
        //选取部分的灰度用户
        String clientIp = ctx.getRequest().getClientIp();
        //等价于对1024取模
        int res = clientIp.hashCode() & (1024 - 1);
        if (res == 1) {
            //1024分之一的概率
            ctx.setGray(true);
        }

    }
}
