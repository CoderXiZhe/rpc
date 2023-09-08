package com.xizhe.enumeration;




public enum ResponseCode {

    SUCCESS((byte) 1,"成功"),
    FAIL((byte)2 ,"失败");

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
