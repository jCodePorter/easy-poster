package com.bytefuture.easy.poster.text.split;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * 文本拆分请求
 *
 * @author biaoy
 * @since 2026/04/10
 */
@Getter
@Setter
public class TextSplitRequest {

    private final String text;

    private final int maxWidth;

    private final FontMetrics fontMetrics;

    private final boolean trimLeadingWhitespace;

    private final boolean trimTrailingWhitespace;

    private final boolean preserveEmptyLine;

    private TextSplitRequest(String text, int maxWidth, FontMetrics fontMetrics,
                             boolean trimLeadingWhitespace, boolean trimTrailingWhitespace,
                             boolean preserveEmptyLine) {
        this.text = text;
        this.maxWidth = maxWidth;
        this.fontMetrics = fontMetrics;
        this.trimLeadingWhitespace = trimLeadingWhitespace;
        this.trimTrailingWhitespace = trimTrailingWhitespace;
        this.preserveEmptyLine = preserveEmptyLine;
    }

    public static TextSplitRequest of(String text, int maxWidth, FontMetrics fontMetrics) {
        return new TextSplitRequest(text, maxWidth, fontMetrics, true, true, true);
    }

    public TextSplitRequest withTrimLeadingWhitespace(boolean trimLeadingWhitespace) {
        return new TextSplitRequest(text, maxWidth, fontMetrics, trimLeadingWhitespace, trimTrailingWhitespace, preserveEmptyLine);
    }

    public TextSplitRequest withTrimTrailingWhitespace(boolean trimTrailingWhitespace) {
        return new TextSplitRequest(text, maxWidth, fontMetrics, trimLeadingWhitespace, trimTrailingWhitespace, preserveEmptyLine);
    }

    public TextSplitRequest withPreserveEmptyLine(boolean preserveEmptyLine) {
        return new TextSplitRequest(text, maxWidth, fontMetrics, trimLeadingWhitespace, trimTrailingWhitespace, preserveEmptyLine);
    }
}
