package com.bytefuture.easy.poster.element.v2;

import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.Gradient;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.text.layout.LayoutLine;
import com.bytefuture.easy.poster.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.text.layout.VerticalGlyph;
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

    public Point render(TextElementConfig config, int rotate, Color fillColor, Gradient gradient,
                        PosterContext context,
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
            Paint fillPaint = resolveFillPaint(g, fillColor, gradient, context, dimension, layout, xDiff, yDiff);
            boolean useSharedFillPaint = gradient != null;

            g.setPaint(fillPaint);

            for (int i = 0; i < layout.getLines().size(); i++) {
                LayoutLine line = layout.getLines().get(i);
                int startX = line.getPoint().getX() + xDiff;
                int startY = line.hasVerticalGlyphs()
                        ? line.getPoint().getY() + layout.getBaselineOffset() + yDiff
                        : line.getPoint().getY() + layout.getBaselineOffset() + yDiff + i * layout.getLineHeight();

                if (context.getConfig().isDebug()) {
                    drawDebugBox(g, line, startX, startY, layout);
                }

                drawLine(config, g, line, startX, startY, useSharedFillPaint);
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

    private Paint resolveFillPaint(Graphics2D g, Color fillColor, Gradient gradient, PosterContext context,
                                   Dimension dimension, TextLayoutResult layout, int xDiff, int yDiff) {
        Color resolvedColor = fillColor != null ? fillColor : context.getConfig().getColor();
        if (gradient == null) {
            return resolvedColor;
        }
        return gradient.toGradient(resolveGradientBounds(g, layout, dimension, xDiff, yDiff));
    }

    private Dimension resolveGradientBounds(Graphics2D g, TextLayoutResult layout, Dimension fallback, int xDiff, int yDiff) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int i = 0; i < layout.getLines().size(); i++) {
            LayoutLine line = layout.getLines().get(i);
            int lineStartX = line.getPoint().getX() + xDiff;
            int lineTopY = line.getPoint().getY() + yDiff + i * layout.getLineHeight();

            if (line.hasVerticalGlyphs()) {
                for (VerticalGlyph glyph : line.getVerticalGlyphs()) {
                    Font glyphFont = glyph.getFont() != null ? glyph.getFont() : layout.getFont();
                    FontMetrics glyphMetrics = g.getFontMetrics(glyphFont);
                    int glyphLeft = lineStartX + glyph.getXOffset();
                    int glyphBaselineY = line.getPoint().getY() + layout.getBaselineOffset() + yDiff + glyph.getYOffset();
                    int glyphTop = glyphBaselineY - glyphMetrics.getAscent();
                    int glyphBottom = glyphBaselineY + glyphMetrics.getDescent();
                    minX = Math.min(minX, glyphLeft);
                    minY = Math.min(minY, glyphTop);
                    maxX = Math.max(maxX, glyphLeft + Math.max(1, glyph.getWidth()));
                    maxY = Math.max(maxY, Math.max(glyphTop + 1, glyphBottom));
                }
            } else if (line.hasRichFragments()) {
                for (RichTextFragment fragment : line.getRichFragments()) {
                    int fragmentLeft = lineStartX + fragment.getXOffset();
                    int fragmentBaselineY = lineTopY + layout.getBaselineOffset() + fragment.getBaselineShift();
                    FontMetrics fragmentMetrics = g.getFontMetrics(fragment.getFont());
                    int fragmentTop = fragmentBaselineY - fragmentMetrics.getAscent();
                    int fragmentBottom = fragmentBaselineY + fragmentMetrics.getDescent();
                    minX = Math.min(minX, fragmentLeft);
                    minY = Math.min(minY, fragmentTop);
                    maxX = Math.max(maxX, fragmentLeft + Math.max(1, fragment.getWidth()));
                    maxY = Math.max(maxY, Math.max(fragmentTop + 1, fragmentBottom));
                }
            } else if (line.getRenderWidth() > 0) {
                minX = Math.min(minX, lineStartX);
                minY = Math.min(minY, lineTopY);
                maxX = Math.max(maxX, lineStartX + line.getRenderWidth());
                maxY = Math.max(maxY, lineTopY + Math.max(1, layout.getLineHeight()));
            }
        }

        if (minX == Integer.MAX_VALUE || minY == Integer.MAX_VALUE || maxX <= minX || maxY <= minY) {
            return resolveContentBounds(layout, fallback, xDiff, yDiff);
        }

        return Dimension.builder()
                .point(Point.of(minX, minY))
                .width(maxX - minX)
                .height(maxY - minY)
                .build();
    }

    private Dimension resolveContentBounds(TextLayoutResult layout, Dimension fallback, int xDiff, int yDiff) {
        int contentWidth = layout.getContentWidth();
        int contentHeight = layout.getContentHeight();
        if (contentWidth <= 0 || contentHeight <= 0) {
            return fallback;
        }

        return Dimension.builder()
                .point(Point.of(
                        layout.getPoint().getX() + layout.getDecorationInsets().getLeft() + layout.getTextPadding().getLeft() + xDiff,
                        layout.getPoint().getY() + layout.getDecorationInsets().getTop() + layout.getTextPadding().getTop() + yDiff
                ))
                .width(contentWidth)
                .height(contentHeight)
                .build();
    }

    private void drawLine(TextElementConfig config, Graphics2D g, LayoutLine line, int startX, int startY,
                          boolean useSharedFillPaint) {
        if (line.hasVerticalGlyphs()) {
            drawVerticalLine(config, g, line, startX, startY, useSharedFillPaint);
        } else if (line.hasRichFragments()) {
            drawRichLine(config, g, line.getRichFragments(), startX, startY, useSharedFillPaint);
        } else {
            drawPlainLine(config, g, line, startX, startY);
        }
    }

    private void drawVerticalLine(TextElementConfig config, Graphics2D g, LayoutLine line, int startX, int startY,
                                  boolean useSharedFillPaint) {
        Paint textPaint = g.getPaint();
        Font savedFont = g.getFont();
        Stroke savedStroke = g.getStroke();

        for (VerticalGlyph glyph : line.getVerticalGlyphs()) {
            int drawX = startX + glyph.getXOffset();
            int drawY = line.getPoint().getY() + glyph.getYOffset() + startY - line.getPoint().getY();
            g.setFont(glyph.getFont() != null ? glyph.getFont() : savedFont);
            if (config.getShadow() != null) {
                g.setPaint(config.getShadow().getColor());
                g.drawString(glyph.getText(), drawX + config.getShadow().getOffsetX(),
                        drawY + config.getShadow().getOffsetY());
            }

            if (config.getStroke() != null) {
                g.setPaint(config.getStroke().getColor());
                g.setStroke(new BasicStroke(config.getStroke().getWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Shape outline = g.getFont().createGlyphVector(g.getFontRenderContext(), glyph.getText()).getOutline(drawX, drawY);
                g.draw(outline);
                g.setStroke(savedStroke);
            }

            g.setPaint(resolveGlyphPaint(textPaint, useSharedFillPaint, glyph.getColor()));
            g.drawString(glyph.getText(), drawX, drawY);

            if (glyph.isUnderline() || glyph.isStrikeThrough()) {
                drawVerticalGlyphDecoration(g, glyph, drawX, drawY);
            }
        }
        g.setFont(savedFont);
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
                              java.util.List<RichTextFragment> fragments, int startX, int startY,
                              boolean useSharedFillPaint) {
        Paint savedPaint = g.getPaint();
        Font savedFont = g.getFont();
        Stroke savedStroke = g.getStroke();

        for (RichTextFragment f : fragments) {
            int baselineY = startY + f.getBaselineShift();
            g.setFont(f.getFont());

            if (f.getBackgroundColor() != null) {
                drawRichFragmentBackground(g, f, startX, baselineY);
            }

            if (f.getShadow() != null) {
                g.setPaint(f.getShadow().getColor());
                drawTextWithSpacing(g, f.getText(), startX + f.getXOffset() + f.getShadow().getOffsetX(),
                        baselineY + f.getShadow().getOffsetY(), config.getLetterSpacing());
            }

            if (f.getStroke() != null) {
                g.setPaint(f.getStroke().getColor());
                g.setStroke(new BasicStroke(f.getStroke().getWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                drawTextOutline(g, f.getText(), startX + f.getXOffset(), baselineY, config.getLetterSpacing());
                g.setStroke(savedStroke);
            }

            g.setPaint(resolveGlyphPaint(savedPaint, useSharedFillPaint, f.getColor()));
            drawTextWithSpacing(g, f.getText(), startX + f.getXOffset(), baselineY, config.getLetterSpacing());

            if (f.isUnderline() || f.isStrikeThrough()) {
                drawRichDecoration(g, f, startX, baselineY);
            }
        }

        g.setFont(savedFont);
        g.setPaint(savedPaint);
    }

    private Paint resolveGlyphPaint(Paint blockPaint, boolean useSharedFillPaint, Color glyphColor) {
        if (useSharedFillPaint || glyphColor == null) {
            return blockPaint;
        }
        return glyphColor;
    }

    private void drawRichFragmentBackground(Graphics2D g, RichTextFragment fragment, int lineStartX, int baselineY) {
        FontMetrics fm = g.getFontMetrics(fragment.getFont());
        Paint savedPaint = g.getPaint();
        int drawX = lineStartX + fragment.getXOffset();
        int drawY = baselineY - fm.getAscent();
        g.setPaint(fragment.getBackgroundColor());
        g.fillRect(drawX, drawY, fragment.getWidth(), fm.getHeight());
        g.setPaint(savedPaint);
    }

    private void drawTextWithSpacing(Graphics2D g, String text, int x, int y, int letterSpacing) {
        java.util.List<String> units = splitRenderableUnits(text);
        if (letterSpacing == 0 || units.size() <= 1) {
            g.drawString(text, x, y);
            return;
        }

        FontMetrics fm = g.getFontMetrics();
        int cursorX = x;
        for (int i = 0; i < units.size(); i++) {
            String unit = units.get(i);
            g.drawString(unit, cursorX, y);
            if (i < units.size() - 1) {
                cursorX += fm.stringWidth(unit) + letterSpacing;
            }
        }
    }

    private void drawTextOutline(Graphics2D g, String text, int x, int y, int letterSpacing) {
        java.util.List<String> units = splitRenderableUnits(text);
        if (letterSpacing == 0 || units.size() <= 1) {
            Shape outline = g.getFont().createGlyphVector(g.getFontRenderContext(), text).getOutline(x, y);
            g.draw(outline);
            return;
        }

        FontMetrics fm = g.getFontMetrics();
        int cursorX = x;
        for (int i = 0; i < units.size(); i++) {
            String unit = units.get(i);
            Shape outline = g.getFont().createGlyphVector(g.getFontRenderContext(), unit).getOutline(cursorX, y);
            g.draw(outline);
            if (i < units.size() - 1) {
                cursorX += fm.stringWidth(unit) + letterSpacing;
            }
        }
    }

    private java.util.List<String> splitRenderableUnits(String text) {
        java.util.List<String> units = new java.util.ArrayList<String>();
        for (int i = 0; i < text.length(); ) {
            int codePoint = text.codePointAt(i);
            units.add(new String(Character.toChars(codePoint)));
            i += Character.charCount(codePoint);
        }
        return units;
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
    }

    private void drawRichDecoration(Graphics2D g, RichTextFragment fragment, int lineStartX, int baselineY) {
        if (fragment.getWidth() <= 0) {
            return;
        }

        FontMetrics fm = g.getFontMetrics(fragment.getFont());
        LineMetrics lm = fm.getLineMetrics(fragment.getText().isEmpty() ? "Ag" : fragment.getText(), g);
        int x = lineStartX + fragment.getXOffset();
        Stroke savedStroke = g.getStroke();

        if (fragment.isStrikeThrough()) {
            drawDecorationLine(g, x, baselineY, fragment.getWidth(),
                    lm.getStrikethroughOffset(), lm.getStrikethroughThickness());
        }
        if (fragment.isUnderline()) {
            drawDecorationLine(g, x, baselineY, fragment.getWidth(),
                    lm.getUnderlineOffset(), lm.getUnderlineThickness());
        }

        g.setStroke(savedStroke);
    }

    private void drawDecorationLine(Graphics2D g, int startX, int baselineY, int width,
                                    float offset, float thickness) {
        g.setStroke(new BasicStroke(Math.max(1.0f, thickness), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        float y = baselineY + offset;
        g.draw(new Line2D.Float(startX, y, startX + width, y));
    }

    private void drawVerticalGlyphDecoration(Graphics2D g, VerticalGlyph glyph, int drawX, int baselineY) {
        FontMetrics fm = g.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(glyph.getText().isEmpty() ? "Ag" : glyph.getText(), g);
        Paint savedPaint = g.getPaint();
        Stroke savedStroke = g.getStroke();

        if (glyph.isStrikeThrough()) {
            drawDecorationLine(g, drawX, baselineY, glyph.getWidth(),
                    lm.getStrikethroughOffset(), lm.getStrikethroughThickness());
        }
        if (glyph.isUnderline()) {
            drawDecorationLine(g, drawX, baselineY, glyph.getWidth(),
                    lm.getUnderlineOffset(), lm.getUnderlineThickness());
        }

        g.setStroke(savedStroke);
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
