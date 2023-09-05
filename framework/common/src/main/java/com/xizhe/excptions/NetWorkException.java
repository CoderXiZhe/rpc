package com.xizhe.excptions;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 20:00
 */

public class NetWorkException extends RuntimeException{
    public NetWorkException() {
        super();
    }

    public NetWorkException(String message) {
        super(message);
    }

    public NetWorkException(Throwable cause) {
        super(cause);

    }
}
