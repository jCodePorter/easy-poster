package com.bytefuture.easy.poster.element.v2.text.wrap;

import com.bytefuture.easy.poster.model.TextShadow;
import com.bytefuture.easy.poster.model.TextStroke;
import lombok.Getter;

import java.awt.Color;
import java.awt.Font;

@Getter
public final class RichGlyph {
    private final String text;
    private final int width;
    private final Font font;
    private final Color color;
    private final Color backgroundColor;
    private final TextShadow shadow;
    private final TextStroke stroke;
    private final int baselineShift;
    private final boolean underline;
    private final boolean strikeThrough;

    public RichGlyph(String text, int width, Font font, Color color, Color backgroundColor,
                     TextShadow shadow, TextStroke stroke, int baselineShift,
                     boolean underline, boolean strikeThrough) {
        this.text = text;
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

    public boolean hasSameStyle(RichGlyph other) {
        return this.font.equals(other.font)
                && this.color.equals(other.color)
                && sameColor(this.backgroundColor, other.backgroundColor)
                && sameShadow(this.shadow, other.shadow)
                && sameStroke(this.stroke, other.stroke)
                && this.baselineShift == other.baselineShift
                && this.underline == other.underline
                && this.strikeThrough == other.strikeThrough;
    }

    private boolean sameColor(Color left, Color right) {
        return left == null ? right == null : left.equals(right);
    }

    private boolean sameShadow(TextShadow left, TextShadow right) {
        if (left == null || right == null) {
            return left == right;
        }
        return left.getColor().equals(right.getColor())
                && left.getOffsetX() == right.getOffsetX()
                && left.getOffsetY() == right.getOffsetY();
    }

    private boolean sameStroke(TextStroke left, TextStroke right) {
        if (left == null || right == null) {
            return left == right;
        }
        return left.getColor().equals(right.getColor())
                && left.getWidth() == right.getWidth();
    }
}
