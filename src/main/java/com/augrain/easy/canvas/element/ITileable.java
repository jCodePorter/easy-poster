package com.augrain.easy.canvas.element;

import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;

/**
 * 可平铺的元素
 *
 * @author biaoy
 * @since 2025/03/16
 */
public interface ITileable extends IElement {

    /**
     * 重新计算位置
     */
    CoordinatePoint reCalculatePosition(int canvasWidth, int canvasHeight, Dimension dimension);
}
