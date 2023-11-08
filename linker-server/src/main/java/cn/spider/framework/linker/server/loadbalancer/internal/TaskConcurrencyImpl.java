package cn.spider.framework.linker.server.loadbalancer.internal;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskConcurrencyImpl<T> implements TaskConcurrency<T> {
    /**
     * The balancing entity
     */
    protected final T task;
    /**
     * The concurrency counter
     */
    protected final AtomicInteger concurrency;

    protected TaskConcurrencyImpl(T task) {
        this.task = task;
        this.concurrency = new AtomicInteger();
    }

    @Override
    public T getTask() {
        return task;
    }

    @Override
    public void acquire(int n) {
        concurrency.addAndGet(n);
    }

    @Override
    public void complete(int n, Duration duration) {
        concurrency.addAndGet(-n);
    }

    /**
     * Gets concurrency.
     *
     * @return the concurrency
     */
    @Override
    public int getConcurrency() {
        return concurrency.get();
    }

    @Override
    public void syncState() {
    }

    public static TaskConcurrency.Builder newBuilder() {
        return Builder.BUILDER;
    }

    private static class Builder implements TaskConcurrency.Builder<Builder> {
        private static final TaskConcurrency.Builder BUILDER = new Builder();

        @Override
        public Builder withLookBackTime(Duration lookBackTime) {
            //duration is not used for this implementation
            return this;
        }

        public <T> TaskConcurrency<T> build(T task) {
            return new TaskConcurrencyImpl<>(task);
        }
    }
}
