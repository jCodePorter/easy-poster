package com.augrain.easy.poster.element;

import com.augrain.easy.poster.element.advance.RepeatElement;
import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.geometry.Dimension;

/**
 * 可重复平铺的元素
 *
 * @author biaoy
 * @since 2025/03/16
 */
public interface IRepeatableElement extends IElement {

    /**
     * 重新计算位置，由类 {@link RepeatElement} 进行调用
     */
    Point reCalculatePosition(int posterWidth, int posterHeight, Dimension dimension);
}
