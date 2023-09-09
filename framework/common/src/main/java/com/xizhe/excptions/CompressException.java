package com.xizhe.excptions;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 20:00
 */

public class CompressException extends RuntimeException{
    public CompressException(String message) {
        super(message);
    }

    public CompressException() {
        super();
    }

    public CompressException(Throwable cause) {
        super(cause);
    }
}
