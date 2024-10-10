package me.raining.gateway.core.helper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import me.raining.gateway.common.config.*;
import me.raining.gateway.common.constant.BasicConst;
import me.raining.gateway.common.constant.GatewayConst;
import me.raining.gateway.common.exception.ResponseException;
import me.raining.gateway.core.context.GatewayContext;
import me.raining.gateway.core.request.DefaultHttpGatewayRequest;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static me.raining.gateway.common.enums.ResponseCode.PATH_NO_MATCHED;

/**
 * @author raining
 * @version 1.0.0
 * @description 构建网关上下文 GatewayContext 的辅助类，输入是netty的request和ctx
 */
public class RequestHelper {
    public static GatewayContext doContext(FullHttpRequest request, ChannelHandlerContext ctx) {

        //	构建请求对象 DefaultHttpGatewayRequest
        DefaultHttpGatewayRequest gateWayRequest = doRequest(request, ctx);

        //	根据请求对象里的uniqueId，获取资源服务信息(也就是服务定义信息)
        //通过每个请求的uniqueId，获取到serviceId，再根据serviceId+path获取到rule集合（针对当前服务）
        ServiceDefinition serviceDefinition =
                DynamicConfigManager.getInstance().getServiceDefinition(gateWayRequest.getUniqueId());

        //根据请求对象获取规则
        Rule rule = getRule(gateWayRequest, serviceDefinition.getServiceId());

        //	构建我们而定GateWayContext对象
        GatewayContext gatewayContext = new GatewayContext(serviceDefinition.getProtocol(), ctx,
                HttpUtil.isKeepAlive(request), gateWayRequest, rule, 0);

        return gatewayContext;
    }

    /**
     * 构建Request请求对象
     */
    private static DefaultHttpGatewayRequest doRequest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {

        HttpHeaders headers = fullHttpRequest.headers();
        //	从header头获取必须要传入的关键属性 uniqueId
        String uniqueId = headers.get(GatewayConst.UNIQUE_ID);

        String host = headers.get(HttpHeaderNames.HOST);
        HttpMethod method = fullHttpRequest.method();
        String uri = fullHttpRequest.uri();
        String clientIp = getClientIp(ctx, fullHttpRequest);
        String contentType = HttpUtil.getMimeType(fullHttpRequest) == null ? null :
                HttpUtil.getMimeType(fullHttpRequest).toString();
        Charset charset = HttpUtil.getCharset(fullHttpRequest, StandardCharsets.UTF_8);

        //只解析基本的，最后一个参数是fullHttpRequest，像body这种需要的时候再解析
        DefaultHttpGatewayRequest gatewayRequest = new DefaultHttpGatewayRequest(uniqueId, charset, clientIp, host, uri, method,
                contentType, headers, fullHttpRequest);

        return gatewayRequest;
    }

    /**
     * 获取客户端ip
     */
    private static String getClientIp(ChannelHandlerContext ctx, FullHttpRequest request) {
        String xForwardedValue = request.headers().get(BasicConst.HTTP_FORWARD_SEPARATOR);

        String clientIp = null;
        if (StringUtils.isNotEmpty(xForwardedValue)) {
            List<String> values = Arrays.asList(xForwardedValue.split(", "));
            if (values.size() >= 1 && StringUtils.isNotBlank(values.get(0))) {
                clientIp = values.get(0);
            }
        }
        if (clientIp == null) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            clientIp = inetSocketAddress.getAddress().getHostAddress();
        }
        return clientIp;
    }

    /**
     * 根据请求对象获取Rule对象
     *
     * @param gateWayRequest 请求对象
     */
    private static Rule getRule(DefaultHttpGatewayRequest gateWayRequest, String serviceId) {
        //先按照serviceId+path找rule
        String key = serviceId + "." + gateWayRequest.getPath();
        Rule rule = DynamicConfigManager.getInstance().getRuleByPath(key);

        if (rule != null) {
            return rule;
        }
        /**
         * .filter(r -> gateWayRequest.getPath().startsWith(r.getPrefix()))
         * 在流上应用了一个过滤器操作，它会遍历流中的每个元素（这里的每个元素都是一个规则对象r）。
         * 对于每个规则对象r，它检查gateWayRequest对象的路径是否以规则对象的前缀r.getPrefix()开始。
         * 只有满足这个条件的规则才会被保留在流中。
         */
        //找不到的话，根据serviceId拿到rule集合，查找第一个匹配请求路径前缀的规则
        return DynamicConfigManager.getInstance().getRuleByServiceId(serviceId).stream()
                .filter(r -> gateWayRequest.getPath().startsWith(r.getPrefix())).findAny()
                .orElseThrow(() -> new ResponseException(PATH_NO_MATCHED));
    }

}
