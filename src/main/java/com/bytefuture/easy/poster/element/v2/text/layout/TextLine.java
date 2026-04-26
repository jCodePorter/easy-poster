package com.bytefuture.easy.poster.element.v2.text.layout;

import com.bytefuture.easy.poster.element.v2.text.resolve.ResolvedTextRun;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public final class TextLine {
    private final String text;
    private final int width;
    private final int offsetX;
    private final List<ResolvedTextRun> runs;

    public TextLine(String text, int width, int offsetX, List<ResolvedTextRun> runs) {
        this.text = text;
        this.width = width;
        this.offsetX = offsetX;
        this.runs = Collections.unmodifiableList(new ArrayList<ResolvedTextRun>(runs));
    }

    public TextLine withOffsetX(int offsetX) {
        return new TextLine(this.text, this.width, offsetX, this.runs);
    }

    public static TextLine empty() {
        return new TextLine("", 0, 0, Collections.<ResolvedTextRun>emptyList());
    }
}
