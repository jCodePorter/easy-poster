package com.augrain.easy.canvas.element;

import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;

import java.awt.*;

/**
 * 有明确宽高尺寸的元素
 *
 * @author biaoy
 * @since 2025/03/17
 */
public abstract class AbstractDimensionElement extends AbstractRepeatableElement implements IElement {

    /**
     * 宽度
     */
    protected int width;

    /**
     * 高度
     */
    protected int height;

    @Override
    public Dimension calculateDimension(Graphics2D g, int canvasWidth, int canvasHeight) {
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
