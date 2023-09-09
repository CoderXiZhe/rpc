package com.xizhe.loadbalance.impl;

import com.xizhe.RpcBootstrap;
import com.xizhe.loadbalance.AbstractLoadBalancer;
import com.xizhe.loadbalance.Selector;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/9 17:22
 */

public class MinimumRespTimeLoadBalancer extends AbstractLoadBalancer {


    @Override
    protected Selector getSelector(List<InetSocketAddress> serverList, String serviceName) {
        return new MinimumRespTimeSelector();
    }

    private static class MinimumRespTimeSelector implements Selector {


        @Override
        public InetSocketAddress getNext() {
            return (InetSocketAddress )RpcBootstrap.ANSWER_TIME_CACHE
                    .get(RpcBootstrap.ANSWER_TIME_CACHE.firstKey()).remoteAddress();
        }
    }
}
