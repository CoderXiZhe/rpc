package com.xizhe.channelHandler.handler;

import com.xizhe.RpcBootstrap;
import com.xizhe.ServiceConfig;
import com.xizhe.enumeration.RequestType;
import com.xizhe.enumeration.ResponseCode;
import com.xizhe.protection.RateLimiter;
import com.xizhe.protection.TokenBucketRateLimiter;
import com.xizhe.transport.message.RequestPayload;
import com.xizhe.transport.message.RpcRequest;
import com.xizhe.transport.message.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Map;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/7 14:32
 */

@Slf4j
public class MethodCallHandler extends SimpleChannelInboundHandler<RpcRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {

        // 封装部分响应
        RpcResponse response = RpcResponse.builder()
                .compressType(rpcRequest.getCompressType())
                .requestId(rpcRequest.getRequestId())
                .serializeType(rpcRequest.getSerializeType())
                .timestamp(System.currentTimeMillis())
                .build();
        SocketAddress socketAddress = channelHandlerContext.channel().remoteAddress();
        Map<SocketAddress, RateLimiter> everyIpRateLimiter = RpcBootstrap.getInstance().getConfiguration().getEveryIpRateLimiter();
        RateLimiter rateLimiter = everyIpRateLimiter.get(socketAddress);
        if (rateLimiter == null) {
            rateLimiter = new TokenBucketRateLimiter(5,1);
            everyIpRateLimiter.put(socketAddress,rateLimiter);
        }

        if(!rateLimiter.allowRequest()) {
            response.setResponseCode(ResponseCode.RATE_LIMIT.getId());
        }else if(rpcRequest.getRequestType() == RequestType.HEART_BEAT.getId()) {
            // 心跳类型 直接写回
            response.setResponseCode(ResponseCode.SUCCESS_HEART_BEAT.getId());
        }else {
            RequestPayload requestPayload = rpcRequest.getRequestPayload();
            Object result = null;
            try {
                result = callTargetMethod(requestPayload);
                response.setResponseCode(ResponseCode.SUCCESS.getId());
                response.setBody(result);
            } catch (Exception e) {
               response.setResponseCode(ResponseCode.FAIL.getId());
            }
        }
        channelHandlerContext.channel().writeAndFlush(response);

    }

    private Object callTargetMethod(RequestPayload requestPayload) {
        String interfaceName = requestPayload.getInterfaceName();
        String method = requestPayload.getMethod();
        Class<?>[] parameterType = requestPayload.getParameterType();
        Object[] parameterValue = requestPayload.getParameterValue();
        // 从服务列表缓存中获取服务
        ServiceConfig<?> serviceConfig = RpcBootstrap.SERVER_LIST.get(interfaceName);
        Object ref = serviceConfig.getRef();
        Method targetMethod = null;
        Object result = null;
        try {
            Class<?> targetInterface = ref.getClass();
            targetMethod = targetInterface.getMethod(method, parameterType);
            result = targetMethod.invoke(ref, parameterValue);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("执行【{}】目标方法【{}】发生异常",interfaceName,method,e);
            throw new RuntimeException("执行目标方法发生异常");
        }
        return result;
    }
}
