package com.bytefuture.easy.poster.text.paint;

import com.bytefuture.easy.poster.element.basic.EnhanceTextElement;
import com.bytefuture.easy.poster.text.wrap.RichTextFragment;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.List;

public final class RichTextPainter {

    private final DecorationPainter decorationPainter;
    private final com.bytefuture.easy.poster.text.metrics.TextMetricsService textMetricsService;

    public RichTextPainter(com.bytefuture.easy.poster.text.metrics.TextMetricsService textMetricsService,
                           DecorationPainter decorationPainter) {
        this.textMetricsService = textMetricsService;
        this.decorationPainter = decorationPainter;
    }

    public void paint(EnhanceTextElement element, Graphics2D graphics, List<RichTextFragment> fragments,
                      int startX, int startY) {
        Paint savedPaint = graphics.getPaint();
        Stroke savedStroke = graphics.getStroke();

        if (element.getShadow() != null) {
            for (RichTextFragment fragment : fragments) {
                drawRichFragmentFill(element, graphics, fragment, startX, startY + element.getShadow().getOffsetY(),
                        element.getShadow().getOffsetX(), element.getShadow().getColor());
            }
        }
        if (element.getStroke() != null) {
            for (RichTextFragment fragment : fragments) {
                drawRichFragmentStroke(element, graphics, fragment, startX, startY);
            }
        }
        for (RichTextFragment fragment : fragments) {
            drawRichFragmentFill(element, graphics, fragment, startX, startY, 0, fragment.getColor());
            this.decorationPainter.paintRichDecorations(graphics, fragment, startX, startY);
        }

        graphics.setStroke(savedStroke);
        graphics.setPaint(savedPaint);
    }

    private void drawRichFragmentFill(EnhanceTextElement element, Graphics2D graphics,
                                      RichTextFragment fragment, int lineStartX,
                                      int baselineY, int xOffset, Paint paint) {
        if (fragment.getText().isEmpty()) {
            return;
        }
        Paint savedPaint = graphics.getPaint();
        Font savedFont = graphics.getFont();
        graphics.setPaint(paint);
        graphics.setFont(fragment.getFont());
        drawFragmentText(element, graphics, fragment.getText(),
                lineStartX + fragment.getXOffset() + xOffset, baselineY);
        graphics.setFont(savedFont);
        graphics.setPaint(savedPaint);
    }

    private void drawRichFragmentStroke(EnhanceTextElement element, Graphics2D graphics,
                                        RichTextFragment fragment, int lineStartX, int baselineY) {
        if (fragment.getText().isEmpty()) {
            return;
        }
        Paint savedPaint = graphics.getPaint();
        Font savedFont = graphics.getFont();
        Stroke savedStroke = graphics.getStroke();
        graphics.setPaint(element.getStroke().getColor());
        graphics.setFont(fragment.getFont());
        graphics.setStroke(new BasicStroke(element.getStroke().getWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawFragmentStroke(element, graphics, fragment.getText(), lineStartX + fragment.getXOffset(), baselineY);
        graphics.setStroke(savedStroke);
        graphics.setFont(savedFont);
        graphics.setPaint(savedPaint);
    }

    private void drawFragmentText(EnhanceTextElement element, Graphics2D graphics,
                                  String textValue, int startX, int baselineY) {
        if (element.getLetterSpacing() == 0 || textValue.length() <= 1) {
            graphics.drawString(textValue, startX, baselineY);
            return;
        }

        FontMetrics fontMetrics = graphics.getFontMetrics();
        int cursorX = startX;
        for (int i = 0; i < textValue.length(); i++) {
            String currentChar = String.valueOf(textValue.charAt(i));
            graphics.drawString(currentChar, cursorX, baselineY);
            if (i < textValue.length() - 1) {
                cursorX += this.textMetricsService.measureBaseStringWidth(currentChar, fontMetrics, graphics)
                        + element.getLetterSpacing();
            }
        }
    }

    private void drawFragmentStroke(EnhanceTextElement element, Graphics2D graphics,
                                    String textValue, int startX, int baselineY) {
        if (element.getLetterSpacing() == 0 || textValue.length() <= 1) {
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
                cursorX += this.textMetricsService.measureBaseStringWidth(currentChar, fontMetrics, graphics)
                        + element.getLetterSpacing();
            }
        }
    }
}
