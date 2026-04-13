package com.bytefuture.easy.poster.element.chart;

import java.awt.*;

/**
 * 漏斗图阶段数据。
 * <p>
 * 表示漏斗图中的单个阶段，包含名称、数值和可选的颜色配置。
 * </p>
 *
 * @author biaoy
 * @since 2026/04/13
 */
public class FunnelChartStage {

    /**
     * 阶段名称。
     */
    private final String name;

    /**
     * 阶段数值，必须为正数。
     */
    private final double value;

    /**
     * 阶段颜色，为null时使用调色板颜色。
     */
    private Color color;

    /**
     * 构造阶段数据。
     *
     * @param name  阶段名称
     * @param value 阶段数值（必须为正数）
     */
    public FunnelChartStage(String name, Number value) {
        this.name = name;
        this.value = value == null ? 0D : value.doubleValue();
    }

    /**
     * 设置阶段颜色。
     *
     * @param color 阶段颜色
     * @return 当前阶段
     */
    public FunnelChartStage setColor(Color color) {
        this.color = color;
        return this;
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

    /**
     * 创建阶段数据。
     *
     * @param name  阶段名称
     * @param value 阶段数值
     * @return 阶段数据
     */
    public static FunnelChartStage of(String name, Number value) {
        return new FunnelChartStage(name, value);
    }

    /**
     * 创建带颜色的阶段数据。
     *
     * @param name  阶段名称
     * @param value 阶段数值
     * @param color 阶段颜色
     * @return 阶段数据
     */
    public static FunnelChartStage of(String name, Number value, Color color) {
        return new FunnelChartStage(name, value).setColor(color);
    }
}
