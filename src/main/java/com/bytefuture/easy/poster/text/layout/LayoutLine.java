package com.bytefuture.easy.poster.text.layout;

import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.text.wrap.RichTextFragment;
import lombok.Getter;

import java.util.List;

@Getter
public final class LayoutLine {
    /** 当前行最终要绘制的文本内容。 */
    private final String text;
    /** 当前行的真实文本宽度。 */
    private final int width;
    /** 当前行内容区域左上角坐标。 */
    private final Point point;
    /** 是否按两端对齐方式绘制。 */
    private final boolean justified;
    /** 当前行实际可绘制宽度，用于调试框和裁剪场景。 */
    private final int renderWidth;
    /** 富文本片段列表；纯文本场景下为空。 */
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
