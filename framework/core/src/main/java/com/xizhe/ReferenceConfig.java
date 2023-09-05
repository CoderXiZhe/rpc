package com.xizhe;

import com.xizhe.discovery.Registry;
import com.xizhe.discovery.RegistryConfig;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 17:12
 */

@Slf4j
public class ReferenceConfig<T> {
    private Class<T> interfaceRef;

    private RegistryConfig registryConfig;

    public RegistryConfig getRegistryConfig() {
        return registryConfig;
    }

    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    public Class<T> getInterface() {
        return interfaceRef;
    }

    public void setInterface(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }



    public T get() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class[] classes = new Class[]{interfaceRef};
        // 生成代理对象
        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                log.info("method --> {}",method.getName());
                log.info("args --> {}",args);

                // 1. 发现服务 从注册中心寻找一个可用服务
                Registry registry = registryConfig.getRegistry();
                InetSocketAddress address = registry.discovery(interfaceRef.getName());
                log.debug("consumer发现服务【{}】可用主机:{}",interfaceRef.getName(),address);
                // 2. 使用netty连接服务器 发送调用的服务名称 + 方法名 + 参数列表 得到返回结果
                return null;
            }
        });
        return (T) helloProxy;
    }
}
