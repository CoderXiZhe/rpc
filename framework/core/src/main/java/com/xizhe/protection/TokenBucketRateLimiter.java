package com.xizhe.protection;

import java.util.Random;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/11 16:11
 */

public class TokenBucketRateLimiter implements RateLimiter{

    private int tokens;

    private int capacity;

    private int rate;

    private long lastTokenTime;

    public TokenBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
        lastTokenTime = System.currentTimeMillis();
        this.tokens = capacity;
    }

    public synchronized boolean allowRequest() {
        long current = System.currentTimeMillis();
        long interval = current - lastTokenTime;
        int count = (int) (interval * rate/1000);
        // 生成令牌
        this.tokens = Math.min(capacity,count + this.tokens);
        if(count > 0) {
            lastTokenTime = System.currentTimeMillis();
        }
        if(this.tokens > 0) {
            this.tokens--;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(10, 10);
        for (int i = 0; i < 100; i++) {
            Thread.sleep( 10);
            boolean b = limiter.allowRequest();
            System.out.println("allowRequest : " +b);

        }

    }



}
