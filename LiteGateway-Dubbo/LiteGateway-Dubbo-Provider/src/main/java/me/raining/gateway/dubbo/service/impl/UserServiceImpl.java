package me.raining.gateway.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import me.raining.gateway.client.api.ApiInvoker;
import me.raining.gateway.client.api.ApiProtocol;
import me.raining.gateway.client.api.ApiService;
import me.raining.gateway.dubbo.entity.XReq;
import me.raining.gateway.dubbo.service.UserService;
import org.apache.dubbo.config.annotation.Service;

/**
 * @author raining
 * @version 1.0.0
 * @description
 */
@ApiService(serviceId = "backend-dubbo-server", protocol = ApiProtocol.DUBBO,
        patternPath = "/**")
@Service
public class UserServiceImpl implements UserService {
    @Override
    @ApiInvoker(path = "/sayHi")
    public String sayHi(String str) {
        return "hi " + str + " by LiteGateway-Dubbo-Provider";
    }

    @Override
    @ApiInvoker(path = "/insert")
    public String insert(XReq req) {
        return "hi " + JSON.toJSONString(req) + " by LiteGateway-Dubbo-Provider";
    }

    @Override
    @ApiInvoker(path = "/test")
    public String test(String str, XReq req) {
        return "hi " + str + JSON.toJSONString(req) + " by LiteGateway-Dubbo-Provider";
    }
}
