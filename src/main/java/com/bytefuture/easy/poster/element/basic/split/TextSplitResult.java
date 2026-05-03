package com.bytefuture.easy.poster.element.basic.split;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * 文本拆分结果
 */
@Getter
@Setter
public class TextSplitResult {

    /**
     * 分行结果列表
     */
    private final List<SplitTextInfo> lines;

    /**
     * 结果中最宽一行的宽度
     */
    private final int maxLineWidth;

    private TextSplitResult(List<SplitTextInfo> lines, int maxLineWidth) {
        this.lines = Collections.unmodifiableList(lines);
        this.maxLineWidth = maxLineWidth;
    }

    /**
     * 创建拆分结果，并同步计算最大行宽。
     *
     * @param lines 分行结果
     * @return 拆分结果
     */
    public static TextSplitResult of(List<SplitTextInfo> lines) {
        int maxWidth = 0;
        for (SplitTextInfo line : lines) {
            // 预先计算最大行宽，供上层布局直接复用。
            maxWidth = Math.max(maxWidth, line.getWidth());
        }
        return new TextSplitResult(lines, maxWidth);
    }
}
