package com.bytefuture.easy.poster.element.chart;

import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 柱状图系列数据
 *
 * @author biaoy
 * @since 2026/04/11
 */
@Getter
public class BarChartSeries {

    private final String name;

    private final List<Double> values = new ArrayList<Double>();

    private Color color;

    public BarChartSeries(String name, List<? extends Number> values) {
        this.name = name;
        if (values != null) {
            for (Number value : values) {
                this.values.add(value == null ? 0D : value.doubleValue());
            }
        }
    }

    public BarChartSeries setColor(Color color) {
        this.color = color;
        return this;
    }

    public static BarChartSeries of(String name, List<? extends Number> values) {
        return new BarChartSeries(name, values);
    }
}
