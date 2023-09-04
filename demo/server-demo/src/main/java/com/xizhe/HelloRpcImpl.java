package com.xizhe;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 15:35
 */

public class HelloRpcImpl implements HelloRpc {

    @Override
    public String sayHello(String msg) {
        return "hello rpc!";
    }
}
