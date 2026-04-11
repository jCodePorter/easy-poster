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
    private final Font font;
    private final BaseLine baseLine;
    private final TextAlign textAlign;
    private final TextOverflowStrategy overflowStrategy;
    private final int lineHeight;
    private final int baselineOffset;
    private final Point point;
    private final int width;
    private final int height;
    private final int contentWidth;
    private final int contentHeight;
    private final int backgroundWidth;
    private final int backgroundHeight;
    private final List<LayoutLine> lines;
    private final boolean truncated;
    private final boolean clipOverflow;
    private final TextDecorationInsets decorationInsets;
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
        Dimension.DimensionBuilder builder = Dimension.builder()
                .width(this.width)
                .height(this.height)
                .xOffset(this.decorationInsets.getLeft() + this.textPadding.getLeft())
                .yOffset(this.baselineOffset + this.decorationInsets.getTop() + this.textPadding.getTop())
                .point(Point.of(this.point.getX(), this.point.getY()));
        if (rotate != 0) {
            int[] bounds = RotateUtils.newBounds(this.width, this.height, rotate);
            builder.rotateWidth(bounds[0])
                    .rotateHeight(bounds[1]);
        }
        return builder.build();
    }
}
