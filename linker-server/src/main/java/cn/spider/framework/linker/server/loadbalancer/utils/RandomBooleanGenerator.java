package cn.spider.framework.linker.server.loadbalancer.utils;

import java.util.PrimitiveIterator;
import java.util.SplittableRandom;


/**
 * RandomBooleanGenerator generates random boolean
 */
public class RandomBooleanGenerator {
    private final PrimitiveIterator.OfLong longStream = new SplittableRandom().longs().iterator();
    private int nBits = 0;
    private long bits = 0L;
    
    public boolean next() {
        if (nBits == 0) {
            bits = longStream.nextLong();
            nBits = Long.SIZE;
        }

        boolean result = (int)(bits % 2) == 0;
        nBits--;
        bits = bits >>> 1;
        return result;
    }
}
