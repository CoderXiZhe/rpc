package com.xizhe.channelHandler.handler;

import com.xizhe.RpcBootstrap;
import com.xizhe.enumeration.ResponseCode;
import com.xizhe.excptions.ResponseException;
import com.xizhe.protection.CircuitBreaker;
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

        byte responseCode = response.getResponseCode();
        Object body = response.getBody();
        CompletableFuture<Object> future = RpcBootstrap.PENDING_REQUEST.get(response.getRequestId());
        CircuitBreaker circuitBreaker = RpcBootstrap.getInstance().getConfiguration()
                .getEveryIpRateCircuitBreaker().get(channelHandlerContext.channel().remoteAddress());
        if(responseCode == ResponseCode.FAIL.getId()) {
            circuitBreaker.recordException();
            future.complete(null);
            log.error("请求【{}】失败", response.getRequestId());
            throw new ResponseException(responseCode,ResponseCode.FAIL.getType());
        }else if(responseCode == ResponseCode.SUCCESS.getId()) {
            log.debug("客户端收到响应:{}", body);
            future.complete(body);
        }else if(responseCode == ResponseCode.RATE_LIMIT.getId()) {
            circuitBreaker.recordException();
            future.complete(null);
            log.error("请求【{}】被限流", response.getRequestId());
            throw new ResponseException(responseCode,ResponseCode.RATE_LIMIT.getType());
        }else if(responseCode == ResponseCode.SUCCESS_HEART_BEAT.getId()) {
            // 心跳请求
            future.complete(response.getTimestamp());
        }else if(responseCode == ResponseCode.RESOURCE_NOT_FOUND.getId()) {
            circuitBreaker.recordException();
            future.complete(null);
            log.error("请求【{}】找不到资源", response.getRequestId());
            throw new ResponseException(responseCode,ResponseCode.RESOURCE_NOT_FOUND.getType());
        }

    }
}
