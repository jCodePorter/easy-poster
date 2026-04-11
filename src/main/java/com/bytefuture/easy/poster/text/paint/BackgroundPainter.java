package com.bytefuture.easy.poster.text.paint;

import com.bytefuture.easy.poster.element.basic.EnhanceTextElement;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.text.layout.TextLayoutResult;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.RoundRectangle2D;

public final class BackgroundPainter {

    public void paint(EnhanceTextElement element, Graphics2D graphics, Dimension dimension, TextLayoutResult layout) {
        if (element.getTextBackgroundColor() == null) {
            return;
        }

        Paint savedPaint = graphics.getPaint();
        graphics.setPaint(element.getTextBackgroundColor());
        graphics.fill(new RoundRectangle2D.Double(
                dimension.getPoint().getX() + layout.getDecorationInsets().getLeft(),
                dimension.getPoint().getY() + layout.getDecorationInsets().getTop(),
                layout.getBackgroundWidth(),
                layout.getBackgroundHeight(),
                element.getTextBackgroundArcWidth(),
                element.getTextBackgroundArcHeight()
        ));
        graphics.setPaint(savedPaint);
    }
}
