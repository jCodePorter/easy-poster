package com.bytefuture.easy.poster.element.basic;

import com.bytefuture.easy.poster.element.AbstractRepeatableElement;
import com.bytefuture.easy.poster.element.IElement;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.Config;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextOverflowStrategy;
import com.bytefuture.easy.poster.model.TextShadow;
import com.bytefuture.easy.poster.model.TextSpan;
import com.bytefuture.easy.poster.model.TextStroke;
import com.bytefuture.easy.poster.text.ITextSplitter;
import com.bytefuture.easy.poster.text.SplitTextInfo;
import com.bytefuture.easy.poster.text.TextSplitRequest;
import com.bytefuture.easy.poster.text.TextSplitResult;
import com.bytefuture.easy.poster.text.TextSplitterSimpleImpl;
import com.bytefuture.easy.poster.utils.RotateUtils;
import lombok.Getter;

import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Upgraded text element with a side-effect free layout pipeline.
 */
@Getter
public class EnhanceTextElement extends AbstractRepeatableElement<EnhanceTextElement> implements IElement {

    private static final ITextSplitter DEFAULT_TEXT_SPLITTER = new TextSplitterSimpleImpl();

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
        return measureLayout(context, posterWidth, posterHeight).toDimension(this.rotate);
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        TextLayoutResult layout = measureLayout(context, posterWidth, posterHeight);
        Graphics2D graphics = context.getGraphics();

        super.gradient(context, dimension);
        graphics.setFont(layout.getFont());

        int xDiff = dimension.getPoint().getX() - layout.getPoint().getX();
        int yDiff = dimension.getPoint().getY() - layout.getPoint().getY();

        AffineTransform savedTransform = graphics.getTransform();
        Shape savedClip = graphics.getClip();
        if (this.rotate != 0) {
            AffineTransform rotatedTransform = new AffineTransform(savedTransform);
            rotatedTransform.rotate(Math.toRadians(this.rotate),
                    dimension.getPoint().getX() + dimension.getWidth() / 2.0,
                    dimension.getPoint().getY() + dimension.getHeight() / 2.0);
            graphics.setTransform(rotatedTransform);
        }
        drawTextBackground(graphics, dimension, layout);
        if (layout.isClipOverflow()) {
            graphics.clip(new Rectangle(
                    dimension.getPoint().getX() + layout.getDecorationInsets().getLeft() + layout.getTextPadding().getLeft(),
                    dimension.getPoint().getY() + layout.getDecorationInsets().getTop() + layout.getTextPadding().getTop(),
                    layout.getContentWidth(),
                    layout.getContentHeight()
            ));
        }

        for (int i = 0; i < layout.getLines().size(); i++) {
            LayoutLine line = layout.getLines().get(i);
            int startX = line.getPoint().getX() + xDiff;
            int startY = line.getPoint().getY() + layout.getBaselineOffset() + yDiff + i * layout.getLineHeight();

            if (context.getConfig().isDebug()) {
                drawDebugLine(context, layout, line, startX, startY);
            }
            drawTextLine(graphics, line, layout, startX, startY);
        }

        graphics.setClip(savedClip);
        graphics.setTransform(savedTransform);
        return dimension.getPoint();
    }

    @Override
    public void beforeRender(PosterContext context) {
        super.beforeRender(context);
        Graphics2D graphics = context.getGraphics();
        graphics.setColor(Optional.ofNullable(this.color).orElse(context.getConfig().getColor()));
        graphics.setFont(resolveBaseFont(context.getConfig()));
    }

    @Override
    public void debug(PosterContext context, Dimension dimension) {
        // Text debug rectangles are rendered line by line during drawing.
    }

    private TextLayoutResult measureLayout(PosterContext context, int posterWidth, int posterHeight) {
        if (hasRichTextSpans()) {
            return measureRichLayout(context, posterWidth, posterHeight);
        }

        Graphics2D graphics = context.getGraphics();
        Config config = context.getConfig();

        String normalizedText = normalizeText(this.text);
        Font baseFont = resolveBaseFont(config);
        Font renderFont = resolveRenderFont(normalizedText, baseFont, graphics);
        FontMetrics fontMetrics = graphics.getFontMetrics(renderFont);

        int resolvedLineHeight = resolveLineHeight(config, fontMetrics);
        int baselineOffset = resolveBaselineOffset(fontMetrics, resolvedLineHeight);
        BaseLine resolvedBaseLine = resolveBaseLine(config);
        TextAlign resolvedTextAlign = resolveTextAlign();
        TextOverflowStrategy resolvedOverflowStrategy = resolveOverflowStrategy();
        ResolvedTextLines resolvedTextLines = resolveLines(normalizedText, fontMetrics, graphics, renderFont, resolvedOverflowStrategy);
        TextDecorationInsets decorationInsets = resolveDecorationInsets(graphics, fontMetrics, resolvedLineHeight, baselineOffset);
        TextPaddingInsets paddingInsets = resolveTextPaddingInsets();

        int textWidth = resolvedTextLines.layoutWidth;
        int textHeight = resolvedLineHeight * resolvedTextLines.lines.size();
        int backgroundWidth = textWidth + paddingInsets.left + paddingInsets.right;
        int backgroundHeight = textHeight + paddingInsets.top + paddingInsets.bottom;
        int totalWidth = backgroundWidth + decorationInsets.left + decorationInsets.right;
        int totalHeight = backgroundHeight + decorationInsets.top + decorationInsets.bottom;
        Point blockPoint = resolveBlockPoint(posterWidth, posterHeight, totalWidth, totalHeight,
                resolvedBaseLine, baselineOffset, resolvedLineHeight, decorationInsets, paddingInsets);
        Point contentPoint = Point.of(
                blockPoint.getX() + decorationInsets.left + paddingInsets.left,
                blockPoint.getY() + decorationInsets.top + paddingInsets.top
        );

        List<LayoutLine> layoutLines = new ArrayList<LayoutLine>(resolvedTextLines.lines.size());
        for (int i = 0; i < resolvedTextLines.lines.size(); i++) {
            SplitTextInfo line = resolvedTextLines.lines.get(i);
            boolean justified = shouldJustifyLine(resolvedTextAlign, line, i, resolvedTextLines);
            int offsetX = justified ? 0 : resolvedTextAlign.offset(resolvedTextLines.layoutWidth, line.getWidth());
            int renderWidth = resolveLineRenderWidth(line, resolvedTextLines.layoutWidth, justified, resolvedTextLines.clipOverflow);
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
                resolvedTextLines.truncated,
                resolvedTextLines.clipOverflow,
                decorationInsets,
                paddingInsets
        );
    }

    private TextLayoutResult measureRichLayout(PosterContext context, int posterWidth, int posterHeight) {
        validateRichTextConfig();

        Graphics2D graphics = context.getGraphics();
        Config config = context.getConfig();
        Font baseFont = resolveBaseFont(config);
        int resolvedLineHeight = resolveLineHeight(config, graphics.getFontMetrics(baseFont));
        int baselineOffset = resolveBaselineOffset(graphics.getFontMetrics(baseFont), resolvedLineHeight);
        BaseLine resolvedBaseLine = resolveBaseLine(config);
        TextAlign resolvedTextAlign = resolveTextAlign();
        TextOverflowStrategy resolvedOverflowStrategy = resolveOverflowStrategy();
        ResolvedRichTextLines resolvedRichTextLines = resolveRichTextLines(config, graphics, resolvedOverflowStrategy);
        List<RichLine> richLines = resolvedRichTextLines.lines;
        TextDecorationInsets decorationInsets = resolveRichDecorationInsets(graphics, baseFont, resolvedLineHeight, baselineOffset, richLines);
        TextPaddingInsets paddingInsets = resolveTextPaddingInsets();

        int textWidth = resolvedRichTextLines.layoutWidth;
        int textHeight = resolvedLineHeight * richLines.size();
        int backgroundWidth = textWidth + paddingInsets.left + paddingInsets.right;
        int backgroundHeight = textHeight + paddingInsets.top + paddingInsets.bottom;
        int totalWidth = backgroundWidth + decorationInsets.left + decorationInsets.right;
        int totalHeight = backgroundHeight + decorationInsets.top + decorationInsets.bottom;
        Point blockPoint = resolveBlockPoint(posterWidth, posterHeight, totalWidth, totalHeight,
                resolvedBaseLine, baselineOffset, resolvedLineHeight, decorationInsets, paddingInsets);
        Point contentPoint = Point.of(
                blockPoint.getX() + decorationInsets.left + paddingInsets.left,
                blockPoint.getY() + decorationInsets.top + paddingInsets.top
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
                    resolvedRichTextLines.clipOverflow ? Math.min(richLine.getWidth(), textWidth) : richLine.getWidth(),
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
                resolvedRichTextLines.truncated,
                resolvedRichTextLines.clipOverflow,
                decorationInsets,
                paddingInsets
        );
    }

    private Font resolveBaseFont(Config config) {
        Font baseFont = Optional.ofNullable(this.font).orElse(config.getFont());
        if (baseFont != null) {
            if (this.fontName != null) {
                return new Font(
                        this.fontName,
                        Optional.ofNullable(this.fontStyle).orElse(baseFont.getStyle()),
                        Optional.ofNullable(this.fontSize).orElse(baseFont.getSize())
                );
            }

            int resolvedStyle = Optional.ofNullable(this.fontStyle).orElse(baseFont.getStyle());
            int resolvedSize = Optional.ofNullable(this.fontSize).orElse(baseFont.getSize());
            if (resolvedStyle == baseFont.getStyle() && resolvedSize == baseFont.getSize()) {
                return baseFont;
            }
            return baseFont.deriveFont(resolvedStyle, (float) resolvedSize);
        }

        return new Font(
                Optional.ofNullable(this.fontName).orElse(config.getFontName()),
                Optional.ofNullable(this.fontStyle).orElse(config.getFontStyle()),
                Optional.ofNullable(this.fontSize).orElse(config.getFontSize())
        );
    }

    private void validateRichTextConfig() {
        if (this.autoFitText) {
            throw new PosterException("rich text span does not support autoFitText yet");
        }
        if (resolveTextAlign() == TextAlign.JUSTIFY) {
            throw new PosterException("rich text span does not support justify yet");
        }
    }

    private ResolvedRichTextLines resolveRichTextLines(Config config, Graphics2D graphics,
                                                       TextOverflowStrategy resolvedOverflowStrategy) {
        Color defaultColor = Optional.ofNullable(this.color).orElse(config.getColor());
        Font baseFont = resolveBaseFont(config);
        int widthLimit = resolveWidthLimit();
        List<ResolvedTextSpan> spans = resolveAllTextSpans(config, defaultColor);
        List<RichToken> tokens = tokenizeRichText(spans, graphics);

        if (tokens.isEmpty()) {
            return new ResolvedRichTextLines(Collections.singletonList(createEmptyRichLine()),
                    resolveRichLayoutWidth(Collections.singletonList(createEmptyRichLine()), resolvedOverflowStrategy, widthLimit),
                    false, false);
        }

        List<RichLine> rawLines;
        if (resolvedOverflowStrategy == TextOverflowStrategy.WRAP && widthLimit > 0) {
            rawLines = resolveWrappedRichLines(tokens, widthLimit);
        } else {
            rawLines = splitRichLinesByExplicitNewLine(tokens);
        }

        if (rawLines.isEmpty()) {
            rawLines = Collections.singletonList(createEmptyRichLine());
        }

        List<RichLine> visibleLines = rawLines;
        if (resolvedOverflowStrategy == TextOverflowStrategy.ELLIPSIS && widthLimit > 0) {
            visibleLines = applyRichWidthEllipsis(visibleLines, widthLimit, baseFont, defaultColor, graphics);
        }

        ResolvedRichTextLines limitedLines = applyRichMaxLines(visibleLines,
                resolveRichLayoutWidth(visibleLines, resolvedOverflowStrategy, widthLimit),
                baseFont, defaultColor, graphics);
        if (resolvedOverflowStrategy == TextOverflowStrategy.CLIP && widthLimit > 0) {
            return new ResolvedRichTextLines(limitedLines.lines, widthLimit, limitedLines.truncated, true);
        }
        return limitedLines;
    }

    private List<RichLine> resolveRichLines(Config config, Graphics2D graphics) {
        return splitRichLinesByExplicitNewLine(tokenizeRichText(
                resolveAllTextSpans(config, Optional.ofNullable(this.color).orElse(config.getColor())), graphics));
    }

    private List<ResolvedTextSpan> resolveAllTextSpans(Config config, Color defaultColor) {
        List<ResolvedTextSpan> spans = new ArrayList<ResolvedTextSpan>();
        Font baseFont = resolveBaseFont(config);
        if (this.text != null && !this.text.isEmpty()) {
            spans.add(new ResolvedTextSpan(this.text, baseFont, defaultColor, this.underline, this.strikeThrough));
        }
        for (TextSpan textSpan : this.textSpans) {
            spans.add(resolveTextSpan(textSpan, baseFont, defaultColor));
        }
        return spans;
    }

    private ResolvedTextSpan resolveTextSpan(TextSpan textSpan, Font baseFont, Color defaultColor) {
        int resolvedStyle = Optional.ofNullable(textSpan.getFontStyle()).orElse(baseFont.getStyle());
        Font spanFont = resolvedStyle == baseFont.getStyle()
                ? baseFont
                : baseFont.deriveFont(resolvedStyle, baseFont.getSize2D());
        Color spanColor = Optional.ofNullable(textSpan.getColor()).orElse(defaultColor);
        boolean spanUnderline = Optional.ofNullable(textSpan.getUnderline()).orElse(this.underline);
        boolean spanStrikeThrough = Optional.ofNullable(textSpan.getStrikeThrough()).orElse(this.strikeThrough);
        return new ResolvedTextSpan(textSpan.getText(), spanFont, spanColor, spanUnderline, spanStrikeThrough);
    }

    private List<RichToken> tokenizeRichText(List<ResolvedTextSpan> spans, Graphics2D graphics) {
        List<RichToken> tokens = new ArrayList<RichToken>();
        List<RichGlyph> bufferGlyphs = new ArrayList<RichGlyph>();
        RichTokenType bufferType = null;

        for (ResolvedTextSpan span : spans) {
            String normalized = normalizeLineBreaks(span.getText());
            FontMetrics fontMetrics = graphics.getFontMetrics(span.getFont());

            for (int i = 0; i < normalized.length(); i++) {
                char current = normalized.charAt(i);
                if (current == '\n') {
                    flushRichToken(tokens, bufferGlyphs, bufferType);
                    bufferType = null;
                    tokens.add(RichToken.newLine());
                    continue;
                }

                RichGlyph glyph = new RichGlyph(String.valueOf(current),
                        measureBaseStringWidth(String.valueOf(current), fontMetrics, graphics),
                        span.getFont(), span.getColor(), span.isUnderline(), span.isStrikeThrough());
                RichTokenType currentType = resolveRichTokenType(current);
                if (currentType == RichTokenType.WORD || currentType == RichTokenType.WHITESPACE) {
                    if (bufferType != currentType) {
                        flushRichToken(tokens, bufferGlyphs, bufferType);
                        bufferType = currentType;
                    }
                    bufferGlyphs.add(glyph);
                    continue;
                }

                flushRichToken(tokens, bufferGlyphs, bufferType);
                bufferType = null;
                tokens.add(createRichToken(Collections.singletonList(glyph), currentType));
            }
        }

        flushRichToken(tokens, bufferGlyphs, bufferType);
        return tokens;
    }

    private void flushRichToken(List<RichToken> tokens, List<RichGlyph> bufferGlyphs, RichTokenType bufferType) {
        if (bufferType == null || bufferGlyphs.isEmpty()) {
            bufferGlyphs.clear();
            return;
        }
        tokens.add(createRichToken(new ArrayList<RichGlyph>(bufferGlyphs), bufferType));
        bufferGlyphs.clear();
    }

    private RichToken createRichToken(List<RichGlyph> glyphs, RichTokenType tokenType) {
        return new RichToken(buildRichText(glyphs), measureRichGlyphsWidth(glyphs), glyphs, tokenType);
    }

    private List<RichLine> splitRichLinesByExplicitNewLine(List<RichToken> tokens) {
        List<RichLine> lines = new ArrayList<RichLine>();
        RichTokenLineBuffer lineBuffer = new RichTokenLineBuffer();

        for (RichToken token : tokens) {
            if (token.getType() == RichTokenType.NEW_LINE) {
                flushRichLine(lines, lineBuffer, true);
                continue;
            }
            if (token.getType() == RichTokenType.WHITESPACE && lineBuffer.isEmpty()) {
                continue;
            }
            lineBuffer.append(token, this.letterSpacing);
        }

        flushRichLine(lines, lineBuffer, false);
        return lines;
    }

    private List<RichLine> resolveWrappedRichLines(List<RichToken> tokens, int maxWidth) {
        List<RichLine> lines = new ArrayList<RichLine>();
        RichTokenLineBuffer lineBuffer = new RichTokenLineBuffer();

        for (RichToken token : tokens) {
            if (token.getType() == RichTokenType.NEW_LINE) {
                flushRichLine(lines, lineBuffer, true);
                continue;
            }

            if (token.getType() == RichTokenType.WHITESPACE && lineBuffer.isEmpty()) {
                continue;
            }

            if (lineBuffer.canAppend(token, maxWidth, this.letterSpacing)) {
                lineBuffer.append(token, this.letterSpacing);
                continue;
            }

            if (shouldForceAppendToCurrentRichLine(token, lineBuffer)) {
                lineBuffer.append(token, this.letterSpacing);
                flushRichLine(lines, lineBuffer, false);
                continue;
            }

            if (!lineBuffer.isEmpty()) {
                flushRichLine(lines, lineBuffer, false);
                if (token.getType() == RichTokenType.WHITESPACE) {
                    continue;
                }
            }

            if (token.getWidth() <= maxWidth) {
                lineBuffer.append(token, this.letterSpacing);
                continue;
            }

            lines.addAll(splitOversizedRichToken(token, maxWidth));
        }

        flushRichLine(lines, lineBuffer, false);
        return lines;
    }

    private void flushRichLine(List<RichLine> lines, RichTokenLineBuffer lineBuffer, boolean explicitNewLine) {
        if (lineBuffer.isEmpty()) {
            if (explicitNewLine) {
                lines.add(createEmptyRichLine());
            }
            return;
        }

        RichLine line = lineBuffer.buildLine(true);
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

    private List<RichLine> splitOversizedRichToken(RichToken token, int maxWidth) {
        List<RichLine> lines = new ArrayList<RichLine>();
        List<RichGlyph> currentGlyphs = new ArrayList<RichGlyph>();
        int currentWidth = 0;

        for (RichGlyph glyph : token.getGlyphs()) {
            int candidateWidth = currentGlyphs.isEmpty()
                    ? glyph.getWidth()
                    : currentWidth + this.letterSpacing + glyph.getWidth();
            if (!currentGlyphs.isEmpty() && candidateWidth > maxWidth) {
                lines.add(createRichLineFromGlyphs(currentGlyphs));
                currentGlyphs = new ArrayList<RichGlyph>();
                currentWidth = 0;
            }
            if (!currentGlyphs.isEmpty()) {
                currentWidth += this.letterSpacing;
            }
            currentGlyphs.add(glyph);
            currentWidth += glyph.getWidth();
        }

        if (!currentGlyphs.isEmpty()) {
            lines.add(createRichLineFromGlyphs(currentGlyphs));
        }
        return lines;
    }

    private List<RichLine> applyRichWidthEllipsis(List<RichLine> rawLines, int widthLimit,
                                                  Font baseFont, Color defaultColor, Graphics2D graphics) {
        List<RichLine> ellipsized = new ArrayList<RichLine>(rawLines.size());
        for (RichLine line : rawLines) {
            if (line.getWidth() <= widthLimit) {
                ellipsized.add(line);
            } else {
                ellipsized.add(appendRichEllipsis(line, widthLimit, baseFont, defaultColor, graphics));
            }
        }
        return ellipsized;
    }

    private ResolvedRichTextLines applyRichMaxLines(List<RichLine> rawLines, int layoutWidth,
                                                    Font baseFont, Color defaultColor, Graphics2D graphics) {
        if (this.maxLines == null || rawLines.size() <= this.maxLines) {
            return new ResolvedRichTextLines(rawLines, layoutWidth, false, false);
        }

        List<RichLine> visibleLines = new ArrayList<RichLine>(rawLines.subList(0, this.maxLines));
        int lastIndex = visibleLines.size() - 1;
        int widthLimit = layoutWidth > 0 ? layoutWidth : Integer.MAX_VALUE;
        visibleLines.set(lastIndex, appendRichEllipsis(visibleLines.get(lastIndex), widthLimit, baseFont, defaultColor, graphics));

        int resolvedLayoutWidth = layoutWidth > 0 ? layoutWidth : resolveMaxRichWidth(visibleLines);
        return new ResolvedRichTextLines(visibleLines, resolvedLayoutWidth, true, false);
    }

    private RichLine appendRichEllipsis(RichLine originalLine, int maxWidth,
                                        Font baseFont, Color defaultColor, Graphics2D graphics) {
        List<RichGlyph> originalGlyphs = new ArrayList<RichGlyph>(originalLine.getGlyphs());
        List<RichGlyph> suffixGlyphs = fitRichSuffixToWidth(
                buildEllipsisGlyphs(resolveEllipsisStyleGlyph(originalGlyphs, baseFont, defaultColor, graphics), graphics), maxWidth);
        if (suffixGlyphs.isEmpty()) {
            return originalLine;
        }

        if (!this.ellipsis.isEmpty() && originalLine.getText().endsWith(this.ellipsis)
                && originalGlyphs.size() >= this.ellipsis.length()) {
            originalGlyphs = new ArrayList<RichGlyph>(originalGlyphs.subList(0, originalGlyphs.size() - this.ellipsis.length()));
        }

        while (!originalGlyphs.isEmpty() && measureRichGlyphsWidth(joinRichGlyphs(originalGlyphs, suffixGlyphs)) > maxWidth) {
            originalGlyphs.remove(originalGlyphs.size() - 1);
        }

        List<RichGlyph> candidateGlyphs = joinRichGlyphs(originalGlyphs, suffixGlyphs);
        if (measureRichGlyphsWidth(candidateGlyphs) > maxWidth) {
            candidateGlyphs = new ArrayList<RichGlyph>(suffixGlyphs);
        }
        return createRichLineFromGlyphs(candidateGlyphs);
    }

    private List<RichGlyph> buildEllipsisGlyphs(RichGlyph templateGlyph, Graphics2D graphics) {
        if (this.ellipsis.isEmpty()) {
            return Collections.emptyList();
        }

        List<RichGlyph> suffixGlyphs = new ArrayList<RichGlyph>(this.ellipsis.length());
        FontMetrics fontMetrics = graphics.getFontMetrics(templateGlyph.getFont());
        for (int i = 0; i < this.ellipsis.length(); i++) {
            String current = String.valueOf(this.ellipsis.charAt(i));
            suffixGlyphs.add(new RichGlyph(current, measureBaseStringWidth(current, fontMetrics, graphics),
                    templateGlyph.getFont(), templateGlyph.getColor(),
                    templateGlyph.isUnderline(), templateGlyph.isStrikeThrough()));
        }
        return suffixGlyphs;
    }

    private RichGlyph resolveEllipsisStyleGlyph(List<RichGlyph> originalGlyphs, Font baseFont,
                                                Color defaultColor, Graphics2D graphics) {
        if (!originalGlyphs.isEmpty()) {
            return originalGlyphs.get(originalGlyphs.size() - 1);
        }
        return new RichGlyph(".", measureBaseStringWidth(".", graphics.getFontMetrics(baseFont), graphics),
                baseFont, defaultColor, this.underline, this.strikeThrough);
    }

    private List<RichGlyph> fitRichSuffixToWidth(List<RichGlyph> suffixGlyphs, int maxWidth) {
        if (maxWidth == Integer.MAX_VALUE) {
            return suffixGlyphs;
        }
        List<RichGlyph> fitted = new ArrayList<RichGlyph>(suffixGlyphs);
        while (!fitted.isEmpty() && measureRichGlyphsWidth(fitted) > maxWidth) {
            fitted.remove(fitted.size() - 1);
        }
        return fitted;
    }

    private int resolveRichLayoutWidth(List<RichLine> lines, TextOverflowStrategy resolvedOverflowStrategy, int widthLimit) {
        if ((resolvedOverflowStrategy == TextOverflowStrategy.WRAP
                || resolvedOverflowStrategy == TextOverflowStrategy.CLIP
                || resolvedOverflowStrategy == TextOverflowStrategy.ELLIPSIS) && widthLimit > 0) {
            return widthLimit;
        }
        return resolveMaxRichWidth(lines);
    }

    private RichLine createEmptyRichLine() {
        return new RichLine("", 0, Collections.<RichTextFragment>emptyList(), Collections.<RichGlyph>emptyList());
    }

    private RichLine createRichLineFromTokens(List<RichToken> tokens, boolean trimTrailingWhitespace) {
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
        return createRichLineFromGlyphs(glyphs);
    }

    private RichLine createRichLineFromGlyphs(List<RichGlyph> glyphs) {
        if (glyphs.isEmpty()) {
            return createEmptyRichLine();
        }

        StringBuilder textBuilder = new StringBuilder();
        List<RichTextFragment> fragments = new ArrayList<RichTextFragment>();
        List<RichGlyph> copiedGlyphs = new ArrayList<RichGlyph>(glyphs);
        StringBuilder fragmentText = new StringBuilder();
        RichGlyph fragmentStyle = null;
        int fragmentStartX = 0;
        int fragmentWidth = 0;
        int currentX = 0;
        boolean firstGlyph = true;

        for (RichGlyph glyph : glyphs) {
            if (!firstGlyph) {
                currentX += this.letterSpacing;
            }

            if (fragmentStyle == null || !fragmentStyle.hasSameStyle(glyph)) {
                if (fragmentStyle != null) {
                    fragments.add(new RichTextFragment(fragmentText.toString(), fragmentStartX, fragmentWidth,
                            fragmentStyle.getFont(), fragmentStyle.getColor(),
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
            fragments.add(new RichTextFragment(fragmentText.toString(), fragmentStartX, fragmentWidth,
                    fragmentStyle.getFont(), fragmentStyle.getColor(),
                    fragmentStyle.isUnderline(), fragmentStyle.isStrikeThrough()));
        }
        return new RichLine(textBuilder.toString(), currentX, fragments, copiedGlyphs);
    }

    private List<RichGlyph> joinRichGlyphs(List<RichGlyph> leftGlyphs, List<RichGlyph> rightGlyphs) {
        List<RichGlyph> merged = new ArrayList<RichGlyph>(leftGlyphs.size() + rightGlyphs.size());
        merged.addAll(leftGlyphs);
        merged.addAll(rightGlyphs);
        return merged;
    }

    private String buildRichText(List<RichGlyph> glyphs) {
        StringBuilder builder = new StringBuilder(glyphs.size());
        for (RichGlyph glyph : glyphs) {
            builder.append(glyph.getText());
        }
        return builder.toString();
    }

    private int measureRichGlyphsWidth(List<RichGlyph> glyphs) {
        if (glyphs.isEmpty()) {
            return 0;
        }
        int width = 0;
        for (int i = 0; i < glyphs.size(); i++) {
            if (i > 0) {
                width += this.letterSpacing;
            }
            width += glyphs.get(i).getWidth();
        }
        return width;
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

    private Font resolveRenderFont(String content, Font baseFont, Graphics2D graphics) {
        if (!this.autoFitText || this.autoFitTargetWidth <= 0 || content.isEmpty()) {
            return baseFont;
        }

        int baseSize = Math.max(1, Math.round(baseFont.getSize2D()));
        int minSize = Math.max(1, Math.min(baseSize, this.autoFitMinFontSize));
        int baseWidth = measureParagraphWidth(content, graphics.getFontMetrics(baseFont), graphics);
        if (baseWidth <= this.autoFitTargetWidth || baseSize == minSize) {
            return baseSize == minSize ? deriveFont(baseFont, minSize) : baseFont;
        }

        Font floorFont = deriveFont(baseFont, minSize);
        if (measureParagraphWidth(content, graphics.getFontMetrics(floorFont), graphics) > this.autoFitTargetWidth) {
            return floorFont;
        }

        Font bestFont = floorFont;
        int low = minSize;
        int high = baseSize;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            Font candidate = deriveFont(baseFont, mid);
            int candidateWidth = measureParagraphWidth(content, graphics.getFontMetrics(candidate), graphics);
            if (candidateWidth <= this.autoFitTargetWidth) {
                bestFont = candidate;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return bestFont;
    }

    private ResolvedTextLines resolveLines(String content, FontMetrics fontMetrics, Graphics2D graphics, Font renderFont,
                                           TextOverflowStrategy resolvedOverflowStrategy) {
        if (content.isEmpty()) {
            return new ResolvedTextLines(Collections.singletonList(SplitTextInfo.of("", 0)), 0, false, false);
        }

        int widthLimit = resolveWidthLimit();
        int effectiveWrapWidth = resolveEffectiveWrapWidth(content, fontMetrics, graphics, renderFont, resolvedOverflowStrategy, widthLimit);
        List<SplitTextInfo> rawLines;
        if (resolvedOverflowStrategy == TextOverflowStrategy.WRAP && effectiveWrapWidth > 0) {
            rawLines = resolveWrappedLines(content, effectiveWrapWidth, fontMetrics, graphics);
        } else if (containsLineBreak(content)) {
            rawLines = splitExplicitLines(content, fontMetrics, graphics);
        } else {
            rawLines = Collections.singletonList(SplitTextInfo.of(content, measureLineWidth(content, fontMetrics, graphics)));
        }

        if (rawLines.isEmpty()) {
            rawLines = Collections.singletonList(SplitTextInfo.of("", 0));
        }

        List<SplitTextInfo> visibleLines = rawLines;
        if (resolvedOverflowStrategy == TextOverflowStrategy.ELLIPSIS && widthLimit > 0) {
            visibleLines = applyWidthEllipsis(visibleLines, widthLimit, fontMetrics, graphics);
        }

        ResolvedTextLines limitedLines = applyMaxLines(visibleLines, resolveLayoutWidth(visibleLines, resolvedOverflowStrategy, widthLimit, effectiveWrapWidth),
                fontMetrics, graphics);
        if (resolvedOverflowStrategy == TextOverflowStrategy.CLIP && widthLimit > 0) {
            return new ResolvedTextLines(limitedLines.lines, widthLimit, limitedLines.truncated, true);
        }
        return limitedLines;
    }

    private List<SplitTextInfo> resolveWrappedLines(String content, int maxWidth, FontMetrics fontMetrics, Graphics2D graphics) {
        TextSplitResult result = this.textSplitter.split(TextSplitRequest.of(content, maxWidth, fontMetrics));
        return normalizeWrappedLines(result.getLines(), maxWidth, fontMetrics, graphics);
    }

    private List<SplitTextInfo> normalizeSplitLines(List<SplitTextInfo> lines, FontMetrics fontMetrics, Graphics2D graphics) {
        List<SplitTextInfo> normalized = new ArrayList<SplitTextInfo>(lines.size());
        for (SplitTextInfo line : lines) {
            normalized.add(SplitTextInfo.of(line.getText(), measureLineWidth(line.getText(), fontMetrics, graphics)));
        }
        return normalized;
    }

    private List<SplitTextInfo> normalizeWrappedLines(List<SplitTextInfo> lines, int maxWidth,
                                                      FontMetrics fontMetrics, Graphics2D graphics) {
        List<SplitTextInfo> normalized = new ArrayList<SplitTextInfo>();
        for (SplitTextInfo line : lines) {
            String textValue = line.getText();
            int measuredWidth = measureLineWidth(textValue, fontMetrics, graphics);
            if (textValue == null || textValue.isEmpty() || measuredWidth <= maxWidth) {
                normalized.add(SplitTextInfo.of(textValue, measuredWidth));
                continue;
            }
            normalized.addAll(splitOverflowLine(textValue, maxWidth, fontMetrics, graphics));
        }
        return normalized;
    }

    private List<SplitTextInfo> splitOverflowLine(String lineText, int maxWidth,
                                                  FontMetrics fontMetrics, Graphics2D graphics) {
        List<SplitTextInfo> lines = new ArrayList<SplitTextInfo>();
        String remaining = lineText;
        while (!remaining.isEmpty()) {
            int lineEnd = findLineBreakIndex(remaining, maxWidth, fontMetrics, graphics);
            String rawSegment = remaining.substring(0, lineEnd);
            String visibleSegment = trimTrailingWhitespace(rawSegment);

            if (visibleSegment.isEmpty()) {
                visibleSegment = remaining.substring(0, 1);
                lineEnd = 1;
            }

            lines.add(SplitTextInfo.of(visibleSegment, measureLineWidth(visibleSegment, fontMetrics, graphics)));

            int nextStart = lineEnd;
            while (nextStart < remaining.length() && Character.isWhitespace(remaining.charAt(nextStart))) {
                nextStart++;
            }
            remaining = remaining.substring(nextStart);
        }
        return lines;
    }

    private int findLineBreakIndex(String textValue, int maxWidth, FontMetrics fontMetrics, Graphics2D graphics) {
        int lastBreak = -1;
        int end = 0;
        while (end < textValue.length()) {
            String candidate = textValue.substring(0, end + 1);
            if (measureLineWidth(candidate, fontMetrics, graphics) > maxWidth) {
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

    private ResolvedTextLines applyMaxLines(List<SplitTextInfo> rawLines, int layoutWidth,
                                            FontMetrics fontMetrics, Graphics2D graphics) {
        if (this.maxLines == null || rawLines.size() <= this.maxLines) {
            return new ResolvedTextLines(rawLines, layoutWidth, false, false);
        }

        List<SplitTextInfo> visibleLines = new ArrayList<SplitTextInfo>(rawLines.subList(0, this.maxLines));
        int lastIndex = visibleLines.size() - 1;
        int widthLimit = layoutWidth > 0 ? layoutWidth : Integer.MAX_VALUE;
        visibleLines.set(lastIndex, appendEllipsis(visibleLines.get(lastIndex), widthLimit, fontMetrics, graphics));

        int resolvedLayoutWidth = layoutWidth > 0 ? layoutWidth : resolveMaxWidth(visibleLines);
        return new ResolvedTextLines(visibleLines, resolvedLayoutWidth, true, false);
    }

    private SplitTextInfo appendEllipsis(SplitTextInfo originalLine, int maxWidth, FontMetrics fontMetrics, Graphics2D graphics) {
        String suffix = fitSuffixToWidth(this.ellipsis, maxWidth, fontMetrics, graphics);
        if (suffix.isEmpty()) {
            return originalLine;
        }

        String baseText = Optional.ofNullable(originalLine.getText()).orElse("");
        if (!suffix.isEmpty() && baseText.endsWith(suffix)) {
            baseText = baseText.substring(0, baseText.length() - suffix.length());
        }
        if (maxWidth == Integer.MAX_VALUE) {
            String merged = baseText + suffix;
            return SplitTextInfo.of(merged, measureLineWidth(merged, fontMetrics, graphics));
        }

        String candidate = baseText + suffix;
        while (!candidate.isEmpty() && measureLineWidth(candidate, fontMetrics, graphics) > maxWidth) {
            if (baseText.isEmpty()) {
                candidate = suffix;
                break;
            }
            baseText = baseText.substring(0, baseText.length() - 1);
            candidate = baseText + suffix;
        }

        if (measureLineWidth(candidate, fontMetrics, graphics) > maxWidth) {
            candidate = suffix;
        }
        return SplitTextInfo.of(candidate, measureLineWidth(candidate, fontMetrics, graphics));
    }

    private String fitSuffixToWidth(String suffix, int maxWidth, FontMetrics fontMetrics, Graphics2D graphics) {
        if (suffix.isEmpty() || maxWidth == Integer.MAX_VALUE) {
            return suffix;
        }

        String candidate = suffix;
        while (!candidate.isEmpty() && measureLineWidth(candidate, fontMetrics, graphics) > maxWidth) {
            candidate = candidate.substring(0, candidate.length() - 1);
        }
        return candidate;
    }

    private List<SplitTextInfo> applyWidthEllipsis(List<SplitTextInfo> rawLines, int widthLimit,
                                                   FontMetrics fontMetrics, Graphics2D graphics) {
        List<SplitTextInfo> ellipsized = new ArrayList<SplitTextInfo>(rawLines.size());
        for (SplitTextInfo line : rawLines) {
            if (line.getWidth() <= widthLimit) {
                ellipsized.add(line);
            } else {
                ellipsized.add(appendEllipsis(line, widthLimit, fontMetrics, graphics));
            }
        }
        return ellipsized;
    }

    private int resolveEffectiveWrapWidth(String content, FontMetrics fontMetrics, Graphics2D graphics, Font renderFont,
                                          TextOverflowStrategy resolvedOverflowStrategy, int widthLimit) {
        int wrapWidth = resolvedOverflowStrategy == TextOverflowStrategy.WRAP ? widthLimit : 0;
        if (!this.autoFitText || resolvedOverflowStrategy != TextOverflowStrategy.WRAP) {
            return wrapWidth;
        }

        int paragraphWidth = measureParagraphWidth(content, fontMetrics, graphics);
        int floorSize = Math.max(1, Math.min(Math.max(1, Math.round(renderFont.getSize2D())), this.autoFitMinFontSize));
        boolean needsFallbackWrap = paragraphWidth > this.autoFitTargetWidth
                && Math.max(1, Math.round(renderFont.getSize2D())) <= floorSize;
        if (!needsFallbackWrap) {
            return wrapWidth;
        }
        if (wrapWidth <= 0) {
            return this.autoFitTargetWidth;
        }
        return Math.min(wrapWidth, this.autoFitTargetWidth);
    }

    private int resolveLayoutWidth(List<SplitTextInfo> lines, TextOverflowStrategy resolvedOverflowStrategy,
                                   int widthLimit, int effectiveWrapWidth) {
        if (resolvedOverflowStrategy == TextOverflowStrategy.WRAP && effectiveWrapWidth > 0) {
            return effectiveWrapWidth;
        }
        if ((resolvedOverflowStrategy == TextOverflowStrategy.CLIP || resolvedOverflowStrategy == TextOverflowStrategy.ELLIPSIS)
                && widthLimit > 0) {
            return widthLimit;
        }
        return resolveMaxWidth(lines);
    }

    private List<SplitTextInfo> splitExplicitLines(String content, FontMetrics fontMetrics, Graphics2D graphics) {
        String[] segments = normalizeLineBreaks(content).split("\n", -1);
        List<SplitTextInfo> lines = new ArrayList<SplitTextInfo>(segments.length);
        for (String segment : segments) {
            lines.add(SplitTextInfo.of(segment, measureLineWidth(segment, fontMetrics, graphics)));
        }
        return lines;
    }

    private int resolveLineHeight(Config config, FontMetrics fontMetrics) {
        Integer resolvedLineHeight = this.lineHeight != null ? this.lineHeight : config.getLineHeight();
        return resolvedLineHeight != null ? resolvedLineHeight : fontMetrics.getHeight();
    }

    private BaseLine resolveBaseLine(Config config) {
        return Optional.ofNullable(this.baseLine).orElse(config.getBaseLine());
    }

    private TextOverflowStrategy resolveOverflowStrategy() {
        if (this.overflowStrategy != null) {
            return this.overflowStrategy;
        }
        if (this.autoWordWrap) {
            return TextOverflowStrategy.WRAP;
        }
        return TextOverflowStrategy.VISIBLE;
    }

    private TextAlign resolveTextAlign() {
        if (this.textAlign != null) {
            return this.textAlign;
        }
        if (this.position instanceof RelativePosition) {
            Direction direction = ((RelativePosition) this.position).getDirection();
            if (direction == Direction.CENTER || direction == Direction.TOP_CENTER || direction == Direction.BOTTOM_CENTER) {
                return TextAlign.CENTER;
            }
            if (direction == Direction.TOP_RIGHT || direction == Direction.RIGHT_CENTER || direction == Direction.RIGHT_BOTTOM) {
                return TextAlign.RIGHT;
            }
        }
        return TextAlign.LEFT;
    }

    private int resolveWidthLimit() {
        if (this.maxTextWidth > 0) {
            return this.maxTextWidth;
        }
        if (this.autoFitText) {
            return this.autoFitTargetWidth;
        }
        return 0;
    }

    private boolean shouldJustifyLine(TextAlign resolvedTextAlign, SplitTextInfo line, int index,
                                      ResolvedTextLines resolvedTextLines) {
        if (resolvedTextAlign != TextAlign.JUSTIFY) {
            return false;
        }
        if (index >= resolvedTextLines.lines.size() - 1) {
            return false;
        }
        if (resolvedTextLines.layoutWidth <= line.getWidth()) {
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
                    anchor.getX() - decorationInsets.left - paddingInsets.left,
                    contentTopY - decorationInsets.top - paddingInsets.top
            );
        }
        if (this.position != null) {
            Position blockPosition = this.position;
            return blockPosition.calculate(posterWidth, posterHeight, layoutWidth, totalHeight);
        }
        return Point.ORIGIN_COORDINATE;
    }

    private TextPaddingInsets resolveTextPaddingInsets() {
        return new TextPaddingInsets(
                this.textPadding.getMarginLeft(),
                this.textPadding.getMarginTop(),
                this.textPadding.getMarginRight(),
                this.textPadding.getMarginBottom()
        );
    }

    private TextDecorationInsets resolveDecorationInsets(Graphics2D graphics, FontMetrics fontMetrics,
                                                         int lineHeight, int baselineOffset) {
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;

        if (this.stroke != null) {
            int strokeInset = (int) Math.ceil(this.stroke.getWidth() / 2.0d);
            left = Math.max(left, strokeInset);
            right = Math.max(right, strokeInset);
            top = Math.max(top, strokeInset);
            bottom = Math.max(bottom, strokeInset);
        }

        if (this.shadow != null) {
            left = Math.max(left, Math.max(0, -this.shadow.getOffsetX()));
            right = Math.max(right, Math.max(0, this.shadow.getOffsetX()));
            top = Math.max(top, Math.max(0, -this.shadow.getOffsetY()));
            bottom = Math.max(bottom, Math.max(0, this.shadow.getOffsetY()));
        }

        LineMetrics lineMetrics = fontMetrics.getLineMetrics(resolveMetricsSampleText(this.text), graphics);
        if (this.underline) {
            top = Math.max(top, resolveDecorationTopOverflow(lineMetrics.getUnderlineOffset(),
                    lineMetrics.getUnderlineThickness(), baselineOffset));
            bottom = Math.max(bottom, resolveDecorationBottomOverflow(lineMetrics.getUnderlineOffset(),
                    lineMetrics.getUnderlineThickness(), baselineOffset, lineHeight));
        }
        if (this.strikeThrough) {
            top = Math.max(top, resolveDecorationTopOverflow(lineMetrics.getStrikethroughOffset(),
                    lineMetrics.getStrikethroughThickness(), baselineOffset));
            bottom = Math.max(bottom, resolveDecorationBottomOverflow(lineMetrics.getStrikethroughOffset(),
                    lineMetrics.getStrikethroughThickness(), baselineOffset, lineHeight));
        }

        return new TextDecorationInsets(left, top, right, bottom);
    }

    private int resolveDecorationTopOverflow(float decorationOffset, float thickness, int baselineOffset) {
        int minY = (int) Math.floor(baselineOffset + decorationOffset - thickness / 2.0f);
        return Math.max(0, -minY);
    }

    private int resolveDecorationBottomOverflow(float decorationOffset, float thickness,
                                                int baselineOffset, int lineHeight) {
        int maxY = (int) Math.ceil(baselineOffset + decorationOffset + thickness / 2.0f);
        return Math.max(0, maxY - lineHeight);
    }

    private int resolveBaselineOffset(FontMetrics fontMetrics, int resolvedLineHeight) {
        return fontMetrics.getAscent() + (resolvedLineHeight - fontMetrics.getHeight()) / 2;
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

    private int resolveMaxWidth(List<SplitTextInfo> lines) {
        int maxWidth = 0;
        for (SplitTextInfo line : lines) {
            maxWidth = Math.max(maxWidth, line.getWidth());
        }
        return maxWidth;
    }

    private int resolveMaxRichWidth(List<RichLine> lines) {
        int maxWidth = 0;
        for (RichLine line : lines) {
            maxWidth = Math.max(maxWidth, line.getWidth());
        }
        return maxWidth;
    }

    private TextDecorationInsets resolveRichDecorationInsets(Graphics2D graphics, Font baseFont,
                                                             int lineHeight, int baselineOffset, List<RichLine> richLines) {
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;

        if (this.stroke != null) {
            int strokeInset = (int) Math.ceil(this.stroke.getWidth() / 2.0d);
            left = Math.max(left, strokeInset);
            right = Math.max(right, strokeInset);
            top = Math.max(top, strokeInset);
            bottom = Math.max(bottom, strokeInset);
        }

        if (this.shadow != null) {
            left = Math.max(left, Math.max(0, -this.shadow.getOffsetX()));
            right = Math.max(right, Math.max(0, this.shadow.getOffsetX()));
            top = Math.max(top, Math.max(0, -this.shadow.getOffsetY()));
            bottom = Math.max(bottom, Math.max(0, this.shadow.getOffsetY()));
        }

        boolean hasUnderlineDecoration = false;
        boolean hasStrikeThroughDecoration = false;
        for (RichLine richLine : richLines) {
            for (RichTextFragment fragment : richLine.getFragments()) {
                hasUnderlineDecoration = hasUnderlineDecoration || fragment.isUnderline();
                hasStrikeThroughDecoration = hasStrikeThroughDecoration || fragment.isStrikeThrough();
            }
        }

        if (!hasUnderlineDecoration && !hasStrikeThroughDecoration) {
            return new TextDecorationInsets(left, top, right, bottom);
        }

        LineMetrics lineMetrics = graphics.getFontMetrics(baseFont)
                .getLineMetrics(resolveMetricsSampleText(resolveRichMetricsSampleText(richLines)), graphics);
        if (hasUnderlineDecoration) {
            top = Math.max(top, resolveDecorationTopOverflow(lineMetrics.getUnderlineOffset(),
                    lineMetrics.getUnderlineThickness(), baselineOffset));
            bottom = Math.max(bottom, resolveDecorationBottomOverflow(lineMetrics.getUnderlineOffset(),
                    lineMetrics.getUnderlineThickness(), baselineOffset, lineHeight));
        }
        if (hasStrikeThroughDecoration) {
            top = Math.max(top, resolveDecorationTopOverflow(lineMetrics.getStrikethroughOffset(),
                    lineMetrics.getStrikethroughThickness(), baselineOffset));
            bottom = Math.max(bottom, resolveDecorationBottomOverflow(lineMetrics.getStrikethroughOffset(),
                    lineMetrics.getStrikethroughThickness(), baselineOffset, lineHeight));
        }
        return new TextDecorationInsets(left, top, right, bottom);
    }

    private Font deriveFont(Font baseFont, int size) {
        if (Math.round(baseFont.getSize2D()) == size) {
            return baseFont;
        }
        return baseFont.deriveFont(baseFont.getStyle(), (float) size);
    }

    private int measureParagraphWidth(String content, FontMetrics fontMetrics, Graphics2D graphics) {
        String[] segments = normalizeLineBreaks(content).split("\n", -1);
        int maxWidth = 0;
        for (String segment : segments) {
            maxWidth = Math.max(maxWidth, measureLineWidth(segment, fontMetrics, graphics));
        }
        return maxWidth;
    }

    private int measureLineWidth(String line, FontMetrics fontMetrics, Graphics2D graphics) {
        if (line == null || line.isEmpty()) {
            return 0;
        }
        return measureBaseStringWidth(line, fontMetrics, graphics) + Math.max(0, line.length() - 1) * this.letterSpacing;
    }

    private int measureBaseStringWidth(String line, FontMetrics fontMetrics, Graphics2D graphics) {
        Rectangle2D bounds = fontMetrics.getStringBounds(line, graphics);
        return (int) Math.ceil(bounds.getWidth());
    }

    private int measureGapWidth(int spaceRunLength, FontMetrics fontMetrics, Graphics2D graphics) {
        if (spaceRunLength <= 0) {
            return 0;
        }
        return measureLineWidth(repeat(' ', spaceRunLength), fontMetrics, graphics) + 2 * this.letterSpacing;
    }

    private boolean containsLineBreak(String content) {
        return content.indexOf('\n') >= 0 || content.indexOf('\r') >= 0;
    }

    private String normalizeText(String content) {
        return content == null ? "" : normalizeLineBreaks(content);
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

    private String repeat(char value, int count) {
        StringBuilder builder = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            builder.append(value);
        }
        return builder.toString();
    }

    private String resolveMetricsSampleText(String content) {
        if (content == null || content.isEmpty()) {
            return "Ag";
        }
        return content;
    }

    private String resolveRichMetricsSampleText(List<RichLine> lines) {
        for (RichLine line : lines) {
            if (!line.getText().isEmpty()) {
                return line.getText();
            }
        }
        return "Ag";
    }

    private Margin copyMargin(Margin margin) {
        return Margin.of(margin.getMarginLeft(), margin.getMarginTop(),
                margin.getMarginRight(), margin.getMarginBottom());
    }

    private void drawDebugLine(PosterContext context, TextLayoutResult layout, LayoutLine line, int startX, int startY) {
        Graphics2D graphics = context.getGraphics();
        FontMetrics fontMetrics = graphics.getFontMetrics(layout.getFont());
        LineMetrics lineMetrics = fontMetrics.getLineMetrics(resolveMetricsSampleText(line.getText()), graphics);
        float ascent = lineMetrics.getAscent();
        int diffHeight = (layout.getLineHeight() - fontMetrics.getHeight()) / 2;

        int topY = (int) (startY - ascent - diffHeight);
        if (this.position instanceof AbsolutePosition) {
            if (layout.getBaseLine() == BaseLine.TOP) {
                topY += diffHeight;
            } else if (layout.getBaseLine() == BaseLine.BOTTOM) {
                topY -= diffHeight;
            }
        }
        graphics.drawRect(startX, topY, line.getRenderWidth(), layout.getLineHeight());
    }

    private void drawTextBackground(Graphics2D graphics, Dimension dimension, TextLayoutResult layout) {
        if (this.textBackgroundColor == null) {
            return;
        }

        Paint savedPaint = graphics.getPaint();
        graphics.setPaint(this.textBackgroundColor);
        graphics.fill(new RoundRectangle2D.Double(
                dimension.getPoint().getX() + layout.getDecorationInsets().getLeft(),
                dimension.getPoint().getY() + layout.getDecorationInsets().getTop(),
                layout.getBackgroundWidth(),
                layout.getBackgroundHeight(),
                this.textBackgroundArcWidth,
                this.textBackgroundArcHeight
        ));
        graphics.setPaint(savedPaint);
    }

    private void drawTextLine(Graphics2D graphics, LayoutLine line, TextLayoutResult layout, int startX, int startY) {
        if (line.hasRichFragments()) {
            drawRichTextLine(graphics, line, startX, startY);
            return;
        }

        List<TextFragment> fragments = resolveTextFragments(graphics, line, layout, startX);
        Paint fillPaint = graphics.getPaint();
        Stroke savedStroke = graphics.getStroke();

        if (this.shadow != null) {
            drawFragmentsFill(graphics, fragments, startY + this.shadow.getOffsetY(), this.shadow.getOffsetX(), this.shadow.getColor());
        }
        if (this.stroke != null) {
            drawFragmentsStroke(graphics, fragments, startY, this.stroke);
        }

        drawFragmentsFill(graphics, fragments, startY, 0, fillPaint);
        drawTextDecorations(graphics, line, startX, startY, fillPaint);
        graphics.setStroke(savedStroke);
        graphics.setPaint(fillPaint);
    }

    private void drawRichTextLine(Graphics2D graphics, LayoutLine line, int startX, int startY) {
        Paint savedPaint = graphics.getPaint();
        Stroke savedStroke = graphics.getStroke();

        if (this.shadow != null) {
            for (RichTextFragment fragment : line.getRichFragments()) {
                drawRichFragmentFill(graphics, fragment, startX, startY + this.shadow.getOffsetY(),
                        this.shadow.getOffsetX(), this.shadow.getColor());
            }
        }
        if (this.stroke != null) {
            for (RichTextFragment fragment : line.getRichFragments()) {
                drawRichFragmentStroke(graphics, fragment, startX, startY);
            }
        }
        for (RichTextFragment fragment : line.getRichFragments()) {
            drawRichFragmentFill(graphics, fragment, startX, startY, 0, fragment.getColor());
            drawRichFragmentDecorations(graphics, fragment, startX, startY);
        }

        graphics.setStroke(savedStroke);
        graphics.setPaint(savedPaint);
    }

    private List<TextFragment> resolveTextFragments(Graphics2D graphics, LayoutLine line,
                                                    TextLayoutResult layout, int startX) {
        if (!line.isJustified()) {
            return Collections.singletonList(new TextFragment(line.getText(), startX));
        }

        JustifySegments segments = splitJustifySegments(line.getText());
        if (segments.words.size() < 2 || line.getWidth() >= layout.getContentWidth()) {
            return Collections.singletonList(new TextFragment(line.getText(), startX));
        }

        int extraWidth = layout.getContentWidth() - line.getWidth();
        int gapCount = segments.spaceRunLengths.size();
        if (gapCount <= 0) {
            return Collections.singletonList(new TextFragment(line.getText(), startX));
        }

        FontMetrics fontMetrics = graphics.getFontMetrics();
        int extraPerGap = extraWidth / gapCount;
        int remainder = extraWidth % gapCount;
        int cursorX = startX;
        List<TextFragment> fragments = new ArrayList<TextFragment>(segments.words.size());

        fragments.add(new TextFragment(segments.words.get(0), cursorX));
        cursorX += measureLineWidth(segments.words.get(0), fontMetrics, graphics);

        for (int i = 0; i < gapCount; i++) {
            int gapWidth = measureGapWidth(segments.spaceRunLengths.get(i), fontMetrics, graphics)
                    + extraPerGap + (i < remainder ? 1 : 0);
            cursorX += gapWidth;
            fragments.add(new TextFragment(segments.words.get(i + 1), cursorX));
            cursorX += measureLineWidth(segments.words.get(i + 1), fontMetrics, graphics);
        }
        return fragments;
    }

    private void drawFragmentsFill(Graphics2D graphics, List<TextFragment> fragments,
                                   int baselineY, int xOffset, Paint paint) {
        Paint savedPaint = graphics.getPaint();
        graphics.setPaint(paint);
        for (TextFragment fragment : fragments) {
            if (fragment.getText().isEmpty()) {
                continue;
            }
            drawFragmentText(graphics, fragment.getText(), fragment.getX() + xOffset, baselineY);
        }
        graphics.setPaint(savedPaint);
    }

    private void drawFragmentsStroke(Graphics2D graphics, List<TextFragment> fragments,
                                     int baselineY, TextStroke textStroke) {
        Paint savedPaint = graphics.getPaint();
        Stroke savedStroke = graphics.getStroke();
        graphics.setPaint(textStroke.getColor());
        graphics.setStroke(new BasicStroke(textStroke.getWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (TextFragment fragment : fragments) {
            if (fragment.getText().isEmpty()) {
                continue;
            }
            drawFragmentStroke(graphics, fragment.getText(), fragment.getX(), baselineY);
        }

        graphics.setStroke(savedStroke);
        graphics.setPaint(savedPaint);
    }

    private void drawFragmentText(Graphics2D graphics, String textValue, int startX, int baselineY) {
        if (this.letterSpacing == 0 || textValue.length() <= 1) {
            graphics.drawString(textValue, startX, baselineY);
            return;
        }

        FontMetrics fontMetrics = graphics.getFontMetrics();
        int cursorX = startX;
        for (int i = 0; i < textValue.length(); i++) {
            String currentChar = String.valueOf(textValue.charAt(i));
            graphics.drawString(currentChar, cursorX, baselineY);
            if (i < textValue.length() - 1) {
                cursorX += measureBaseStringWidth(currentChar, fontMetrics, graphics) + this.letterSpacing;
            }
        }
    }

    private void drawRichFragmentFill(Graphics2D graphics, RichTextFragment fragment, int lineStartX,
                                      int baselineY, int xOffset, Paint paint) {
        if (fragment.getText().isEmpty()) {
            return;
        }
        Paint savedPaint = graphics.getPaint();
        Font savedFont = graphics.getFont();
        graphics.setPaint(paint);
        graphics.setFont(fragment.getFont());
        drawFragmentText(graphics, fragment.getText(), lineStartX + fragment.getXOffset() + xOffset, baselineY);
        graphics.setFont(savedFont);
        graphics.setPaint(savedPaint);
    }

    private void drawFragmentStroke(Graphics2D graphics, String textValue, int startX, int baselineY) {
        if (this.letterSpacing == 0 || textValue.length() <= 1) {
            Shape outline = graphics.getFont().createGlyphVector(graphics.getFontRenderContext(), textValue)
                    .getOutline(startX, baselineY);
            graphics.draw(outline);
            return;
        }

        FontMetrics fontMetrics = graphics.getFontMetrics();
        int cursorX = startX;
        for (int i = 0; i < textValue.length(); i++) {
            String currentChar = String.valueOf(textValue.charAt(i));
            Shape outline = graphics.getFont().createGlyphVector(graphics.getFontRenderContext(), currentChar)
                    .getOutline(cursorX, baselineY);
            graphics.draw(outline);
            if (i < textValue.length() - 1) {
                cursorX += measureBaseStringWidth(currentChar, fontMetrics, graphics) + this.letterSpacing;
            }
        }
    }

    private void drawRichFragmentStroke(Graphics2D graphics, RichTextFragment fragment, int lineStartX, int baselineY) {
        if (fragment.getText().isEmpty()) {
            return;
        }
        Paint savedPaint = graphics.getPaint();
        Font savedFont = graphics.getFont();
        Stroke savedStroke = graphics.getStroke();
        graphics.setPaint(this.stroke.getColor());
        graphics.setFont(fragment.getFont());
        graphics.setStroke(new BasicStroke(this.stroke.getWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawFragmentStroke(graphics, fragment.getText(), lineStartX + fragment.getXOffset(), baselineY);
        graphics.setStroke(savedStroke);
        graphics.setFont(savedFont);
        graphics.setPaint(savedPaint);
    }

    private void drawTextDecorations(Graphics2D graphics, LayoutLine line, int startX, int baselineY, Paint paint) {
        if ((!this.underline && !this.strikeThrough) || line.getRenderWidth() <= 0) {
            return;
        }

        FontMetrics fontMetrics = graphics.getFontMetrics();
        LineMetrics lineMetrics = fontMetrics.getLineMetrics(resolveMetricsSampleText(line.getText()), graphics);
        Paint savedPaint = graphics.getPaint();
        Stroke savedStroke = graphics.getStroke();
        graphics.setPaint(paint);

        if (this.strikeThrough) {
            drawHorizontalDecoration(graphics, startX, baselineY, line.getRenderWidth(),
                    lineMetrics.getStrikethroughOffset(), lineMetrics.getStrikethroughThickness());
        }
        if (this.underline) {
            drawHorizontalDecoration(graphics, startX, baselineY, line.getRenderWidth(),
                    lineMetrics.getUnderlineOffset(), lineMetrics.getUnderlineThickness());
        }

        graphics.setStroke(savedStroke);
        graphics.setPaint(savedPaint);
    }

    private void drawHorizontalDecoration(Graphics2D graphics, int startX, int baselineY, int width,
                                          float offset, float thickness) {
        graphics.setStroke(new BasicStroke(Math.max(1.0f, thickness), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        float y = baselineY + offset;
        graphics.draw(new Line2D.Float(startX, y, startX + width, y));
    }

    private void drawRichFragmentDecorations(Graphics2D graphics, RichTextFragment fragment, int lineStartX, int baselineY) {
        if ((!fragment.isUnderline() && !fragment.isStrikeThrough()) || fragment.getWidth() <= 0) {
            return;
        }

        Paint savedPaint = graphics.getPaint();
        Font savedFont = graphics.getFont();
        Stroke savedStroke = graphics.getStroke();
        graphics.setPaint(fragment.getColor());
        graphics.setFont(fragment.getFont());
        FontMetrics fontMetrics = graphics.getFontMetrics(fragment.getFont());
        LineMetrics lineMetrics = fontMetrics.getLineMetrics(resolveMetricsSampleText(fragment.getText()), graphics);
        int fragmentX = lineStartX + fragment.getXOffset();

        if (fragment.isStrikeThrough()) {
            drawHorizontalDecoration(graphics, fragmentX, baselineY, fragment.getWidth(),
                    lineMetrics.getStrikethroughOffset(), lineMetrics.getStrikethroughThickness());
        }
        if (fragment.isUnderline()) {
            drawHorizontalDecoration(graphics, fragmentX, baselineY, fragment.getWidth(),
                    lineMetrics.getUnderlineOffset(), lineMetrics.getUnderlineThickness());
        }

        graphics.setStroke(savedStroke);
        graphics.setFont(savedFont);
        graphics.setPaint(savedPaint);
    }

    private JustifySegments splitJustifySegments(String lineText) {
        List<String> words = new ArrayList<String>();
        List<Integer> spaceRunLengths = new ArrayList<Integer>();
        StringBuilder currentWord = new StringBuilder();
        int currentSpaceRun = 0;

        for (int i = 0; i < lineText.length(); i++) {
            char current = lineText.charAt(i);
            if (current == ' ') {
                if (currentWord.length() > 0) {
                    words.add(currentWord.toString());
                    currentWord.setLength(0);
                    currentSpaceRun = 1;
                } else if (!words.isEmpty()) {
                    currentSpaceRun++;
                }
                continue;
            }

            if (currentSpaceRun > 0) {
                spaceRunLengths.add(currentSpaceRun);
                currentSpaceRun = 0;
            }
            currentWord.append(current);
        }

        if (currentWord.length() > 0) {
            words.add(currentWord.toString());
        }
        if (spaceRunLengths.size() >= words.size() && !spaceRunLengths.isEmpty()) {
            spaceRunLengths.remove(spaceRunLengths.size() - 1);
        }
        return new JustifySegments(words, spaceRunLengths);
    }

    private boolean hasJustifiableGap(String lineText) {
        return lineText != null && lineText.indexOf(' ') >= 0;
    }

    private boolean hasRichTextSpans() {
        return !this.textSpans.isEmpty();
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

    @Getter
    private static final class TextLayoutResult {
        private final Font font;
        private final BaseLine baseLine;
        private final TextAlign textAlign;
        private final TextOverflowStrategy overflowStrategy;
        private final int lineHeight;
        private final int baselineOffset;
        private final Point point;
        private final int width;
        private final int height;
        private final int contentWidth;
        private final int contentHeight;
        private final int backgroundWidth;
        private final int backgroundHeight;
        private final List<LayoutLine> lines;
        private final boolean truncated;
        private final boolean clipOverflow;
        private final TextDecorationInsets decorationInsets;
        private final TextPaddingInsets textPadding;

        private TextLayoutResult(Font font, BaseLine baseLine, TextAlign textAlign, TextOverflowStrategy overflowStrategy,
                                 int lineHeight, int baselineOffset, Point point, int width, int height,
                                 int contentWidth, int contentHeight, int backgroundWidth, int backgroundHeight,
                                 List<LayoutLine> lines, boolean truncated, boolean clipOverflow,
                                 TextDecorationInsets decorationInsets, TextPaddingInsets textPadding) {
            this.font = font;
            this.baseLine = baseLine;
            this.textAlign = textAlign;
            this.overflowStrategy = overflowStrategy;
            this.lineHeight = lineHeight;
            this.baselineOffset = baselineOffset;
            this.point = point;
            this.width = width;
            this.height = height;
            this.contentWidth = contentWidth;
            this.contentHeight = contentHeight;
            this.backgroundWidth = backgroundWidth;
            this.backgroundHeight = backgroundHeight;
            this.lines = lines;
            this.truncated = truncated;
            this.clipOverflow = clipOverflow;
            this.decorationInsets = decorationInsets;
            this.textPadding = textPadding;
        }

        private Dimension toDimension(int rotate) {
            Dimension.DimensionBuilder builder = Dimension.builder()
                    .width(this.width)
                    .height(this.height)
                    .xOffset(this.decorationInsets.getLeft() + this.textPadding.getLeft())
                    .yOffset(this.baselineOffset + this.decorationInsets.getTop() + this.textPadding.getTop())
                    .point(Point.of(this.point.getX(), this.point.getY()));
            if (rotate != 0) {
                int[] bounds = RotateUtils.newBounds(this.width, this.height, rotate);
                builder.rotateWidth(bounds[0])
                        .rotateHeight(bounds[1]);
            }
            return builder.build();
        }
    }

    @Getter
    private static final class LayoutLine {
        private final String text;
        private final int width;
        private final Point point;
        private final boolean justified;
        private final int renderWidth;
        private final List<RichTextFragment> richFragments;

        private LayoutLine(String text, int width, Point point, boolean justified, int renderWidth,
                           List<RichTextFragment> richFragments) {
            this.text = text;
            this.width = width;
            this.point = point;
            this.justified = justified;
            this.renderWidth = renderWidth;
            this.richFragments = richFragments;
        }

        private boolean hasRichFragments() {
            return this.richFragments != null && !this.richFragments.isEmpty();
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

    private static final class ResolvedRichTextLines {
        private final List<RichLine> lines;
        private final int layoutWidth;
        private final boolean truncated;
        private final boolean clipOverflow;

        private ResolvedRichTextLines(List<RichLine> lines, int layoutWidth, boolean truncated, boolean clipOverflow) {
            this.lines = lines;
            this.layoutWidth = layoutWidth;
            this.truncated = truncated;
            this.clipOverflow = clipOverflow;
        }
    }

    private static final class JustifySegments {
        private final List<String> words;
        private final List<Integer> spaceRunLengths;

        private JustifySegments(List<String> words, List<Integer> spaceRunLengths) {
            this.words = words;
            this.spaceRunLengths = spaceRunLengths;
        }
    }

    @Getter
    private static final class ResolvedTextSpan {
        private final String text;
        private final Font font;
        private final Color color;
        private final boolean underline;
        private final boolean strikeThrough;

        private ResolvedTextSpan(String text, Font font, Color color, boolean underline, boolean strikeThrough) {
            this.text = text;
            this.font = font;
            this.color = color;
            this.underline = underline;
            this.strikeThrough = strikeThrough;
        }
    }

    @Getter
    private static final class RichLine {
        private final String text;
        private final int width;
        private final List<RichTextFragment> fragments;
        private final List<RichGlyph> glyphs;

        private RichLine(String text, int width, List<RichTextFragment> fragments, List<RichGlyph> glyphs) {
            this.text = text;
            this.width = width;
            this.fragments = fragments;
            this.glyphs = glyphs;
        }
    }

    private enum RichTokenType {
        WORD,
        WHITESPACE,
        NEW_LINE,
        OPENING_PUNCTUATION,
        CLOSING_PUNCTUATION,
        TEXT
    }

    @Getter
    private static final class RichToken {
        private final String text;
        private final int width;
        private final List<RichGlyph> glyphs;
        private final RichTokenType type;

        private RichToken(String text, int width, List<RichGlyph> glyphs, RichTokenType type) {
            this.text = text;
            this.width = width;
            this.glyphs = glyphs;
            this.type = type;
        }

        private static RichToken newLine() {
            return new RichToken("\n", 0, Collections.<RichGlyph>emptyList(), RichTokenType.NEW_LINE);
        }
    }

    private final class RichTokenLineBuffer {
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

        private RichLine buildLine(boolean trimTrailingWhitespace) {
            return createRichLineFromTokens(this.tokens, trimTrailingWhitespace);
        }

        private void clear() {
            this.tokens.clear();
            this.width = 0;
        }
    }

    @Getter
    private static final class TextFragment {
        private final String text;
        private final int x;

        private TextFragment(String text, int x) {
            this.text = text;
            this.x = x;
        }
    }

    @Getter
    private static final class RichTextFragment {
        private final String text;
        private final int xOffset;
        private final int width;
        private final Font font;
        private final Color color;
        private final boolean underline;
        private final boolean strikeThrough;

        private RichTextFragment(String text, int xOffset, int width, Font font, Color color,
                                 boolean underline, boolean strikeThrough) {
            this.text = text;
            this.xOffset = xOffset;
            this.width = width;
            this.font = font;
            this.color = color;
            this.underline = underline;
            this.strikeThrough = strikeThrough;
        }

        private RichTextFragment shiftX(int offsetX) {
            if (offsetX == 0) {
                return this;
            }
            return new RichTextFragment(this.text, this.xOffset + offsetX, this.width, this.font,
                    this.color, this.underline, this.strikeThrough);
        }
    }

    @Getter
    private static final class RichGlyph {
        private final String text;
        private final int width;
        private final Font font;
        private final Color color;
        private final boolean underline;
        private final boolean strikeThrough;

        private RichGlyph(String text, int width, Font font, Color color, boolean underline, boolean strikeThrough) {
            this.text = text;
            this.width = width;
            this.font = font;
            this.color = color;
            this.underline = underline;
            this.strikeThrough = strikeThrough;
        }

        private boolean hasSameStyle(RichGlyph other) {
            return this.font.equals(other.font)
                    && this.color.equals(other.color)
                    && this.underline == other.underline
                    && this.strikeThrough == other.strikeThrough;
        }
    }

    @Getter
    private static final class TextDecorationInsets {
        private final int left;
        private final int top;
        private final int right;
        private final int bottom;

        private TextDecorationInsets(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    @Getter
    private static final class TextPaddingInsets {
        private final int left;
        private final int top;
        private final int right;
        private final int bottom;

        private TextPaddingInsets(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }
}
