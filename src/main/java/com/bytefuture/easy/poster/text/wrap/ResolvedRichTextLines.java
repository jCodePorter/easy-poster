package com.bytefuture.easy.poster.text.wrap;

import lombok.Getter;

import java.util.List;

@Getter
public final class ResolvedRichTextLines {
    /** 富文本分行结果。 */
    private final List<RichLine> lines;
    /** 本次布局宽度。 */
    private final int layoutWidth;
    /** 是否发生截断。 */
    private final boolean truncated;
    /** 是否需要绘制阶段裁剪。 */
    private final boolean clipOverflow;

    public ResolvedRichTextLines(List<RichLine> lines, int layoutWidth, boolean truncated, boolean clipOverflow) {
        this.lines = lines;
        this.layoutWidth = layoutWidth;
        this.truncated = truncated;
        this.clipOverflow = clipOverflow;
    }
}
