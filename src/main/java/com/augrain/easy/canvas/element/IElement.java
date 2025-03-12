package com.augrain.easy.canvas.element;

import com.augrain.easy.canvas.geometry.CoordinatePoint;

import java.awt.*;

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
     * @param g            画笔
     * @param canvasWidth  画板宽度
     * @param canvasHeight 画板高度
     * @return 渲染元素的起始坐标点
     */
    CoordinatePoint render(Graphics2D g, int canvasWidth, int canvasHeight) throws Exception;
}
