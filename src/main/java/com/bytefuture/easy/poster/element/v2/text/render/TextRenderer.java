package com.bytefuture.easy.poster.element.v2.text.render;

import com.bytefuture.easy.poster.element.v2.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLine;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.text.AttributedString;

/**
 * 文本渲染器。
 *
 * @author Codex
 * @since 2026/04/26
 */
public final class TextRenderer {

    /**
     * 将布局结果绘制到当前画布。
     *
     * @param context   海报上下文
     * @param dimension 元素尺寸
     * @param layout    文本布局结果
     * @param rotate    旋转角度
     * @return 文本绘制起点
     */
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
            int baselineY = dimension.getPoint().getY() + dimension.getYOffset() + i * layout.getLineHeight();
            for (TextLine.Segment segment : line.getSegments()) {
                graphics.setFont(segment.getStyle().getFont());
                graphics.setColor(segment.getStyle().getColor());
                drawRun(graphics, segment, dimension.getPoint().getX() + line.getOffsetX() + segment.getOffsetX(), baselineY);
            }
        }

        if (rotate != 0) {
            graphics.setTransform(original);
        }
        return dimension.getPoint();
    }

    /**
     * 按最终样式绘制单个文本片段。
     *
     * @param graphics  图形上下文
     * @param segment   文本片段
     * @param currentX  当前绘制起点 X 坐标
     * @param baselineY 当前绘制基线 Y 坐标
     */
    private void drawRun(Graphics2D graphics, TextLine.Segment segment, int currentX, int baselineY) {
        if (segment.getStyle().isUnderline() || segment.getStyle().isStrikeThrough()) {
            AttributedString attributedString = new AttributedString(segment.getText());
            attributedString.addAttribute(TextAttribute.FONT, segment.getStyle().getFont());
            if (segment.getStyle().isUnderline()) {
                attributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 0, segment.getText().length());
            }
            if (segment.getStyle().isStrikeThrough()) {
                attributedString.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, 0, segment.getText().length());
            }
            graphics.drawString(attributedString.getIterator(), currentX, baselineY);
            return;
        }
        graphics.drawString(segment.getText(), currentX, baselineY);
    }
}
