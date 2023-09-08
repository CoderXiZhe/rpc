package com.xizhe.serialize;

/**
 * @author admin
 * @version 1.0
 * @description: 序列化和反序列化接口
 * @date 2023/9/8 19:13
 */
public interface Serializer {

    /**
     * 序列化抽象方法
     * @param object 待序列化对象
     * @return 序列化后字节数组
     */
    byte[] serialize(Object object);

    /**
     * 反序列化抽象方法
     * @param bytes 待反序列化的字节数组
     * @param clazz 目标类的class对象
     * @param <T> 目标类泛型
     * @return 反序列化后的实例
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);


}
