package com.augrain.easy.poster.element;

import com.augrain.easy.poster.geometry.CoordinatePoint;
import com.augrain.easy.poster.geometry.Dimension;
import com.augrain.easy.poster.model.CanvasContext;

/**
 * 有明确宽高尺寸的元素
 *
 * @author biaoy
 * @since 2025/03/17
 */
public abstract class AbstractDimensionElement<T extends AbstractDimensionElement>
        extends AbstractRepeatableElement<T> implements IElement {

    /**
     * 宽度
     */
    protected int width;

    /**
     * 高度
     */
    protected int height;

    @Override
    public Dimension calculateDimension(CanvasContext context, int canvasWidth, int canvasHeight) {
        CoordinatePoint point = CoordinatePoint.ORIGIN_COORDINATE;
        if (position != null) {
            point = position.calculate(canvasWidth, canvasHeight, width, height);
        }
        return Dimension.builder()
                .width(width)
                .height(height)
                .point(point)
                .build();
    }
}
