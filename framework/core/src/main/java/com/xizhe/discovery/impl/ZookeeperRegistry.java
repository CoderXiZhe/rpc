package com.xizhe.discovery.impl;

import com.xizhe.Constant;
import com.xizhe.ServiceConfig;
import com.xizhe.ZookeeperNode;
import com.xizhe.discovery.AbstractRegistry;
import com.xizhe.excptions.NetWorkException;
import com.xizhe.utils.NetUtils;
import com.xizhe.utils.ZookeeperUtils;
import com.xizhe.watch.UpAndDownWatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/5 12:11
 */

@Slf4j
public class ZookeeperRegistry extends AbstractRegistry {

    private ZooKeeper zooKeeper;

    public ZookeeperRegistry() {
        this.zooKeeper = ZookeeperUtils.createZookeeper();
    }

    public ZookeeperRegistry(String host) {
        this.zooKeeper = ZookeeperUtils.createZookeeper(host,Constant.TIME_OUT);
    }

    @Override
    public void registry(ServiceConfig<?> service) {
        String nodePath = Constant.BASE_PROVIDERS_PATH + "/" + service.getInterface().getName();
        boolean exist = ZookeeperUtils.exist(zooKeeper, nodePath, null);
        if(!exist) {
            ZookeeperNode zookeeperNode = new ZookeeperNode(nodePath,null);
            ZookeeperUtils.createNode(zooKeeper,zookeeperNode,null, CreateMode.PERSISTENT);
        }
        // 创建本机临时节点 ip:port
        // todo ： 后续处理端口问题
        String node = nodePath + "/" + NetUtils.getIp() + ":" + Constant.port;
        boolean exist1 = ZookeeperUtils.exist(zooKeeper, node, null);
        if(!exist1) {
            ZookeeperNode zookeeperNode = new ZookeeperNode(node,null);
            ZookeeperUtils.createNode(zooKeeper,zookeeperNode,null, CreateMode.EPHEMERAL);
        }
        log.debug("服务{}已经被注册", nodePath);
    }

    @Override
    public List<InetSocketAddress> discovery(String serviceName) {
        // 1. 找到对应服务的节点
        String serviceNode = Constant.BASE_PROVIDERS_PATH + "/" + serviceName;
        // 2. 获取该节点的子节点
        List<String> children = ZookeeperUtils.getChildren(zooKeeper, serviceNode, new UpAndDownWatcher());
        // 3. 可用服务列表
        List<InetSocketAddress> collect = children.stream().map(ipString -> {
            String[] hostAndPort = ipString.split(":");
            String host = hostAndPort[0].trim();
            Integer port = Integer.valueOf(hostAndPort[1]);
            return new InetSocketAddress(host, port);
        }).collect(Collectors.toList());

        if(collect.size() == 0) {
            throw new NetWorkException("未发现可用服务！");
        }

        return collect;
    }
}
