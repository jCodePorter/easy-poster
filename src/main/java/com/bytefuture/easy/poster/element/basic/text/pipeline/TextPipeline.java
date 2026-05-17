package com.bytefuture.easy.poster.element.basic.text.pipeline;

import com.bytefuture.easy.poster.element.basic.TextElement;
import com.bytefuture.easy.poster.element.basic.text.layout.TextLayoutEngine;
import com.bytefuture.easy.poster.element.basic.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.element.basic.text.layout.VerticalTextLayoutEngine;
import com.bytefuture.easy.poster.element.basic.text.layout.VerticalTextLayoutResult;
import com.bytefuture.easy.poster.element.basic.text.render.HorizontalTextRenderer;
import com.bytefuture.easy.poster.element.basic.text.render.VerticalTextRenderer;
import com.bytefuture.easy.poster.element.basic.text.style.ResolvedStyleContext;
import com.bytefuture.easy.poster.element.basic.text.style.ResolvedTextSpan;
import com.bytefuture.easy.poster.element.basic.text.style.TextStyleResolver;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.model.ColumnDirection;
import com.bytefuture.easy.poster.model.Gradient;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.WritingMode;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;

/**
 * 文本渲染流程编排器
 * 协调样式解析、布局计算、渲染绘制三大流程
 *
 * @author biaoy
 * @since 2026/04/27
 */
public class TextPipeline {
    /**
     * 样式解析器
     */
    private final TextStyleResolver styleResolver = new TextStyleResolver();

    /**
     * 横排布局引擎
     */
    private final TextLayoutEngine layoutEngine = new TextLayoutEngine();

    /**
     * 竖排布局引擎
     */
    private final VerticalTextLayoutEngine verticalLayoutEngine = new VerticalTextLayoutEngine();

    /**
     * 横排渲染器
     */
    private final HorizontalTextRenderer renderer = new HorizontalTextRenderer();

    /**
     * 竖排渲染器
     */
    private final VerticalTextRenderer verticalRenderer = new VerticalTextRenderer();

    /**
     * 执行样式解析 + 布局计算流程
     *
     * @param element      文本元素
     * @param position     元素位置
     * @param context      海报上下文
     * @param posterWidth  海报宽度
     * @param posterHeight 海报高度
     * @return 布局结果（横排返回 TextLayoutResult，竖排返回 VerticalTextLayoutResult）
     */
    public Object resolveLayout(TextElement element, Position position,
                                PosterContext context, int posterWidth, int posterHeight) {
        // 1. 样式解析
        ResolvedStyleContext styleContext = styleResolver.resolve(element, context);

        // 2. 自适应文本大小调整（仅单 TextSpan 横排文本）
        styleContext = applyAutoFit(styleContext, context.getGraphics());

        // 3. 布局计算（根据书写模式选择引擎）
        WritingMode writingMode = element.getBlockStyle().getWritingMode();
        if (writingMode == WritingMode.VERTICAL) {
            return verticalLayoutEngine.layout(styleContext, position, context.getGraphics(), posterWidth, posterHeight);
        }
        return layoutEngine.layout(styleContext, position, context.getGraphics(), posterWidth, posterHeight);
    }

    /**
     * 执行横排渲染绘制流程
     *
     * @param context   海报上下文
     * @param dimension 元素尺寸
     * @param layout    布局结果
     * @param rotate    旋转角度
     * @param gradient  渐变色
     * @return 绘制起点
     */
    public Point render(PosterContext context, Dimension dimension,
                        TextLayoutResult layout, int rotate, Gradient gradient) {
        return renderer.render(context, dimension, layout, rotate, gradient);
    }

    /**
     * 执行竖排渲染绘制流程
     *
     * @param context         海报上下文
     * @param dimension       元素尺寸
     * @param layout          竖排布局结果
     * @param rotate          旋转角度
     * @param gradient        渐变色
     * @param columnDirection 列方向
     * @return 绘制起点
     */
    public Point renderVertical(PosterContext context, Dimension dimension,
                                VerticalTextLayoutResult layout, int rotate, Gradient gradient,
                                ColumnDirection columnDirection) {
        return verticalRenderer.render(context, dimension, layout, rotate, gradient, columnDirection);
    }

    /**
     * 自适应文本大小调整
     * 当启用 autoFitText 且为单 TextSpan 横排文本时，自动缩小字体以适应目标宽度
     *
     * @param styleContext 样式解析结果
     * @param graphics     图形上下文
     * @return 调整后的样式解析结果
     */
    private ResolvedStyleContext applyAutoFit(ResolvedStyleContext styleContext, Graphics2D graphics) {
        if (!styleContext.getBlockStyle().isAutoFitText()) {
            return styleContext;
        }

        // 仅对单 TextSpan 生效
        List<ResolvedTextSpan> spans = styleContext.getResolvedTextSpans();
        if (spans.size() != 1) {
            return styleContext;
        }

        // 仅对横排文本生效
        if (styleContext.getBlockStyle().getWritingMode() != WritingMode.HORIZONTAL) {
            return styleContext;
        }

        int targetWidth = styleContext.getBlockStyle().getAutoFitTargetWidth();
        int minFontSize = styleContext.getBlockStyle().getAutoFitMinFontSize();

        ResolvedTextSpan span = spans.get(0);
        Font currentFont = span.getStyle().getFont();
        String text = span.getText();

        // 测量当前字体下的文本宽度
        FontMetrics fm = graphics.getFontMetrics(currentFont);
        Rectangle2D textBounds = fm.getStringBounds(text, graphics);
        int textWidth = (int) textBounds.getWidth();

        // 如果文本宽度未超出目标宽度，无需调整
        if (textWidth <= targetWidth) {
            return styleContext;
        }

        // 计算合适的字体大小
        int currentFontSize = currentFont.getSize();
        double scaleRatio = (double) targetWidth / textWidth;
        int optimalFontSize = (int) Math.floor(currentFontSize * scaleRatio);
        optimalFontSize = Math.max(optimalFontSize, minFontSize);

        // 重新测量调整后的文本宽度
        Font adjustedFont = new Font(currentFont.getFamily(), currentFont.getStyle(), optimalFontSize);
        FontMetrics adjustedFm = graphics.getFontMetrics(adjustedFont);
        Rectangle2D adjustedBounds = adjustedFm.getStringBounds(text, graphics);
        int adjustedWidth = (int) adjustedBounds.getWidth();

        // 如果即使使用最小字体仍然超出宽度，则启用自动换行作为兜底
        if (optimalFontSize == minFontSize && adjustedWidth > targetWidth) {
            styleContext.getBlockStyle().maxTextWidth(targetWidth);
        }

        // 更新块级字体大小
        styleContext.getBlockStyle().setFontSize(optimalFontSize);

        // 构造调整后的新 ResolvedTextSpan（保留原样式属性，仅替换字体）
        ResolvedTextSpan adjustedSpan = new ResolvedTextSpan(text,
                span.getStyle().withFont(adjustedFont));

        return new ResolvedStyleContext(
                adjustedFont,
                styleContext.getDefaultColor(),
                Collections.singletonList(adjustedSpan),
                styleContext.getBlockStyle()
        );
    }
}
