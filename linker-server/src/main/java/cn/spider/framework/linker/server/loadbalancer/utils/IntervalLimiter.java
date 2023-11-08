package cn.spider.framework.linker.server.loadbalancer.utils;

import com.google.common.base.Ticker;

import java.util.concurrent.atomic.AtomicLong;

/**
 * IntervalLimiter limits operation minimal interval
 */
public class IntervalLimiter {
    private final long intervalNano;
    private final AtomicLong lastTick;
    private final Ticker ticker;

    /**
     * Instantiates a IntervalLimiter with default ticker
     *
     * @param intervalNano the min interval between each operation
     */
    public IntervalLimiter(long intervalNano) {
        this(intervalNano, Ticker.systemTicker());
    }

    /**
     * Instantiates a IntervalLimiter
     *
     * @param intervalNano the min interval between each operation
     * @param ticker           the ticker
     */
    public IntervalLimiter(long intervalNano, Ticker ticker) {
        this.ticker = ticker;
        this.lastTick = new AtomicLong(ticker.read());
        this.intervalNano = intervalNano;
    }

    /**
     * Acquire permission from the limiter
     *
     * @return the long nano seconds since last successful access, -1 indicates access deny
     */
    public long acquire() {
        long oldTick = this.lastTick.get();
        long newTick = this.ticker.read();
        long age = newTick - oldTick;
        if (age > intervalNano) {
            long newIntervalStartTick = newTick - age % intervalNano;
            if (this.lastTick.compareAndSet(oldTick, newIntervalStartTick)) {
                return age;
            }
        }
        return -1;
    }
}
