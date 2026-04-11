package com.bytefuture.easy.poster.text.layout;

import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.text.wrap.RichTextFragment;
import lombok.Getter;

import java.util.List;

@Getter
public final class LayoutLine {
    private final String text;
    private final int width;
    private final Point point;
    private final boolean justified;
    private final int renderWidth;
    private final List<RichTextFragment> richFragments;

    public LayoutLine(String text, int width, Point point, boolean justified, int renderWidth,
                      List<RichTextFragment> richFragments) {
        this.text = text;
        this.width = width;
        this.point = point;
        this.justified = justified;
        this.renderWidth = renderWidth;
        this.richFragments = richFragments;
    }

    public boolean hasRichFragments() {
        return this.richFragments != null && !this.richFragments.isEmpty();
    }
}
