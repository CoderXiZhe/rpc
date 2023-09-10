package com.xizhe;


import com.xizhe.channelHandler.ConsumerChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 * @version 1.0
 * @description: 封装客户端Bootstrap,避免每次调用重新创建
 * @date 2023/9/6 12:42
 */

@Slf4j
public class NettyBootstrapInitializer {
    private static Bootstrap bootstrap = new Bootstrap();

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = bootstrap.group(group)
                // 选择初始化一个怎样的channel
                .channel(NioSocketChannel.class)
                .handler(new ConsumerChannelInitializer());
    }

    private NettyBootstrapInitializer() {}

    public static Bootstrap getBootstrap() {
        return bootstrap;
    }
}
