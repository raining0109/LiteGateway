package me.raining.gateway.core.netty.processor;

import com.lmax.disruptor.dsl.ProducerType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.common.enums.ResponseCode;
import me.raining.gateway.core.Config;
import me.raining.gateway.core.context.HttpRequestWrapper;
import me.raining.gateway.core.disruptor.EventListener;
import me.raining.gateway.core.disruptor.ParallelQueueHandler;
import me.raining.gateway.core.helper.ResponseHelper;

/**
 * @author raining
 * @version 1.0.0
 * @description 使用 Disruptor 提高性能的 Netty 处理器
 * 这个处理器是一个缓存层，通过 Disruptor 来异步处理 HTTP 请求，减轻 Netty 核心处理器的负担。
 */
@Slf4j
public class DisruptorNettyCoreProcessor implements NettyProcessor {

    /**
     * 线程前缀
     */
    private static final String THREAD_NAME_PREFIX = "gateway-queue-";

    private Config config;

    /**
     * Disruptor 只是缓存依然需要使用到 Netty 核心处理器
     */
    private NettyCoreProcessor nettyCoreProcessor;

    /**
     * 处理类
     */
    private ParallelQueueHandler<HttpRequestWrapper> parallelQueueHandler;

    /**
     * 构造方法，初始化 DisruptorNettyCoreProcessor。
     *
     * @param config             配置信息对象。
     * @param nettyCoreProcessor Netty 核心处理器。
     */
    public DisruptorNettyCoreProcessor(Config config, NettyCoreProcessor nettyCoreProcessor) {
        this.config = config;
        this.nettyCoreProcessor = nettyCoreProcessor;

        // 使用 Disruptor 创建并配置处理队列。
        ParallelQueueHandler.Builder<HttpRequestWrapper> builder = new ParallelQueueHandler.Builder<HttpRequestWrapper>()
                .setBufferSize(config.getBufferSize())
                .setThreads(config.getProcessThread())
                .setProducerType(ProducerType.MULTI)
                .setNamePrefix(THREAD_NAME_PREFIX)
                .setWaitStrategy(config.getWaitStrategy());

        // 监听事件处理类
        BatchEventListenerProcessor batchEventListenerProcessor = new BatchEventListenerProcessor();
        builder.setListener(batchEventListenerProcessor);
        this.parallelQueueHandler = builder.build();
    }

    /**
     * 处理 HTTP 请求，将请求添加到 Disruptor 处理队列中。
     *
     * @param wrapper HttpRequestWrapper 包装类。
     */
    @Override
    public void process(HttpRequestWrapper wrapper) {
        this.parallelQueueHandler.add(wrapper);
    }

    /**
     * 监听处理类，处理从 Disruptor 处理队列中取出的事件。
     */
    public class BatchEventListenerProcessor implements EventListener<HttpRequestWrapper> {

        @Override
        public void onEvent(HttpRequestWrapper event) {
            // 使用 Netty 核心处理器处理事件。
            nettyCoreProcessor.process(event);
        }

        @Override
        public void onException(Throwable ex, long sequence, HttpRequestWrapper event) {
            HttpRequest request = event.getRequest();
            ChannelHandlerContext ctx = event.getCtx();

            try {
                log.error("BatchEventListenerProcessor onException 请求写回失败，request:{}, errMsg:{} ", request, ex.getMessage(), ex);

                // 构建响应对象
                FullHttpResponse fullHttpResponse = ResponseHelper.getHttpResponse(ResponseCode.INTERNAL_ERROR);

                if (!HttpUtil.isKeepAlive(request)) {
                    ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
                } else {
                    fullHttpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    ctx.writeAndFlush(fullHttpResponse);
                }
            } catch (Exception e) {
                log.error("BatchEventListenerProcessor onException 请求写回失败，request:{}, errMsg:{} ", request, e.getMessage(), e);
            }
        }
    }

    /**
     * 启动 DisruptorNettyCoreProcessor，启动处理队列。
     */
    @Override
    public void start() {
        parallelQueueHandler.start();
    }

    /**
     * 关闭 DisruptorNettyCoreProcessor，关闭处理队列。
     */
    @Override
    public void shutDown() {
        parallelQueueHandler.shutDown();
    }
}