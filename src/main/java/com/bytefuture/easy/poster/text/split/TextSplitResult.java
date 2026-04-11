package com.bytefuture.easy.poster.text.split;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * 文本拆分结果
 *
 * @author biaoy
 * @since 2026/04/10
 */
@Getter
@Setter
public class TextSplitResult {

    private final List<SplitTextInfo> lines;

    private final int maxLineWidth;

    private TextSplitResult(List<SplitTextInfo> lines, int maxLineWidth) {
        this.lines = Collections.unmodifiableList(lines);
        this.maxLineWidth = maxLineWidth;
    }

    public static TextSplitResult of(List<SplitTextInfo> lines) {
        int maxWidth = 0;
        for (SplitTextInfo line : lines) {
            maxWidth = Math.max(maxWidth, line.getWidth());
        }
        return new TextSplitResult(lines, maxWidth);
    }
}
