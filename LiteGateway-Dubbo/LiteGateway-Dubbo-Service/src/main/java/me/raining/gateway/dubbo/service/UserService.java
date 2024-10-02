package me.raining.gateway.dubbo.service;

import me.raining.gateway.dubbo.entity.XReq;

/**
 * @author raining
 * @version 1.0.0
 * @description 活动服务接口
 */
public interface UserService {

    String sayHi(String str);

    String insert(XReq req);

    String test(String str, XReq req);
}
