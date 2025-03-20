package com.augrain.easy.canvas.element;

import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;

/**
 * 可平铺的元素
 *
 * @author biaoy
 * @since 2025/03/16
 */
public abstract class AbstractRepeatableElement<T extends AbstractRepeatableElement>
        extends AbstractElement<AbstractRepeatableElement> implements IRepeatableElement {

    @Override
    public CoordinatePoint reCalculatePosition(int canvasWidth, int canvasHeight, Dimension dimension) {
        return getPosition().calculate(canvasWidth, canvasHeight, dimension.getWidth(), dimension.getHeight());
    }
}
