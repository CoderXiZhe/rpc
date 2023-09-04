package com.xizhe;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.logging.Handler;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 16:37
 */

@Slf4j
public class RpcBootstrap {

    private static RpcBootstrap instance = new RpcBootstrap();

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
        return this;
    }

    /**
     *  配置一个注册中心
     * @param registryConfig 注册中心
     * @return
     */
    public RpcBootstrap registry(RegistryConfig registryConfig) {
        return this;
    }

    /**
     * 配置当前序列化协议
     * @param protocolConfig 协议的封装
     * @return
     */
    public RpcBootstrap protocol(ProtocolConfig protocolConfig) {
        log.debug("当前工程使用了: {} 协议进行序列化",protocolConfig.getProtocolName());
        return this;
    }

    /**
     * 发布服务,将接口-> 实现 注册到服务中心
     * @param service 封装好的需要发布的服务
     * @return
     */
    public RpcBootstrap publish(ServiceConfig<?> service) {
        log.debug("服务{}已经被注册",service.getInterface().getName());
        return this;
    }

    /**
     * 批量发布
     * @param services
     * @return
     */
    public RpcBootstrap publish(List<?> services) {
        return this;
    }

    /**
     *  启动netty服务
     */
    public void start() {

    }

    /**
     * ------------------------------------ 服务调用方api --------------------------------------------------
     */
    public RpcBootstrap reference(ReferenceConfig<?> reference) {
        return this;
    }
}
