package com.bytefuture.easy.poster.element.basic;

import com.bytefuture.easy.poster.element.AbstractRepeatableElement;
import com.bytefuture.easy.poster.element.IElement;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextOverflowStrategy;
import com.bytefuture.easy.poster.model.TextShadow;
import com.bytefuture.easy.poster.model.TextSpan;
import com.bytefuture.easy.poster.model.TextStroke;
import com.bytefuture.easy.poster.text.layout.TextLayoutEngine;
import com.bytefuture.easy.poster.text.layout.LayoutLine;
import com.bytefuture.easy.poster.text.layout.TextDecorationInsets;
import com.bytefuture.easy.poster.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.text.layout.TextPaddingInsets;
import com.bytefuture.easy.poster.text.layout.TextRenderSpec;
import com.bytefuture.easy.poster.text.layout.TextRenderSpecFactory;
import com.bytefuture.easy.poster.text.metrics.DecorationMetricsResolver;
import com.bytefuture.easy.poster.text.metrics.TextMetricsService;
import com.bytefuture.easy.poster.text.paint.TextPainter;
import com.bytefuture.easy.poster.text.split.ITextSplitter;
import com.bytefuture.easy.poster.text.split.SplitTextInfo;
import com.bytefuture.easy.poster.text.split.TextSplitterSimpleImpl;
import com.bytefuture.easy.poster.text.wrap.PlainTextWrapper;
import com.bytefuture.easy.poster.text.wrap.ResolvedRichTextLines;
import com.bytefuture.easy.poster.text.wrap.RichGlyph;
import com.bytefuture.easy.poster.text.wrap.RichLine;
import com.bytefuture.easy.poster.text.wrap.RichTextFragment;
import com.bytefuture.easy.poster.text.wrap.RichTextWrapper;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Upgraded text element with a side-effect free layout pipeline.
 */
@Getter
public class EnhanceTextElement extends AbstractRepeatableElement<EnhanceTextElement> implements IElement {

    private static final ITextSplitter DEFAULT_TEXT_SPLITTER = new TextSplitterSimpleImpl();
    private static final TextLayoutEngine LAYOUT_ENGINE = new TextLayoutEngine();
    private static final TextPainter TEXT_PAINTER = new TextPainter();
    private static final PlainTextWrapper PLAIN_TEXT_LAYOUT_PROCESSOR = new PlainTextWrapper();
    private static final RichTextWrapper RICH_TEXT_LAYOUT_PROCESSOR = new RichTextWrapper();
    private static final TextMetricsService TEXT_METRICS = new TextMetricsService();
    private static final DecorationMetricsResolver DECORATION_METRICS = new DecorationMetricsResolver();

    private final String text;

    private String fontName;

    private Integer fontStyle;

    private Integer fontSize;

    private Font font;

    private BaseLine baseLine;

    private Integer lineHeight;

    private boolean autoWordWrap = false;

    private int maxTextWidth;

    private boolean autoFitText = false;

    private int autoFitTargetWidth;

    private int autoFitMinFontSize;

    private boolean strikeThrough = false;

    private boolean underline = false;

    private TextAlign textAlign;

    private TextOverflowStrategy overflowStrategy;

    private Integer maxLines;

    private String ellipsis = "...";

    private TextShadow shadow;

    private TextStroke stroke;

    private int letterSpacing = 0;

    private Color textBackgroundColor;

    private Margin textPadding = Margin.of(0);

    private int textBackgroundArcWidth = 0;

    private int textBackgroundArcHeight = 0;

    private final List<TextSpan> textSpans = new ArrayList<TextSpan>();

    private ITextSplitter textSplitter = DEFAULT_TEXT_SPLITTER;

    public EnhanceTextElement(String text) {
        this.text = text;
    }

    public static EnhanceTextElement of(String text) {
        return new EnhanceTextElement(text);
    }

    public static EnhanceTextElement richText(TextSpan... spans) {
        EnhanceTextElement element = new EnhanceTextElement("");
        if (spans != null) {
            for (TextSpan span : spans) {
                element.appendTextSpan(span);
            }
        }
        return element;
    }

    public EnhanceTextElement setFontName(String fontName) {
        this.fontName = fontName;
        return this;
    }

    public EnhanceTextElement setFontSize(int fontSize) {
        validatePositive("fontSize", fontSize);
        this.fontSize = fontSize;
        return this;
    }

    public EnhanceTextElement setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
        return this;
    }

    public EnhanceTextElement setFont(String fontName, int fontStyle, int fontSize) {
        validatePositive("fontSize", fontSize);
        this.fontName = fontName;
        this.fontStyle = fontStyle;
        this.fontSize = fontSize;
        return this;
    }

    public EnhanceTextElement setFont(Font font) {
        if (font == null) {
            throw new PosterException("font can not be null");
        }
        this.font = font;
        return this;
    }

    public EnhanceTextElement setBaseLine(BaseLine baseLine) {
        this.baseLine = baseLine;
        return this;
    }

    public EnhanceTextElement setLineHeight(Integer lineHeight) {
        if (lineHeight != null) {
            validatePositive("lineHeight", lineHeight);
        }
        this.lineHeight = lineHeight;
        return this;
    }

    public EnhanceTextElement setAutoWrapText(int maxTextWidth) {
        validatePositive("maxTextWidth", maxTextWidth);
        this.autoWordWrap = true;
        this.maxTextWidth = maxTextWidth;
        return this;
    }

    public EnhanceTextElement setLayoutWidth(int layoutWidth) {
        validatePositive("layoutWidth", layoutWidth);
        this.maxTextWidth = layoutWidth;
        return this;
    }

    public EnhanceTextElement setAutoFitText(int targetWidth, int minFontSize) {
        validatePositive("targetWidth", targetWidth);
        validatePositive("minFontSize", minFontSize);
        this.autoFitText = true;
        this.autoFitTargetWidth = targetWidth;
        this.autoFitMinFontSize = minFontSize;
        return this;
    }

    public EnhanceTextElement setStrikeThrough(boolean strikeThrough) {
        this.strikeThrough = strikeThrough;
        return this;
    }

    public EnhanceTextElement setUnderline(boolean underline) {
        this.underline = underline;
        return this;
    }

    public EnhanceTextElement setLetterSpacing(int letterSpacing) {
        this.letterSpacing = letterSpacing;
        return this;
    }

    public EnhanceTextElement setTextAlign(TextAlign textAlign) {
        if (textAlign == null) {
            throw new PosterException("textAlign can not be null");
        }
        this.textAlign = textAlign;
        return this;
    }

    public EnhanceTextElement setOverflowStrategy(TextOverflowStrategy overflowStrategy) {
        if (overflowStrategy == null) {
            throw new PosterException("overflowStrategy can not be null");
        }
        this.overflowStrategy = overflowStrategy;
        return this;
    }

    public EnhanceTextElement setMaxLines(int maxLines) {
        validatePositive("maxLines", maxLines);
        this.maxLines = maxLines;
        return this;
    }

    public EnhanceTextElement setEllipsis(String ellipsis) {
        if (ellipsis == null) {
            throw new PosterException("ellipsis can not be null");
        }
        this.ellipsis = ellipsis;
        return this;
    }

    public EnhanceTextElement setShadow(Color color, int offsetX, int offsetY) {
        if (color == null) {
            throw new PosterException("shadow color can not be null");
        }
        this.shadow = TextShadow.of(color, offsetX, offsetY);
        return this;
    }

    public EnhanceTextElement setShadow(TextShadow shadow) {
        if (shadow == null) {
            throw new PosterException("shadow can not be null");
        }
        this.shadow = shadow;
        return this;
    }

    public EnhanceTextElement setStroke(Color color, float width) {
        if (color == null) {
            throw new PosterException("stroke color can not be null");
        }
        if (width <= 0) {
            throw new PosterException("stroke width must be greater than 0");
        }
        this.stroke = TextStroke.of(color, width);
        return this;
    }

    public EnhanceTextElement setStroke(TextStroke stroke) {
        if (stroke == null) {
            throw new PosterException("stroke can not be null");
        }
        if (stroke.getWidth() <= 0) {
            throw new PosterException("stroke width must be greater than 0");
        }
        this.stroke = stroke;
        return this;
    }

    public EnhanceTextElement setTextBackground(Color textBackgroundColor) {
        if (textBackgroundColor == null) {
            throw new PosterException("textBackgroundColor can not be null");
        }
        this.textBackgroundColor = textBackgroundColor;
        return this;
    }

    public EnhanceTextElement setTextBackground(Color textBackgroundColor, int padding) {
        return this.setTextBackground(textBackgroundColor).setTextPadding(padding);
    }

    public EnhanceTextElement setTextBackground(Color textBackgroundColor, Margin textPadding) {
        return this.setTextBackground(textBackgroundColor).setTextPadding(textPadding);
    }

    public EnhanceTextElement setTextPadding(int padding) {
        validateNonNegative("textPadding", padding);
        this.textPadding = Margin.of(padding);
        return this;
    }

    public EnhanceTextElement setTextPadding(int paddingLeftRight, int paddingTopBottom) {
        validateNonNegative("paddingLeftRight", paddingLeftRight);
        validateNonNegative("paddingTopBottom", paddingTopBottom);
        this.textPadding = Margin.of(paddingLeftRight, paddingTopBottom);
        return this;
    }

    public EnhanceTextElement setTextPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        validateNonNegative("paddingLeft", paddingLeft);
        validateNonNegative("paddingTop", paddingTop);
        validateNonNegative("paddingRight", paddingRight);
        validateNonNegative("paddingBottom", paddingBottom);
        this.textPadding = Margin.of(paddingLeft, paddingTop, paddingRight, paddingBottom);
        return this;
    }

    public EnhanceTextElement setTextPadding(Margin textPadding) {
        if (textPadding == null) {
            throw new PosterException("textPadding can not be null");
        }
        validateNonNegative("paddingLeft", textPadding.getMarginLeft());
        validateNonNegative("paddingTop", textPadding.getMarginTop());
        validateNonNegative("paddingRight", textPadding.getMarginRight());
        validateNonNegative("paddingBottom", textPadding.getMarginBottom());
        this.textPadding = copyMargin(textPadding);
        return this;
    }

    public EnhanceTextElement setTextBackgroundArc(int arc) {
        validateNonNegative("textBackgroundArc", arc);
        this.textBackgroundArcWidth = arc;
        this.textBackgroundArcHeight = arc;
        return this;
    }

    public EnhanceTextElement setTextBackgroundArc(int arcWidth, int arcHeight) {
        validateNonNegative("textBackgroundArcWidth", arcWidth);
        validateNonNegative("textBackgroundArcHeight", arcHeight);
        this.textBackgroundArcWidth = arcWidth;
        this.textBackgroundArcHeight = arcHeight;
        return this;
    }

    public EnhanceTextElement appendTextSpan(TextSpan textSpan) {
        if (textSpan == null) {
            throw new PosterException("textSpan can not be null");
        }
        this.textSpans.add(textSpan);
        return this;
    }

    public EnhanceTextElement setTextSpans(List<TextSpan> textSpans) {
        this.textSpans.clear();
        if (textSpans == null) {
            return this;
        }
        for (TextSpan textSpan : textSpans) {
            appendTextSpan(textSpan);
        }
        return this;
    }

    public EnhanceTextElement setTextSplitter(ITextSplitter textSplitter) {
        if (textSplitter == null) {
            throw new PosterException("textSplitter can not be null");
        }
        this.textSplitter = textSplitter;
        return this;
    }

    @Override
    public Dimension calculateDimension(PosterContext context, int posterWidth, int posterHeight) {
        return LAYOUT_ENGINE.layout(this, context, posterWidth, posterHeight).toDimension(this.rotate);
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        TextLayoutResult layout = LAYOUT_ENGINE.layout(this, context, posterWidth, posterHeight);
        return TEXT_PAINTER.paint(this, context, dimension, layout);
    }

    @Override
    public void beforeRender(PosterContext context) {
        super.beforeRender(context);
        Graphics2D graphics = context.getGraphics();
        TextRenderSpec spec = TextRenderSpecFactory.from(this, context.getConfig());
        graphics.setColor(spec.getColor());
        graphics.setFont(spec.getBaseFont());
    }

    @Override
    public void debug(PosterContext context, Dimension dimension) {
        // Text debug rectangles are rendered line by line during drawing.
    }

    public void applyGradient(PosterContext context, Dimension dimension) {
        super.gradient(context, dimension);
    }

    private TextLayoutResult measureLayout(PosterContext context, int posterWidth, int posterHeight) {
        return LAYOUT_ENGINE.layout(this, context, posterWidth, posterHeight);
    }

    public TextLayoutResult measureLayoutInternal(TextRenderSpec spec, PosterContext context, int posterWidth, int posterHeight) {
        if (spec.hasRichTextSpans()) {
            return measureRichLayoutInternal(spec, context, posterWidth, posterHeight);
        }

        Graphics2D graphics = context.getGraphics();

        String normalizedText = spec.normalizedText();
        Font baseFont = spec.getBaseFont();
        Font renderFont = resolveRenderFont(spec, normalizedText, baseFont, graphics);
        FontMetrics fontMetrics = graphics.getFontMetrics(renderFont);

        int resolvedLineHeight = resolveLineHeight(spec, fontMetrics);
        int baselineOffset = TEXT_METRICS.resolveBaselineOffset(fontMetrics, resolvedLineHeight);
        BaseLine resolvedBaseLine = spec.getBaseLine();
        TextAlign resolvedTextAlign = spec.getTextAlign();
        TextOverflowStrategy resolvedOverflowStrategy = spec.getOverflowStrategy();
        PlainTextWrapper.ResolvedLines resolvedTextLines = resolveLines(spec, normalizedText, fontMetrics, graphics, renderFont, resolvedOverflowStrategy);
        TextDecorationInsets decorationInsets = DECORATION_METRICS.resolveTextInsets(spec, graphics, fontMetrics, resolvedLineHeight, baselineOffset);
        TextPaddingInsets paddingInsets = resolveTextPaddingInsets(spec);

        int textWidth = resolvedTextLines.getLayoutWidth();
        int textHeight = resolvedLineHeight * resolvedTextLines.getLines().size();
        int backgroundWidth = textWidth + paddingInsets.getLeft() + paddingInsets.getRight();
        int backgroundHeight = textHeight + paddingInsets.getTop() + paddingInsets.getBottom();
        int totalWidth = backgroundWidth + decorationInsets.getLeft() + decorationInsets.getRight();
        int totalHeight = backgroundHeight + decorationInsets.getTop() + decorationInsets.getBottom();
        Point blockPoint = resolveBlockPoint(posterWidth, posterHeight, totalWidth, totalHeight,
                resolvedBaseLine, baselineOffset, resolvedLineHeight, decorationInsets, paddingInsets);
        Point contentPoint = Point.of(
                blockPoint.getX() + decorationInsets.getLeft() + paddingInsets.getLeft(),
                blockPoint.getY() + decorationInsets.getTop() + paddingInsets.getTop()
        );

        List<LayoutLine> layoutLines = new ArrayList<LayoutLine>(resolvedTextLines.getLines().size());
        for (int i = 0; i < resolvedTextLines.getLines().size(); i++) {
            SplitTextInfo line = resolvedTextLines.getLines().get(i);
            boolean justified = shouldJustifyLine(resolvedTextAlign, line, i, resolvedTextLines);
            int offsetX = justified ? 0 : resolvedTextAlign.offset(resolvedTextLines.getLayoutWidth(), line.getWidth());
            int renderWidth = resolveLineRenderWidth(line, resolvedTextLines.getLayoutWidth(), justified, resolvedTextLines.isClipOverflow());
            layoutLines.add(new LayoutLine(line.getText(), line.getWidth(),
                    Point.of(contentPoint.getX() + offsetX, contentPoint.getY()), justified, renderWidth, null));
        }

        return new TextLayoutResult(
                renderFont,
                resolvedBaseLine,
                resolvedTextAlign,
                resolvedOverflowStrategy,
                resolvedLineHeight,
                baselineOffset,
                blockPoint,
                totalWidth,
                totalHeight,
                textWidth,
                textHeight,
                backgroundWidth,
                backgroundHeight,
                layoutLines,
                resolvedTextLines.isTruncated(),
                resolvedTextLines.isClipOverflow(),
                decorationInsets,
                paddingInsets
        );
    }

    private TextLayoutResult measureRichLayoutInternal(TextRenderSpec spec, PosterContext context, int posterWidth, int posterHeight) {
        validateRichTextConfig(spec);

        Graphics2D graphics = context.getGraphics();
        Font baseFont = spec.getBaseFont();
        int resolvedLineHeight = resolveLineHeight(spec, graphics.getFontMetrics(baseFont));
        int baselineOffset = TEXT_METRICS.resolveBaselineOffset(graphics.getFontMetrics(baseFont), resolvedLineHeight);
        BaseLine resolvedBaseLine = spec.getBaseLine();
        TextAlign resolvedTextAlign = spec.getTextAlign();
        TextOverflowStrategy resolvedOverflowStrategy = spec.getOverflowStrategy();
        ResolvedRichTextLines resolvedRichTextLines = resolveRichTextLines(spec, graphics, resolvedOverflowStrategy);
        List<RichLine> richLines = resolvedRichTextLines.getLines();
        TextDecorationInsets decorationInsets = DECORATION_METRICS.resolveRichTextInsets(spec, graphics, baseFont, resolvedLineHeight, baselineOffset, richLines);
        TextPaddingInsets paddingInsets = resolveTextPaddingInsets(spec);

        int textWidth = resolvedRichTextLines.getLayoutWidth();
        int textHeight = resolvedLineHeight * richLines.size();
        int backgroundWidth = textWidth + paddingInsets.getLeft() + paddingInsets.getRight();
        int backgroundHeight = textHeight + paddingInsets.getTop() + paddingInsets.getBottom();
        int totalWidth = backgroundWidth + decorationInsets.getLeft() + decorationInsets.getRight();
        int totalHeight = backgroundHeight + decorationInsets.getTop() + decorationInsets.getBottom();
        Point blockPoint = resolveBlockPoint(posterWidth, posterHeight, totalWidth, totalHeight,
                resolvedBaseLine, baselineOffset, resolvedLineHeight, decorationInsets, paddingInsets);
        Point contentPoint = Point.of(
                blockPoint.getX() + decorationInsets.getLeft() + paddingInsets.getLeft(),
                blockPoint.getY() + decorationInsets.getTop() + paddingInsets.getTop()
        );

        List<LayoutLine> layoutLines = new ArrayList<LayoutLine>(richLines.size());
        for (RichLine richLine : richLines) {
            int offsetX = resolvedTextAlign.offset(textWidth, richLine.getWidth());
            List<RichTextFragment> fragments = new ArrayList<RichTextFragment>(richLine.getFragments().size());
            for (RichTextFragment fragment : richLine.getFragments()) {
                fragments.add(fragment.shiftX(offsetX));
            }
            layoutLines.add(new LayoutLine(richLine.getText(), richLine.getWidth(),
                    Point.of(contentPoint.getX(), contentPoint.getY()), false,
                    resolvedRichTextLines.isClipOverflow() ? Math.min(richLine.getWidth(), textWidth) : richLine.getWidth(),
                    fragments));
        }

        return new TextLayoutResult(
                baseFont,
                resolvedBaseLine,
                resolvedTextAlign,
                resolvedOverflowStrategy,
                resolvedLineHeight,
                baselineOffset,
                blockPoint,
                totalWidth,
                totalHeight,
                textWidth,
                textHeight,
                backgroundWidth,
                backgroundHeight,
                layoutLines,
                resolvedRichTextLines.isTruncated(),
                resolvedRichTextLines.isClipOverflow(),
                decorationInsets,
                paddingInsets
        );
    }

    private void validateRichTextConfig(TextRenderSpec spec) {
        if (spec.isAutoFitText()) {
            throw new PosterException("rich text span does not support autoFitText yet");
        }
        if (spec.getTextAlign() == TextAlign.JUSTIFY) {
            throw new PosterException("rich text span does not support justify yet");
        }
    }

    private ResolvedRichTextLines resolveRichTextLines(TextRenderSpec spec, Graphics2D graphics,
                                                       TextOverflowStrategy resolvedOverflowStrategy) {
        return RICH_TEXT_LAYOUT_PROCESSOR.resolveRichTextLines(spec, graphics, resolvedOverflowStrategy,
                new RichTextWrapper.Measurer() {
                    @Override
                    public int measureBaseStringWidth(String text, FontMetrics fontMetrics, Graphics2D graphics2D) {
                        return EnhanceTextElement.this.measureBaseStringWidth(text, fontMetrics, graphics2D);
                    }

                    @Override
                    public int measureRichGlyphsWidth(List<RichGlyph> glyphs, int letterSpacing) {
                        return EnhanceTextElement.this.measureRichGlyphsWidth(glyphs, letterSpacing);
                    }

                    @Override
                    public String normalizeLineBreaks(String text) {
                        return EnhanceTextElement.this.normalizeLineBreaks(text);
                    }
                });
    }

    private Font resolveRenderFont(TextRenderSpec spec, String content, Font baseFont, Graphics2D graphics) {
        if (!spec.isAutoFitText() || spec.getAutoFitTargetWidth() <= 0 || content.isEmpty()) {
            return baseFont;
        }

        int baseSize = Math.max(1, Math.round(baseFont.getSize2D()));
        int minSize = Math.max(1, Math.min(baseSize, spec.getAutoFitMinFontSize()));
        int baseWidth = measureParagraphWidth(content, graphics.getFontMetrics(baseFont), graphics);
        if (baseWidth <= spec.getAutoFitTargetWidth() || baseSize == minSize) {
            return baseSize == minSize ? TEXT_METRICS.deriveFont(baseFont, minSize) : baseFont;
        }

        Font floorFont = TEXT_METRICS.deriveFont(baseFont, minSize);
        if (measureParagraphWidth(content, graphics.getFontMetrics(floorFont), graphics) > spec.getAutoFitTargetWidth()) {
            return floorFont;
        }

        Font bestFont = floorFont;
        int low = minSize;
        int high = baseSize;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            Font candidate = TEXT_METRICS.deriveFont(baseFont, mid);
            int candidateWidth = measureParagraphWidth(content, graphics.getFontMetrics(candidate), graphics);
            if (candidateWidth <= spec.getAutoFitTargetWidth()) {
                bestFont = candidate;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return bestFont;
    }

    private PlainTextWrapper.ResolvedLines resolveLines(TextRenderSpec spec, String content,
                                                        FontMetrics fontMetrics, Graphics2D graphics,
                                                        Font renderFont,
                                                        TextOverflowStrategy resolvedOverflowStrategy) {
        return PLAIN_TEXT_LAYOUT_PROCESSOR.resolveLines(spec, content, fontMetrics, graphics, renderFont,
                this.textSplitter, new PlainTextWrapper.Measurer() {
                    @Override
                    public int measureLineWidth(String text, FontMetrics lineFontMetrics, Graphics2D lineGraphics) {
                        return EnhanceTextElement.this.measureLineWidth(text, lineFontMetrics, lineGraphics);
                    }

                    @Override
                    public int measureParagraphWidth(String text, FontMetrics lineFontMetrics, Graphics2D lineGraphics) {
                        return EnhanceTextElement.this.measureParagraphWidth(text, lineFontMetrics, lineGraphics);
                    }
                });
    }

    private int resolveLineHeight(TextRenderSpec spec, FontMetrics fontMetrics) {
        Integer resolvedLineHeight = spec.getLineHeight();
        return resolvedLineHeight != null ? resolvedLineHeight : fontMetrics.getHeight();
    }

    private boolean shouldJustifyLine(TextAlign resolvedTextAlign, SplitTextInfo line, int index,
                                      PlainTextWrapper.ResolvedLines resolvedTextLines) {
        if (resolvedTextAlign != TextAlign.JUSTIFY) {
            return false;
        }
        if (index >= resolvedTextLines.getLines().size() - 1) {
            return false;
        }
        if (resolvedTextLines.getLayoutWidth() <= line.getWidth()) {
            return false;
        }
        return hasJustifiableGap(line.getText());
    }

    private int resolveLineRenderWidth(SplitTextInfo line, int layoutWidth, boolean justified, boolean clipOverflow) {
        if (justified) {
            return layoutWidth;
        }
        if (clipOverflow && layoutWidth > 0) {
            return Math.min(line.getWidth(), layoutWidth);
        }
        return line.getWidth();
    }

    private Point resolveBlockPoint(int posterWidth, int posterHeight, int layoutWidth, int totalHeight,
                                    BaseLine resolvedBaseLine, int baselineOffset, int resolvedLineHeight,
                                    TextDecorationInsets decorationInsets, TextPaddingInsets paddingInsets) {
        if (this.position instanceof AbsolutePosition) {
            Point anchor = ((AbsolutePosition) this.position).getPoint();
            int contentTopY = anchor.getY() - resolveAbsoluteAnchorOffset(resolvedBaseLine, baselineOffset, resolvedLineHeight);
            return Point.of(
                    anchor.getX() - decorationInsets.getLeft() - paddingInsets.getLeft(),
                    contentTopY - decorationInsets.getTop() - paddingInsets.getTop()
            );
        }
        if (this.position != null) {
            Position blockPosition = this.position;
            return blockPosition.calculate(posterWidth, posterHeight, layoutWidth, totalHeight);
        }
        return Point.ORIGIN_COORDINATE;
    }

    private TextPaddingInsets resolveTextPaddingInsets(TextRenderSpec spec) {
        return new TextPaddingInsets(
                spec.getTextPadding().getMarginLeft(),
                spec.getTextPadding().getMarginTop(),
                spec.getTextPadding().getMarginRight(),
                spec.getTextPadding().getMarginBottom()
        );
    }

    private int resolveAbsoluteAnchorOffset(BaseLine resolvedBaseLine, int baselineOffset, int resolvedLineHeight) {
        if (resolvedBaseLine == BaseLine.TOP) {
            return 0;
        }
        if (resolvedBaseLine == BaseLine.CENTER) {
            return resolvedLineHeight / 2;
        }
        if (resolvedBaseLine == BaseLine.BOTTOM) {
            return resolvedLineHeight;
        }
        return baselineOffset;
    }

    private int measureParagraphWidth(String content, FontMetrics fontMetrics, Graphics2D graphics) {
        return TEXT_METRICS.measureParagraphWidth(content, fontMetrics, graphics, this.letterSpacing);
    }

    private int measureLineWidth(String line, FontMetrics fontMetrics, Graphics2D graphics) {
        return TEXT_METRICS.measureLineWidth(line, fontMetrics, graphics, this.letterSpacing);
    }

    private int measureBaseStringWidth(String line, FontMetrics fontMetrics, Graphics2D graphics) {
        return TEXT_METRICS.measureBaseStringWidth(line, fontMetrics, graphics);
    }

    private int measureRichGlyphsWidth(List<RichGlyph> glyphs, int letterSpacing) {
        if (glyphs.isEmpty()) {
            return 0;
        }
        int width = 0;
        for (int i = 0; i < glyphs.size(); i++) {
            if (i > 0) {
                width += letterSpacing;
            }
            width += glyphs.get(i).getWidth();
        }
        return width;
    }

    private String normalizeLineBreaks(String content) {
        return content.replace("\r\n", "\n").replace('\r', '\n');
    }

    private Margin copyMargin(Margin margin) {
        return Margin.of(margin.getMarginLeft(), margin.getMarginTop(),
                margin.getMarginRight(), margin.getMarginBottom());
    }

    private boolean hasJustifiableGap(String lineText) {
        return lineText != null && lineText.indexOf(' ') >= 0;
    }

    private void validatePositive(String field, int value) {
        if (value <= 0) {
            throw new PosterException(field + " must be greater than 0");
        }
    }

    private void validateNonNegative(String field, int value) {
        if (value < 0) {
            throw new PosterException(field + " must be greater than or equal to 0");
        }
    }

    private static final class ResolvedTextLines {
        private final List<SplitTextInfo> lines;
        private final int layoutWidth;
        private final boolean truncated;
        private final boolean clipOverflow;

        private ResolvedTextLines(List<SplitTextInfo> lines, int layoutWidth, boolean truncated, boolean clipOverflow) {
            this.lines = lines;
            this.layoutWidth = layoutWidth;
            this.truncated = truncated;
            this.clipOverflow = clipOverflow;
        }
    }

}
