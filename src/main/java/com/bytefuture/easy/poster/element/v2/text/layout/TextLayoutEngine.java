package com.bytefuture.easy.poster.element.v2.text.layout;

import com.bytefuture.easy.poster.element.v2.text.resolve.ResolvedStyleContext;
import com.bytefuture.easy.poster.element.v2.text.resolve.ResolvedTextRun;
import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextStyle;
import com.bytefuture.easy.poster.element.v2.text.style.TextBlockStyle;
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

    /**
     * 文本测量器
     */
    private final TextMeasurer measurer = new TextMeasurer();

    /**
     * 计算文本布局结果
     *
     * @param styleContext  样式解析结果
     * @param position      元素位置
     * @param graphics      图形上下文
     * @param posterWidth   海报宽度
     * @param posterHeight  海报高度
     * @return 布局结果
     */
    public TextLayoutResult layout(ResolvedStyleContext styleContext, Position position,
                                   Graphics2D graphics, int posterWidth, int posterHeight) {
        // 空文本直接返回空布局结果
        if (styleContext.getRuns().isEmpty()) {
            return TextLayoutResult.empty(position);
        }

        List<ResolvedTextRun> runs = styleContext.getRuns();
        Font baseFont = styleContext.getBaseFont();

        // 计算行高和基线偏移
        int lineHeight = resolveLineHeight(graphics, runs, baseFont, styleContext.getBlockStyle(), measurer);
        int baselineOffset = resolveBaselineOffset(graphics, runs, baseFont, measurer);

        // 只有开启自动换行时才启用宽度约束
        int widthLimit = styleContext.getBlockStyle().isAutoWordWrap()
                ? Math.max(0, styleContext.getBlockStyle().getMaxTextWidth())
                : 0;

        // wrapRuns 负责把按样式拆分后的 runs 进一步整理成"行"
        List<TextLine> lines = wrapRuns(graphics, runs, widthLimit, measurer);

        // 自动换行场景下，布局宽度应当服从外部给定的最大宽度；不换行时则取真实内容最宽的一行
        int layoutWidth = widthLimit > 0 ? widthLimit : resolveMaxLineWidth(lines);

        // 文本对齐本质上是为每一行计算相对于布局区域左上角的 X 方向偏移
        List<TextLine> alignedLines = alignLines(lines, layoutWidth, styleContext.getBlockStyle().getTextAlign());
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
     * 根据宽度限制将文本运行单元切分为多行
     *
     * @param graphics   图形上下文
     * @param runs       已解析样式的文本运行单元
     * @param widthLimit 单行最大宽度；小于等于 0 表示不限制
     * @param measurer   文本测量器
     * @return 按顺序生成的文本行
     */
    private List<TextLine> wrapRuns(Graphics2D graphics, List<ResolvedTextRun> runs, int widthLimit, TextMeasurer measurer) {
        List<Token> tokens = tokenizeRuns(runs, graphics, measurer);
        if (tokens.isEmpty()) {
            return Collections.singletonList(TextLine.empty());
        }

        List<TextLine> lines = new ArrayList<>();
        List<Token> current = new ArrayList<>();
        int currentWidth = 0;

        for (Token token : tokens) {
            // 显式换行符优先级最高
            if (token.type == TokenType.NEWLINE) {
                lines.add(buildLine(current, currentWidth));
                current = new ArrayList<>();
                currentWidth = 0;
                continue;
            }

            // 不限制宽度时，所有 token 顺序累加
            if (widthLimit <= 0) {
                current.add(token);
                currentWidth += token.width;
                continue;
            }

            // 新行起始位置出现空白直接丢弃
            if (token.type == TokenType.SPACE && current.isEmpty()) {
                continue;
            }

            // 单个 token 自身已经超过整行宽度时，必须进一步拆分
            if (token.width > widthLimit) {
                if (!current.isEmpty()) {
                    lines.add(buildLine(current, currentWidth));
                    current = new ArrayList<>();
                    currentWidth = 0;
                }
                List<Token> pieces = splitOversizedToken(token, widthLimit, graphics, measurer);
                for (int i = 0; i < pieces.size(); i++) {
                    Token piece = pieces.get(i);
                    if (i == pieces.size() - 1) {
                        current.add(piece);
                        currentWidth = piece.width;
                    } else {
                        lines.add(buildLine(Collections.singletonList(piece), piece.width));
                    }
                }
                continue;
            }

            // 当前 token 加入后仍未超宽，或者当前行还是空行时，直接吸收
            if (currentWidth + token.width <= widthLimit || current.isEmpty()) {
                current.add(token);
                currentWidth += token.width;
                continue;
            }

            // 超宽的是空格时，直接在空格处分行
            if (token.type == TokenType.SPACE) {
                lines.add(buildLine(current, currentWidth));
                current = new ArrayList<>();
                currentWidth = 0;
                continue;
            }

            // 普通单词导致超宽：先提交当前行，再把该单词作为下一行的起点
            lines.add(buildLine(current, currentWidth));
            current = new ArrayList<>();
            current.add(token);
            currentWidth = token.width;
        }

        // 循环结束后仍然可能有尚未提交的尾行
        if (!current.isEmpty() || lines.isEmpty()) {
            lines.add(buildLine(current, currentWidth));
        }
        return lines;
    }

    /**
     * 将运行单元拆分为可参与换行判断的 token
     *
     * @param runs     文本运行单元
     * @param graphics 图形上下文
     * @param measurer 文本测量器
     * @return token 列表
     */
    private List<Token> tokenizeRuns(List<ResolvedTextRun> runs, Graphics2D graphics, TextMeasurer measurer) {
        List<Token> tokens = new ArrayList<>();
        for (ResolvedTextRun run : runs) {
            String text = run.getText();
            if (text.isEmpty()) {
                continue;
            }
            StringBuilder buffer = new StringBuilder();
            TokenType currentType = null;
            for (int i = 0; i < text.length(); ) {
                int codePoint = text.codePointAt(i);
                String ch = new String(Character.toChars(codePoint));
                i += Character.charCount(codePoint);

                // CRLF 场景下忽略 \r，只保留 \n
                if ("\r".equals(ch)) {
                    continue;
                }
                if ("\n".equals(ch)) {
                    flushBufferedToken(tokens, buffer, currentType, run, graphics, measurer);
                    currentType = null;
                    tokens.add(new Token(TokenType.NEWLINE, "\n", run.getStyle(), 0));
                    continue;
                }

                TokenType tokenType = Character.isWhitespace(codePoint) ? TokenType.SPACE : TokenType.WORD;
                if (currentType != null && currentType != tokenType) {
                    flushBufferedToken(tokens, buffer, currentType, run, graphics, measurer);
                }
                currentType = tokenType;
                buffer.append(ch);
            }
            flushBufferedToken(tokens, buffer, currentType, run, graphics, measurer);
        }
        return tokens;
    }

    /**
     * 将缓冲中的连续字符输出为一个 token
     */
    private void flushBufferedToken(List<Token> tokens, StringBuilder buffer, TokenType type,
                                    ResolvedTextRun run, Graphics2D graphics, TextMeasurer measurer) {
        if (type == null || buffer.length() == 0) {
            return;
        }
        String text = buffer.toString();
        int width = measurer.measureWidth(graphics, text, run.getStyle().getFont());
        tokens.add(new Token(type, text, run.getStyle(), width));
        buffer.setLength(0);
    }

    /**
     * 将单个超宽 token 按字符级别拆分为多个可容纳片段
     */
    private List<Token> splitOversizedToken(Token token, int widthLimit, Graphics2D graphics, TextMeasurer measurer) {
        List<Token> pieces = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int width = 0;
        for (int i = 0; i < token.text.length(); ) {
            int codePoint = token.text.codePointAt(i);
            String ch = new String(Character.toChars(codePoint));
            i += Character.charCount(codePoint);
            int charWidth = measurer.measureWidth(graphics, ch, token.style.getFont());
            if (builder.length() > 0 && width + charWidth > widthLimit) {
                pieces.add(new Token(TokenType.WORD, builder.toString(), token.style, width));
                builder.setLength(0);
                width = 0;
            }
            builder.append(ch);
            width += charWidth;
        }
        if (builder.length() > 0) {
            pieces.add(new Token(TokenType.WORD, builder.toString(), token.style, width));
        }
        return pieces;
    }

    /**
     * 将一行 token 重新组装为 TextLine
     */
    private TextLine buildLine(List<Token> tokens, int width) {
        if (tokens.isEmpty()) {
            return TextLine.empty();
        }
        List<TextLine.Segment> segments = new ArrayList<>(tokens.size());
        StringBuilder builder = new StringBuilder();
        for (Token token : tokens) {
            builder.append(token.text);
            boolean stretchableSpace = token.type == TokenType.SPACE;
            if (!segments.isEmpty() && canMergeSegment(segments.get(segments.size() - 1), token)) {
                TextLine.Segment previous = segments.remove(segments.size() - 1);
                segments.add(new TextLine.Segment(previous.getText() + token.text, previous.getStyle(),
                        0, previous.getWidth() + token.width, previous.isStretchableSpace()));
            } else {
                segments.add(new TextLine.Segment(token.text, token.style, 0, token.width, stretchableSpace));
            }
        }
        return new TextLine(builder.toString(), width, 0, segments);
    }

    /**
     * 判断两个解析后样式是否可视为同一绘制样式
     */
    private boolean hasSameStyle(ResolvedTextStyle left, ResolvedTextStyle right) {
        return left.getFont().equals(right.getFont())
                && left.getColor().equals(right.getColor())
                && left.isUnderline() == right.isUnderline()
                && left.isStrikeThrough() == right.isStrikeThrough();
    }

    private boolean canMergeSegment(TextLine.Segment previous, Token token) {
        return hasSameStyle(previous.getStyle(), token.style)
                && previous.isStretchableSpace() == (token.type == TokenType.SPACE);
    }

    /**
     * 根据对齐方式为每一行计算横向偏移量
     */
    private List<TextLine> alignLines(List<TextLine> lines, int layoutWidth, TextAlign align) {
        List<TextLine> aligned = new ArrayList<>(lines.size());
        for (TextLine line : lines) {
            if (align == TextAlign.JUSTIFY && layoutWidth > line.getWidth()) {
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
        return index > 0
                && index < segments.size() - 1
                && segments.get(index).isStretchableSpace();
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
    private int resolveLineHeight(Graphics2D graphics, List<ResolvedTextRun> runs, Font baseFont,
                                  TextBlockStyle blockStyle, TextMeasurer measurer) {
        // 块级样式显式指定行高时，直接采用该值
        if (blockStyle.getLineHeight() != null) {
            return blockStyle.getLineHeight();
        }
        int maxHeight = measurer.getFontHeight(graphics, baseFont);
        for (ResolvedTextRun run : runs) {
            maxHeight = Math.max(maxHeight, measurer.getFontHeight(graphics, run.getStyle().getFont()));
        }
        return maxHeight;
    }

    /**
     * 计算统一基线偏移
     */
    private int resolveBaselineOffset(Graphics2D graphics, List<ResolvedTextRun> runs, Font baseFont, TextMeasurer measurer) {
        int maxAscent = measurer.getAscent(graphics, baseFont);
        for (ResolvedTextRun run : runs) {
            maxAscent = Math.max(maxAscent, measurer.getAscent(graphics, run.getStyle().getFont()));
        }
        return maxAscent;
    }

    private enum TokenType {
        WORD,
        SPACE,
        NEWLINE
    }

    private static final class Token {
        private final TokenType type;
        private final String text;
        private final ResolvedTextStyle style;
        private final int width;

        private Token(TokenType type, String text, ResolvedTextStyle style, int width) {
            this.type = type;
            this.text = text;
            this.style = style;
            this.width = width;
        }
    }
}