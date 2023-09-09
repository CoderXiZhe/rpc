package com.xizhe.discovery;

import com.xizhe.ServiceConfig;

import java.net.InetSocketAddress;
import java.util.List;

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
     * 从注册中心拉取可用服务列表
     * @param serviceName 服务的名称
     * @return 服务列表
     */
    List<InetSocketAddress> discovery(String serviceName);
}
