package com.bytefuture.easy.poster.element.v2.text.render;

import com.bytefuture.easy.poster.element.v2.text.layout.CharCell;
import com.bytefuture.easy.poster.element.v2.text.layout.TextColumn;
import com.bytefuture.easy.poster.element.v2.text.layout.VerticalTextLayoutResult;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.ColumnDirection;
import com.bytefuture.easy.poster.model.Gradient;
import com.bytefuture.easy.poster.model.PosterContext;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.text.AttributedString;

/**
 * 竖排文本渲染器
 *
 * @author biaoy
 * @since 2026/05/07
 */
public class VerticalTextRenderer {

    /**
     * 将竖排布局结果绘制到当前画布
     *
     * @param context         海报上下文
     * @param dimension       元素尺寸
     * @param layout          竖排布局结果
     * @param rotate          旋转角度
     * @param gradient        文本块级渐变
     * @param columnDirection 列方向
     * @return 文本绘制起点
     */
    public Point render(PosterContext context, Dimension dimension, VerticalTextLayoutResult layout,
                        int rotate, Gradient gradient, ColumnDirection columnDirection) {
        Graphics2D graphics = context.getGraphics();
        AffineTransform original = graphics.getTransform();
        Paint originalPaint = graphics.getPaint();
        Color originalColor = graphics.getColor();

        if (rotate != 0) {
            double centerX = dimension.getPoint().getX() + dimension.getWidth() / 2.0d;
            double centerY = dimension.getPoint().getY() + dimension.getHeight() / 2.0d;
            graphics.rotate(Math.toRadians(rotate), centerX, centerY);
        }

        int startX = dimension.getPoint().getX();
        int startY = dimension.getPoint().getY() + dimension.getYOffset();
        int lineHeight = layout.getLineHeight();

        for (int i = 0; i < layout.getColumns().size(); i++) {
            TextColumn column = layout.getColumns().get(i);

            int columnX;
            if (columnDirection == ColumnDirection.RIGHT_TO_LEFT) {
                // 从右到左：第0列在最右侧
                columnX = startX + layout.getWidth() - (i + 1) * lineHeight;
            } else {
                // 从左到右：第0列在最左侧
                columnX = startX + i * lineHeight;
            }

            for (CharCell cell : column.getCharacters()) {
                graphics.setFont(cell.getStyle().getFont());
                applyTextPaint(graphics, cell, dimension, gradient);
                int drawX = columnX + (lineHeight - cell.getWidth()) / 2;
                int drawY = startY + cell.getOffsetY();
                drawCharWithDecorations(graphics, cell.getCharacter(), drawX, drawY,
                        cell.getStyle().isUnderline(), cell.getStyle().isStrikeThrough());
            }
        }

        if (rotate != 0) {
            graphics.setTransform(original);
        }
        graphics.setPaint(originalPaint);
        graphics.setColor(originalColor);
        return dimension.getPoint();
    }

    /** 设置文本填充画笔 */
    private void applyTextPaint(Graphics2D graphics, CharCell cell, Dimension dimension, Gradient gradient) {
        if (gradient != null && !cell.getStyle().isSpanColorOverride()) {
            graphics.setPaint(gradient.toGradient(dimension));
            return;
        }
        graphics.setColor(cell.getStyle().getColor());
    }

    /** 绘制单个字符并可选添加装饰线 */
    private void drawCharWithDecorations(Graphics2D graphics, String ch, int x, int baselineY,
                                         boolean underline, boolean strikeThrough) {
        if (underline || strikeThrough) {
            AttributedString attributedString = new AttributedString(ch);
            attributedString.addAttribute(TextAttribute.FONT, graphics.getFont());
            if (underline) {
                attributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 0, ch.length());
            }
            if (strikeThrough) {
                attributedString.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, 0, ch.length());
            }
            graphics.drawString(attributedString.getIterator(), x, baselineY);
            return;
        }
        graphics.drawString(ch, x, baselineY);
    }
}