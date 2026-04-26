package com.bytefuture.easy.poster.element.v2;

import com.bytefuture.easy.poster.element.v2.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLine;
import com.bytefuture.easy.poster.element.v2.text.resolve.ResolvedTextRun;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public final class TextRenderer {

    public Point render(PosterContext context, Dimension dimension, TextLayoutResult layout, int rotate) {
        Graphics2D graphics = context.getGraphics();
        AffineTransform original = graphics.getTransform();
        if (rotate != 0) {
            double centerX = dimension.getPoint().getX() + dimension.getWidth() / 2.0d;
            double centerY = dimension.getPoint().getY() + dimension.getHeight() / 2.0d;
            graphics.rotate(Math.toRadians(rotate), centerX, centerY);
        }

        for (int i = 0; i < layout.getLines().size(); i++) {
            TextLine line = layout.getLines().get(i);
            int currentX = dimension.getPoint().getX() + line.getOffsetX();
            int baselineY = dimension.getPoint().getY() + dimension.getYOffset() + i * layout.getLineHeight();
            for (ResolvedTextRun run : line.getRuns()) {
                graphics.setFont(run.getStyle().getFont());
                graphics.setColor(run.getStyle().getColor());
                graphics.drawString(run.getText(), currentX, baselineY);
                FontMetrics metrics = graphics.getFontMetrics(run.getStyle().getFont());
                currentX += metrics.stringWidth(run.getText());
            }
        }

        if (rotate != 0) {
            graphics.setTransform(original);
        }
        return dimension.getPoint();
    }
}
