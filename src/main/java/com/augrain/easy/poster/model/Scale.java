package com.augrain.easy.poster.model;

import lombok.Getter;

/**
 * 缩放参数
 *
 * @author biaoy
 * @since 2025/03/20
 */
@Getter
public class Scale {

    /**
     * 宽度
     */
    private int width;

    /**
     * 高度
     */
    private int height;

    /**
     * 比例
     */
    private double ratio;

    /**
     * 缩放模式
     */
    private ZoomMode zoomMode;

    public static Scale byWidth(int width) {
        Scale scale = new Scale();
        scale.width = width;
        scale.zoomMode = ZoomMode.WIDTH;
        return scale;
    }

    public static Scale byHeight(int height) {
        Scale scale = new Scale();
        scale.height = height;
        scale.zoomMode = ZoomMode.HEIGHT;
        return scale;
    }

    public static Scale byWidthAndHeight(int width, int height) {
        Scale scale = new Scale();
        scale.width = width;
        scale.height = height;
        scale.zoomMode = ZoomMode.WIDTH_HEIGHT;
        return scale;
    }

    public static Scale byRatio(double ratio) {
        Scale scale = new Scale();
        scale.ratio = ratio;
        scale.zoomMode = ZoomMode.RATIO;
        return scale;
    }
}
