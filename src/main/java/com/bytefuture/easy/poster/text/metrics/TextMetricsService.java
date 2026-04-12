package com.bytefuture.easy.poster.text.metrics;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TextMetricsService {

    /** 不带字间距的基础字符串宽度缓存。 */
    private final Map<String, Integer> baseWidthCache = new ConcurrentHashMap<String, Integer>();
    /** 单行文本宽度缓存。 */
    private final Map<String, Integer> lineWidthCache = new ConcurrentHashMap<String, Integer>();
    /** 段落最大宽度缓存。 */
    private final Map<String, Integer> paragraphWidthCache = new ConcurrentHashMap<String, Integer>();

    public Font deriveFont(Font baseFont, int size) {
        if (Math.round(baseFont.getSize2D()) == size) {
            return baseFont;
        }
        return baseFont.deriveFont(baseFont.getStyle(), (float) size);
    }

    public int resolveBaselineOffset(FontMetrics fontMetrics, int resolvedLineHeight) {
        return fontMetrics.getAscent() + (resolvedLineHeight - fontMetrics.getHeight()) / 2;
    }

    public int measureParagraphWidth(String content, FontMetrics fontMetrics, Graphics2D graphics, int letterSpacing) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        String cacheKey = buildCacheKey("paragraph", fontMetrics.getFont(), letterSpacing, content);
        Integer cached = this.paragraphWidthCache.get(cacheKey);
        if (cached != null) {
            return cached.intValue();
        }

        String[] segments = normalizeLineBreaks(content).split("\n", -1);
        int maxWidth = 0;
        for (String segment : segments) {
            // 段落宽度取显式分行后各行的最大值。
            maxWidth = Math.max(maxWidth, measureLineWidth(segment, fontMetrics, graphics, letterSpacing));
        }
        this.paragraphWidthCache.put(cacheKey, Integer.valueOf(maxWidth));
        return maxWidth;
    }

    public int measureLineWidth(String line, FontMetrics fontMetrics, Graphics2D graphics, int letterSpacing) {
        if (line == null || line.isEmpty()) {
            return 0;
        }
        String cacheKey = buildCacheKey("line", fontMetrics.getFont(), letterSpacing, line);
        Integer cached = this.lineWidthCache.get(cacheKey);
        if (cached != null) {
            return cached.intValue();
        }

        // 字间距只作用于相邻字符之间，因此总共增加 length - 1 次。
        int width = measureBaseStringWidth(line, fontMetrics, graphics)
                + Math.max(0, line.length() - 1) * letterSpacing;
        this.lineWidthCache.put(cacheKey, Integer.valueOf(width));
        return width;
    }

    public int measureBaseStringWidth(String line, FontMetrics fontMetrics, Graphics2D graphics) {
        if (line == null || line.isEmpty()) {
            return 0;
        }
        String cacheKey = buildCacheKey("base", fontMetrics.getFont(), 0, line);
        Integer cached = this.baseWidthCache.get(cacheKey);
        if (cached != null) {
            return cached.intValue();
        }

        Rectangle2D bounds = fontMetrics.getStringBounds(line, graphics);
        int width = (int) Math.ceil(bounds.getWidth());
        this.baseWidthCache.put(cacheKey, Integer.valueOf(width));
        return width;
    }

    public int measureGapWidth(int spaceRunLength, FontMetrics fontMetrics, Graphics2D graphics, int letterSpacing) {
        if (spaceRunLength <= 0) {
            return 0;
        }
        // 两端对齐时，空白段左右两侧也要算入一次字符间隔。
        return measureLineWidth(repeat(' ', spaceRunLength), fontMetrics, graphics, letterSpacing) + 2 * letterSpacing;
    }

    private String buildCacheKey(String prefix, Font font, int letterSpacing, String text) {
        return prefix + "|" + font.getName() + "|" + font.getStyle() + "|" + font.getSize2D()
                + "|" + letterSpacing + "|" + text;
    }

    private String normalizeLineBreaks(String content) {
        return content.replace("\r\n", "\n").replace('\r', '\n');
    }

    private String repeat(char value, int count) {
        StringBuilder builder = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            builder.append(value);
        }
        return builder.toString();
    }
}
