package com.augrain.easy.poster.element;

import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.model.PosterContext;

/**
 * 元素
 *
 * @author biaoy
 * @since 2025/02/20
 */
public interface IElement {

    /**
     * 渲染
     *
     * @param context      上下文
     * @param posterWidth  画板宽度
     * @param posterHeight 画板高度
     * @return 渲染元素的起始坐标点
     */
    Point render(PosterContext context, int posterWidth, int posterHeight) throws Exception;
}
