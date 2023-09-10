package com.xizhe.watch;

import com.xizhe.RpcBootstrap;
import com.xizhe.NettyBootstrapInitializer;
import com.xizhe.discovery.Registry;
import com.xizhe.loadbalance.LoadBalancer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 * @version 1.0
 * @description: 服务动态上下线的感知
 * @date 2023/9/10 13:16
 */

@Slf4j
public class UpAndDownWatcher implements Watcher {
    @Override
    public void process(WatchedEvent event) {

        if(event.getType()  == Event.EventType.NodeChildrenChanged) {
            log.debug("监测到【{}】子节点上/下线,将重新拉取服务",event.getPath());
            Registry registry = RpcBootstrap.getInstance().getRegistry();
            String serviceName = getServiceName(event.getPath());
            List<InetSocketAddress> hostList = registry.discovery(serviceName);
            for (InetSocketAddress address : hostList) {
                // 服务上线
                if(!RpcBootstrap.CHANNEL_CACHE.containsKey(address)) {
                    try {
                        Channel channel = NettyBootstrapInitializer.getBootstrap()
                                .connect(address).sync().channel();
                            RpcBootstrap.CHANNEL_CACHE.put(address,channel);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            for(Map.Entry<InetSocketAddress,Channel> entry : RpcBootstrap.CHANNEL_CACHE.entrySet()) {
                if(!hostList.contains(entry.getKey())) {
                    // 服务下线
                    RpcBootstrap.CHANNEL_CACHE.remove(entry.getKey());
                }

            }
            // rebalance
            LoadBalancer loadBalancer = RpcBootstrap.getInstance().getConfiguration().getLoadBalancer();
            loadBalancer.reBalance(serviceName,hostList);
        }

    }

    private String getServiceName(String path) {
        String[] split = path.split("/");
        return split[split.length - 1];
    }
}
