package me.raining.gateway.core;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.common.config.DynamicConfigManager;
import me.raining.gateway.common.config.ServiceDefinition;
import me.raining.gateway.common.config.ServiceInstance;
import me.raining.gateway.common.utils.NetUtils;
import me.raining.gateway.common.utils.TimeUtil;
import me.raining.gateway.config.center.api.ConfigCenter;
import me.raining.gateway.register.center.api.RegisterCenter;
import me.raining.gateway.register.center.api.RegisterCenterListener;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static me.raining.gateway.common.constant.BasicConst.COLON_SEPARATOR;

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

        //启动容器
        Container container = new Container(config);
        container.start();

        //连接注册中心，将注册中心的实例加载到本地，并订阅注册中心变更
        final RegisterCenter registerCenter = registerAndSubscribe(config);

        //服务优雅关机
        //收到kill信号时调用
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                registerCenter.deregister(buildGatewayServiceDefinition(config),
                        buildGatewayServiceInstance(config));
                container.shutdown();
            }
        });

    }

    private static RegisterCenter registerAndSubscribe(Config config) {
        ServiceLoader<RegisterCenter> serviceLoader = ServiceLoader.load(RegisterCenter.class);
        final RegisterCenter registerCenter = serviceLoader.findFirst().orElseThrow(() -> {
            log.error("not found RegisterCenter impl");
            return new RuntimeException("not found RegisterCenter impl");
        });
        registerCenter.init(config.getRegistryAddress(), config.getEnv());

        //构造网关服务定义和服务实例
        ServiceDefinition serviceDefinition = buildGatewayServiceDefinition(config);
        ServiceInstance serviceInstance = buildGatewayServiceInstance(config);

        //注册
        registerCenter.register(serviceDefinition, serviceInstance);

        //订阅
        registerCenter.subscribeAllServices(new RegisterCenterListener() {
            @Override
            public void onChange(ServiceDefinition serviceDefinition, Set<ServiceInstance> serviceInstanceSet) {
                log.info("refresh service and instance: {} {}", serviceDefinition.getUniqueId(),
                        JSON.toJSON(serviceInstanceSet));
                DynamicConfigManager manager = DynamicConfigManager.getInstance();
                //将这次变更事件影响之后的服务实例再次添加到对应的服务实例集合
                manager.addServiceInstance(serviceDefinition.getUniqueId(), serviceInstanceSet);
                //修改发生对应的服务定义
                manager.putServiceDefinition(serviceDefinition.getUniqueId(), serviceDefinition);
            }
        });
        return registerCenter;
    }

    private static ServiceInstance buildGatewayServiceInstance(Config config) {
        String localIp = NetUtils.getLocalIp();
        int port = config.getPort();
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceInstanceId(localIp + COLON_SEPARATOR + port);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setRegisterTime(TimeUtil.currentTimeMillis());
        return serviceInstance;
    }

    private static ServiceDefinition buildGatewayServiceDefinition(Config config) {
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setInvokerMap(Map.of());
        serviceDefinition.setUniqueId(config.getApplicationName());
        serviceDefinition.setServiceId(config.getApplicationName());
        serviceDefinition.setEnvType(config.getEnv());
        return serviceDefinition;
    }
}
