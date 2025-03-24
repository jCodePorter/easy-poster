package com.augrain.easy.canvas.model;

import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;

import java.util.function.Function;

/**
 * 矩形元素渐变方向
 *
 * @author biaoy
 * @since 2025/03/24
 */
public enum GradientDirection {
    /**
     * 上到下
     */
    TOP_BOTTOM(d -> {
        int fromX = d.getPoint().getX() + d.getWidth() / 2;
        int fromY = d.getPoint().getY();
        int toX = fromX;
        int toY = d.getPoint().getY() + d.getHeight();
        return new CoordinatePoint[]{CoordinatePoint.of(fromX, fromY), CoordinatePoint.of(toX, toY)};
    }),

    /**
     * 左到右
     */
    LEFT_RIGHT(d -> {
        int fromX = d.getPoint().getX();
        int fromY = d.getPoint().getY() + d.getHeight() / 2;
        int toX = d.getPoint().getX() + d.getWidth();
        int toY = fromY;
        return new CoordinatePoint[]{CoordinatePoint.of(fromX, fromY), CoordinatePoint.of(toX, toY)};
    }),

    /**
     * 左上到右下
     */
    TOP_LEFT_RIGHT_BOTTOM(d -> {
        int fromX = d.getPoint().getX();
        int fromY = d.getPoint().getY();
        int toX = d.getPoint().getX() + d.getWidth();
        int toY = d.getPoint().getY() + d.getHeight();
        return new CoordinatePoint[]{CoordinatePoint.of(fromX, fromY), CoordinatePoint.of(toX, toY)};
    }),

    /**
     * 右上到左下
     */
    TOP_RIGHT_LEFT_BOTTOM(d -> {
        int fromX = d.getPoint().getX() + d.getWidth();
        int fromY = d.getPoint().getY();
        int toX = d.getPoint().getX();
        int toY = d.getPoint().getY() + d.getHeight();
        return new CoordinatePoint[]{CoordinatePoint.of(fromX, fromY), CoordinatePoint.of(toX, toY)};
    });

    private final Function<Dimension, CoordinatePoint[]> directionCalculator;

    GradientDirection(Function<Dimension, CoordinatePoint[]> directionCalculator) {
        this.directionCalculator = directionCalculator;
    }

    public CoordinatePoint[] calcStartEnd(Dimension dimension) {
        return directionCalculator.apply(dimension);
    }
}
