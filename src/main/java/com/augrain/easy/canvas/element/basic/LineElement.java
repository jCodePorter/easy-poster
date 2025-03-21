package com.augrain.easy.canvas.element.basic;

import com.augrain.easy.canvas.element.AbstractRepeatableElement;
import com.augrain.easy.canvas.element.IElement;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;
import com.augrain.easy.canvas.utils.PointUtils;

import java.awt.*;
import java.util.Arrays;

/**
 * 线段类型
 *
 * @author biaoy
 * @since 2025/03/21
 */
public class LineElement extends AbstractRepeatableElement<LineElement> implements IElement {

    /**
     * 起始坐标点
     */
    private final CoordinatePoint start;

    /**
     * 结束坐标点
     */
    private final CoordinatePoint end;

    /**
     * 线宽
     */
    private int borderSize = 1;

    /**
     * 颜色
     */
    private Color color = Color.BLACK;

    private boolean dashed = false;

    public LineElement(CoordinatePoint start, CoordinatePoint end) {
        this.start = start;
        this.end = end;
    }

    public LineElement(CoordinatePoint start, CoordinatePoint end, int borderSize) {
        this.start = start;
        this.end = end;
        this.borderSize = borderSize;
    }

    public LineElement setColor(final Color color) {
        this.color = color;
        return this;
    }

    public LineElement setDashed(final boolean dashed) {
        this.dashed = dashed;
        return this;
    }

    @Override
    public Dimension calculateDimension(Graphics2D g, int canvasWidth, int canvasHeight) {
        Dimension dimension = PointUtils.boundingBox(Arrays.asList(this.start, this.end));
        CoordinatePoint point = dimension.getPoint();
        if (position != null) {
            point = position.calculate(canvasWidth, canvasHeight, dimension.getWidth(), dimension.getHeight());
        }
        dimension.setPoint(point);
        return dimension;
    }

    @Override
    public CoordinatePoint doRender(Graphics2D g, Dimension dimension, int canvasWidth, int canvasHeight) {
        CoordinatePoint point = dimension.getPoint();
        g.drawLine(point.getX(), point.getY(), point.getX() + dimension.getWidth(), point.getY() + dimension.getHeight());
        return dimension.getPoint();
    }

    @Override
    public void beforeRender(Graphics2D g) {
        g.setColor(this.color);
        if (this.dashed) {
            float[] dotPattern = {2, 5};
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10, dotPattern, 0));
        } else {
            if (this.borderSize > 1) {
                g.setStroke(new BasicStroke(this.borderSize));
            }
        }
    }
}
