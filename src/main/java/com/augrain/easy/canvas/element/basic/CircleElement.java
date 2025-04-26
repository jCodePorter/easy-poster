package com.augrain.easy.canvas.element.basic;

import com.augrain.easy.canvas.element.AbstractDimensionElement;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;
import com.augrain.easy.canvas.model.CanvasContext;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Optional;

/**
 * 圆形
 *
 * @author biaoy
 * @since 2025/03/17
 */
public class CircleElement extends AbstractDimensionElement<CircleElement> {

    /**
     * 线宽
     */
    private double borderSize = 0;

    /**
     * 填充颜色或者边框颜色
     */
    private Color color = Color.BLACK;

    /**
     * 圆形构造器
     *
     * @param radius 半径
     */
    public CircleElement(final int radius) {
        this.width = radius * 2;
        this.height = radius * 2;
    }

    /**
     * 圆形构造器，当 width != height 时，绘制的是椭圆
     *
     * @param width  宽度
     * @param height 高度
     */
    public CircleElement(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public CircleElement setBorderSize(final int borderSize) {
        this.borderSize = borderSize;
        return this;
    }

    public CircleElement setColor(final Color color) {
        this.color = color;
        return this;
    }

    @Override
    public CoordinatePoint doRender(CanvasContext context, Dimension dimension, int canvasWidth, int canvasHeight) {
        super.gradient(context, dimension);
        CoordinatePoint point = dimension.getPoint();
        Graphics2D g = context.getGraphics();
        if (this.borderSize > 0 && this.borderSize < Math.max(this.width, this.height)) {
            Ellipse2D outer = new Ellipse2D.Double(point.getX(), point.getY(), this.width, this.height);
            Ellipse2D inner = new Ellipse2D.Double(
                    point.getX() + borderSize,
                    point.getY() + borderSize,
                    width - 2 * borderSize,
                    height - 2 * borderSize
            );
            Area ring = new Area(outer);
            ring.subtract(new Area(inner));
            g.fill(ring);
        } else {
            g.fillOval(point.getX(), point.getY(), this.width, this.height);
        }
        return dimension.getPoint();
    }

    @Override
    public void beforeRender(CanvasContext context) {
        context.getGraphics().setColor(Optional.ofNullable(this.color).orElse(context.getConfig().getColor()));
    }
}
