package cn.spider.framework.linker.server.loadbalancer;

public interface LeastConcurrencyLoadBalancer<T> extends LoadBalancer<CompletableTask<T>> {
    LeastConcurrencyLoadBalancer NOOP_INSTANCE = new NoopLeastConcurrencyLoadBalancer();

    class NoopLeastConcurrencyLoadBalancer<T> implements LeastConcurrencyLoadBalancer<T> {
        private static Metrics NOOP_METRICS = new Metrics() {

            @Override
            public double requestRate() {
                return 0;
            }

            @Override
            public double successRate() {
                return 0;
            }

            @Override
            public double failureRate() {
                return 0;
            }

            @Override
            public double requestCOV() {
                return 0;
            }
        };
        /**
         * Instantiates a NoopLeastConcurrencyLoadBalancer
         */
        private NoopLeastConcurrencyLoadBalancer() {
        }

        @Override
        public CompletableTask<T> next() {
            return null;
        }

        @Override
        public Metrics getMetrics() {
            return NOOP_METRICS;
        }
    }

    Metrics getMetrics();

    /**
     * Least concurrency Loadbalancer metrics
     */
    interface Metrics {
        /**
         * per second request rate
         *
         * @return the double
         */
        double requestRate();

        /**
         * per second completion result success rate
         *
         * @return the double
         */
        double successRate();

        /**
         * per second completion result failure rate
         *
         * @return the double
         */
        double failureRate();

        /**
         * Coefficient of variation of request distribution.
         *
         * @return the double
         */
        double requestCOV();
    }
}
