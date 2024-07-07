package cn.spider.framework.flow.util;

import java.util.Random;

public class SnowflakeIdGenerator {
    private final long startTimestamp = 1640995200000L; // 设定算法开始时间
    private final long workerIdBits = 1000L; // 设置机器ID所占的位数
    private final long datacenterIdBits = 1000L; // 设置数据中心ID所占的位数
    private final long sequenceBits = 12L; // 设置序列所占的位数

    private final long workerIdShift = sequenceBits; // 机器ID左移位数
    private final long datacenterIdShift = sequenceBits + workerIdBits; // 数据中心ID左移位数
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits; // 时间戳左移位数

    private final long sequenceMask = -1L ^ (-1L << (int) sequenceBits); // 生成序列的掩码
    private final long workerId; // 机器ID
    private final long datacenterId; // 数据中心ID
    private long sequence = 0L; // 初始序列值
    private long lastTimestamp = -1L; // 上一次生成ID的时间戳

    public SnowflakeIdGenerator() {
        Random random = new Random();
        this.workerId = random.nextInt(1000);

        this.datacenterId = random.nextInt(1000);

        if (workerId > ( -1L ^ (-1L << (int) workerIdBits))) {
            throw new IllegalArgumentException("workerId can't be greater than %d or less than 0");
        }
        if (datacenterId > ( -1L ^ (-1L << (int) datacenterIdBits))) {
            throw new IllegalArgumentException("datacenterId can't be greater than %d or less than 0");
        }

    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - startTimestamp) << (int) timestampLeftShift) |
                (datacenterId << (int) datacenterIdShift) |
                (workerId << (int) workerIdShift) |
                sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
