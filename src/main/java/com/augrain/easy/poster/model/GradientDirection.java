package com.augrain.easy.poster.model;

import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.geometry.Dimension;

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
        return new Point[]{Point.of(fromX, fromY), Point.of(toX, toY)};
    }),

    /**
     * 左到右
     */
    LEFT_RIGHT(d -> {
        int fromX = d.getPoint().getX();
        int fromY = d.getPoint().getY() + d.getHeight() / 2;
        int toX = d.getPoint().getX() + d.getWidth();
        int toY = fromY;
        return new Point[]{Point.of(fromX, fromY), Point.of(toX, toY)};
    }),

    /**
     * 左上到右下
     */
    TOP_LEFT_RIGHT_BOTTOM(d -> {
        int fromX = d.getPoint().getX();
        int fromY = d.getPoint().getY();
        int toX = d.getPoint().getX() + d.getWidth();
        int toY = d.getPoint().getY() + d.getHeight();
        return new Point[]{Point.of(fromX, fromY), Point.of(toX, toY)};
    }),

    /**
     * 右上到左下
     */
    TOP_RIGHT_LEFT_BOTTOM(d -> {
        int fromX = d.getPoint().getX() + d.getWidth();
        int fromY = d.getPoint().getY();
        int toX = d.getPoint().getX();
        int toY = d.getPoint().getY() + d.getHeight();
        return new Point[]{Point.of(fromX, fromY), Point.of(toX, toY)};
    });

    private final Function<Dimension, Point[]> directionCalculator;

    GradientDirection(Function<Dimension, Point[]> directionCalculator) {
        this.directionCalculator = directionCalculator;
    }

    public Point[] calcStartEnd(Dimension dimension) {
        return directionCalculator.apply(dimension);
    }
}
