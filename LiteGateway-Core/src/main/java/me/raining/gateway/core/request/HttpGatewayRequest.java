package me.raining.gateway.core.request;

import org.asynchttpclient.Request;
import org.asynchttpclient.cookie.Cookie;

/**
 * @author raining
 * @version 1.0.0
 * @description HTTP网关请求抽象类
 */
public interface HttpGatewayRequest extends LiteGatewayRequest{

    /**
     * 修改请求时的主机名或者域名
     */
    void setRequestHost(String host);

    /**
     * 获取请求时的主机名或者域名
     */
    String getRequestHost();

    /**
     * 设置请求路径
     */
    void setRequestPath(String path);

    /**
     * 获取请求路径
     */
    String  getRequestPath();

    /**
     * 添加请求头信息
     */
    void addHeader(CharSequence name,String value);

    /**
     * 设置请求头信息
     */
    void setHeader(CharSequence name,String value);

    /**
     * Get 请求参数
     */
    void addQueryParam(String name ,String value);

    /**
     * POST 请求参数
     */
    void addFormParam(String name ,String value);

    /**
     * 添加或者替换Cookie
     */
    void addOrReplaceCookie(Cookie cookie);

    /**
     * 设置请求超时时间
     */
    void setRequestTimeout(int requestTimeout);

    /**
     * 获取最终的请求路径
     */
    String getFinalUrl();

    /**
     * 构造最终的请求对象
     */
    Request build();
}
