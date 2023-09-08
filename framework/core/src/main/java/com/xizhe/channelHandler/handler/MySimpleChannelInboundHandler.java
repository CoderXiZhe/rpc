package com.xizhe.channelHandler.handler;

import com.xizhe.RpcBootstrap;
import com.xizhe.transport.message.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/6 16:17
 */

@Slf4j
public class MySimpleChannelInboundHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
        // 服务端响应的结果
        log.debug("客户端收到响应:{}", response.getBody());
        CompletableFuture<Object> future = RpcBootstrap.PENDING_REQUEST.get(response.getRequestId());
        future.complete(response.getBody());
    }
}
