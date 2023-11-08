package cn.spider.framework.flow.delayQueue;

public interface RedisDelayQueueHandle<T> {
    void execute(T t);
}
