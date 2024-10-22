package me.raining.gateway.core.filter.auth;

import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.common.enums.ResponseCode;
import me.raining.gateway.common.exception.ResponseException;
import me.raining.gateway.core.context.GatewayContext;
import me.raining.gateway.core.filter.Filter;
import me.raining.gateway.core.filter.FilterAspect;
import org.apache.commons.lang3.StringUtils;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;

import static me.raining.gateway.common.constant.FilterConst.*;

/**
 * @author raining
 * @version 1.0.0
 * @description 用户JWT鉴权过滤器
 */
@Slf4j
@FilterAspect(id = AUTH_FILTER_ID,
        name = AUTH_FILTER_NAME,
        order = AUTH_FILTER_ORDER)
public class AuthFilter implements Filter {
    /**
     * 加密密钥
     */
    private static final String SECRET_KEY = "szc";

    /**
     * cookie键  从对应的cookie中获取到这个键 存储的就是我们的token信息
     */
    private static final String COOKIE_NAME = "lite-gateway-jwt";

    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        //检查是否需要用户鉴权
        if (ctx.getRule().getFilterConfig(AUTH_FILTER_ID) == null) {
            return;
        }

        String token = ctx.getRequest().getCookie(COOKIE_NAME).value();
        if (StringUtils.isBlank(token)) {
            throw new ResponseException(ResponseCode.UNAUTHORIZED);
        }

        try {
            //解析用户id
            long userId = parseUserId(token);
            //把用户id传给下游
            ctx.getRequest().setUserId(userId);
        } catch (Exception e) {
            throw new ResponseException(ResponseCode.UNAUTHORIZED);
        }

    }

    /**
     * 根据token解析用户id
     *
     * @param token
     * @return
     */
    private long parseUserId(String token) {
        Jwt jwt = Jwts.parser().setSigningKey(SECRET_KEY).parse(token);
        return Long.parseLong(((DefaultClaims) jwt.getBody()).getSubject());
    }
}
