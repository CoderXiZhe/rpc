package com.xizhe.enumeration;




public enum ResponseCode {

    SUCCESS((byte) 1,"成功"),
    FAIL((byte)2 ,"失败"),
    SUCCESS_HEART_BEAT((byte) 3, "心跳检测成功"),
    RESOURCE_NOT_FOUND((byte) 4,"请求资源不存在"),
    RATE_LIMIT((byte) 5,"服务被限流");

    private byte code;
    private String type;

    public byte getId() {
        return code;
    }

    public String getType() {
        return type;
    }

    ResponseCode(byte code, String type) {
        this.code = code;
        this.type = type;
    }
}
