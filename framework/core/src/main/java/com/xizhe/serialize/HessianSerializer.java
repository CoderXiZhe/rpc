package com.xizhe.serialize;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/8 20:13
 */

public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {

        if (object == null) {
            return new byte[0];
        }
        byte[] result = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(byteArrayOutputStream);
        try {
            hessian2Output.startMessage();
            hessian2Output.writeObject(object);
            hessian2Output.flush();
            hessian2Output.completeMessage();
            result = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != hessian2Output) {
                    hessian2Output.close();
                    byteArrayOutputStream.close();
                }
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes.length == 0 || clazz == null) {
            return null;
        }
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        Hessian2Input hessian2Input = new Hessian2Input(byteInputStream);
        T object = null;
        try {
            hessian2Input.startMessage();
            object = (T) hessian2Input.readObject();
            hessian2Input.completeMessage();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                hessian2Input.close();
                byteInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return (T) object;
        }
    }

    public static void main(String[] args) {
        String s = "你好 世界 hello world";
        Serializer serializer = new HessianSerializer();
        byte[] serialize = serializer.serialize(s);
        System.out.println(serializer.deserialize(serialize, String.class));
    }
}
