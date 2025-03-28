package com.augrain.easy.canvas.element.basic;

import com.augrain.easy.canvas.element.AbstractDimensionElement;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

/**
 * 矩形元素
 *
 * @author biaoy
 * @since 2025/03/16
 */
@Getter
public class RectangleElement extends AbstractDimensionElement<RectangleElement> {

    /**
     * 线宽
     */
    private double borderSize = 0;

    /**
     * 圆角宽度
     */
    private double arcWidth = 0;

    /**
     * 圆角高度
     */
    private double arcHeight = 0;

    /**
     * 填充颜色或者边框颜色
     */
    private Color color = Color.BLACK;

    public RectangleElement(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public RectangleElement setArc(final int arc) {
        this.arcWidth = arc;
        this.arcHeight = arc;
        return this;
    }

    public RectangleElement setArc(final int arcWidth, final int arcHeight) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        return this;
    }

    public RectangleElement setColor(final Color color) {
        this.color = color;
        return this;
    }

    public RectangleElement setBorderSize(final int borderSize) {
        this.borderSize = borderSize;
        return this;
    }

    @Override
    public CoordinatePoint doRender(Graphics2D g, Dimension dimension, int canvasWidth, int canvasHeight) {
        super.gradient(g, dimension);
        CoordinatePoint point = dimension.getPoint();

        RoundRectangle2D rect = new RoundRectangle2D.Double(point.getX(), point.getY(), width, height,
                this.arcWidth, this.arcHeight);
        if (this.borderSize > 0 && this.borderSize < Math.max(this.width, this.height)) {
            RoundRectangle2D inner = new RoundRectangle2D.Double(
                    point.getX() + borderSize,
                    point.getY() + borderSize,
                    width - 2 * borderSize,
                    height - 2 * borderSize,
                    Math.max(0, this.arcWidth - borderSize),
                    Math.max(0, this.arcHeight - borderSize)
            );
            Area outerArea = new Area(rect);
            outerArea.subtract(new Area(inner));
            g.fill(outerArea);
        } else {
            g.fill(rect);
        }
        return dimension.getPoint();
    }

    @Override
    public void beforeRender(Graphics2D g) {
        g.setColor(this.color);
    }
}
