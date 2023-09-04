package com.xizhe;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 17:08
 */

public class ServiceConfig<T> {
    private Class<T> interfaceProvider;
    private Object ref;

    public Class<T> getInterface() {
        return interfaceProvider;
    }

    public void setInterface(Class<T> interfaceProvider) {
        this.interfaceProvider = interfaceProvider;
    }

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }
}
