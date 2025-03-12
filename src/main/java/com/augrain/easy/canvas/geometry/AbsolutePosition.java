package com.augrain.easy.canvas.geometry;

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
    private final CoordinatePoint point;

    /**
     * {@link Positions} 相对位置枚举实现，相当于以 {@link CoordinatePoint} 点为基准点，进行绝对位置的偏移
     */
    private final Position position;

    private AbsolutePosition(CoordinatePoint point, Position position) {
        this.point = point;
        this.position = position;
    }

    public static AbsolutePosition of(CoordinatePoint point) {
        return new AbsolutePosition(point, Positions.CENTER);
    }

    public static AbsolutePosition of(CoordinatePoint point, Position position) {
        return new AbsolutePosition(point, position);
    }

    @Override
    public CoordinatePoint calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
        CoordinatePoint calculate = position.calculate(0, 0, elementWidth, elementHeight, Margin.of(0));
        int newX = point.getX() + calculate.getX();
        int newY = point.getY() + calculate.getY();
        return CoordinatePoint.of(newX, newY);
    }
}
