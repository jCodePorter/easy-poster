package com.augrain.easy.poster.geometry;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 相对位置
 *
 * @author biaoy
 * @since 2025/03/04
 */
@Getter
@Setter
@ToString
public class RelativePosition implements Position {

    /**
     * {@link Direction} 相对位置的参考方向
     */
    private Direction direction;

    /**
     * 边距
     */
    private Margin margin;

    private RelativePosition(Direction direction, Margin margin) {
        this.direction = direction;
        this.margin = margin;
    }

    public static RelativePosition of(Direction direction) {
        return new RelativePosition(direction, Margin.DEFAULT);
    }

    public static RelativePosition of(Direction direction, Margin margin) {
        return new RelativePosition(direction, margin);
    }

    @Override
    public Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
        return this.direction.calculate(enclosingWidth, enclosingHeight, elementWidth, elementHeight, this.margin);
    }
}
