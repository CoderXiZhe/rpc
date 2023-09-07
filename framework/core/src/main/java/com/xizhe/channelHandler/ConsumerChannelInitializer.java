package com.xizhe.channelHandler;

import com.xizhe.channelHandler.handler.MySimpleChannelInboundHandler;
import com.xizhe.channelHandler.handler.RpcMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author admin
 * @version 1.0
 * @description: 客户端Channel初始化器
 * @date 2023/9/6 16:26
 */

public class ConsumerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                // 出站 netty自带log
                .addLast(new LoggingHandler(LogLevel.DEBUG))
                // 出站 编码器
                .addLast(new RpcMessageEncoder())
                // 入站
                .addLast(new MySimpleChannelInboundHandler());
    }
}
