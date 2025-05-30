package com.augrain.easy.poster.utils;

import com.augrain.easy.poster.geometry.CoordinatePoint;
import com.augrain.easy.poster.geometry.Dimension;

import java.util.List;

/**
 * 坐标点相关计算
 *
 * @author biaoy
 * @since 2025/03/21
 */
public class PointUtils {

    public static Dimension boundingBox(List<CoordinatePoint> points) {
        if (points == null || points.isEmpty()) {
            return null;
        }

        int xMin = Integer.MAX_VALUE;
        int yMin = Integer.MAX_VALUE;
        int xMax = Integer.MIN_VALUE;
        int yMax = Integer.MIN_VALUE;

        for (CoordinatePoint point : points) {
            if (point.getX() < xMin) {
                xMin = point.getX();
            }
            if (point.getY() < yMin) {
                yMin = point.getY();
            }
            if (point.getX() > xMax) {
                xMax = point.getX();
            }
            if (point.getY() > yMax) {
                yMax = point.getY();
            }
        }
        int width = xMax - xMin;
        int height = yMax - yMin;
        CoordinatePoint point = CoordinatePoint.of(xMin, yMin);
        return Dimension.builder()
                .width(width)
                .height(height)
                .point(point)
                .build();
    }
}
