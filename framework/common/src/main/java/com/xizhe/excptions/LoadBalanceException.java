package com.xizhe.excptions;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 20:00
 */

public class LoadBalanceException extends RuntimeException{
    public LoadBalanceException(String message) {
        super(message);
    }

    public LoadBalanceException() {
        super();
    }

    public LoadBalanceException(Throwable cause) {
        super(cause);
    }
}
