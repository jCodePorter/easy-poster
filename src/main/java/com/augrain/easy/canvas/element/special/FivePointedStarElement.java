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
     * 圆形构造器
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
        // 绘制5个四边形
        Polygon polygon = new Polygon();
        polygon.addPoint(point0[0], point0[1]);
        polygon.addPoint(point2[0], point2[1]);
        polygon.addPoint(point5[0], point5[1]);
        polygon.addPoint(point3[0], point3[1]);
        g.fillPolygon(polygon);

        polygon = new Polygon();
        polygon.addPoint(point1[0], point1[1]);
        polygon.addPoint(point3[0], point3[1]);
        polygon.addPoint(point5[0], point5[1]);
        polygon.addPoint(point4[0], point4[1]);
        g.fillPolygon(polygon);

        polygon = new Polygon();
        polygon.addPoint(point2[0], point2[1]);
        polygon.addPoint(point4[0], point4[1]);
        polygon.addPoint(point5[0], point5[1]);
        polygon.addPoint(point0[0], point0[1]);
        g.fillPolygon(polygon);

        polygon = new Polygon();
        polygon.addPoint(point3[0], point3[1]);
        polygon.addPoint(point0[0], point0[1]);
        polygon.addPoint(point5[0], point5[1]);
        polygon.addPoint(point1[0], point1[1]);
        g.fillPolygon(polygon);

        polygon = new Polygon();
        polygon.addPoint(point4[0], point4[1]);
        polygon.addPoint(point2[0], point2[1]);
        polygon.addPoint(point5[0], point5[1]);
        polygon.addPoint(point1[0], point1[1]);
        g.fillPolygon(polygon);
        return dimension.getPoint();
    }

    @Override
    public void beforeRender(Graphics2D g) {
        g.setColor(this.color);
    }
}
