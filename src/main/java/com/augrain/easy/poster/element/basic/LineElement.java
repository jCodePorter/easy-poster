package com.augrain.easy.poster.element.basic;

import com.augrain.easy.poster.element.AbstractElement;
import com.augrain.easy.poster.element.IElement;
import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.geometry.Dimension;
import com.augrain.easy.poster.model.PosterContext;
import com.augrain.easy.poster.model.LineStyle;

import java.awt.*;
import java.util.Optional;

/**
 * 线段类型
 *
 * @author biaoy
 * @since 2025/03/21
 */
public class LineElement extends AbstractElement<LineElement> implements IElement {

    /**
     * 起始坐标点
     */
    private final Point start;

    /**
     * 结束坐标点
     */
    private final Point end;

    /**
     * 线宽
     */
    private int borderSize = 1;

    /**
     * 颜色
     */
    private Color color = Color.BLACK;

    /**
     * 线段样式
     */
    private LineStyle lineStyle;

    public LineElement(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public LineElement(Point start, Point end, int borderSize) {
        this.start = start;
        this.end = end;
        this.borderSize = borderSize;
    }

    public LineElement setColor(final Color color) {
        this.color = color;
        return this;
    }

    public LineElement setLineStyle(final LineStyle lineStyle) {
        this.lineStyle = lineStyle;
        return this;
    }

    public LineElement setBorderSize(final int borderSize) {
        this.borderSize = borderSize;
        return this;
    }

    @Override
    public Dimension calculateDimension(PosterContext context, int posterWidth, int posterHeight) {
        return null;
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        context.getGraphics().drawLine(this.start.getX(), start.getY(), this.end.getX(), this.end.getY());
        return this.start;
    }

    @Override
    public void beforeRender(PosterContext context) {
        Graphics2D g = context.getGraphics();
        g.setColor(Optional.ofNullable(this.color).orElse(context.getConfig().getColor()));
        if (this.lineStyle != null) {
            g.setStroke(this.lineStyle.toStroke(this.borderSize));
        } else {
            g.setStroke(new BasicStroke(this.borderSize));
        }
    }
}
