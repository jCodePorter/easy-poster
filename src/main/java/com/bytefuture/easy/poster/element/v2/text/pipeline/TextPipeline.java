package com.bytefuture.easy.poster.element.v2.text.pipeline;

import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLayoutEngine;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.element.v2.text.layout.TextMeasurer;
import com.bytefuture.easy.poster.element.v2.text.render.TextRenderer;
import com.bytefuture.easy.poster.element.v2.text.resolve.ResolvedStyleContext;
import com.bytefuture.easy.poster.element.v2.text.style.TextStyleResolver;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.model.PosterContext;

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
     * 布局计算引擎
     */
    private final TextLayoutEngine layoutEngine = new TextLayoutEngine();

    /**
     * 渲染绘制器
     */
    private final TextRenderer renderer = new TextRenderer();

    /**
     * 执行样式解析 + 布局计算流程
     *
     * @param element      文本元素
     * @param position     元素位置
     * @param context      海报上下文
     * @param posterWidth  海报宽度
     * @param posterHeight 海报高度
     * @return 布局结果
     */
    public TextLayoutResult resolveLayout(TextElement element, Position position,
                                          PosterContext context, int posterWidth, int posterHeight) {
        // 1. 样式解析
        ResolvedStyleContext styleContext = styleResolver.resolve(element, context);

        // 2. 布局计算
        return layoutEngine.layout(styleContext, position, context.getGraphics(), posterWidth, posterHeight);
    }

    /**
     * 执行渲染绘制流程
     *
     * @param context   海报上下文
     * @param dimension 元素尺寸
     * @param layout    布局结果
     * @param rotate    旋转角度
     * @return 绘制起点
     */
    public Point render(PosterContext context, Dimension dimension,
                        TextLayoutResult layout, int rotate) {
        return renderer.render(context, dimension, layout, rotate);
    }
}