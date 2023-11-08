package cn.spider.framework.linker.server.loadbalancer;

import javax.annotation.Nonnull;

/**
 * The result type of ConcurrencyLoadBalancer.next()
 * it defines callback function to be invoked by caller when task completes
 * ConcurrencyLoadBalancer use this callback function to update its internal concurrency counters.
 *
 * @param <T> the entity type returned by ConcurrencyLoadBalancer
 */
public interface CompletableTask<T> {
    /**
     * get the wrapped entity
     *
     * @return the entity
     */
    @Nonnull
    T getTask();

    /**
     * invoked by caller to indicate task completion
     * ConcurrencyLoadBalancer use this callback to update its internal states.
     *
     * @param succeed indicates if the task is succeed or failed, some loadBalancer                differentiate result
     * @return the boolean indicates if completion succeed.
     */
    boolean complete(boolean succeed);

    /**
     * equivalent to complete(true)
     * invoke this function if caller don't want loadBalancer to differentiate result
     *
     * @return the boolean indicates if completion succeed.
     */
    default boolean complete() {
        return complete(true);
    }

    /**
     * Listener can be attached to {@link LeastConcurrencyLoadBalancer}
     * and get notified when {@link CompletableTask} create/complete events happened
     *
     * @param <T> the entity type
     */
    interface Listener<T> {
        /**
         * {@link CompletableTask} create event happens after loadBalancer selected one entity
         * but before caller interact with the entity
         *
         * @param t    the entity being selected
         */
        void onCreate(T t);

        /**
         * {@link CompletableTask} complete event happens after caller finished interact with the entity
         * but before loadBalancer mark the entity as complete
         *
         * @param t       the t
         * @param succeed the succeed
         */
        void onComplete(T t, boolean succeed);
    }

    /**
     * create Noop instance of CompletableTask
     *
     * @param <T> the type parameter
     * @param t   the task entity
     * @return the completable task
     */
    static <T> CompletableTask<T> ofNoop(@Nonnull T t) {
        return new NoopCompletableTask(t);
    }

    /**
     * No operation CompletableTask
     *
     * @param <T> the type parameter
     */
    class NoopCompletableTask<T> implements CompletableTask<T> {
        final T t;

        private NoopCompletableTask(@Nonnull T t) {
            this.t = t;
        }

        @Override
        public @Nonnull T getTask() {
            return t;
        }

        @Override
        public boolean complete(boolean succeed) {
            return true;
        }
    }
}
