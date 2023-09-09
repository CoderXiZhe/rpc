package com.xizhe.compress;


public enum CompressType {
    GZIP((byte) 1,"gzip");

    private byte type;

    private String name;

    CompressType(byte type, String name) {
        this.type = type;
        this.name = name;
    }

    public byte getType() {
        return type;
    }


    public String getName() {
        return name;
    }

    public static String getNameByType(byte type) {
        CompressType[] values = CompressType.values();
        for (CompressType value : values) {
            if(type == value.type) {
                return value.getName();
            }
        }
        throw new RuntimeException("获取压缩器出现异常,不存在对应压缩器!");
    }

}
