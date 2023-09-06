package com.xizhe.discovery;

import com.xizhe.ServiceConfig;

import java.net.InetSocketAddress;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/5 12:09
 */

public interface Registry {

    /**
     * 注册服务
     * @param serviceConfig 服务的配置内容
     */
    void registry(ServiceConfig<?> serviceConfig);

    /**
     * 从注册中心发现并拉取一个可用服务
     * @param serviceName 服务的名称
     * @return 服务的 ip+port
     */
    InetSocketAddress discovery(String serviceName);
}
