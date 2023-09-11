package com.xizhe.spi;

import com.xizhe.compress.Compressor;
import com.xizhe.config.Configuration;
import com.xizhe.loadbalance.LoadBalancer;
import com.xizhe.serialize.Serializer;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/11 12:36
 */

public class SpiResolver {


    public void loadFromSpi(Configuration configuration) {
        LoadBalancer loadBalancer = SpiHandler.get(LoadBalancer.class);
        configuration.setLoadBalancer(loadBalancer);

        Serializer serializer = SpiHandler.get(Serializer.class);
        configuration.setSerializer(serializer);

        Compressor compressor = SpiHandler.get(Compressor.class);
        configuration.setCompressor(compressor);

    }
}
