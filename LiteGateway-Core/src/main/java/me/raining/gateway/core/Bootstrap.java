package me.raining.gateway.core;

import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.common.config.DynamicConfigManager;
import me.raining.gateway.config.center.api.ConfigCenter;

import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;

/**
 * @author raining
 * @version 1.0.0
 * @description 网关启动类
 */
@Slf4j
public class Bootstrap {
    public static void main(String[] args) {
        //加载网关核心静态配置
        Config config = ConfigLoader.getInstance().load(args);
        System.out.println(config.getPort());

        //配置中心相关
        //基于SPI机制，加载配置中心管理器
        ServiceLoader<ConfigCenter> serviceLoader = ServiceLoader.load(ConfigCenter.class);
        //默认使用第一个实现
        final ConfigCenter configCenter = serviceLoader.findFirst().orElseThrow(() -> {
            log.error("not found ConfigCenter impl");
            return new RuntimeException("not found ConfigCenter impl");
        });

        //初始化配置中心(里面会初始化rules)
        configCenter.init(config.getRegistryAddress(), config.getEnv());
        //监听配置的新增、修改、删除
        //一旦有变化，就更新规则rules集合，存入DynamicConfigManager
        //测试的时候，需要在nacos下创建配置：
        //Data Id: api-gateway；Group: dev
        //json文件：
        /**
         * {
         *   "rules": [
         *     {
         *       "id": "user-private",
         *       "name": "user-private",
         *       "paths": [
         *         "/user/private/user-info"
         *       ],
         *       "prefix": "/user/private",
         *       "protocol": "http",
         *       "serviceId": "backend-user-server",
         *       "filterConfigs": [
         *         {
         *           "config": {
         *             "load_balance": "Random"
         *           },
         *           "id": "load_balance_filter"
         *         },
         *         {
         *           "id":"auth_filter"
         *         }
         *       ]
         *     },
         *     {
         *       "id": "user",
         *       "name": "user",
         *       "paths": [
         *         "/user/login"
         *       ],
         *       "prefix": "/user",
         *       "protocol": "http",
         *       "serviceId": "backend-user-server",
         *       "filterConfigs": [
         *         {
         *           "config": {
         *             "load_balance": "Random"
         *           },
         *           "id": "load_balance_filter"
         *         }
         *       ]
         *     },
         *     {
         *       "id": "http-server",
         *       "name": "http-server",
         *       "paths": [
         *         "/http-server/ping"
         *       ],
         *       "prefix": "/http-server",
         *       "protocol": "http",
         *       "retryConfig": {
         *         "times": 3
         *       },
         *       "serviceId": "backend-http-server",
         *       "filterConfigs": [
         *         {
         *           "config": {
         *             "load_balance": "RoundRobin"
         *           },
         *           "id": "load_balance_filter"
         *         },
         *         {
         *           "id": "auth_filter"
         *         }
         *       ]
         *     }
         *   ]
         * }
         */
        configCenter.subscribeRulesChange(rules -> DynamicConfigManager.getInstance()
                .putAllRule(rules));

    }
}
