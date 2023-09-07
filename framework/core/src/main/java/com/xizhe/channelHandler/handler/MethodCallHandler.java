package com.xizhe.channelHandler.handler;

import com.xizhe.RpcBootstrap;
import com.xizhe.ServiceConfig;
import com.xizhe.transport.message.RequestPayload;
import com.xizhe.transport.message.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        RequestPayload requestPayload = rpcRequest.getRequestPayload();
        Object result = callTargetMethod(requestPayload);
        // 封装响应

        // 写出响应
        channelHandlerContext.channel().writeAndFlush(result);
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
