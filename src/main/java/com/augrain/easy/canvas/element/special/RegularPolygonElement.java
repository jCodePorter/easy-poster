package com.augrain.easy.canvas.element.special;

import com.augrain.easy.canvas.element.AbstractDimensionElement;
import com.augrain.easy.canvas.element.IElement;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;
import com.augrain.easy.canvas.model.CanvasContext;

import java.awt.*;

/**
 * 正N边形
 *
 * @author biaoy
 * @since 2025/03/29
 */
public class RegularPolygonElement extends AbstractDimensionElement<RegularPolygonElement> implements IElement {

    /**
     * 边数
     */
    private final int edges;

    /**
     * 填充颜色或者边框颜色
     */
    private Color color = Color.BLACK;

    /**
     * N边形
     *
     * @param radius 半径
     * @param edges  边数
     */
    public RegularPolygonElement(final int radius, final int edges) {
        this.width = radius * 2;
        this.height = radius * 2;
        this.edges = edges;
    }

    public RegularPolygonElement setColor(final Color color) {
        this.color = color;
        return this;
    }

    @Override
    public CoordinatePoint doRender(CanvasContext context, Dimension dimension, int canvasWidth, int canvasHeight) {
        int r = this.width / 2;
        int centerX = dimension.getPoint().getX() + r;
        int centerY = dimension.getPoint().getY() + r;

        double thetaOffset = Math.PI / this.edges;
        CoordinatePoint[] points = new CoordinatePoint[this.edges + 1];
        for (int i = 0; i < this.edges; i++) {
            double xi = centerX + r * Math.cos(Math.PI * 2 * i / this.edges - thetaOffset);
            double yi = centerY + r * Math.sin(Math.PI * 2 * i / this.edges - thetaOffset);
            points[i] = CoordinatePoint.of((int) xi, (int) yi);
        }
        points[this.edges] = points[0];

        Graphics2D g = context.getGraphics();
        for (int i = 0; i < this.edges; i++) {
            g.drawLine(points[i].getX(), points[i].getY(), points[i + 1].getX(), points[i + 1].getY());
        }
        return dimension.getPoint();
    }

    @Override
    public void beforeRender(CanvasContext context) {
        super.beforeRender(context);
        context.getGraphics().setColor(color);
    }
}
