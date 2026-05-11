package com.bytefuture.easy.poster.element.v2.text.layout;

import com.bytefuture.easy.poster.element.v2.text.style.ResolvedStyleContext;
import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextSpan;
import com.bytefuture.easy.poster.element.v2.text.style.TextBlockStyle;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.model.VerticalAlign;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 竖排文本布局引擎
 * 负责逐字符拆列、列方向排列计算、列内对齐以及最终绘制起点推导
 *
 * @author biaoy
 * @since 2026/05/07
 */
public class VerticalTextLayoutEngine {

    private final TextMeasurer measurer = new TextMeasurer();
    private final VerticalTextSplitter splitter = new VerticalTextSplitter();
    private final PunctuationAdjuster punctuationAdjuster = new PunctuationAdjuster();

    /**
     * 计算竖排文本布局结果
     *
     * @param styleContext 样式解析结果
     * @param position     元素位置
     * @param graphics     图形上下文
     * @param posterWidth  海报宽度
     * @param posterHeight 海报高度
     * @return 竖排布局结果
     */
    public VerticalTextLayoutResult layout(ResolvedStyleContext styleContext, Position position,
                                           Graphics2D graphics, int posterWidth, int posterHeight) {
        if (styleContext.getResolvedTextSpans().isEmpty()) {
            return VerticalTextLayoutResult.empty(position);
        }

        List<ResolvedTextSpan> resolvedTextSpans = styleContext.getResolvedTextSpans();
        Font baseFont = styleContext.getBaseFont();
        TextBlockStyle blockStyle = styleContext.getBlockStyle();

        // 计算列宽（= lineHeight）和列间距
        int lineHeight = resolveLineHeight(graphics, resolvedTextSpans, baseFont, blockStyle);
        int columnSpacing = blockStyle.getColumnSpacing();
        int baselineOffset = resolveBaselineOffset(graphics, resolvedTextSpans, baseFont);

        // 拆列
        Integer heightLimit = blockStyle.getMaxVerticalWidth();
        List<TextColumn> columns = splitter.splitColumns(graphics, resolvedTextSpans, heightLimit);

        // 避头尾调整
        columns = punctuationAdjuster.adjust(columns);

        // 为每列赋宽度
        columns = assignColumnWidths(columns, lineHeight);

        // 列内对齐
        VerticalAlign verticalAlign = blockStyle.getVerticalAlign();
        columns = alignColumns(columns, verticalAlign, heightLimit);

        // 计算布局宽高
        int totalWidth = columns.size() * lineHeight + Math.max(0, columns.size() - 1) * columnSpacing;
        int layoutHeight = heightLimit != null && heightLimit > 0
                ? heightLimit
                : resolveMaxColumnHeight(columns);

        // 点位计算
        Point point = resolvePoint(position, posterWidth, posterHeight, totalWidth, layoutHeight);

        // 绘制 Y 偏移（绝对定位场景基线修正）
        int drawOffsetY = position instanceof AbsolutePosition
                ? blockStyle.getBaseLine().getOffset(graphics.getFontMetrics(baseFont), lineHeight)
                : baselineOffset;

        return new VerticalTextLayoutResult(point, totalWidth, layoutHeight, lineHeight, columnSpacing, baselineOffset, drawOffsetY, columns);
    }

    /** 为每列设置宽度 */
    private List<TextColumn> assignColumnWidths(List<TextColumn> columns, int lineHeight) {
        List<TextColumn> result = new ArrayList<>(columns.size());
        for (TextColumn column : columns) {
            result.add(new TextColumn(column.getText(), lineHeight, column.getHeight(), column.getOffsetX(), column.getCharacters()));
        }
        return result;
    }

    /** 列内对齐：根据 verticalAlign 为每个 CharCell 设置 offsetY */
    private List<TextColumn> alignColumns(List<TextColumn> columns, VerticalAlign verticalAlign, Integer heightLimit) {
        int columnHeight = (heightLimit != null && heightLimit > 0) ? heightLimit : 0;
        List<TextColumn> result = new ArrayList<>(columns.size());
        for (TextColumn column : columns) {
            int contentHeight = column.getHeight();
            int availableHeight = columnHeight > 0 ? columnHeight : contentHeight;
            int alignOffset = verticalAlign.offset(availableHeight, contentHeight);

            List<CharCell> alignedCells = new ArrayList<>(column.getCharacters().size());
            for (CharCell cell : column.getCharacters()) {
                alignedCells.add(cell.withOffsetY(cell.getOffsetY() + alignOffset));
            }
            result.add(column.withLayout(column.getOffsetX(), alignedCells));
        }
        return result;
    }

    /** 计算所有列中的最大高度 */
    private int resolveMaxColumnHeight(List<TextColumn> columns) {
        int maxHeight = 0;
        for (TextColumn column : columns) {
            maxHeight = Math.max(maxHeight, column.getHeight());
        }
        return maxHeight;
    }

    /** 计算统一行高（列宽） */
    private int resolveLineHeight(Graphics2D graphics, List<ResolvedTextSpan> resolvedTextSpans, Font baseFont, TextBlockStyle blockStyle) {
        if (blockStyle.getLineHeight() != null) {
            return blockStyle.getLineHeight();
        }
        int maxWidth = measurer.measureWidth(graphics, "M", baseFont);
        for (ResolvedTextSpan span : resolvedTextSpans) {
            maxWidth = Math.max(maxWidth, measurer.measureWidth(graphics, "M", span.getStyle().getFont()));
        }
        return maxWidth;
    }

    /** 计算统一基线偏移 */
    private int resolveBaselineOffset(Graphics2D graphics, List<ResolvedTextSpan> resolvedTextSpans, Font baseFont) {
        int maxAscent = measurer.getAscent(graphics, baseFont);
        for (ResolvedTextSpan span : resolvedTextSpans) {
            maxAscent = Math.max(maxAscent, measurer.getAscent(graphics, span.getStyle().getFont()));
        }
        return maxAscent;
    }

    /** 解析绘制起点 */
    private Point resolvePoint(Position position, int posterWidth, int posterHeight, int width, int height) {
        if (position == null) {
            return Point.ORIGIN_COORDINATE;
        }
        return position.calculate(posterWidth, posterHeight, width, height);
    }
}