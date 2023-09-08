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
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/7 13:15
 */

@Slf4j
public class RpcResponseDecoder extends LengthFieldBasedFrameDecoder {
    public RpcResponseDecoder() {
        super(
                // 最大帧的长度
                MessageFormatConstant.MAX_FRAME_LENGTH,
                // 长度字段的偏移量
                MessageFormatConstant.MAGIC.length
                        + MessageFormatConstant.VERSION_LENGTH
                        + MessageFormatConstant.HEADER_FIELD_LENGTH,
                // 长度字段所占长度
                MessageFormatConstant.FULL_FIELD_LENGTH,
                // 负载的适配长度
                -(MessageFormatConstant.MAGIC.length + MessageFormatConstant.VERSION_LENGTH
                    + MessageFormatConstant.HEADER_FIELD_LENGTH + MessageFormatConstant.FULL_FIELD_LENGTH ),
                0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if(decode instanceof ByteBuf ) {
            ByteBuf byteBuf = (ByteBuf) decode;
            return decodeFrame(byteBuf);
        }
        throw new RuntimeException();
    }

    private Object decodeFrame(ByteBuf byteBuf) {
        // magic(4B) version(1B) headLength(2B) fullLength(4B) requestType(1B)
        // serializeType(1B) compressType(1B) RequestId(8B) body(?)

        // 1. 解析魔术
        byte[] magic = new byte[MessageFormatConstant.MAGIC.length];
        byteBuf.readBytes(magic);
        for (int i = 0; i < magic.length; i++) {
            if(magic[i] != MessageFormatConstant.MAGIC[i]) {
                throw new RuntimeException("请求不合法!");
            }
        }
        // 2. 解析版本
        byte version = byteBuf.readByte();
        if(version > MessageFormatConstant.VERSION) {
            throw new RuntimeException("不支持该请求版本！");
        }
        // 3. 解析头部长度
        short headerLength = byteBuf.readShort();
        // 4. 解析总长度
        int fullLength = byteBuf.readInt();
        // 5. 解析响应码
        byte responseCode = byteBuf.readByte();
        // 6. 解析序列化类型
        byte serializeType = byteBuf.readByte();
        // 7. 解析压缩类型
        byte compressType = byteBuf.readByte();
        // 8. 解析请求id
        long requestId = byteBuf.readLong();
        // 9. 解析body
        byte[] body = new byte[fullLength - headerLength];
        byteBuf.readBytes(body);

        RpcResponse response = RpcResponse.builder()
                .serializeType(serializeType)
                .responseCode(responseCode)
                .compressType(compressType)
                .requestId(requestId)
                .build();

        // todo 心跳响应

        // 10. 对body进行解压缩

        // 11. 对body进行反序列化

        Serializer serializer = SerializerFactory.getSerializer(SerializerType.getNameByType(serializeType));
        Object object = serializer.deserialize(body, Object.class);
        response.setBody(object);
        log.debug("请求【{}】的响应已在客户端完成解码",response.getRequestId());
        return response;


//        try( ByteArrayInputStream bais = new ByteArrayInputStream(body);
//                ObjectInputStream ois = new ObjectInputStream(bais)
//        ) {
//            Object object = ois.readObject();
//            response.setBody(object);
//        } catch (IOException | ClassNotFoundException e) {
//            log.error("对应请求【{}】的响应反序列化出现异常:",requestId,e);
//        }
//        log.debug("请求【{}】的响应已在客户端完成解码",response.getRequestId());
//        return response;
    }
}
