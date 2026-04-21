package com.bytefuture.easy.poster.element.chart.bar;

import com.bytefuture.easy.poster.element.chart.base.ChartTextSupport;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Shared label renderer for bar-chart value labels.
 */
public class BarChartLabelRenderer {

    public void drawValueLabel(Graphics2D g, Font valueFont, Color valueLabelColor, String text,
                               double value, int x, int y, int barWidth, int barHeight, int zeroY) {
        g.setFont(valueFont);
        g.setColor(valueLabelColor);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textX = x + (barWidth - textWidth) / 2;
        int textY;
        if (value >= 0) {
            textY = Math.max(metrics.getAscent(), y - 4);
        } else {
            textY = Math.min(zeroY + barHeight + metrics.getAscent() + 4, zeroY + barHeight + metrics.getHeight());
        }
        g.drawString(text, textX, textY);
    }

    public void drawStackedValueLabel(Graphics2D g, Font valueFont, Color valueLabelColor, String text,
                                      int x, int y, int barWidth, int barHeight, boolean positive, int zeroY,
                                      int minInsideLabelHeight, boolean showSmallStackLabelOutside, int externalLabelGap) {
        if (text == null || text.isEmpty()) {
            return;
        }
        Font targetFont = fitFontToWidth(g, valueFont, text, Math.max(12, barWidth - 4));
        FontMetrics metrics = g.getFontMetrics(targetFont);
        if (barHeight < Math.max(minInsideLabelHeight, metrics.getHeight() + 4)) {
            if (showSmallStackLabelOutside) {
                drawExternalStackedValueLabel(g, targetFont, valueLabelColor, text, x, y, barWidth, barHeight, positive, zeroY, externalLabelGap);
            }
            return;
        }

        g.setFont(targetFont);
        g.setColor(ChartTextSupport.chooseReadableTextColor(g.getColor()));
        int textWidth = metrics.stringWidth(text);
        int textX = x + (barWidth - textWidth) / 2;
        int textY = y + (barHeight - metrics.getHeight()) / 2 + metrics.getAscent();
        g.drawString(text, textX, textY);
    }

    private void drawExternalStackedValueLabel(Graphics2D g, Font font, Color valueLabelColor, String text, int x, int y,
                                               int barWidth, int barHeight, boolean positive, int zeroY, int externalLabelGap) {
        Font targetFont = fitFontToWidth(g, font, text, Math.max(16, barWidth + 28));
        g.setFont(targetFont);
        g.setColor(valueLabelColor);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textX = x + (barWidth - textWidth) / 2;
        int textY;
        if (positive) {
            textY = Math.max(metrics.getAscent(), y - externalLabelGap);
        } else {
            textY = Math.min(zeroY + barHeight + metrics.getAscent() + externalLabelGap,
                    y + barHeight + metrics.getAscent() + externalLabelGap);
        }
        g.drawString(text, textX, textY);
    }

    private Font fitFontToWidth(Graphics2D g, Font baseFont, String text, int maxWidth) {
        Font font = baseFont;
        FontMetrics metrics = g.getFontMetrics(font);
        if (metrics.stringWidth(text) <= maxWidth) {
            return font;
        }

        float size = font.getSize2D();
        while (size > 8F) {
            size -= 1F;
            Font candidate = font.deriveFont(size);
            Rectangle2D bounds = g.getFontMetrics(candidate).getStringBounds(text, g);
            if (bounds.getWidth() <= maxWidth) {
                return candidate;
            }
        }
        return font.deriveFont(8F);
    }
}
