package com.augrain.easy.poster.model;

import java.awt.image.BufferedImage;

/**
 * canvas 监听器
 *
 * @author biaoy
 * @since 2025/03/20
 */
public interface CanvasListener {

    /**
     * 在文件输出之前，用于自定义扩展，对图片做其他处理
     *
     * @param image 待输出的 image
     */
    BufferedImage beforeOut(BufferedImage image);
}
