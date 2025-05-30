package com.augrain.easy.poster.element;

import com.augrain.easy.poster.geometry.CoordinatePoint;
import com.augrain.easy.poster.geometry.Dimension;

/**
 * 可平铺的元素
 *
 * @author biaoy
 * @since 2025/03/16
 */
public abstract class AbstractRepeatableElement<T extends AbstractRepeatableElement>
        extends AbstractElement<T> implements IRepeatableElement {

    @Override
    public CoordinatePoint reCalculatePosition(int canvasWidth, int canvasHeight, Dimension dimension) {
        return getPosition().calculate(canvasWidth, canvasHeight, dimension.getWidth(), dimension.getHeight());
    }
}
