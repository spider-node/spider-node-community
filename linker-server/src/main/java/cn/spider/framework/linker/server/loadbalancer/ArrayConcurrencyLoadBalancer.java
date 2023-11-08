package cn.spider.framework.linker.server.loadbalancer;

import cn.spider.framework.linker.server.loadbalancer.internal.TaskConcurrency;
import cn.spider.framework.linker.server.loadbalancer.timedcounter.ScheduledCounter;
import cn.spider.framework.linker.server.loadbalancer.utils.ReservoirSampler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Ticker;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ArrayConcurrencyLoadBalancer distribute interaction to the least concurrent entity.
 * Comparing with roundRobin or random loadBalancer generally, it can achieve optimal throughput and latency
 *
 * <P>
 * ArrayConcurrencyLoadBalancer locates least concurrent entity by traversing through entity array.
 * to reduce time complexity of the algorithm, adjust groupSize to divided entities into multiple groups.
 * ArrayConcurrencyLoadBalancer select entity group randomly and traverse through the selected entity group to find approximate least concurrent entity
 * with time complexity O(G), G indicate group size.
 * ArrayConcurrencyLoadBalancer update entity concurrency counter with time complexity O(1)
 * bigger the group size is, more accurate the result could be, and higher CPU overhead could cause.
 * </p>
 *
 * <p>
 * An advantage of ArrayConcurrencyLoadBalancer is multiple loadbalancers can share concurrency state to improve over-all efficiency
 * so that, even low-throughput loadBalancer can get benefit by sharing state with high-throughput loadBalancers
 * </p>
 * Example:
 * <pre>
 * ArrayList<String> entries = new ArrayList<String>() {{add("a"); add("b");}};
 *
 * ArrayConcurrencyLoadBalancer<String> loadBalancer = ArrayConcurrencyLoadBalancer.newBuilder()
 *                 .withTasks(entries)
 *                 .build();
 * loadBalancer.next();
 * </pre>
 *
 * @param <T> the type parameter
 */
public final class ArrayConcurrencyLoadBalancer<T> extends AbstractConcurrencyLoadBalancer<T> {
    private static final TaskConcurrency LEAST_TASK_CONCURRENCY = new TaskConcurrency.Noop(Integer.MAX_VALUE);

    private final WeightedSelector<TaskGroup<T>> weightedSelector;

    /**
     * Instantiates a ArrayConcurrencyLoadBalancer
     *
     * @param tasks              the entity list
     * @param taskConcurrencyMap the concurrency state storage
     * @param groupSize          to balance between accuracy and computation complexity
     * @param listeners          the listeners
     */
    ArrayConcurrencyLoadBalancer(Collection<T> tasks,
                                 Function<T, TaskConcurrency<T>> taskConcurrencyMap,
                                 int groupSize,
                                 List<CompletableTask.Listener<T>> listeners,
                                 Ticker ticker) {
        super(tasks, listeners, ticker);
        //build WeightedSelector
        this.weightedSelector = buildWeightedSelector(tasks, taskConcurrencyMap, groupSize);
    }

    private static <T> WeightedSelector<TaskGroup<T>> buildWeightedSelector(Collection<T> tasks, Function<T, TaskConcurrency<T>> taskConcurrencyMap, int groupSize) {
        if (groupSize <= 0) {
            groupSize = Integer.MAX_VALUE;
        }
        WeightedSelector.WeightedSelectorBuilder<TaskGroup<T>> builder = WeightedSelector.newBuilder();
        int numTaskGroups = tasks.size() / groupSize + ( tasks.size() % groupSize == 0 ? 0 : 1 );
        TaskGroup<T>[] taskGroups = new TaskGroup[numTaskGroups];
        for (int i = 0 ; i < numTaskGroups ; ++i) {
            taskGroups[i] = new TaskGroup<>();
        }
        int i = 0;
        for (T task : tasks) {
            TaskConcurrency<T> taskConcurrency = taskConcurrencyMap.apply(task);
            taskGroups[i++ % numTaskGroups].add(taskConcurrency);
        }
        for (i = 0 ; i < numTaskGroups ; ++i) {
            builder.add(taskGroups[i], taskGroups[i].size());
        }
        return builder.build();
    }

    @Override
    public CompletableTask<T> next() {
        TaskGroup<T> taskGroup = weightedSelector.select();
        if (taskGroup == null) {
            return null;
        }
        TaskConcurrency<T> leastTaskConcurrency = LEAST_TASK_CONCURRENCY;
        ReservoirSampler<TaskConcurrency<T>> sampler = new ReservoirSampler<>();
        for(TaskConcurrency<T> taskConcurrency : taskGroup) {
            taskConcurrency.syncState();
            int diff = taskConcurrency.compareTo(leastTaskConcurrency);
            if (diff < 0) {
                sampler.reset();
                leastTaskConcurrency = taskConcurrency;
            } else if (diff > 0){
                continue;
            }
            sampler.sample(taskConcurrency);
        }
        TaskConcurrency<T> result = sampler.getSample();
        if (result == null) {
            return null; // no tasks or all tasks reached concurrency limits
        }
        return new ConcurrentTaskImpl(ticker.read(), result);
    }

    /**
     * Entity group represent a subset of entities
     *
     * @param <T> the type parameter
     */
    private static class TaskGroup<T> extends ArrayList<TaskConcurrency<T>> {
    }

    /**
     * Entity concurrency storage.
     * The instance can be shared across loadBalancer to optimize both throughput and latency
     * To prevent surge of failure, customize TaskConcurrencyMap with timeout setting
     * <pre>
     * TaskConcurrencyMap<String> taskConcurrencyMap = TaskConcurrencyMap.newBuilder()
     *                      .withTimeout(Duration.ofSeconds(30))
     *                      .build();
     *
     * ArrayConcurrencyLoadBalancer<String> loadBalancer = ArrayConcurrencyLoadBalancer.newBuilder(String.class)
     *                      .withTasks(entries)
     *                      .withTaskConcurrencyMap(taskConcurrencyMap)
     *                      .build();
     * </pre>
     *
     * @param <T> the type parameter
     */
    static class TaskConcurrencyMap<T> implements Function<T, TaskConcurrency<T>> {
        private final ConcurrentHashMap<T, TaskConcurrency<T>> taskToTaskConcurrency;
        private final ScheduledCounter.Builder scheduledCounterBuilder;
        private final TaskConcurrency.Builder taskConcurrencyBuilder;

        /**
         * Instantiates a TaskConcurrencyMap
         *
         * @param taskConcurrencyBuilder  the task concurrency factory
         * @param scheduledCounterBuilder builder of scheduledCounter for efficient error handling
         */
        TaskConcurrencyMap(TaskConcurrency.Builder taskConcurrencyBuilder, ScheduledCounter.Builder scheduledCounterBuilder) {
            this.taskToTaskConcurrency = new ConcurrentHashMap<>();
            this.taskConcurrencyBuilder = taskConcurrencyBuilder;
            this.scheduledCounterBuilder = scheduledCounterBuilder;
        }

        @Override
        public TaskConcurrency<T> apply(T t) {
            Objects.requireNonNull(t);
            return taskToTaskConcurrency.computeIfAbsent(t, o -> new ScheduledTaskConcurrency<>(taskConcurrencyBuilder.build(t), scheduledCounterBuilder));
        }
    }

    /**
     * WeightedSelector can select one entity out of a collection of entities.
     * Probability of a entity being selected scale linearly with comparative weight
     * Example:
     * WeightedSelectorBuilder<String> builder = WeightedSelector.newBuilder();
     * builder.add("a", 5);
     * builder.add("b", 3);
     * builder.add("c", 2);
     * WeightedSelector<String> selector = builder.build();
     * selector.select() // 50% probability get "a", 30% probability get "b" ,20% probability get "c"
     *
     * @param <T> the type parameter
     */
    @VisibleForTesting
    static class WeightedSelector<T> {
        private final TreeMap<Integer, T> weightMap ;
        private final int totalWeight;
        private final Random rand;

        /**
         * Instantiates a new Weighted selector.
         *
         * @param weightMap   the weight map
         * @param totalWeight the total weight
         * @param rand        the rand
         */
        WeightedSelector(TreeMap<Integer, T> weightMap, int totalWeight, Random rand) {
            this.weightMap = weightMap;
            this.totalWeight = totalWeight;
            this.rand = rand;
        }

        /**
         * select one entity out of a collection of entities.
         *
         * @return the entity being selected
         */
        @VisibleForTesting
        protected T select() {
            if (totalWeight == 0) {
                return null;
            }
            int selected = rand.nextInt(totalWeight);
            return weightMap.floorEntry(selected).getValue();
        }

        /**
         * New builder
         *
         * @param <T> the type parameter
         * @return the weighted selector builder
         */
        public static <T> WeightedSelectorBuilder<T> newBuilder() {
            return new WeightedSelectorBuilder<>();
        }

        /**
         * The type Weighted selector builder.
         *
         * @param <T> the type parameter
         */
        @VisibleForTesting
        protected static class WeightedSelectorBuilder<T> {
            private TreeMap<Integer, T> weightMap = new TreeMap<>();
            private int totalWeight = 0;

            /**
             * Add entity with specific weight
             * finally the entity will have weight/totalWeight probability to be selected
             *
             * @param t      the entity
             * @param weight the weight
             */
            @VisibleForTesting
            WeightedSelectorBuilder<T> add(T t, int weight) {
                weight = Math.abs(weight);
                weightMap.put(totalWeight, t);
                totalWeight += weight;
                return this;
            }

            /**
             * Build weighted selector.
             *
             * @return the weighted selector
             */
            WeightedSelector<T> build() {
                return new WeightedSelector<>(weightMap, totalWeight, new Random());
            }
        }
    }

    /**
     * New builder of ArrayConcurrencyLoadBalancer.
     *
     * @param <T> the type parameter
     * @return the tasks array concurrency load balancer builder
     */
    public static <T> Builder<T> newBuilder(Class<T> cls) {
        return new Builder<T>();
    }

    /**
     * Multi-stage builder of ArrayConcurrencyLoadBalancer
     *
     * @param <T> the type parameter
     */
    public static class Builder<T>
            extends AbstractBuilder<T, Builder<T>> {
        private volatile Function<T, TaskConcurrency<T>> taskConcurrencyMap = null;
        private int groupSize = Integer.MAX_VALUE;

        Function<T, TaskConcurrency<T>> getTaskConcurrencyMap() {
            return taskConcurrencyMap;
        }

        /**
         * set group size to balance between accuracy and computation complexity
         * 1 result in random selection
         * Integer.MAX_VALUE result in absolute accurate selection
         *
         * @param groupSize the group size
         * @return the final array concurrency load balancer builder
         */
        public Builder withGroupSize(int groupSize) {
            this.groupSize = groupSize;
            return this;
        }

        /**
         * Build array concurrency load balancer.
         *
         * @return the array concurrency load balancer
         */
        public ArrayConcurrencyLoadBalancer<T> build() {
            if (taskConcurrencyMap == null) {
                synchronized (this) {
                    if (taskConcurrencyMap == null) {
                        TaskConcurrency.Builder taskConcurrencyBuilder = subStrategy.newTaskConcurrencyBuilder().withLookBackTime(lookBackTime);
                        taskConcurrencyMap = new TaskConcurrencyMap<>(taskConcurrencyBuilder, scheduledCounterBuilder);
                    }
                }
            }
            return new ArrayConcurrencyLoadBalancer(tasks, taskConcurrencyMap, groupSize, listeners, scheduledCounterBuilder.getTicker());
        }
    }
}
