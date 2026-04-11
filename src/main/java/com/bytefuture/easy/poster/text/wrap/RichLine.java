package com.bytefuture.easy.poster.text.wrap;

import lombok.Getter;

import java.util.List;

@Getter
public final class RichLine {
    private final String text;
    private final int width;
    private final List<RichTextFragment> fragments;
    private final List<RichGlyph> glyphs;

    public RichLine(String text, int width, List<RichTextFragment> fragments, List<RichGlyph> glyphs) {
        this.text = text;
        this.width = width;
        this.fragments = fragments;
        this.glyphs = glyphs;
    }
}
