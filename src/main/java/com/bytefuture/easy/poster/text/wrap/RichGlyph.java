package com.bytefuture.easy.poster.text.wrap;

import lombok.Getter;

import java.awt.Color;
import java.awt.Font;

@Getter
public final class RichGlyph {
    private final String text;
    private final int width;
    private final Font font;
    private final Color color;
    private final boolean underline;
    private final boolean strikeThrough;

    public RichGlyph(String text, int width, Font font, Color color, boolean underline, boolean strikeThrough) {
        this.text = text;
        this.width = width;
        this.font = font;
        this.color = color;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
    }

    public boolean hasSameStyle(RichGlyph other) {
        return this.font.equals(other.font)
                && this.color.equals(other.color)
                && this.underline == other.underline
                && this.strikeThrough == other.strikeThrough;
    }
}
