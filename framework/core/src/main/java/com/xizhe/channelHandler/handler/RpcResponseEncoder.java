package com.xizhe.channelHandler.handler;

import com.xizhe.MessageFormatConstant;
import com.xizhe.enumeration.RequestType;
import com.xizhe.serialize.JdkSerializer;
import com.xizhe.serialize.Serializer;
import com.xizhe.serialize.SerializerFactory;
import com.xizhe.serialize.SerializerType;
import com.xizhe.transport.message.RequestPayload;
import com.xizhe.transport.message.RpcRequest;
import com.xizhe.transport.message.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author admin
 * @version 1.0
 * @description:  消息出站经过的第一个handler
 * @date 2023/9/7 12:00
 */

/**
 * magic(4B) version(1B) headLength(2B) fullLength(4B) code(1B)
 * serializeType(1B) compressType(1B) RequestId(8B) body(?)
 */

@Slf4j
public class RpcResponseEncoder extends MessageToByteEncoder<RpcResponse> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          RpcResponse rpcResponse, ByteBuf byteBuf) throws Exception {
        byte serializeType = rpcResponse.getSerializeType();
        byte[] bodyBytes = getBodyBytes(rpcResponse.getBody(),serializeType);
        // 魔术值 4B 内容: lrpc
        byteBuf.writeBytes(MessageFormatConstant.MAGIC);
        // 版本 1B 内容：1
        byteBuf.writeByte(MessageFormatConstant.VERSION);
        // 头部大小 2B , 值：22
        byteBuf.writeShort(MessageFormatConstant.HEADER_LENGTH);
        // 总长度 header + body
        byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH + bodyBytes.length);
        // 响应码 1B
        byteBuf.writeByte(rpcResponse.getResponseCode());
        // 序列化类型 1B
        byteBuf.writeByte(serializeType);
        // 压缩类型 1B
        byteBuf.writeByte(rpcResponse.getCompressType());
        // 请求id 8B
        byteBuf.writeLong(rpcResponse.getRequestId());
        // body 不是null 才写
        if(rpcResponse.getBody() != null) {
            byteBuf.writeBytes(bodyBytes);
        }

        log.debug("请求【{}】的响应已在服务端完成编码",rpcResponse.getRequestId());


    }

    private byte[] getBodyBytes(Object body, byte serializeType) {
        Serializer serializer = SerializerFactory.getSerializer(SerializerType.getNameByType(serializeType));
        return serializer.serialize(body);

//
//        if (body == null) {
//            return new byte[0];
//        }
//        // object 序列化成字节数组
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
//            outputStream.writeObject(body);
//            // todo 压缩
//            return baos.toByteArray();
//        } catch (IOException e) {
//            log.error("序列化时发生异常,body:{}",body.toString());
//            throw new RuntimeException();
//        }

    }


}
