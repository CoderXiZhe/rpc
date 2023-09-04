package com.xizhe;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/8/22 15:21
 */

public class test {
    @Test
    public void testcompositeBuffer() {
        ByteBuf header = Unpooled.buffer();
        ByteBuf body = Unpooled.buffer();

        CompositeByteBuf byteBuf = Unpooled.compositeBuffer();
        byteBuf.addComponents(header,body);


    }

    @Test
    public void test4Bootstrap() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap = bootstrap.group(group)
                .remoteAddress(new InetSocketAddress(8088))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                    }
                });
        // 尝试连接服务器
        ChannelFuture channelfuture = null;
        try {
            channelfuture = bootstrap.connect().sync();
            // 获取channel 并且写出数据
            channelfuture.channel().writeAndFlush(Unpooled.copiedBuffer("hello netty".getBytes("urf-8")));
            // 阻塞程序 等到接受消息
            channelfuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void compress() throws IOException {
        byte[] by = new byte[]{11,2,3,12,33,3,33,2,3,12,33,3,33,3,12,33,3,33,2,3,12,33,3,
                33,3,12,33,3,33,2,3,12,33,3,33,3,12,33,3,33,2,3,12,33
                ,3,33,3,12,33,3,33,2,3,12,33,3,33,3,12,33,3,33,2,3,12,33,3,33};
        ByteOutputStream bos = new ByteOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bos);
        gzipOutputStream.write(by);
        gzipOutputStream.finish();

        byte[] bytes = bos.toByteArray();
        System.out.println("原始大小: " +by.length + " ------>压缩后大小: " + bytes.length);
        System.out.println(Arrays.toString(bytes));
    }

    @Test
    public void decompress() throws IOException {
        byte[] by = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, 0, -29, 102, 98, -26
                , 81, 100, 86, -124, -112, 100, 50, 1, -79, -102, 17, -67, 68, 0, 0, 0};
        ByteArrayInputStream bais = new ByteArrayInputStream(by);
        GZIPInputStream gzipInputStream = new GZIPInputStream(bais);
        byte[] buffer = new byte[1024];
        int offset = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while(( offset = gzipInputStream.read(buffer)) != -1) {
            out.write(buffer,0,offset);
        }
        byte[] bytes = out.toByteArray();
        System.out.println(Arrays.toString(bytes));
    }

}
