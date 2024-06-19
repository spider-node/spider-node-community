package cn.spider.framework.flow.delayQueue;

import lombok.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface SpiderDelayQueue {

    // 添加到队列
    <T> boolean addDelayQueue(@NonNull T value, @NonNull long delay, @NonNull TimeUnit timeUnit, @NonNull String queueCode);

    // 获取队列数据
    <T> T getDelayQueue(@NonNull String queueCode) throws InterruptedException;

    // 批量获取数据
    List<Object> getDelayQueueList(@NonNull String queueCode) throws InterruptedException;

    // 移除队列数据
    boolean removeDelayedQueue(@NonNull Object o, @NonNull String queueCode);

}
