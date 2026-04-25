package com.bytefuture.easy.poster.element.v2.text.wrap;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public final class RichToken {
    private final String text;
    private final int width;
    private final List<RichGlyph> glyphs;
    private final RichTokenType type;

    public RichToken(String text, int width, List<RichGlyph> glyphs, RichTokenType type) {
        this.text = text;
        this.width = width;
        this.glyphs = glyphs;
        this.type = type;
    }

    public static RichToken newLine() {
        return new RichToken("\n", 0, Collections.<RichGlyph>emptyList(), RichTokenType.NEW_LINE);
    }
}
