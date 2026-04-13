package com.bytefuture.easy.poster.element.chart;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * 平滑曲线路径构建器
 *
 * @author biaoy
 * @since 2026/04/13
 */
public class SmoothLinePathBuilder implements LinePathBuilder {

    @Override
    public Shape buildPath(List<Point2D.Double> points, double tension) {
        Path2D.Double path = new Path2D.Double();
        if (points == null || points.isEmpty()) {
            return path;
        }
        Point2D.Double first = points.get(0);
        path.moveTo(first.x, first.y);
        if (points.size() == 1 || tension <= 0D) {
            return path;
        }
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D.Double previous = i > 0 ? points.get(i - 1) : points.get(i);
            Point2D.Double current = points.get(i);
            Point2D.Double next = points.get(i + 1);
            Point2D.Double nextNext = i + 2 < points.size() ? points.get(i + 2) : next;

            double firstControlX = current.x + (next.x - previous.x) * tension / 6D;
            double firstControlY = current.y + (next.y - previous.y) * tension / 6D;
            double secondControlX = next.x - (nextNext.x - current.x) * tension / 6D;
            double secondControlY = next.y - (nextNext.y - current.y) * tension / 6D;
            path.curveTo(firstControlX, firstControlY, secondControlX, secondControlY, next.x, next.y);
        }
        return path;
    }
}
