package com.bytefuture.easy.poster.model;

import com.bytefuture.easy.poster.element.v2.text.style.BaseTextStyle;
import com.bytefuture.easy.poster.exception.PosterException;
import lombok.Getter;

import java.awt.*;

@Getter
public class TextSpan {
    private final String text;

    private final BaseTextStyle spanStyle = new BaseTextStyle();

    private TextSpan(String text) {
        this.text = text == null ? "" : text;
    }

    public static TextSpan of(String text) {
        return new TextSpan(text);
    }

    public Color getColor() {
        return this.spanStyle.getColor();
    }

    public Integer getFontStyle() {
        return this.spanStyle.getFontStyle();
    }

    public Integer getFontSize() {
        return this.spanStyle.getFontSize();
    }

    public String getFontName() {
        return this.spanStyle.getFontName();
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
}
