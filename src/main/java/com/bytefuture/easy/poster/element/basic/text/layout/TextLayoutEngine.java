package com.bytefuture.easy.poster.element.basic.text.layout;

import com.bytefuture.easy.poster.element.basic.text.style.*;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.model.TextAlign;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文本布局引擎
 * 负责分词换行、对齐计算以及最终绘制起点推导
 *
 * @author biaoy
 * @since 2026/04/26
 */
public class TextLayoutEngine {

    private final TextMeasurer measurer = new TextMeasurer();

    private final TextSplitter splitter = new TextSplitter();

    /**
     * 计算文本布局结果
     *
     * @param styleContext 样式解析结果
     * @param position     元素位置
     * @param graphics     图形上下文
     * @param posterWidth  海报宽度
     * @param posterHeight 海报高度
     * @return 布局结果
     */
    public TextLayoutResult layout(ResolvedStyleContext styleContext, Position position,
                                   Graphics2D graphics, int posterWidth, int posterHeight) {
        // 空文本直接返回空布局结果
        if (styleContext.getResolvedTextSpans().isEmpty()) {
            return TextLayoutResult.empty(position);
        }

        List<ResolvedTextSpan> resolvedTextSpans = styleContext.getResolvedTextSpans();
        Font baseFont = styleContext.getBaseFont();

        // 计算行高和基线偏移
        int lineHeight = resolveLineHeight(graphics, resolvedTextSpans, baseFont, styleContext.getBlockStyle());
        int baselineOffset = resolveBaselineOffset(graphics, resolvedTextSpans, baseFont);

        // 只有开启自动换行时才启用宽度约束
        int widthLimit = styleContext.getBlockStyle().getMaxTextWidth();

        // wrapRuns 负责把按样式拆分后的 resolvedTextSpans 进一步整理成"行"
        List<TextLine> wrappedLines = splitter.splitLines(graphics, resolvedTextSpans, widthLimit);
        ClampResult clampResult = clampLines(graphics, wrappedLines, widthLimit, styleContext.getBlockStyle());
        List<TextLine> lines = clampResult.lines;

        // 自动换行场景下，布局宽度应当服从外部给定的最大宽度；不换行时则取真实内容最宽的一行
        int layoutWidth = widthLimit > 0 ? widthLimit : resolveMaxLineWidth(lines);

        // 文本对齐本质上是为每一行计算相对于布局区域左上角的 X 方向偏移
        List<TextLine> alignedLines = alignLines(lines, layoutWidth, styleContext.getBlockStyle().getTextAlign(),
                clampResult.lastLineTruncated);
        int totalHeight = alignedLines.size() * lineHeight;

        // 点位计算依赖最终布局宽高
        Point point = resolvePoint(position, posterWidth, posterHeight, layoutWidth, totalHeight);

        // 绝对定位场景允许通过 blockStyle 的基线策略强制修正绘制起点
        int drawOffsetY = position instanceof AbsolutePosition
                ? styleContext.getBlockStyle().getBaseLine().getOffset(graphics.getFontMetrics(baseFont), lineHeight)
                : baselineOffset;

        return new TextLayoutResult(point, layoutWidth, totalHeight, lineHeight, baselineOffset, drawOffsetY, alignedLines);
    }


    /**
     * 按最大行数约束文本行数
     *
     * @param graphics   图形上下文
     * @param lines      已完成自动换行的文本行
     * @param widthLimit 自动换行宽度限制
     * @param blockStyle 块级样式
     * @return 处理后的文本行
     */
    private ClampResult clampLines(Graphics2D graphics, List<TextLine> lines, int widthLimit,
                                   TextBlockStyle blockStyle) {
        if (blockStyle.getMaxTextWidth() == 0 || blockStyle.getMaxLines() == null || widthLimit <= 0) {
            return new ClampResult(lines, false);
        }
        int maxLines = blockStyle.getMaxLines();
        if (lines.size() <= maxLines) {
            return new ClampResult(lines, false);
        }
        List<TextLine> clamped = new ArrayList<>(maxLines);
        for (int i = 0; i < maxLines - 1; i++) {
            clamped.add(lines.get(i));
        }
        clamped.add(truncateLine(graphics, lines.get(maxLines - 1), widthLimit, blockStyle.getTextOverflow(), measurer, true));
        return new ClampResult(clamped, true);
    }

    /**
     * 将最后保留的一行裁剪到给定宽度内
     *
     * @param graphics     图形上下文
     * @param line         原始文本行
     * @param widthLimit   可用宽度
     * @param textOverflow 超出策略
     * @param measurer     文本测量器
     * @return 裁剪后的文本行
     */
    private TextLine truncateLine(Graphics2D graphics, TextLine line, int widthLimit,
                                  TextOverflow textOverflow, TextMeasurer measurer, boolean forceOverflowHandling) {
        if (!forceOverflowHandling && line.getWidth() <= widthLimit) {
            return line;
        }
        int suffixWidth = 0;
        String suffix = "";
        if (textOverflow == TextOverflow.ELLIPSIS) {
            suffix = resolveEllipsis(graphics, line, widthLimit, measurer);
            suffixWidth = measureSuffixWidth(graphics, line, suffix, measurer);
        }
        List<TextLine.Segment> segments = new ArrayList<>();
        int occupiedWidth = 0;
        for (TextLine.Segment segment : line.getSegments()) {
            if (occupiedWidth >= widthLimit - suffixWidth) {
                break;
            }
            int remainingWidth = widthLimit - suffixWidth - occupiedWidth;
            TextLine.Segment truncatedSegment = truncateSegment(graphics, segment, remainingWidth, measurer);
            if (truncatedSegment == null) {
                break;
            }
            segments.add(truncatedSegment);
            occupiedWidth += truncatedSegment.getWidth();
            if (truncatedSegment.getText().length() < segment.getText().length()) {
                break;
            }
        }
        segments = trimTrailingSpaceSegments(graphics, segments, measurer);
        if (textOverflow == TextOverflow.ELLIPSIS && !suffix.isEmpty()) {
            segments = appendSuffix(graphics, segments, line, suffix, measurer);
        }
        return rebuildLine(segments);
    }

    /**
     * 生成省略符文本
     */
    private String resolveEllipsis(Graphics2D graphics, TextLine line, int widthLimit, TextMeasurer measurer) {
        String[] candidates = new String[]{"...", "..", "."};
        for (String candidate : candidates) {
            int width = measureSuffixWidth(graphics, line, candidate, measurer);
            if (width <= widthLimit) {
                return candidate;
            }
        }
        return "";
    }

    /**
     * 计算省略符宽度
     */
    private int measureSuffixWidth(Graphics2D graphics, TextLine line, String suffix, TextMeasurer measurer) {
        if (suffix.isEmpty()) {
            return 0;
        }
        ResolvedTextStyle style = resolveSuffixStyle(line);
        return measurer.measureWidthWithSpacing(graphics, suffix, style.getFont(), style.getLetterSpacing());
    }

    /**
     * 裁剪单个文本片段
     */
    private TextLine.Segment truncateSegment(Graphics2D graphics, TextLine.Segment segment, int widthLimit, TextMeasurer measurer) {
        if (widthLimit <= 0) {
            return null;
        }
        if (segment.getWidth() <= widthLimit) {
            return segment;
        }
        StringBuilder builder = new StringBuilder();
        int width = 0;
        String text = segment.getText();
        for (int i = 0; i < text.length(); ) {
            int codePoint = text.codePointAt(i);
            String ch = new String(Character.toChars(codePoint));
            int charWidth = measurer.measureWidthWithSpacing(graphics, ch, segment.getStyle().getFont(), segment.getLetterSpacing());
            if (builder.length() > 0 && width + charWidth > widthLimit) {
                break;
            }
            if (builder.length() == 0 && charWidth > widthLimit) {
                return null;
            }
            builder.append(ch);
            width += charWidth;
            i += Character.charCount(codePoint);
        }
        if (builder.length() == 0) {
            return null;
        }
        return new TextLine.Segment(builder.toString(), segment.getStyle(), 0, width,
                segment.isStretchableSpace(), segment.getLetterSpacing());
    }

    /**
     * 移除末尾空白片段及片段末尾空白字符
     */
    private List<TextLine.Segment> trimTrailingSpaceSegments(Graphics2D graphics, List<TextLine.Segment> segments, TextMeasurer measurer) {
        List<TextLine.Segment> trimmed = new ArrayList<>(segments);
        while (!trimmed.isEmpty()) {
            TextLine.Segment last = trimmed.get(trimmed.size() - 1);
            String text = trimTrailingWhitespace(last.getText());
            if (text.isEmpty()) {
                trimmed.remove(trimmed.size() - 1);
                continue;
            }
            if (!text.equals(last.getText())) {
                int width = measurer.measureWidthWithSpacing(graphics, text, last.getStyle().getFont(), last.getLetterSpacing());
                trimmed.set(trimmed.size() - 1, new TextLine.Segment(text, last.getStyle(), 0, width,
                        false, last.getLetterSpacing()));
            }
            break;
        }
        return trimmed;
    }

    /**
     * 追加省略符
     */
    private List<TextLine.Segment> appendSuffix(Graphics2D graphics, List<TextLine.Segment> segments,
                                                TextLine line, String suffix, TextMeasurer measurer) {
        List<TextLine.Segment> result = new ArrayList<>(segments);
        ResolvedTextStyle style = !result.isEmpty()
                ? result.get(result.size() - 1).getStyle()
                : resolveSuffixStyle(line);
        int width = measurer.measureWidthWithSpacing(graphics, suffix, style.getFont(), style.getLetterSpacing());
        // if (!result.isEmpty() && hasSameStyle(result.get(result.size() - 1).getStyle(), style)) {
        //     TextLine.Segment last = result.remove(result.size() - 1);
        //     result.add(new TextLine.Segment(last.getText() + suffix, style, 0, last.getWidth() + width,
        //             false, style.getLetterSpacing()));
        //     return result;
        // }
        result.add(new TextLine.Segment(suffix, style, 0, width, false, style.getLetterSpacing()));
        return result;
    }

    /**
     * 选择省略符样式
     */
    private ResolvedTextStyle resolveSuffixStyle(TextLine line) {
        List<TextLine.Segment> segments = line.getSegments();
        if (segments.isEmpty()) {
            throw new IllegalStateException("line must contain at least one segment");
        }
        return segments.get(segments.size() - 1).getStyle();
    }

    /**
     * 重建文本行宽度与片段
     */
    private TextLine rebuildLine(List<TextLine.Segment> segments) {
        if (segments.isEmpty()) {
            return TextLine.empty();
        }
        List<TextLine.Segment> sequenced = sequenceSegments(segments);
        StringBuilder builder = new StringBuilder();
        int width = 0;
        for (TextLine.Segment segment : sequenced) {
            builder.append(segment.getText());
            width += segment.getWidth();
        }
        return new TextLine(builder.toString(), width, 0, sequenced);
    }

    /**
     * 删除末尾空白字符
     */
    private String trimTrailingWhitespace(String text) {
        int end = text.length();
        while (end > 0) {
            int codePoint = text.codePointBefore(end);
            if (!Character.isWhitespace(codePoint)) {
                break;
            }
            end -= Character.charCount(codePoint);
        }
        return text.substring(0, end);
    }

    /**
     * 根据对齐方式为每一行计算横向偏移量
     */
    private List<TextLine> alignLines(List<TextLine> lines, int layoutWidth, TextAlign align, boolean lastLineTruncated) {
        List<TextLine> aligned = new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            TextLine line = lines.get(i);
            boolean justify = align == TextAlign.JUSTIFY
                    && layoutWidth > line.getWidth()
                    && (i < lines.size() - 1 || !lastLineTruncated);
            if (justify) {
                List<TextLine.Segment> justifiedSegments = justifySegments(line, layoutWidth);
                if (!justifiedSegments.isEmpty()) {
                    aligned.add(line.withLayout(0, justifiedSegments));
                    continue;
                }
            }
            aligned.add(line.withLayout(align.offset(layoutWidth, line.getWidth()), sequenceSegments(line.getSegments())));
        }
        return aligned;
    }

    private List<TextLine.Segment> sequenceSegments(List<TextLine.Segment> segments) {
        List<TextLine.Segment> sequenced = new ArrayList<>(segments.size());
        int offsetX = 0;
        for (TextLine.Segment segment : segments) {
            sequenced.add(segment.withOffsetX(offsetX));
            offsetX += segment.getWidth();
        }
        return sequenced;
    }

    private List<TextLine.Segment> justifySegments(TextLine line, int layoutWidth) {
        int stretchableCount = countStretchableSpaces(line.getSegments());
        if (stretchableCount <= 0) {
            return Collections.emptyList();
        }
        int extraWidth = layoutWidth - line.getWidth();
        int baseExtra = extraWidth / stretchableCount;
        int remainder = extraWidth % stretchableCount;
        List<TextLine.Segment> justified = new ArrayList<>(line.getSegments().size());
        int offsetX = 0;
        for (int i = 0; i < line.getSegments().size(); i++) {
            TextLine.Segment segment = line.getSegments().get(i);
            justified.add(segment.withOffsetX(offsetX));
            offsetX += segment.getWidth();
            if (isJustifiableSpace(line.getSegments(), i)) {
                offsetX += baseExtra;
                if (remainder > 0) {
                    offsetX++;
                    remainder--;
                }
            }
        }
        return justified;
    }

    private int countStretchableSpaces(List<TextLine.Segment> segments) {
        int count = 0;
        for (int i = 0; i < segments.size(); i++) {
            if (isJustifiableSpace(segments, i)) {
                count++;
            }
        }
        return count;
    }

    private boolean isJustifiableSpace(List<TextLine.Segment> segments, int index) {
        return index > 0 && index < segments.size() - 1 && segments.get(index).isStretchableSpace();
    }

    /**
     * 计算所有行中的最大宽度
     */
    private int resolveMaxLineWidth(List<TextLine> lines) {
        int maxWidth = 0;
        for (TextLine line : lines) {
            maxWidth = Math.max(maxWidth, line.getWidth());
        }
        return maxWidth;
    }

    /**
     * 根据定位策略解析文本块左上角坐标
     */
    private Point resolvePoint(Position position, int posterWidth, int posterHeight, int width, int height) {
        if (position == null) {
            return Point.ORIGIN_COORDINATE;
        }
        return position.calculate(posterWidth, posterHeight, width, height);
    }

    /**
     * 计算统一行高
     */
    private int resolveLineHeight(Graphics2D graphics, List<ResolvedTextSpan> resolvedTextSpans, Font baseFont,
                                  TextBlockStyle blockStyle) {
        // 块级样式显式指定行高时，直接采用该值
        if (blockStyle.getLineHeight() != null) {
            return blockStyle.getLineHeight();
        }
        int maxHeight = measurer.getFontHeight(graphics, baseFont);
        for (ResolvedTextSpan resolvedTextSpan : resolvedTextSpans) {
            maxHeight = Math.max(maxHeight, measurer.getFontHeight(graphics, resolvedTextSpan.getStyle().getFont()));
        }
        return maxHeight;
    }

    /**
     * 计算统一基线偏移
     */
    private int resolveBaselineOffset(Graphics2D graphics, List<ResolvedTextSpan> resolvedTextSpans, Font baseFont) {
        int maxAscent = measurer.getAscent(graphics, baseFont);
        for (ResolvedTextSpan resolvedTextSpan : resolvedTextSpans) {
            maxAscent = Math.max(maxAscent, measurer.getAscent(graphics, resolvedTextSpan.getStyle().getFont()));
        }
        return maxAscent;
    }

    private static final class ClampResult {
        private final List<TextLine> lines;
        private final boolean lastLineTruncated;

        private ClampResult(List<TextLine> lines, boolean lastLineTruncated) {
            this.lines = lines;
            this.lastLineTruncated = lastLineTruncated;
        }
    }
}
