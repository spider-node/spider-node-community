package cn.spider.framework.linker.server.loadbalancer.timedcounter;

import java.time.Duration;

/**
 * TimedCounter support value accumulation with sustain period
 */
public interface TimedCounter {
    /**
     * add count with maximum sustain period
     *
     * @param n the value
     */
    void add(long n);

    /**
     * add count with specific sustain period
     *
     * @param n    the value
     * @param duration sustain period of value, capped by maximum sustain period
     */
    void add(long n, Duration duration);

    /**
     * Check and dropped matured count
     */
    void check();

    /**
     * Get accumulated count
     *
     * @return the long
     */
    long get();
}
