package com.xizhe.serialize;


public enum SerializerType {
    JDK((byte) 1,"jdk"),
    HESSIAN((byte) 2, "hessian");

    private byte type;

    private String name;

    SerializerType(byte type, String name) {
        this.type = type;
        this.name = name;
    }

    public static byte getTypeByName(String compressName) {
        SerializerType[] values = SerializerType.values();
        for (SerializerType value : values) {
            if(compressName.equals(value.name)) {
                return value.getType();
            }
        }
        throw new RuntimeException("获取序列化器出现异常,不存在对应序列化器!");
    }


    public byte getType() {
        return type;
    }


    public String getName() {
        return name;
    }

    public static String getNameByType(byte type) {
        SerializerType[] values = SerializerType.values();
        for (SerializerType value : values) {
            if(type == value.type) {
                return value.getName();
            }
        }
        throw new RuntimeException("获取序列化器出现异常,不存在对应序列化器!");
    }

}
