package com.augrain.easy.canvas.element.basic;

import com.augrain.easy.canvas.element.AbstractDimensionElement;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;

import java.awt.*;

/**
 * 圆形
 *
 * @author biaoy
 * @since 2025/03/17
 */
public class CircleElement extends AbstractDimensionElement<CircleElement> {

    private int borderSize = 0;

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
    public CoordinatePoint doRender(Graphics2D g, Dimension dimension, int canvasWidth, int canvasHeight) {
        super.gradient(g, dimension);
        CoordinatePoint point = dimension.getPoint();
        if (this.borderSize > 0) {
            g.setStroke(new BasicStroke(this.borderSize));
            g.drawOval(point.getX(), point.getX(), this.width, this.height);
        } else {
            g.fillOval(point.getX(), point.getX(), this.width, this.height);
        }
        return dimension.getPoint();
    }

    @Override
    public void beforeRender(Graphics2D g) {
        g.setColor(this.color);
    }
}
