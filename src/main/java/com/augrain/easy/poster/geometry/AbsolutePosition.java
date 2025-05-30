package com.augrain.easy.poster.geometry;

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
    private final Point point;

    /**
     * {@link Direction} 相对位置枚举实现，相当于以 {@link Point} 点为基准点，进行绝对位置的相对偏移
     * 比如，输入 point(10,10), 元素宽高为10,10
     * 如果 referencePosition = Positions.LEFT_CENTER, 则绘制起始点为(10,5)，相当于绘制起始坐标点在元素左侧居中位置
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
