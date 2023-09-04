package com.xizhe;

import com.xizhe.netty.MyWatcher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/3 14:45
 */

public class ZookeeperTest {

    ZooKeeper zooKeeper;

    @Before
    public void createZk() {
        String connectString = "127.0.0.1:2181";
        int timeout = 10000;
        try {
            zooKeeper = new ZooKeeper(connectString, timeout, new MyWatcher());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test4createPNode() {
        try {
            String result = zooKeeper.create("/ujs", "hello zk".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("result = " + result);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteNode() {
        try {
            // version : 乐观锁
            zooKeeper.delete("/ujs", -1);
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test4Watcher() {
        try {
            // version : 乐观锁
            zooKeeper.exists("/ujs", true);
            while(true) {
                Thread.sleep(1000);
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }


}
