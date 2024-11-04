<p align="center"><img src= "https://tuchuang-01.oss-cn-beijing.aliyuncs.com/img/logo.drawio.png" alt="MaxKB" width="300" /></p>
<h3 align="center">LiteGateway，一个基于 Netty & Nacos 的高性能轻量级自研网关</h3>
<p align="center">
  <a href="https://opensource.org/license/MIT"><img src="https://img.shields.io/github/license/raining0109/LiteGateway?color=rgb(25%2C%20121%2C%20255)" alt="The MIT License"></a>
  <a href=""><img src="https://img.shields.io/github/forks/raining0109/LiteGateway?color=green" alt="Forks"></a>
  <a href="https://laigeoffer.cn/"><img src="https://img.shields.io/badge/LiteGateway-GitHub-green" alt="Official"></a>
  <a href="https://github.com/laigeoffer/pmhub"><img src="https://img.shields.io/github/stars/raining0109/LiteGateway?style=flat-square&color=rgb(25%2C%20121%2C%20255)" alt="Stars"></a>    
</p>
<hr/>
LiteGateway 是一个基于 Netty & Nacos 的高性能轻量级自研网关，支持 Dubbo/HTTP 双协议，实现了鉴权、限流、负载均衡、灰度、MOCK 和路由等多个过滤器，并支持用户基于 SPI 机制自定义过滤器。

## 一、项目架构
### 整体架构

<p align="center"><img src= "https://tuchuang-01.oss-cn-beijing.aliyuncs.com/img/API网关框架.drawio.png" /></p>

### 核心流程

<p align="center"><img src= "https://tuchuang-01.oss-cn-beijing.aliyuncs.com/img/gateway-core-logic.drawio.png" /></p>

### 过滤器链

<p align="center"><img src= "https://tuchuang-01.oss-cn-beijing.aliyuncs.com/img/filter1002.png" /></p>

## 二、技术调研

### 公司网关

| 公司 | 链接 | 技术栈 |
| :-: | :-: | :-: |
| 美团 | [百亿规模API网关服务Shepherd的设计与实现](https://mp.weixin.qq.com/s/iITqdIiHi3XGKq6u6FRVdg) | 基于 Netty 自研 |
| 得物 | [得物自研API网关实践之路](https://mp.weixin.qq.com/s/IXInfuWkKe5D1fmtpQQ1SA) | 基于 Netty 自研 |
| 喜马拉雅 | [喜马拉雅自研网关架构实践](https://www.jianshu.com/p/165b1941cdfa) | 基于 Netty 自研 |
| 携程 | [日均流量200亿，携程高性能全异步网关实践](https://mp.weixin.qq.com/s/JdbPf_H4pn5PnPH2LIKQlw) | 基于 Netty 自研 |
| vivo | [vivo 微服务 API 网关架构实践](https://mp.weixin.qq.com/s/5U1rgpcW21LDYzv8K9EX7g) | 基于 Zuul2 定制化开发 |
| <font style="color:rgba(0, 0, 0, 0.9);">随行付</font> | [随行付微服务之基于Zuul自研服务网关](https://mp.weixin.qq.com/s/ykgCJ5I1BtqTjMtXUvFVHA) | 基于 Zuul2 定制化开发 |
| 爱奇艺 | [基于 Apache APISIX，爱奇艺 API 网关的更新与落地实践](https://mp.weixin.qq.com/s/f3xlBDdq1FavidzWeoQMIQ) | 基于 Apache APISIX 定制化开发 |
| 新浪微博 | [新浪微博 API 网关的定制化开发之路](https://mp.weixin.qq.com/s/I44WTwY0otnR3OJbLcLtDw) | 基于 Apache APISIX 定制化开发 |
| B 站 | [B站 API 网关的发展](https://mp.weixin.qq.com/s/-bV1gZq7GO6bGPcYGAwc9g) | Golang 自研 |
| 个推 | [日均数十亿访问量！解读个推API网关高能演进](https://mp.weixin.qq.com/s/mVa5BQwc9l1HAOUgusqJRQ) | Golang 自研 |
| 顺丰 | [API网关在北科的实践与应用](https://mp.weixin.qq.com/s/9Df_dKFh1vkkWZXz4kZtAA) | 基于 Kong 定制化开发 |


### 著名开源网关
| 公司 | 链接 | 技术栈 |
| :-: | :-: | :-: |
| OpenResty | [https://github.com/openresty/openresty](https://github.com/openresty/openresty) | Nginx+Lua |
| Apache APISIX | [https://apisix.apache.org/zh/](https://apisix.apache.org/zh/) | Nginx+Lua |
| Kong | [https://github.com/Kong/kong](https://github.com/Kong/kong) | Nginx+Lua |
| Zuul1 | [https://github.com/Netflix/zuul](https://github.com/Netflix/zuul) | Tomcat(NIO)+<font style="color:rgba(0, 0, 0, 0.9);">HttpClient</font> |
| Zuul2 | [https://github.com/Netflix/zuul](https://github.com/Netflix/zuul) | Netty |
| Shenyu | [https://github.com/apache/shenyu](https://github.com/apache/shenyu) | Spring WebFlux |
| Spring Cloud Gateway | [https://github.com/spring-cloud/spring-cloud-gateway](https://github.com/spring-cloud/spring-cloud-gateway) | Spring WebFlux |

### 结论

常见的开源网关按照语言分类有如下几类：

- **Nginx+Lua：** OpenResty、Kong 等；
- **Java：** Zuul1/Zuul2、Spring Cloud Gateway、gravitee-gateway、gateway、Shenyu 等；
- **Go：** janus、GoKu API Gateway 等；
- **Node.js：** Express Gateway、MicroGateway 等。

我们主要考虑 Java 语言的网关。接下来调研了Zuul1、Zuul2、Spring Cloud Gateway、Shenyu。

业界主流的网关基本上可以分为下面四种：

- Servlet + 线程池
- NIO(Tomcat / Jetty) + Servlet 3.0 异步
- Spring WebFlux
- NettyServer + NettyClient

在进行技术选型的时候，主要考虑功能丰富度、性能、稳定性。LiteGateway 是一个基于 Netty 框架的异步非阻塞的高性能网关，设计中借鉴了 Zuul2 的部分思想：

- [Zuul2 原理与源码深入剖析](https://raining.me/2024/10/06/zuul2-gateway-source-code-analysis/)

## 三、技术选型

|       技术        |        说明         | 官网                                                                                                 |
|:---------------:|:-----------------:|:--------------------------------------------------------------------------------------------------:|
|      Netty      |  异步事件驱动的网络应用程序框架  | [https://github.com/netty/netty](https://github.com/netty/netty)                                                           |
|      Nacos      |     注册中心和配置中心     | https://github.com/alibaba/nacos |
| AsyncHttpClient | Java 异步 HTTP 客户端库 | https://github.com/AsyncHttpClient/async-http-client   |
|      Dubbo      |      RPC 服务       | [https://github.com/apache/dubbo](https://github.com/apache/dubbo)                                                     |
|    Disruptor    |       高性能队列       | https://github.com/LMAX-Exchange/disruptor                     |
|    Caffeine     |      高性能缓存框架      | https://github.com/ben-manes/caffeine                        |
|       JWT       |     JWT 鉴权登录      | [https://jwt.io](https://jwt.io)               |
|      Redis      |    基于内存的键值对数据库    |               https://github.com/redis/redis                                                                                     |
|   Prometheus    |    系统监控和警报工具包     | https://github.com/prometheus/prometheus |
|     Grafana     |   查询、可视化、警报观测平台   | https://github.com/grafana/grafana                           |

## 四、使用流程

1. 首先在客户端引入 Client 包，具体使用流程参照模块 `LiteGateway-HttpServer`。
```
<dependency>
    <groupId>me.raining</groupId>
    <artifactId>LiteGateway-Client</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
2. 确保 Nacos（注册中心和配置中心） 和 Redis（用于分布式限流）配置正确且成功启动。
3. 在 Nacos 中配置网关规则，配置规则见文件`api-gateway.json`。
4. 最后，启动`me.raining.gateway.core.Bootstrap`即可。

