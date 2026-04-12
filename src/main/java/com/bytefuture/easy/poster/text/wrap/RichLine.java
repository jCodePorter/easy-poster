package com.bytefuture.easy.poster.text.wrap;

import lombok.Getter;

import java.util.List;

@Getter
public final class RichLine {
    /** 当前行拼接后的纯文本。 */
    private final String text;
    /** 当前行总宽度。 */
    private final int width;
    /** 当前行按样式分组后的绘制片段。 */
    private final List<RichTextFragment> fragments;
    /** 当前行逐字形列表。 */
    private final List<RichGlyph> glyphs;

    public RichLine(String text, int width, List<RichTextFragment> fragments, List<RichGlyph> glyphs) {
        this.text = text;
        this.width = width;
        this.fragments = fragments;
        this.glyphs = glyphs;
    }
}
