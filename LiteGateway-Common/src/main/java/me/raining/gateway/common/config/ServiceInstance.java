package me.raining.gateway.common.config;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author raining
 * @version 1.0.0
 * @description 服务实例注册信息，每一个{ip:port}对应一个服务实例
 */
public class ServiceInstance implements Serializable {

    @Getter
    private static final long serialVersionUID = 2171720952813363425L;

    /**
     * 	服务实例ID: ip:port
     */
    @Getter
    @Setter
    protected String serviceInstanceId;

    /**
     * 	服务定义唯一id： uniqueId
     */
    @Getter
    @Setter
    protected String uniqueId;

    /**
     * 	服务实例ip地址
     */
    @Getter
    @Setter
    protected String ip;

    /**
     * 	服务实例端口
     */
    @Getter
    @Setter
    protected int port;

    /**
     * 	标签信息
     */
    @Getter
    @Setter
    protected String tags;

    /**
     * 	权重信息
     */
    @Getter
    @Setter
    protected Integer weight;

    /**
     * 	服务注册的时间戳，用来做负载均衡，warmup预热
     */
    @Getter
    @Setter
    protected long registerTime;

    /**
     * 	服务实例启用禁用
     */
    @Getter
    @Setter
    protected boolean enable = true;

    /**
     * 	服务实例对应的版本号
     */
    @Getter
    @Setter
    protected String version;

    /**
     * 服务实例是否是灰度的，用于灰度发布
     */
    @Getter
    @Setter
    protected boolean gray;

    public ServiceInstance() {
        super();
    }

    /**
     * 根据 serviceInstanceId 判断两个服务实例是不是同一个
     * 也即：{ip+port}
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(this == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceInstance serviceInstance = (ServiceInstance)o;
        return Objects.equals(serviceInstanceId, serviceInstance.serviceInstanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceInstanceId);
    }
}