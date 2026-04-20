package com.bytefuture.easy.poster.element.chart;

import com.bytefuture.easy.poster.element.chart.base.ChartDataPoint;

import java.awt.Color;

/**
 * Pie chart slice data.
 */
public class PieChartSlice extends ChartDataPoint {

    public PieChartSlice(String name, Number value) {
        super(name, value);
    }

    public PieChartSlice setColor(Color color) {
        setColorInternal(color);
        return this;
    }

    public static PieChartSlice of(String name, Number value) {
        return new PieChartSlice(name, value);
    }

    public static PieChartSlice of(String name, Number value, Color color) {
        return new PieChartSlice(name, value).setColor(color);
    }
}
