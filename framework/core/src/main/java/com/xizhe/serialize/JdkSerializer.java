package com.xizhe.serialize;

import com.xizhe.excptions.SerializeException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/8 19:22
 */

@Slf4j
public class JdkSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return new byte[0];
        }
        // object 序列化成字节数组
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(object);
            // todo 压缩
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("序列化时发生异常,object:{}",object);
            throw new SerializeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if(bytes.length == 0 || clazz == null ) {
            return null;
        }
        try(ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais)
        ) {
            Object object = ois.readObject();
            return (T) object;
        } catch (IOException | ClassNotFoundException e) {
            log.error("反序列化出现异常");
            throw new SerializeException(e);
        }
    }
}
