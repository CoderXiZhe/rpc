package com.xizhe;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 17:12
 */

public class ReferenceConfig<T> {
    private Class<T> interfaceRef;

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
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(" hello proxy");
                return null;
            }
        });
        return (T) helloProxy;
    }
}
