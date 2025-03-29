package com.augrain.easy.canvas.element.special;

import com.augrain.easy.canvas.element.AbstractDimensionElement;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;

import java.awt.*;

/**
 * 五角星
 *
 * @author biaoy
 * @since 2025/03/18
 */
public class FivePointedStarElement extends AbstractDimensionElement {

    private int borderSize = 0;

    /**
     * 填充颜色或者边框颜色
     */
    private Color color = Color.BLACK;

    /**
     * 五角星构造器
     *
     * @param radius 半径
     */
    public FivePointedStarElement(final int radius) {
        this.width = radius * 2;
        this.height = radius * 2;
    }

    public FivePointedStarElement setColor(final Color color) {
        this.color = color;
        return this;
    }

    public FivePointedStarElement setBorderSize(final int borderSize) {
        this.borderSize = borderSize;
        return this;
    }

    @Override
    public CoordinatePoint doRender(Graphics2D g, Dimension dimension, int canvasWidth, int canvasHeight) {
        // 绘制五角星。五角星分别有5个顶点，1个中心。绘制五个四边形，绘制方法：每个顶点（称为'A'点）连接不相邻的两个顶点（称为'B'点、'C'点）,五角星的正中心点（称为'D'点），'B'点、'C'点分别连接'D'点。
        int centerX = dimension.getPoint().getX() + width / 2;
        int radius = width / 2;
        // 顶点坐标（5个）point0:中心点正上方顶点，从point0右边开始数顶点依次为point1，point2，point3，point4
        int[] point0 = new int[]{centerX, centerX - radius};
        int[] point1 = new int[]{centerX + (int) (Math.sin(0.4 * Math.PI) * radius), centerX - (int) (Math.cos(0.4 * Math.PI) * radius)};
        int[] point2 = new int[]{centerX + (int) (Math.sin(0.2 * Math.PI) * radius), centerX + (int) (Math.cos(0.2 * Math.PI) * radius)};
        int[] point3 = new int[]{centerX - (int) (Math.sin(0.2 * Math.PI) * radius), centerX + (int) (Math.cos(0.2 * Math.PI) * radius)};
        int[] point4 = new int[]{centerX - (int) (Math.sin(0.4 * Math.PI) * radius), centerX - (int) (Math.cos(0.4 * Math.PI) * radius)};
        // 中心坐标
        int[] point5 = new int[]{centerX, centerX};

        if (this.borderSize > 0) {
            g.setStroke(new BasicStroke(this.borderSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawLine(g, point0, point1, point2, point3, point4, point5);
        } else {
            drawPolygon(g, point0, point1, point2, point3, point4, point5);
        }
        return dimension.getPoint();
    }

    private static void drawLine(Graphics2D g, int[] point0, int[] point1, int[] point2, int[] point3, int[] point4, int[] point5) {
        doDrawLine(g, point0, point2, point1, point4);
        doDrawLine(g, point1, point3, point2, point0);
        doDrawLine(g, point2, point4, point3, point1);
        doDrawLine(g, point3, point0, point4, point2);
        doDrawLine(g, point4, point1, point0, point3);
    }

    private static void doDrawLine(Graphics2D g, int[] p1, int[] p2, int[] q1, int[] q2) {
        int[] p = findLineIntersection(p1, p2, q1, q2);
        if (p == null) {
            return;
        }
        g.drawLine(p1[0], p1[1], p[0], p[1]);
        g.drawLine(q1[0], q1[1], p[0], p[1]);
    }

    private static int[] findLineIntersection(int[] p1, int[] p2, int[] q1, int[] q2) {
        // 提取坐标
        double x1 = p1[0], y1 = p1[1];
        double x2 = p2[0], y2 = p2[1];
        double x3 = q1[0], y3 = q1[1];
        double x4 = q2[0], y4 = q2[1];

        // 计算分母
        double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        // 如果分母为 0，说明两条直线平行或重合
        if (denominator == 0) {
            return null;
        }

        // 计算交点坐标
        double px = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / denominator;
        double py = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / denominator;
        return new int[]{(int) px, (int) py};
    }

    private static void drawPolygon(Graphics2D g, int[] point0, int[] point1, int[] point2, int[] point3, int[] point4, int[] point5) {
        // 绘制5个四边形
        doDrawPolygon(g, point0, point2, point3, point5);
        doDrawPolygon(g, point1, point3, point5, point4);
        doDrawPolygon(g, point2, point4, point5, point0);
        doDrawPolygon(g, point3, point0, point5, point1);
        doDrawPolygon(g, point4, point2, point5, point1);
    }

    private static void doDrawPolygon(Graphics2D g, int[] point0, int[] point2, int[] point3, int[] point5) {
        Polygon polygon = new Polygon();
        polygon.addPoint(point0[0], point0[1]);
        polygon.addPoint(point2[0], point2[1]);
        polygon.addPoint(point5[0], point5[1]);
        polygon.addPoint(point3[0], point3[1]);
        g.fillPolygon(polygon);
    }

    @Override
    public void beforeRender(Graphics2D g) {
        g.setColor(this.color);
    }
}
