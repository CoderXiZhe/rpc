package com.xizhe.compress;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.xizhe.excptions.CompressException;
import com.xizhe.excptions.SerializeException;
import com.xizhe.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/8 19:22
 */

@Slf4j
public class GzipCompressor implements Compressor {



    @Override
    public byte[] compress(byte[] bytes) {
        if(bytes == null || bytes.length == 0) {
            return new byte[0];
        }
        ByteOutputStream bos = new ByteOutputStream();
        GZIPOutputStream gzipOutputStream = null;
        try {
            gzipOutputStream = new GZIPOutputStream(bos);
            gzipOutputStream.write(bytes);
            gzipOutputStream.finish();
            return  bos.toByteArray();
        } catch (IOException e) {
            log.error("压缩异常!byte:{}",bytes);
            throw new CompressException(e);
        }

    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if(bytes == null || bytes.length == 0) {
            return new byte[0];
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipInputStream = null;
        try {
            gzipInputStream = new GZIPInputStream(bais);
            byte[] buffer = new byte[1024];
            int offset = 0;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while(( offset = gzipInputStream.read(buffer)) != -1) {
                out.write(buffer,0,offset);
            }
            return out.toByteArray();
        } catch (IOException e) {
            log.error("解压缩异常!byte:{}",bytes);
            throw new CompressException(e);
        }
    }
}
