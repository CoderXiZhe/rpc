package com.xizhe.utils;

import com.xizhe.Constant;
import com.xizhe.ZookeeperNode;
import com.xizhe.excptions.ZookeeperException;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 19:52
 */

@Slf4j
public class ZookeeperUtils {

    public static ZooKeeper createZookeeper() {
        String connectString = Constant.DEFAULT_ZK_CONNECT;
        int timeout = Constant.TIME_OUT;
        return createZookeeper(connectString,timeout);
    }

    /**
     * 创建zk实例
     * @param connectString 连接地址
     * @param timeout 超时时间
     * @return
     */
    public static ZooKeeper createZookeeper(String connectString,int timeout) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            // 创建zookeeper实例
            final ZooKeeper zooKeeper = new ZooKeeper(connectString, timeout, event -> {
                // 只有连接成功才会放行
                if(event.getState() == Watcher.Event.KeeperState.SyncConnected){
                    System.out.println("客户端连接成功!");
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            return zooKeeper;
        } catch (IOException | InterruptedException e) {
            log.error("创建zookeeper实例发生错误:",e);
            throw new ZookeeperException();
        }
    }

    /**
     * 创建一个ZK节点
     * @param zooKeeper zk实例
     * @param node 节点
     * @param watcher watcher
     * @param createMode 节点的类型
     */
    public static boolean createNode(ZooKeeper zooKeeper, ZookeeperNode node, Watcher watcher, CreateMode createMode) {
        try {
            if(zooKeeper.exists(node.getNodePath(),watcher) == null) {
                String result = zooKeeper.create(node.getNodePath(), node.getData(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                log.info("节点【{}】创建成功",result);
                return true;
            }else {
                log.error("节点【{}】已存在",node.getNodePath());
                return false;
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("创建节点【{}】发生异常:",node.getNodePath(),e);
            throw new ZookeeperException();
        }
    }


    /**
     * 判断节点是否存在
     * @param zooKeeper zk实例
     * @param nodePath 节点路径
     * @param watcher watcher
     * @return true: 存在 | false: 不存在
     */
    public static boolean exist(ZooKeeper zooKeeper,String nodePath,Watcher watcher) {
        try {
            return zooKeeper.exists(nodePath,watcher) != null;
        } catch (KeeperException | InterruptedException e) {
            log.error("判断节点[{}]是否存在发生异常",nodePath,e);
            throw new ZookeeperException(e);
        }

    }

    /**
     * 关闭zk实例
     * @param zookeeper
     */
    public static void close(ZooKeeper zookeeper) {
        try {
            zookeeper.close();
        } catch (InterruptedException e) {
            log.error("关闭zk实例发生错误：",e);
        }
    }

    public static List<String> getChildren(ZooKeeper zooKeeper, String serviceNode,Watcher watcher) {
        try {
            return zooKeeper.getChildren(serviceNode, watcher);
        } catch (KeeperException | InterruptedException e) {
            log.error("获取【{}】子节点出现异常",serviceNode,e);
            throw new ZookeeperException(e);
        }
    }
}
