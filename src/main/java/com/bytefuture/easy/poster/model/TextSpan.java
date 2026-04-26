package com.bytefuture.easy.poster.model;

import com.bytefuture.easy.poster.element.v2.text.style.TextSpanStyle;
import com.bytefuture.easy.poster.exception.PosterException;
import lombok.Getter;

import java.awt.Color;

@Getter
public class TextSpan {
    private final String text;
    private final TextSpanStyle style = new TextSpanStyle();

    private TextSpan(String text) {
        this.text = text == null ? "" : text;
    }

    public static TextSpan of(String text) {
        return new TextSpan(text);
    }

    public Color getColor() {
        return this.style.getColor();
    }

    public Integer getFontStyle() {
        return this.style.getFontStyle();
    }

    public Integer getFontSize() {
        return this.style.getFontSize();
    }

    public String getFontName() {
        return this.style.getFontName();
    }

    public TextSpanStyle getStyle() {
        return this.style;
    }

    public TextSpan setColor(Color color) {
        if (color == null) {
            throw new PosterException("span color can not be null");
        }
        this.style.setColor(color);
        return this;
    }

    public TextSpan setFontStyle(int fontStyle) {
        this.style.setFontStyle(Integer.valueOf(fontStyle));
        return this;
    }

    public TextSpan setFontSize(int fontSize) {
        if (fontSize <= 0) {
            throw new PosterException("span fontSize must be greater than 0");
        }
        this.style.setFontSize(Integer.valueOf(fontSize));
        return this;
    }

    public TextSpan setFontName(String fontName) {
        if (fontName == null) {
            throw new PosterException("span fontName can not be null");
        }
        this.style.setFontName(fontName);
        return this;
    }
}
