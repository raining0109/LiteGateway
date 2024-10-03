package me.raining.gateway.core;

/**
 * @author raining
 * @version 1.0.0
 * @description 网关组件的生命周期
 * 比如整个网关容器，netty客户端，netty服务端都要实现此接口
 */
public interface LifeCycle {

    /**
     * 初始化
     */
    void init();

    /**
     * 启动
     */
    void start();


    /**
     * 关闭
     */
    void shutdown();
}
