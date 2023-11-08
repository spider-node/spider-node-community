package cn.spider.framework.linker.server.loadbalancer.internal;
import cn.spider.framework.linker.server.loadbalancer.timedcounter.TimedCounter;
import cn.spider.framework.linker.server.loadbalancer.timedcounter.WindowScheduledCounter;
import cn.spider.framework.linker.server.loadbalancer.timedcounter.WindowTimedCounter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.time.Duration;

/**
 * Keeps track of task latency, when all tasks have same concurrency, select the least
 * accumulative latency task
 * @param <T> the type parameter
 */
@SuppressFBWarnings(
        value="EQ_COMPARETO_USE_OBJECT_EQUALS",
        justification="Note: this class has a natural ordering that is inconsistent with equals.")
public class LatencyTaskConcurrency<T> extends TaskConcurrencyImpl<T> {
    private final TimedCounter durationMs;

    private LatencyTaskConcurrency(T task, Builder builder) {
        super(task);
        durationMs = new WindowTimedCounter(builder.scheduledCounterBuilder);
    }

    @Override
    public void complete(int n, Duration latency) {
        durationMs.add(latency.toMillis());
        super.complete(n, latency);
    }

    @Override
    public int compareTo(TaskConcurrency o) {
        int result = super.compareTo(o);
        //When two tasks have same concurrency, pick the least duration one
        if (result == 0 && o instanceof LatencyTaskConcurrency) {
            result = Long.compareUnsigned(durationMs.get(), ((LatencyTaskConcurrency)o).durationMs.get());
        }
        return result;
    }

    @Override
    public void syncState() {
        durationMs.check();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private static class Builder implements TaskConcurrency.Builder<Builder>  {
        private WindowScheduledCounter.Builder scheduledCounterBuilder = WindowScheduledCounter.newBuilder();

        @Override
        public Builder withLookBackTime(Duration lookBackTime) {
            this.scheduledCounterBuilder.withMaxDelay(lookBackTime);
            return this;
        }

        public <T> TaskConcurrency<T> build(T task) {
            return new LatencyTaskConcurrency<>(task, this);
        }
    }
}
