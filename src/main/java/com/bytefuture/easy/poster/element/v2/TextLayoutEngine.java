package com.bytefuture.easy.poster.element.v2;

import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.*;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.*;
import com.bytefuture.easy.poster.text.layout.*;
import com.bytefuture.easy.poster.text.metrics.DecorationMetricsResolver;
import com.bytefuture.easy.poster.text.metrics.TextMetricsService;
import com.bytefuture.easy.poster.text.split.ITextSplitter;
import com.bytefuture.easy.poster.text.split.SplitTextInfo;
import com.bytefuture.easy.poster.text.split.TextSplitterSimpleImpl;
import com.bytefuture.easy.poster.text.wrap.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 文本布局引擎 V2 - 完全独立的布局计算。
 * <p>
 * 核心职责：
 * <ul>
 *   <li>接收 TextElementConfig 配置</li>
 *   <li>执行完整的文本布局计算</li>
 *   <li>返回 TextLayoutResult 结果</li>
 *   <li>不依赖 Element，不做循环调用</li>
 * </ul>
 *
 * @author biaoy
 * @since 2025/04/15
 */
public final class TextLayoutEngine {

    // ========== 核心依赖 ==========
    private static final ITextSplitter DEFAULT_SPLITTER = new TextSplitterSimpleImpl();
    private static final PlainTextWrapper PLAIN_WRAPPER = new PlainTextWrapper();
    private static final RichTextWrapper RICH_WRAPPER = new RichTextWrapper();
    private static final TextMetricsService METRICS = new TextMetricsService();
    private static final DecorationMetricsResolver DECORATION = new DecorationMetricsResolver();

    // ========== 缓存 ==========
    private final Map<String, TextLayoutResult> cache = new WeakHashMap<>();

    /**
     * 执行布局计算。
     */
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

    /**
     * 核心布局计算。
     */
    private TextLayoutResult computeLayout(TextElementConfig config, Position position, int rotate,
                                           PosterContext context, int posterWidth, int posterHeight) {
        // 1. 解析字体
        Font baseFont = resolveFont(config, context.getConfig());
        
        // 2. 根据文本类型分流
        if (config.isRichText()) {
            return computeRichLayout(config, position, rotate, context, posterWidth, posterHeight, baseFont);
        } else {
            return computePlainLayout(config, position, rotate, context, posterWidth, posterHeight, baseFont);
        }
    }

    /**
     * 纯文本布局。
     */
    private TextLayoutResult computePlainLayout(TextElementConfig config, Position position, int rotate,
                                                 PosterContext context, int posterWidth, int posterHeight,
                                                 Font baseFont) {
        Graphics2D g = context.getGraphics();
        TextAlign resolvedTextAlign = resolveTextAlign(config, position);
        String text = normalizeText(config.getText());

        // 解析渲染字体（可能因autoFit而改变）
        Font renderFont = resolveRenderFont(config, text, baseFont, g);
        FontMetrics fm = g.getFontMetrics(renderFont);

        // 计算行高和基线
        int lineHeight = config.getLineHeight() != null ? config.getLineHeight() : fm.getHeight();
        int baselineOffset = METRICS.resolveBaselineOffset(fm, lineHeight);

        // 执行换行
        ITextSplitter splitter = config.getTextSplitter() != null ? config.getTextSplitter() : DEFAULT_SPLITTER;
        PlainTextWrapper.ResolvedLines resolvedLines = PLAIN_WRAPPER.resolveLines(
                buildRenderSpec(config, baseFont, resolvedTextAlign), text, fm, g, renderFont, splitter,
                createPlainMeasurer(config));

        // 计算装饰占位
        TextDecorationInsets decorInsets = DECORATION.resolveTextInsets(
                buildRenderSpec(config, baseFont, resolvedTextAlign), g, fm, lineHeight, baselineOffset);
        TextPaddingInsets paddingInsets = new TextPaddingInsets(
                config.getTextPadding().getMarginLeft(),
                config.getTextPadding().getMarginTop(),
                config.getTextPadding().getMarginRight(),
                config.getTextPadding().getMarginBottom()
        );

        // 计算尺寸
        int textWidth = resolvedLines.getLayoutWidth();
        int textHeight = lineHeight * resolvedLines.getLines().size();
        int bgWidth = textWidth + paddingInsets.getLeft() + paddingInsets.getRight();
        int bgHeight = textHeight + paddingInsets.getTop() + paddingInsets.getBottom();
        int totalWidth = bgWidth + decorInsets.getLeft() + decorInsets.getRight();
        int totalHeight = bgHeight + decorInsets.getTop() + decorInsets.getBottom();

        // 计算位置
        Point blockPoint = resolvePosition(position, posterWidth, posterHeight, totalWidth, totalHeight,
                textWidth, textHeight, config.getBaseLine(), baselineOffset, lineHeight, decorInsets, paddingInsets);
        Point contentPoint = Point.of(
                blockPoint.getX() + decorInsets.getLeft() + paddingInsets.getLeft(),
                blockPoint.getY() + decorInsets.getTop() + paddingInsets.getTop()
        );

        // 构建行
        List<LayoutLine> layoutLines = buildPlainLines(resolvedLines, contentPoint, resolvedTextAlign);

        return new TextLayoutResult(
                renderFont, config.getBaseLine(), resolvedTextAlign, getOverflowStrategy(config),
                lineHeight, baselineOffset, blockPoint, totalWidth, totalHeight,
                textWidth, textHeight, bgWidth, bgHeight,
                layoutLines, resolvedLines.isTruncated(), resolvedLines.isClipOverflow(),
                decorInsets, paddingInsets
        );
    }

    /**
     * 富文本布局。
     */
    private TextLayoutResult computeRichLayout(TextElementConfig config, Position position, int rotate,
                                                PosterContext context, int posterWidth, int posterHeight,
                                                Font baseFont) {
        Graphics2D g = context.getGraphics();
        TextAlign resolvedTextAlign = resolveTextAlign(config, position);

        // 富文本不支持autoFit
        if (config.isAutoFitText()) {
            throw new PosterException("rich text span does not support autoFitText yet");
        }
        if (resolvedTextAlign == TextAlign.JUSTIFY) {
            throw new PosterException("rich text span does not support justify yet");
        }

        int lineHeight = resolveRichLineHeight(config, baseFont, g);
        int baselineOffset = resolveRichBaselineOffset(config, baseFont, g, lineHeight);

        // 执行换行
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

        // 计算尺寸
        int textWidth = resolvedLines.getLayoutWidth();
        int textHeight = lineHeight * richLines.size();
        int bgWidth = textWidth + paddingInsets.getLeft() + paddingInsets.getRight();
        int bgHeight = textHeight + paddingInsets.getTop() + paddingInsets.getBottom();
        int totalWidth = bgWidth + decorInsets.getLeft() + decorInsets.getRight();
        int totalHeight = bgHeight + decorInsets.getTop() + decorInsets.getBottom();

        // 计算位置
        Point blockPoint = resolvePosition(position, posterWidth, posterHeight, totalWidth, totalHeight,
                textWidth, textHeight, config.getBaseLine(), baselineOffset, lineHeight, decorInsets, paddingInsets);
        Point contentPoint = Point.of(
                blockPoint.getX() + decorInsets.getLeft() + paddingInsets.getLeft(),
                blockPoint.getY() + decorInsets.getTop() + paddingInsets.getTop()
        );

        // 构建行
        List<LayoutLine> layoutLines = buildRichLines(richLines, contentPoint, textWidth,
                resolvedTextAlign, resolvedLines);

        return new TextLayoutResult(
                baseFont, config.getBaseLine(), resolvedTextAlign, getOverflowStrategy(config),
                lineHeight, baselineOffset, blockPoint, totalWidth, totalHeight,
                textWidth, textHeight, bgWidth, bgHeight,
                layoutLines, resolvedLines.isTruncated(), resolvedLines.isClipOverflow(),
                decorInsets, paddingInsets
        );
    }

    // ========== 辅助方法 ==========

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

        // 二分查找最佳字号
        Font bestFont = METRICS.deriveFont(baseFont, minSize);
        int low = minSize, high = baseSize;
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
                                   TextDecorationInsets decorInsets, TextPaddingInsets paddingInsets) {
        if (position instanceof AbsolutePosition) {
            Point anchor = position.calculate(posterWidth, posterHeight, contentWidth, contentHeight);
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
            case TOP: return 0;
            case CENTER: return lineHeight / 2;
            case BOTTOM: return lineHeight;
            default: return baselineOffset;
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

    private List<LayoutLine> buildRichLines(List<RichLine> richLines, Point contentPoint, int textWidth,
                                             TextAlign align, ResolvedRichTextLines resolved) {
        List<LayoutLine> lines = new ArrayList<>(richLines.size());
        boolean clipOverflow = resolved.isClipOverflow();

        for (RichLine richLine : richLines) {
            int offsetX = align.offset(textWidth, richLine.getWidth());
            List<RichTextFragment> fragments = new ArrayList<>(richLine.getFragments().size());
            for (RichTextFragment f : richLine.getFragments()) {
                fragments.add(f.shiftX(offsetX));
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
                new Font("SansSerif", Font.PLAIN, 16), BaseLine.BASE_LINE, TextAlign.LEFT, TextOverflowStrategy.VISIBLE,
                16, 12, point, 0, 0, 0, 0, 0, 0,
                new ArrayList<>(), false, false, decor, padding
        );
    }

    private TextRenderSpec buildRenderSpec(TextElementConfig config, Font baseFont, TextAlign resolvedTextAlign) {
        // 仅用于传递给Wrapper和Decoration计算
        return new TextRenderSpec(
                config.getText(), config.getTextSpans(), null,
                Color.BLACK, baseFont, config.getBaseLine(), config.getLineHeight(),
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
}
