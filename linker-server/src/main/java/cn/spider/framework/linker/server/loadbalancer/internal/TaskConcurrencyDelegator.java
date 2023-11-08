package cn.spider.framework.linker.server.loadbalancer.internal;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.time.Duration;

@SuppressFBWarnings(
        value="EQ_COMPARETO_USE_OBJECT_EQUALS",
        justification="Note: this class has a natural ordering that is inconsistent with equals.")
public class TaskConcurrencyDelegator<T> implements TaskConcurrency<T> {
    private final TaskConcurrency<T> delegate;

    public TaskConcurrencyDelegator(TaskConcurrency<T> delegate) {
        this.delegate = delegate;
    }

    public T getTask() {
        return delegate.getTask();
    }

    public void acquire() {
        delegate.acquire();
    }

    public void complete(boolean succeed, Duration latency) {
        delegate.complete(succeed, latency);
    }

    public void acquire(int n) {
        delegate.acquire(n);
    }

    public void complete(int n, Duration latency) {
        delegate.complete(n, latency);
    }

    public int getConcurrency() {
        return delegate.getConcurrency();
    }

    public void syncState() {
        delegate.syncState();
    }

    @Override
    public int compareTo(TaskConcurrency o) {
        if (o instanceof TaskConcurrencyDelegator) {
            return delegate.compareTo(((TaskConcurrencyDelegator) o).delegate);
        } else {
            return delegate.compareTo(o);
        }
    }
}
