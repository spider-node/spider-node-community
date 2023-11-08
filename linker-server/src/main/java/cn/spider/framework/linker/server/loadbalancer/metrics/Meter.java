package cn.spider.framework.linker.server.loadbalancer.metrics;

import cn.spider.framework.linker.server.loadbalancer.utils.IntervalLimiter;
import com.codahale.metrics.EWMA;
import com.google.common.base.Ticker;
import java.util.concurrent.TimeUnit;

/**
 * Meter calculate per second operation rate
 * in one minute moving window
 */
public class Meter {
    private static final long TICK_INTERVAL = TimeUnit.SECONDS.toNanos(5L);
    private final IntervalLimiter limiter;
    private final EWMA m1Rate;

    /**
     * Instantiates a new Meter
     */
    public Meter() {
        this(Ticker.systemTicker());
    }

    /**
     * Instantiates a new Meter
     *
     * @param ticker the ticker
     */
    public Meter(Ticker ticker) {
        this.limiter = new IntervalLimiter(TICK_INTERVAL, ticker);
        this.m1Rate = EWMA.oneMinuteEWMA();
    }

    /**
     * mark number of operation performed
     *
     * @param n number of operation
     */
    public void mark(int n) {
        tickIfNecessary();
        this.m1Rate.update(n);
    }

    /**
     * Mark 1
     */
    public void mark() {
        this.mark(1);
    }

    /**
     * Gets per second operation rate in one minute sliding window
     *
     * @return the rate
     */
    public double getRate() {
        tickIfNecessary();
        return this.m1Rate.getRate(TimeUnit.SECONDS);
    }

    private void tickIfNecessary() {
        long ageNano = limiter.acquire();
        if (ageNano > 0) {
            long requiredTicks = ageNano / TICK_INTERVAL;

            for(long i = 0L; i < requiredTicks; ++i) {
                this.m1Rate.tick();
            }
        }
    }
}
