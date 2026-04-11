package com.bytefuture.easy.poster.text.wrap;

import lombok.Getter;

import java.awt.Color;
import java.awt.Font;

@Getter
public final class RichTextFragment {
    private final String text;
    private final int xOffset;
    private final int width;
    private final Font font;
    private final Color color;
    private final boolean underline;
    private final boolean strikeThrough;

    public RichTextFragment(String text, int xOffset, int width, Font font, Color color,
                            boolean underline, boolean strikeThrough) {
        this.text = text;
        this.xOffset = xOffset;
        this.width = width;
        this.font = font;
        this.color = color;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
    }

    public RichTextFragment shiftX(int offsetX) {
        if (offsetX == 0) {
            return this;
        }
        return new RichTextFragment(this.text, this.xOffset + offsetX, this.width, this.font,
                this.color, this.underline, this.strikeThrough);
    }
}
