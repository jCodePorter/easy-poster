package com.bytefuture.easy.poster.text.wrap;

import com.bytefuture.easy.poster.model.TextShadow;
import com.bytefuture.easy.poster.model.TextStroke;
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
    private final Color backgroundColor;
    private final TextShadow shadow;
    private final TextStroke stroke;
    private final int baselineShift;
    private final boolean underline;
    private final boolean strikeThrough;

    public RichTextFragment(String text, int xOffset, int width, Font font, Color color,
                            Color backgroundColor, TextShadow shadow, TextStroke stroke, int baselineShift,
                            boolean underline, boolean strikeThrough) {
        this.text = text;
        this.xOffset = xOffset;
        this.width = width;
        this.font = font;
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.shadow = shadow;
        this.stroke = stroke;
        this.baselineShift = baselineShift;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
    }

    public RichTextFragment shiftX(int offsetX) {
        if (offsetX == 0) {
            return this;
        }
        return new RichTextFragment(this.text, this.xOffset + offsetX, this.width, this.font,
                this.color, this.backgroundColor, this.shadow, this.stroke, this.baselineShift,
                this.underline, this.strikeThrough);
    }
}
