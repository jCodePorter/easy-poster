package com.bytefuture.easy.poster.element.v2;

import com.bytefuture.easy.poster.element.v2.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLine;
import com.bytefuture.easy.poster.element.v2.text.resolve.ResolvedTextRun;
import com.bytefuture.easy.poster.element.v2.text.resolve.TextStyleResolver;
import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextStyle;
import com.bytefuture.easy.poster.element.v2.text.style.TextBlockStyle;
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
public final class TextLayoutEngine {
    /**
     * 文本样式解析器。
     */
    private final TextStyleResolver styleResolver = new TextStyleResolver();

    /**
     * 计算文本布局结果。
     *
     * @param config 文本配置
     * @param position 元素位置
     * @param rotate 旋转角度
     * @param context 海报上下文
     * @param posterWidth 海报宽度
     * @param posterHeight 海报高度
     * @return 布局结果
     */
    public TextLayoutResult layout(TextElementConfig config, Position position, int rotate,
                                   PosterContext context, int posterWidth, int posterHeight) {
        if (config.isEmpty()) {
            return TextLayoutResult.empty(position);
        }

        Graphics2D graphics = context.getGraphics();
        Font baseFont = resolveBaseFont(config, context.getConfig());
        Color defaultColor = resolveDefaultColor(config, context.getConfig());
        TextBlockStyle blockStyle = config.toBlockStyle();
        List<ResolvedTextRun> runs = resolveRuns(config.toRichSpans(), blockStyle, baseFont, defaultColor);
        int lineHeight = resolveLineHeight(graphics, runs, baseFont);
        int baselineOffset = resolveBaselineOffset(graphics, runs, baseFont);
        int widthLimit = config.isAutoWordWrap() ? Math.max(0, config.getMaxTextWidth()) : 0;
        List<TextLine> lines = wrapRuns(graphics, runs, widthLimit);
        // 指定布局宽度时优先使用固定宽度，以保证多行对齐基准一致。
        int layoutWidth = widthLimit > 0 ? widthLimit : resolveMaxLineWidth(lines);
        List<TextLine> alignedLines = alignLines(lines, layoutWidth, config.getTextAlign());
        int totalHeight = alignedLines.size() * lineHeight;
        Point point = resolvePoint(position, posterWidth, posterHeight, layoutWidth, totalHeight);
        // 绝对定位时使用基线策略修正 Y 轴，其他定位模式沿用布局测得的基线偏移。
        int drawOffsetY = position instanceof AbsolutePosition ? config.getBaseLine().getOffset(
                graphics.getFontMetrics(baseFont), lineHeight) : baselineOffset;
        return new TextLayoutResult(point, layoutWidth, totalHeight, lineHeight, baselineOffset, drawOffsetY, alignedLines);
    }

    /**
     * 将富文本片段解析为带最终样式的运行段。
     *
     * @param spans 文本片段列表
     * @param blockStyle 块级默认样式
     * @param baseFont 基础字体
     * @param defaultColor 默认颜色
     * @return 解析后的文本运行段
     */
    private List<ResolvedTextRun> resolveRuns(List<TextSpan> spans, TextBlockStyle blockStyle,
                                              Font baseFont, Color defaultColor) {
        if (spans.isEmpty()) {
            return Collections.emptyList();
        }
        List<ResolvedTextRun> runs = new ArrayList<ResolvedTextRun>(spans.size());
        for (TextSpan span : spans) {
            runs.add(styleResolver.resolve(span, blockStyle, baseFont, defaultColor));
        }
        return runs;
    }

    /**
     * 按宽度限制对运行段执行换行。
     *
     * @param graphics 图形上下文
     * @param runs 文本运行段
     * @param widthLimit 宽度限制，非正数表示不限制
     * @return 分行后的文本行列表
     */
    private List<TextLine> wrapRuns(Graphics2D graphics, List<ResolvedTextRun> runs, int widthLimit) {
        List<Token> tokens = tokenizeRuns(runs, graphics);
        if (tokens.isEmpty()) {
            return Collections.singletonList(TextLine.empty());
        }

        List<TextLine> lines = new ArrayList<TextLine>();
        List<Token> current = new ArrayList<Token>();
        int currentWidth = 0;

        for (Token token : tokens) {
            if (token.type == TokenType.NEWLINE) {
                // 显式换行符立即结束当前行，并开启新行缓冲。
                lines.add(buildLine(current, currentWidth));
                current = new ArrayList<Token>();
                currentWidth = 0;
                continue;
            }

            if (widthLimit <= 0) {
                // 未设置换行宽度时，保留原始 token 顺序输出。
                current.add(token);
                currentWidth += token.width;
                continue;
            }

            if (token.type == TokenType.SPACE && current.isEmpty()) {
                // 新行行首空白直接丢弃，避免视觉上的无效缩进。
                continue;
            }

            if (token.width > widthLimit) {
                if (!current.isEmpty()) {
                    lines.add(buildLine(current, currentWidth));
                    current = new ArrayList<Token>();
                    currentWidth = 0;
                }
                // 超宽 token 退化为逐字符切分，保证任意内容都能落入限制宽度内。
                List<Token> pieces = splitOversizedToken(token, widthLimit, graphics);
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

            if (currentWidth + token.width <= widthLimit || current.isEmpty()) {
                current.add(token);
                currentWidth += token.width;
                continue;
            }

            if (token.type == TokenType.SPACE) {
                // 空白刚好触发溢出时直接换行，避免把空白带到下一行开头。
                lines.add(buildLine(current, currentWidth));
                current = new ArrayList<Token>();
                currentWidth = 0;
                continue;
            }

            // 普通内容溢出时保留 token 完整性，将其移动到下一行。
            lines.add(buildLine(current, currentWidth));
            current = new ArrayList<Token>();
            current.add(token);
            currentWidth = token.width;
        }

        if (!current.isEmpty() || lines.isEmpty()) {
            lines.add(buildLine(current, currentWidth));
        }
        return lines;
    }

    /**
     * 将运行段拆分为可换行 token。
     *
     * @param runs 文本运行段
     * @param graphics 图形上下文
     * @return token 列表
     */
    private List<Token> tokenizeRuns(List<ResolvedTextRun> runs, Graphics2D graphics) {
        List<Token> tokens = new ArrayList<Token>();
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

                if ("\r".equals(ch)) {
                    continue;
                }
                if ("\n".equals(ch)) {
                    flushBufferedToken(tokens, buffer, currentType, run, graphics);
                    currentType = null;
                    tokens.add(new Token(TokenType.NEWLINE, "\n", run.getStyle(), 0));
                    continue;
                }

                TokenType tokenType = Character.isWhitespace(codePoint) ? TokenType.SPACE : TokenType.WORD;
                if (currentType != null && currentType != tokenType) {
                    flushBufferedToken(tokens, buffer, currentType, run, graphics);
                }
                currentType = tokenType;
                buffer.append(ch);
            }
            flushBufferedToken(tokens, buffer, currentType, run, graphics);
        }
        return tokens;
    }

    /**
     * 将缓冲区中的连续字符写入 token 列表。
     *
     * @param tokens token 列表
     * @param buffer 文本缓冲区
     * @param type token 类型
     * @param run 文本运行段
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
     * 按字符级别切分超宽 token。
     *
     * @param token 超宽 token
     * @param widthLimit 宽度限制
     * @param graphics 图形上下文
     * @return 切分后的 token 列表
     */
    private List<Token> splitOversizedToken(Token token, int widthLimit, Graphics2D graphics) {
        List<Token> pieces = new ArrayList<Token>();
        StringBuilder builder = new StringBuilder();
        int width = 0;
        for (int i = 0; i < token.text.length(); ) {
            int codePoint = token.text.codePointAt(i);
            String ch = new String(Character.toChars(codePoint));
            i += Character.charCount(codePoint);
            int charWidth = measureText(graphics, ch, token.style.getFont());
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
     * 将 token 列表组装为单行文本。
     *
     * @param tokens 当前行 token
     * @param width 当前行宽度
     * @return 文本行
     */
    private TextLine buildLine(List<Token> tokens, int width) {
        if (tokens.isEmpty()) {
            return TextLine.empty();
        }
        List<ResolvedTextRun> runs = new ArrayList<ResolvedTextRun>(tokens.size());
        StringBuilder builder = new StringBuilder();
        for (Token token : tokens) {
            builder.append(token.text);
            if (!runs.isEmpty() && hasSameStyle(runs.get(runs.size() - 1).getStyle(), token.style)) {
                ResolvedTextRun previous = runs.remove(runs.size() - 1);
                runs.add(new ResolvedTextRun(previous.getText() + token.text, previous.getStyle()));
            } else {
                runs.add(new ResolvedTextRun(token.text, token.style));
            }
        }
        return new TextLine(builder.toString(), width, 0, runs);
    }

    /**
     * 判断两个样式是否可以合并。
     *
     * @param left 左侧样式
     * @param right 右侧样式
     * @return 样式一致时返回 {@code true}
     */
    private boolean hasSameStyle(ResolvedTextStyle left, ResolvedTextStyle right) {
        return left.getFont().equals(right.getFont()) && left.getColor().equals(right.getColor());
    }

    /**
     * 按对齐方式计算每行的 X 偏移。
     *
     * @param lines 文本行列表
     * @param layoutWidth 布局宽度
     * @param align 对齐方式
     * @return 应用偏移后的文本行
     */
    private List<TextLine> alignLines(List<TextLine> lines, int layoutWidth, TextAlign align) {
        List<TextLine> aligned = new ArrayList<TextLine>(lines.size());
        for (TextLine line : lines) {
            aligned.add(line.withOffsetX(align.offset(layoutWidth, line.getWidth())));
        }
        return aligned;
    }

    /**
     * 计算所有行中的最大宽度。
     *
     * @param lines 文本行列表
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
     * 计算文本绘制起点。
     *
     * @param position 元素位置
     * @param posterWidth 海报宽度
     * @param posterHeight 海报高度
     * @param width 文本宽度
     * @param height 文本高度
     * @return 绘制起点
     */
    private Point resolvePoint(Position position, int posterWidth, int posterHeight, int width, int height) {
        if (position == null) {
            return Point.ORIGIN_COORDINATE;
        }
        return position.calculate(posterWidth, posterHeight, width, height);
    }

    /**
     * 计算布局采用的统一行高。
     *
     * @param graphics 图形上下文
     * @param runs 文本运行段
     * @param baseFont 基础字体
     * @return 行高
     */
    private int resolveLineHeight(Graphics2D graphics, List<ResolvedTextRun> runs, Font baseFont) {
        int maxHeight = graphics.getFontMetrics(baseFont).getHeight();
        for (ResolvedTextRun run : runs) {
            maxHeight = Math.max(maxHeight, graphics.getFontMetrics(run.getStyle().getFont()).getHeight());
        }
        return maxHeight;
    }

    /**
     * 计算行内基线偏移。
     *
     * @param graphics 图形上下文
     * @param runs 文本运行段
     * @param baseFont 基础字体
     * @return 基线偏移
     */
    private int resolveBaselineOffset(Graphics2D graphics, List<ResolvedTextRun> runs, Font baseFont) {
        int maxAscent = graphics.getFontMetrics(baseFont).getAscent();
        for (ResolvedTextRun run : runs) {
            maxAscent = Math.max(maxAscent, graphics.getFontMetrics(run.getStyle().getFont()).getAscent());
        }
        return maxAscent;
    }

    /**
     * 测量指定字体下的文本宽度。
     *
     * @param graphics 图形上下文
     * @param text 文本内容
     * @param font 字体
     * @return 文本宽度
     */
    private int measureText(Graphics2D graphics, String text, Font font) {
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.stringWidth(text);
    }

    /**
     * 解析块级基础字体。
     *
     * @param config 文本配置
     * @param globalConfig 全局配置
     * @return 基础字体
     */
    private Font resolveBaseFont(TextElementConfig config, Config globalConfig) {
        if (config.getFont() != null) {
            return config.getFont();
        }
        String fontName = config.getFontName() != null ? config.getFontName() : globalConfig.getFontName();
        return new Font(fontName, config.getFontStyle(), config.getFontSize());
    }

    /**
     * 解析默认颜色，优先级依次为配置色、全局色、系统默认色。
     *
     * @param config 文本配置
     * @param globalConfig 全局配置
     * @return 默认颜色
     */
    private Color resolveDefaultColor(TextElementConfig config, Config globalConfig) {
        // 第一优先级：TextElementConfig 中配置的默认颜色
        if (config.getColor() != null) {
            return config.getColor();
        }
        // 第二优先级：全局配置中的颜色
        if (globalConfig != null && globalConfig.getColor() != null) {
            return globalConfig.getColor();
        }
        // 第三优先级：系统默认黑色
        return Color.BLACK;
    }

    /** 文本 token 类型。 */
    private enum TokenType {
        WORD,
        SPACE,
        NEWLINE
    }

    /**
     * 布局阶段使用的文本 token。
     */
    private static final class Token {
        /** token 类型。 */
        private final TokenType type;
        /** token 文本。 */
        private final String text;
        /** token 解析后样式。 */
        private final ResolvedTextStyle style;
        /** token 宽度。 */
        private final int width;

        private Token(TokenType type, String text, ResolvedTextStyle style, int width) {
            this.type = type;
            this.text = text;
            this.style = style;
            this.width = width;
        }
    }
}
