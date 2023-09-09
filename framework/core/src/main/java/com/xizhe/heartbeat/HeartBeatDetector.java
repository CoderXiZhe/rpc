package com.xizhe.heartbeat;

import com.xizhe.RpcBootstrap;
import com.xizhe.discovery.NettyBootstrapInitializer;
import com.xizhe.enumeration.RequestType;
import com.xizhe.transport.message.RpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;


import java.net.InetSocketAddress;

import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/9 15:39
 */
@Slf4j
public class HeartBeatDetector {
    public static void detectHeartBeat(String serviceName) {
        // 拉取所有服务
        List<InetSocketAddress> addresses = RpcBootstrap.getInstance().getRegistry().discovery(serviceName);
        for (InetSocketAddress address : addresses) {
            try {
                // 对连接进行缓存
                if (!RpcBootstrap.CHANNEL_CACHE.containsKey(address)) {
                    Channel channel = NettyBootstrapInitializer.getBootstrap().connect(address).sync().channel();
                    RpcBootstrap.CHANNEL_CACHE.put(address,channel);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // 定时发送消息
            Thread t = new Thread(() -> {
                new Timer().scheduleAtFixedRate(new MyTimerTask(),2000,2000);
            },"rpc-heartbeat-detector");
            t.setDaemon(true);
            t.start();

        }
    }

    private static class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            Set<InetSocketAddress> addresses = RpcBootstrap.CHANNEL_CACHE.keySet();
            for (InetSocketAddress address : addresses) {
                RpcRequest request = RpcRequest.builder()
                        .requestId(RpcBootstrap.ID_GENERATOR.getId())
                        .requestType(RequestType.HEART_BEAT.getId())
                        .compressType(RpcBootstrap.COMPRESS_TYPE)
                        .serializeType(RpcBootstrap.SERIALIZE_TYPE)
                        .timestamp(System.currentTimeMillis())
                        .build();
                Channel channel = RpcBootstrap.CHANNEL_CACHE.get(address);
                CompletableFuture<Object> future = new CompletableFuture<>();
                // 将future暴露出去 等到服务端提供响应时候调用complete方法
                RpcBootstrap.PENDING_REQUEST.put(request.getRequestId(),future);
                channel.writeAndFlush(request)
                        .addListener((ChannelFutureListener) promise -> {
                            // 当数据已经写完 promise就结束了
                            // 我们需要的是 数据写完后 服务端给的返回值
                            if(!promise.isSuccess()) {
                                future.completeExceptionally(promise.cause());
                            }
                        });
                // 阻塞获取服务端提供的响应
                Long endTime = null;
                try {
                    endTime = (Long) future.get(5, TimeUnit.SECONDS);
                    Long time = endTime -  request.getTimestamp();
                    RpcBootstrap.ANSWER_TIME_CACHE.put(time,channel);
                    log.debug("客户端和服务器【{}】的响应时间【{}】",address,time);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}
