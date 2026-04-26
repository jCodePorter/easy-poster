package com.bytefuture.easy.poster.element.v2.text.style;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * 文本样式基类。
 * 保存块级样式和片段样式共享的可选属性。
 */
@Getter
@Setter
public class BaseTextStyle {
    /**
     * 文本颜色
     */
    private Color color;
    /**
     * 字体名称
     */
    private String fontName;
    /**
     * 字体样式
     */
    private Integer fontStyle;
    /**
     * 字体大小
     */
    private Integer fontSize;
}
