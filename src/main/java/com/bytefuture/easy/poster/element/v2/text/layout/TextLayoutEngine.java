package com.bytefuture.easy.poster.element.v2.text.layout;

import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.element.v2.text.resolve.ResolvedTextRun;
import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextStyle;
import com.bytefuture.easy.poster.element.v2.text.style.TextBlockStyle;
import com.bytefuture.easy.poster.element.v2.text.style.TextStyleResolver;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.model.Config;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextSpan;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文本布局引擎。
 * 负责样式解析、分词换行、对齐计算以及最终绘制起点推导。
 */
public class TextLayoutEngine {
    private final TextStyleResolver styleResolver = new TextStyleResolver();

    /**
     * 计算文本布局结果。
     *
     * @param element      文本元素
     * @param position     元素位置
     * @param rotate       旋转角度
     * @param context      海报上下文
     * @param posterWidth  海报宽度
     * @param posterHeight 海报高度
     * @return 布局结果
     */
    public TextLayoutResult layout(TextElement element, Position position, int rotate,
                                   PosterContext context, int posterWidth, int posterHeight) {
        // 空文本直接返回空布局结果，避免后续字体测量、分词、换行等流程做无意义计算。
        // 这里保留 position，是为了让调用方在“内容为空”时仍然拿到可感知的定位语义。
        if (element.isEmpty()) {
            return TextLayoutResult.empty(position);
        }

        Graphics2D graphics = context.getGraphics();
        TextBlockStyle blockStyle = element.getBlockStyle();
        // 先确定块级默认字体和颜色，后续每个 span 的局部样式都会基于这两个默认值做覆盖解析。
        Font baseFont = resolveBaseFont(blockStyle, context.getConfig());
        Color defaultColor = resolveDefaultColor(blockStyle, context.getConfig());
        // 将富文本 span 解析为“文本 + 最终样式”的运行单元，后面的测量和换行全部基于该结果进行。
        List<ResolvedTextRun> runs = resolveRuns(element.getTextSpans(), blockStyle, baseFont, defaultColor);
        // 行高取所有运行单元中的最大高度，保证同一行里出现大字号文本时不会被裁切。
        int lineHeight = resolveLineHeight(graphics, runs, baseFont, blockStyle);
        // 基线偏移同样取最大 ascent，确保不同字体、字号混排时能按统一基线绘制。
        int baselineOffset = resolveBaselineOffset(graphics, runs, baseFont);
        // 只有开启自动换行时才启用宽度约束；否则 widthLimit 为 0，表示整段文本按单行连续布局。
        int widthLimit = blockStyle.isAutoWordWrap() ? Math.max(0, blockStyle.getMaxTextWidth()) : 0;
        // wrapRuns 负责把按样式拆分后的 runs 进一步整理成“行”。
        List<TextLine> lines = wrapRuns(graphics, runs, widthLimit);
        // 自动换行场景下，布局宽度应当服从外部给定的最大宽度；不换行时则取真实内容最宽的一行。
        int layoutWidth = widthLimit > 0 ? widthLimit : resolveMaxLineWidth(lines);
        // 文本对齐本质上是为每一行计算相对于布局区域左上角的 X 方向偏移。
        List<TextLine> alignedLines = alignLines(lines, layoutWidth, blockStyle.getTextAlign());
        int totalHeight = alignedLines.size() * lineHeight;
        // 点位计算依赖最终布局宽高，因为居中、右下角等相对定位都要先知道文本实际占位。
        Point point = resolvePoint(position, posterWidth, posterHeight, layoutWidth, totalHeight);
        // 绝对定位场景允许通过 blockStyle 的基线策略强制修正绘制起点；
        // 非绝对定位则统一采用测量出的最大基线偏移，保证相对定位下的整体一致性。
        int drawOffsetY = position instanceof AbsolutePosition
                ? blockStyle.getBaseLine().getOffset(graphics.getFontMetrics(baseFont), lineHeight)
                : baselineOffset;
        return new TextLayoutResult(point, layoutWidth, totalHeight, lineHeight, baselineOffset, drawOffsetY, alignedLines);
    }

    /**
     * 将原始 {@link TextSpan} 集合解析为带最终样式的文本运行单元。
     *
     * @param spans        原始文本片段集合
     * @param blockStyle   文本块样式
     * @param baseFont     解析得到的基础字体
     * @param defaultColor 解析得到的默认颜色
     * @return 样式已收敛的文本运行单元列表
     */
    private List<ResolvedTextRun> resolveRuns(List<TextSpan> spans, TextBlockStyle blockStyle,
                                              Font baseFont, Color defaultColor) {
        if (spans.isEmpty()) {
            return Collections.emptyList();
        }
        List<ResolvedTextRun> runs = new ArrayList<>(spans.size());
        for (TextSpan span : spans) {
            runs.add(styleResolver.resolve(span, blockStyle, baseFont, defaultColor));
        }
        return runs;
    }

    /**
     * 根据宽度限制将文本运行单元切分为多行。
     *
     * @param graphics   图形上下文，用于测量文本宽度
     * @param runs       已解析样式的文本运行单元
     * @param widthLimit 单行最大宽度；小于等于 0 表示不限制
     * @return 按顺序生成的文本行
     */
    private List<TextLine> wrapRuns(Graphics2D graphics, List<ResolvedTextRun> runs, int widthLimit) {
        List<Token> tokens = tokenizeRuns(runs, graphics);
        if (tokens.isEmpty()) {
            return Collections.singletonList(TextLine.empty());
        }

        List<TextLine> lines = new ArrayList<>();
        List<Token> current = new ArrayList<>();
        int currentWidth = 0;

        for (Token token : tokens) {
            // 显式换行符优先级最高，不参与宽度判断，直接结束当前行并开启下一行。
            if (token.type == TokenType.NEWLINE) {
                lines.add(buildLine(current, currentWidth));
                current = new ArrayList<>();
                currentWidth = 0;
                continue;
            }

            // 不限制宽度时，所有 token 顺序累加即可，此时只保留显式换行带来的断行效果。
            if (widthLimit <= 0) {
                current.add(token);
                currentWidth += token.width;
                continue;
            }

            // 新行起始位置出现空白通常没有视觉价值，还会影响对齐和宽度计算，因此直接丢弃。
            if (token.type == TokenType.SPACE && current.isEmpty()) {
                continue;
            }

            // 单个 token 自身已经超过整行宽度时，常规“整词换行”无法处理，
            // 必须进一步拆成更小的片段，否则这一行永远放不下它。
            if (token.width > widthLimit) {
                // 先把已有内容落成一行，避免超宽 token 与前一段文本错误拼接。
                if (!current.isEmpty()) {
                    lines.add(buildLine(current, currentWidth));
                    current = new ArrayList<>();
                    currentWidth = 0;
                }
                List<Token> pieces = splitOversizedToken(token, widthLimit, graphics);
                for (int i = 0; i < pieces.size(); i++) {
                    Token piece = pieces.get(i);
                    // 除最后一段外，前面的拆分片段都可以直接形成完整行；
                    // 最后一段则保留在 current 中，便于继续和后续 token 拼接。
                    if (i == pieces.size() - 1) {
                        current.add(piece);
                        currentWidth = piece.width;
                    } else {
                        lines.add(buildLine(Collections.singletonList(piece), piece.width));
                    }
                }
                continue;
            }

            // 当前 token 加入后仍未超宽，或者当前行还是空行时，直接吸收即可。
            // “空行也允许加入”这一分支保证了非空 token 至少能落到一行里。
            if (currentWidth + token.width <= widthLimit || current.isEmpty()) {
                current.add(token);
                currentWidth += token.width;
                continue;
            }

            // 超宽的是空格时，不需要把空格挪到下一行；直接在空格处分行即可，
            // 这样既能保留单词边界，又不会让新行以空白字符开头。
            if (token.type == TokenType.SPACE) {
                lines.add(buildLine(current, currentWidth));
                current = new ArrayList<>();
                currentWidth = 0;
                continue;
            }

            // 走到这里说明是普通单词导致超宽。
            // 处理策略是：先提交当前行，再把该单词作为下一行的起点。
            lines.add(buildLine(current, currentWidth));
            current = new ArrayList<>();
            current.add(token);
            currentWidth = token.width;
        }

        // 循环结束后仍然可能有尚未提交的尾行；
        // 如果 lines 为空，也要至少补一行空行，保持布局结果结构稳定。
        if (!current.isEmpty() || lines.isEmpty()) {
            lines.add(buildLine(current, currentWidth));
        }
        return lines;
    }

    /**
     * 将运行单元拆分为可参与换行判断的 token。
     * token 的粒度是“单词 / 空白 / 显式换行”。
     *
     * @param runs     文本运行单元
     * @param graphics 图形上下文
     * @return token 列表
     */
    private List<Token> tokenizeRuns(List<ResolvedTextRun> runs, Graphics2D graphics) {
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

                // CRLF 场景下忽略 \r，只保留 \n 作为真正的换行信号，避免生成重复换行 token。
                if ("\r".equals(ch)) {
                    continue;
                }
                // 遇到显式换行前，先把缓冲中的同类字符刷出，再单独记录换行 token。
                if ("\n".equals(ch)) {
                    flushBufferedToken(tokens, buffer, currentType, run, graphics);
                    currentType = null;
                    tokens.add(new Token(TokenType.NEWLINE, "\n", run.getStyle(), 0));
                    continue;
                }

                // 连续空白合并为一个 SPACE token，连续非空白合并为一个 WORD token，
                // 这样换行判断会更接近“按词分行”，同时减少 token 数量。
                TokenType tokenType = Character.isWhitespace(codePoint) ? TokenType.SPACE : TokenType.WORD;
                // token 类型发生切换，说明前一段连续字符已经结束，需要先落盘。
                if (currentType != null && currentType != tokenType) {
                    flushBufferedToken(tokens, buffer, currentType, run, graphics);
                }
                currentType = tokenType;
                buffer.append(ch);
            }
            // run 遍历结束后，把尾部残留字符补成最后一个 token。
            flushBufferedToken(tokens, buffer, currentType, run, graphics);
        }
        return tokens;
    }

    /**
     * 将缓冲中的连续字符输出为一个 token，并同步记录测量宽度。
     *
     * @param tokens   输出 token 列表
     * @param buffer   连续字符缓冲区
     * @param type     缓冲字符对应的 token 类型
     * @param run      样式来源运行单元
     * @param graphics 图形上下文
     */
    private void flushBufferedToken(List<Token> tokens, StringBuilder buffer, TokenType type,
                                    ResolvedTextRun run, Graphics2D graphics) {
        if (type == null || buffer.length() == 0) {
            return;
        }
        String text = buffer.toString();
        int width = measureText(graphics, text, run.getStyle().getFont());
        tokens.add(new Token(type, text, run.getStyle(), width));
        buffer.setLength(0);
    }

    /**
     * 将单个超宽 token 按字符级别拆分为多个可容纳片段。
     *
     * @param token      需要拆分的原始 token
     * @param widthLimit 单行最大宽度
     * @param graphics   图形上下文
     * @return 拆分后的 token 列表
     */
    private List<Token> splitOversizedToken(Token token, int widthLimit, Graphics2D graphics) {
        List<Token> pieces = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int width = 0;
        for (int i = 0; i < token.text.length(); ) {
            int codePoint = token.text.codePointAt(i);
            String ch = new String(Character.toChars(codePoint));
            i += Character.charCount(codePoint);
            int charWidth = measureText(graphics, ch, token.style.getFont());
            // 只要再追加一个字符就会超宽，就先输出前面已经累计的片段。
            // 这里按 code point 拆分，避免把代理对字符拆坏。
            if (builder.length() > 0 && width + charWidth > widthLimit) {
                pieces.add(new Token(TokenType.WORD, builder.toString(), token.style, width));
                builder.setLength(0);
                width = 0;
            }
            builder.append(ch);
            width += charWidth;
        }
        // 收集最后一个未满宽度上限的尾片段。
        if (builder.length() > 0) {
            pieces.add(new Token(TokenType.WORD, builder.toString(), token.style, width));
        }
        return pieces;
    }

    /**
     * 将一行 token 重新组装为 {@link TextLine}。
     *
     * @param tokens 当前行的 token
     * @param width  当前行总宽度
     * @return 文本行对象
     */
    private TextLine buildLine(List<Token> tokens, int width) {
        if (tokens.isEmpty()) {
            return TextLine.empty();
        }
        List<TextLine.Segment> segments = new ArrayList<>(tokens.size());
        StringBuilder builder = new StringBuilder();
        for (Token token : tokens) {
            builder.append(token.text);
            // 相邻 token 如果样式完全一致，则在这里合并回一个 run，
            // 避免后续绘制阶段出现过度切片，降低遍历成本。
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
     * 判断两个解析后样式是否可视为同一绘制样式。
     *
     * @param left  左侧样式
     * @param right 右侧样式
     * @return true 表示可以合并
     */
    private boolean hasSameStyle(ResolvedTextStyle left, ResolvedTextStyle right) {
        return left.getFont().equals(right.getFont())
                && left.getColor().equals(right.getColor())
                && left.isUnderline() == right.isUnderline()
                && left.isStrikeThrough() == right.isStrikeThrough();
    }

    /**
     * 根据对齐方式为每一行计算横向偏移量。
     *
     * @param lines       原始文本行
     * @param layoutWidth 布局总宽度
     * @param align       对齐方式
     * @return 带偏移量的文本行
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

    private boolean canMergeSegment(TextLine.Segment previous, Token token) {
        return hasSameStyle(previous.getStyle(), token.style)
                && previous.isStretchableSpace() == (token.type == TokenType.SPACE);
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
     * 计算所有行中的最大宽度。
     *
     * @param lines 文本行集合
     * @return 最大行宽
     */
    private int resolveMaxLineWidth(List<TextLine> lines) {
        int maxWidth = 0;
        for (TextLine line : lines) {
            maxWidth = Math.max(maxWidth, line.getWidth());
        }
        return maxWidth;
    }

    /**
     * 根据定位策略解析文本块左上角坐标。
     *
     * @param position     定位策略
     * @param posterWidth  海报宽度
     * @param posterHeight 海报高度
     * @param width        文本布局宽度
     * @param height       文本布局高度
     * @return 最终绘制原点
     */
    private Point resolvePoint(Position position, int posterWidth, int posterHeight, int width, int height) {
        if (position == null) {
            return Point.ORIGIN_COORDINATE;
        }
        return position.calculate(posterWidth, posterHeight, width, height);
    }

    /**
     * 计算统一行高。
     *
     * @param graphics   图形上下文
     * @param runs       文本运行单元
     * @param baseFont   基础字体
     * @param blockStyle 文本块样式
     * @return 最大行高
     */
    private int resolveLineHeight(Graphics2D graphics, List<ResolvedTextRun> runs, Font baseFont,
                                  TextBlockStyle blockStyle) {
        // 块级样式显式指定行高时，直接采用该值作为统一行距。
        // 这样调用方可以主动控制多行文本的疏密，而不是被字体度量结果绑定。
        if (blockStyle.getLineHeight() != null) {
            return blockStyle.getLineHeight();
        }
        int maxHeight = graphics.getFontMetrics(baseFont).getHeight();
        for (ResolvedTextRun run : runs) {
            maxHeight = Math.max(maxHeight, graphics.getFontMetrics(run.getStyle().getFont()).getHeight());
        }
        return maxHeight;
    }

    /**
     * 计算统一基线偏移。
     *
     * @param graphics 图形上下文
     * @param runs     文本运行单元
     * @param baseFont 基础字体
     * @return 最大 ascent
     */
    private int resolveBaselineOffset(Graphics2D graphics, List<ResolvedTextRun> runs, Font baseFont) {
        int maxAscent = graphics.getFontMetrics(baseFont).getAscent();
        for (ResolvedTextRun run : runs) {
            maxAscent = Math.max(maxAscent, graphics.getFontMetrics(run.getStyle().getFont()).getAscent());
        }
        return maxAscent;
    }

    /**
     * 测量指定字体下文本的像素宽度。
     *
     * @param graphics 图形上下文
     * @param text     目标文本
     * @param font     测量字体
     * @return 像素宽度
     */
    private int measureText(Graphics2D graphics, String text, Font font) {
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.stringWidth(text);
    }

    /**
     * 解析文本块默认字体。
     *
     * @param blockStyle   文本块样式
     * @param globalConfig 全局配置
     * @return 默认字体
     */
    private Font resolveBaseFont(TextBlockStyle blockStyle, Config globalConfig) {
        // 块级样式直接给出 Font 实例时优先级最高，避免再次从名称、字号、样式重建。
        if (blockStyle.getFont() != null) {
            return blockStyle.getFont();
        }
        String fontName = blockStyle.getFontName() != null ? blockStyle.getFontName() : globalConfig.getFontName();
        int fontStyle = blockStyle.getFontStyle() != null ? blockStyle.getFontStyle() : Font.PLAIN;
        int fontSize = blockStyle.getFontSize() != null ? blockStyle.getFontSize() : globalConfig.getFontSize();
        return new Font(fontName, fontStyle, fontSize);
    }

    /**
     * 解析文本块默认颜色。
     *
     * @param blockStyle   文本块样式
     * @param globalConfig 全局配置
     * @return 默认颜色
     */
    private Color resolveDefaultColor(TextBlockStyle blockStyle, Config globalConfig) {
        // 块级配置优先，其次回落到全局配置，最后兜底黑色，保证渲染阶段一定有可用颜色。
        if (blockStyle.getColor() != null) {
            return blockStyle.getColor();
        }
        if (globalConfig != null && globalConfig.getColor() != null) {
            return globalConfig.getColor();
        }
        return Color.BLACK;
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
