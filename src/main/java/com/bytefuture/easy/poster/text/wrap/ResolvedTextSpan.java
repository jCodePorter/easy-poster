package com.bytefuture.easy.poster.text.wrap;

import lombok.Getter;

import java.awt.Color;
import java.awt.Font;

@Getter
public final class ResolvedTextSpan {
    private final String text;
    private final Font font;
    private final Color color;
    private final boolean underline;
    private final boolean strikeThrough;

    public ResolvedTextSpan(String text, Font font, Color color, boolean underline, boolean strikeThrough) {
        this.text = text;
        this.font = font;
        this.color = color;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
    }
}
