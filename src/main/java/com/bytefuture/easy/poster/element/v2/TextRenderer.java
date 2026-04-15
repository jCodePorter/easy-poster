package com.bytefuture.easy.poster.element.v2;

import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.text.layout.LayoutLine;
import com.bytefuture.easy.poster.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.text.wrap.RichTextFragment;

import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Text renderer V2.
 *
 * @author biaoy
 * @since 2025/04/15
 */
public final class TextRenderer {

    public Point render(TextElementConfig config, int rotate, PosterContext context,
                        Dimension dimension, TextLayoutResult layout) {
        Graphics2D g = context.getGraphics();

        AffineTransform savedTransform = g.getTransform();
        Shape savedClip = g.getClip();
        Font savedFont = g.getFont();
        Paint savedPaint = g.getPaint();
        Stroke savedStroke = g.getStroke();

        try {
            if (rotate != 0) {
                AffineTransform rotated = new AffineTransform(savedTransform);
                rotated.rotate(Math.toRadians(rotate),
                        dimension.getPoint().getX() + dimension.getWidth() / 2.0,
                        dimension.getPoint().getY() + dimension.getHeight() / 2.0);
                g.setTransform(rotated);
            }

            g.setFont(layout.getFont());

            drawBackground(config, g, dimension, layout);

            if (layout.isClipOverflow()) {
                g.clip(new Rectangle(
                        dimension.getPoint().getX() + layout.getDecorationInsets().getLeft() + layout.getTextPadding().getLeft(),
                        dimension.getPoint().getY() + layout.getDecorationInsets().getTop() + layout.getTextPadding().getTop(),
                        layout.getContentWidth(),
                        layout.getContentHeight()
                ));
            }

            int xDiff = dimension.getPoint().getX() - layout.getPoint().getX();
            int yDiff = dimension.getPoint().getY() - layout.getPoint().getY();

            for (int i = 0; i < layout.getLines().size(); i++) {
                LayoutLine line = layout.getLines().get(i);
                int startX = line.getPoint().getX() + xDiff;
                int startY = line.getPoint().getY() + layout.getBaselineOffset() + yDiff + i * layout.getLineHeight();

                if (context.getConfig().isDebug()) {
                    drawDebugBox(g, line, startX, startY, layout);
                }

                drawLine(config, g, line, startX, startY);
            }

            return dimension.getPoint();
        } finally {
            g.setTransform(savedTransform);
            g.setClip(savedClip);
            g.setFont(savedFont);
            g.setPaint(savedPaint);
            g.setStroke(savedStroke);
        }
    }

    private void drawBackground(TextElementConfig config, Graphics2D g, Dimension dimension, TextLayoutResult layout) {
        if (config.getTextBackgroundColor() == null) {
            return;
        }

        Paint savedPaint = g.getPaint();
        g.setPaint(config.getTextBackgroundColor());
        g.fill(new RoundRectangle2D.Double(
                dimension.getPoint().getX() + layout.getDecorationInsets().getLeft(),
                dimension.getPoint().getY() + layout.getDecorationInsets().getTop(),
                layout.getBackgroundWidth(),
                layout.getBackgroundHeight(),
                config.getTextBackgroundArcWidth(),
                config.getTextBackgroundArcHeight()
        ));
        g.setPaint(savedPaint);
    }

    private void drawLine(TextElementConfig config, Graphics2D g, LayoutLine line, int startX, int startY) {
        if (line.hasRichFragments()) {
            drawRichLine(config, g, line.getRichFragments(), startX, startY);
        } else {
            drawPlainLine(config, g, line, startX, startY);
        }
    }

    private void drawPlainLine(TextElementConfig config, Graphics2D g, LayoutLine line, int startX, int startY) {
        Paint textPaint = g.getPaint();
        Stroke savedStroke = g.getStroke();

        if (config.getShadow() != null) {
            g.setPaint(config.getShadow().getColor());
            drawTextWithSpacing(g, line.getText(), startX + config.getShadow().getOffsetX(),
                    startY + config.getShadow().getOffsetY(), config.getLetterSpacing());
        }

        if (config.getStroke() != null) {
            g.setPaint(config.getStroke().getColor());
            g.setStroke(new BasicStroke(config.getStroke().getWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawTextOutline(g, line.getText(), startX, startY, config.getLetterSpacing());
            g.setStroke(savedStroke);
        }

        g.setPaint(textPaint);
        drawTextWithSpacing(g, line.getText(), startX, startY, config.getLetterSpacing());

        if (config.isUnderline() || config.isStrikeThrough()) {
            drawPlainDecorations(config, g, line, startX, startY, textPaint);
        }
    }

    private void drawRichLine(TextElementConfig config, Graphics2D g,
                              java.util.List<RichTextFragment> fragments, int startX, int startY) {
        Paint savedPaint = g.getPaint();
        Font savedFont = g.getFont();
        Stroke savedStroke = g.getStroke();

        if (config.getShadow() != null) {
            g.setPaint(config.getShadow().getColor());
            for (RichTextFragment f : fragments) {
                g.setFont(f.getFont());
                drawTextWithSpacing(g, f.getText(), startX + f.getXOffset() + config.getShadow().getOffsetX(),
                        startY + config.getShadow().getOffsetY(), config.getLetterSpacing());
            }
        }

        if (config.getStroke() != null) {
            g.setPaint(config.getStroke().getColor());
            g.setStroke(new BasicStroke(config.getStroke().getWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (RichTextFragment f : fragments) {
                g.setFont(f.getFont());
                drawTextOutline(g, f.getText(), startX + f.getXOffset(), startY, config.getLetterSpacing());
            }
            g.setStroke(savedStroke);
        }

        for (RichTextFragment f : fragments) {
            g.setFont(f.getFont());
            g.setPaint(f.getColor());
            drawTextWithSpacing(g, f.getText(), startX + f.getXOffset(), startY, config.getLetterSpacing());

            if (f.isUnderline() || f.isStrikeThrough()) {
                drawRichDecoration(g, f, startX, startY);
            }
        }

        g.setFont(savedFont);
        g.setPaint(savedPaint);
    }

    private void drawTextWithSpacing(Graphics2D g, String text, int x, int y, int letterSpacing) {
        if (letterSpacing == 0 || text.length() <= 1) {
            g.drawString(text, x, y);
            return;
        }

        FontMetrics fm = g.getFontMetrics();
        int cursorX = x;
        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            g.drawString(ch, cursorX, y);
            if (i < text.length() - 1) {
                cursorX += fm.stringWidth(ch) + letterSpacing;
            }
        }
    }

    private void drawTextOutline(Graphics2D g, String text, int x, int y, int letterSpacing) {
        if (letterSpacing == 0 || text.length() <= 1) {
            Shape outline = g.getFont().createGlyphVector(g.getFontRenderContext(), text).getOutline(x, y);
            g.draw(outline);
            return;
        }

        FontMetrics fm = g.getFontMetrics();
        int cursorX = x;
        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            Shape outline = g.getFont().createGlyphVector(g.getFontRenderContext(), ch).getOutline(cursorX, y);
            g.draw(outline);
            if (i < text.length() - 1) {
                cursorX += fm.stringWidth(ch) + letterSpacing;
            }
        }
    }

    private void drawPlainDecorations(TextElementConfig config, Graphics2D g, LayoutLine line,
                                      int startX, int baselineY, Paint textPaint) {
        if (line.getRenderWidth() <= 0) {
            return;
        }

        FontMetrics fm = g.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(line.getText().isEmpty() ? "Ag" : line.getText(), g);
        Paint savedPaint = g.getPaint();
        Stroke savedStroke = g.getStroke();

        g.setPaint(textPaint);

        if (config.isStrikeThrough() && lm.getStrikethroughOffset() != 0) {
            drawDecorationLine(g, startX, baselineY, line.getRenderWidth(),
                    lm.getStrikethroughOffset(), lm.getStrikethroughThickness());
        }
        if (config.isUnderline() && lm.getUnderlineOffset() != 0) {
            drawDecorationLine(g, startX, baselineY, line.getRenderWidth(),
                    lm.getUnderlineOffset(), lm.getUnderlineThickness());
        }

        g.setStroke(savedStroke);
        g.setPaint(savedPaint);
    }

    private void drawRichDecoration(Graphics2D g, RichTextFragment fragment, int lineStartX, int baselineY) {
        if (fragment.getWidth() <= 0) {
            return;
        }

        FontMetrics fm = g.getFontMetrics(fragment.getFont());
        LineMetrics lm = fm.getLineMetrics(fragment.getText().isEmpty() ? "Ag" : fragment.getText(), g);
        int x = lineStartX + fragment.getXOffset();
        Paint savedPaint = g.getPaint();
        Stroke savedStroke = g.getStroke();

        g.setPaint(fragment.getColor());

        if (fragment.isStrikeThrough()) {
            drawDecorationLine(g, x, baselineY, fragment.getWidth(),
                    lm.getStrikethroughOffset(), lm.getStrikethroughThickness());
        }
        if (fragment.isUnderline()) {
            drawDecorationLine(g, x, baselineY, fragment.getWidth(),
                    lm.getUnderlineOffset(), lm.getUnderlineThickness());
        }

        g.setStroke(savedStroke);
        g.setPaint(savedPaint);
    }

    private void drawDecorationLine(Graphics2D g, int startX, int baselineY, int width,
                                    float offset, float thickness) {
        g.setStroke(new BasicStroke(Math.max(1.0f, thickness), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        float y = baselineY + offset;
        g.draw(new Line2D.Float(startX, y, startX + width, y));
    }

    private void drawDebugBox(Graphics2D g, LayoutLine line, int startX, int startY, TextLayoutResult layout) {
        FontMetrics fm = g.getFontMetrics(layout.getFont());
        LineMetrics lm = fm.getLineMetrics(line.getText().isEmpty() ? "Ag" : line.getText(), g);
        float ascent = lm.getAscent();
        int diffHeight = (layout.getLineHeight() - fm.getHeight()) / 2;
        int topY = (int) (startY - ascent - diffHeight);
        g.drawRect(startX, topY, line.getRenderWidth(), layout.getLineHeight());
    }
}
