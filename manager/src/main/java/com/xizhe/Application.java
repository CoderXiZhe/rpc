package com.xizhe;

import com.xizhe.utils.ZookeeperUtils;
import org.apache.zookeeper.*;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;


/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 19:18
 */

public class Application {
    public static void main(String[] args) {
        ZooKeeper zooKeeper = ZookeeperUtils.createZookeeper();
        // 定义节点和数据
        String basePath = "/rpc-metadata";
        String providerPath = basePath + "/providers";
        String consumerPath = basePath + "/consumers";
        ZookeeperNode baseNode = new ZookeeperNode(basePath,null);
        ZookeeperNode providerNode = new ZookeeperNode(providerPath,null);
        ZookeeperNode consumerNode = new ZookeeperNode(consumerPath,null);

        List<Object> l = new ArrayList<>();

        Arrays.asList(baseNode,providerNode,consumerNode).forEach(node -> {
            ZookeeperUtils.createNode(zooKeeper, node,null,CreateMode.PERSISTENT);
        });

        ZookeeperUtils.close(zooKeeper);

    }
}





