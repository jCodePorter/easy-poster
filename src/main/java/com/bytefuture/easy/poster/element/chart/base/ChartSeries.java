package com.bytefuture.easy.poster.element.chart.base;

import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据系列
 */
@Getter
public class ChartSeries {

    /**
     * 系列名称，用于图例展示。
     */
    private final String name;

    /**
     * 与分类一一对应的数值列表。
     */
    private final List<Double> values = new ArrayList<>();

    /**
     * 当前系列的显示颜色，未设置时由图表调色板补足。
     */
    private Color color;

    /**
     * 使用系列名称和值列表创建数据系列。
     */
    public ChartSeries(String name, List<? extends Number> values) {
        this.name = name;
        if (values != null) {
            // 统一将外部 Number 转成 double，并把 null 当成 0 处理。
            for (Number value : values) {
                this.values.add(value == null ? 0D : value.doubleValue());
            }
        }
    }

    /**
     * 快速创建柱状图数据系列。
     */
    public static ChartSeries of(String name, List<? extends Number> values) {
        return new ChartSeries(name, values);
    }

    /**
     * 设置系列颜色。
     */
    public ChartSeries setColor(Color color) {
        this.color = color;
        return this;
    }
}
