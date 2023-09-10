package com.xizhe.impl;

import com.xizhe.HelloRpc2;
import com.xizhe.annotation.RpcApi;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 15:35
 */
@RpcApi
public class HelloRpcImpl2 implements HelloRpc2 {

    @Override
    public String sayHello(String msg) {
        return msg;
    }
}
