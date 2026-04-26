package com.bytefuture.easy.poster.element.v2;

import com.bytefuture.easy.poster.element.v2.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLine;
import com.bytefuture.easy.poster.element.v2.text.resolve.ResolvedTextRun;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * 文本渲染器。
 * 根据布局结果逐行逐段绘制文本，并处理旋转矩阵恢复。
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
            // 以元素中心点旋转，避免文本绕左上角旋转导致位置漂移。
            double centerX = dimension.getPoint().getX() + dimension.getWidth() / 2.0d;
            double centerY = dimension.getPoint().getY() + dimension.getHeight() / 2.0d;
            graphics.rotate(Math.toRadians(rotate), centerX, centerY);
        }

        for (int i = 0; i < layout.getLines().size(); i++) {
            TextLine line = layout.getLines().get(i);
            int currentX = dimension.getPoint().getX() + line.getOffsetX();
            int baselineY = dimension.getPoint().getY() + dimension.getYOffset() + i * layout.getLineHeight();
            for (ResolvedTextRun run : line.getRuns()) {
                // 每个运行段独立设置字体和颜色，以支持富文本混排。
                graphics.setFont(run.getStyle().getFont());
                graphics.setColor(run.getStyle().getColor());
                graphics.drawString(run.getText(), currentX, baselineY);
                FontMetrics metrics = graphics.getFontMetrics(run.getStyle().getFont());
                currentX += metrics.stringWidth(run.getText());
            }
        }

        if (rotate != 0) {
            // 恢复原始变换矩阵，防止影响后续元素绘制。
            graphics.setTransform(original);
        }
        return dimension.getPoint();
    }
}
