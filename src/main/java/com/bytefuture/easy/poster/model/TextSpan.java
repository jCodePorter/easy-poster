package com.bytefuture.easy.poster.model;

import com.bytefuture.easy.poster.element.basic.text.style.BaseTextStyle;
import com.bytefuture.easy.poster.exception.PosterException;
import lombok.Getter;

import java.awt.*;

@Getter
public class TextSpan {

    /**
     * 绘制的文本
     */
    private String text;

    /**
     * 当前文本样式
     */
    private BaseTextStyle spanStyle;

    public static TextSpan of(String text) {
        TextSpan textSpan = new TextSpan();
        textSpan.spanStyle = new BaseTextStyle();
        textSpan.text = text;
        return textSpan;
    }

    public static TextSpan of(String text, BaseTextStyle style) {
        TextSpan textSpan = new TextSpan();
        textSpan.spanStyle = style;
        textSpan.text = text;
        return textSpan;
    }

    public Color getColor() {
        return this.spanStyle.getColor();
    }

    public Integer getFontSize() {
        return this.spanStyle.getFontSize();
    }

    public TextSpan setColor(Color color) {
        if (color == null) {
            throw new PosterException("span color can not be null");
        }
        this.spanStyle.setColor(color);
        return this;
    }

    public TextSpan setFontStyle(int fontStyle) {
        this.spanStyle.setFontStyle(fontStyle);
        return this;
    }

    public TextSpan setFontSize(int fontSize) {
        if (fontSize <= 0) {
            throw new PosterException("span fontSize must be greater than 0");
        }
        this.spanStyle.setFontSize(fontSize);
        return this;
    }

    public TextSpan setFontName(String fontName) {
        if (fontName == null) {
            throw new PosterException("span fontName can not be null");
        }
        this.spanStyle.setFontName(fontName);
        return this;
    }

    /**
     * 设置是否绘制下划线。
     *
     * @param underline 是否绘制下划线
     * @return 当前文本片段
     */
    public TextSpan setUnderline(boolean underline) {
        this.spanStyle.setUnderline(underline);
        return this;
    }

    /**
     * 设置是否绘制删除线。
     *
     * @param strikeThrough 是否绘制删除线
     * @return 当前文本片段
     */
    public TextSpan setStrikeThrough(boolean strikeThrough) {
        this.spanStyle.setStrikeThrough(strikeThrough);
        return this;
    }

    /**
     * 设置字间距
     *
     * @param letterSpacing 字间距，单位为像素
     * @return 当前文本片段
     */
    public TextSpan setLetterSpacing(int letterSpacing) {
        this.spanStyle.setLetterSpacing(letterSpacing);
        return this;
    }

    /**
     * 设置文本片段背景色
     *
     * @param backgroundColor 背景色
     * @return 当前文本片段
     */
    public TextSpan setBackgroundColor(Color backgroundColor) {
        this.spanStyle.setBackgroundColor(backgroundColor);
        return this;
    }

    /**
     * 设置文本片段背景色
     *
     * @param backgroundColor 十六进制背景色
     * @return 当前文本片段
     */
    public TextSpan setBackgroundColor(String backgroundColor) {
        this.spanStyle.setBackgroundColor(backgroundColor);
        return this;
    }

    /**
     * 设置文本片段背景内边距
     *
     * @param backgroundPadding 背景内边距
     * @return 当前文本片段
     */
    public TextSpan setBackgroundPadding(int backgroundPadding) {
        if (backgroundPadding < 0) {
            throw new PosterException("backgroundPadding must be greater than or equal to 0");
        }
        this.spanStyle.setBackgroundPadding(backgroundPadding);
        return this;
    }

    /**
     * 设置文本片段背景圆角半径
     *
     * @param backgroundRadius 背景圆角半径
     * @return 当前文本片段
     */
    public TextSpan setBackgroundRadius(int backgroundRadius) {
        if (backgroundRadius < 0) {
            throw new PosterException("backgroundRadius must be greater than or equal to 0");
        }
        this.spanStyle.setBackgroundRadius(backgroundRadius);
        return this;
    }
}
