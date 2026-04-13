package com.bytefuture.easy.poster.element.chart;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * 单调平滑曲线路径构建器
 *
 * @author biaoy
 * @since 2026/04/13
 */
public class MonotoneSmoothLinePathBuilder implements LinePathBuilder {

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

        int size = points.size();
        double[] slopes = new double[size - 1];
        double[] tangents = new double[size];
        for (int i = 0; i < size - 1; i++) {
            Point2D.Double current = points.get(i);
            Point2D.Double next = points.get(i + 1);
            double dx = next.x - current.x;
            slopes[i] = dx == 0D ? 0D : (next.y - current.y) / dx;
        }

        tangents[0] = slopes[0];
        tangents[size - 1] = slopes[size - 2];
        for (int i = 1; i < size - 1; i++) {
            double previousSlope = slopes[i - 1];
            double nextSlope = slopes[i];
            if (previousSlope == 0D || nextSlope == 0D || previousSlope * nextSlope < 0D) {
                tangents[i] = 0D;
            } else {
                tangents[i] = (previousSlope + nextSlope) / 2D;
            }
        }

        applyMonotoneConstraint(slopes, tangents);

        for (int i = 0; i < size - 1; i++) {
            Point2D.Double current = points.get(i);
            Point2D.Double next = points.get(i + 1);
            double dx = next.x - current.x;
            double scaledTension = tension / 3D;
            double firstControlX = current.x + dx * scaledTension;
            double firstControlY = current.y + tangents[i] * dx * scaledTension;
            double secondControlX = next.x - dx * scaledTension;
            double secondControlY = next.y - tangents[i + 1] * dx * scaledTension;
            path.curveTo(firstControlX, firstControlY, secondControlX, secondControlY, next.x, next.y);
        }
        return path;
    }

    private void applyMonotoneConstraint(double[] slopes, double[] tangents) {
        for (int i = 0; i < slopes.length; i++) {
            double slope = slopes[i];
            if (slope == 0D) {
                tangents[i] = 0D;
                tangents[i + 1] = 0D;
                continue;
            }
            double alpha = tangents[i] / slope;
            double beta = tangents[i + 1] / slope;
            double norm = alpha * alpha + beta * beta;
            if (norm > 9D) {
                double scale = 3D / Math.sqrt(norm);
                tangents[i] = scale * alpha * slope;
                tangents[i + 1] = scale * beta * slope;
            }
        }
    }
}
