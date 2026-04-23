package com.bytefuture.easy.poster.element.v2;

import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.Config;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextLayoutMode;
import com.bytefuture.easy.poster.model.TextOverflowStrategy;
import com.bytefuture.easy.poster.model.TextSpan;
import com.bytefuture.easy.poster.model.VerticalAlign;
import com.bytefuture.easy.poster.model.VerticalDirection;
import com.bytefuture.easy.poster.text.layout.LayoutLine;
import com.bytefuture.easy.poster.text.layout.TextDecorationInsets;
import com.bytefuture.easy.poster.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.text.layout.TextPaddingInsets;
import com.bytefuture.easy.poster.text.layout.TextRenderSpec;
import com.bytefuture.easy.poster.text.layout.VerticalGlyph;
import com.bytefuture.easy.poster.text.metrics.DecorationMetricsResolver;
import com.bytefuture.easy.poster.text.metrics.TextMetricsService;
import com.bytefuture.easy.poster.text.split.ITextSplitter;
import com.bytefuture.easy.poster.text.split.SplitTextInfo;
import com.bytefuture.easy.poster.text.split.TextSplitterSimpleImpl;
import com.bytefuture.easy.poster.text.wrap.PlainTextWrapper;
import com.bytefuture.easy.poster.text.wrap.ResolvedRichTextLines;
import com.bytefuture.easy.poster.text.wrap.RichGlyph;
import com.bytefuture.easy.poster.text.wrap.RichLine;
import com.bytefuture.easy.poster.text.wrap.RichTextFragment;
import com.bytefuture.easy.poster.text.wrap.RichTextWrapper;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TextLayoutEngine {

    private static final ITextSplitter DEFAULT_SPLITTER = new TextSplitterSimpleImpl();
    private static final PlainTextWrapper PLAIN_WRAPPER = new PlainTextWrapper();
    private static final RichTextWrapper RICH_WRAPPER = new RichTextWrapper();
    private static final TextMetricsService METRICS = new TextMetricsService();
    private static final DecorationMetricsResolver DECORATION = new DecorationMetricsResolver();

    public TextLayoutResult layout(TextElementConfig config, Position position, int rotate,
                                   PosterContext context, int posterWidth, int posterHeight) {
        if (config.isEmpty()) {
            return createEmptyResult(position);
        }
        return computeLayout(config, position, rotate, context, posterWidth, posterHeight);
    }

    private TextLayoutResult computeLayout(TextElementConfig config, Position position, int rotate,
                                           PosterContext context, int posterWidth, int posterHeight) {
        Font baseFont = resolveFont(config, context.getConfig());
        if (config.isVerticalLayout()) {
            return computeVerticalLayout(config, position, rotate, context, posterWidth, posterHeight, baseFont);
        }
        if (config.isRichText()) {
            return computeRichLayout(config, position, rotate, context, posterWidth, posterHeight, baseFont);
        }
        return computePlainLayout(config, position, rotate, context, posterWidth, posterHeight, baseFont);
    }

    private TextLayoutResult computePlainLayout(TextElementConfig config, Position position, int rotate,
                                                PosterContext context, int posterWidth, int posterHeight,
                                                Font baseFont) {
        Graphics2D g = context.getGraphics();
        TextAlign resolvedTextAlign = resolveTextAlign(config, position);
        String text = normalizeText(config.getText());
        Font renderFont = resolveRenderFont(config, text, baseFont, g);
        FontMetrics fm = g.getFontMetrics(renderFont);

        int lineHeight = config.getLineHeight() != null ? config.getLineHeight() : fm.getHeight();
        int baselineOffset = METRICS.resolveBaselineOffset(fm, lineHeight);

        ITextSplitter splitter = config.getTextSplitter() != null ? config.getTextSplitter() : DEFAULT_SPLITTER;
        PlainTextWrapper.ResolvedLines resolvedLines = PLAIN_WRAPPER.resolveLines(
                buildRenderSpec(config, baseFont, resolvedTextAlign), text, fm, g, renderFont, splitter,
                createPlainMeasurer(config));

        TextDecorationInsets decorInsets = DECORATION.resolveTextInsets(
                buildRenderSpec(config, baseFont, resolvedTextAlign), g, fm, lineHeight, baselineOffset);
        TextPaddingInsets paddingInsets = new TextPaddingInsets(
                config.getTextPadding().getMarginLeft(),
                config.getTextPadding().getMarginTop(),
                config.getTextPadding().getMarginRight(),
                config.getTextPadding().getMarginBottom()
        );

        int textWidth = resolvedLines.getLayoutWidth();
        int textHeight = lineHeight * resolvedLines.getLines().size();
        int bgWidth = textWidth + paddingInsets.getLeft() + paddingInsets.getRight();
        int bgHeight = textHeight + paddingInsets.getTop() + paddingInsets.getBottom();
        int totalWidth = bgWidth + decorInsets.getLeft() + decorInsets.getRight();
        int totalHeight = bgHeight + decorInsets.getTop() + decorInsets.getBottom();

        Point blockPoint = resolvePosition(position, posterWidth, posterHeight, totalWidth, totalHeight,
                textWidth, textHeight, config.getBaseLine(), baselineOffset, lineHeight, decorInsets, paddingInsets,
                false);
        Point contentPoint = Point.of(
                blockPoint.getX() + decorInsets.getLeft() + paddingInsets.getLeft(),
                blockPoint.getY() + decorInsets.getTop() + paddingInsets.getTop()
        );

        List<LayoutLine> layoutLines = buildPlainLines(resolvedLines, contentPoint, resolvedTextAlign);

        return new TextLayoutResult(
                renderFont, config.getBaseLine(), resolvedTextAlign, TextLayoutMode.HORIZONTAL, getOverflowStrategy(config),
                lineHeight, baselineOffset, blockPoint, totalWidth, totalHeight,
                textWidth, textHeight, bgWidth, bgHeight,
                layoutLines, resolvedLines.isTruncated(), resolvedLines.isClipOverflow(),
                decorInsets, paddingInsets
        );
    }

    private TextLayoutResult computeVerticalLayout(TextElementConfig config, Position position, int rotate,
                                                   PosterContext context, int posterWidth, int posterHeight,
                                                   Font baseFont) {
        Graphics2D g = context.getGraphics();
        Font renderFont = resolveFont(config, context.getConfig());
        FontMetrics fm = g.getFontMetrics(renderFont);
        int lineHeight = config.getLineHeight() != null ? config.getLineHeight() : fm.getHeight();
        int baselineOffset = METRICS.resolveBaselineOffset(fm, lineHeight);
        List<Integer> columnWidths = new ArrayList<>();
        int contentWidth = 0;
        int contentHeight;
        List<LayoutLine> layoutLines;

        TextDecorationInsets decorInsets = new TextDecorationInsets(0, 0, 0, 0);
        TextPaddingInsets paddingInsets = new TextPaddingInsets(
                config.getTextPadding().getMarginLeft(),
                config.getTextPadding().getMarginTop(),
                config.getTextPadding().getMarginRight(),
                config.getTextPadding().getMarginBottom()
        );
        int bgWidth = 0;
        int bgHeight = 0;
        int totalWidth = 0;
        int totalHeight = 0;
        Point blockPoint = Point.ORIGIN_COORDINATE;
        Point contentPoint = Point.ORIGIN_COORDINATE;
        if (config.isRichText()) {
            List<List<VerticalGlyph>> richColumns = resolveVerticalRichColumns(config, renderFont, lineHeight, g);
            contentHeight = resolveVerticalRichContentHeight(richColumns, config, lineHeight);
            for (int i = 0; i < richColumns.size(); i++) {
                int width = measureVerticalRichColumnWidth(richColumns.get(i));
                columnWidths.add(width);
                contentWidth += width;
                if (i > 0) {
                    contentWidth += config.getColumnSpacing();
                }
            }
            bgWidth = contentWidth + paddingInsets.getLeft() + paddingInsets.getRight();
            bgHeight = contentHeight + paddingInsets.getTop() + paddingInsets.getBottom();
            totalWidth = bgWidth + decorInsets.getLeft() + decorInsets.getRight();
            totalHeight = bgHeight + decorInsets.getTop() + decorInsets.getBottom();
            blockPoint = resolvePosition(position, posterWidth, posterHeight, totalWidth, totalHeight,
                    contentWidth, contentHeight, config.getBaseLine(), baselineOffset, lineHeight, decorInsets, paddingInsets,
                    true);
            contentPoint = Point.of(
                    blockPoint.getX() + decorInsets.getLeft() + paddingInsets.getLeft(),
                    blockPoint.getY() + decorInsets.getTop() + paddingInsets.getTop()
            );
            layoutLines = buildVerticalRichLines(richColumns, columnWidths, contentPoint, contentHeight,
                    lineHeight, config.getVerticalAlign(), config.getVerticalDirection(), config.getColumnSpacing());
        } else {
            List<String> columns = resolveVerticalColumns(config, lineHeight);
            contentHeight = resolveVerticalContentHeight(columns, config, lineHeight);
            for (int i = 0; i < columns.size(); i++) {
                int width = measureVerticalColumnWidth(columns.get(i), fm);
                columnWidths.add(width);
                contentWidth += width;
                if (i > 0) {
                    contentWidth += config.getColumnSpacing();
                }
            }
            bgWidth = contentWidth + paddingInsets.getLeft() + paddingInsets.getRight();
            bgHeight = contentHeight + paddingInsets.getTop() + paddingInsets.getBottom();
            totalWidth = bgWidth + decorInsets.getLeft() + decorInsets.getRight();
            totalHeight = bgHeight + decorInsets.getTop() + decorInsets.getBottom();
            blockPoint = resolvePosition(position, posterWidth, posterHeight, totalWidth, totalHeight,
                    contentWidth, contentHeight, config.getBaseLine(), baselineOffset, lineHeight, decorInsets, paddingInsets,
                    true);
            contentPoint = Point.of(
                    blockPoint.getX() + decorInsets.getLeft() + paddingInsets.getLeft(),
                    blockPoint.getY() + decorInsets.getTop() + paddingInsets.getTop()
            );
            layoutLines = buildVerticalLines(columns, columnWidths, contentPoint, contentHeight,
                    lineHeight, baselineOffset, fm, config.getVerticalAlign(), config.getVerticalDirection(),
                    config.getColumnSpacing());
        }

        return new TextLayoutResult(
                renderFont, config.getBaseLine(), TextAlign.LEFT, TextLayoutMode.VERTICAL, TextOverflowStrategy.VISIBLE,
                lineHeight, baselineOffset, blockPoint, totalWidth, totalHeight,
                contentWidth, contentHeight, bgWidth, bgHeight,
                layoutLines, false, false, decorInsets, paddingInsets
        );
    }

    private TextLayoutResult computeRichLayout(TextElementConfig config, Position position, int rotate,
                                               PosterContext context, int posterWidth, int posterHeight,
                                               Font baseFont) {
        Graphics2D g = context.getGraphics();
        TextAlign resolvedTextAlign = resolveTextAlign(config, position);

        TextRenderSpec renderSpec = config.isAutoFitText()
                ? resolveRichAutoFitRenderSpec(config, baseFont, g, resolvedTextAlign)
                : buildRenderSpec(config, baseFont, resolvedTextAlign);
        Font renderFont = renderSpec.getBaseFont();

        int lineHeight = resolveRichLineHeight(renderSpec, g);
        int baselineOffset = resolveRichBaselineOffset(renderSpec, g, lineHeight);
        ResolvedRichTextLines resolvedLines = RICH_WRAPPER.resolveRichTextLines(
                renderSpec, g, getOverflowStrategy(config), createRichMeasurer(g));

        List<RichLine> richLines = resolvedLines.getLines();
        TextDecorationInsets decorInsets = DECORATION.resolveRichTextInsets(
                renderSpec, g, renderFont, lineHeight, baselineOffset, richLines);
        TextPaddingInsets paddingInsets = new TextPaddingInsets(
                config.getTextPadding().getMarginLeft(),
                config.getTextPadding().getMarginTop(),
                config.getTextPadding().getMarginRight(),
                config.getTextPadding().getMarginBottom()
        );

        int textWidth = resolvedLines.getLayoutWidth();
        int textHeight = lineHeight * richLines.size();
        int bgWidth = textWidth + paddingInsets.getLeft() + paddingInsets.getRight();
        int bgHeight = textHeight + paddingInsets.getTop() + paddingInsets.getBottom();
        int totalWidth = bgWidth + decorInsets.getLeft() + decorInsets.getRight();
        int totalHeight = bgHeight + decorInsets.getTop() + decorInsets.getBottom();

        Point blockPoint = resolvePosition(position, posterWidth, posterHeight, totalWidth, totalHeight,
                textWidth, textHeight, config.getBaseLine(), baselineOffset, lineHeight, decorInsets, paddingInsets,
                false);
        Point contentPoint = Point.of(
                blockPoint.getX() + decorInsets.getLeft() + paddingInsets.getLeft(),
                blockPoint.getY() + decorInsets.getTop() + paddingInsets.getTop()
        );

        List<LayoutLine> layoutLines = buildRichLines(richLines, contentPoint, textWidth,
                resolvedTextAlign, resolvedLines, config.getLetterSpacing());

        return new TextLayoutResult(
                renderFont, config.getBaseLine(), resolvedTextAlign, TextLayoutMode.HORIZONTAL, getOverflowStrategy(config),
                lineHeight, baselineOffset, blockPoint, totalWidth, totalHeight,
                textWidth, textHeight, bgWidth, bgHeight,
                layoutLines, resolvedLines.isTruncated(), resolvedLines.isClipOverflow(),
                decorInsets, paddingInsets
        );
    }

    private Font resolveFont(TextElementConfig config, Config globalConfig) {
        if (config.getFont() != null) {
            return config.getFont();
        }
        Font baseFont = globalConfig.getFont();
        if (baseFont != null) {
            if (config.getFontName() != null) {
                return new Font(config.getFontName(), config.getFontStyle(), config.getFontSize());
            }
            if (config.getFontStyle() == baseFont.getStyle() && config.getFontSize() == baseFont.getSize()) {
                return baseFont;
            }
            return baseFont.deriveFont(config.getFontStyle(), (float) config.getFontSize());
        }
        return new Font(
                config.getFontName() != null ? config.getFontName() : globalConfig.getFontName(),
                config.getFontStyle(),
                config.getFontSize()
        );
    }

    private Font resolveRenderFont(TextElementConfig config, String text, Font baseFont, Graphics2D g) {
        if (!config.isAutoFitText() || config.getAutoFitTargetWidth() <= 0 || text.isEmpty()) {
            return baseFont;
        }

        int baseSize = baseFont.getSize();
        int minSize = Math.max(1, Math.min(baseSize, config.getAutoFitMinFontSize()));
        int baseWidth = METRICS.measureParagraphWidth(text, g.getFontMetrics(baseFont), g, config.getLetterSpacing());

        if (baseWidth <= config.getAutoFitTargetWidth() || baseSize == minSize) {
            return baseSize == minSize ? METRICS.deriveFont(baseFont, minSize) : baseFont;
        }

        Font bestFont = METRICS.deriveFont(baseFont, minSize);
        int low = minSize;
        int high = baseSize;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            Font candidate = METRICS.deriveFont(baseFont, mid);
            int width = METRICS.measureParagraphWidth(text, g.getFontMetrics(candidate), g, config.getLetterSpacing());
            if (width <= config.getAutoFitTargetWidth()) {
                bestFont = candidate;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return bestFont;
    }

    private Point resolvePosition(Position position, int posterWidth, int posterHeight,
                                  int totalWidth, int totalHeight, int contentWidth, int contentHeight,
                                  BaseLine baseLine, int baselineOffset, int lineHeight,
                                  TextDecorationInsets decorInsets, TextPaddingInsets paddingInsets,
                                  boolean verticalLayout) {
        if (position instanceof AbsolutePosition) {
            Point anchor = position.calculate(posterWidth, posterHeight, contentWidth, contentHeight);
            if (verticalLayout) {
                return Point.of(
                        anchor.getX() - decorInsets.getLeft() - paddingInsets.getLeft(),
                        anchor.getY() - decorInsets.getTop() - paddingInsets.getTop()
                );
            }
            int offsetY = resolveBaselineOffset(baseLine, baselineOffset, lineHeight);
            return Point.of(
                    anchor.getX() - decorInsets.getLeft() - paddingInsets.getLeft(),
                    anchor.getY() - offsetY - decorInsets.getTop() - paddingInsets.getTop()
            );
        }
        if (position != null) {
            return position.calculate(posterWidth, posterHeight, totalWidth, totalHeight);
        }
        return Point.ORIGIN_COORDINATE;
    }

    private int resolveBaselineOffset(BaseLine baseLine, int baselineOffset, int lineHeight) {
        switch (baseLine) {
            case TOP:
                return 0;
            case CENTER:
                return lineHeight / 2;
            case BOTTOM:
                return lineHeight;
            default:
                return baselineOffset;
        }
    }

    private List<LayoutLine> buildPlainLines(PlainTextWrapper.ResolvedLines resolved, Point contentPoint, TextAlign align) {
        List<LayoutLine> lines = new ArrayList<>(resolved.getLines().size());
        int layoutWidth = resolved.getLayoutWidth();
        boolean clipOverflow = resolved.isClipOverflow();

        for (int i = 0; i < resolved.getLines().size(); i++) {
            SplitTextInfo line = resolved.getLines().get(i);
            boolean justified = shouldJustify(align, line, i, resolved);
            int offsetX = justified ? 0 : align.offset(layoutWidth, line.getWidth());
            int renderWidth = justified ? layoutWidth : (clipOverflow ? Math.min(line.getWidth(), layoutWidth) : line.getWidth());

            lines.add(new LayoutLine(line.getText(), line.getWidth(),
                    Point.of(contentPoint.getX() + offsetX, contentPoint.getY()),
                    justified, renderWidth, null));
        }
        return lines;
    }

    private List<LayoutLine> buildVerticalLines(List<String> columns, List<Integer> columnWidths, Point contentPoint,
                                                int contentHeight, int lineHeight, int baselineOffset, FontMetrics fm,
                                                VerticalAlign verticalAlign, VerticalDirection verticalDirection,
                                                int columnSpacing) {
        List<LayoutLine> lines = new ArrayList<>(columns.size());
        if (columns.isEmpty()) {
            return lines;
        }

        int totalWidth = 0;
        for (int i = 0; i < columnWidths.size(); i++) {
            totalWidth += columnWidths.get(i);
            if (i > 0) {
                totalWidth += columnSpacing;
            }
        }

        int currentX = verticalDirection == VerticalDirection.LEFT_TO_RIGHT
                ? contentPoint.getX()
                : contentPoint.getX() + totalWidth;
        for (int i = 0; i < columns.size(); i++) {
            int columnWidth = columnWidths.get(i);
            if (verticalDirection == VerticalDirection.RIGHT_TO_LEFT) {
                currentX -= columnWidth;
            }
            List<VerticalGlyph> glyphs = buildVerticalGlyphs(columns.get(i), columnWidth, contentHeight, lineHeight,
                    baselineOffset, fm, verticalAlign);
            lines.add(new LayoutLine(columns.get(i), columnWidth, Point.of(currentX, contentPoint.getY()),
                    false, columnWidth, null, glyphs));
            if (verticalDirection == VerticalDirection.LEFT_TO_RIGHT) {
                currentX += columnWidth + columnSpacing;
            } else {
                currentX -= columnSpacing;
            }
        }
        return lines;
    }

    private List<LayoutLine> buildVerticalRichLines(List<List<VerticalGlyph>> columns, List<Integer> columnWidths,
                                                    Point contentPoint, int contentHeight, int lineHeight,
                                                    VerticalAlign verticalAlign, VerticalDirection verticalDirection,
                                                    int columnSpacing) {
        List<LayoutLine> lines = new ArrayList<>(columns.size());
        if (columns.isEmpty()) {
            return lines;
        }

        int totalWidth = 0;
        for (int i = 0; i < columnWidths.size(); i++) {
            totalWidth += columnWidths.get(i);
            if (i > 0) {
                totalWidth += columnSpacing;
            }
        }

        int currentX = verticalDirection == VerticalDirection.LEFT_TO_RIGHT
                ? contentPoint.getX()
                : contentPoint.getX() + totalWidth;
        for (int i = 0; i < columns.size(); i++) {
            int columnWidth = columnWidths.get(i);
            if (verticalDirection == VerticalDirection.RIGHT_TO_LEFT) {
                currentX -= columnWidth;
            }
            List<VerticalGlyph> glyphs = positionVerticalRichGlyphs(columns.get(i), columnWidth, contentHeight,
                    lineHeight, verticalAlign);
            StringBuilder text = new StringBuilder();
            for (VerticalGlyph glyph : glyphs) {
                text.append(glyph.getText());
            }
            lines.add(new LayoutLine(text.toString(), columnWidth, Point.of(currentX, contentPoint.getY()),
                    false, columnWidth, null, glyphs));
            if (verticalDirection == VerticalDirection.LEFT_TO_RIGHT) {
                currentX += columnWidth + columnSpacing;
            } else {
                currentX -= columnSpacing;
            }
        }
        return lines;
    }

    private List<VerticalGlyph> buildVerticalGlyphs(String column, int columnWidth, int contentHeight, int lineHeight,
                                                    int baselineOffset, FontMetrics fm, VerticalAlign verticalAlign) {
        if (column == null || column.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> glyphTexts = splitGlyphs(column);
        int glyphCount = glyphTexts.size();
        int naturalHeight = glyphCount * lineHeight;
        int startY = resolveVerticalStartY(verticalAlign, contentHeight, naturalHeight);
        double justifyStep = resolveJustifyStep(verticalAlign, glyphCount, contentHeight, lineHeight);

        List<VerticalGlyph> glyphs = new ArrayList<>(glyphCount);
        for (int i = 0; i < glyphCount; i++) {
            String glyphText = glyphTexts.get(i);
            int glyphWidth = fm.stringWidth(glyphText);
            int xOffset = Math.max(0, (columnWidth - glyphWidth) / 2);
            int yOffset = verticalAlign == VerticalAlign.JUSTIFY && glyphCount > 1
                    ? (int) Math.round(i * justifyStep)
                    : startY + i * lineHeight;
            glyphs.add(new VerticalGlyph(glyphText, xOffset, yOffset, glyphWidth));
        }
        return glyphs;
    }

    private List<VerticalGlyph> positionVerticalRichGlyphs(List<VerticalGlyph> glyphs, int columnWidth,
                                                           int contentHeight, int lineHeight,
                                                           VerticalAlign verticalAlign) {
        if (glyphs.isEmpty()) {
            return Collections.emptyList();
        }
        int naturalHeight = glyphs.size() * lineHeight;
        int startY = resolveVerticalStartY(verticalAlign, contentHeight, naturalHeight);
        double justifyStep = resolveJustifyStep(verticalAlign, glyphs.size(), contentHeight, lineHeight);
        List<VerticalGlyph> positioned = new ArrayList<>(glyphs.size());
        for (int i = 0; i < glyphs.size(); i++) {
            VerticalGlyph glyph = glyphs.get(i);
            int xOffset = Math.max(0, (columnWidth - glyph.getWidth()) / 2);
            int yOffset = verticalAlign == VerticalAlign.JUSTIFY && glyphs.size() > 1
                    ? (int) Math.round(i * justifyStep)
                    : startY + i * lineHeight;
            positioned.add(new VerticalGlyph(glyph.getText(), xOffset, yOffset, glyph.getWidth(),
                    glyph.getFont(), glyph.getColor(), glyph.isUnderline(), glyph.isStrikeThrough()));
        }
        return positioned;
    }

    private List<String> splitGlyphs(String text) {
        List<String> glyphs = new ArrayList<>();
        for (int i = 0; i < text.length(); ) {
            int codePoint = text.codePointAt(i);
            glyphs.add(new String(Character.toChars(codePoint)));
            i += Character.charCount(codePoint);
        }
        return glyphs;
    }

    private int resolveVerticalStartY(VerticalAlign verticalAlign, int contentHeight, int naturalHeight) {
        switch (verticalAlign) {
            case BOTTOM:
                return Math.max(0, contentHeight - naturalHeight);
            case MIDDLE:
                return Math.max(0, (contentHeight - naturalHeight) / 2);
            default:
                return 0;
        }
    }

    private double resolveJustifyStep(VerticalAlign verticalAlign, int glyphCount, int contentHeight, int lineHeight) {
        if (verticalAlign != VerticalAlign.JUSTIFY || glyphCount <= 1) {
            return lineHeight;
        }
        return (double) (contentHeight - lineHeight) / (glyphCount - 1);
    }

    private List<LayoutLine> buildRichLines(List<RichLine> richLines, Point contentPoint, int textWidth,
                                            TextAlign align, ResolvedRichTextLines resolved, int letterSpacing) {
        List<LayoutLine> lines = new ArrayList<>(richLines.size());
        boolean clipOverflow = resolved.isClipOverflow();

        for (int i = 0; i < richLines.size(); i++) {
            RichLine richLine = richLines.get(i);
            boolean justified = shouldJustify(align, richLine, i, richLines, textWidth);
            int offsetX = justified ? 0 : align.offset(textWidth, richLine.getWidth());
            List<RichTextFragment> fragments = justified
                    ? justifyRichFragments(richLine, textWidth, letterSpacing)
                    : shiftRichFragments(richLine.getFragments(), offsetX);
            int renderWidth = justified
                    ? textWidth
                    : (clipOverflow ? Math.min(richLine.getWidth(), textWidth) : richLine.getWidth());

            lines.add(new LayoutLine(richLine.getText(), richLine.getWidth(),
                    Point.of(contentPoint.getX(), contentPoint.getY()),
                    justified, renderWidth, fragments));
        }
        return lines;
    }

    private List<RichTextFragment> shiftRichFragments(List<RichTextFragment> fragments, int offsetX) {
        List<RichTextFragment> shifted = new ArrayList<>(fragments.size());
        for (RichTextFragment fragment : fragments) {
            shifted.add(fragment.shiftX(offsetX));
        }
        return shifted;
    }

    private boolean shouldJustify(TextAlign align, RichLine line, int index, List<RichLine> lines, int layoutWidth) {
        if (align != TextAlign.JUSTIFY) return false;
        if (index >= lines.size() - 1) return false;
        if (layoutWidth <= line.getWidth()) return false;
        return line.getText() != null && line.getText().contains(" ");
    }

    private List<RichTextFragment> justifyRichFragments(RichLine line, int layoutWidth, int letterSpacing) {
        List<RichTextFragment> segmented = splitFragmentsForJustify(line, letterSpacing);
        if (segmented.isEmpty()) {
            return segmented;
        }

        int totalExtra = layoutWidth - line.getWidth();
        int gapCount = countExpandableRichGaps(segmented);
        if (totalExtra <= 0 || gapCount <= 0) {
            return segmented;
        }

        int baseExtra = totalExtra / gapCount;
        int remainder = totalExtra % gapCount;
        int shiftX = 0;
        List<RichTextFragment> justified = new ArrayList<>(segmented.size());
        for (RichTextFragment fragment : segmented) {
            justified.add(fragment.shiftX(shiftX));
            if (fragment.getText().endsWith(" ")) {
                shiftX += baseExtra;
                if (remainder > 0) {
                    shiftX++;
                    remainder--;
                }
            }
        }
        return justified;
    }

    private List<RichTextFragment> splitFragmentsForJustify(RichLine line, int letterSpacing) {
        List<RichTextFragment> segmented = new ArrayList<>();
        List<RichGlyph> glyphs = line.getGlyphs();
        if (glyphs == null || glyphs.isEmpty()) {
            return segmented;
        }

        StringBuilder segmentText = new StringBuilder();
        RichGlyph segmentStyle = null;
        int segmentStartX = 0;
        int segmentWidth = 0;
        int currentX = 0;
        boolean firstGlyph = true;

        for (RichGlyph glyph : glyphs) {
            if (!firstGlyph) {
                currentX += letterSpacing;
            }

            if (segmentStyle == null || !segmentStyle.hasSameStyle(glyph)) {
                if (segmentStyle != null && segmentText.length() > 0) {
                    segmented.add(new RichTextFragment(segmentText.toString(), segmentStartX, segmentWidth,
                            segmentStyle.getFont(), segmentStyle.getColor(),
                            segmentStyle.getBackgroundColor(), segmentStyle.getShadow(),
                            segmentStyle.getStroke(), segmentStyle.getBaselineShift(),
                            segmentStyle.isUnderline(), segmentStyle.isStrikeThrough()));
                }
                segmentText = new StringBuilder();
                segmentStyle = glyph;
                segmentStartX = currentX;
                segmentWidth = 0;
            }

            segmentText.append(glyph.getText());
            segmentWidth = currentX + glyph.getWidth() - segmentStartX;

            if (" ".equals(glyph.getText())) {
                segmented.add(new RichTextFragment(segmentText.toString(), segmentStartX, segmentWidth,
                        segmentStyle.getFont(), segmentStyle.getColor(),
                        segmentStyle.getBackgroundColor(), segmentStyle.getShadow(),
                        segmentStyle.getStroke(), segmentStyle.getBaselineShift(),
                        segmentStyle.isUnderline(), segmentStyle.isStrikeThrough()));
                segmentText = new StringBuilder();
                segmentStyle = null;
                segmentWidth = 0;
            }

            currentX += glyph.getWidth();
            firstGlyph = false;
        }

        if (segmentStyle != null && segmentText.length() > 0) {
            segmented.add(new RichTextFragment(segmentText.toString(), segmentStartX, segmentWidth,
                    segmentStyle.getFont(), segmentStyle.getColor(),
                    segmentStyle.getBackgroundColor(), segmentStyle.getShadow(),
                    segmentStyle.getStroke(), segmentStyle.getBaselineShift(),
                    segmentStyle.isUnderline(), segmentStyle.isStrikeThrough()));
        }
        return segmented;
    }

    private int countExpandableRichGaps(List<RichTextFragment> fragments) {
        int gapCount = 0;
        for (RichTextFragment fragment : fragments) {
            if (fragment.getText().endsWith(" ")) {
                gapCount++;
            }
        }
        return gapCount;
    }

    private boolean shouldJustify(TextAlign align, SplitTextInfo line, int index, PlainTextWrapper.ResolvedLines resolved) {
        if (align != TextAlign.JUSTIFY) return false;
        if (index >= resolved.getLines().size() - 1) return false;
        if (resolved.getLayoutWidth() <= line.getWidth()) return false;
        return line.getText() != null && line.getText().contains(" ");
    }

    private TextOverflowStrategy getOverflowStrategy(TextElementConfig config) {
        if (config.getOverflowStrategy() != null) return config.getOverflowStrategy();
        if (config.isAutoWordWrap()) return TextOverflowStrategy.WRAP;
        return TextOverflowStrategy.VISIBLE;
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.replace("\r\n", "\n").replace('\r', '\n');
    }

    private TextLayoutResult createEmptyResult(Position position) {
        Point point = position != null ? position.calculate(0, 0, 0, 0) : Point.ORIGIN_COORDINATE;
        TextPaddingInsets padding = new TextPaddingInsets(0, 0, 0, 0);
        TextDecorationInsets decor = new TextDecorationInsets(0, 0, 0, 0);
        return new TextLayoutResult(
                new Font("SansSerif", Font.PLAIN, 16), BaseLine.BASE_LINE, TextAlign.LEFT, TextLayoutMode.HORIZONTAL,
                TextOverflowStrategy.VISIBLE, 16, 12, point, 0, 0, 0, 0, 0, 0,
                new ArrayList<LayoutLine>(), false, false, decor, padding
        );
    }

    private TextRenderSpec buildRenderSpec(TextElementConfig config, Font baseFont, TextAlign resolvedTextAlign) {
        return buildRenderSpec(config, baseFont, resolvedTextAlign, config.getTextSpans(), config.getLineHeight());
    }

    private TextRenderSpec buildRenderSpec(TextElementConfig config, Font baseFont, TextAlign resolvedTextAlign,
                                           List<TextSpan> textSpans, Integer lineHeight) {
        return new TextRenderSpec(
                config.getText(), textSpans, null,
                java.awt.Color.BLACK, baseFont, config.getBaseLine(), lineHeight,
                resolvedTextAlign, getOverflowStrategy(config), config.getMaxLines(),
                config.getEllipsis(), config.getShadow(), config.getStroke(),
                config.getLetterSpacing(), config.getTextBackgroundColor(), config.getTextPadding(),
                config.getTextBackgroundArcWidth(), config.getTextBackgroundArcHeight(),
                0, config.isAutoWordWrap(), config.getMaxTextWidth(),
                config.isAutoFitText(), config.getAutoFitTargetWidth(), config.getAutoFitMinFontSize(),
                config.isUnderline(), config.isStrikeThrough()
        );
    }

    private TextAlign resolveTextAlign(TextElementConfig config, Position position) {
        if (config.getTextAlign() != null) {
            return config.getTextAlign();
        }
        if (position instanceof RelativePosition) {
            Direction direction = ((RelativePosition) position).getDirection();
            if (direction == Direction.CENTER || direction == Direction.TOP_CENTER || direction == Direction.BOTTOM_CENTER) {
                return TextAlign.CENTER;
            }
            if (direction == Direction.TOP_RIGHT || direction == Direction.RIGHT_CENTER || direction == Direction.RIGHT_BOTTOM) {
                return TextAlign.RIGHT;
            }
        }
        return TextAlign.LEFT;
    }

    private PlainTextWrapper.Measurer createPlainMeasurer(final TextElementConfig config) {
        return new PlainTextWrapper.Measurer() {
            @Override
            public int measureLineWidth(String text, FontMetrics fm, Graphics2D g) {
                return METRICS.measureLineWidth(text, fm, g, config.getLetterSpacing());
            }

            @Override
            public int measureParagraphWidth(String text, FontMetrics fm, Graphics2D g) {
                return METRICS.measureParagraphWidth(text, fm, g, config.getLetterSpacing());
            }
        };
    }

    private RichTextWrapper.Measurer createRichMeasurer(Graphics2D g) {
        return new RichTextWrapper.Measurer() {
            @Override
            public int measureBaseStringWidth(String text, FontMetrics fm, Graphics2D graphics) {
                return METRICS.measureBaseStringWidth(text, fm, graphics);
            }

            @Override
            public int measureRichGlyphsWidth(List<RichGlyph> glyphs, int letterSpacing) {
                int width = 0;
                for (int i = 0; i < glyphs.size(); i++) {
                    if (i > 0) width += letterSpacing;
                    width += glyphs.get(i).getWidth();
                }
                return width;
            }

            @Override
            public String normalizeLineBreaks(String text) {
                return text.replace("\r\n", "\n").replace('\r', '\n');
            }
        };
    }

    private int resolveRichLineHeight(TextRenderSpec renderSpec, Graphics2D graphics) {
        if (renderSpec.getLineHeight() != null) {
            return renderSpec.getLineHeight();
        }
        Font baseFont = renderSpec.getBaseFont();
        int maxHeight = graphics.getFontMetrics(baseFont).getHeight();
        for (TextSpan span : renderSpec.getTextSpans()) {
            Font spanFont = resolveRichSpanFont(span, baseFont);
            maxHeight = Math.max(maxHeight, graphics.getFontMetrics(spanFont).getHeight());
        }
        return maxHeight;
    }

    private int resolveRichBaselineOffset(TextRenderSpec renderSpec, Graphics2D graphics, int lineHeight) {
        Font baseFont = renderSpec.getBaseFont();
        int maxOffset = METRICS.resolveBaselineOffset(graphics.getFontMetrics(baseFont), lineHeight);
        for (TextSpan span : renderSpec.getTextSpans()) {
            Font spanFont = resolveRichSpanFont(span, baseFont);
            maxOffset = Math.max(maxOffset,
                    METRICS.resolveBaselineOffset(graphics.getFontMetrics(spanFont), lineHeight));
        }
        return maxOffset;
    }

    private Font resolveRichSpanFont(TextSpan span, Font baseFont) {
        int resolvedStyle = span.getFontStyle() != null ? span.getFontStyle() : baseFont.getStyle();
        int resolvedSize = span.getFontSize() != null ? span.getFontSize() : Math.round(baseFont.getSize2D());
        if (span.getFontName() != null) {
            return new Font(span.getFontName(), resolvedStyle, resolvedSize);
        }
        if (resolvedStyle == baseFont.getStyle() && resolvedSize == Math.round(baseFont.getSize2D())) {
            return baseFont;
        }
        return baseFont.deriveFont(resolvedStyle, (float) resolvedSize);
    }

    private List<List<VerticalGlyph>> resolveVerticalRichColumns(TextElementConfig config, Font baseFont,
                                                                 int lineHeight, Graphics2D graphics) {
        List<List<VerticalGlyph>> rawColumns = new ArrayList<>();
        List<VerticalGlyph> currentColumn = new ArrayList<>();
        Color defaultColor = Color.BLACK;
        for (TextSpan span : config.getTextSpans()) {
            Font spanFont = resolveRichSpanFont(span, baseFont);
            Color spanColor = span.getColor() != null ? span.getColor() : defaultColor;
            boolean underline = span.getUnderline() != null ? span.getUnderline() : config.isUnderline();
            boolean strikeThrough = span.getStrikeThrough() != null ? span.getStrikeThrough() : config.isStrikeThrough();
            String normalized = normalizeText(span.getText());
            for (int i = 0; i < normalized.length(); ) {
                int codePoint = normalized.codePointAt(i);
                i += Character.charCount(codePoint);
                if (codePoint == '\n') {
                    rawColumns.add(currentColumn);
                    currentColumn = new ArrayList<>();
                    continue;
                }
                String glyphText = new String(Character.toChars(codePoint));
                int width = graphics.getFontMetrics(spanFont).stringWidth(glyphText);
                currentColumn.add(new VerticalGlyph(glyphText, 0, 0, width, spanFont, spanColor, underline, strikeThrough));
            }
        }
        rawColumns.add(currentColumn);

        int capacity = resolveVerticalCapacity(config, lineHeight);
        if (capacity == Integer.MAX_VALUE) {
            return rawColumns;
        }

        List<List<VerticalGlyph>> columns = new ArrayList<>();
        for (List<VerticalGlyph> rawColumn : rawColumns) {
            if (rawColumn.isEmpty()) {
                columns.add(new ArrayList<VerticalGlyph>());
                continue;
            }
            List<VerticalGlyph> current = new ArrayList<>(capacity);
            for (VerticalGlyph glyph : rawColumn) {
                current.add(glyph);
                if (current.size() >= capacity) {
                    columns.add(current);
                    current = new ArrayList<>(capacity);
                }
            }
            if (!current.isEmpty()) {
                columns.add(current);
            }
        }
        return columns;
    }

    private int resolveVerticalRichContentHeight(List<List<VerticalGlyph>> columns, TextElementConfig config, int lineHeight) {
        if (config.getLayoutHeight() > 0) {
            return config.getLayoutHeight();
        }
        int maxGlyphCount = 0;
        for (List<VerticalGlyph> column : columns) {
            maxGlyphCount = Math.max(maxGlyphCount, column.size());
        }
        return maxGlyphCount * lineHeight;
    }

    private int measureVerticalRichColumnWidth(List<VerticalGlyph> column) {
        int width = 0;
        for (VerticalGlyph glyph : column) {
            width = Math.max(width, glyph.getWidth());
        }
        return width;
    }

    private TextRenderSpec resolveRichAutoFitRenderSpec(TextElementConfig config, Font baseFont, Graphics2D g,
                                                        TextAlign resolvedTextAlign) {
        TextRenderSpec baseSpec = buildRenderSpec(config, baseFont, resolvedTextAlign);
        ResolvedRichTextLines baseLines = RICH_WRAPPER.resolveRichTextLines(
                baseSpec, g, getOverflowStrategy(config), createRichMeasurer(g));
        if (baseLines.getLayoutWidth() <= config.getAutoFitTargetWidth()) {
            return baseSpec;
        }

        float minScale = resolveRichAutoFitMinScale(config, baseFont);
        TextRenderSpec minSpec = scaleRichRenderSpec(config, baseFont, resolvedTextAlign, minScale);
        ResolvedRichTextLines minLines = RICH_WRAPPER.resolveRichTextLines(
                minSpec, g, getOverflowStrategy(config), createRichMeasurer(g));
        if (minLines.getLayoutWidth() > config.getAutoFitTargetWidth()) {
            return minSpec;
        }

        float low = minScale;
        float high = 1F;
        TextRenderSpec bestSpec = minSpec;
        for (int i = 0; i < 12; i++) {
            float mid = (low + high) / 2F;
            TextRenderSpec candidateSpec = scaleRichRenderSpec(config, baseFont, resolvedTextAlign, mid);
            ResolvedRichTextLines candidateLines = RICH_WRAPPER.resolveRichTextLines(
                    candidateSpec, g, getOverflowStrategy(config), createRichMeasurer(g));
            if (candidateLines.getLayoutWidth() <= config.getAutoFitTargetWidth()) {
                bestSpec = candidateSpec;
                low = mid;
            } else {
                high = mid;
            }
        }
        return bestSpec;
    }

    private float resolveRichAutoFitMinScale(TextElementConfig config, Font baseFont) {
        int minFontSize = config.getAutoFitMinFontSize();
        float minScale = Math.min(1F, minFontSize / Math.max(1F, baseFont.getSize2D()));
        for (TextSpan span : config.getTextSpans()) {
            if (span.getFontSize() != null) {
                minScale = Math.max(minScale, minFontSize / (float) span.getFontSize());
            }
        }
        return Math.min(1F, minScale);
    }

    private TextRenderSpec scaleRichRenderSpec(TextElementConfig config, Font baseFont,
                                               TextAlign resolvedTextAlign, float scale) {
        Font scaledBaseFont = METRICS.deriveFont(baseFont,
                Math.max(config.getAutoFitMinFontSize(), Math.round(baseFont.getSize2D() * scale)));
        Integer scaledLineHeight = config.getLineHeight() == null
                ? null
                : Math.max(1, Math.round(config.getLineHeight() * scale));
        List<TextSpan> scaledSpans = scaleTextSpans(config.getTextSpans(), scale, config.getAutoFitMinFontSize());
        return buildRenderSpec(config, scaledBaseFont, resolvedTextAlign, scaledSpans, scaledLineHeight);
    }

    private List<TextSpan> scaleTextSpans(List<TextSpan> sourceSpans, float scale, int minFontSize) {
        List<TextSpan> scaled = new ArrayList<>(sourceSpans.size());
        for (TextSpan span : sourceSpans) {
            TextSpan scaledSpan = TextSpan.of(span.getText());
            if (span.getColor() != null) {
                scaledSpan.setColor(span.getColor());
            }
            if (span.getFontName() != null) {
                scaledSpan.setFontName(span.getFontName());
            }
            if (span.getFontStyle() != null) {
                scaledSpan.setFontStyle(span.getFontStyle());
            }
            if (span.getFontSize() != null) {
                scaledSpan.setFontSize(Math.max(minFontSize, Math.round(span.getFontSize() * scale)));
            }
            if (span.getBackgroundColor() != null) {
                scaledSpan.setBackgroundColor(span.getBackgroundColor());
            }
            if (span.getShadow() != null) {
                scaledSpan.setShadow(span.getShadow());
            }
            if (span.getStroke() != null) {
                scaledSpan.setStroke(span.getStroke());
            }
            if (span.getBaselineShift() != null) {
                scaledSpan.setBaselineShift(Math.round(span.getBaselineShift() * scale));
            }
            if (span.getUnderline() != null) {
                scaledSpan.setUnderline(span.getUnderline());
            }
            if (span.getStrikeThrough() != null) {
                scaledSpan.setStrikeThrough(span.getStrikeThrough());
            }
            scaled.add(scaledSpan);
        }
        return scaled;
    }

    private List<String> resolveVerticalColumns(TextElementConfig config, int lineHeight) {
        if (!config.getVerticalColumns().isEmpty()) {
            return config.getVerticalColumns();
        }
        String text = normalizeText(config.getVerticalText());
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        String[] explicitColumns = text.split("\n", -1);
        if (explicitColumns.length > 1) {
            List<String> columns = new ArrayList<>(explicitColumns.length);
            Collections.addAll(columns, explicitColumns);
            return columns;
        }
        int capacity = resolveVerticalCapacity(config, lineHeight);
        List<String> columns = new ArrayList<>();
        List<String> glyphs = splitGlyphs(text);
        StringBuilder builder = new StringBuilder();
        for (String glyph : glyphs) {
            builder.append(glyph);
            if (builder.length() >= capacity) {
                columns.add(builder.toString());
                builder.setLength(0);
            }
        }
        if (builder.length() > 0) {
            columns.add(builder.toString());
        }
        return columns;
    }

    private int resolveVerticalCapacity(TextElementConfig config, int lineHeight) {
        if (config.getLayoutHeight() <= 0) {
            return Integer.MAX_VALUE;
        }
        return Math.max(1, config.getLayoutHeight() / lineHeight);
    }

    private int resolveVerticalContentHeight(List<String> columns, TextElementConfig config, int lineHeight) {
        if (config.getLayoutHeight() > 0) {
            return config.getLayoutHeight();
        }
        int maxGlyphCount = 0;
        for (String column : columns) {
            maxGlyphCount = Math.max(maxGlyphCount, splitGlyphs(column).size());
        }
        return maxGlyphCount * lineHeight;
    }

    private int measureVerticalColumnWidth(String column, FontMetrics fm) {
        int width = 0;
        for (String glyph : splitGlyphs(column)) {
            width = Math.max(width, fm.stringWidth(glyph));
        }
        return width;
    }
}
