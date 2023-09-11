package com.xizhe.protection;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/11 19:14
 */
public interface RateLimiter {
    boolean allowRequest();
}
