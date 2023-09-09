package com.xizhe.loadbalance.impl;

import com.xizhe.RpcBootstrap;
import com.xizhe.discovery.Registry;
import com.xizhe.excptions.LoadBalanceException;
import com.xizhe.loadbalance.AbstractLoadBalancer;
import com.xizhe.loadbalance.Selector;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/9 12:25
 */

@Slf4j
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected Selector getSelector(List<InetSocketAddress> serverList, String serviceName) {
        return new RoundRobinSelector(registry.discovery(serviceName));
    }


    private static class RoundRobinSelector implements Selector {
        private List<InetSocketAddress> serverList;

        private AtomicInteger index;

        public RoundRobinSelector(List<InetSocketAddress> serverList) {
            this.serverList = serverList;
            index =  new AtomicInteger(0);
        }

        @Override
        public InetSocketAddress getNext() {
            if(serverList == null || serverList.size() == 0) {
                log.error("负载均衡失败!服务列表为空!");
                throw new LoadBalanceException();
            }
            InetSocketAddress address = serverList.get(index.get());
            if(index.get() == serverList.size() -1 ) {
                index.set(0);
            }else {
                index.incrementAndGet();
            }
            return address;
        }
    }
}
