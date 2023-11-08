package cn.spider.framework.linker.server.loadbalancer.timedcounter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Ticker;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * WindowScheduledCounter is a counter support delayed add operation.
 * delayed counts are then pushed to count consumer asynchronous according to provided schedule.
 * Sliding time window is used to optimize both space and time complexity.
 * Each window represent a slice of time. windows length is decided by
 * maxDuration / totalWindows. e.g. with 30s maxDuration and 10 totalWindows
 * window size = 30s / 10 = 3s, An array of 10 windows can represent max duration of 30s.
 * tasks are scheduled to align with the edge of window. for example
 * .schedule(1, 10s) @0s will be scheduled to 12s which is the upper edge of window [9,12]
 * .schedule(1, 11s) @0s will be scheduled to 12s for the same reason
 * ScheduledCounter aggregates counts of the same window and feed to count consumer.
 * for this case .check() @12s will call consumer.accept(2)
 * Example:
 * <pre>
 * WindowScheduledCounter scheduledCounter = WindowScheduledCounter.newBuilder()
 *                 .withMaxDelay(Duration.ofSeconds(30))
 *                 .withNumWindow(100)
 *                 .of(consumer);
 * scheduledCounter.schedule(1, Duration.ofMillis(delayMs));
 * scheduledCounter.check() //check and push count to consumer
 * </pre>
 *
 * <p>
 * Performance related
 * more windows indicates higher precision but also more CPU cost
 * ScheduledCounter use the number of window as its concurrency limit, when the limit reached
 * </p>
 *
 * @TheadSafe
 */
public class WindowScheduledCounter implements ScheduledCounter {
    final LifespanTracker lifespanTracker;
    private final Consumer<Long> consumer;
    private final Ticker ticker;
    private final AtomicLong expiredCount;

    /**
     * Instantiates a new Scheduled counter.
     *
     * @param lifespanTracker the lifespan tracker
     * @param consumer         the count consumer
     */
    WindowScheduledCounter(LifespanTracker lifespanTracker, Consumer<Long> consumer) {
        this.lifespanTracker = lifespanTracker;
        this.consumer = consumer;
        this.ticker = lifespanTracker.getTicker();
        this.expiredCount = new AtomicLong();
    }

    /**
     * Delay counter add operation, delayed counts are then feed to count consumer
     * asynchronous according to provided schedule.
     * Max delay is limited by max age of ScheduledCounter,
     * and a delay exceed the limit will be trimmed to maxAge
     *
     * @param n     the count to add
     * @param delay time to wait before feed the count to consumer
     */
    @Override
    public void schedule(long n, Duration delay) {
        internalSchedule(n, delay);
    }

    @Override
    public void schedule(long n) {
        internalSchedule(n, lifespanTracker.getMaxAge());
    }

    @VisibleForTesting
    protected long internalSchedule(long n, Duration delay) {
        long nanoTimestamp = lifespanTracker.add(n, delay);
        if (nanoTimestamp - ticker.read() <= 0) {
            tryPush();
        }
        return nanoTimestamp;
    }

    /**
     * check and push matured count to consumer
     * if exception throw from consumer, count will be saved and delivered later
     */
    @Override
    public void check() {
        tryPush();
    }

    private void tryPush() {
        long n = lifespanTracker.purge() + expiredCount.getAndSet(0);
        if (n == 0) {
            return;
        }

        try {
            consumer.accept(n);
        } catch (Throwable t) {
            expiredCount.addAndGet(n);
        }
    }

    /**
     * New builder for ScheduledCounter
     *
     * @return the builder
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Builder ScheduledCounter
     */
    public static class Builder implements ScheduledCounter.Builder<WindowScheduledCounter> {
        private Duration maxDelay = Duration.ofSeconds(30);
        private int numWindows = 100;
        private Ticker ticker = Ticker.systemTicker();

        /**
         * Max delay the ScheduledCounter support.
         *
         * @param maxDelay the max delay
         * @return the final scheduled counter builder
         */
        public Builder withMaxDelay(Duration maxDelay) {
            this.maxDelay = maxDelay;
            return this;
        }

        @Override
        public Duration getMaxDelay() {
            return maxDelay;
        }

        @Override
        public Ticker getTicker() {
            return ticker;
        }

        /**
         * The number of window the ScheduledCounter support.
         * Precision increase with the number of window, but also increase CPU usage.
         *
         * @param numWindow the num window
         * @return the final scheduled counter builder
         */
        public Builder withNumWindow(int numWindow) {
            this.numWindows = numWindow;
            return this;
        }

        public Builder withTicker(Ticker ticker) {
            this.ticker = ticker;
            return this;
        }

        /**
         * Build instance of ScheduledCounter
         *
         * @param consumer the consumer
         * @return the scheduled counter
         */
        public WindowScheduledCounter of(Consumer<Long> consumer) {
            return new WindowScheduledCounter(new LifespanTracker(maxDelay, numWindows, ticker), consumer);
        }
    }
}
