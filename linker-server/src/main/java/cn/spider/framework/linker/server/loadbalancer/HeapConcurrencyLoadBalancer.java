package cn.spider.framework.linker.server.loadbalancer;

import cn.spider.framework.linker.server.loadbalancer.internal.TaskConcurrency;
import cn.spider.framework.linker.server.loadbalancer.internal.TaskConcurrencyDelegator;
import cn.spider.framework.linker.server.loadbalancer.timedcounter.ScheduledCounter;
import cn.spider.framework.linker.server.loadbalancer.utils.HashIndexedPriorityQueue;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HeapConcurrencyLoadBalancer distribute interaction to the least concurrent entity.
 * Comparing with roundRobin or random loadBalancer generally, it can achieve optimal throughput and latency
 *
 * <P>
 * HeapConcurrencyLoadBalancer locates least concurrent entity by polling from min-heap ordered by concurrency
 * with time complexity o(1).
 * HeapConcurrencyLoadBalancer update entity concurrency counter with time complexity O(logn), n indicate number of tasks
 * </p>
 *
 * <P>
 * Advantage of HeapConcurrencyLoadBalancer is to achieve high accuracy with optimal time complexity
 * but multiple instances of HeapConcurrencyLoadBalancer can't share concurrency state.
 * </P>
 * Example:
 * <pre>
 * ArrayList<String> urls = new ArrayList<String>() {{add("http://192.168.0.1:80"); add("http://192.168.0.2:80");}};
 *
 * HeapConcurrencyLoadBalancer<String> loadBalancer = HeapConcurrencyLoadBalancer.newBuilder()
 *                 .withTasks(urls)
 *                 .build();
 * CompletableTask<String> url = loadBalancer.next();
 * boolean succeed = doPost(url.getTask()); //make rest call with url.getTask()
 * url.complete(succeed);                   //finish the task
 *
 * </pre>
 * @param <T> the type parameter
 */
public final class HeapConcurrencyLoadBalancer<T> extends AbstractConcurrencyLoadBalancer<T> {
    private final TaskConcurrencyQueue<T> taskConcurrencyQueue;

    /**
     * Instantiates a new HeapConcurrencyLoadBalancer
     *
     * @param taskConcurrencyQueue concurrency state storage
     */
    HeapConcurrencyLoadBalancer(TaskConcurrencyQueue<T> taskConcurrencyQueue, List<CompletableTask.Listener<T>> listeners, Ticker ticker) {
        super(taskConcurrencyQueue.tasks, listeners, ticker);
        this.taskConcurrencyQueue = taskConcurrencyQueue;
    }

    @Override
    public CompletableTask<T> next() {
        TaskConcurrency<T> taskConcurrency = taskConcurrencyQueue.peek();
        if (taskConcurrency == null || Integer.compareUnsigned(Integer.MAX_VALUE, taskConcurrency.getConcurrency()) < 0) {
            return null; // no tasks or all tasks reached concurrency limits
        }
        return new ConcurrentTaskImpl(ticker.read(), taskConcurrency);
    };

    @VisibleForTesting
    TaskConcurrencyQueue<T> getTaskConcurrencyQueue() {
      return taskConcurrencyQueue;
    }

    /**
     * Min-Heap based concurrency state storage
     * support get least concurrent entity with O(1) time complexity
     * update concurrency counter of entity with O(logn) time complexity
     *
     * To prevent surge of failure, specify timeout to treat failure as timeout
     * <pre>
     * HeapConcurrencyLoadBalancer<String> loadBalancer = HeapConcurrencyLoadBalancer.newBuilder(String.class)
     *                 .withTasks(entries)
     *                 .withTimeout(Duration.ofSeconds(10))
     *                 .build();
     * </pre>
     * @param <T> the type parameter
     */
    protected static class TaskConcurrencyQueue<T> {
        private final int size;
        private final ImmutableList<T> tasks;
        private final ImmutableList<TaskConcurrency<T>> taskConcurrences;
        private final AtomicInteger iter = new AtomicInteger();
        private final HashIndexedPriorityQueue<TaskConcurrency<T>> queue;

        /**
         * Instantiates a TaskConcurrencyQueue with sustain period of failed task
         *
         * @param scheduledCounterBuilder the timeout
         */
        private TaskConcurrencyQueue(Collection<T> tasks, TaskConcurrency.Builder taskConcurrencyBuilder , ScheduledCounter.Builder scheduledCounterBuilder) {
            this.queue = new HashIndexedPriorityQueue<>();
            this.tasks = ImmutableList.copyOf(tasks);
            for (T t : tasks) {
                TaskConcurrency<T> task = new HeapTaskConcurrency(new ScheduledTaskConcurrency(taskConcurrencyBuilder.build(t), scheduledCounterBuilder));
                queue.offer(task);
            }
            this.taskConcurrences = ImmutableList.copyOf(queue);
            this.size = taskConcurrences.size();
        }

        public TaskConcurrency<T> peek() {
            if (size == 0) {
                return null;
            }

            /**
             * on completion of failed requests, instead of reducing task concurrency right away
             * we reduce concurrency with delay to avoid more number of requests assigned to failed task
             * syncState() will check scheduledCounter to see if there is matured count can be reduced
             */
            int index = iter.get();
            if (iter.compareAndSet(index, (index+1) % size)) {
                taskConcurrences.get(index).syncState();
            }

            return queue.peek();
        }

        @VisibleForTesting
        TaskConcurrency<T> get(T t) {
            for (TaskConcurrency<T> tc : queue) {
                if (tc.getTask() == t) {
                    return tc;
                }
            }
            return null;
        }

        private synchronized void syncUpdate(TaskConcurrency<T> tc, Runnable update) {
            update.run();
            queue.offer(tc);
        }

        private class HeapTaskConcurrency extends TaskConcurrencyDelegator {
            /**
             * Instantiates a new instance.
             *
             * @param delegate the delegate
             */
            HeapTaskConcurrency(ScheduledTaskConcurrency<T> delegate) {
                super(delegate);
            }

            @Override
            public void acquire() {
                syncUpdate(this, ()->super.acquire());
            }

            @Override
            public void complete(boolean succeed, Duration latency) {
                syncUpdate(this, ()->super.complete(succeed, latency));
            }

            @Override
            public void syncState() {
                syncUpdate(this, ()->super.syncState());
            }
        }
    }

    /**
     * New builder of HeapConcurrencyLoadBalancer
     *
     * @param <T> the type parameter
     * @return the tasks heap concurrency load balancer builder
     */
    public static <T> Builder<T> newBuilder(Class<T> cls) {
        return new Builder<>();
    }

    /**
     * The type Final heap concurrency load balancer builder.
     *
     * @param <T> the type parameter
     */
    public final static class Builder<T>
            extends AbstractBuilder<T, Builder<T>> {
        /**
         * Build a HeapConcurrencyLoadBalancer
         *
         * @return the heap concurrency load balancer
         */
        public HeapConcurrencyLoadBalancer<T> build() {
            TaskConcurrency.Builder taskConcurrencyBuilder = subStrategy.newTaskConcurrencyBuilder().withLookBackTime(lookBackTime);

            TaskConcurrencyQueue<T> taskConcurrencyRepo = new TaskConcurrencyQueue<>(tasks, taskConcurrencyBuilder, scheduledCounterBuilder);
            return new HeapConcurrencyLoadBalancer(taskConcurrencyRepo, listeners, scheduledCounterBuilder.getTicker());
        }
    }
}
