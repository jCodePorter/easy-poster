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
 * 文本渲染器
 *
 * @author biaoy
 * @since 2026/04/26
 */
public class TextRenderer {

    /**
     * 将布局结果绘制到当前画布
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
     * 按最终样式绘制单个文本片段
     *
     * @param graphics  图形上下文
     * @param segment   文本片段
     * @param currentX  当前绘制起点 X 坐标
     * @param baselineY 当前绘制基线 Y 坐标
     */
    private void drawRun(Graphics2D graphics, TextLine.Segment segment, int currentX, int baselineY) {
        graphics.setFont(segment.getStyle().getFont());
        graphics.setColor(segment.getStyle().getColor());

        int letterSpacing = segment.getLetterSpacing();

        // 字间距为 0 或文本为空/单字符时，使用整串绘制（性能优化）
        if (letterSpacing <= 0 || segment.getText().codePointCount(0, segment.getText().length()) <= 1) {
            drawTextWithDecorations(graphics, segment.getText(), currentX, baselineY,
                    segment.getStyle().isUnderline(), segment.getStyle().isStrikeThrough());
            return;
        }

        // 逐字符绘制
        int x = currentX;
        String text = segment.getText();
        FontMetrics fm = graphics.getFontMetrics();

        for (int i = 0; i < text.length(); ) {
            int codePoint = text.codePointAt(i);
            String ch = new String(Character.toChars(codePoint));
            int charWidth = fm.stringWidth(ch);

            drawTextWithDecorations(graphics, ch, x, baselineY,
                    segment.getStyle().isUnderline(), segment.getStyle().isStrikeThrough());

            x += charWidth + letterSpacing;
            i += Character.charCount(codePoint);
        }
    }

    /**
     * 绘制文本并可选添加装饰线
     *
     * @param graphics      图形上下文
     * @param text          文本内容
     * @param x             X 坐标
     * @param baselineY     基线 Y 坐标
     * @param underline     是否绘制下划线
     * @param strikeThrough 是否绘制删除线
     */
    private void drawTextWithDecorations(Graphics2D graphics, String text, int x, int baselineY,
                                         boolean underline, boolean strikeThrough) {
        if (underline || strikeThrough) {
            AttributedString attributedString = new AttributedString(text);
            attributedString.addAttribute(TextAttribute.FONT, graphics.getFont());
            if (underline) {
                attributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 0, text.length());
            }
            if (strikeThrough) {
                attributedString.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, 0, text.length());
            }
            graphics.drawString(attributedString.getIterator(), x, baselineY);
            return;
        }
        graphics.drawString(text, x, baselineY);
    }
}
