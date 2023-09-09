package com.xizhe.loadbalance;

import java.net.InetSocketAddress;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/9 13:02
 */
public interface Selector {

    InetSocketAddress getNext();
}
