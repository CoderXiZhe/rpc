package com.xizhe.loadbalance;

import com.xizhe.RpcBootstrap;
import com.xizhe.discovery.Registry;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/9 13:38
 */

public abstract class AbstractLoadBalancer implements LoadBalancer{

    protected Registry registry;

    private Map<String,Selector> selectorCache = new ConcurrentHashMap<>(8);


    public AbstractLoadBalancer() {

    }

    @Override
    public void reBalance(String serviceName, List<InetSocketAddress> addressList) {
        selectorCache.put(serviceName,getSelector(addressList,serviceName));
    }

    /**
     * 根据服务名获取服务的骨架代码
     * @param serviceName 服务名
     * @return 具体服务
     */
    @Override
    public InetSocketAddress selectServiceAddress(String serviceName) {
        this.registry = RpcBootstrap.getInstance().getConfiguration().getRegistryConfig().getRegistry();
        Selector selector = selectorCache.get(serviceName);
        if (selector == null) {
            selector = getSelector(registry.discovery(serviceName),serviceName);
            selectorCache.put(serviceName,selector);
        }
        return selector.getNext();
    }

    /**
     * 具体实现推迟到子类去完成
     * @param serverList 服务列表
     * @return 具体的Selector
     */
    protected abstract Selector getSelector(List<InetSocketAddress> serverList,String serviceName);

}
