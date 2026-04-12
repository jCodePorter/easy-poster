package com.bytefuture.easy.poster.text.paint;

import com.bytefuture.easy.poster.element.basic.EnhanceTextElement;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.text.layout.LayoutLine;
import com.bytefuture.easy.poster.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.text.metrics.TextMetricsService;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;

public final class TextPainter {

    private static final TextMetricsService TEXT_METRICS = new TextMetricsService();
    private static final BackgroundPainter BACKGROUND_PAINTER = new BackgroundPainter();
    private static final DecorationPainter DECORATION_PAINTER = new DecorationPainter();
    private static final PlainTextPainter PLAIN_TEXT_PAINTER = new PlainTextPainter(TEXT_METRICS, DECORATION_PAINTER);
    private static final RichTextPainter RICH_TEXT_PAINTER = new RichTextPainter(TEXT_METRICS, DECORATION_PAINTER);

    public Point paint(EnhanceTextElement element, PosterContext context, Dimension dimension,
                       TextLayoutResult layout) {
        Graphics2D graphics = context.getGraphics();

        // 渐变依赖最终绘制区域，因此在真正绘制前再应用。
        element.applyGradient(context, dimension);
        graphics.setFont(layout.getFont());

        int xDiff = dimension.getPoint().getX() - layout.getPoint().getX();
        int yDiff = dimension.getPoint().getY() - layout.getPoint().getY();

        AffineTransform savedTransform = graphics.getTransform();
        Shape savedClip = graphics.getClip();
        try {
            if (element.getRotate() != 0) {
                // 旋转围绕文本块中心进行，这样与外层测得的旋转包围盒保持一致。
                AffineTransform rotatedTransform = new AffineTransform(savedTransform);
                rotatedTransform.rotate(Math.toRadians(element.getRotate()),
                        dimension.getPoint().getX() + dimension.getWidth() / 2.0,
                        dimension.getPoint().getY() + dimension.getHeight() / 2.0);
                graphics.setTransform(rotatedTransform);
            }
            BACKGROUND_PAINTER.paint(element, graphics, dimension, layout);
            if (layout.isClipOverflow()) {
                // 裁剪区域只覆盖文本内容区，不包含装饰外扩和背景内边距。
                graphics.clip(new Rectangle(
                        dimension.getPoint().getX() + layout.getDecorationInsets().getLeft() + layout.getTextPadding().getLeft(),
                        dimension.getPoint().getY() + layout.getDecorationInsets().getTop() + layout.getTextPadding().getTop(),
                        layout.getContentWidth(),
                        layout.getContentHeight()
                ));
            }

            for (int i = 0; i < layout.getLines().size(); i++) {
                LayoutLine line = layout.getLines().get(i);
                int startX = line.getPoint().getX() + xDiff;
                // 每一行的绘制 Y 基于块顶部、基线偏移和行高累加得到。
                int startY = line.getPoint().getY() + layout.getBaselineOffset() + yDiff + i * layout.getLineHeight();

                if (context.getConfig().isDebug()) {
                    drawDebugLine(element, context, layout, line, startX, startY);
                }
                paintLine(element, graphics, line, layout, startX, startY);
            }
            return dimension.getPoint();
        } finally {
            graphics.setClip(savedClip);
            graphics.setTransform(savedTransform);
        }
    }

    private void drawDebugLine(EnhanceTextElement element, PosterContext context,
                               TextLayoutResult layout,
                               LayoutLine line, int startX, int startY) {
        Graphics2D graphics = context.getGraphics();
        FontMetrics fontMetrics = graphics.getFontMetrics(layout.getFont());
        LineMetrics lineMetrics = fontMetrics.getLineMetrics(resolveMetricsSampleText(line.getText()), graphics);
        float ascent = lineMetrics.getAscent();
        int diffHeight = (layout.getLineHeight() - fontMetrics.getHeight()) / 2;

        int topY = (int) (startY - ascent - diffHeight);
        if (element.getPosition() instanceof AbsolutePosition) {
            // TOP/BOTTOM 基线下，调试框需要跟随锚点语义修正顶部位置。
            if (layout.getBaseLine() == BaseLine.TOP) {
                topY += diffHeight;
            } else if (layout.getBaseLine() == BaseLine.BOTTOM) {
                topY -= diffHeight;
            }
        }
        graphics.drawRect(startX, topY, line.getRenderWidth(), layout.getLineHeight());
    }

    private void paintLine(EnhanceTextElement element, Graphics2D graphics,
                           LayoutLine line, TextLayoutResult layout, int startX, int startY) {
        if (line.hasRichFragments()) {
            // 富文本按片段分别绘制，不走普通文本绘制逻辑。
            RICH_TEXT_PAINTER.paint(element, graphics, line.getRichFragments(), startX, startY);
            return;
        }
        PLAIN_TEXT_PAINTER.paint(element, graphics, line, layout, startX, startY);
    }

    private String resolveMetricsSampleText(String content) {
        if (content == null || content.isEmpty()) {
            return "Ag";
        }
        return content;
    }
}
