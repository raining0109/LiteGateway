package me.raining.gateway.client.api;

/**
 * @author raining
 * @version 1.0.0
 * @description 协议枚举类
 */
public enum ApiProtocol {
    HTTP("http", "http协议"),
    DUBBO("dubbo", "dubbo协议");

    private String protocol;

    private String desc;

    ApiProtocol(String protocol, String desc) {
        this.protocol = protocol;
        this.desc = desc;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getDesc() {
        return desc;
    }
}