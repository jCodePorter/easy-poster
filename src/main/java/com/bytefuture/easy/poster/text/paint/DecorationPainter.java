package com.bytefuture.easy.poster.text.paint;

import com.bytefuture.easy.poster.element.basic.EnhanceTextElement;
import com.bytefuture.easy.poster.text.layout.LayoutLine;
import com.bytefuture.easy.poster.text.wrap.RichTextFragment;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;

public final class DecorationPainter {

    public void paintPlainDecorations(EnhanceTextElement element, Graphics2D graphics,
                                      LayoutLine line, int startX, int baselineY, Paint paint) {
        if ((!element.isUnderline() && !element.isStrikeThrough()) || line.getRenderWidth() <= 0) {
            return;
        }

        FontMetrics fontMetrics = graphics.getFontMetrics();
        LineMetrics lineMetrics = fontMetrics.getLineMetrics(resolveMetricsSampleText(line.getText()), graphics);
        Paint savedPaint = graphics.getPaint();
        Stroke savedStroke = graphics.getStroke();
        graphics.setPaint(paint);

        if (element.isStrikeThrough()) {
            drawHorizontalDecoration(graphics, startX, baselineY, line.getRenderWidth(),
                    lineMetrics.getStrikethroughOffset(), lineMetrics.getStrikethroughThickness());
        }
        if (element.isUnderline()) {
            drawHorizontalDecoration(graphics, startX, baselineY, line.getRenderWidth(),
                    lineMetrics.getUnderlineOffset(), lineMetrics.getUnderlineThickness());
        }

        graphics.setStroke(savedStroke);
        graphics.setPaint(savedPaint);
    }

    public void paintRichDecorations(Graphics2D graphics, RichTextFragment fragment,
                                     int lineStartX, int baselineY) {
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

    private void drawHorizontalDecoration(Graphics2D graphics, int startX, int baselineY, int width,
                                          float offset, float thickness) {
        graphics.setStroke(new BasicStroke(Math.max(1.0f, thickness), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        float y = baselineY + offset;
        graphics.draw(new Line2D.Float(startX, y, startX + width, y));
    }

    private String resolveMetricsSampleText(String content) {
        if (content == null || content.isEmpty()) {
            return "Ag";
        }
        return content;
    }
}
