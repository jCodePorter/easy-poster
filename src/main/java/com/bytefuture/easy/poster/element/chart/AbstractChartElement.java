package com.bytefuture.easy.poster.element.chart;

import com.bytefuture.easy.poster.element.AbstractDimensionElement;
import com.bytefuture.easy.poster.element.chart.base.ChartLayoutBox;
import com.bytefuture.easy.poster.element.chart.base.ChartStyle;
import com.bytefuture.easy.poster.element.chart.base.ChartTextSupport;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;

import java.awt.*;

/**
 * 图表抽象类
 */
public abstract class AbstractChartElement<T extends AbstractChartElement<T>> extends AbstractDimensionElement<T> {

    protected final ChartStyle chartStyle = new ChartStyle();

    @Override
    public final Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        validateChartData();
        Graphics2D graphics = context.getGraphics();
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            Point origin = dimension.getPoint();
            fillBackground(g, origin);
            renderChart(g, context, resolveInnerBox(origin));
        } finally {
            g.dispose();
        }
        return dimension.getPoint();
    }

    protected abstract void validateChartData();

    protected abstract void renderChart(Graphics2D g, PosterContext context, ChartLayoutBox box);

    protected Font resolveBaseFont(PosterContext context) {
        return ChartTextSupport.resolveBaseFont(context);
    }

    protected void fillBackground(Graphics2D g, Point origin) {
        if (chartStyle.getBackgroundColor() == null) {
            return;
        }
        g.setColor(chartStyle.getBackgroundColor());
        g.fillRect(origin.getX(), origin.getY(), width, height);
    }

    protected ChartLayoutBox resolveInnerBox(Point origin) {
        Insets padding = chartStyle.getPadding();
        return new ChartLayoutBox(
                origin.getX() + padding.left,
                origin.getY() + padding.top,
                origin.getX() + width - padding.right,
                origin.getY() + height - padding.bottom
        );
    }

    protected Insets getPadding() {
        return chartStyle.getPadding();
    }

    protected void setPaddingInternal(Insets padding) {
        chartStyle.setPadding(padding);
    }

    protected Color getBackgroundColor() {
        return chartStyle.getBackgroundColor();
    }

    protected void setBackgroundColorInternal(Color backgroundColor) {
        chartStyle.setBackgroundColor(backgroundColor);
    }

    protected Color getLabelColor() {
        return chartStyle.getLabelColor();
    }

    protected void setLabelColorInternal(Color labelColor) {
        chartStyle.setLabelColor(labelColor);
    }

    protected Color chooseReadableLabelColor(Color background) {
        return ChartTextSupport.chooseReadableTextColor(background);
    }
}
