package com.xizhe.protection;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author admin
 * @version 1.0
 * @description: 简单熔断器(正常熔断器应该有3种状态: 开启、断开、半开)
 * 当触发熔断器时应该休眠一段时间,然后重置状态为半开,释放部分请求查看请求异常状态从而进一步判断是否继续开启熔断器
 * @date 2023/9/11 17:37
 */

public class CircuitBreaker {

    private volatile boolean isOpen = false;

    private AtomicInteger totalRequest = new AtomicInteger(0);

    private AtomicInteger errorRequest = new AtomicInteger(0);

    private int maxErrorRequest;
    private float maxErrorRate;

    public CircuitBreaker(int maxErrorRequest, float maxErrorRate) {
        this.maxErrorRequest = maxErrorRequest;
        this.maxErrorRate = maxErrorRate;
    }

    public boolean isBreak() {
        if(isOpen) {
            return true;
        }
        if(errorRequest.get() > maxErrorRequest) {
            this.isOpen = true;
            return true;
        }
        if(errorRequest.get() > 0 && totalRequest.get() > 0 &&
            errorRequest.get()/(float)totalRequest.get() > maxErrorRate) {
            this.isOpen = true;
            return true;
        }
        return false;
    }

    public void recordRequest() {
        this.totalRequest.incrementAndGet();
    }

    public void recordException() {
        this.errorRequest.incrementAndGet();
    }

    /**
     * 重置熔断器状态
     */
    public void reset() {
        this.isOpen = false;
        this.totalRequest.set(0);
        this.errorRequest.set(0);
    }
}
