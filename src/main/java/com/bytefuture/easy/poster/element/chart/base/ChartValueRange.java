package com.bytefuture.easy.poster.element.chart.base;

import lombok.Getter;
import lombok.Setter;

/**
 * Shared numeric value range for cartesian charts.
 */
@Getter
@Setter
public class ChartValueRange {

    private final double min;

    private final double max;

    public ChartValueRange(double min, double max) {
        this.min = min;
        this.max = max;
    }
}
