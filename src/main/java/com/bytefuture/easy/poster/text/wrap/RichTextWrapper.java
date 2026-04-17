package com.bytefuture.easy.poster.text.wrap;

import com.bytefuture.easy.poster.text.layout.TextRenderSpec;
import com.bytefuture.easy.poster.model.TextOverflowStrategy;
import com.bytefuture.easy.poster.model.TextSpan;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RichTextWrapper {

    private static final EllipsisProcessor ELLIPSIS_PROCESSOR = new EllipsisProcessor();

    public interface Measurer {
        int measureBaseStringWidth(String text, FontMetrics fontMetrics, Graphics2D graphics);

        int measureRichGlyphsWidth(List<RichGlyph> glyphs, int letterSpacing);

        String normalizeLineBreaks(String text);
    }

    public ResolvedRichTextLines resolveRichTextLines(TextRenderSpec spec, Graphics2D graphics,
                                                      TextOverflowStrategy overflowStrategy,
                                                      Measurer measurer) {
        Color defaultColor = spec.getColor();
        Font baseFont = spec.getBaseFont();
        int widthLimit = spec.resolveWidthLimit();
        List<ResolvedTextSpan> spans = resolveAllTextSpans(spec, defaultColor);
        List<RichToken> tokens = tokenizeRichText(spec, spans, graphics, measurer);

        if (tokens.isEmpty()) {
            // 空富文本也返回一行空行，避免后续布局逻辑额外判空。
            RichLine emptyLine = createEmptyRichLine();
            return new ResolvedRichTextLines(Collections.singletonList(emptyLine),
                    resolveRichLayoutWidth(Collections.singletonList(emptyLine), overflowStrategy, widthLimit),
                    false, false);
        }

        List<RichLine> rawLines;
        if (overflowStrategy == TextOverflowStrategy.WRAP && widthLimit > 0) {
            // 富文本按 token 换行，尽量保留词和标点的语义边界。
            rawLines = resolveWrappedRichLines(tokens, widthLimit, spec.getLetterSpacing(), measurer);
        } else {
            rawLines = splitRichLinesByExplicitNewLine(tokens, spec.getLetterSpacing(), measurer);
        }

        if (rawLines.isEmpty()) {
            rawLines = Collections.singletonList(createEmptyRichLine());
        }

        List<RichLine> visibleLines = rawLines;
        if (overflowStrategy == TextOverflowStrategy.ELLIPSIS && widthLimit > 0) {
            // 富文本省略需要保留片段样式，因此单独走富文本省略流程。
            visibleLines = ELLIPSIS_PROCESSOR.applyRichWidthEllipsis(spec, visibleLines, widthLimit, baseFont, defaultColor,
                    graphics, new EllipsisProcessor.RichTextMeasurer() {
                        @Override
                        public int measureBaseStringWidth(String text, FontMetrics fontMetrics, Graphics2D lineGraphics) {
                            return measurer.measureBaseStringWidth(text, fontMetrics, lineGraphics);
                        }

                        @Override
                        public int measureRichGlyphsWidth(List<RichGlyph> glyphs, int letterSpacing) {
                            return measurer.measureRichGlyphsWidth(glyphs, letterSpacing);
                        }
                    });
        }

        ResolvedRichTextLines limitedLines = applyRichMaxLines(spec, visibleLines,
                resolveRichLayoutWidth(visibleLines, overflowStrategy, widthLimit), baseFont, defaultColor, graphics, measurer);
        if (overflowStrategy == TextOverflowStrategy.CLIP && widthLimit > 0) {
            return new ResolvedRichTextLines(limitedLines.getLines(), widthLimit,
                    limitedLines.isTruncated(), true);
        }
        return limitedLines;
    }

    private List<ResolvedTextSpan> resolveAllTextSpans(TextRenderSpec spec, Color defaultColor) {
        List<ResolvedTextSpan> spans = new ArrayList<ResolvedTextSpan>();
        Font baseFont = spec.getBaseFont();
        if (spec.getText() != null && !spec.getText().isEmpty()) {
            // 兼容普通文本与富文本片段混合输入。
            spans.add(new ResolvedTextSpan(spec.getText(), baseFont, defaultColor,
                    spec.isUnderline(), spec.isStrikeThrough()));
        }
        for (TextSpan textSpan : spec.getTextSpans()) {
            spans.add(resolveTextSpan(spec, textSpan, baseFont, defaultColor));
        }
        return spans;
    }

    private ResolvedTextSpan resolveTextSpan(TextRenderSpec spec, TextSpan textSpan,
                                             Font baseFont, Color defaultColor) {
        // span 未设置的样式字段回退到元素级默认样式。
        int resolvedStyle = textSpan.getFontStyle() != null ? textSpan.getFontStyle() : baseFont.getStyle();
        int resolvedSize = textSpan.getFontSize() != null ? textSpan.getFontSize() : Math.round(baseFont.getSize2D());
        Font spanFont = resolvedStyle == baseFont.getStyle() && resolvedSize == Math.round(baseFont.getSize2D())
                ? baseFont
                : baseFont.deriveFont(resolvedStyle, (float) resolvedSize);
        Color spanColor = textSpan.getColor() != null ? textSpan.getColor() : defaultColor;
        boolean spanUnderline = textSpan.getUnderline() != null ? textSpan.getUnderline() : spec.isUnderline();
        boolean spanStrikeThrough = textSpan.getStrikeThrough() != null ? textSpan.getStrikeThrough() : spec.isStrikeThrough();
        return new ResolvedTextSpan(textSpan.getText(), spanFont, spanColor, spanUnderline, spanStrikeThrough);
    }

    private List<RichToken> tokenizeRichText(TextRenderSpec spec,
                                             List<ResolvedTextSpan> spans,
                                                                Graphics2D graphics, Measurer measurer) {
        List<RichToken> tokens = new ArrayList<RichToken>();
        List<RichGlyph> bufferGlyphs = new ArrayList<RichGlyph>();
        RichTokenType bufferType = null;

        for (ResolvedTextSpan span : spans) {
            String normalized = measurer.normalizeLineBreaks(span.getText());
            FontMetrics fontMetrics = graphics.getFontMetrics(span.getFont());

            for (int i = 0; i < normalized.length(); i++) {
                char current = normalized.charAt(i);
                if (current == '\n') {
                    // 显式换行会先冲刷缓冲 token，再插入换行标记。
                    flushRichToken(tokens, bufferGlyphs, bufferType, specLetterSpacing(spec), measurer);
                    bufferType = null;
                    tokens.add(RichToken.newLine());
                    continue;
                }

                String currentText = String.valueOf(current);
                RichGlyph glyph = new RichGlyph(currentText,
                        measurer.measureBaseStringWidth(currentText, fontMetrics, graphics),
                        span.getFont(), span.getColor(), span.isUnderline(), span.isStrikeThrough());
                RichTokenType currentType = resolveRichTokenType(current);
                if (currentType == RichTokenType.WORD
                        || currentType == RichTokenType.WHITESPACE) {
                    // 连续单词或空白尽量合并，便于后续按词换行。
                    if (bufferType != currentType) {
                        flushRichToken(tokens, bufferGlyphs, bufferType, specLetterSpacing(spec), measurer);
                        bufferType = currentType;
                    }
                    bufferGlyphs.add(glyph);
                    continue;
                }

                flushRichToken(tokens, bufferGlyphs, bufferType, specLetterSpacing(spec), measurer);
                bufferType = null;
                tokens.add(createRichToken(Collections.singletonList(glyph), currentType, specLetterSpacing(spec), measurer));
            }
        }

        flushRichToken(tokens, bufferGlyphs, bufferType, specLetterSpacing(spec), measurer);
        return tokens;
    }

    private int specLetterSpacing(TextRenderSpec spec) {
        return spec.getLetterSpacing();
    }

    private void flushRichToken(List<RichToken> tokens,
                                List<RichGlyph> bufferGlyphs,
                                RichTokenType bufferType, int letterSpacing, Measurer measurer) {
        if (bufferType == null || bufferGlyphs.isEmpty()) {
            bufferGlyphs.clear();
            return;
        }
        tokens.add(createRichToken(new ArrayList<RichGlyph>(bufferGlyphs), bufferType,
                letterSpacing, measurer));
        bufferGlyphs.clear();
    }

    private RichToken createRichToken(List<RichGlyph> glyphs,
                                      RichTokenType tokenType,
                                      int letterSpacing, Measurer measurer) {
        return new RichToken(buildRichText(glyphs),
                measurer.measureRichGlyphsWidth(glyphs, letterSpacing), glyphs, tokenType);
    }

    private List<RichLine> splitRichLinesByExplicitNewLine(List<RichToken> tokens,
                                                           int letterSpacing, Measurer measurer) {
        List<RichLine> lines = new ArrayList<RichLine>();
        RichTokenLineBuffer lineBuffer = new RichTokenLineBuffer();

        for (RichToken token : tokens) {
            if (token.getType() == RichTokenType.NEW_LINE) {
                flushRichLine(lines, lineBuffer, true, letterSpacing, measurer);
                continue;
            }
            if (token.getType() == RichTokenType.WHITESPACE && lineBuffer.isEmpty()) {
                continue;
            }
            lineBuffer.append(token, letterSpacing);
        }

        flushRichLine(lines, lineBuffer, false, letterSpacing, measurer);
        return lines;
    }

    private List<RichLine> resolveWrappedRichLines(List<RichToken> tokens,
                                                   int maxWidth, int letterSpacing,
                                                   Measurer measurer) {
        List<RichLine> lines = new ArrayList<RichLine>();
        RichTokenLineBuffer lineBuffer = new RichTokenLineBuffer();

        for (RichToken token : tokens) {
            if (token.getType() == RichTokenType.NEW_LINE) {
                flushRichLine(lines, lineBuffer, true, letterSpacing, measurer);
                continue;
            }

            if (token.getType() == RichTokenType.WHITESPACE && lineBuffer.isEmpty()) {
                continue;
            }

            if (lineBuffer.canAppend(token, maxWidth, letterSpacing)) {
                lineBuffer.append(token, letterSpacing);
                continue;
            }

            if (shouldForceAppendToCurrentRichLine(token, lineBuffer)) {
                // 收尾标点不希望单独掉到下一行。
                lineBuffer.append(token, letterSpacing);
                flushRichLine(lines, lineBuffer, false, letterSpacing, measurer);
                continue;
            }

            if (!lineBuffer.isEmpty()) {
                flushRichLine(lines, lineBuffer, false, letterSpacing, measurer);
                if (token.getType() == RichTokenType.WHITESPACE) {
                    continue;
                }
            }

            if (token.getWidth() <= maxWidth) {
                lineBuffer.append(token, letterSpacing);
                continue;
            }

            // 单个 token 自身超宽时，再退化为逐字切分。
            lines.addAll(splitOversizedRichToken(token, maxWidth, letterSpacing, measurer));
        }

        flushRichLine(lines, lineBuffer, false, letterSpacing, measurer);
        return lines;
    }

    private void flushRichLine(List<RichLine> lines, RichTokenLineBuffer lineBuffer,
                               boolean explicitNewLine, int letterSpacing, Measurer measurer) {
        if (lineBuffer.isEmpty()) {
            if (explicitNewLine) {
                // 连续显式换行要保留为空行。
                lines.add(createEmptyRichLine());
            }
            return;
        }

        RichLine line = createRichLineFromTokens(lineBuffer.getTokens(), true, letterSpacing, measurer);
        if (line.getText().isEmpty() && !explicitNewLine) {
            lineBuffer.clear();
            return;
        }

        lines.add(line);
        lineBuffer.clear();
    }

    private boolean shouldForceAppendToCurrentRichLine(RichToken token, RichTokenLineBuffer lineBuffer) {
        return !lineBuffer.isEmpty() && token.getType() == RichTokenType.CLOSING_PUNCTUATION;
    }

    private List<RichLine> splitOversizedRichToken(RichToken token, int maxWidth,
                                                   int letterSpacing, Measurer measurer) {
        List<RichLine> lines = new ArrayList<RichLine>();
        List<RichGlyph> currentGlyphs = new ArrayList<RichGlyph>();
        int currentWidth = 0;

        for (RichGlyph glyph : token.getGlyphs()) {
            int candidateWidth = currentGlyphs.isEmpty()
                    ? glyph.getWidth()
                    : currentWidth + letterSpacing + glyph.getWidth();
            if (!currentGlyphs.isEmpty() && candidateWidth > maxWidth) {
                lines.add(createRichLineFromGlyphs(currentGlyphs, letterSpacing));
                currentGlyphs = new ArrayList<RichGlyph>();
                currentWidth = 0;
            }
            if (!currentGlyphs.isEmpty()) {
                currentWidth += letterSpacing;
            }
            currentGlyphs.add(glyph);
            currentWidth += glyph.getWidth();
        }

        if (!currentGlyphs.isEmpty()) {
            lines.add(createRichLineFromGlyphs(currentGlyphs, letterSpacing));
        }
        return lines;
    }

    private ResolvedRichTextLines applyRichMaxLines(TextRenderSpec spec,
                                                    List<RichLine> rawLines,
                                                    int layoutWidth, Font baseFont,
                                                    Color defaultColor, Graphics2D graphics,
                                                    Measurer measurer) {
        if (spec.getMaxLines() == null || rawLines.size() <= spec.getMaxLines()) {
            return new ResolvedRichTextLines(rawLines, layoutWidth, false, false);
        }

        // 富文本超出最大行数时，最后一行需要重新生成带样式的省略结果。
        List<RichLine> visibleLines =
                new ArrayList<RichLine>(rawLines.subList(0, spec.getMaxLines()));
        int lastIndex = visibleLines.size() - 1;
        int widthLimit = layoutWidth > 0 ? layoutWidth : Integer.MAX_VALUE;
        visibleLines.set(lastIndex, ELLIPSIS_PROCESSOR.appendRichEllipsis(spec, visibleLines.get(lastIndex), widthLimit,
                baseFont, defaultColor, graphics, new EllipsisProcessor.RichTextMeasurer() {
                    @Override
                    public int measureBaseStringWidth(String text, FontMetrics fontMetrics, Graphics2D lineGraphics) {
                        return measurer.measureBaseStringWidth(text, fontMetrics, lineGraphics);
                    }

                    @Override
                    public int measureRichGlyphsWidth(List<RichGlyph> glyphs, int letterSpacing) {
                        return measurer.measureRichGlyphsWidth(glyphs, letterSpacing);
                    }
                }));

        int resolvedLayoutWidth = layoutWidth > 0 ? layoutWidth : resolveMaxRichWidth(visibleLines);
        return new ResolvedRichTextLines(visibleLines, resolvedLayoutWidth, true, false);
    }

    private int resolveRichLayoutWidth(List<RichLine> lines,
                                       TextOverflowStrategy overflowStrategy, int widthLimit) {
        if ((overflowStrategy == TextOverflowStrategy.WRAP
                || overflowStrategy == TextOverflowStrategy.CLIP
                || overflowStrategy == TextOverflowStrategy.ELLIPSIS) && widthLimit > 0) {
            return widthLimit;
        }
        return resolveMaxRichWidth(lines);
    }

    private RichLine createEmptyRichLine() {
        return new RichLine("", 0,
                Collections.<RichTextFragment>emptyList(),
                Collections.<RichGlyph>emptyList());
    }

    private RichLine createRichLineFromTokens(List<RichToken> tokens,
                                              boolean trimTrailingWhitespace,
                                              int letterSpacing, Measurer measurer) {
        int end = tokens.size();
        if (trimTrailingWhitespace) {
            while (end > 0 && tokens.get(end - 1).getType() == RichTokenType.WHITESPACE) {
                end--;
            }
        }

        List<RichGlyph> glyphs = new ArrayList<RichGlyph>();
        for (int i = 0; i < end; i++) {
            glyphs.addAll(tokens.get(i).getGlyphs());
        }
        return createRichLineFromGlyphs(glyphs, letterSpacing);
    }

    private RichLine createRichLineFromGlyphs(List<RichGlyph> glyphs,
                                              int letterSpacing) {
        if (glyphs.isEmpty()) {
            return createEmptyRichLine();
        }

        StringBuilder textBuilder = new StringBuilder();
        List<RichTextFragment> fragments =
                new ArrayList<RichTextFragment>();
        List<RichGlyph> copiedGlyphs =
                new ArrayList<RichGlyph>(glyphs);
        StringBuilder fragmentText = new StringBuilder();
        RichGlyph fragmentStyle = null;
        int fragmentStartX = 0;
        int fragmentWidth = 0;
        int currentX = 0;
        boolean firstGlyph = true;

        for (RichGlyph glyph : glyphs) {
            if (!firstGlyph) {
                currentX += letterSpacing;
            }

            if (fragmentStyle == null || !fragmentStyle.hasSameStyle(glyph)) {
                // 样式变化时切分 fragment，确保同一 fragment 内的样式一致。
                if (fragmentStyle != null) {
                    fragments.add(new RichTextFragment(fragmentText.toString(), fragmentStartX,
                            fragmentWidth, fragmentStyle.getFont(), fragmentStyle.getColor(),
                            fragmentStyle.isUnderline(), fragmentStyle.isStrikeThrough()));
                }
                fragmentStyle = glyph;
                fragmentText = new StringBuilder();
                fragmentStartX = currentX;
                fragmentWidth = 0;
            }

            textBuilder.append(glyph.getText());
            fragmentText.append(glyph.getText());
            fragmentWidth = currentX + glyph.getWidth() - fragmentStartX;
            currentX += glyph.getWidth();
            firstGlyph = false;
        }

        if (fragmentStyle != null) {
            fragments.add(new RichTextFragment(fragmentText.toString(), fragmentStartX,
                    fragmentWidth, fragmentStyle.getFont(), fragmentStyle.getColor(),
                    fragmentStyle.isUnderline(), fragmentStyle.isStrikeThrough()));
        }
        return new RichLine(textBuilder.toString(), currentX, fragments, copiedGlyphs);
    }

    private String buildRichText(List<RichGlyph> glyphs) {
        StringBuilder builder = new StringBuilder(glyphs.size());
        for (RichGlyph glyph : glyphs) {
            builder.append(glyph.getText());
        }
        return builder.toString();
    }

    private int resolveMaxRichWidth(List<RichLine> lines) {
        int maxWidth = 0;
        for (RichLine line : lines) {
            maxWidth = Math.max(maxWidth, line.getWidth());
        }
        return maxWidth;
    }

    private RichTokenType resolveRichTokenType(char current) {
        if (Character.isWhitespace(current)) {
            return RichTokenType.WHITESPACE;
        }
        if (isAsciiWordLikeChar(current)) {
            return RichTokenType.WORD;
        }
        if (isClosingPunctuation(current)) {
            return RichTokenType.CLOSING_PUNCTUATION;
        }
        if (isOpeningPunctuation(current)) {
            return RichTokenType.OPENING_PUNCTUATION;
        }
        return RichTokenType.TEXT;
    }

    private boolean isAsciiWordLikeChar(char current) {
        return current < 128 && !Character.isWhitespace(current) && !isAsciiBreakPunctuation(current);
    }

    private boolean isAsciiBreakPunctuation(char current) {
        return ",;!()[]{}<>\"".indexOf(current) >= 0;
    }

    private boolean isOpeningPunctuation(char current) {
        return "([{\"'“‘《「『【〈".indexOf(current) >= 0;
    }

    private boolean isClosingPunctuation(char current) {
        return ")]}\"'”’》」』】〉，。！？）；：、,.!?;:".indexOf(current) >= 0;
    }

    private static final class RichTokenLineBuffer {
        private final List<RichToken> tokens = new ArrayList<RichToken>();
        private int width = 0;

        private boolean isEmpty() {
            return this.tokens.isEmpty();
        }

        private boolean canAppend(RichToken token, int maxWidth, int letterSpacing) {
            int candidateWidth = this.width;
            if (!this.tokens.isEmpty() && !token.getGlyphs().isEmpty()) {
                candidateWidth += letterSpacing;
            }
            candidateWidth += token.getWidth();
            return candidateWidth <= maxWidth;
        }

        private void append(RichToken token, int letterSpacing) {
            if (!this.tokens.isEmpty() && !token.getGlyphs().isEmpty()) {
                this.width += letterSpacing;
            }
            this.tokens.add(token);
            this.width += token.getWidth();
        }

        private List<RichToken> getTokens() {
            return this.tokens;
        }

        private void clear() {
            this.tokens.clear();
            this.width = 0;
        }
    }
}
