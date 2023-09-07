package com.xizhe.enumeration;

public enum RequestType {

    REQUEST((byte) 1,"普通请求"),
    HEART_BEAT((byte)2 ,"心跳请求");

    private byte id;
    private String type;

    public byte getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    RequestType(byte id, String type) {
        this.id = id;
        this.type = type;
    }
}
