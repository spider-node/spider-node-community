package cn.spider.framework.flow.delayQueue.task;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class TimedTask<T> implements Delayed {
    private final long executionTime; // 任务执行的绝对时间（毫秒）

    private T t;


    public TimedTask(long delay, TimeUnit unit,T t) {
        this.executionTime = System.currentTimeMillis() + unit.toMillis(delay);
        this.t = t;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = executionTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        if (other == this) {
            return 0;
        }
        long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
        return (diff < 0) ? -1 : (diff > 0) ? 1 : 0;
    }

    public T get() {
        return t;
    }
}
