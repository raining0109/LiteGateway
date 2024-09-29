package me.raining.gateway.client.manager;

import lombok.extern.slf4j.Slf4j;
import me.raining.gateway.common.config.ServiceDefinition;
import me.raining.gateway.common.config.ServiceInstance;
import me.raining.gateway.client.api.ApiAnnotationScanner;
import me.raining.gateway.client.api.ApiProperties;
import me.raining.gateway.common.utils.NetUtils;
import me.raining.gateway.common.utils.TimeUtil;
import org.apache.dubbo.config.spring.ServiceBean;
import org.apache.dubbo.config.spring.context.event.ServiceBeanExportedEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashSet;
import java.util.Set;

import static me.raining.gateway.common.constant.BasicConst.COLON_SEPARATOR;
import static me.raining.gateway.common.constant.GatewayConst.DEFAULT_WEIGHT;

/**
 * @author raining
 * @version 1.0.0
 * @description dubbo实现
 * 实现ApplicationListener<ApplicationEvent>接口，监听事件
 */
@Slf4j
public class DubboClientRegisterManager extends AbstractClientRegisterManager implements ApplicationListener<ApplicationEvent> {

    /**
     * 存储处理过的bean
     */
    private Set<Object> set = new HashSet<>();

    public DubboClientRegisterManager(ApiProperties apiProperties) {
        super(apiProperties);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        //这里和mvc有区别 毕竟mvc可以直接监听star容器启动事件
        //而dubbo的导出事件会在spring容器初始化之后执行 所以也可以使用applicationlistener去监听
        if (applicationEvent instanceof ServiceBeanExportedEvent) {
            try {
                ServiceBean serviceBean = ((ServiceBeanExportedEvent) applicationEvent).getServiceBean();
                doRegisterDubbo(serviceBean);
            } catch (Exception e) {
                log.error("doRegisterDubbo error", e);
                throw new RuntimeException(e);
            }
        } else if (applicationEvent instanceof ApplicationStartedEvent) {
            log.info("dubbo api started");
        }
    }

    /**
     * 实际注册方法
     *
     * @param serviceBean 包含了服务的接口、实现类、版本、分组、协议、注册中心等配置
     */
    private void doRegisterDubbo(ServiceBean serviceBean) {
        //拿到真正bean对象  是我们真正的service
        Object bean = serviceBean.getRef();

        if (set.contains(bean)) {
            return;
        }
        set.add(bean);

        //获取服务定义
        ServiceDefinition serviceDefinition = ApiAnnotationScanner.getInstance().scanner(bean, serviceBean);

        if (serviceDefinition == null) {
            return;
        }

        serviceDefinition.setEnvType(getApiProperties().getEnv());

        //构建服务实例
        ServiceInstance serviceInstance = new ServiceInstance();
        String localIp = NetUtils.getLocalIp();
        int port = serviceBean.getProtocol().getPort();
        String serviceInstanceId = localIp + COLON_SEPARATOR + port;
        String uniqueId = serviceDefinition.getUniqueId();
        String version = serviceDefinition.getVersion();

        serviceInstance.setServiceInstanceId(serviceInstanceId);
        serviceInstance.setUniqueId(uniqueId);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setRegisterTime(TimeUtil.currentTimeMillis());
        serviceInstance.setVersion(version);
        serviceInstance.setWeight(DEFAULT_WEIGHT);

        register(serviceDefinition, serviceInstance);
    }
}
