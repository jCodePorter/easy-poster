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
     * {@link Positions} 相对位置枚举实现，相当于以 {@link CoordinatePoint} 点为基准点，进行绝对位置的相对偏移
     * 比如，输入 point(10,10), 元素宽高为10,10
     * 如果 referencePosition = Positions.LEFT_CENTER, 则绘制起始点为(10,5)，相当于绘制起始坐标点在元素左侧居中位置
     */
    private final Positions referencePosition;

    private AbsolutePosition(CoordinatePoint point, Positions position) {
        this.point = point;
        this.referencePosition = position;
    }

    public static AbsolutePosition of(CoordinatePoint point) {
        return new AbsolutePosition(point, Positions.CENTER);
    }

    public static AbsolutePosition of(CoordinatePoint point, Positions position) {
        return new AbsolutePosition(point, position);
    }

    @Override
    public CoordinatePoint calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
        CoordinatePoint relative = referencePosition.calculate(0, 0, elementWidth, elementHeight, Margin.of(0));
        int newX = point.getX() + relative.getX();
        int newY = point.getY() + relative.getY();
        return CoordinatePoint.of(newX, newY);
    }
}
