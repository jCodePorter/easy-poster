package com.bytefuture.easy.poster.model;

import com.bytefuture.easy.poster.element.v2.text.style.BaseTextStyle;
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
}
