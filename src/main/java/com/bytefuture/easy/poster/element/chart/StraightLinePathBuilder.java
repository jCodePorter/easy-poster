package com.bytefuture.easy.poster.element.chart;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * 直线路径构建器
 *
 * @author biaoy
 * @since 2026/04/13
 */
public class StraightLinePathBuilder implements LinePathBuilder {

    @Override
    public Shape buildPath(List<Point2D.Double> points, double tension) {
        Path2D.Double path = new Path2D.Double();
        if (points == null || points.isEmpty()) {
            return path;
        }
        Point2D.Double first = points.get(0);
        path.moveTo(first.x, first.y);
        for (int i = 1; i < points.size(); i++) {
            Point2D.Double point = points.get(i);
            path.lineTo(point.x, point.y);
        }
        return path;
    }
}
