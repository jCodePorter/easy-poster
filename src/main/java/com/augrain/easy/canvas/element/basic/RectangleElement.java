package com.augrain.easy.canvas.element.basic;

import com.augrain.easy.canvas.element.AbstractDimensionElement;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;
import lombok.Getter;

import java.awt.*;

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
    private int borderSize = 0;

    /**
     * 圆角
     */
    private int roundCorner = 0;

    /**
     * 填充颜色或者边框颜色
     */
    private Color color = Color.BLACK;

    public RectangleElement(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public RectangleElement(final int width, final int height, final int borderSize) {
        this.width = width;
        this.height = height;
        this.borderSize = borderSize;
    }

    public RectangleElement setRoundCorner(final int roundCorner) {
        this.roundCorner = roundCorner;
        return this;
    }

    public RectangleElement setColor(final Color color) {
        this.color = color;
        return this;
    }

    @Override
    public CoordinatePoint doRender(Graphics2D g, Dimension dimension, int canvasWidth, int canvasHeight) {
        CoordinatePoint point = dimension.getPoint();
        super.gradient(g, dimension);
        if (this.borderSize > 0) {
            g.setStroke(new BasicStroke(this.borderSize));
            if (this.getRoundCorner() != 0) {
                g.drawRoundRect(point.getX(), point.getY(), dimension.getWidth(), dimension.getHeight(),
                        this.getRoundCorner(), this.getRoundCorner());
            } else {
                g.drawRect(point.getX(), point.getY(), dimension.getWidth(), dimension.getHeight());
            }
        } else {
            if (this.getRoundCorner() != 0) {
                g.fillRoundRect(point.getX(), point.getY(), dimension.getWidth(), dimension.getHeight(),
                        this.getRoundCorner(), this.getRoundCorner());
            } else {
                g.fillRect(point.getX(), point.getY(), dimension.getWidth(), dimension.getHeight());
            }
        }
        return dimension.getPoint();
    }

    @Override
    public void beforeRender(Graphics2D g) {
        g.setColor(this.color);
    }
}
