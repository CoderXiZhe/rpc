package com.xizhe;

import com.xizhe.discovery.RegistryConfig;
import com.xizhe.proxy.handler.RpcConsumerInvocationHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

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
        Object helloProxy = Proxy.newProxyInstance(classLoader, classes,
                new RpcConsumerInvocationHandler(registryConfig.getRegistry(),interfaceRef));
        return (T) helloProxy;
    }



}
