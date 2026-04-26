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
    private final TextStyleResolver styleResolver = new TextStyleResolver();

    /**
     * 计算文本布局结果。
     *
     * @param element 文本元素
     * @param position 元素位置
     * @param rotate 旋转角度
     * @param context 海报上下文
     * @param posterWidth 海报宽度
     * @param posterHeight 海报高度
     * @return 布局结果
     */
    public TextLayoutResult layout(TextElement element, Position position, int rotate,
                                   PosterContext context, int posterWidth, int posterHeight) {
        if (element.isEmpty()) {
            return TextLayoutResult.empty(position);
        }

        Graphics2D graphics = context.getGraphics();
        TextBlockStyle blockStyle = element.getBlockStyle();
        Font baseFont = resolveBaseFont(blockStyle, context.getConfig());
        Color defaultColor = resolveDefaultColor(blockStyle, context.getConfig());
        List<ResolvedTextRun> runs = resolveRuns(element.getTextSpans(), blockStyle, baseFont, defaultColor);
        int lineHeight = resolveLineHeight(graphics, runs, baseFont);
        int baselineOffset = resolveBaselineOffset(graphics, runs, baseFont);
        int widthLimit = blockStyle.isAutoWordWrap() ? Math.max(0, blockStyle.getMaxTextWidth()) : 0;
        List<TextLine> lines = wrapRuns(graphics, runs, widthLimit);
        int layoutWidth = widthLimit > 0 ? widthLimit : resolveMaxLineWidth(lines);
        List<TextLine> alignedLines = alignLines(lines, layoutWidth, blockStyle.getTextAlign());
        int totalHeight = alignedLines.size() * lineHeight;
        Point point = resolvePoint(position, posterWidth, posterHeight, layoutWidth, totalHeight);
        int drawOffsetY = position instanceof AbsolutePosition
                ? blockStyle.getBaseLine().getOffset(graphics.getFontMetrics(baseFont), lineHeight)
                : baselineOffset;
        return new TextLayoutResult(point, layoutWidth, totalHeight, lineHeight, baselineOffset, drawOffsetY, alignedLines);
    }

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
                lines.add(buildLine(current, currentWidth));
                current = new ArrayList<Token>();
                currentWidth = 0;
                continue;
            }

            if (widthLimit <= 0) {
                current.add(token);
                currentWidth += token.width;
                continue;
            }

            if (token.type == TokenType.SPACE && current.isEmpty()) {
                continue;
            }

            if (token.width > widthLimit) {
                if (!current.isEmpty()) {
                    lines.add(buildLine(current, currentWidth));
                    current = new ArrayList<Token>();
                    currentWidth = 0;
                }
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
                lines.add(buildLine(current, currentWidth));
                current = new ArrayList<Token>();
                currentWidth = 0;
                continue;
            }

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

    private boolean hasSameStyle(ResolvedTextStyle left, ResolvedTextStyle right) {
        return left.getFont().equals(right.getFont()) && left.getColor().equals(right.getColor());
    }

    private List<TextLine> alignLines(List<TextLine> lines, int layoutWidth, TextAlign align) {
        List<TextLine> aligned = new ArrayList<TextLine>(lines.size());
        for (TextLine line : lines) {
            aligned.add(line.withOffsetX(align.offset(layoutWidth, line.getWidth())));
        }
        return aligned;
    }

    private int resolveMaxLineWidth(List<TextLine> lines) {
        int maxWidth = 0;
        for (TextLine line : lines) {
            maxWidth = Math.max(maxWidth, line.getWidth());
        }
        return maxWidth;
    }

    private Point resolvePoint(Position position, int posterWidth, int posterHeight, int width, int height) {
        if (position == null) {
            return Point.ORIGIN_COORDINATE;
        }
        return position.calculate(posterWidth, posterHeight, width, height);
    }

    private int resolveLineHeight(Graphics2D graphics, List<ResolvedTextRun> runs, Font baseFont) {
        int maxHeight = graphics.getFontMetrics(baseFont).getHeight();
        for (ResolvedTextRun run : runs) {
            maxHeight = Math.max(maxHeight, graphics.getFontMetrics(run.getStyle().getFont()).getHeight());
        }
        return maxHeight;
    }

    private int resolveBaselineOffset(Graphics2D graphics, List<ResolvedTextRun> runs, Font baseFont) {
        int maxAscent = graphics.getFontMetrics(baseFont).getAscent();
        for (ResolvedTextRun run : runs) {
            maxAscent = Math.max(maxAscent, graphics.getFontMetrics(run.getStyle().getFont()).getAscent());
        }
        return maxAscent;
    }

    private int measureText(Graphics2D graphics, String text, Font font) {
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.stringWidth(text);
    }

    private Font resolveBaseFont(TextBlockStyle blockStyle, Config globalConfig) {
        if (blockStyle.getFont() != null) {
            return blockStyle.getFont();
        }
        String fontName = blockStyle.getFontName() != null ? blockStyle.getFontName() : globalConfig.getFontName();
        int fontStyle = blockStyle.getFontStyle() != null ? blockStyle.getFontStyle().intValue() : Font.PLAIN;
        int fontSize = blockStyle.getFontSize() != null ? blockStyle.getFontSize().intValue() : globalConfig.getFontSize();
        return new Font(fontName, fontStyle, fontSize);
    }

    private Color resolveDefaultColor(TextBlockStyle blockStyle, Config globalConfig) {
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
