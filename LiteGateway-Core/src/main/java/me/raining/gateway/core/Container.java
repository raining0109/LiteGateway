package me.raining.gateway.core;

import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.core.netty.NettyHttpClient;
import me.raining.gateway.core.netty.NettyHttpServer;
import me.raining.gateway.core.netty.processor.NettyCoreProcessor;
import me.raining.gateway.core.netty.processor.NettyProcessor;

/**
 * @author raining
 * @version 1.0.0
 * @description 网关服务类，统一管理NettyHttpServer、NettyHttpClient和nettyProcessor
 */
@Slf4j
public class Container implements LifeCycle {
    private final Config config;

    private NettyHttpServer nettyHttpServer;

    private NettyHttpClient nettyHttpClient;

    private NettyProcessor nettyProcessor;

    public Container(Config config) {
        this.config = config;
        init();
    }

    @Override
    public void init() {
        this.nettyProcessor = new NettyCoreProcessor();

        this.nettyHttpServer = new NettyHttpServer(config, nettyProcessor);

        this.nettyHttpClient = new NettyHttpClient(config,
                nettyHttpServer.getEventLoopGroupWorker());
    }

    @Override
    public void start() {
        nettyHttpServer.start();;
        nettyHttpClient.start();
        log.info("api gateway started!");
    }

    @Override
    public void shutdown() {
        nettyHttpServer.shutdown();
        nettyHttpClient.shutdown();
    }
}