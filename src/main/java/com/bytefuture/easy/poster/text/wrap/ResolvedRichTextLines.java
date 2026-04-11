package com.bytefuture.easy.poster.text.wrap;

import lombok.Getter;

import java.util.List;

@Getter
public final class ResolvedRichTextLines {
    private final List<RichLine> lines;
    private final int layoutWidth;
    private final boolean truncated;
    private final boolean clipOverflow;

    public ResolvedRichTextLines(List<RichLine> lines, int layoutWidth, boolean truncated, boolean clipOverflow) {
        this.lines = lines;
        this.layoutWidth = layoutWidth;
        this.truncated = truncated;
        this.clipOverflow = clipOverflow;
    }
}
