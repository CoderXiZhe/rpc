package com.xizhe.excptions;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 20:00
 */

public class DiscoveryException extends RuntimeException{
    public DiscoveryException(String message) {
        super(message);
    }

    public DiscoveryException() {
        super();
    }

    public DiscoveryException(Throwable cause) {
        super(cause);
    }
}
