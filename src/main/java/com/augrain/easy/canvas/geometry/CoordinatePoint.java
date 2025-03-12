package com.augrain.easy.canvas.geometry;

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
public class CoordinatePoint {

    /**
     * 坐标原点
     */
    public static final CoordinatePoint ORIGIN_COORDINATE = CoordinatePoint.of(0, 0);

    private int x;

    private int y;

    private CoordinatePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static CoordinatePoint of(int x, int y) {
        return new CoordinatePoint(x, y);
    }
}
