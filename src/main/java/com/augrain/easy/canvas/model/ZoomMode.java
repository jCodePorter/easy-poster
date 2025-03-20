package com.augrain.easy.canvas.model;

/**
 * 图片缩放方式
 *
 * @author biaoy
 * @since 2025/02/20
 */
public enum ZoomMode {

    WIDTH("按宽度等比例缩放"),

    HEIGHT("按高度等比例缩放"),

    WIDTH_HEIGHT("指定宽高"),

    RATIO("按比例");

    private String text;

    ZoomMode(String text) {
        this.text = text;
    }
}
