package com.xizhe;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 15:33
 */

public interface HelloRpc {

    /**
     * 通用接口, client和server都需要依赖
     * @param msg 发送的具体消息
     * @return
     */
    String sayHello(String msg);
}
