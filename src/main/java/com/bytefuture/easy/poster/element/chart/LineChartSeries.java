package com.bytefuture.easy.poster.element.chart;

import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 折线图系列数据
 *
 * @author biaoy
 * @since 2026/04/13
 */
@Getter
public class LineChartSeries {

    private final String name;

    private final List<Double> values = new ArrayList<Double>();

    private Color color;

    public LineChartSeries(String name, List<? extends Number> values) {
        this.name = name;
        if (values != null) {
            for (Number value : values) {
                this.values.add(value == null ? 0D : value.doubleValue());
            }
        }
    }

    public LineChartSeries setColor(Color color) {
        this.color = color;
        return this;
    }

    public static LineChartSeries of(String name, List<? extends Number> values) {
        return new LineChartSeries(name, values);
    }
}
