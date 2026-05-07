package com.bytefuture.easy.poster.element.v2.text.pipeline;

import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLayoutEngine;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.element.v2.text.layout.VerticalTextLayoutEngine;
import com.bytefuture.easy.poster.element.v2.text.layout.VerticalTextLayoutResult;
import com.bytefuture.easy.poster.element.v2.text.render.TextRenderer;
import com.bytefuture.easy.poster.element.v2.text.render.VerticalTextRenderer;
import com.bytefuture.easy.poster.element.v2.text.style.ResolvedStyleContext;
import com.bytefuture.easy.poster.element.v2.text.style.TextStyleResolver;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.model.ColumnDirection;
import com.bytefuture.easy.poster.model.Gradient;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.WritingMode;

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
    private final TextRenderer renderer = new TextRenderer();

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

        // 2. 布局计算（根据书写模式选择引擎）
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
}