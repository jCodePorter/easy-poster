package com.bytefuture.easy.poster.element.basic.text.layout;

import com.bytefuture.easy.poster.element.basic.text.style.ResolvedTextSpan;
import com.bytefuture.easy.poster.element.basic.text.style.ResolvedTextStyle;

import java.awt.*;
import java.util.*;
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
     * 字符基础宽度缓存（不含字间距），减少重复调用 FontMetrics
     */
    private static final int CHAR_WIDTH_CACHE_MAX_SIZE = 1024;

    private static final Map<String, Integer> charWidthCache = new LinkedHashMap<String, Integer>(CHAR_WIDTH_CACHE_MAX_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
            return size() > CHAR_WIDTH_CACHE_MAX_SIZE;
        }
    };

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
        List<Token> lastCommittedTokens = null;
        int lastCommittedWidth = 0;
        List<Token> current = new ArrayList<>();
        int currentWidth = 0;

        for (Token token : tokens) {
            // 显式换行符优先级最高
            if (token.type == TokenType.NEWLINE) {
                TrimResult trim = trimTrailingSpaces(current, currentWidth);
                lines.add(buildLine(trim.tokens, trim.width));
                lastCommittedTokens = new ArrayList<>(trim.tokens);
                lastCommittedWidth = trim.width;
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
                    TrimResult trim = trimTrailingSpaces(current, currentWidth);
                    lines.add(buildLine(trim.tokens, trim.width));
                    lastCommittedTokens = new ArrayList<>(trim.tokens);
                    lastCommittedWidth = trim.width;
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
                        lastCommittedTokens = new ArrayList<>(Collections.singletonList(piece));
                        lastCommittedWidth = piece.width;
                    }
                }
                continue;
            }

            // 当前 token 加入后仍未超宽时，直接吸收
            if (currentWidth + token.width <= widthLimit) {
                current.add(token);
                currentWidth += token.width;
                continue;
            }

            // 空行时至少放一个字符，但行首禁则字符需要回退到上一行
            if (current.isEmpty()) {
                if (isLineStartProhibited(token) && lastCommittedTokens != null) {
                    lastCommittedTokens.add(token);
                    lastCommittedWidth += token.width;
                    lines.set(lines.size() - 1, buildLine(lastCommittedTokens, lastCommittedWidth));
                    continue;
                }
                current.add(token);
                currentWidth = token.width;
                continue;
            }

            // 超宽的是空格时，直接在空格处分行
            if (token.type == TokenType.SPACE) {
                TrimResult trim = trimTrailingSpaces(current, currentWidth);
                lines.add(buildLine(trim.tokens, trim.width));
                lastCommittedTokens = new ArrayList<>(trim.tokens);
                lastCommittedWidth = trim.width;
                current = new ArrayList<>();
                currentWidth = 0;
                continue;
            }

            // 行首禁则：标点符号不应出现在行首，强制将其留在当前行（允许微幅超宽）
            if (isLineStartProhibited(token)) {
                current.add(token);
                currentWidth += token.width;
                continue;
            }

            // 普通单词导致超宽：先提交当前行，再把该单词作为下一行的起点
            TrimResult trim = trimTrailingSpaces(current, currentWidth);
            lines.add(buildLine(trim.tokens, trim.width));
            lastCommittedTokens = new ArrayList<>(trim.tokens);
            lastCommittedWidth = trim.width;
            current = new ArrayList<>();
            current.add(token);
            currentWidth = token.width;
        }

        // 循环结束后仍然可能有尚未提交的尾行
        if (!current.isEmpty() || lines.isEmpty()) {
            TrimResult trim = trimTrailingSpaces(current, currentWidth);
            lines.add(buildLine(trim.tokens, trim.width));
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

                // 空白字符归为 SPACE token
                if (Character.isWhitespace(codePoint)) {
                    if (currentType != null && currentType != TokenType.SPACE) {
                        flushBufferedToken(tokens, buffer, currentType, run, graphics);
                    }
                    currentType = TokenType.SPACE;
                    buffer.append(ch);
                    continue;
                }

                // CJK 字符各自成为独立 token，避免混合文本中英文单词被 mid-word 断开
                if (isCJK(codePoint)) {
                    flushBufferedToken(tokens, buffer, currentType, run, graphics);
                    currentType = null;
                    int letterSpacing = run.getStyle().getLetterSpacing();
                    int width = textMeasurer.measureWidthWithSpacing(graphics, ch, run.getStyle().getFont(), letterSpacing) + letterSpacing;
                    tokens.add(new Token(TokenType.WORD, ch, run.getStyle(), width, letterSpacing));
                    continue;
                }

                // 其他非空白字符累积为一个 WORD token
                if (currentType != null && currentType != TokenType.WORD) {
                    flushBufferedToken(tokens, buffer, currentType, run, graphics);
                }
                currentType = TokenType.WORD;
                buffer.append(ch);
            }
            flushBufferedToken(tokens, buffer, currentType, run, graphics);
        }
        return tokens;
    }

    /**
     * 判断 token 是否为行首禁则字符（不应出现在行首的标点符号）
     *
     * @param token 待判断的 token
     * @return 是否为行首禁则字符
     */
    private boolean isLineStartProhibited(Token token) {
        if (token.type != TokenType.WORD || token.text.isEmpty()) {
            return false;
        }
        return isLineStartProhibitedChar(token.text.codePointAt(0));
    }

    /**
     * 判断 Unicode 码点是否为行首禁则字符
     * 中文排版中，关闭类标点（逗号、句号、右括号等）不应出现在行首
     *
     * @param codePoint Unicode 码点
     * @return 是否为行首禁则字符
     */
    private boolean isLineStartProhibitedChar(int codePoint) {
        // CJK Symbols and Punctuation - 关闭类标点与右括号
        if (codePoint == 0x3001) return true; // 、
        if (codePoint == 0x3002) return true; // 。
        if (codePoint == 0x300B) return true; // 》
        if (codePoint == 0x300D) return true; // 」
        if (codePoint == 0x300F) return true; // 』
        if (codePoint == 0x3011) return true; // 】
        if (codePoint == 0x3015) return true; // 〕
        if (codePoint == 0x3017) return true; // 〗

        // Fullwidth Forms - 关闭类标点与右括号
        if (codePoint == 0xFF01) return true; // ！
        if (codePoint == 0xFF09) return true; // ）
        if (codePoint == 0xFF0C) return true; // ，
        if (codePoint == 0xFF0E) return true; // ．
        if (codePoint == 0xFF1A) return true; // ：
        if (codePoint == 0xFF1B) return true; // ；
        if (codePoint == 0xFF1D) return true; // 〉
        if (codePoint == 0xFF1F) return true; // ？

        // General Punctuation - 右引号
        if (codePoint == 0x2019) return true; // '
        if (codePoint == 0x201D) return true; // "

        return false;
    }

    /**
     * 判断字符是否属于 CJK（中日韩）字符集
     * CJK 字符在排版中可在任意两个字符之间换行，因此需要各自成为独立 token，
     * 防止英文单词在混合文本中被 mid-word 断开。
     *
     * @param codePoint Unicode 码点
     * @return 是否为 CJK 字符
     */
    private boolean isCJK(int codePoint) {
        // CJK Unified Ideographs
        if (codePoint >= 0x4E00 && codePoint <= 0x9FFF) return true;
        // CJK Extension A
        if (codePoint >= 0x3400 && codePoint <= 0x4DBF) return true;
        // CJK Extension B
        if (codePoint >= 0x20000 && codePoint <= 0x2A6DF) return true;
        // CJK Compatibility Ideographs
        if (codePoint >= 0xF900 && codePoint <= 0xFAFF) return true;
        // CJK Symbols and Punctuation
        if (codePoint >= 0x3000 && codePoint <= 0x303F) return true;
        // Fullwidth Forms
        if (codePoint >= 0xFF00 && codePoint <= 0xFFEF) return true;
        // Hiragana
        if (codePoint >= 0x3040 && codePoint <= 0x309F) return true;
        // Katakana
        if (codePoint >= 0x30A0 && codePoint <= 0x30FF) return true;
        // Hangul Syllables
        if (codePoint >= 0xAC00 && codePoint <= 0xD7AF) return true;
        // Hangul Jamo
        if (codePoint >= 0x1100 && codePoint <= 0x11FF) return true;
        return false;
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
        Font font = token.style.getFont();
        for (int i = 0; i < token.text.length(); ) {
            int codePoint = token.text.codePointAt(i);
            String ch = new String(Character.toChars(codePoint));
            i += Character.charCount(codePoint);
            int charWidth = getCachedCharBaseWidth(graphics, ch, font);
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
     * 去除行尾空白 token 并计算去除后的宽度
     *
     * @param tokens     token 列表
     * @param totalWidth 包含空白在内的总宽度
     * @return trim 结果
     */
    private TrimResult trimTrailingSpaces(List<Token> tokens, int totalWidth) {
        List<Token> result = new ArrayList<>(tokens);
        int width = totalWidth;
        while (!result.isEmpty() && result.get(result.size() - 1).type == TokenType.SPACE) {
            width -= result.remove(result.size() - 1).width;
        }
        return new TrimResult(result, width);
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

    /**
     * 获取单个字符的基础宽度（不含字间距），优先从缓存读取
     *
     * @param graphics 图形上下文
     * @param ch       单个字符
     * @param font     测量字体
     * @return 字符基础宽度
     */
    private int getCachedCharBaseWidth(Graphics2D graphics, String ch, Font font) {
        String cacheKey = font.getName() + "#" + font.getStyle() + "#" + font.getSize() + "#" + ch;
        Integer cached = charWidthCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        int width = graphics.getFontMetrics(font).stringWidth(ch);
        charWidthCache.put(cacheKey, width);
        return width;
    }

    /**
     * trimTrailingSpaces 的返回结果
     */
    private static class TrimResult {
        final List<Token> tokens;
        final int width;

        TrimResult(List<Token> tokens, int width) {
            this.tokens = tokens;
            this.width = width;
        }
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
