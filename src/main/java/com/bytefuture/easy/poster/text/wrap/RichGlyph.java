package com.bytefuture.easy.poster.text.wrap;

import lombok.Getter;

import java.awt.Color;
import java.awt.Font;

@Getter
public final class RichGlyph {
    /** 字形对应的文本，一般为单个字符。 */
    private final String text;
    /** 当前字形宽度。 */
    private final int width;
    /** 当前字形字体。 */
    private final Font font;
    /** 当前字形颜色。 */
    private final Color color;
    /** 当前字形是否绘制下划线。 */
    private final boolean underline;
    /** 当前字形是否绘制删除线。 */
    private final boolean strikeThrough;

    public RichGlyph(String text, int width, Font font, Color color, boolean underline, boolean strikeThrough) {
        this.text = text;
        this.width = width;
        this.font = font;
        this.color = color;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
    }

    public boolean hasSameStyle(RichGlyph other) {
        // 只有样式完全一致时，才能安全合并到同一个渲染片段。
        return this.font.equals(other.font)
                && this.color.equals(other.color)
                && this.underline == other.underline
                && this.strikeThrough == other.strikeThrough;
    }
}
