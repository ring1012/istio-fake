## 项目介绍
本项目仿照Feign，旨在istio体系中，快速完成服务间调用，并保持链路追踪。

去除了Feign中所有熔断，负载均衡策略，全部由istio控制。
### demo

- [servicea](https://github.com/ring1012/servicea.git)
- [serviceb](https://github.com/ring1012/serviceb.git) 


### demo说明
  servicea提供client，serviceb依赖servicea的client并开启Fake，调用servicea提供的服务


## 项目功能

1. 便捷的在istio框架中，完成服务调用

2. 支持多环境配置：例如本地环境微服务调用的地址可配置为本地，其他环境默认为kubernetes集群中的服务

3. 支持链路追踪：项目默认透传了如下header，可以自动支持jaeger、zipkin链路追踪
```$xslt
"x-request-id", "x-b3-traceid", "x-b3-spanid", "x-b3-sampled", "x-b3-flags", "x-b3-parentspanid","x-ot-span-context", "x-datadog-trace-id", "x-datadog-parent-id", "x-datadog-sampled", "end-user", "user-agent"

```

4. 支持异步调用链路追踪：使用了阿里ttl线程池，每次请求的上下文都可以动态变化

## 项目使用

* 1.code参考serivea和serviceb。servicea角色：服务提供方；serviceb角色：服务调用方

* 2.servicea依赖本项目，提供FakeClient("servera"")，编写声明式调用接口。

* 3.serviceb依赖servicea-client

* 4.serviceb依赖本项目，开启Fake注解@EnableFakeClients，项目会自动生成servicea-client接口的实现，并注入到spring容器中

* 5.serviceb重写本项目的HttpRequestHeaderHolder接口，将服务的每次请求头保存到线程的上下文中。

## 结果展示

![异步链路追踪](https://www.showdoc.cc/server/api/common/visitfile/sign/d8a3795a3023a1cb790a32238ea14175?showdoc=.jpg "异步链路追踪")

## 服务配置说明

* 1.默认情况下，FakeClient("servera"")会直接使用servera作为服务的host。适用于集群环境

* 2.本地开发环境中，由于没有集群，直接调用http://servera/apixxx是不通的。本项目会优先解析spring的properties配置，如果寻找到以“fake.”打头的配置，那么会优先使用该配置地址。
  eg：fake.servera=localhost:8080 

