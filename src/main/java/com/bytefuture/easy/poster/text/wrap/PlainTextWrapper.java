package com.bytefuture.easy.poster.text.wrap;

import com.bytefuture.easy.poster.text.layout.TextRenderSpec;
import com.bytefuture.easy.poster.model.TextOverflowStrategy;
import com.bytefuture.easy.poster.text.split.ITextSplitter;
import com.bytefuture.easy.poster.text.split.SplitTextInfo;
import com.bytefuture.easy.poster.text.split.TextSplitRequest;
import com.bytefuture.easy.poster.text.split.TextSplitResult;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PlainTextWrapper {

    private static final EllipsisProcessor ELLIPSIS_PROCESSOR = new EllipsisProcessor();

    public interface Measurer {
        int measureLineWidth(String text, FontMetrics fontMetrics, Graphics2D graphics);

        int measureParagraphWidth(String text, FontMetrics fontMetrics, Graphics2D graphics);
    }

    public static final class ResolvedLines {
        private final List<SplitTextInfo> lines;
        private final int layoutWidth;
        private final boolean truncated;
        private final boolean clipOverflow;

        public ResolvedLines(List<SplitTextInfo> lines, int layoutWidth, boolean truncated, boolean clipOverflow) {
            this.lines = lines;
            this.layoutWidth = layoutWidth;
            this.truncated = truncated;
            this.clipOverflow = clipOverflow;
        }

        public List<SplitTextInfo> getLines() {
            return this.lines;
        }

        public int getLayoutWidth() {
            return this.layoutWidth;
        }

        public boolean isTruncated() {
            return this.truncated;
        }

        public boolean isClipOverflow() {
            return this.clipOverflow;
        }
    }

    public ResolvedLines resolveLines(TextRenderSpec spec, String content, FontMetrics fontMetrics, Graphics2D graphics,
                                      Font renderFont, ITextSplitter textSplitter, Measurer measurer) {
        if (content.isEmpty()) {
            return new ResolvedLines(Collections.singletonList(SplitTextInfo.of("", 0)), 0, false, false);
        }

        TextOverflowStrategy overflowStrategy = spec.getOverflowStrategy();
        int widthLimit = spec.resolveWidthLimit();
        int effectiveWrapWidth = resolveEffectiveWrapWidth(spec, content, fontMetrics, graphics, renderFont,
                overflowStrategy, widthLimit, measurer);
        List<SplitTextInfo> rawLines;
        if (overflowStrategy == TextOverflowStrategy.WRAP && effectiveWrapWidth > 0) {
            rawLines = resolveWrappedLines(content, effectiveWrapWidth, fontMetrics, graphics, textSplitter, measurer);
        } else if (containsLineBreak(content)) {
            rawLines = splitExplicitLines(content, fontMetrics, graphics, measurer);
        } else {
            rawLines = Collections.singletonList(SplitTextInfo.of(content,
                    measurer.measureLineWidth(content, fontMetrics, graphics)));
        }

        if (rawLines.isEmpty()) {
            rawLines = Collections.singletonList(SplitTextInfo.of("", 0));
        }

        List<SplitTextInfo> visibleLines = rawLines;
        if (overflowStrategy == TextOverflowStrategy.ELLIPSIS && widthLimit > 0) {
            visibleLines = ELLIPSIS_PROCESSOR.applyPlainWidthEllipsis(spec, visibleLines, widthLimit, fontMetrics, graphics,
                    new EllipsisProcessor.PlainTextMeasurer() {
                        @Override
                        public int measureLineWidth(String text, FontMetrics lineFontMetrics, Graphics2D lineGraphics) {
                            return measurer.measureLineWidth(text, lineFontMetrics, lineGraphics);
                        }
                    });
        }

        ResolvedLines limitedLines = applyMaxLines(spec, visibleLines,
                resolveLayoutWidth(visibleLines, overflowStrategy, widthLimit, effectiveWrapWidth),
                fontMetrics, graphics, measurer);
        if (overflowStrategy == TextOverflowStrategy.CLIP && widthLimit > 0) {
            return new ResolvedLines(limitedLines.getLines(), widthLimit, limitedLines.isTruncated(), true);
        }
        return limitedLines;
    }

    private List<SplitTextInfo> resolveWrappedLines(String content, int maxWidth, FontMetrics fontMetrics,
                                                    Graphics2D graphics, ITextSplitter textSplitter, Measurer measurer) {
        TextSplitResult result = textSplitter.split(TextSplitRequest.of(content, maxWidth, fontMetrics));
        return normalizeWrappedLines(result.getLines(), maxWidth, fontMetrics, graphics, measurer);
    }

    private List<SplitTextInfo> normalizeWrappedLines(List<SplitTextInfo> lines, int maxWidth,
                                                      FontMetrics fontMetrics, Graphics2D graphics, Measurer measurer) {
        List<SplitTextInfo> normalized = new ArrayList<SplitTextInfo>();
        for (SplitTextInfo line : lines) {
            String textValue = line.getText();
            int measuredWidth = measurer.measureLineWidth(textValue, fontMetrics, graphics);
            if (textValue == null || textValue.isEmpty() || measuredWidth <= maxWidth) {
                normalized.add(SplitTextInfo.of(textValue, measuredWidth));
                continue;
            }
            normalized.addAll(splitOverflowLine(textValue, maxWidth, fontMetrics, graphics, measurer));
        }
        return normalized;
    }

    private List<SplitTextInfo> splitOverflowLine(String lineText, int maxWidth,
                                                  FontMetrics fontMetrics, Graphics2D graphics, Measurer measurer) {
        List<SplitTextInfo> lines = new ArrayList<SplitTextInfo>();
        String remaining = lineText;
        while (!remaining.isEmpty()) {
            int lineEnd = findLineBreakIndex(remaining, maxWidth, fontMetrics, graphics, measurer);
            String rawSegment = remaining.substring(0, lineEnd);
            String visibleSegment = trimTrailingWhitespace(rawSegment);

            if (visibleSegment.isEmpty()) {
                visibleSegment = remaining.substring(0, 1);
                lineEnd = 1;
            }

            lines.add(SplitTextInfo.of(visibleSegment,
                    measurer.measureLineWidth(visibleSegment, fontMetrics, graphics)));

            int nextStart = lineEnd;
            while (nextStart < remaining.length() && Character.isWhitespace(remaining.charAt(nextStart))) {
                nextStart++;
            }
            remaining = remaining.substring(nextStart);
        }
        return lines;
    }

    private int findLineBreakIndex(String textValue, int maxWidth, FontMetrics fontMetrics,
                                   Graphics2D graphics, Measurer measurer) {
        int lastBreak = -1;
        int end = 0;
        while (end < textValue.length()) {
            String candidate = textValue.substring(0, end + 1);
            if (measurer.measureLineWidth(candidate, fontMetrics, graphics) > maxWidth) {
                break;
            }
            if (isWrapBreakCharacter(textValue.charAt(end))) {
                lastBreak = end + 1;
            }
            end++;
        }

        if (end == 0) {
            return 1;
        }
        if (end < textValue.length() && lastBreak > 0) {
            return lastBreak;
        }
        return end;
    }

    private ResolvedLines applyMaxLines(TextRenderSpec spec, List<SplitTextInfo> rawLines, int layoutWidth,
                                        FontMetrics fontMetrics, Graphics2D graphics, Measurer measurer) {
        if (spec.getMaxLines() == null || rawLines.size() <= spec.getMaxLines()) {
            return new ResolvedLines(rawLines, layoutWidth, false, false);
        }

        List<SplitTextInfo> visibleLines = new ArrayList<SplitTextInfo>(rawLines.subList(0, spec.getMaxLines()));
        int lastIndex = visibleLines.size() - 1;
        int widthLimit = layoutWidth > 0 ? layoutWidth : Integer.MAX_VALUE;
        visibleLines.set(lastIndex, ELLIPSIS_PROCESSOR.appendPlainEllipsis(spec, visibleLines.get(lastIndex), widthLimit,
                fontMetrics, graphics, new EllipsisProcessor.PlainTextMeasurer() {
                    @Override
                    public int measureLineWidth(String text, FontMetrics lineFontMetrics, Graphics2D lineGraphics) {
                        return measurer.measureLineWidth(text, lineFontMetrics, lineGraphics);
                    }
                }));

        int resolvedLayoutWidth = layoutWidth > 0 ? layoutWidth : resolveMaxWidth(visibleLines);
        return new ResolvedLines(visibleLines, resolvedLayoutWidth, true, false);
    }

    private int resolveEffectiveWrapWidth(TextRenderSpec spec, String content, FontMetrics fontMetrics,
                                          Graphics2D graphics, Font renderFont, TextOverflowStrategy overflowStrategy,
                                          int widthLimit, Measurer measurer) {
        int wrapWidth = overflowStrategy == TextOverflowStrategy.WRAP ? widthLimit : 0;
        if (!spec.isAutoFitText() || overflowStrategy != TextOverflowStrategy.WRAP) {
            return wrapWidth;
        }

        int paragraphWidth = measurer.measureParagraphWidth(content, fontMetrics, graphics);
        int floorSize = Math.max(1, Math.min(Math.max(1, Math.round(renderFont.getSize2D())),
                spec.getAutoFitMinFontSize()));
        boolean needsFallbackWrap = paragraphWidth > spec.getAutoFitTargetWidth()
                && Math.max(1, Math.round(renderFont.getSize2D())) <= floorSize;
        if (!needsFallbackWrap) {
            return wrapWidth;
        }
        if (wrapWidth <= 0) {
            return spec.getAutoFitTargetWidth();
        }
        return Math.min(wrapWidth, spec.getAutoFitTargetWidth());
    }

    private int resolveLayoutWidth(List<SplitTextInfo> lines, TextOverflowStrategy overflowStrategy,
                                   int widthLimit, int effectiveWrapWidth) {
        if (overflowStrategy == TextOverflowStrategy.WRAP && effectiveWrapWidth > 0) {
            return effectiveWrapWidth;
        }
        if ((overflowStrategy == TextOverflowStrategy.CLIP || overflowStrategy == TextOverflowStrategy.ELLIPSIS)
                && widthLimit > 0) {
            return widthLimit;
        }
        return resolveMaxWidth(lines);
    }

    private List<SplitTextInfo> splitExplicitLines(String content, FontMetrics fontMetrics,
                                                   Graphics2D graphics, Measurer measurer) {
        String[] segments = normalizeLineBreaks(content).split("\n", -1);
        List<SplitTextInfo> lines = new ArrayList<SplitTextInfo>(segments.length);
        for (String segment : segments) {
            lines.add(SplitTextInfo.of(segment, measurer.measureLineWidth(segment, fontMetrics, graphics)));
        }
        return lines;
    }

    private int resolveMaxWidth(List<SplitTextInfo> lines) {
        int maxWidth = 0;
        for (SplitTextInfo line : lines) {
            maxWidth = Math.max(maxWidth, line.getWidth());
        }
        return maxWidth;
    }

    private boolean containsLineBreak(String content) {
        return content.indexOf('\n') >= 0 || content.indexOf('\r') >= 0;
    }

    private String normalizeLineBreaks(String content) {
        return content.replace("\r\n", "\n").replace('\r', '\n');
    }

    private String trimTrailingWhitespace(String content) {
        int end = content.length();
        while (end > 0 && Character.isWhitespace(content.charAt(end - 1))) {
            end--;
        }
        return content.substring(0, end);
    }

    private boolean isWrapBreakCharacter(char current) {
        return Character.isWhitespace(current) || ",.;:!?-/\\".indexOf(current) >= 0;
    }
}
