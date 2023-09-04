package com.xizhe;

import com.xizhe.utils.ZookeeperNode;
import com.xizhe.utils.ZookeeperUtil;
import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 19:18
 */

public class Application {
    public static void main(String[] args) {
        ZooKeeper zooKeeper = ZookeeperUtil.createZookeeper();
        // 定义节点和数据
        String basePath = "/rpc-metadata";
        String providerPath = basePath + "/providers";
        String consumerPath = basePath + "/consumers";
        ZookeeperNode baseNode = new ZookeeperNode(basePath,null);
        ZookeeperNode providerNode = new ZookeeperNode(providerPath,null);
        ZookeeperNode consumerNode = new ZookeeperNode(consumerPath,null);

        List<Object> l = new ArrayList<>();

        Arrays.asList(baseNode,providerNode,consumerNode).forEach(node -> {
            ZookeeperUtil.createNode(zooKeeper, node,null,CreateMode.PERSISTENT);
        });

        ZookeeperUtil.close(zooKeeper);

    }
}
