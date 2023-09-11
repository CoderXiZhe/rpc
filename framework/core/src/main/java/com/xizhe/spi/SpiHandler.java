package com.xizhe.spi;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/11 12:36
 */

@Slf4j
public class SpiHandler {


    public static final String BASE_PATH = "META-INFO/rpc-services";

    private static final Map<String, List<String>> SPI_LIST = new ConcurrentHashMap<>(8);

    private static final Map<String, List<Object>> SPI_IMPLEMENT = new ConcurrentHashMap<>(8);

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(BASE_PATH);
        if (resource != null) {
            File file = new File(resource.getPath());
            File[] files = file.listFiles();
            for (File child : files) {
                String key = child.getName();
                List<String> value = getImplNames(child);
                SPI_LIST.put(key,value);
            }
        }

    }

    /**
     * 根据相应文件去获取实现类的集合
     * @param child 目标文件
     * @return 目标类的实现类集合
     */
    private static List<String> getImplNames(File child) {
        List<String> result = new ArrayList<>();
        try (FileReader reader = new FileReader(child);
             BufferedReader bufferedReader = new BufferedReader(reader);
        ) {
            while(true) {
                String line = bufferedReader.readLine();
                if(line == null || "".equals(line)) {
                   break;
                }
                result.add(line);
            }
            return result;
        }catch (IOException e) {
            log.error("读取文件[{}]发生异常",child.getName(),e);
        }
        return null;
    }

    /**
     * 获取一个和当前服务相关的实例
     * @param clazz 服务接口的class实例
     * @param <T>
     * @return 实现类的一个实例
     */
    public static <T> T get(Class<T> clazz) {
        String name = clazz.getName();
        List<Object> objects = SPI_IMPLEMENT.get(name);
        if(objects != null && !objects.isEmpty()) {
            return (T) objects.get(0);
        }
        objects = buildCache(name);
        return (T) objects.get(0);
    }


    /**
     * 获取所有和当前服务相关的实例
     * @param clazz 服务接口的class实例
     * @param <T>
     * @return 实现类的集合
     */
    public static <T> List<T> getList(Class<T> clazz) {
        String name = clazz.getName();
        List<Object> objects = SPI_IMPLEMENT.get(name);
        if(!objects.isEmpty()) {
            return (List<T>) objects;
        }
        objects = buildCache(name);
        return (List<T>) objects;
    }

    /**
     * 根据目标类名去构建实现类的缓存集合
     * @param name 目标类名
     * @return 目标类的实现类集合
     */
    private static List<Object> buildCache(String name) {
        List<String> list = SPI_LIST.get(name);
        List<Object> implist = new ArrayList<>();
        if (list != null && list.size()>0) {
            for (String implName : list) {
                Class<?> aClass = null;
                Object instance = null;
                try {
                    aClass = Class.forName(implName);
                    instance = aClass.getConstructor().newInstance();
                } catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                implist.add(instance);
            }
            SPI_IMPLEMENT.put(name, implist);
        }
        return implist;
    }

    public static void main(String[] args) {

    }
}
