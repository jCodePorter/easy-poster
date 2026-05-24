package com.bytefuture.easy.poster.verify;

import com.bytefuture.easy.poster.element.basic.text.layout.TextLine;
import com.bytefuture.easy.poster.element.basic.text.layout.TextMeasurer;
import com.bytefuture.easy.poster.element.basic.text.layout.TextSplitter;
import com.bytefuture.easy.poster.element.basic.text.style.ResolvedTextSpan;
import com.bytefuture.easy.poster.element.basic.text.style.ResolvedTextStyle;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 基于 Knuth-Plass 算法的文本拆分器
 * 通过全局最优断行取代贪心策略，实现更均衡的排版效果。
 * 支持中日韩（CJK）字符间换行、行首/行尾禁则（避头尾）规则，
 * 以及混合文本（CJK + 西文）的排版优化。
 *
 * <p>使用方式：与 TextSplitter 具有相同的公开接口 {@link #splitLines}，
 * 可在 TextLayoutEngine 中将 {@code new TextSplitter()} 替换为
 * {@code new KnuthPlassTextSplitter()} 来启用本算法。</p>
 *
 * @author biaoy
 * @since 2026/05/24
 */
public class KnuthPlassTextSplitter {

    /**
     * 强制断行惩罚值（对应 Knuth-Plass 中的 -infinity）
     */
    private static final int FORCED_BREAK_PENALTY = -10000;

    /**
     * 行首禁则惩罚：断行后下一行首字符为避头标点时的额外代价
     */
    private static final int KINSOKU_START_PENALTY = 500;

    /**
     * 行尾禁则惩罚：断行后当前行末字符为避尾标点时的额外代价
     */
    private static final int KINSOKU_END_PENALTY = 300;

    /**
     * CJK/非CJK 边界惩罚：在 CJK 字符与西文单词之间断行的额外代价
     */
    private static final int CJK_BOUNDARY_PENALTY = 50;

    /**
     * 默认容差：调整比率（adjustment ratio）的绝对值上限
     */
    private static final double DEFAULT_TOLERANCE = 2.0;

    /**
     * 宽容容差：首轮 DP 无可行解时的放宽阈值
     */
    private static final double RELAXED_TOLERANCE = 10.0;

    /**
     * 相邻行适应类差异惩罚
     */
    private static final double ADJACENT_FITNESS_COST = 50.0;

    /**
     * 空格伸展因子：Glue 的 stretch = width * 此值
     */
    private static final double SPACE_STRETCH_FACTOR = 0.5;

    /**
     * 空格收缩因子：Glue 的 shrink = width * 此值
     */
    private static final double SPACE_SHRINK_FACTOR = 0.25;

    /**
     * CJK 行最小伸展/收缩比例（无空格时仍允许微幅偏差）
     */
    private static final double MIN_LINE_FLEX_RATIO = 0.02;

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

        // 不限制宽度时直接组装单行
        if (widthLimit <= 0) {
            int totalWidth = sumTokenWidths(tokens);
            TrimResult trim = trimTrailingSpaces(tokens, totalWidth);
            return Collections.singletonList(buildLine(trim.tokens, trim.width));
        }

        // 按显式换行符拆分段落，每段独立优化
        List<List<Token>> paragraphs = splitIntoParagraphs(tokens);
        List<TextLine> result = new ArrayList<>();

        for (List<Token> paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                result.add(TextLine.empty());
                continue;
            }
            result.addAll(processParagraph(paragraph, widthLimit, graphics, runs));
        }

        if (result.isEmpty()) {
            result.add(TextLine.empty());
        }
        return result;
    }

    /**
     * 对单个段落执行 Knuth-Plass 断行优化
     */
    private List<TextLine> processParagraph(List<Token> tokens, int widthLimit,
                                            Graphics2D graphics, List<ResolvedTextSpan> originalRuns) {
        // 预处理：拆分超宽 token
        List<Token> processed = preprocessOversizedTokens(tokens, widthLimit, graphics);
        if (processed.isEmpty()) {
            return Collections.singletonList(TextLine.empty());
        }

        // 转换为 Knuth-Plass 排版元素
        List<Item> items = tokensToItems(processed);

        // 单行即够时直接组装
        double naturalWidth = computeItemsNaturalWidth(items);
        if (naturalWidth <= widthLimit) {
            int totalWidth = sumTokenWidths(processed);
            TrimResult trim = trimTrailingSpaces(processed, totalWidth);
            return Collections.singletonList(buildLine(trim.tokens, trim.width));
        }

        // Knuth-Plass DP 断行
        List<Integer> breaks = breakLines(items, widthLimit, DEFAULT_TOLERANCE);
        if (breaks == null) {
            breaks = breakLines(items, widthLimit, RELAXED_TOLERANCE);
        }
        if (breaks == null) {
            // DP 无可行解时回退到贪心算法
            TextSplitter fallback = new TextSplitter();
            return fallback.splitLines(graphics, originalRuns, widthLimit);
        }

        return buildLinesFromBreaks(items, breaks, processed);
    }

    // ======================== 段落拆分 ========================

    /**
     * 按 NEWLINE token 将 token 列表拆分为多个段落
     */
    private List<List<Token>> splitIntoParagraphs(List<Token> tokens) {
        List<List<Token>> paragraphs = new ArrayList<>();
        List<Token> current = new ArrayList<>();
        for (Token token : tokens) {
            if (token.type == TokenType.NEWLINE) {
                paragraphs.add(current);
                current = new ArrayList<>();
            } else {
                current.add(token);
            }
        }
        paragraphs.add(current);
        return paragraphs;
    }

    // ======================== 预处理 ========================

    /**
     * 拆分超过行宽限制的 token
     */
    private List<Token> preprocessOversizedTokens(List<Token> tokens, int widthLimit, Graphics2D graphics) {
        List<Token> result = new ArrayList<>();
        for (Token token : tokens) {
            if (token.type == TokenType.WORD && token.width > widthLimit) {
                result.addAll(splitOversizedToken(token, widthLimit, graphics));
            } else {
                result.add(token);
            }
        }
        return result;
    }

    // ======================== 排版元素转换 ========================

    /**
     * 将 token 序列转换为 Knuth-Plass 排版元素（Box/Glue/Penalty）
     */
    private List<Item> tokensToItems(List<Token> tokens) {
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            Token nextToken = (i + 1 < tokens.size()) ? tokens.get(i + 1) : null;

            switch (token.type) {
                case SPACE:
                    double stretch = token.width * SPACE_STRETCH_FACTOR;
                    double shrink = token.width * SPACE_SHRINK_FACTOR;
                    items.add(new GlueItem(token.width, stretch, shrink, token));
                    // 空格处断行惩罚：如果下一 token 为行首禁则字符则加惩罚
                    int spacePenalty = 0;
                    if (nextToken != null && isLineStartProhibitedToken(nextToken)) {
                        spacePenalty += KINSOKU_START_PENALTY;
                    }
                    items.add(new PenaltyItem(spacePenalty, false));
                    break;

                case WORD:
                    // 连续 WORD token 之间插入断行惩罚（CJK 字符间可断行）
                    if (!items.isEmpty() && lastItemIsBox(items)) {
                        Token prevWordToken = getBoxTokenFromLast(items);
                        int penalty = calculateBreakPenalty(prevWordToken, token);
                        items.add(new PenaltyItem(penalty, false));
                    }
                    items.add(new BoxItem(token.width, token));
                    break;

                default:
                    break;
            }
        }

        // 段落末尾：强制断行
        items.add(new PenaltyItem(FORCED_BREAK_PENALTY, true));
        return items;
    }

    private boolean lastItemIsBox(List<Item> items) {
        return items.get(items.size() - 1) instanceof BoxItem;
    }

    private Token getBoxTokenFromLast(List<Item> items) {
        return ((BoxItem) items.get(items.size() - 1)).token;
    }

    /**
     * 计算在 prevToken 和 nextToken 之间断行的惩罚值
     */
    private int calculateBreakPenalty(Token prevToken, Token nextToken) {
        int penalty = 0;
        // 行尾禁则：当前行末字符不应为避尾标点
        if (isLineEndProhibitedToken(prevToken)) {
            penalty += KINSOKU_END_PENALTY;
        }
        // 行首禁则：下一行首字符不应为避头标点
        if (isLineStartProhibitedToken(nextToken)) {
            penalty += KINSOKU_START_PENALTY;
        }
        // CJK/非CJK 边界断行代价
        boolean prevIsCJK = isCJKToken(prevToken);
        boolean nextIsCJK = isCJKToken(nextToken);
        if (prevIsCJK != nextIsCJK) {
            penalty += CJK_BOUNDARY_PENALTY;
        }
        return penalty;
    }

    // ======================== Knuth-Plass DP ========================

    /**
     * Knuth-Plass 动态规划断行算法
     *
     * @param items     排版元素序列
     * @param lineWidth 目标行宽
     * @param tolerance 调整比率容差
     * @return 最优断点位置列表（PenaltyItem 在 items 中的索引），无可行解时返回 null
     */
    private List<Integer> breakLines(List<Item> items, int lineWidth, double tolerance) {
        // 收集所有 Penalty 位置作为候选断点，并加入虚拟起点
        List<Integer> breakPositions = new ArrayList<>();
        breakPositions.add(-1);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof PenaltyItem) {
                breakPositions.add(i);
            }
        }

        int m = breakPositions.size();
        double[] dp = new double[m];
        int[] prevIdx = new int[m];
        int[] fitnessClass = new int[m];
        Arrays.fill(dp, Double.POSITIVE_INFINITY);
        Arrays.fill(prevIdx, -1);
        dp[0] = 0;

        // 前缀和：Box 总宽度 + Glue 自然宽度 / 伸展 / 收缩
        int n = items.size();
        double[] boxPref = new double[n + 1];
        double[] glueNaturalPref = new double[n + 1];
        double[] glueStretchPref = new double[n + 1];
        double[] glueShrinkPref = new double[n + 1];
        for (int i = 0; i < n; i++) {
            boxPref[i + 1] = boxPref[i];
            glueNaturalPref[i + 1] = glueNaturalPref[i];
            glueStretchPref[i + 1] = glueStretchPref[i];
            glueShrinkPref[i + 1] = glueShrinkPref[i];
            Item item = items.get(i);
            if (item instanceof BoxItem) {
                boxPref[i + 1] += ((BoxItem) item).width;
            } else if (item instanceof GlueItem) {
                GlueItem g = (GlueItem) item;
                glueNaturalPref[i + 1] += g.width;
                glueStretchPref[i + 1] += g.stretch;
                glueShrinkPref[i + 1] += g.shrink;
            }
        }

        // 动态规划：对每个候选断点，寻找最优前驱断点
        for (int cur = 1; cur < m; cur++) {
            int endPos = breakPositions.get(cur);
            PenaltyItem curPenaltyItem = (PenaltyItem) items.get(endPos);

            for (int prevBrk = 0; prevBrk < cur; prevBrk++) {
                int startPos = breakPositions.get(prevBrk) + 1;
                if (startPos > endPos) continue;

                double totalBox = boxPref[endPos] - boxPref[startPos];
                double totalGlueNatural = glueNaturalPref[endPos] - glueNaturalPref[startPos];
                double totalStretch = glueStretchPref[endPos] - glueStretchPref[startPos];
                double totalShrink = glueShrinkPref[endPos] - glueShrinkPref[startPos];

                // CJK 行无空格时仍需微幅伸缩空间
                double minFlex = lineWidth * MIN_LINE_FLEX_RATIO;
                totalStretch = Math.max(totalStretch, minFlex);
                totalShrink = Math.max(totalShrink, minFlex);

                double naturalWidth = totalBox + totalGlueNatural;
                double diff = lineWidth - naturalWidth;

                // 调整比率（adjustment ratio）
                double ratio;
                boolean feasible;
                if (Math.abs(diff) < 1e-6) {
                    ratio = 0;
                    feasible = true;
                } else if (diff >= 0) {
                    ratio = diff / totalStretch;
                    feasible = ratio <= tolerance;
                } else {
                    ratio = diff / totalShrink;
                    feasible = ratio >= -tolerance;
                }

                if (!feasible) continue;

                // 不良度（badness）
                double badness = Math.min(100 * Math.abs(ratio) * Math.abs(ratio) * Math.abs(ratio), 10000);

                // 惩罚值
                double penaltyVal = curPenaltyItem.forced ? 0 : Math.abs(curPenaltyItem.penalty);

                // 适应类（fitness class）
                int cls;
                if (ratio < -0.5) cls = 0;
                else if (ratio < 0.5) cls = 1;
                else if (ratio < 1.0) cls = 2;
                else cls = 3;

                // 相邻适应类差异惩罚
                double extra = 0;
                if (prevBrk > 0 && prevIdx[prevBrk] >= 0 && Math.abs(cls - fitnessClass[prevBrk]) > 1) {
                    extra = ADJACENT_FITNESS_COST;
                }

                // 总代价（demerits）
                double demerits = (1 + badness) * (1 + badness) + penaltyVal * penaltyVal + extra;
                double candidateTotal = dp[prevBrk] + demerits;
                if (candidateTotal < dp[cur] - 1e-9) {
                    dp[cur] = candidateTotal;
                    prevIdx[cur] = prevBrk;
                    fitnessClass[cur] = cls;
                }
            }
        }

        // 无可行解
        if (dp[m - 1] == Double.POSITIVE_INFINITY) {
            return null;
        }

        // 回溯最优断点序列
        List<Integer> result = new ArrayList<>();
        int idx = m - 1;
        while (idx > 0) {
            if (prevIdx[idx] < 0) break;
            result.add(breakPositions.get(idx));
            idx = prevIdx[idx];
        }
        Collections.reverse(result);
        return result;
    }

    // ======================== 行重建 ========================

    /**
     * 从断点序列重建 TextLine 列表
     */
    private List<TextLine> buildLinesFromBreaks(List<Item> items, List<Integer> breaks, List<Token> processedTokens) {
        List<TextLine> lines = new ArrayList<>();

        // 将段落末尾强制断行纳入断点列表
        int lastItemIndex = items.size() - 1;
        if (breaks.isEmpty() || breaks.get(breaks.size() - 1) != lastItemIndex) {
            breaks.add(lastItemIndex);
        }

        int lineStartItemIdx = 0;
        for (int breakIdx : breaks) {
            // 收集 lineStartItemIdx 到 breakIdx 之间的 Box/Glue 对应 token
            List<Token> lineTokens = new ArrayList<>();
            int lineWidth = 0;
            for (int i = lineStartItemIdx; i < breakIdx; i++) {
                Item item = items.get(i);
                if (item instanceof BoxItem) {
                    BoxItem box = (BoxItem) item;
                    lineTokens.add(box.token);
                    lineWidth += box.width;
                } else if (item instanceof GlueItem) {
                    GlueItem glue = (GlueItem) item;
                    lineTokens.add(glue.token);
                    lineWidth += glue.width;
                }
                // PenaltyItem 跳过（零宽度，仅标记断点）
            }

            // 去除行首空格
            while (!lineTokens.isEmpty() && lineTokens.get(0).type == TokenType.SPACE) {
                lineWidth -= lineTokens.remove(0).width;
            }
            // 去除行尾空格
            TrimResult trim = trimTrailingSpaces(lineTokens, lineWidth);

            if (!trim.tokens.isEmpty()) {
                lines.add(buildLine(trim.tokens, trim.width));
            } else if (lines.isEmpty()) {
                lines.add(TextLine.empty());
            }

            lineStartItemIdx = breakIdx + 1;
        }

        // 尾部无内容时补充空行
        if (lines.isEmpty()) {
            lines.add(TextLine.empty());
        }
        return lines;
    }

    // ======================== 分词（与 TextSplitter 相同） ========================

    /**
     * 将运行单元拆分为可参与换行判断的 token
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

                if ("\r".equals(ch)) {
                    continue;
                }
                if ("\n".equals(ch)) {
                    flushBufferedToken(tokens, buffer, currentType, run, graphics);
                    currentType = null;
                    tokens.add(new Token(TokenType.NEWLINE, "\n", run.getStyle(), 0, run.getStyle().getLetterSpacing()));
                    continue;
                }

                if (Character.isWhitespace(codePoint)) {
                    if (currentType != null && currentType != TokenType.SPACE) {
                        flushBufferedToken(tokens, buffer, currentType, run, graphics);
                    }
                    currentType = TokenType.SPACE;
                    buffer.append(ch);
                    continue;
                }

                if (isCJK(codePoint)) {
                    flushBufferedToken(tokens, buffer, currentType, run, graphics);
                    currentType = null;
                    int letterSpacing = run.getStyle().getLetterSpacing();
                    int width = textMeasurer.measureWidthWithSpacing(graphics, ch, run.getStyle().getFont(), letterSpacing) + letterSpacing;
                    tokens.add(new Token(TokenType.WORD, ch, run.getStyle(), width, letterSpacing));
                    continue;
                }

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

    // ======================== 行组装 ========================

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

    /**
     * 去除行尾空白 token 并计算去除后的宽度
     */
    private TrimResult trimTrailingSpaces(List<Token> tokens, int totalWidth) {
        List<Token> result = new ArrayList<>(tokens);
        int width = totalWidth;
        while (!result.isEmpty() && result.get(result.size() - 1).type == TokenType.SPACE) {
            width -= result.remove(result.size() - 1).width;
        }
        return new TrimResult(result, width);
    }

    private boolean canMergeSegment(TextLine.Segment previous, Token token) {
        return hasSameStyle(previous.getStyle(), token.style)
                && previous.isStretchableSpace() == (token.type == TokenType.SPACE);
    }

    private boolean hasSameStyle(ResolvedTextStyle left, ResolvedTextStyle right) {
        return left.getFont().equals(right.getFont())
                && left.getColor().equals(right.getColor())
                && left.isUnderline() == right.isUnderline()
                && left.isStrikeThrough() == right.isStrikeThrough()
                && left.getLetterSpacing() == right.getLetterSpacing();
    }

    private int sumTokenWidths(List<Token> tokens) {
        int sum = 0;
        for (Token token : tokens) {
            sum += token.width;
        }
        return sum;
    }

    private double computeItemsNaturalWidth(List<Item> items) {
        double width = 0;
        for (Item item : items) {
            if (item instanceof BoxItem) {
                width += ((BoxItem) item).width;
            } else if (item instanceof GlueItem) {
                width += ((GlueItem) item).width;
            }
        }
        return width;
    }

    // ======================== 字符分类 ========================

    /**
     * 判断 Unicode 码点是否属于 CJK（中日韩）字符集
     */
    private boolean isCJK(int codePoint) {
        if (codePoint >= 0x4E00 && codePoint <= 0x9FFF) return true;
        if (codePoint >= 0x3400 && codePoint <= 0x4DBF) return true;
        if (codePoint >= 0x20000 && codePoint <= 0x2A6DF) return true;
        if (codePoint >= 0xF900 && codePoint <= 0xFAFF) return true;
        if (codePoint >= 0x3000 && codePoint <= 0x303F) return true;
        if (codePoint >= 0xFF00 && codePoint <= 0xFFEF) return true;
        if (codePoint >= 0x3040 && codePoint <= 0x309F) return true;
        if (codePoint >= 0x30A0 && codePoint <= 0x30FF) return true;
        if (codePoint >= 0xAC00 && codePoint <= 0xD7AF) return true;
        if (codePoint >= 0x1100 && codePoint <= 0x11FF) return true;
        return false;
    }

    private boolean isCJKToken(Token token) {
        if (token.type != TokenType.WORD || token.text.isEmpty()) return false;
        return isCJK(token.text.codePointAt(0));
    }

    /**
     * 判断 token 是否为行首禁则字符
     */
    private boolean isLineStartProhibitedToken(Token token) {
        if (token.type != TokenType.WORD || token.text.isEmpty()) return false;
        return isLineStartProhibitedChar(token.text.codePointAt(0));
    }

    /**
     * 判断 Unicode 码点是否为行首禁则字符（不应出现在行首）
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
     * 判断 token 是否为行尾禁则字符
     */
    private boolean isLineEndProhibitedToken(Token token) {
        if (token.type != TokenType.WORD || token.text.isEmpty()) return false;
        return isLineEndProhibitedChar(token.text.codePointAt(0));
    }

    /**
     * 判断 Unicode 码点是否为行尾禁则字符（不应出现在行尾）
     * 中文排版中，左括号、左引号等不应出现在行尾
     */
    private boolean isLineEndProhibitedChar(int codePoint) {
        // Fullwidth Forms - 左括号
        if (codePoint == 0xFF08) return true; // （
        // CJK Symbols and Punctuation - 左括号
        if (codePoint == 0x300A) return true; // 《
        if (codePoint == 0x300C) return true; // 「
        if (codePoint == 0x300E) return true; // 『
        if (codePoint == 0x3010) return true; // 【
        if (codePoint == 0x3014) return true; // 〔
        if (codePoint == 0x3016) return true; // 〖
        // General Punctuation - 左引号
        if (codePoint == 0x2018) return true; // '
        if (codePoint == 0x201C) return true; // "
        return false;
    }

    // ======================== 宽度缓存 ========================

    /**
     * 获取单个字符的基础宽度（不含字间距），优先从缓存读取
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

    // ======================== 内部类型 ========================

    /**
     * Knuth-Plass 排版元素基接口
     */
    private interface Item {
    }

    /**
     * Box：固定宽度内容（单词或 CJK 单字）
     */
    private static class BoxItem implements Item {
        final int width;
        final Token token;

        BoxItem(int width, Token token) {
            this.width = width;
            this.token = token;
        }
    }

    /**
     * Glue：弹性间距（空格），具有自然宽度、伸展量和收缩量
     */
    private static class GlueItem implements Item {
        final int width;
        final double stretch;
        final double shrink;
        final Token token;

        GlueItem(int width, double stretch, double shrink, Token token) {
            this.width = width;
            this.stretch = stretch;
            this.shrink = shrink;
            this.token = token;
        }
    }

    /**
     * Penalty：潜在断行点，带有代价值；forced=true 时为强制断行
     */
    private static class PenaltyItem implements Item {
        final int penalty;
        final boolean forced;

        PenaltyItem(int penalty, boolean forced) {
            this.penalty = penalty;
            this.forced = forced;
        }
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

    /**
     * 分词 token
     */
    private static class Token {
        final TokenType type;
        final String text;
        final ResolvedTextStyle style;
        final int width;
        final int letterSpacing;

        Token(TokenType type, String text, ResolvedTextStyle style, int width, int letterSpacing) {
            this.type = type;
            this.text = text;
            this.style = style;
            this.width = width;
            this.letterSpacing = letterSpacing;
        }
    }

    /**
     * Token 类型枚举
     */
    private enum TokenType {
        WORD,
        SPACE,
        NEWLINE
    }
}