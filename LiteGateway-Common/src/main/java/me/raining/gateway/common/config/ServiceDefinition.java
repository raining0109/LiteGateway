package me.raining.gateway.common.config;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * @author raining
 * @version 1.0.0
 * @description 资源服务定义类，包含服务列表信息(http/rpc接口)
 * 下游服务都需要进行注册
 */
public class ServiceDefinition implements Serializable {

    private static final long serialVersionUID = 3031463352108975608L;


    /**
     * 唯一的服务ID: serviceId:version
     */
    @Getter
    @Setter
    private String uniqueId;

    /**
     * 服务唯一id
     */
    @Getter
    @Setter
    private String serviceId;

    /**
     * 服务的版本号
     */
    @Getter
    @Setter
    private String version;

    /**
     * 服务的具体协议：http(mvc http) dubbo ..
     */
    @Getter
    @Setter
    private String protocol;

    /**
     * 路径匹配规则：访问真实ANT表达式：定义具体的服务路径的匹配规则
     */
    @Getter
    @Setter
    private String patternPath;

    /**
     * 环境名称
     */
    @Getter
    @Setter
    private String envType;

    /**
     * 服务启用禁用
     */
    @Getter
    @Setter
    private boolean enable = true;

    /**
     * 服务列表信息
     */
    @Getter
    @Setter
    private Map<String /*invokerPath*/, ServiceInvoker> invokerMap;

    public ServiceDefinition() {
        super();
    }

    public ServiceDefinition(String uniqueId, String serviceId, String version, String protocol, String patternPath,
                             String envType, boolean enable, Map<String, ServiceInvoker> invokerMap) {
        super();
        this.uniqueId = uniqueId;
        this.serviceId = serviceId;
        this.version = version;
        this.protocol = protocol;
        this.patternPath = patternPath;
        this.envType = envType;
        this.enable = enable;
        this.invokerMap = invokerMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (this == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceDefinition serviceDefinition = (ServiceDefinition) o;
        return Objects.equals(uniqueId, serviceDefinition.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }
}
