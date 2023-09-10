package com.xizhe.loadbalance.impl;

import com.xizhe.RpcBootstrap;
import com.xizhe.loadbalance.AbstractLoadBalancer;
import com.xizhe.loadbalance.Selector;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/9 17:22
 */

@Slf4j
public class MinimumRespTimeLoadBalancer extends AbstractLoadBalancer {


    @Override
    protected Selector getSelector(List<InetSocketAddress> serverList, String serviceName) {
        return new MinimumRespTimeSelector();
    }


    private static class MinimumRespTimeSelector implements Selector {


        @Override
        public InetSocketAddress getNext() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("--------------响应时间treemap-------------");
            Set<Long> time = RpcBootstrap.ANSWER_TIME_CACHE.keySet();
            for (Long t : time) {
                log.debug("与主机【{}】的响应时间:[{}]",RpcBootstrap.ANSWER_TIME_CACHE.get(t),t);
            }
            if(!RpcBootstrap.ANSWER_TIME_CACHE.isEmpty()) {
                Long minimum = RpcBootstrap.ANSWER_TIME_CACHE.firstKey();
                InetSocketAddress address = (InetSocketAddress) RpcBootstrap.ANSWER_TIME_CACHE
                        .get(minimum).remoteAddress();
                log.debug("选取了主机【{}】,响应时间为:[{}]", address, minimum);
                return address;
            }
            throw new RuntimeException();
        }

    }
}
