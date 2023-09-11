package com.xizhe.config;

import com.xizhe.IdGenerator;
import com.xizhe.ProtocolConfig;
import com.xizhe.compress.CompressType;
import com.xizhe.compress.Compressor;
import com.xizhe.discovery.RegistryConfig;
import com.xizhe.loadbalance.LoadBalancer;
import com.xizhe.protection.CircuitBreaker;
import com.xizhe.protection.RateLimiter;
import com.xizhe.serialize.Serializer;
import com.xizhe.serialize.SerializerType;
import com.xizhe.spi.SpiResolver;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author admin
 * @version 1.0
 * @description: 全局配置类 : 代码配置 -> xml配置 -> 默认项
 * @date 2023/9/10 16:19
 */

@Data
@Slf4j
public class Configuration {

    private int port = 8099;

    private String appName;

    private ProtocolConfig protocolConfig;

    private RegistryConfig registryConfig = new RegistryConfig("zookeeper://127.0.0.1:2181");

    // id生成器
    private IdGenerator idGenerator = new IdGenerator(1,2);

    // 默认使用jdk方式进行序列化
    private byte serializeType = SerializerType.JDK.getType();

    // 默认使用gzip进行压缩
    private byte compressType = CompressType.GZIP.getType();

    private Serializer serializer;

    private Compressor compressor;

    private LoadBalancer loadBalancer;

    private Map<SocketAddress, RateLimiter> everyIpRateLimiter = new ConcurrentHashMap<>();

    private Map<SocketAddress, CircuitBreaker> everyIpRateCircuitBreaker = new ConcurrentHashMap<>();

    public Configuration() {
        // spi 发现相关配置
        SpiResolver spiResolver = new SpiResolver();
        spiResolver.loadFromSpi(this);

        // xml 读取相关配置
        XmlResolver xmlResolver = new XmlResolver();
        xmlResolver.loadFromXMl(this);

        System.out.println();
    }




    public static void main(String[] args) {
        new Configuration();
    }

}
