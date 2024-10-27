package me.raining.gateway.core.context;

import io.micrometer.core.instrument.Timer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.Getter;
import lombok.Setter;
import me.raining.gateway.common.config.Rule;
import me.raining.gateway.common.utils.AssertUtil;
import me.raining.gateway.core.request.DefaultHttpGatewayRequest;
import me.raining.gateway.core.response.LiteGatewayResponse;

/**
 * @author raining
 * @version 1.0.0
 * @description 网关上下文，包含了请求以及请求响应，并且包含了一系列的规则
 */
public class GatewayContext extends BaseContext {

    private DefaultHttpGatewayRequest request;

    private LiteGatewayResponse response;

    private Rule rule;

    private int currentRetryTimes;

    @Setter
    @Getter
    private boolean gray;

    /**
     * 记录应用程序中的方法调用或服务请求所花费的时间
     */
    @Setter
    @Getter
    private Timer.Sample timerSample;

    /**
     * 构造函数
     */
    public GatewayContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive, DefaultHttpGatewayRequest request, Rule rule, int currentRetryTimes) {
        super(protocol, nettyCtx, keepAlive);
        this.request = request;
        this.rule = rule;
        this.currentRetryTimes = currentRetryTimes;
    }

    public static class Builder {
        private String protocol;
        private ChannelHandlerContext nettyCtx;
        private boolean keepAlive;
        private DefaultHttpGatewayRequest request;
        private Rule rule;

        private Builder() {

        }

        public Builder setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder setNettyCtx(ChannelHandlerContext nettyCtx) {
            this.nettyCtx = nettyCtx;
            return this;
        }

        public Builder setKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
            return this;
        }

        public Builder setRequest(DefaultHttpGatewayRequest request) {
            this.request = request;
            return this;
        }

        public Builder setRule(Rule rule) {
            this.rule = rule;
            return this;
        }

        public GatewayContext build() {
            AssertUtil.notNull(protocol, "protocol 不能为空");

            AssertUtil.notNull(nettyCtx, "nettyCtx 不能为空");

            AssertUtil.notNull(request, "request 不能为空");

            AssertUtil.notNull(rule, "rule 不能为空");
            return new GatewayContext(protocol, nettyCtx, keepAlive, request, rule, 0);
        }
    }

    @Override
    public DefaultHttpGatewayRequest getRequest() {
        return request;
    }

    public void setRequest(DefaultHttpGatewayRequest request) {
        this.request = request;
    }

    @Override
    public LiteGatewayResponse getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = (LiteGatewayResponse) response;
    }

    @Override
    public Rule getRule() {
        return rule;
    }

    @Override
    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public int getCurrentRetryTimes() {
        return currentRetryTimes;
    }

    public void setCurrentRetryTimes(int currentRetryTimes) {
        this.currentRetryTimes = currentRetryTimes;
    }

    public String getUniqueId() {
        return request.getUniqueId();
    }

    /**
     * 重写父类释放资源方法，用于正在释放资源
     * release() 方法通常减少对象的引用计数。当计数达到零时，资源被释放。
     */
    @Override
    public void releaseRequest() {
        if (requestReleased.compareAndSet(false, true)) {
            ReferenceCountUtil.release(request.getFullHttpRequest());
        }
    }

    /**
     * 获取原始的请求对象
     */
    public DefaultHttpGatewayRequest getOriginRequest() {
        return request;
    }

    /**
     * 根据过滤器ID获取对应的过滤器配置信息
     */
    public Rule.FilterConfig getFilterConfig(String filterId){
        return  rule.getFilterConfig(filterId);
    }
}
