package cn.spider.framework.linker.server.loadbalancer.utils;

import com.google.common.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This sampler can sample from multiple candidates.
 * The probability each candidate being selected is same.
 *
 * Example:
 * <pre>
 * ReservoirSampler<Entity> sampler = new ReservoirSampler();
 * for(Entity e : entities) {
 *     sampler.sample(e);
 * }
 * Entity selected = sampler.get();
 * </pre>
 * @param <T> the entity type
 */
public final class ReservoirSampler<T> {

    private final Random rand;
    private final int nSample;
    private int sampledTimes;
    private List<T> result;

    /**
     * Instantiates a new Reservoir sampler with a specific random number generator
     *
     * @param rand the rand
     */
    @VisibleForTesting
    ReservoirSampler(int nSample, Random rand) {
        reset();
        this.rand = rand;
        this.nSample = nSample;
    }

    /**
     * Instantiates a new Reservoir sampler
     *
     * @param nSample the number of samples to take
     */
    public ReservoirSampler(int nSample) {
        this(nSample, new Random());
    }

    /**
     * Instantiates a new Reservoir sampler
     * The sample only take one sample
     */
    public ReservoirSampler() {
        this(1);
    }

    /**
     * Reset state of the sampler;
     */
    public void reset() {
        sampledTimes = 0;
        result = new ArrayList<>();
    }

    /**
     * Sample an entity
     *
     * @param t the t
     */
    public void sample(T t) {
        if (sampledTimes == Integer.MAX_VALUE) {
            return;
        }

        if (result.size() < nSample) {
            result.add(t);
        } else {
            int index = rand.nextInt(1 + sampledTimes);
            if (index < nSample) {
                result.set(index, t);
            }
        }
        sampledTimes++;
    }

    /**
     * Gets all samples
     *
     * @return selected entities
     */
    public Iterable<T> getSamples() {
        return result;
    }

    /**
     * Gets one sample.
     *
     * @return the sample
     */
    public T getSample() {
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }
}
