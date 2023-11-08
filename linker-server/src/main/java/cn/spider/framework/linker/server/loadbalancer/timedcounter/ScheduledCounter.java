package cn.spider.framework.linker.server.loadbalancer.timedcounter;

import com.google.common.base.Ticker;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * ScheduledCounter supports delayed counting.
 */
public interface ScheduledCounter {

    /**
     * delay count n for period delay
     *
     * @param n     the n count to add
     * @param delay the delay before counting
     */
    void schedule(long n, Duration delay);


    /**
     * delay count n for max period
     *
     * @param n the n
     */
    void schedule(long n);

    /**
     * check and push matured count to consumer
     */
    void check();

    /**
     * ScheduledCounterBuilder
     *
     * @param <C> the actual type of ScheduledCounter
     */
    interface Builder<C extends ScheduledCounter> {
        /**
         * create instance of ScheduledCounter, and have count consumed by consumer
         *
         * @param consumer the consumer
         * @return the b
         */
        C of(Consumer<Long> consumer);

        /**
         * Gets max delay.
         *
         * @return the max delay
         */
        Duration getMaxDelay();

        /**
         * Gets ticker.
         *
         * @return the ticker
         */
        Ticker getTicker();
    }
}
