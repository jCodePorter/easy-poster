package com.bytefuture.easy.poster.element.chart.base;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * 图标数据
 */
@Getter
@Setter
public class ChartData {

    private final String name;

    private final double value;

    private Color color;

    public ChartData(String name, Number value) {
        this.name = name;
        this.value = value == null ? 0D : value.doubleValue();
    }

    public ChartData(String name, Number value, Color color) {
        this.name = name;
        this.value = value == null ? 0D : value.doubleValue();
        this.color = color;
    }

    public static ChartData of(String name, Number value) {
        return new ChartData(name, value);
    }

    public static ChartData of(String name, Number value, Color color) {
        return new ChartData(name, value, color);
    }

    protected void setColorInternal(Color color) {
        this.color = color;
    }
}
