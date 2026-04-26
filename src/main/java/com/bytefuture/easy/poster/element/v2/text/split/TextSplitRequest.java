package com.bytefuture.easy.poster.element.v2.text.split;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * 文本拆分请求。
 * 封装拆分器所需的文本、宽度限制和空白处理策略。
 */
@Getter
@Setter
public class TextSplitRequest {

    /**
     * 原始文本。
     */
    private final String text;

    /** 允许的最大宽度。 */
    private final int maxWidth;

    /** 当前字体度量信息。 */
    private final FontMetrics fontMetrics;

    /** 是否裁掉新行行首空白。 */
    private final boolean trimLeadingWhitespace;

    /** 是否裁掉行尾空白。 */
    private final boolean trimTrailingWhitespace;

    /** 是否保留显式空行。 */
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

    /**
     * 创建默认拆分请求。
     *
     * @param text 原始文本
     * @param maxWidth 最大宽度
     * @param fontMetrics 字体度量
     * @return 拆分请求
     */
    public static TextSplitRequest of(String text, int maxWidth, FontMetrics fontMetrics) {
        return new TextSplitRequest(text, maxWidth, fontMetrics, true, true, true);
    }

    /**
     * 返回一个修改了“是否裁掉行首空白”的新请求对象。
     *
     * @param trimLeadingWhitespace 是否裁掉行首空白
     * @return 新请求对象
     */
    public TextSplitRequest withTrimLeadingWhitespace(boolean trimLeadingWhitespace) {
        return new TextSplitRequest(text, maxWidth, fontMetrics, trimLeadingWhitespace, trimTrailingWhitespace, preserveEmptyLine);
    }

    /**
     * 返回一个修改了“是否裁掉行尾空白”的新请求对象。
     *
     * @param trimTrailingWhitespace 是否裁掉行尾空白
     * @return 新请求对象
     */
    public TextSplitRequest withTrimTrailingWhitespace(boolean trimTrailingWhitespace) {
        return new TextSplitRequest(text, maxWidth, fontMetrics, trimLeadingWhitespace, trimTrailingWhitespace, preserveEmptyLine);
    }

    /**
     * 返回一个修改了“是否保留空行”的新请求对象。
     *
     * @param preserveEmptyLine 是否保留空行
     * @return 新请求对象
     */
    public TextSplitRequest withPreserveEmptyLine(boolean preserveEmptyLine) {
        return new TextSplitRequest(text, maxWidth, fontMetrics, trimLeadingWhitespace, trimTrailingWhitespace, preserveEmptyLine);
    }
}
