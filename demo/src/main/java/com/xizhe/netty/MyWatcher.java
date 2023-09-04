package com.xizhe.netty;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 12:49
 */

public class MyWatcher implements Watcher {
    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.EventType type = watchedEvent.getType();
        // Event.EventType.None 该状态就是连接状态事件类型
        if(type == Event.EventType.None) {
            // 连接状态
            Event.KeeperState state = watchedEvent.getState();
            if(state == Event.KeeperState.SyncConnected) {
                System.out.println("zk连接成功");
            }else if(state == Event.KeeperState.AuthFailed) {
                System.out.println("zk认证失败");
            }else if(state == Event.KeeperState.Disconnected) {
                System.out.println("zk断开连接");
            }
        }else if(type == Event.EventType.NodeCreated) {
            System.out.println(watchedEvent.getPath() + "节点被创建了");
        }else if(type == Event.EventType.NodeDeleted) {
            System.out.println(watchedEvent.getPath() + "节点被删除了");
        }else if(type == Event.EventType.NodeDataChanged) {
            System.out.println(watchedEvent.getPath() + "节点数据被修改了");
        }
    }
}
