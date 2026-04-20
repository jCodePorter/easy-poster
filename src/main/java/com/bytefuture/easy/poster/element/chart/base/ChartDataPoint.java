package com.bytefuture.easy.poster.element.chart.base;

import java.awt.Color;

/**
 * Shared chart data point with name, value and optional color.
 */
public class ChartDataPoint {

    private final String name;

    private final double value;

    private Color color;

    public ChartDataPoint(String name, Number value) {
        this.name = name;
        this.value = value == null ? 0D : value.doubleValue();
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }

    protected void setColorInternal(Color color) {
        this.color = color;
    }
}
