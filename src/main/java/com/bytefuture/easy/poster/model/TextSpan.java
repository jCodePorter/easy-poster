package com.bytefuture.easy.poster.model;

import com.bytefuture.easy.poster.exception.PosterException;
import lombok.Getter;

import java.awt.*;

/**
 * Inline rich text span for TextElementUpgrade.
 */
@Getter
public class TextSpan {

    private final String text;

    private Color color;

    private Integer fontStyle;

    private Integer fontSize;

    private String fontName;

    private Color backgroundColor;

    private TextShadow shadow;

    private TextStroke stroke;

    private Integer baselineShift;

    private Boolean underline;

    private Boolean strikeThrough;

    private TextSpan(String text) {
        this.text = text == null ? "" : text;
    }

    public static TextSpan of(String text) {
        return new TextSpan(text);
    }

    public TextSpan setColor(Color color) {
        if (color == null) {
            throw new PosterException("span color can not be null");
        }
        this.color = color;
        return this;
    }

    public TextSpan setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
        return this;
    }

    public TextSpan setFontSize(int fontSize) {
        if (fontSize <= 0) {
            throw new PosterException("span fontSize must be greater than 0");
        }
        this.fontSize = fontSize;
        return this;
    }

    public TextSpan setFontName(String fontName) {
        if (fontName == null) {
            throw new PosterException("span fontName can not be null");
        }
        this.fontName = fontName;
        return this;
    }

    public TextSpan setBackgroundColor(Color backgroundColor) {
        if (backgroundColor == null) {
            throw new PosterException("span backgroundColor can not be null");
        }
        this.backgroundColor = backgroundColor;
        return this;
    }

    public TextSpan setShadow(TextShadow shadow) {
        if (shadow == null) {
            throw new PosterException("span shadow can not be null");
        }
        this.shadow = shadow;
        return this;
    }

    public TextSpan setStroke(TextStroke stroke) {
        if (stroke == null) {
            throw new PosterException("span stroke can not be null");
        }
        this.stroke = stroke;
        return this;
    }

    public TextSpan setBaselineShift(int baselineShift) {
        this.baselineShift = baselineShift;
        return this;
    }

    public TextSpan setUnderline(boolean underline) {
        this.underline = underline;
        return this;
    }

    public TextSpan setStrikeThrough(boolean strikeThrough) {
        this.strikeThrough = strikeThrough;
        return this;
    }
}
