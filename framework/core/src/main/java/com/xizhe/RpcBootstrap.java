package com.xizhe;

import com.xizhe.annotation.RpcApi;
import com.xizhe.channelHandler.handler.MethodCallHandler;
import com.xizhe.channelHandler.handler.RpcRequestDecoder;
import com.xizhe.channelHandler.handler.RpcResponseEncoder;
import com.xizhe.compress.CompressType;
import com.xizhe.config.Configuration;
import com.xizhe.discovery.Registry;
import com.xizhe.discovery.RegistryConfig;
import com.xizhe.heartbeat.HeartBeatDetector;
import com.xizhe.loadbalance.LoadBalancer;
import com.xizhe.serialize.SerializerType;
import com.xizhe.transport.message.RpcRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 16:37
 */

@Slf4j
public class RpcBootstrap {

    private static final RpcBootstrap instance = new RpcBootstrap();

    private Configuration configuration;
    // 维护一个已经发布的服务列表 key: interface的全限定名 value:serviceConfig
    public static final Map<String,ServiceConfig<?>> SERVER_LIST = new ConcurrentHashMap<>(16);
    // 维护一个Channel缓存, InetSocketAddress作为key需要重写equals和hashcode
    public static final Map<InetSocketAddress, Channel> CHANNEL_CACHE = new ConcurrentHashMap<>(16);

    public static final SortedMap<Long,Channel> ANSWER_TIME_CACHE = new TreeMap<>();
    // 维护一个对外挂起的completablefuture
    public static final Map<Long, CompletableFuture<Object>> PENDING_REQUEST = new ConcurrentHashMap<>(128);

    public static ThreadLocal<RpcRequest> REQUEST_THREAD_LOCAL = new ThreadLocal<>();



    private RpcBootstrap() {
        configuration = new Configuration();
    }

    public static RpcBootstrap getInstance() {
        return instance;
    }

    /**
     * 定义当前应用名称
     * @param appName
     * @return
     */

    public RpcBootstrap application(String appName) {
        this.configuration.setAppName(appName);
        return this;
    }

    /**
     *  配置一个注册中心
     * @param registryConfig 注册中心
     * @return
     */
    public RpcBootstrap registry(RegistryConfig registryConfig) {
        this.configuration.setRegistryConfig(registryConfig);
        // 通过registryConfig 获取注册中心 : 工厂模式
        return this;
    }

    /**
     *  配置一个负载均衡器
     * @param loadBalancer 负载均衡器
     * @return
     */
    public RpcBootstrap loadBalance(LoadBalancer loadBalancer) {
        this.configuration.setLoadBalancer(loadBalancer);
        return this;
    }

    /**
     * 配置当前序列化协议
     * @param protocolConfig 协议的封装
     * @return
     */
    public RpcBootstrap protocol(ProtocolConfig protocolConfig) {
        this.configuration.setProtocolConfig(protocolConfig);
        log.debug("当前工程使用了: {} 协议进行序列化",protocolConfig.getProtocolName());
        return this;
    }

    /**
     * 发布服务,将接口-> 实现 注册到服务中心
     * @param service 封装好的需要发布的服务
     * @return
     */
    public RpcBootstrap publish(ServiceConfig<?> service) {
        // 面相抽象编程
        this.configuration.getRegistryConfig().getRegistry().registry(service);
        SERVER_LIST.put(service.getInterface().getName(),service);
        return this;
    }

    /**
     * 批量发布
     * @param services
     * @return
     */
    public RpcBootstrap publish(List<ServiceConfig<?>> services) {
        services.forEach(this::publish);
        return this;
    }

    /**
     *  启动netty服务
     */
    public void start() {
        // 1. 创建eventloop , 老板只负责接活，之后将请求分发至worker
        NioEventLoopGroup boss = new NioEventLoopGroup(2);
        NioEventLoopGroup worker = new NioEventLoopGroup(10);
        try {
            // 2. 需要一个服务器引导程序
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 3. 配置服务器
            serverBootstrap = serverBootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new LoggingHandler())
                                    .addLast(new RpcRequestDecoder())
                                    .addLast(new MethodCallHandler())
                                    .addLast(new RpcResponseEncoder());
                        }
                    })
                    .localAddress(new InetSocketAddress(Constant.port));
            // 4. 绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(Constant.port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ------------------------------------ 服务调用方api --------------------------------------------------
     */
    public RpcBootstrap reference(ReferenceConfig<?> reference) {
        reference.setRegistryConfig(this.configuration.getRegistryConfig());
        // 开启对该服务的心跳检测
        HeartBeatDetector.detectHeartBeat(reference.getInterface().getName());
        return this;
    }


    public RpcBootstrap serialize(SerializerType serializerType) {
        this.configuration.setSerializeType(serializerType.getType());
        return this;
    }

    public Registry getRegistry() {
        return this.configuration.getRegistryConfig().getRegistry();
    }

    public RpcBootstrap compress(CompressType gzip) {
        this.configuration.setCompressType(gzip.getType());
        return this;
    }

    public RpcBootstrap scan(String packageName) {
        // 获取通过packageName得到其下的所有类的全限定名
        List<String> names = getAllClassName(packageName);
        // 通过反射获取目标接口，构建具体实现
        List<? extends Class<?>> classes = names.stream().map(name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).filter(clazz -> clazz.getAnnotation(RpcApi.class) != null).collect(Collectors.toList());
        for (Class<?> clazz : classes) {
            Class<?>[] interfaces = clazz.getInterfaces();
            Object instance = null;
            try {
                 instance = clazz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            List<ServiceConfig<?>> serviceConfigs = new ArrayList<>();
            for (Class<?> anInterface : interfaces) {
                ServiceConfig<Object> serviceConfig = new ServiceConfig();
                serviceConfig.setInterface(anInterface);
                serviceConfig.setRef(instance);
                serviceConfigs.add(serviceConfig);
                publish(serviceConfig);
                log.debug("【{}】已通过包扫描进行发布",anInterface);
            }
        }

        return this;
    }

    private List<String> getAllClassName(String packageName) {
        String basePath = packageName.replaceAll("\\.","/");
        System.out.println(basePath);
        URL resource = ClassLoader.getSystemClassLoader().getResource(basePath);
        if (resource == null) {
            throw new RuntimeException();
        }
        String absolutePath = resource.getPath();
        System.out.println(absolutePath);
        List<String> classNames = new ArrayList<>();
        classNames = recursionFile(absolutePath,classNames);
        return classNames;
    }

    private List<String> recursionFile(String absolutePath, List<String> classNames) {
        File file = new File(absolutePath);
        if(file.isDirectory()) {
            File[] children = file.listFiles(pathname -> pathname.isDirectory() || pathname.getPath().contains(".class"));
            if (children == null || children.length == 0) {
                return classNames;
            }
            for (File child : children) {
                if(child.isDirectory()) {
                    recursionFile(child.getAbsolutePath(),classNames);
                }else {
                    String className = getClassNameByAbsolutePath(child.getAbsolutePath());
                    classNames.add(className);
                }
            }
        }else {
            String className = getClassNameByAbsolutePath(absolutePath);
            classNames.add(className);
        }
        return classNames;
    }

    private String getClassNameByAbsolutePath(String absolutePath) {
        // D:\Java-course\rpc\framework\core\target\classes\com\xizhe\watch\UpAndDownWatcher.class
        // -> com\xizhe\watch\UpAndDownWatcher.class
        // -> com.xizhe.watch.UpAndDownWatcher
        String[] split = absolutePath.split("classes\\\\");
        String path = split[split.length - 1];
        String[] split1 = path.replaceAll("\\\\", ".").split("\\.class");
        return split1[split1.length -1];
    }

    public static void main(String[] args) {
        RpcBootstrap.getInstance().getAllClassName("com.xizhe");
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
