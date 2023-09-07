package com.xizhe.channelHandler.handler;

import com.xizhe.MessageFormatConstant;
import com.xizhe.enumeration.RequestType;
import com.xizhe.transport.message.RequestPayload;
import com.xizhe.transport.message.RpcRequest;
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
 * magic(4B) version(1B) headLength(2B) fullLength(4B) requestType(1B)
 * serializeType(1B) compressType(1B) RequestId(8B) body(?)
 */

@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcRequest> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          RpcRequest rpcRequest, ByteBuf byteBuf) throws Exception {
        byte[] bodyBytes = getBodyBytes(rpcRequest.getRequestPayload());
        // 魔术值 4B 内容: lrpc
        byteBuf.writeBytes(MessageFormatConstant.MAGIC);
        // 版本 1B 内容：1
        byteBuf.writeByte(MessageFormatConstant.VERSION);
        // 头部大小 2B , 值：22
        byteBuf.writeShort(MessageFormatConstant.HEADER_LENGTH);
        // 总长度 header + body
        byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH + bodyBytes.length);
        // 请求类型 1B
        byteBuf.writeByte(rpcRequest.getRequestType());
        // 序列化类型 1B
        byteBuf.writeByte(rpcRequest.getSerializeType());
        // 压缩类型 1B
        byteBuf.writeByte(rpcRequest.getCompressType());
        // 请求id 8B
        byteBuf.writeLong(rpcRequest.getRequestId());
        // body 不是心跳请求 才需要写body
        if(rpcRequest.getRequestType() != RequestType.HEART_BEAT.getId()) {
            byteBuf.writeBytes(bodyBytes);
        }


    }

    private byte[] getBodyBytes(RequestPayload requestPayload) {
        // 针对不同的消息类型作处理（心跳没有payload）
        if (requestPayload == null) {
            return new byte[0];
        }
        // object 序列化成字节数组
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(requestPayload);
            // todo 压缩
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("序列化时发生异常,requestPayload:{}",requestPayload.toString());
            throw new RuntimeException();
        }

    }


}
