package com.bytefuture.easy.poster.element;

import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;

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

        int realWidth = width == 0 ? posterWidth : width;
        int realHeight = height == 0 ? posterHeight : height;
        if (position != null) {
            point = position.calculate(posterWidth, posterHeight, realWidth, realHeight);
        }
        return Dimension.builder()
                .width(realWidth)
                .height(realHeight)
                .point(point)
                .build();
    }
}
