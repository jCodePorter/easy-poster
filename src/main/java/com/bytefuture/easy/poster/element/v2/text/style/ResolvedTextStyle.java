package com.bytefuture.easy.poster.element.v2.text.style;

import lombok.Getter;

import java.awt.Color;
import java.awt.Font;
import java.util.Objects;

@Getter
public final class ResolvedTextStyle {
    private final Font font;
    private final Color color;

    public ResolvedTextStyle(Font font, Color color) {
        this.font = Objects.requireNonNull(font, "font");
        this.color = Objects.requireNonNull(color, "color");
    }
}
