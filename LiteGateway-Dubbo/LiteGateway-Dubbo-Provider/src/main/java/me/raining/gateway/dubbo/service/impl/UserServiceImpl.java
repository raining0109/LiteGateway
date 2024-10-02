package me.raining.gateway.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import me.raining.gateway.dubbo.entity.XReq;
import me.raining.gateway.dubbo.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Service;

/**
 * @author raining
 * @version 1.0.0
 * @description
 */
@DubboService
public class UserServiceImpl implements UserService {
    @Override
    public String sayHi(String str) {
        return "hi " + str + " by LiteGateway-Dubbo-Provider";
    }

    @Override
    public String insert(XReq req) {
        return "hi " + JSON.toJSONString(req) + " by LiteGateway-Dubbo-Provider";
    }

    @Override
    public String test(String str, XReq req) {
        return "hi " + str + JSON.toJSONString(req) + " by LiteGateway-Dubbo-Provider";
    }
}
