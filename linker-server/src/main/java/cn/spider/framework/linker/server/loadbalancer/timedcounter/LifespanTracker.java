package cn.spider.framework.linker.server.loadbalancer.timedcounter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Ticker;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * LifespanTracker tracks lifespan of numbers being added. a number is considering dead/purgeable
 * when current time - born time >= its maxAge.
 * <p>
 * Time window is defined to optimize time/space complexity of the tracker.
 * Each window represent a slice of time. windows length is decided by
 * maxDuration / totalWindows. e.g. with 30s maxDuration and 10 totalWindows
 * window size = 30s / 10 = 3s, rolling array can represent max duration of 30s.
 * </p>
 * Example:
 * <pre>
 * LifetimeTracker tracer = new LifetimeTracker(30s, 10, () -> System.nanoTime()); //30s maxAge, 10 windows ,use system nano clock
 * tracer.add(5, 10s) //@0s add 5 with maxAge 10s
 * int result = tracker.purge() //@15s, expected result = 5, because 5 is purgable at the moment
 * </pre>
 *
 * @ThreadSafe
 */
class LifespanTracker {
    private final Duration maxAge;
    private final int totalWindows;
    private final long windowNanos;
    private final Window[] windows;
    private final Ticker ticker;

    private final AtomicLong lastWindowId = new AtomicLong();
    private final AtomicLong toBePurged = new AtomicLong();

    /**
     * Instantiates a new tracker.
     *
     * @param maxAge   the max duration
     * @param nWindows the number of windows
     * @param ticker   the clock to provide time in nano
     */
    LifespanTracker(Duration maxAge, int nWindows, Ticker ticker) {
        if (nWindows <= 0) {
            throw new IllegalArgumentException("number of slots must be positive");
        }

        if (maxAge.isNegative()) {
            throw new IllegalArgumentException("max duration must be non-negative");
        }

        if (Duration.ofDays(1000).minus(maxAge).isNegative()) {
            throw new IllegalArgumentException("duration over 1000days is not supported");
        }
        this.maxAge = maxAge;
        this.totalWindows = nWindows;
        this.ticker = ticker;
        this.windowNanos = maxAge.dividedBy(nWindows).toNanos();
        this.windows = new Window[nWindows];
        for (int i = 0; i < nWindows; ++i) {
            windows[i] = new Window();
        }
    }

    /**
     * add number to tracker with timeToLive
     * Aged number became purgeable by function purge()
     *
     * @param n          the number to add
     * @param timeToLive the time to live will be rounded to zero when negative
     * @return nanoSecond of lower limit when the number is purgable
     */
    public long add(long n, Duration timeToLive) {
        if (maxAge.minus(timeToLive).isNegative()) {
            timeToLive = maxAge;
        }
        if (timeToLive.isNegative()) {
            timeToLive = Duration.ZERO;
        }
        long nowNanos = ticker.read();
        if (windowNanos == 0) {
            toBePurged.addAndGet(n);
            return nowNanos;
        }
        long windowId = (nowNanos + timeToLive.toNanos()) / windowNanos;
        long windowUpperNano = (windowId + 1) * windowNanos;
        //windowId could be negative
        int index = (int) (((windowId % totalWindows) + totalWindows) % totalWindows);

        Window window = windows[index];

        if (window.windowId < windowId) {
            synchronized (window) {
                if (window.windowId < windowId) {
                    toBePurged.addAndGet(window.count.getAndSet(n));
                    window.windowId = windowId;
                    return windowUpperNano;
                }
            }
        }
        if (window.windowId > windowId) {
            toBePurged.addAndGet(n);
        } else {
            window.count.addAndGet(n);
        }

        return windowUpperNano;
    }

    /**
     * Purge aged numbers with current timestamp
     *
     * @return the int
     */
    public long purge() {
        return purge(ticker.read());
    }

    /**
     * Purge aged numbers
     *
     * @param nanoTimestamp the timestamp to purge
     * @return the sum of aged numbers
     */
    long purge(long nanoTimestamp) {
        if (windowNanos != 0) {
            long windowId = nanoTimestamp / windowNanos;
            //lastWindowId indicates last logical window which count is in-sync with global count
            long id = lastWindowId.get();
            if (id < windowId && lastWindowId.compareAndSet(id, windowId)) {
                //only one thread can entry here for the same lastWindowId
                int n = 0;
                for (; id < windowId && n < totalWindows; id++, n++) {
                    //recycle all expired window, minus count from global count
                    int index = (int) (((id % totalWindows) + totalWindows) % totalWindows);
                    Window window = windows[index];
                    if (window.count.get() != 0 && window.windowId < windowId) {
                        toBePurged.addAndGet(window.count.getAndSet(0));
                    }
                }
            }
        }

        return toBePurged.getAndSet(0);
    }

    /**
     * Gets max duration.
     *
     * @return the max duration
     */
    protected Duration getMaxAge() {
        return maxAge;
    }

    /**
     * Gets total number of windows.
     *
     * @return the total windows
     */
    @VisibleForTesting
    protected int getTotalWindows() {
        return totalWindows;
    }

    /**
     * Gets ticker
     *
     * @return the nano c lock
     */
    protected Ticker getTicker() {
        return ticker;
    }

    /**
     * Gets Window length in nanos.
     *
     * @return the window nanos
     */
    public long getWindowNanos() {
        return windowNanos;
    }

    /**
     * The type Window.
     */
    static class Window {
        /**
         * equals to nano / windowNano
         * The windowId can be negative
         */
        long windowId = 0;

        /**
         * The Count.
         * the count can be negative
         */
        AtomicLong count = new AtomicLong();
    }
}
