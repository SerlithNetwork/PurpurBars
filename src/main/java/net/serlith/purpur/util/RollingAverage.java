package net.serlith.purpur.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * Rolling average calculator.
 *
 * <p>This code is taken from PaperMC/Paper, licensed under MIT.</p>
 *
 * @author aikar (PaperMC) <a href="https://github.com/PaperMC/Paper/blob/master/Spigot-Server-Patches/0021-Further-improve-server-tick-loop.patch">...</a>
 */
public class RollingAverage {

    private static final BigDecimal[] EMPTY_DECIMAL = new BigDecimal[0];

    private final Queue<BigDecimal> samples;
    private final int windowSize;
    private BigDecimal total = BigDecimal.ZERO;

    public RollingAverage(int windowSize) {
        this.windowSize = windowSize;
        this.samples = new ArrayDeque<>(this.windowSize + 1);
    }

    public void add(BigDecimal num) {
        synchronized (this) {
            this.total = this.total.add(num);
            this.samples.add(num);
            if (this.samples.size() > this.windowSize) {
                this.total = this.total.subtract(this.samples.remove());
            }
        }
    }

    public double getAverage() {
        synchronized (this) {
            if (this.samples.isEmpty()) {
                return 0;
            }
            return this.total.divide(new BigDecimal(this.samples.size()), 30, RoundingMode.HALF_UP).doubleValue();
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public double getMin() {
        synchronized (this) {
            BigDecimal min = null;
            for (BigDecimal sample : this.samples) {
                if (min == null || sample.compareTo(min) < 0) {
                    min = sample;
                }
            }
            return min == null ? 0 : min.doubleValue();
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public double getMax() {
        synchronized (this) {
            BigDecimal max = null;
            for (BigDecimal sample : this.samples) {
                if (max == null || sample.compareTo(max) > 0) {
                    max = sample;
                }
            }
            return max == null ? 0 : max.doubleValue();
        }
    }

    public double getPercentile(double percentile) {
        if (percentile < 0 || percentile > 1) {
            throw new IllegalArgumentException("Invalid percentile: " + percentile);
        }

        BigDecimal[] sortedSamples;
        synchronized (this) {
            if (this.samples.isEmpty()) {
                return 0;
            }
            sortedSamples = this.samples.toArray(EMPTY_DECIMAL);
        }
        Arrays.sort(sortedSamples);

        int rank = (int) Math.ceil(percentile * (sortedSamples.length - 1));
        return sortedSamples[rank].doubleValue();
    }

}
