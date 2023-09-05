package com.xizhe;

import com.xizhe.discovery.Registry;
import com.xizhe.discovery.RegistryConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 16:37
 */

@Slf4j
public class RpcBootstrap {

    private static final RpcBootstrap instance = new RpcBootstrap();

    private String appName;

    private ProtocolConfig protocolConfig;

    private RegistryConfig registryConfig;

    private int port = 8099;

    // 维护一个zk实例
    private Registry registry ;

    // 维护一个已经发布的服务列表 key: interface的全限定名 value:serviceConfig
    private static final Map<String,ServiceConfig<?>> SERVER_LIST = new ConcurrentHashMap<>();

    private RpcBootstrap() {

    }

    public static RpcBootstrap getInstance() {
        return instance;
    }

    /**
     * 定义当前应用名称
     * @param appName
     * @return
     */

    public RpcBootstrap application(String appName) {
        this.appName = appName;
        return this;
    }

    /**
     *  配置一个注册中心
     * @param registryConfig 注册中心
     * @return
     */
    public RpcBootstrap registry(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
        // 通过registryConfig 获取注册中心 : 工厂模式
        this.registry = registryConfig.getRegistry();
        return this;
    }

    /**
     * 配置当前序列化协议
     * @param protocolConfig 协议的封装
     * @return
     */
    public RpcBootstrap protocol(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
        log.debug("当前工程使用了: {} 协议进行序列化",protocolConfig.getProtocolName());
        return this;
    }

    /**
     * 发布服务,将接口-> 实现 注册到服务中心
     * @param service 封装好的需要发布的服务
     * @return
     */
    public RpcBootstrap publish(ServiceConfig<?> service) {
        // 面相抽象编程
        registry.registry(service);
        SERVER_LIST.put(service.getInterface().getName(),service);
        return this;
    }

    /**
     * 批量发布
     * @param services
     * @return
     */
    public RpcBootstrap publish(List<ServiceConfig<?>> services) {
        services.forEach(this::publish);
        return this;
    }

    /**
     *  启动netty服务
     */
    public void start() {
        try {
            Thread.sleep(1000000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * ------------------------------------ 服务调用方api --------------------------------------------------
     */
    public RpcBootstrap reference(ReferenceConfig<?> reference) {
        reference.setRegistryConfig(registryConfig);
        return this;
    }
}
