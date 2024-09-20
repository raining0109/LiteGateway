package me.raining.gateway.core;

import lombok.extern.slf4j.Slf4j;

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
    }
}
