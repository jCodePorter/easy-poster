package com.bytefuture.easy.poster.text.layout;

import lombok.Getter;

@Getter
public final class VerticalGlyph {
    private final String text;
    private final int xOffset;
    private final int yOffset;
    private final int width;

    public VerticalGlyph(String text, int xOffset, int yOffset, int width) {
        this.text = text;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.width = width;
    }
}
