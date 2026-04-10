package com.bytefuture.easy.poster.text;

import java.awt.*;
import java.util.List;

/**
 * 文本拆分器
 *
 * @author biaoy
 * @since 2025/03/16
 */
public interface ITextSplitter {

    TextSplitResult split(TextSplitRequest request);

    default List<SplitTextInfo> splitText(String text, int width, FontMetrics fontMetrics) {
        return split(TextSplitRequest.of(text, width, fontMetrics)).getLines();
    }
}
