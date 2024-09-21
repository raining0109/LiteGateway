package me.raining.gateway.config.center.api;

/**
 * @author raining
 * @version 1.0.0
 * @description 配置中心接口方法
 */
public interface ConfigCenter {

    /**
     * 初始化配置中心配置
     */
    void init(String serverAddr, String env);

    /**
     * 订阅配置中心配置变更
     */
    void subscribeRulesChange(RulesChangeListener listener);
}
