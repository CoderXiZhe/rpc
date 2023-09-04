package com.xizhe.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/8/22 16:25
 */

public class AppClient {
    public void run() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            // 启动一个客户端需要一个辅助类
            Bootstrap bootstrap = new Bootstrap();
            bootstrap = bootstrap.group(group)
                    .remoteAddress(new InetSocketAddress(8089))
                    // 选择初始化一个怎样的channel
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MyChannelHandler2());
                        }
                    });
            // 尝试连接服务器
            ChannelFuture channelfuture = null;
            channelfuture = bootstrap.connect().sync();
            // 获取channel 并且写出数据
            channelfuture.channel()
                    .writeAndFlush(Unpooled.copiedBuffer("hello netty".getBytes("utf-8")));
            // 阻塞程序 等到接受消息
            channelfuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        new AppClient().run();
    }
}
