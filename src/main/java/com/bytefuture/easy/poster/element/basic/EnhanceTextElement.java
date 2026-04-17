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
 * 增强版文本元素。
 * 负责承载文本样式配置，并串联文本规格解析、布局测量与最终绘制流程。
 */
@Getter
public class EnhanceTextElement extends AbstractRepeatableElement<EnhanceTextElement> implements IElement {

    /** 默认文本拆分器，用于普通文本自动换行。 */
    private static final ITextSplitter DEFAULT_TEXT_SPLITTER = new TextSplitterSimpleImpl();
    /** 文本布局引擎，负责缓存并产出布局结果。 */
    private static final TextLayoutEngine LAYOUT_ENGINE = new TextLayoutEngine();
    /** 文本绘制器，负责背景、装饰线与正文绘制。 */
    private static final TextPainter TEXT_PAINTER = new TextPainter();
    /** 纯文本换行处理器。 */
    private static final PlainTextWrapper PLAIN_TEXT_LAYOUT_PROCESSOR = new PlainTextWrapper();
    /** 富文本换行处理器。 */
    private static final RichTextWrapper RICH_TEXT_LAYOUT_PROCESSOR = new RichTextWrapper();
    /** 文本宽度、高度等基础度量服务。 */
    private static final TextMetricsService TEXT_METRICS = new TextMetricsService();
    /** 文本描边、阴影、下划线等装饰的额外占位计算器。 */
    private static final DecorationMetricsResolver DECORATION_METRICS = new DecorationMetricsResolver();

    /** 主文本内容；富文本场景下通常为空字符串。 */
    private final String text;

    /** 字体名称，未设置时回退到全局配置。 */
    private String fontName;

    /** 字体样式，如普通、粗体、斜体。 */
    private Integer fontStyle;

    /** 字体大小。 */
    private Integer fontSize;

    /** 直接指定的基础字体对象，优先级高于配置默认字体。 */
    private Font font;

    /** 文本锚点所采用的基线类型。 */
    private BaseLine baseLine;

    /** 行高；为空时使用字体默认高度。 */
    private Integer lineHeight;

    /** 是否启用自动换行。 */
    private boolean autoWordWrap = false;

    /** 文本布局宽度或自动换行时的最大宽度。 */
    private int maxTextWidth;

    /** 是否启用自动缩放字体以适配宽度。 */
    private boolean autoFitText = false;

    /** 自动缩放目标宽度。 */
    private int autoFitTargetWidth;

    /** 自动缩放允许收缩到的最小字号。 */
    private int autoFitMinFontSize;

    /** 是否绘制删除线。 */
    private boolean strikeThrough = false;

    /** 是否绘制下划线。 */
    private boolean underline = false;

    /** 文本对齐方式。 */
    private TextAlign textAlign;

    /** 文本溢出策略，如换行、裁剪、省略。 */
    private TextOverflowStrategy overflowStrategy;

    /** 最大显示行数。 */
    private Integer maxLines;

    /** 省略策略使用的省略符。 */
    private String ellipsis = "...";

    /** 文本阴影效果。 */
    private TextShadow shadow;

    /** 文本描边效果。 */
    private TextStroke stroke;

    /** 字间距。 */
    private int letterSpacing = 0;

    /** 文本背景色。 */
    private Color textBackgroundColor;

    /** 文本背景内边距。 */
    private Margin textPadding = Margin.of(0);

    /** 文本背景圆角宽度。 */
    private int textBackgroundArcWidth = 0;

    /** 文本背景圆角高度。 */
    private int textBackgroundArcHeight = 0;

    /** 富文本片段集合，按顺序参与布局和绘制。 */
    private final List<TextSpan> textSpans = new ArrayList<TextSpan>();

    /** 文本拆分器，实现自动换行时的分词/切分逻辑。 */
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
        // 尺寸计算与实际绘制共用同一套布局流程，确保测量结果与渲染结果一致。
        return LAYOUT_ENGINE.layout(this, context, posterWidth, posterHeight).toDimension(this.rotate);
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        // 绘制前先拿到布局结果，避免绘制期再次推导行信息导致前后不一致。
        TextLayoutResult layout = LAYOUT_ENGINE.layout(this, context, posterWidth, posterHeight);
        return TEXT_PAINTER.paint(this, context, dimension, layout);
    }

    @Override
    public void beforeRender(PosterContext context) {
        super.beforeRender(context);
        Graphics2D graphics = context.getGraphics();
        TextRenderSpec spec = TextRenderSpecFactory.from(this, context.getConfig());
        // 预先把画笔颜色和字体切到文本最终使用的基准样式，便于后续流程复用。
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
        // 富文本与纯文本的布局模型不同，先按数据类型分流。
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

        // 总尺寸 = 文本内容 + 背景内边距 + 装饰外扩（描边、阴影、装饰线等）。
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
            // 两端对齐只对非末行、且仍有剩余空间的行生效。
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
        int resolvedLineHeight = resolveRichLineHeight(spec, graphics, baseFont);
        int baselineOffset = resolveRichBaselineOffset(spec, graphics, baseFont, resolvedLineHeight);
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
            // 富文本按整行做水平偏移，片段自身再保留相对 xOffset。
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
        // 目前富文本还未支持缩放字体，因为不同片段样式缩放后需要统一重算。
        if (spec.isAutoFitText()) {
            throw new PosterException("rich text span does not support autoFitText yet");
        }
        // 富文本两端对齐需要片段级空白扩展逻辑，当前实现尚未覆盖。
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
        // 未开启自动缩放、目标宽度非法或文本为空时，直接沿用基础字体。
        if (!spec.isAutoFitText() || spec.getAutoFitTargetWidth() <= 0 || content.isEmpty()) {
            return baseFont;
        }

        int baseSize = Math.max(1, Math.round(baseFont.getSize2D()));
        int minSize = Math.max(1, Math.min(baseSize, spec.getAutoFitMinFontSize()));
        int baseWidth = measureParagraphWidth(content, graphics.getFontMetrics(baseFont), graphics);
        // 初始字体已经满足宽度，或者最小字号等于当前字号时，无需继续二分。
        if (baseWidth <= spec.getAutoFitTargetWidth() || baseSize == minSize) {
            return baseSize == minSize ? TEXT_METRICS.deriveFont(baseFont, minSize) : baseFont;
        }

        Font floorFont = TEXT_METRICS.deriveFont(baseFont, minSize);
        // 即便缩到最小字号仍超宽时，返回最小字号，后续再交给换行/裁剪策略处理。
        if (measureParagraphWidth(content, graphics.getFontMetrics(floorFont), graphics) > spec.getAutoFitTargetWidth()) {
            return floorFont;
        }

        // 在最小字号与基础字号之间二分搜索可容纳的最大字号。
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

    private int resolveRichLineHeight(TextRenderSpec spec, Graphics2D graphics, Font baseFont) {
        if (spec.getLineHeight() != null) {
            return spec.getLineHeight();
        }
        int maxHeight = graphics.getFontMetrics(baseFont).getHeight();
        for (TextSpan span : spec.getTextSpans()) {
            Font spanFont = resolveRichSpanFont(span, baseFont);
            maxHeight = Math.max(maxHeight, graphics.getFontMetrics(spanFont).getHeight());
        }
        return maxHeight;
    }

    private int resolveRichBaselineOffset(TextRenderSpec spec, Graphics2D graphics, Font baseFont, int resolvedLineHeight) {
        int maxOffset = TEXT_METRICS.resolveBaselineOffset(graphics.getFontMetrics(baseFont), resolvedLineHeight);
        for (TextSpan span : spec.getTextSpans()) {
            Font spanFont = resolveRichSpanFont(span, baseFont);
            maxOffset = Math.max(maxOffset,
                    TEXT_METRICS.resolveBaselineOffset(graphics.getFontMetrics(spanFont), resolvedLineHeight));
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

    private boolean shouldJustifyLine(TextAlign resolvedTextAlign, SplitTextInfo line, int index,
                                      PlainTextWrapper.ResolvedLines resolvedTextLines) {
        if (resolvedTextAlign != TextAlign.JUSTIFY) {
            return false;
        }
        // 最后一行通常不做两端对齐，避免单词间距被拉得不自然。
        if (index >= resolvedTextLines.getLines().size() - 1) {
            return false;
        }
        // 行内容已占满布局宽度时，无需再做额外拉伸。
        if (resolvedTextLines.getLayoutWidth() <= line.getWidth()) {
            return false;
        }
        return hasJustifiableGap(line.getText());
    }

    private int resolveLineRenderWidth(SplitTextInfo line, int layoutWidth, boolean justified, boolean clipOverflow) {
        if (justified) {
            // 两端对齐后的可绘制宽度就是整行布局宽度。
            return layoutWidth;
        }
        if (clipOverflow && layoutWidth > 0) {
            // 裁剪模式下记录真实可见宽度，便于调试框和后续裁剪区域计算。
            return Math.min(line.getWidth(), layoutWidth);
        }
        return line.getWidth();
    }

    private Point resolveBlockPoint(int posterWidth, int posterHeight, int layoutWidth, int totalHeight,
                                    BaseLine resolvedBaseLine, int baselineOffset, int resolvedLineHeight,
                                    TextDecorationInsets decorationInsets, TextPaddingInsets paddingInsets) {
        if (this.position instanceof AbsolutePosition) {
            Point anchor = ((AbsolutePosition) this.position).getPoint();
            // 绝对定位传入的是“文本锚点”，需要反推出包含装饰和内边距后的整体左上角。
            int contentTopY = anchor.getY() - resolveAbsoluteAnchorOffset(resolvedBaseLine, baselineOffset, resolvedLineHeight);
            return Point.of(
                    anchor.getX() - decorationInsets.getLeft() - paddingInsets.getLeft(),
                    contentTopY - decorationInsets.getTop() - paddingInsets.getTop()
            );
        }
        if (this.position != null) {
            // 相对定位直接以整体块尺寸参与位置计算。
            Position blockPosition = this.position;
            return blockPosition.calculate(posterWidth, posterHeight, layoutWidth, totalHeight);
        }
        // 未设置位置时默认从原点开始绘制。
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
            // 以行框中线作为锚点时，回退半个行高即可得到内容顶部。
            return resolvedLineHeight / 2;
        }
        if (resolvedBaseLine == BaseLine.BOTTOM) {
            return resolvedLineHeight;
        }
        // 默认使用字体基线偏移，兼容传统 baseline 定位。
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
                // 富文本逐字测量时，字间距只在相邻字形之间追加一次。
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
        // 当前实现仅对包含空格的文本做两端对齐扩展。
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

    /**
     * 纯文本换行结果载体。
     * 当前类内部未直接使用，保留在此用于兼容早期布局阶段的结构抽象。
     */
    private static final class ResolvedTextLines {
        /** 分行后的文本列表。 */
        private final List<SplitTextInfo> lines;
        /** 本次布局采用的宽度。 */
        private final int layoutWidth;
        /** 是否因为最大行数或省略策略发生截断。 */
        private final boolean truncated;
        /** 是否需要在绘制阶段裁剪超出部分。 */
        private final boolean clipOverflow;

        private ResolvedTextLines(List<SplitTextInfo> lines, int layoutWidth, boolean truncated, boolean clipOverflow) {
            this.lines = lines;
            this.layoutWidth = layoutWidth;
            this.truncated = truncated;
            this.clipOverflow = clipOverflow;
        }
    }

}
