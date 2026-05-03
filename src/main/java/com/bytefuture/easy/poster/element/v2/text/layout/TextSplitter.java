package com.bytefuture.easy.poster.element.v2.text.layout;

import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextSpan;
import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextStyle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文本拆分，将输入的TextSpan逐行拆分
 *
 * @author biaoy
 * @since 2026/05/03
 */
public class TextSplitter {

    private static final TextMeasurer textMeasurer = new TextMeasurer();

    /**
     * 根据宽度限制将文本运行单元切分为多行
     *
     * @param graphics   图形上下文
     * @param runs       已解析样式的文本运行单元
     * @param widthLimit 单行最大宽度；小于等于 0 表示不限制
     * @return 按顺序生成的文本行
     */
    public List<TextLine> splitLines(Graphics2D graphics, List<ResolvedTextSpan> runs, int widthLimit) {
        List<Token> tokens = tokenizeRuns(runs, graphics);
        if (tokens.isEmpty()) {
            return Collections.singletonList(TextLine.empty());
        }

        List<TextLine> lines = new ArrayList<>();
        List<Token> current = new ArrayList<>();
        int currentWidth = 0;

        for (Token token : tokens) {
            // 显式换行符优先级最高
            if (token.type == TokenType.NEWLINE) {
                List<Token> trimmed = trimTrailingSpaces(current);
                lines.add(buildLine(trimmed, calculateTokensWidth(trimmed)));
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
                    List<Token> trimmed = trimTrailingSpaces(current);
                    lines.add(buildLine(trimmed, calculateTokensWidth(trimmed)));
                    current = new ArrayList<>();
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

            // 当前 token 加入后仍未超宽，或者当前行还是空行时，直接吸收
            if (currentWidth + token.width <= widthLimit || current.isEmpty()) {
                current.add(token);
                currentWidth += token.width;
                continue;
            }

            // 超宽的是空格时，直接在空格处分行
            if (token.type == TokenType.SPACE) {
                List<Token> trimmed = trimTrailingSpaces(current);
                lines.add(buildLine(trimmed, calculateTokensWidth(trimmed)));
                current = new ArrayList<>();
                currentWidth = 0;
                continue;
            }

            // 普通单词导致超宽：先提交当前行，再把该单词作为下一行的起点
            List<Token> trimmed = trimTrailingSpaces(current);
            lines.add(buildLine(trimmed, calculateTokensWidth(trimmed)));
            current = new ArrayList<>();
            current.add(token);
            currentWidth = token.width;
        }

        // 循环结束后仍然可能有尚未提交的尾行
        if (!current.isEmpty() || lines.isEmpty()) {
            List<Token> trimmed = trimTrailingSpaces(current);
            lines.add(buildLine(trimmed, calculateTokensWidth(trimmed)));
        }
        return lines;
    }

    /**
     * 将运行单元拆分为可参与换行判断的 token
     *
     * @param runs     文本运行单元
     * @param graphics 图形上下文
     * @return token 列表
     */
    private List<Token> tokenizeRuns(List<ResolvedTextSpan> runs, Graphics2D graphics) {
        List<Token> tokens = new ArrayList<>();
        for (ResolvedTextSpan run : runs) {
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
                    flushBufferedToken(tokens, buffer, currentType, run, graphics);
                    currentType = null;
                    tokens.add(new Token(TokenType.NEWLINE, "\n", run.getStyle(), 0, run.getStyle().getLetterSpacing()));
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
     * 将缓冲中的连续字符输出为一个 token
     */
    private void flushBufferedToken(List<Token> tokens, StringBuilder buffer, TokenType type,
                                    ResolvedTextSpan run, Graphics2D graphics) {
        if (type == null || buffer.length() == 0) {
            return;
        }
        String text = buffer.toString();
        int letterSpacing = run.getStyle().getLetterSpacing();
        int width = textMeasurer.measureWidthWithSpacing(graphics, text, run.getStyle().getFont(), letterSpacing);
        tokens.add(new Token(type, text, run.getStyle(), width, letterSpacing));
        buffer.setLength(0);
    }

    /**
     * 将单个超宽 token 按字符级别拆分为多个可容纳片段
     */
    private List<Token> splitOversizedToken(Token token, int widthLimit, Graphics2D graphics) {
        List<Token> pieces = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int width = 0;
        int letterSpacing = token.letterSpacing;
        for (int i = 0; i < token.text.length(); ) {
            int codePoint = token.text.codePointAt(i);
            String ch = new String(Character.toChars(codePoint));
            i += Character.charCount(codePoint);
            int charWidth = textMeasurer.measureWidthWithSpacing(graphics, ch, token.style.getFont(), letterSpacing);
            if (builder.length() > 0 && width + charWidth > widthLimit) {
                pieces.add(new Token(TokenType.WORD, builder.toString(), token.style, width, letterSpacing));
                builder.setLength(0);
                width = 0;
            }
            builder.append(ch);
            width += charWidth;
        }
        if (builder.length() > 0) {
            pieces.add(new Token(TokenType.WORD, builder.toString(), token.style, width, letterSpacing));
        }
        return pieces;
    }

    /**
     * 去除行尾空白 token
     *
     * @param tokens token 列表
     * @return 去除末尾空白后的 token 列表
     */
    private List<Token> trimTrailingSpaces(List<Token> tokens) {
        List<Token> result = new ArrayList<>(tokens);
        while (!result.isEmpty() && result.get(result.size() - 1).type == TokenType.SPACE) {
            result.remove(result.size() - 1);
        }
        return result;
    }

    /**
     * 计算 token 列表的总宽度
     *
     * @param tokens token 列表
     * @return 总宽度
     */
    private int calculateTokensWidth(List<Token> tokens) {
        int width = 0;
        for (Token token : tokens) {
            width += token.width;
        }
        return width;
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
                        0, previous.getWidth() + token.width, previous.isStretchableSpace(), token.letterSpacing));
            } else {
                segments.add(new TextLine.Segment(token.text, token.style, 0, token.width, stretchableSpace, token.letterSpacing));
            }
        }
        return new TextLine(builder.toString(), width, 0, segments);
    }

    private boolean canMergeSegment(TextLine.Segment previous, Token token) {
        return hasSameStyle(previous.getStyle(), token.style)
                && previous.isStretchableSpace() == (token.type == TokenType.SPACE);
    }

    /**
     * 判断两个解析后样式是否可视为同一绘制样式
     */
    private boolean hasSameStyle(ResolvedTextStyle left, ResolvedTextStyle right) {
        return left.getFont().equals(right.getFont())
                && left.getColor().equals(right.getColor())
                && left.isUnderline() == right.isUnderline()
                && left.isStrikeThrough() == right.isStrikeThrough()
                && left.getLetterSpacing() == right.getLetterSpacing();
    }

    public static final class Token {
        private final TokenType type;
        private final String text;
        private final ResolvedTextStyle style;
        private final int width;
        private final int letterSpacing;

        private Token(TokenType type, String text, ResolvedTextStyle style, int width, int letterSpacing) {
            this.type = type;
            this.text = text;
            this.style = style;
            this.width = width;
            this.letterSpacing = letterSpacing;
        }
    }

    public enum TokenType {
        WORD,
        SPACE,
        NEWLINE
    }
}
