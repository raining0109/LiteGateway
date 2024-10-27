package me.raining.gateway.core.filter.monitor;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.core.ConfigLoader;
import me.raining.gateway.core.context.GatewayContext;
import me.raining.gateway.core.filter.Filter;
import me.raining.gateway.core.filter.FilterAspect;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static me.raining.gateway.common.constant.FilterConst.*;

/**
 * @author raining
 * @version 1.0.0
 * @description 集成Prometheus监控， Prometheus出口过滤器
 */
@Slf4j
@FilterAspect(id = MONITOR_END_FILTER_ID, name = MONITOR_END_FILTER_NAME, order = MONITOR_END_FILTER_ORDER)
public class MonitorEndFilter implements Filter {
    /**
     * Prometheus监控的注册表实例，用于存储和管理监控指标。
     */
    private final PrometheusMeterRegistry prometheusMeterRegistry;

    /**
     * 类构造器，初始化Prometheus监控和HTTP服务器。
     */
    public MonitorEndFilter() {
        // 创建PrometheusMeterRegistry实例，使用默认配置
        this.prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        try {
            // 创建一个新的HTTP服务器监听配置中指定的端口
            HttpServer server = HttpServer.create(new InetSocketAddress(ConfigLoader.getConfig().getPrometheusPort())
                    , 0);

            // 配置HTTP服务器处理路径"/prometheus"的请求，用于暴露监控数据
            server.createContext("/prometheus", exchange -> {
                // 获取Prometheus格式的监控数据
                String scrape = prometheusMeterRegistry.scrape();

                // 发送响应头，状态码200，内容长度为指标数据的字节长度
                exchange.sendResponseHeaders(200, scrape.getBytes().length);
                // 发送响应体，即指标数据
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(scrape.getBytes());
                }
            });

            // 启动HTTP服务器
            new Thread(server::start).start();

        } catch (IOException exception) {
            // 记录启动HTTP服务器失败的日志，并抛出运行时异常
            log.error("prometheus http server start error", exception);
            throw new RuntimeException(exception);
        }
        // 记录启动HTTP服务器成功的日志
        log.info("prometheus http server start successful, port:{}", ConfigLoader.getConfig().getPrometheusPort());
    }

    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        // 创建一个计时器对象，用于测量操作执行时间
        Timer timer = prometheusMeterRegistry.timer("gateway_request",
                // 以下是多个标签用于标识和分类度量数据
                // "uniqueId" 标签，用于唯一标识请求，可能是请求的唯一标识符
                "uniqueId", ctx.getUniqueId(),
                // "protocol" 标签，表示请求的协议，可能是HTTP或其他协议
                "protocol", ctx.getProtocol(),
                // "path" 标签，表示请求的路径
                "path", ctx.getRequest().getPath());

        // 停止计时器，记录操作的执行时间
        ctx.getTimerSample().stop(timer);
    }

}
