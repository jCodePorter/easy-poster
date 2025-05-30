package com.augrain.easy.poster.element;

import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.geometry.Dimension;
import com.augrain.easy.poster.model.PosterContext;

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
    public Dimension calculateDimension(PosterContext context, int posterWidth, int posterHeight) {
        Point point = Point.ORIGIN_COORDINATE;
        if (position != null) {
            point = position.calculate(posterWidth, posterHeight, width, height);
        }
        return Dimension.builder()
                .width(width)
                .height(height)
                .point(point)
                .build();
    }
}
