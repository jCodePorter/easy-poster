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

    public TextSpan setUnderline(boolean underline) {
        this.underline = underline;
        return this;
    }

    public TextSpan setStrikeThrough(boolean strikeThrough) {
        this.strikeThrough = strikeThrough;
        return this;
    }
}
