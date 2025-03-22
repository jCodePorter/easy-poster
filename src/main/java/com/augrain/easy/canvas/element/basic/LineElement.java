package com.augrain.easy.canvas.element.basic;

import com.augrain.easy.canvas.element.AbstractElement;
import com.augrain.easy.canvas.element.IElement;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;

import java.awt.*;

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
        return null;
    }

    @Override
    public CoordinatePoint doRender(Graphics2D g, Dimension dimension, int canvasWidth, int canvasHeight) {
        g.drawLine(this.start.getX(), start.getY(), this.end.getX(), this.end.getY());
        return this.start;
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
