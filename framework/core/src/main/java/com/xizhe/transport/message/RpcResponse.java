package com.xizhe.transport.message;

import lombok.*;

import java.io.Serializable;

/**
 * @author admin
 * @version 1.0
 * @description: 响应报文
 * @date 2023/9/8 15:25
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RpcResponse implements Serializable {

    private Long requestId;

    private byte compressType;

    private byte serializeType;

    private byte responseCode;

    private Object body;

    private Long timestamp;


}
