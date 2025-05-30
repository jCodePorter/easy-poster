package com.augrain.easy.poster.geometry;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 坐标点
 *
 * @author biaoy
 * @since 2025/03/04
 */
@Getter
@Setter
@ToString
public class Point {

    /**
     * 坐标原点
     */
    public static final Point ORIGIN_COORDINATE = Point.of(0, 0);

    private int x;

    private int y;

    private Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Point of(int x, int y) {
        return new Point(x, y);
    }
}
