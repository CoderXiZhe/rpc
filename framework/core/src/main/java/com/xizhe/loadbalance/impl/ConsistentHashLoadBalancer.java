package com.xizhe.loadbalance.impl;

import com.xizhe.RpcBootstrap;
import com.xizhe.excptions.LoadBalanceException;
import com.xizhe.loadbalance.AbstractLoadBalancer;
import com.xizhe.loadbalance.Selector;
import com.xizhe.transport.message.RpcRequest;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/9 14:23
 */

public class ConsistentHashLoadBalancer extends AbstractLoadBalancer {



    @Override
    protected Selector getSelector(List<InetSocketAddress> serverList, String serviceName) {
        return new ConsistentHashSelector(serverList,128);
    }


    private static class ConsistentHashSelector implements Selector {
        private SortedMap<Integer, InetSocketAddress> circle = new TreeMap<>();

        private int virtualNodes;

        public ConsistentHashSelector(List<InetSocketAddress> serverList,int nodes) {
            this.virtualNodes = nodes;
            for (InetSocketAddress address : serverList) {
                addNodeToCircle(address);
            }
        }

        @Override
        public InetSocketAddress getNext() {
            // hash环已建立好，需要根据请求要素获取hash进而得到具体的服务
            RpcRequest request = RpcBootstrap.REQUEST_THREAD_LOCAL.get();
            String requestId = request.getRequestId().toString();
            int hash = hash(requestId);
            if(!circle.containsKey(hash)) {
                    SortedMap<Integer, InetSocketAddress> tailMap = circle.tailMap(hash);
                hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
            }
            return circle.get(hash);
        }


        private void addNodeToCircle(InetSocketAddress address) {
            for (int i = 0; i < virtualNodes; i++) {
                int hash = hash(address.toString() + "-" + i);
                circle.put(hash,address);
            }
        }

        private void remoceNodeFromCircle(InetSocketAddress address) {
            for (int i = 0; i < virtualNodes; i++) {
                int hash = hash(address.toString() + "-" + i);
                circle.remove(hash,address);
            }
        }

        private int hash(String s) {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("md5");
                byte[] digest = md5.digest(s.getBytes());
                int res = 0;
                for (int i = 0; i < 4; i++) {
                    int mid = digest[i] << ((3 - i) * 8);
                    res = res | mid;
                }
                return res;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
