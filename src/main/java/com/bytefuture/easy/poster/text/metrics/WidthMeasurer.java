package com.bytefuture.easy.poster.text.metrics;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

public final class WidthMeasurer {

    private final TextMetricsService textMetricsService;

    public WidthMeasurer(TextMetricsService textMetricsService) {
        this.textMetricsService = textMetricsService;
    }

    public int measureLineWidth(String text, FontMetrics fontMetrics, Graphics2D graphics, int letterSpacing) {
        return this.textMetricsService.measureLineWidth(text, fontMetrics, graphics, letterSpacing);
    }

    public int measureParagraphWidth(String text, FontMetrics fontMetrics, Graphics2D graphics, int letterSpacing) {
        return this.textMetricsService.measureParagraphWidth(text, fontMetrics, graphics, letterSpacing);
    }

    public int measureBaseStringWidth(String text, FontMetrics fontMetrics, Graphics2D graphics) {
        return this.textMetricsService.measureBaseStringWidth(text, fontMetrics, graphics);
    }
}
