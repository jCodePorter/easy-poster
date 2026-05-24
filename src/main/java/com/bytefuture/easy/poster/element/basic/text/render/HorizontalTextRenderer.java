package com.bytefuture.easy.poster.element.basic.text.render;

import com.bytefuture.easy.poster.element.basic.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.element.basic.text.layout.TextLine;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.Gradient;
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
public class HorizontalTextRenderer {

    /**
     * 将布局结果绘制到当前画布
     *
     * @param context   海报上下文
     * @param dimension 元素尺寸
     * @param layout    文本布局结果
     * @param rotate    旋转角度
     * @param gradient  文本块级渐变
     * @return 文本绘制起点
     */
    public Point render(PosterContext context, Dimension dimension, TextLayoutResult layout, int rotate, Gradient gradient) {
        Graphics2D graphics = context.getGraphics();
        AffineTransform original = graphics.getTransform();
        Paint originalPaint = graphics.getPaint();
        Color originalColor = graphics.getColor();
        if (rotate != 0) {
            double centerX = dimension.getPoint().getX() + dimension.getWidth() / 2.0d;
            double centerY = dimension.getPoint().getY() + dimension.getHeight() / 2.0d;
            graphics.rotate(Math.toRadians(rotate), centerX, centerY);
        }

        for (int i = 0; i < layout.getLines().size(); i++) {
            TextLine line = layout.getLines().get(i);
            int baselineY = dimension.getPoint().getY() + dimension.getYOffset() + i * layout.getLineHeight();
            int lineStartX = dimension.getPoint().getX() + line.getOffsetX();
            drawLineBackgrounds(graphics, line, lineStartX, baselineY, layout.getLineHeight());
            for (TextLine.Segment segment : line.getSegments()) {
                graphics.setFont(segment.getStyle().getFont());
                applyTextPaint(graphics, segment, dimension, gradient);
                drawRun(graphics, segment, lineStartX + segment.getOffsetX(),
                        baselineY, layout.getLineHeight());
            }
        }

        if (rotate != 0) {
            graphics.setTransform(original);
        }
        graphics.setPaint(originalPaint);
        graphics.setColor(originalColor);
        return dimension.getPoint();
    }

    /**
     * 设置文本填充画笔
     *
     * @param graphics  图形上下文
     * @param segment   当前文本片段
     * @param dimension 文本元素尺寸
     * @param gradient  文本块级渐变
     */
    private void applyTextPaint(Graphics2D graphics, TextLine.Segment segment, Dimension dimension, Gradient gradient) {
        if (gradient != null && !segment.getStyle().isSpanColorOverride()) {
            graphics.setPaint(gradient.toGradient(dimension));
            return;
        }
        graphics.setColor(segment.getStyle().getColor());
    }

    /**
     * 按最终样式绘制单个文本片段
     *
     * @param graphics  图形上下文
     * @param segment   文本片段
     * @param currentX  当前绘制起点 X 坐标
     * @param baselineY 当前绘制基线 Y 坐标
     */
    private void drawRun(Graphics2D graphics, TextLine.Segment segment, int currentX, int baselineY, int lineHeight) {
        graphics.setFont(segment.getStyle().getFont());

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
     * 绘制单行文本背景
     *
     * @param graphics   图形上下文
     * @param line       当前文本行
     * @param lineStartX 当前行起点 X 坐标
     * @param baselineY  当前绘制基线 Y 坐标
     * @param lineHeight 当前行高
     */
    private void drawLineBackgrounds(Graphics2D graphics, TextLine line, int lineStartX, int baselineY, int lineHeight) {
        BackgroundBox current = null;
        for (TextLine.Segment segment : line.getSegments()) {
            BackgroundBox next = resolveBackgroundBox(graphics, segment, lineStartX + segment.getOffsetX(), baselineY, lineHeight);
            if (next == null) {
                drawBackground(graphics, current);
                current = null;
                continue;
            }
            if (current != null && current.canMerge(next)) {
                current = current.merge(next);
            } else {
                drawBackground(graphics, current);
                current = next;
            }
        }
        drawBackground(graphics, current);
    }

    /**
     * 绘制文本背景
     *
     * @param graphics   图形上下文
     * @param segment    文本片段
     * @param currentX   当前绘制起点 X 坐标
     * @param baselineY  当前绘制基线 Y 坐标
     * @param lineHeight 当前行高
     */
    private BackgroundBox resolveBackgroundBox(Graphics2D graphics, TextLine.Segment segment, int currentX,
                                               int baselineY, int lineHeight) {
        if (segment.getStyle().getBackgroundColor() == null) {
            return null;
        }
        FontMetrics fontMetrics = graphics.getFontMetrics(segment.getStyle().getFont());
        int padding = segment.getStyle().getBackgroundPadding();
        int desiredHeight = fontMetrics.getHeight() + padding * 2;
        int backgroundHeight = Math.min(desiredHeight, lineHeight);
        int backgroundY = baselineY - fontMetrics.getAscent() - padding;
        if (backgroundHeight < desiredHeight) {
            backgroundY += (desiredHeight - backgroundHeight) / 2;
        }
        return new BackgroundBox(
                currentX - padding,
                backgroundY,
                segment.getWidth() + padding * 2,
                backgroundHeight,
                segment.getStyle().getBackgroundRadius(),
                segment.getStyle().getBackgroundColor()
        );
    }

    /**
     * 绘制背景矩形
     *
     * @param graphics 图形上下文
     * @param x        背景 X 坐标
     * @param y        背景 Y 坐标
     * @param width    背景宽度
     * @param height   背景高度
     * @param radius   背景圆角半径
     * @param color    背景颜色
     */
    private void drawBackground(Graphics2D graphics, int x, int y, int width, int height, int radius, Color color) {
        Color originalColor = graphics.getColor();
        graphics.setColor(color);
        if (radius > 0) {
            graphics.fillRoundRect(x, y, width, height, radius * 2, radius * 2);
        } else {
            graphics.fillRect(x, y, width, height);
        }
        graphics.setColor(originalColor);
    }

    /**
     * 绘制背景盒
     *
     * @param graphics   图形上下文
     * @param background 背景盒
     */
    private void drawBackground(Graphics2D graphics, BackgroundBox background) {
        if (background == null) {
            return;
        }
        drawBackground(graphics, background.x, background.y, background.width, background.height,
                background.radius, background.color);
    }

    /**
     * 同行连续背景盒
     *
     * @author biaoy
     * @since 2026/04/28
     */
    private static final class BackgroundBox {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final int radius;
        private final Color color;

        private BackgroundBox(int x, int y, int width, int height, int radius, Color color) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.radius = radius;
            this.color = color;
        }

        private boolean canMerge(BackgroundBox other) {
            return other != null
                    && this.radius == other.radius
                    && this.y == other.y
                    && this.height == other.height
                    && this.color.equals(other.color)
                    && other.x <= getRight();
        }

        private BackgroundBox merge(BackgroundBox other) {
            int newX = Math.min(this.x, other.x);
            int newRight = Math.max(getRight(), other.getRight());
            return new BackgroundBox(newX, this.y, newRight - newX, this.height, this.radius, this.color);
        }

        private int getRight() {
            return this.x + this.width;
        }
    }

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
