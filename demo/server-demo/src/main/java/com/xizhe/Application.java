package com.xizhe;

import com.xizhe.discovery.RegistryConfig;
import com.xizhe.impl.HelloRpcImpl;
import com.xizhe.loadbalance.impl.RoundRobinLoadBalancer;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 16:08
 */

public class Application {
    public static void main(String[] args) {

        ServiceConfig<HelloRpc> service = new ServiceConfig();
        service.setInterface(HelloRpc.class);
        service.setRef(new HelloRpcImpl());
        // 服务提供方需要注册服务
        // 1. 封装要发布的服务
        // 2. 定义注册中心

        // 3. 通过启动引导程序启动服务提供方
        // 3.1. 配置应用名称、序列化协议、压缩方式...
        // 3.2. 发布服务
        RpcBootstrap.getInstance()
                .application("first-rpc-provider")
                // 配置注册中心
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .protocol(new ProtocolConfig("jdk"))
                // 发布服务
                // .publish(service)
                .scan("com.xizhe")
                .loadBalance(new RoundRobinLoadBalancer())
                // 启动服务
                .start();
    }
}
