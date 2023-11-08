package cn.spider.framework.linker.server.loadbalancer;

import cn.spider.framework.linker.server.loadbalancer.internal.TaskConcurrency;
import cn.spider.framework.linker.server.loadbalancer.internal.TaskConcurrencyDelegator;
import cn.spider.framework.linker.server.loadbalancer.metrics.Meter;
import cn.spider.framework.linker.server.loadbalancer.timedcounter.ScheduledCounter;
import cn.spider.framework.linker.server.loadbalancer.timedcounter.WindowScheduledCounter;
import cn.spider.framework.linker.server.loadbalancer.utils.IntervalLimiter;
import cn.spider.framework.linker.server.loadbalancer.utils.MathUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


/**
 * AbstractConcurrencyLoadBalancer is abstract least pending request load balancer.
 * There are two implementations
 * {@link HeapConcurrencyLoadBalancer} and {@link ArrayConcurrencyLoadBalancer}
 *
 * @param <T> the entity type
 */
public abstract class AbstractConcurrencyLoadBalancer<T> implements LeastConcurrencyLoadBalancer<T> {
    final List<CompletableTask.Listener<T>> listeners;
    final Ticker ticker;
    final MetricsImpl metrics;

    /**
     * Instantiates a new LeastConcurrencyLoadBalancer
     *
     * @param tasks          the tasks
     * @param listeners      the listeners
     * @param ticker         the ticker
     */
    AbstractConcurrencyLoadBalancer(Collection<T> tasks, List<CompletableTask.Listener<T>> listeners, Ticker ticker) {
        this.listeners = listeners;
        this.ticker = ticker;
        this.metrics = new MetricsImpl(tasks, ticker);
    }

    @Override
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Function called after loadBalancer selected one entity
     * but before caller interact with the entity
     *
     * @param t the selected entity
     */
    void onTaskSelected(T t) {
        metrics.markSelection(t);
        for (CompletableTask.Listener<T> listener : listeners) {
            listener.onCreate(t);
        }
    }


    /**
     * Function called after caller finished interact with the entity
     * but before loadBalancer mark the entity as complete
     *
     * @param t       the completed task
     * @param succeed the result indicator
     */
    void onTaskCompleted(T t, boolean succeed) {
        metrics.markCompletion(t, succeed);
        for (CompletableTask.Listener<T> listener : listeners) {
            listener.onComplete(t, succeed);
        }
    }

    /**
     * AbstractCompletableTask
     */
    abstract class AbstractCompletableTask implements CompletableTask {
        final AtomicBoolean completed = new AtomicBoolean(false);
        final long startNano;
        final T task;

        AbstractCompletableTask(T t, long startNano) {
            this.startNano = startNano;
            this.task = t;
            onTaskSelected(t);
        }

        //if there are multiple completion, only the first call should succeed
        @Override
        public boolean complete(boolean succeed) {
            if (completed.compareAndSet(false, true)) {
                onTaskCompleted(task, succeed);
                return true;
            }
            return false;
        }

        @Override
        public T getTask() {
            return task;
        }
    }

    /**
     * The type Concurrent task.
     * concurrency counter of entity
     *  - increased by 1 when constructed
     *  - decrease by 1 when completed
     *
     */
    class ConcurrentTaskImpl extends AbstractCompletableTask {
        private final TaskConcurrency<T> taskConcurrency;

        /**
         * Instantiates a new Concurrent task.
         *
         * @param startNano           the start nano
         * @param taskConcurrency                the selected entity
         */
        ConcurrentTaskImpl(long startNano, TaskConcurrency<T> taskConcurrency) {
            super(taskConcurrency.getTask(), startNano);
            this.taskConcurrency = taskConcurrency;
            taskConcurrency.acquire();
        }

        @Override
        public boolean complete(boolean succeed) {
            if (super.complete(succeed)) {
                this.taskConcurrency.complete(succeed, Duration.ofNanos(ticker.read() - startNano));
                return true;
            }

            return false;
        }
    }

    static class ScheduledTaskConcurrency<T> extends TaskConcurrencyDelegator<T> {
        private final ScheduledCounter scheduledCounter;
        private final Duration timeout;

        public ScheduledTaskConcurrency(TaskConcurrency<T> delegate, ScheduledCounter.Builder builder) {
            super(delegate);
            this.timeout = builder.getMaxDelay();
            this.scheduledCounter = builder.of(new Consumer<Long>() {
                @Override
                public void accept(Long n) {
                    delegate.complete(n.intValue(), timeout.multipliedBy(n.intValue()));
                }
            });
        }

        @Override
        public void complete(boolean succeed, Duration latency) {
            if (!succeed) {
                //if task failed, postpone counter for (timeout - latency)
                scheduledCounter.schedule(1, timeout.minus(latency));
            } else {
                super.complete(true, latency);
            }
        }

        @Override
        public void syncState() {
            //try recovery sustained count with scheduledCounter
            //matured count will be consumed by DeltaConsumer
            scheduledCounter.check();
        }
    }

    static class MetricsImpl<T> implements Metrics {
        private static final StandardDeviation STANDARD_DEVIATION = new StandardDeviation();
        private static final Mean MEAN = new Mean();
        private static final long TICK_INTERVAL = TimeUnit.SECONDS.toNanos(5L);
        private final IntervalLimiter limiter;
        private final Meter requestRate;
        private final Meter successRate;
        private final Meter failureRate;
        private final Map<T, Meter> taskRequestRate;
        private volatile double cov;

        MetricsImpl(Collection<T> tasks, Ticker ticker) {
            limiter = new IntervalLimiter(TICK_INTERVAL, ticker);
            this.requestRate = new Meter(ticker);
            this.successRate = new Meter(ticker);
            this.failureRate = new Meter(ticker);
            ImmutableMap.Builder builder = new ImmutableMap.Builder();
            for (T task : tasks) {
                builder.put(task, new Meter(ticker));
            }
            this.taskRequestRate = builder.build();
        }

        void markSelection(T task) {
            Meter taskMeter = taskRequestRate.get(task);
            if (taskMeter == null) {
                return;
            }
            requestRate.mark();
            taskMeter.mark();
        }

        void markCompletion(T task, boolean succeed) {
            Meter taskMeter = taskRequestRate.get(task);
            if (taskMeter == null) {
                return;
            }

            if (succeed) {
                successRate.mark();
            } else {
                failureRate.mark();
            }
        }

        @Override
        public double requestRate() {
            return requestRate.getRate();
        }

        @Override
        public double successRate() {
            return successRate.getRate();
        }

        @Override
        public double failureRate() {
            return failureRate.getRate();
        }

        @Override
        public double requestCOV() {
            tickIfNecessary();
            return cov;
        }

        protected void tickIfNecessary() {
            long ageNano = limiter.acquire();
            if (ageNano > 0) {
                cov = calcCOV(taskRequestRate.values());
            }
        }

        private double calcCOV(Collection<Meter> meters) {
            double[] rates = meters.stream()
                    .map(o->o.getRate())
                    .mapToDouble(d->d)
                    .toArray();
            double stdev = STANDARD_DEVIATION.evaluate(rates);
            double mean = MEAN.evaluate(rates);
            return MathUtils.divide(stdev, mean);
        }
    }

    /**
     * The Builder interface that requires assigning of tasks
     *
     * @param <T> the type of entity
     * @param <B> the actual builder type being returned
     */
    interface TaskBuilder<T, B extends AbstractBuilder> {
        /**
         * set task list and return the builder instance
         *
         * @param tasks the tasks
         * @return the b
         */
        B withTasks(Collection<T> tasks);
    }

    /**
     * AbstractBuilder
     *
     * @param <T> the entity type
     * @param <B> the actual builder type being returned
     */
    public static abstract class AbstractBuilder<T, B extends AbstractBuilder> implements TaskBuilder<T,B> {

        SubStrategy subStrategy = SubStrategy.Absent;
        Duration lookBackTime = Duration.ofMinutes(10);
        WindowScheduledCounter.Builder scheduledCounterBuilder = WindowScheduledCounter
                .newBuilder()
                .withMaxDelay(Duration.ZERO)
                .withNumWindow(100);
        Collection<T> tasks;
        List<CompletableTask.Listener<T>> listeners = new ArrayList<>();

        @Override
        public B withTasks(Collection<T> tasks) {
            this.tasks = tasks;
            return (B)this;
        }

        @VisibleForTesting
        protected B withTicker(Ticker ticker) {
            this.scheduledCounterBuilder.withTicker(ticker);
            return (B)this;
        }

        /**
         * Sub load balancing strategy is used to resolve conflicts
         * when concurrency of all partitions are equal
         * <p>
         * this option is especial important for low concurrency use case
         * use SubStrategy.LeastFrequency to achieve round-robin like effect
         * use SubStrategy.LeastTime to achieve optimal latency and throughput
         * </p>
         * @param subStrategy the sub strategy
         * @return the builder
         */
        public B withSubStrategy(SubStrategy subStrategy) {
            this.subStrategy = subStrategy;
            return (B)this;
        }

        /**
         * With sub strategy and look back time of the subStrategy
         * recallDuration is used by sub strategy to manage look back time for frequency and latency
         *
         * @param subStrategy the sub strategy
         * @param lookBackTime duration the sub strategy looks back
         * @return the b
         */
        public B withSubStrategy(SubStrategy subStrategy, Duration lookBackTime) {
            withSubStrategy(subStrategy);
            this.lookBackTime = lookBackTime;
            return (B)this;
        }

        /**
         * As a passive health check strategy, to prevent a task failed fast get higher throughput than task that succeed slower.
         * When request failed, its latency was treated at least the same as specific timeout duration,
         * as a result concurrency count will sustain for extra period of (effectiveLatency - observedLatency)
         *
         * @param latency the minimal effective latency when request failed
         * @return the builder
         */
        public B withFailureEffectiveLatency(Duration latency) {
            if (latency.isNegative()) {
                throw new IllegalArgumentException("timeout can't be negative");
            }
            this.scheduledCounterBuilder.withMaxDelay(latency);
            return (B)this;
        }

        /**
         * Specify both latency and timeWindowCount
         * increase timeWindowCount to improve precision
         * reduce timeWindowCount to improve performance
         *
         * @param latency     the minimal effective latency when request failed
         * @param windowCount number of time window represent whole timeout duration
         * @return the builder
         */
        public B withFailureEffectiveLatency(Duration latency, int windowCount) {
            withFailureEffectiveLatency(latency);
            if (windowCount <= 0) {
                throw new IllegalArgumentException("timeWindowCount must be positive");
            }
            this.scheduledCounterBuilder.withNumWindow(windowCount);
            return (B)this;
        }

        /**
         * With CompletableTask listener
         *
         * @param listener the CompletableTask.Listener
         * @return the builder
         */
        public B withTaskListener(CompletableTask.Listener<T> listener) {
            listeners.add(listener);
            return (B)this;
        }
    }
}
