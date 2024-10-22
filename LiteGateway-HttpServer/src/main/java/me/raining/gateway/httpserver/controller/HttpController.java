package me.raining.gateway.httpserver.controller;

import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.client.api.ApiInvoker;
import me.raining.gateway.client.api.ApiProperties;
import me.raining.gateway.client.api.ApiProtocol;
import me.raining.gateway.client.api.ApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author raining
 * @version 1.0.0
 * @description HTTP Controller类
 */
@Slf4j
@RestController()
@RequestMapping("/http-server")
@ApiService(serviceId = "backend-http-server", protocol = ApiProtocol.HTTP, patternPath = "/http-server/**")
public class HttpController {

    @Resource
    private ApiProperties apiProperties;

    @ApiInvoker(path = "/http-server/ping")
    @GetMapping("/ping")
    public String ping() {
        log.info("{}", apiProperties);
        try {
            Thread.sleep(200000000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "this is application1";
    }

    @ApiInvoker(path = "/http-server/ping2")
    @GetMapping("/ping2")
    public String ping2() {
        log.info("{}", apiProperties);
        try {
            //Thread.sleep(10000000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "this is ping1";
    }
}