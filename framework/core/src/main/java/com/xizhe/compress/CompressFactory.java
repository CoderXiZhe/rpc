package com.xizhe.compress;

import com.xizhe.excptions.SerializeException;
import com.xizhe.serialize.HessianSerializer;
import com.xizhe.serialize.JdkSerializer;
import com.xizhe.serialize.Serializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/8 19:42
 */

public class CompressFactory {
    private static final Map<String, Compressor> COMPRESSOR_MAP = new ConcurrentHashMap<>(16);

    static {
        COMPRESSOR_MAP.put("gzip",new GzipCompressor());
        //
    }

    public static Compressor getCompressor(String type) {
        Compressor compressor = COMPRESSOR_MAP.get(type);
        if(compressor == null) {
            throw new SerializeException("未找到相应压缩器!");
        }
        return compressor;
    }

}
