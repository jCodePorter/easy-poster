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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class TextLayoutEngine {

    private static final ITextSplitter DEFAULT_SPLITTER = new TextSplitterSimpleImpl();
    private static final PlainTextWrapper PLAIN_WRAPPER = new PlainTextWrapper();
    private static final RichTextWrapper RICH_WRAPPER = new RichTextWrapper();
    private static final TextMetricsService METRICS = new TextMetricsService();
    private static final DecorationMetricsResolver DECORATION = new DecorationMetricsResolver();

    private final Map<String, TextLayoutResult> cache = new WeakHashMap<>();

    public TextLayoutResult layout(TextElementConfig config, Position position, int rotate,
                                   PosterContext context, int posterWidth, int posterHeight) {
        if (config.isEmpty()) {
            return createEmptyResult(position);
        }

        String cacheKey = buildCacheKey(config, position, posterWidth, posterHeight);
        synchronized (cache) {
            TextLayoutResult cached = cache.get(cacheKey);
            if (cached != null) return cached;

            TextLayoutResult result = computeLayout(config, position, rotate, context, posterWidth, posterHeight);
            cache.put(cacheKey, result);
            return result;
        }
    }

    private TextLayoutResult computeLayout(TextElementConfig config, Position position, int rotate,
                                           PosterContext context, int posterWidth, int posterHeight) {
        Font baseFont = resolveFont(config, context.getConfig());
        if (config.isVerticalLayout()) {
            if (config.isRichText()) {
                throw new PosterException("vertical layout does not support rich text yet");
            }
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
        List<String> columns = resolveVerticalColumns(config, lineHeight);
        int contentHeight = resolveVerticalContentHeight(columns, config, lineHeight);

        List<Integer> columnWidths = new ArrayList<>(columns.size());
        int contentWidth = 0;
        for (int i = 0; i < columns.size(); i++) {
            int width = measureVerticalColumnWidth(columns.get(i), fm);
            columnWidths.add(width);
            contentWidth += width;
            if (i > 0) {
                contentWidth += config.getColumnSpacing();
            }
        }

        TextDecorationInsets decorInsets = new TextDecorationInsets(0, 0, 0, 0);
        TextPaddingInsets paddingInsets = new TextPaddingInsets(
                config.getTextPadding().getMarginLeft(),
                config.getTextPadding().getMarginTop(),
                config.getTextPadding().getMarginRight(),
                config.getTextPadding().getMarginBottom()
        );
        int bgWidth = contentWidth + paddingInsets.getLeft() + paddingInsets.getRight();
        int bgHeight = contentHeight + paddingInsets.getTop() + paddingInsets.getBottom();
        int totalWidth = bgWidth + decorInsets.getLeft() + decorInsets.getRight();
        int totalHeight = bgHeight + decorInsets.getTop() + decorInsets.getBottom();

        Point blockPoint = resolvePosition(position, posterWidth, posterHeight, totalWidth, totalHeight,
                contentWidth, contentHeight, config.getBaseLine(), baselineOffset, lineHeight, decorInsets, paddingInsets,
                true);
        Point contentPoint = Point.of(
                blockPoint.getX() + decorInsets.getLeft() + paddingInsets.getLeft(),
                blockPoint.getY() + decorInsets.getTop() + paddingInsets.getTop()
        );
        List<LayoutLine> layoutLines = buildVerticalLines(columns, columnWidths, contentPoint, contentHeight,
                lineHeight, baselineOffset, fm, config.getVerticalAlign(), config.getVerticalDirection(),
                config.getColumnSpacing());

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

        if (config.isAutoFitText()) {
            throw new PosterException("rich text span does not support autoFitText yet");
        }
        if (resolvedTextAlign == TextAlign.JUSTIFY) {
            throw new PosterException("rich text span does not support justify yet");
        }

        int lineHeight = resolveRichLineHeight(config, baseFont, g);
        int baselineOffset = resolveRichBaselineOffset(config, baseFont, g, lineHeight);
        ResolvedRichTextLines resolvedLines = RICH_WRAPPER.resolveRichTextLines(
                buildRenderSpec(config, baseFont, resolvedTextAlign), g, getOverflowStrategy(config),
                createRichMeasurer(g));

        List<RichLine> richLines = resolvedLines.getLines();
        TextDecorationInsets decorInsets = DECORATION.resolveRichTextInsets(
                buildRenderSpec(config, baseFont, resolvedTextAlign), g, baseFont, lineHeight, baselineOffset, richLines);
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

        List<LayoutLine> layoutLines = buildRichLines(richLines, contentPoint, textWidth, resolvedTextAlign, resolvedLines);

        return new TextLayoutResult(
                baseFont, config.getBaseLine(), resolvedTextAlign, TextLayoutMode.HORIZONTAL, getOverflowStrategy(config),
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
                                            TextAlign align, ResolvedRichTextLines resolved) {
        List<LayoutLine> lines = new ArrayList<>(richLines.size());
        boolean clipOverflow = resolved.isClipOverflow();

        for (RichLine richLine : richLines) {
            int offsetX = align.offset(textWidth, richLine.getWidth());
            List<RichTextFragment> fragments = new ArrayList<>(richLine.getFragments().size());
            for (RichTextFragment fragment : richLine.getFragments()) {
                fragments.add(fragment.shiftX(offsetX));
            }
            int renderWidth = clipOverflow ? Math.min(richLine.getWidth(), textWidth) : richLine.getWidth();

            lines.add(new LayoutLine(richLine.getText(), richLine.getWidth(),
                    Point.of(contentPoint.getX(), contentPoint.getY()),
                    false, renderWidth, fragments));
        }
        return lines;
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

    private String buildCacheKey(TextElementConfig config, Position position, int posterWidth, int posterHeight) {
        return config.hashCode() + "|" + position + "|" + posterWidth + "|" + posterHeight;
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
        return new TextRenderSpec(
                config.getText(), config.getTextSpans(), null,
                java.awt.Color.BLACK, baseFont, config.getBaseLine(), config.getLineHeight(),
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

    private int resolveRichLineHeight(TextElementConfig config, Font baseFont, Graphics2D graphics) {
        if (config.getLineHeight() != null) {
            return config.getLineHeight();
        }
        int maxHeight = graphics.getFontMetrics(baseFont).getHeight();
        for (TextSpan span : config.getTextSpans()) {
            Font spanFont = resolveRichSpanFont(span, baseFont);
            maxHeight = Math.max(maxHeight, graphics.getFontMetrics(spanFont).getHeight());
        }
        return maxHeight;
    }

    private int resolveRichBaselineOffset(TextElementConfig config, Font baseFont, Graphics2D graphics, int lineHeight) {
        int maxOffset = METRICS.resolveBaselineOffset(graphics.getFontMetrics(baseFont), lineHeight);
        for (TextSpan span : config.getTextSpans()) {
            Font spanFont = resolveRichSpanFont(span, baseFont);
            maxOffset = Math.max(maxOffset,
                    METRICS.resolveBaselineOffset(graphics.getFontMetrics(spanFont), lineHeight));
        }
        return maxOffset;
    }

    private Font resolveRichSpanFont(TextSpan span, Font baseFont) {
        int resolvedStyle = span.getFontStyle() != null ? span.getFontStyle() : baseFont.getStyle();
        int resolvedSize = span.getFontSize() != null ? span.getFontSize() : Math.round(baseFont.getSize2D());
        if (resolvedStyle == baseFont.getStyle() && resolvedSize == Math.round(baseFont.getSize2D())) {
            return baseFont;
        }
        return baseFont.deriveFont(resolvedStyle, (float) resolvedSize);
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
