package com.bytefuture.easy.poster.element.chart;

import com.bytefuture.easy.poster.element.chart.base.ChartData;

import java.awt.Color;

/**
 * Funnel chart stage data.
 */
public class FunnelChartStage extends ChartData {

    public FunnelChartStage(String name, Number value) {
        super(name, value);
    }

    public FunnelChartStage setColor(Color color) {
        setColorInternal(color);
        return this;
    }

    public static FunnelChartStage of(String name, Number value) {
        return new FunnelChartStage(name, value);
    }

    public static FunnelChartStage of(String name, Number value, Color color) {
        return new FunnelChartStage(name, value).setColor(color);
    }
}
