package com.xizhe.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/8/22 16:39
 */

public class MyChannelHandler2 extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf  = (ByteBuf) msg;
        System.out.println("客户端收到消息:------------>" + byteBuf.toString(StandardCharsets.UTF_8));
        // 可以通过ctx 获取channel
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}


