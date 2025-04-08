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
     * {@link PositionDirection} 相对位置枚举实现，相当于以 {@link CoordinatePoint} 点为基准点，进行绝对位置的相对偏移
     * 比如，输入 point(10,10), 元素宽高为10,10
     * 如果 referencePosition = Positions.LEFT_CENTER, 则绘制起始点为(10,5)，相当于绘制起始坐标点在元素左侧居中位置
     */
    private final PositionDirection direction;

    private AbsolutePosition(CoordinatePoint point, PositionDirection direction) {
        this.point = point;
        this.direction = direction;
    }

    public static AbsolutePosition of(CoordinatePoint point) {
        return new AbsolutePosition(point, PositionDirection.CENTER);
    }

    public static AbsolutePosition of(CoordinatePoint point, PositionDirection position) {
        return new AbsolutePosition(point, position);
    }

    @Override
    public CoordinatePoint calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
        CoordinatePoint relative = direction.calculate(0, 0, elementWidth, elementHeight, Margin.of(0));
        int newX = point.getX() + relative.getX();
        int newY = point.getY() + relative.getY();
        return CoordinatePoint.of(newX, newY);
    }
}
