package com.xizhe.excptions;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 20:00
 */

public class SerializeException extends RuntimeException{
    public SerializeException(String message) {
        super(message);
    }

    public SerializeException() {
        super();
    }

    public SerializeException(Throwable cause) {
        super(cause);
    }
}
