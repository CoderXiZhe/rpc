package com.xizhe.loadbalance;

import java.net.InetSocketAddress;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/9 12:26
 */
public interface LoadBalancer {

    /**
     * 根据服务名 选择一个可用服务
     * @param serviceName 服务名
     */
    InetSocketAddress selectServiceAddress(String serviceName);
}
