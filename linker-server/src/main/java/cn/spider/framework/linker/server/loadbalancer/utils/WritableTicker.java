package cn.spider.framework.linker.server.loadbalancer.utils;

import com.google.common.base.Ticker;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public class WritableTicker extends Ticker {
    AtomicLong currentNano = new AtomicLong();
    @Override
    public long read() {
        return currentNano.get();
    }

    public void add(Duration d) {
        currentNano.addAndGet(d.toNanos());
    }
}
