package com.bytefuture.easy.poster.element.chart.base;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Shared legend layout and rendering for chart elements.
 */
public final class ChartLegendRenderer {

    private ChartLegendRenderer() {
    }

    public static int drawLegend(Graphics2D g, ChartLayoutBox innerBox, Font legendFont, List<NamedColorValue> items,
                                 int markerSize, int itemGap, java.awt.Color textColor) {
        g.setFont(legendFont);
        g.setColor(textColor);
        FontMetrics metrics = g.getFontMetrics();
        int rowHeight = Math.max(metrics.getHeight(), markerSize) + 6;
        int cursorX = innerBox.getLeft();
        int cursorY = innerBox.getTop();
        int rows = 1;
        for (NamedColorValue item : items) {
            String text = item.getDisplayText();
            int itemWidth = markerSize + 6 + metrics.stringWidth(text) + itemGap;
            if (cursorX > innerBox.getLeft() && cursorX + itemWidth > innerBox.getRight()) {
                rows++;
                cursorX = innerBox.getLeft();
                cursorY += rowHeight;
            }
            int baseline = cursorY + metrics.getAscent();
            int markerY = baseline - metrics.getAscent() + Math.max(0, (metrics.getHeight() - markerSize) / 2);
            g.setColor(item.getColor());
            g.fillRoundRect(cursorX, markerY, markerSize, markerSize, 4, 4);
            g.setColor(textColor);
            g.drawString(text, cursorX + markerSize + 6, baseline);
            cursorX += itemWidth;
        }
        return rows * rowHeight;
    }
}
