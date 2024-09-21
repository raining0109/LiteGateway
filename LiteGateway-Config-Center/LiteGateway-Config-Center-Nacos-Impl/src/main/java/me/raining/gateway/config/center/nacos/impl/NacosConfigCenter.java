package me.raining.gateway.config.center.nacos.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.common.config.Rule;
import me.raining.gateway.config.center.api.ConfigCenter;
import me.raining.gateway.config.center.api.RulesChangeListener;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author raining
 * @version 1.0.0
 * @description 配置中心 API nacos实现
 * 参考文档：
 * https://nacos.io/docs/latest/manual/user/java-sdk/usage/#32-%E7%9B%91%E5%90%AC%E9%85%8D%E7%BD%AE
 */
@Slf4j
public class NacosConfigCenter implements ConfigCenter {

    /**
     * 需要拉取的服务配置的DATA_ID 要求自定义
     */
    private static final String DATA_ID = "api-gateway";

    /**
     * 服务端地址
     */
    private String serverAddr;

    /**
     * 环境
     */
    private String env;

    /**
     * Nacos提供的与配置中心进行交互的接口
     */
    private ConfigService configService;

    @Override
    public void init(String serverAddr, String env) {
        this.serverAddr = serverAddr;
        this.env = env;
        try {
            this.configService = NacosFactory.createConfigService(serverAddr);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribeRulesChange(RulesChangeListener listener) {
        try {
            //1.初始化通知 DATA_ID是自己定义的 返回值就是一个json
            String configJson = configService.getConfig(DATA_ID, env, 5000);
            //configJson : {"rules":[{}, {}]}
            log.info("config from nacos: {}", configJson);
            //解析出Rule对象
            List<Rule> rules = JSON.parseObject(configJson).getJSONArray("rules").toJavaList(Rule.class);
            //调用我们的监听器 参数就是我们拿到的rules
            listener.onRulesChange(rules);//初始化

            //2.监听变化
            configService.addListener(DATA_ID, env, new Listener() {
                //是否使用额外线程执行
                @Override
                public Executor getExecutor() {
                    return null;
                }

                //有变化时，触发的方法
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("config from nacos: {}", configInfo);
                    List<Rule> rules = JSON.parseObject(configInfo).getJSONArray("rules").toJavaList(Rule.class);
                    listener.onRulesChange(rules);
                }
            });
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
