package com.xizhe.excptions;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 20:00
 */

public class ResponseException extends RuntimeException{
    private byte code;
    private String msg;
    public ResponseException(String message) {
        super(message);
    }

    public ResponseException() {
        super();
    }

    public ResponseException(byte code,String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
