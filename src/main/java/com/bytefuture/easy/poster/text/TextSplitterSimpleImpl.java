package com.bytefuture.easy.poster.text;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文本拆分器简单实现，属于功能验证的实验版本
 *
 * @author biaoy
 * @since 2025/03/16
 */
public class TextSplitterSimpleImpl implements ITextSplitter {
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
            lines.add(SplitTextInfo.of(text, fontMetrics.stringWidth(text)));
            return TextSplitResult.of(lines);
        }

        List<Token> tokens = tokenize(text, fontMetrics);
        LineBuffer lineBuffer = new LineBuffer();

        for (Token token : tokens) {
            if (token.type == TokenType.NEW_LINE) {
                flushCurrentLine(lines, lineBuffer, request, true);
                continue;
            }

            if (request.isTrimLeadingWhitespace() && lineBuffer.isEmpty() && token.type == TokenType.WHITESPACE) {
                continue;
            }

            if (lineBuffer.canAppend(token, maxWidth)) {
                lineBuffer.append(token);
                continue;
            }

            if (shouldForceAppendToCurrentLine(token, lineBuffer)) {
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
        List<Token> tokens = new ArrayList<>();
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
        List<SplitTextInfo> result = new ArrayList<>();
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
        return "([{\"'“‘《「『【〈".indexOf(c) >= 0;
    }

    private static boolean isClosingPunctuation(char c) {
        return ")]}\"'”’》」』】〉，。！？；：、,.!?;:".indexOf(c) >= 0;
    }

    /**
     * 判断字符是否是全角字符
     */
    private static boolean isFullWidthChar(char c) {
        return (c >= '\u4E00' && c <= '\u9FFF') ||
                (c >= '\u3040' && c <= '\u30FF') ||
                (c >= '\uFF01' && c <= '\uFF5E');
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
        private final String text;
        private final int width;
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
        private final List<Token> tokens = new ArrayList<>();
        private int width = 0;

        private boolean isEmpty() {
            return tokens.isEmpty();
        }

        private boolean canAppend(Token token, int maxWidth) {
            return token.width + width <= maxWidth;
        }

        private void append(Token token) {
            tokens.add(token);
            width += token.width;
        }

        private void clear() {
            tokens.clear();
            width = 0;
        }

        private String buildText(boolean trimTrailingWhitespace) {
            int end = tokens.size();
            if (trimTrailingWhitespace) {
                while (end > 0 && tokens.get(end - 1).type == TokenType.WHITESPACE) {
                    end--;
                }
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < end; i++) {
                builder.append(tokens.get(i).text);
            }
            return builder.toString();
        }

    }
}
