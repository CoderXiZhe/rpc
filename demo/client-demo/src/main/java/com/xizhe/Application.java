package com.xizhe;

import com.xizhe.discovery.RegistryConfig;
import com.xizhe.serialize.SerializerType;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 16:14
 */

public class Application {
    public static void main(String[] args) {
        // 获取代理对象 通过ReferenceConfig封装
        ReferenceConfig<HelloRpc> reference = new ReferenceConfig();
        reference.setInterface(HelloRpc.class);

        // 代理 ：
        // 连接注册中心 -> 拉取服务列表 -> 选择一个服务并建立连接 -> 发送请求,携带一些信息(接口名,参数列表...),获得结果
        // 代理对象

        RpcBootstrap.getInstance()
                .application("first-rpc-consumer")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .serialize(SerializerType.HESSIAN)
                .reference(reference);

        HelloRpc proxy = reference.get();
        String result = proxy.sayHello("rpc");
        System.out.println(result);


    }
}
