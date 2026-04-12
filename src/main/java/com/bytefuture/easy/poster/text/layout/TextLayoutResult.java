package com.bytefuture.easy.poster.text.layout;

import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextOverflowStrategy;
import com.bytefuture.easy.poster.utils.RotateUtils;
import lombok.Getter;

import java.awt.Font;
import java.util.List;

@Getter
public final class TextLayoutResult {
    /** 实际参与绘制的字体，可能已被自动缩放。 */
    private final Font font;
    /** 文本锚点基线。 */
    private final BaseLine baseLine;
    /** 水平对齐方式。 */
    private final TextAlign textAlign;
    /** 溢出策略。 */
    private final TextOverflowStrategy overflowStrategy;
    /** 统一行高。 */
    private final int lineHeight;
    /** 基线相对每行顶部的偏移量。 */
    private final int baselineOffset;
    /** 整个文本块左上角坐标。 */
    private final Point point;
    /** 整个文本块总宽度，包含背景和装饰外扩。 */
    private final int width;
    /** 整个文本块总高度，包含背景和装饰外扩。 */
    private final int height;
    /** 文本内容宽度，不含背景内边距与装饰外扩。 */
    private final int contentWidth;
    /** 文本内容高度，不含背景内边距与装饰外扩。 */
    private final int contentHeight;
    /** 文本背景宽度，包含内边距。 */
    private final int backgroundWidth;
    /** 文本背景高度，包含内边距。 */
    private final int backgroundHeight;
    /** 按行拆分后的布局结果。 */
    private final List<LayoutLine> lines;
    /** 是否发生了截断。 */
    private final boolean truncated;
    /** 是否需要在绘制阶段裁剪溢出内容。 */
    private final boolean clipOverflow;
    /** 装饰造成的额外外边界。 */
    private final TextDecorationInsets decorationInsets;
    /** 文本背景内边距。 */
    private final TextPaddingInsets textPadding;

    public TextLayoutResult(Font font, BaseLine baseLine, TextAlign textAlign, TextOverflowStrategy overflowStrategy,
                            int lineHeight, int baselineOffset, Point point, int width, int height,
                            int contentWidth, int contentHeight, int backgroundWidth, int backgroundHeight,
                            List<LayoutLine> lines, boolean truncated, boolean clipOverflow,
                            TextDecorationInsets decorationInsets, TextPaddingInsets textPadding) {
        this.font = font;
        this.baseLine = baseLine;
        this.textAlign = textAlign;
        this.overflowStrategy = overflowStrategy;
        this.lineHeight = lineHeight;
        this.baselineOffset = baselineOffset;
        this.point = point;
        this.width = width;
        this.height = height;
        this.contentWidth = contentWidth;
        this.contentHeight = contentHeight;
        this.backgroundWidth = backgroundWidth;
        this.backgroundHeight = backgroundHeight;
        this.lines = lines;
        this.truncated = truncated;
        this.clipOverflow = clipOverflow;
        this.decorationInsets = decorationInsets;
        this.textPadding = textPadding;
    }

    public Dimension toDimension(int rotate) {
        // Dimension 会被外层元素框架继续使用，因此这里把文本块原点与偏移统一折算好。
        Dimension.DimensionBuilder builder = Dimension.builder()
                .width(this.width)
                .height(this.height)
                .xOffset(this.decorationInsets.getLeft() + this.textPadding.getLeft())
                .yOffset(this.baselineOffset + this.decorationInsets.getTop() + this.textPadding.getTop())
                .point(Point.of(this.point.getX(), this.point.getY()));
        if (rotate != 0) {
            // 旋转后需要补充旋转包围盒尺寸，避免后续定位区域过小。
            int[] bounds = RotateUtils.newBounds(this.width, this.height, rotate);
            builder.rotateWidth(bounds[0])
                    .rotateHeight(bounds[1]);
        }
        return builder.build();
    }
}
