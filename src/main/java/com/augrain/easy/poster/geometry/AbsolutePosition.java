package com.augrain.easy.poster.geometry;

import lombok.Getter;

/**
 * 绝对定位
 *
 * @author biaoy
 * @since 2025/03/04
 */
public class AbsolutePosition implements Position {

    /**
     * 坐标点
     */
    @Getter
    private final Point point;

    /**
     * 参考方向
     * direction = Direction.TOP_LEFT  时，point表示元素的左上角位置
     * direction = Direction.TOP_RIGHT 时，point表示元素的右上角位置
     * 以此类推
     */
    private final Direction direction;

    private AbsolutePosition(Point point, Direction direction) {
        this.point = point;
        this.direction = direction;
    }

    public static AbsolutePosition of(Point point) {
        return new AbsolutePosition(point, Direction.TOP_LEFT);
    }

    public static AbsolutePosition of(Point point, Direction direction) {
        return new AbsolutePosition(point, direction);
    }

    @Override
    public Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
        Point relative = direction.calculate(0, 0, elementWidth, elementHeight, Margin.of(0));
        int newX = point.getX() + relative.getX();
        int newY = point.getY() + relative.getY();
        return Point.of(newX, newY);
    }
}
