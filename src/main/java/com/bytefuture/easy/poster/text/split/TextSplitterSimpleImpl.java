package com.bytefuture.easy.poster.text.split;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简单文本拆分器实现。
 * 通过分词、标点规避和必要时的逐字切分完成自动换行。
 */
public class TextSplitterSimpleImpl implements ITextSplitter {
    /** ASCII 字符宽度缓存，减少重复调用 FontMetrics。 */
    private static final Map<Character, Integer> charSizeMap = new HashMap<>();

    @Override
    public TextSplitResult split(TextSplitRequest request) {
        List<SplitTextInfo> lines = new ArrayList<>();
        if (request == null || request.getFontMetrics() == null) {
            return TextSplitResult.of(lines);
        }

        String text = request.getText();
        if (text == null || text.isEmpty()) {
            return TextSplitResult.of(lines);
        }

        int maxWidth = request.getMaxWidth();
        FontMetrics fontMetrics = request.getFontMetrics();
        if (maxWidth <= 0) {
            // 无宽度限制时直接整段返回。
            lines.add(SplitTextInfo.of(text, fontMetrics.stringWidth(text)));
            return TextSplitResult.of(lines);
        }

        List<Token> tokens = tokenize(text, fontMetrics);
        LineBuffer lineBuffer = new LineBuffer();

        for (Token token : tokens) {
            if (token.type == TokenType.NEW_LINE) {
                // 显式换行直接结束当前行。
                flushCurrentLine(lines, lineBuffer, request, true);
                continue;
            }

            if (request.isTrimLeadingWhitespace() && lineBuffer.isEmpty() && token.type == TokenType.WHITESPACE) {
                // 行首空白按配置忽略，避免新行保留多余空格。
                continue;
            }

            if (lineBuffer.canAppend(token, maxWidth)) {
                lineBuffer.append(token);
                continue;
            }

            if (shouldForceAppendToCurrentLine(token, lineBuffer)) {
                // 收尾标点尽量跟随前一段，避免新行以标点开头。
                lineBuffer.append(token);
                flushCurrentLine(lines, lineBuffer, request, false);
                continue;
            }

            if (!lineBuffer.isEmpty()) {
                flushCurrentLine(lines, lineBuffer, request, false);
                if (request.isTrimLeadingWhitespace() && token.type == TokenType.WHITESPACE) {
                    continue;
                }
            }

            if (token.width <= maxWidth) {
                lineBuffer.append(token);
                continue;
            }

            // 单个 token 超宽时，退化为逐字切分。
            List<SplitTextInfo> overflowLines = splitOversizedToken(token, maxWidth, fontMetrics);
            for (SplitTextInfo info : overflowLines) {
                lines.add(info);
            }
        }

        flushCurrentLine(lines, lineBuffer, request, false);
        return TextSplitResult.of(lines);
    }

    private static void flushCurrentLine(List<SplitTextInfo> lines, LineBuffer lineBuffer,
                                         TextSplitRequest request, boolean explicitNewLine) {
        if (lineBuffer.isEmpty()) {
            if (explicitNewLine && request.isPreserveEmptyLine()) {
                // 连续换行时按配置保留空行。
                lines.add(SplitTextInfo.of("", 0));
            }
            return;
        }

        String lineText = lineBuffer.buildText(request.isTrimTrailingWhitespace());
        if (lineText.isEmpty() && !explicitNewLine && !request.isPreserveEmptyLine()) {
            lineBuffer.clear();
            return;
        }

        lines.add(SplitTextInfo.of(lineText, getTextWidth(lineText, request.getFontMetrics())));
        lineBuffer.clear();
    }

    private static boolean shouldForceAppendToCurrentLine(Token token, LineBuffer lineBuffer) {
        return !lineBuffer.isEmpty() && token.type == TokenType.CLOSING_PUNCTUATION;
    }

    private static List<Token> tokenize(String text, FontMetrics fontMetrics) {
        List<Token> tokens = new ArrayList<Token>();
        String normalized = text.replace("\r\n", "\n").replace('\r', '\n');

        int index = 0;
        while (index < normalized.length()) {
            char current = normalized.charAt(index);
            if (current == '\n') {
                tokens.add(Token.newLine());
                index++;
                continue;
            }

            if (Character.isWhitespace(current)) {
                // 连续空白合并成一个 token，便于整体处理。
                int start = index;
                index++;
                while (index < normalized.length()) {
                    char next = normalized.charAt(index);
                    if (next == '\n' || !Character.isWhitespace(next)) {
                        break;
                    }
                    index++;
                }
                tokens.add(Token.whitespace(normalized.substring(start, index), fontMetrics));
                continue;
            }

            if (isAsciiWordLikeChar(current)) {
                // 英文单词连续合并，优先按词换行而不是逐字断开。
                int start = index;
                index++;
                while (index < normalized.length() && isAsciiWordLikeChar(normalized.charAt(index))) {
                    index++;
                }
                tokens.add(Token.word(normalized.substring(start, index), fontMetrics));
                continue;
            }

            if (isClosingPunctuation(current)) {
                tokens.add(Token.closingPunctuation(String.valueOf(current), fontMetrics));
                index++;
                continue;
            }

            if (isOpeningPunctuation(current)) {
                tokens.add(Token.openingPunctuation(String.valueOf(current), fontMetrics));
                index++;
                continue;
            }

            tokens.add(Token.text(String.valueOf(current), fontMetrics));
            index++;
        }
        return tokens;
    }

    private static List<SplitTextInfo> splitOversizedToken(Token token, int width, FontMetrics fontMetrics) {
        List<SplitTextInfo> result = new ArrayList<SplitTextInfo>();
        if (token == null || token.text == null || token.text.isEmpty()) {
            return result;
        }

        int fontSize = fontMetrics.getFont().getSize();
        StringBuilder builder = new StringBuilder();
        int currentWidth = 0;
        for (int i = 0; i < token.text.length(); i++) {
            char current = token.text.charAt(i);
            int charWidth = getCharSize(current, fontSize, fontMetrics);
            if (builder.length() > 0 && currentWidth + charWidth > width) {
                result.add(SplitTextInfo.of(builder.toString(), currentWidth));
                builder.setLength(0);
                currentWidth = 0;
            }
            builder.append(current);
            currentWidth += charWidth;
        }

        if (builder.length() > 0) {
            result.add(SplitTextInfo.of(builder.toString(), currentWidth));
        }
        return result;
    }

    private static int getTextWidth(String text, FontMetrics fontMetrics) {
        return text == null || text.isEmpty() ? 0 : fontMetrics.stringWidth(text);
    }

    private static int getCharSize(char c, int defaultSize, FontMetrics fm) {
        if (isFullWidthChar(c)) {
            return defaultSize;
        }
        return charSizeMap.computeIfAbsent(c, k -> fm.charWidth(c));
    }

    private static boolean isAsciiWordLikeChar(char c) {
        return c < 128 && !Character.isWhitespace(c) && !isAsciiBreakPunctuation(c);
    }

    private static boolean isAsciiBreakPunctuation(char c) {
        return ",;!()[]{}<>\"".indexOf(c) >= 0;
    }

    private static boolean isOpeningPunctuation(char c) {
        return "([{<\"'“‘《「『【".indexOf(c) >= 0;
    }

    private static boolean isClosingPunctuation(char c) {
        return ")]}>\"'”’》」』】,，。.!?！？;；:：".indexOf(c) >= 0;
    }

    /**
     * 判断字符是否为常见全角字符。
     * 全角字符在简单测量策略下按字号近似处理。
     */
    private static boolean isFullWidthChar(char c) {
        return (c >= '\u4E00' && c <= '\u9FFF')
                || (c >= '\u3040' && c <= '\u30FF')
                || (c >= '\uFF01' && c <= '\uFF5E');
    }

    private enum TokenType {
        TEXT,
        WORD,
        WHITESPACE,
        NEW_LINE,
        OPENING_PUNCTUATION,
        CLOSING_PUNCTUATION
    }

    private static class Token {
        /** token 原始文本。 */
        private final String text;
        /** token 预估宽度。 */
        private final int width;
        /** token 类型。 */
        private final TokenType type;

        private Token(String text, int width, TokenType type) {
            this.text = text;
            this.width = width;
            this.type = type;
        }

        private static Token text(String text, int width) {
            return new Token(text, width, TokenType.TEXT);
        }

        private static Token text(String text, FontMetrics fontMetrics) {
            return new Token(text, getTextWidth(text, fontMetrics), TokenType.TEXT);
        }

        private static Token word(String text, FontMetrics fontMetrics) {
            return new Token(text, getTextWidth(text, fontMetrics), TokenType.WORD);
        }

        private static Token whitespace(String text, FontMetrics fontMetrics) {
            return new Token(text, getTextWidth(text, fontMetrics), TokenType.WHITESPACE);
        }

        private static Token newLine() {
            return new Token("\n", 0, TokenType.NEW_LINE);
        }

        private static Token openingPunctuation(String text, FontMetrics fontMetrics) {
            return new Token(text, getTextWidth(text, fontMetrics), TokenType.OPENING_PUNCTUATION);
        }

        private static Token closingPunctuation(String text, FontMetrics fontMetrics) {
            return new Token(text, getTextWidth(text, fontMetrics), TokenType.CLOSING_PUNCTUATION);
        }
    }

    private static class LineBuffer {
        /** 当前行暂存的 token。 */
        private final List<Token> tokens = new ArrayList<Token>();
        /** 当前行累计宽度。 */
        private int width = 0;

        private boolean isEmpty() {
            return this.tokens.isEmpty();
        }

        private boolean canAppend(Token token, int maxWidth) {
            return token.width + this.width <= maxWidth;
        }

        private void append(Token token) {
            this.tokens.add(token);
            this.width += token.width;
        }

        private void clear() {
            this.tokens.clear();
            this.width = 0;
        }

        private String buildText(boolean trimTrailingWhitespace) {
            int end = this.tokens.size();
            if (trimTrailingWhitespace) {
                while (end > 0 && this.tokens.get(end - 1).type == TokenType.WHITESPACE) {
                    end--;
                }
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < end; i++) {
                builder.append(this.tokens.get(i).text);
            }
            return builder.toString();
        }
    }
}
