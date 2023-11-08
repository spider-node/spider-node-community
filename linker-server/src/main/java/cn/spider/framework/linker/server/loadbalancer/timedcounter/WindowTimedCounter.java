package cn.spider.framework.linker.server.loadbalancer.timedcounter;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public class WindowTimedCounter implements TimedCounter {
    private final ScheduledCounter scheduledCounter;
    private final AtomicLong value;

    public WindowTimedCounter(WindowScheduledCounter.Builder builder) {
        value = new AtomicLong();
        scheduledCounter = builder.of(o->value.addAndGet(o));
    }

    @Override
    public void add(long n) {
        this.value.addAndGet(n);
        this.scheduledCounter.schedule(-n);
    }

    @Override
    public void add(long n, Duration duration) {
        this.value.addAndGet(n);
        this.scheduledCounter.schedule(-n, duration);
    }

    @Override
    public void check() {
        scheduledCounter.check();
    }

    @Override
    public long get() {
        return value.get();
    }
}
