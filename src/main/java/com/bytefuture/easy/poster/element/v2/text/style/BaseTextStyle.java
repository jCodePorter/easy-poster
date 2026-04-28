package com.bytefuture.easy.poster.element.v2.text.style;

import cn.augrain.easy.tool.support.ColorUtils;
import lombok.Getter;

import java.awt.*;

/**
 * 文本样式基类。
 * 保存块级样式和片段样式共享的可选属性。
 */
@Getter
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

    /**
     * 是否绘制下划线。
     */
    private Boolean underline;

    /**
     * 是否绘制删除线。
     */
    private Boolean strikeThrough;

    /**
     * 字间距，单位为像素
     */
    private Integer letterSpacing;

    /**
     * 文本背景色
     */
    private Color backgroundColor;

    /**
     * 文本背景内边距
     */
    private Integer backgroundPadding;

    /**
     * 文本背景圆角半径
     */
    private Integer backgroundRadius;

    /**
     * 设置文本颜色
     *
     * @param color 颜色值
     * @return 当前样式
     */
    public BaseTextStyle setColor(Color color) {
        this.color = color;
        return this;
    }

    /**
     * 设置字体名称。
     *
     * @param fontName 字体名称
     * @return 当前样式
     */
    public BaseTextStyle setFontName(String fontName) {
        this.fontName = fontName;
        return this;
    }

    /**
     * 设置字体样式
     *
     * @param fontStyle 字体样式
     * @return 当前样式
     */
    public BaseTextStyle setFontStyle(Integer fontStyle) {
        this.fontStyle = fontStyle;
        return this;
    }

    /**
     * 设置字体大小
     *
     * @param fontSize 字体大小
     * @return 当前样式
     */
    public BaseTextStyle setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    /**
     * 设置是否绘制下划线。
     *
     * @param underline 是否绘制下划线
     * @return 当前样式
     */
    public BaseTextStyle setUnderline(Boolean underline) {
        this.underline = underline;
        return this;
    }

    /**
     * 设置是否绘制删除线。
     *
     * @param strikeThrough 是否绘制删除线
     * @return 当前样式
     */
    public BaseTextStyle setStrikeThrough(Boolean strikeThrough) {
        this.strikeThrough = strikeThrough;
        return this;
    }

    /**
     * 设置字间距
     *
     * @param letterSpacing 字间距，单位为像素
     * @return 当前样式
     */
    public BaseTextStyle setLetterSpacing(Integer letterSpacing) {
        this.letterSpacing = letterSpacing;
        return this;
    }

    /**
     * 设置文本背景色
     *
     * @param backgroundColor 背景色
     * @return 当前样式
     */
    public BaseTextStyle setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * 设置文本背景色
     *
     * @param backgroundColor 十六进制背景色
     * @return 当前样式
     */
    public BaseTextStyle setBackgroundColor(String backgroundColor) {
        this.backgroundColor = ColorUtils.hexToColor(backgroundColor);
        return this;
    }

    /**
     * 设置文本背景内边距
     *
     * @param backgroundPadding 文本背景内边距
     * @return 当前样式
     */
    public BaseTextStyle setBackgroundPadding(Integer backgroundPadding) {
        this.backgroundPadding = backgroundPadding;
        return this;
    }

    /**
     * 设置文本背景圆角半径
     *
     * @param backgroundRadius 文本背景圆角半径
     * @return 当前样式
     */
    public BaseTextStyle setBackgroundRadius(Integer backgroundRadius) {
        this.backgroundRadius = backgroundRadius;
        return this;
    }
}
