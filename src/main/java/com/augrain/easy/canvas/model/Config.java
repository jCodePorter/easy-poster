package com.augrain.easy.canvas.model;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * 全局配置
 *
 * @author biaoy
 * @since 2025/04/07
 */
@Getter
@Setter
public class Config {

    /**
     * 调试模式，目前仅用于绘制各元素的外边框
     */
    private boolean debug = false;

    /**
     * 文本对齐方式，默认居中对齐
     */
    private BaseLine baseLine = BaseLine.CENTER;

    /**
     * 字体颜色，默认为黑色
     */
    private Color fontColor = Color.BLACK;

    /**
     * 字体名称，默认为微软雅黑
     */
    private String fontName = "微软雅黑";

    /**
     * 字体样式，加粗，斜体，比如：Font.BOLD, Font.ITALIC，或者 Font.BOLD | Font.ITALIC
     */
    private int fontStyle = Font.PLAIN;

    /**
     * 字体大小，默认12pt
     */
    private int fontSize = 12;

    /**
     * 自定义字体
     */
    private Font font;

    /**
     * 行高
     */
    private Integer lineHeight;
}
