package com.bytefuture.easy.poster.text.wrap;

import lombok.Getter;

import java.awt.Color;
import java.awt.Font;
import com.bytefuture.easy.poster.model.TextShadow;
import com.bytefuture.easy.poster.model.TextStroke;

@Getter
public final class ResolvedTextSpan {
    private final String text;
    private final Font font;
    private final Color color;
    private final Color backgroundColor;
    private final TextShadow shadow;
    private final TextStroke stroke;
    private final int baselineShift;
    private final boolean underline;
    private final boolean strikeThrough;

    public ResolvedTextSpan(String text, Font font, Color color, Color backgroundColor,
                            TextShadow shadow, TextStroke stroke, int baselineShift,
                            boolean underline, boolean strikeThrough) {
        this.text = text;
        this.font = font;
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.shadow = shadow;
        this.stroke = stroke;
        this.baselineShift = baselineShift;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
    }
}
