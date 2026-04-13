package com.bytefuture.easy.poster.element.chart;

import lombok.Getter;

import java.awt.*;

/**
 * 饼图切片数据。
 *
 * @author biaoy
 * @since 2026/04/13
 */
@Getter
public class PieChartSlice {

    /**
     * 切片名称。
     */
    private final String name;

    /**
     * 切片数值。
     */
    private final double value;

    /**
     * 切片颜色。
     */
    private Color color;

    /**
     * 构造切片数据。
     *
     * @param name  切片名称
     * @param value 切片数值
     */
    public PieChartSlice(String name, Number value) {
        this.name = name;
        this.value = value == null ? 0D : value.doubleValue();
    }

    /**
     * 设置切片颜色。
     *
     * @param color 切片颜色
     * @return 当前切片
     */
    public PieChartSlice setColor(Color color) {
        this.color = color;
        return this;
    }

    /**
     * 创建切片数据。
     *
     * @param name  切片名称
     * @param value 切片数值
     * @return 切片数据
     */
    public static PieChartSlice of(String name, Number value) {
        return new PieChartSlice(name, value);
    }

    /**
     * 创建带颜色的切片数据。
     *
     * @param name  切片名称
     * @param value 切片数值
     * @param color 切片颜色
     * @return 切片数据
     */
    public static PieChartSlice of(String name, Number value, Color color) {
        return new PieChartSlice(name, value).setColor(color);
    }
}
