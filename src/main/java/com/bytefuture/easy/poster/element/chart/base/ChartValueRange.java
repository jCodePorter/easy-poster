package com.bytefuture.easy.poster.element.chart.base;

/**
 * Shared numeric value range for cartesian charts.
 */
public class ChartValueRange {

    private final double min;

    private final double max;

    public ChartValueRange(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
