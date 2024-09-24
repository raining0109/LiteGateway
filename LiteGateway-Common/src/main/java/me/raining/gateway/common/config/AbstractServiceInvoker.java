package me.raining.gateway.common.config;

import lombok.Data;

/**
 * @author raining
 * @version 1.0.0
 * @description 抽象的服务调用接口实现类
 */
@Data
public class AbstractServiceInvoker implements ServiceInvoker{

    protected String invokerPath;

    protected int timeout = 5000;
}
