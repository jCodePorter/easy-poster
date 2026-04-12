package com.bytefuture.easy.poster.text.wrap;

import lombok.Getter;

import java.awt.Color;
import java.awt.Font;

@Getter
public final class RichTextFragment {
    /** 片段文本内容。 */
    private final String text;
    /** 片段相对整行起点的 X 偏移。 */
    private final int xOffset;
    /** 片段宽度。 */
    private final int width;
    /** 片段字体。 */
    private final Font font;
    /** 片段颜色。 */
    private final Color color;
    /** 片段是否绘制下划线。 */
    private final boolean underline;
    /** 片段是否绘制删除线。 */
    private final boolean strikeThrough;

    public RichTextFragment(String text, int xOffset, int width, Font font, Color color,
                            boolean underline, boolean strikeThrough) {
        this.text = text;
        this.xOffset = xOffset;
        this.width = width;
        this.font = font;
        this.color = color;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
    }

    public RichTextFragment shiftX(int offsetX) {
        if (offsetX == 0) {
            return this;
        }
        // 对齐计算只需要整体平移片段，不改变片段自身宽度和样式。
        return new RichTextFragment(this.text, this.xOffset + offsetX, this.width, this.font,
                this.color, this.underline, this.strikeThrough);
    }
}
