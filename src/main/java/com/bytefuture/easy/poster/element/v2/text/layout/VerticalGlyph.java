package com.bytefuture.easy.poster.element.v2.text.layout;

import lombok.Getter;

import java.awt.Color;
import java.awt.Font;

@Getter
public final class VerticalGlyph {
    private final String text;
    private final int xOffset;
    private final int yOffset;
    private final int width;
    private final Font font;
    private final Color color;
    private final boolean underline;
    private final boolean strikeThrough;

    public VerticalGlyph(String text, int xOffset, int yOffset, int width) {
        this(text, xOffset, yOffset, width, null, null, false, false);
    }

    public VerticalGlyph(String text, int xOffset, int yOffset, int width,
                         Font font, Color color, boolean underline, boolean strikeThrough) {
        this.text = text;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.width = width;
        this.font = font;
        this.color = color;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
    }
}
