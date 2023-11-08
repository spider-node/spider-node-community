package cn.spider.framework.linker.server.loadbalancer.utils;

import com.google.common.math.DoubleMath;

/**
 * MathUtils
 */
public final class MathUtils {
    /**
     * The constant EPSILON.
     */
    public static final double EPSILON = 0.000001;

    /**
     * Divide double if divisor is close to zero return zero
     *
     * @param x the x dividend
     * @param n the n divisor
     * @return the double
     */
    public static double divide(double x, double n) {
        return divide(x, n, 0.0);
    }

    /**
     * Divide double if divisor is close to zero return default result
     *
     * @param x             the x dividend
     * @param n             the n divisor
     * @param defaultResult the default result if divisor is very small
     * @return the double
     */
    public static double divide(double x, double n, double defaultResult) {
        if (DoubleMath.fuzzyEquals(n, 0, EPSILON)) {
            return defaultResult;
        } else {
            return x / n;
        }
    }
}
