package com.xizhe;

import com.xizhe.utils.DateUtil;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author admin
 * @version 1.0
 * @description: 请求id生成器 - 雪花算法 ( 机房号 + 机器号 + 时间戳 )
 * @date 2023/9/8 17:42
 */

public class IdGenerator {

    public static final long START_TIMESTAMP = DateUtil.get("2020-1-1").getTime();

    public static final long DATA_CENTER_BIT = 5L;

    public static final long MACHINE_BIT = 5L;

    public static final long SEQUENCE_BIT = 5L;

    public static final long DATA_CENTER_BIT_MAX = (1L << DATA_CENTER_BIT) -1;

    public static final long MACHINE_BIT_MAX = (1L << MACHINE_BIT) -1;

    public static final long SEQUENCE_BIT_MAX = (1L << SEQUENCE_BIT) -1;

    public static final long TIMESTAMP_LEFT = DATA_CENTER_BIT + MACHINE_BIT + SEQUENCE_BIT;

    public static final long DATA_CENTER_LEFT = MACHINE_BIT + SEQUENCE_BIT;

    public static final long MACHINE_LEFT = SEQUENCE_BIT;

    private long machineId;

    private long dataCenterId;

    private LongAdder sequenceId = new LongAdder();

    private long lastTimestamp = -1L;


    public IdGenerator(long machineId, long dataCenterId) {
        if(machineId > MACHINE_BIT_MAX || dataCenterId > DATA_CENTER_BIT_MAX) {
            throw new RuntimeException("参数请求非法");
        }
        this.machineId = machineId;
        this.dataCenterId = dataCenterId;
    }

    public long getId() {
        long currentTime = System.currentTimeMillis();
        long timestamp = currentTime - START_TIMESTAMP;
        // 时钟回拨
        if(timestamp < lastTimestamp) {
            throw new RuntimeException("时间回拨异常!");
        }
        // 同一个时间点需要进行自增
        if(timestamp == lastTimestamp) {
            sequenceId.increment();
            if(sequenceId.sum() >= SEQUENCE_BIT_MAX ) {
                timestamp = getNextTimestamp();
            }
        }else {
            sequenceId.reset();
        }
        lastTimestamp = timestamp;

        return timestamp << TIMESTAMP_LEFT | dataCenterId << DATA_CENTER_LEFT
                | machineId << MACHINE_LEFT | sequenceId.sum();
    }

    private long getNextTimestamp() {

        long current = System.currentTimeMillis() - START_TIMESTAMP;
        while(current == lastTimestamp) {
             current = System.currentTimeMillis() - START_TIMESTAMP;
        }
        return current;
    }

}
