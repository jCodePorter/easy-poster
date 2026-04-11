package com.bytefuture.easy.poster.text.metrics;

import java.awt.Font;
import java.awt.FontMetrics;

public final class FontResolver {

    private final TextMetricsService textMetricsService;

    public FontResolver(TextMetricsService textMetricsService) {
        this.textMetricsService = textMetricsService;
    }

    public Font deriveFont(Font baseFont, int size) {
        return this.textMetricsService.deriveFont(baseFont, size);
    }

    public int resolveBaselineOffset(FontMetrics fontMetrics, int lineHeight) {
        return this.textMetricsService.resolveBaselineOffset(fontMetrics, lineHeight);
    }
}
