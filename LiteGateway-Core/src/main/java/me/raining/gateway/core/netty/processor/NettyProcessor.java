package me.raining.gateway.core.netty.processor;

import me.raining.gateway.core.context.HttpRequestWrapper;

/**
 * @author raining
 * @version 1.0.0
 * @description
 */
public interface NettyProcessor {

    void process(HttpRequestWrapper wrapper);

    void  start();

    void shutDown();
}
