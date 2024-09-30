package me.raining.gateway.client.autoconfigure;

import me.raining.gateway.client.api.ApiProperties;
import me.raining.gateway.client.manager.DubboClientRegisterManager;
import me.raining.gateway.client.manager.SpringMVCClientRegisterManager;
import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Servlet;

/**
 * @author raining
 * @version 1.0.0
 * @description 自动装配
 * 如果使用2.7以上版本springboot会更加方便实现
 */
@Configuration
@EnableConfigurationProperties(ApiProperties.class)
//要求有registerAddress 否则不会进行自动注册
@ConditionalOnProperty(prefix = "api", name = {"registerAddress"})
public class ApiClientAutoConfiguration {

    @Autowired
    private ApiProperties apiProperties;

    /**
     * springmvc环境下才会配置
     * ConditionalOnMissingBean保证Spring容器中只有一个Bean类型的实例，
     * 当注册多个相同类型的Bean时，会出现异常
     */
    @Bean
    @ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
    @ConditionalOnMissingBean(SpringMVCClientRegisterManager.class)
    public SpringMVCClientRegisterManager springMVCClientRegisterManager() {
        return new SpringMVCClientRegisterManager(apiProperties);
    }

    /**
     * Dubbo环境下才会进行配置
     */
    @Bean
    @ConditionalOnClass({ServiceBean.class})
    @ConditionalOnMissingBean(DubboClientRegisterManager.class)
    public DubboClientRegisterManager dubboClientRegisterManager() {
        return new DubboClientRegisterManager(apiProperties);
    }
}
