package com.bytefuture.easy.poster.geometry;

import lombok.Getter;

/**
 * 容器内部绝对定位
 *
 * @author biaoy
 * @since 2026/05/01
 */
public class LocalAbsolutePosition implements Position {

    /**
     * 相对容器内容区原点的坐标点
     */
    @Getter
    private final Point point;

    /**
     * 坐标点参考方向
     */
    private final Direction direction;

    private LocalAbsolutePosition(Point point, Direction direction) {
        this.point = point;
        this.direction = direction;
    }

    /**
     * 创建左上角参考的容器内绝对定位
     *
     * @param point 相对容器内容区原点的坐标点
     * @return 容器内绝对定位
     */
    public static LocalAbsolutePosition of(Point point) {
        return new LocalAbsolutePosition(point, Direction.TOP_LEFT);
    }

    /**
     * 创建指定参考方向的容器内绝对定位
     *
     * @param point 相对容器内容区原点的坐标点
     * @param direction 坐标点参考方向
     * @return 容器内绝对定位
     */
    public static LocalAbsolutePosition of(Point point, Direction direction) {
        return new LocalAbsolutePosition(point, direction);
    }

    @Override
    public Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
        Point relative = direction.calculate(0, 0, elementWidth, elementHeight, Margin.of(0));
        int newX = point.getX() + relative.getX();
        int newY = point.getY() + relative.getY();
        return Point.of(newX, newY);
    }
}
