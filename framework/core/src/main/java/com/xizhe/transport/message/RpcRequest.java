package com.xizhe.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 * @version 1.0
 * @description: 服务调用方发送的请求内容
 * @date 2023/9/7 11:26
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest {

    /**
     * 请求id
     */
    private Long requestId;

    /**
     * 请求类型
     */
    private byte requestType;

    /**
     * 压缩类型
     */
    private byte compressType;

    /**
     * 序列化类型
     */
    private byte serializeType;

    /**
     * 请求体
     */
    private RequestPayload requestPayload;

    private Long timestamp;

}
