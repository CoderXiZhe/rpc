package com.xizhe.proxy.handler;

import com.xizhe.RpcBootstrap;
import com.xizhe.discovery.NettyBootstrapInitializer;
import com.xizhe.discovery.Registry;
import com.xizhe.enumeration.RequestType;
import com.xizhe.excptions.NetWorkException;
import com.xizhe.transport.message.RequestPayload;
import com.xizhe.transport.message.RpcRequest;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author admin
 * @version 1.0
 * @description: 封装客户端通信的基础逻辑，每一个代理对象的远程调用都封装在invoke方法中
 *  发现服务 -> 建立连接 -> 发送请求 -> 得到结果
 * @date 2023/9/6 15:43
 */

@Slf4j
public class RpcConsumerInvocationHandler implements InvocationHandler {
    private Registry registry;

    private Class<?> interfaceRef;

    public RpcConsumerInvocationHandler() {
    }

    public RpcConsumerInvocationHandler(Registry registry, Class<?> interfaceRef) {
        this.registry = registry;
        this.interfaceRef = interfaceRef;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("method --> {}",method.getName());
        log.info("args --> {}",args);

        // 1. 发现服务 从注册中心寻找一个可用服务
        InetSocketAddress address = registry.discovery(interfaceRef.getName());
        log.debug("consumer发现服务【{}】可用主机:{}",interfaceRef.getName(),address);
        // 2. 获取可用channel
        Channel channel = getAvailableChannel(address);
        log.debug("获取了与【{}】建立的连接通道,准备发送数据",address.toString());
        // 3. 封装报文
        RequestPayload requestPayload = RequestPayload.builder()
                .interfaceName(interfaceRef.getName())
                .method(method.getName())
                .parameterType(method.getParameterTypes())
                .parameterValue(args)
                .returnType(method.getReturnType())
                .build();

        RpcRequest request = RpcRequest.builder()
                .requestId(1L)
                .requestType(RequestType.REQUEST.getId())
                .compressType((byte) 1)
                .serializeType((byte) 1)
                .requestPayload(requestPayload)
                .build();

        CompletableFuture<Object> future = new CompletableFuture<>();
        // 将future暴露出去 等到服务端提供响应时候调用complete方法
        RpcBootstrap.PENDING_REQUEST.put(1L,future);
        channel.writeAndFlush(request)
                .addListener((ChannelFutureListener) promise -> {
            // 当数据已经写完 promise就结束了
            // 我们需要的是 数据写完后 服务端给的返回值
            if(!promise.isSuccess()) {
                future.completeExceptionally(promise.cause());
            }
        });
        // 阻塞获取服务端提供的响应
        return future.get(10,TimeUnit.SECONDS);
    }



    private Channel getAvailableChannel(InetSocketAddress address) {
        // 从缓存中读取channel
        Channel channel = RpcBootstrap.CHANNEL_CACHE.get(address);
        if (channel == null) {
            // 同步操作: await 会阻塞直到连接成功返回
            // Channel channelNew = NettyBootstrapInitializer.getBootstrap()
            //              .connect(address).await().channel();

            // 异步操作: addListener,通过CompletableFuture获取子线程操作结果
            CompletableFuture<Channel> channelFuture = new CompletableFuture<>();
            NettyBootstrapInitializer.getBootstrap().connect(address).addListener(
                    (ChannelFutureListener) promise -> {
                        if (promise.isDone()) {
                            channelFuture.complete(promise.channel());
                        }else if(!promise.isSuccess()) {
                            channelFuture.completeExceptionally(promise.cause());
                        }
                    });
            // 阻塞获取channel
            try {
                channel = channelFuture.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("获取通道时发生异常,address:{}",address);
                throw new NetWorkException("获取channel发生异常!");
            }
            if (channel == null) {
                log.error("获取通道时发生异常,address:{}",address);
                throw new NetWorkException("获取channel发生异常!");
            }
            RpcBootstrap.CHANNEL_CACHE.put(address,channel);
        }
        return channel;
    }
}
