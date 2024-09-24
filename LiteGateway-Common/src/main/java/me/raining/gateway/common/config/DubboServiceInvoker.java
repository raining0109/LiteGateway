package me.raining.gateway.common.config;

/**
 * @author raining
 * @version 1.0.0
 * @description dubbo协议的注册服务调用模型类
 */

import lombok.Data;

@Data
public class DubboServiceInvoker {

    /**
     * 注册中心地址
     */
    private String registerAddress;
    /**
     * 接口全类名
     */
    private String interfaceClass;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 参数名字的集合
     */
    private String[] parameterTypes;
    /**
     * dubbo服务的版本号
     */
    private String version;
}
