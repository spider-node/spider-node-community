package cn.spider.framework.flow.delayQueue;

import cn.spider.framework.flow.delayQueue.enums.DelayQueueEnum;
import cn.spider.framework.flow.delayQueue.task.TimedTask;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

public class JavaDelayQueue implements SpiderDelayQueue {
    private Map<String,DelayQueue<TimedTask>> taskMap;

    public JavaDelayQueue() {
        taskMap = new HashMap<>();
        taskMap.put(DelayQueueEnum.FLOW_DELAY.getCode(),new DelayQueue<TimedTask>());
        taskMap.put(DelayQueueEnum.FLOW_REMOVE_DELAY.getCode(),new DelayQueue<TimedTask>());
    }

    @Override
    public <T> boolean addDelayQueue(@NonNull T value, @NonNull long delay, @NonNull TimeUnit timeUnit, @NonNull String queueCode) {
        return taskMap.get(queueCode).add(new TimedTask(delay,timeUnit,value));
    }

    @Override
    public <T> T getDelayQueue(@NonNull String queueCode) throws InterruptedException {
        return (T) taskMap.get(queueCode).poll().get();
    }

    @Override
    public List<Object> getDelayQueueList(@NonNull String queueCode) throws InterruptedException {
        List<Object> result = new ArrayList<>();
        for(int i = 0;i< 100;i++){
            Object o = taskMap.get(queueCode).poll().get();
            if(Objects.isNull(o)){
                return result;
            }
            result.add(o);
        }
        return result;
    }

    @Override
    public boolean removeDelayedQueue(@NonNull Object o, @NonNull String queueCode) {
        return false;
    }
}
