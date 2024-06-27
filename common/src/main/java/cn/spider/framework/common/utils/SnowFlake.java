package cn.spider.framework.common.utils;

public class SnowFlake {
    // 因为二进制里第一个 bit 为如果是 1，那么都是负数，但是我们生成的 id 都是正数，所以第一个 bit 统一都是 0。

    /**
     * 机器ID  2进制5位
     */
    private long workerId;

    /**
     * 机房ID 2进制5位
     */
    private long datacenterId;

    /**
     * 代表一毫秒内生成的多个id的最新序号  12位，范围从0到4095
     */
    private long sequence;

    /**
     * 设置一个时间初始值(这个用自己业务系统上线的时间)    2^41 - 1   差不多可以用69年
     */
    private long twepoch = 1585644268888L;

    /**
     * 5位的机器id
     */
    private long workerIdBits = 5L;

    /**
     * 5位的机房id
     */
    private long datacenterIdBits = 5L;

    /**
     * 每毫秒内产生的id数 2 的 12次方
     */
    private long sequenceBits = 12L;

    /**
     * 这个是二进制运算，就是5 bit最多只能有31个数字，也就是说机器id最多只能是32以内
     */
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 这个是一个意思，就是5 bit最多只能有31个数字，机房id最多只能是32以内
     */
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /**
     * 机器ID向左移12位
     */
    private long workerIdShift = sequenceBits;

    /**
     * 机房ID向左移17位
     */
    private long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间戳向左移22位
     */
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 上次生成ID的时间截，记录产生时间毫秒数，判断是否是同1毫秒
     */
    private long lastTimestamp = -1L;


    public SnowFlake(long workerId, long datacenterId, long sequence) {

        // 检查机房id和机器id是否超过最大值，不能小于0
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }

        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }

        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = sequence;
    }

    public long nextId() {
        // 获取当前的时间戳，单位是毫秒
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            System.err.printf("clock is moving backwards. Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 下面是说假设在同一个毫秒内，又发送了一个请求生成一个id
        // 这个时候就得把seqence序号给递增1，最多就是4096
        if (lastTimestamp == timestamp) {
            // 这个意思是说一个毫秒内最多只能有4096个数字，无论你传递多少进来，
            //这个位运算保证始终就是在4096这个范围内，避免你自己传递个sequence超过了4096这个范围
            sequence = (sequence + 1) & sequenceMask;
            //当某一毫秒的时间，产生的id数 超过4095，系统会进入等待，直到下一毫秒，系统继续产生ID
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else { // 时间戳改变，毫秒内序列重置
            sequence = 0;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        // 这儿就是最核心的二进制位运算操作，生成一个64bit的id
        // 先将当前时间戳左移，放到41 bit那儿；将机房id左移放到5 bit那儿；将机器id左移放到5 bit那儿；将序号放最后12 bit
        // 最后拼接起来成一个64 bit的二进制数字，转换成10进制就是个long型
        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) | sequence;
    }


    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }


    private long timeGen() {
        return System.currentTimeMillis();
    }
}
