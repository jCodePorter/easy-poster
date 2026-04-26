package com.bytefuture.easy.poster.element.v2.text.style;

import com.bytefuture.easy.poster.element.v2.text.resolve.ResolvedTextRun;
import com.bytefuture.easy.poster.model.TextSpan;

import java.awt.*;

/**
 * 文本样式解析器。
 * 按片段样式、块级样式、基础字体和默认颜色的优先级生成最终样式。
 *
 * <h3>样式合并规则：</h3>
 * <pre>
 * 字体名称: span.fontName > block.fontName > baseFont.family
 * 字体样式: span.fontStyle > block.fontStyle > baseFont.style
 * 字体大小: span.fontSize > block.fontSize > baseFont.size
 * 颜色:     span.color > block.color > defaultColor
 * </pre>
 */
public final class TextStyleResolver {

    /**
     * 解析单个文本片段的最终样式。
     * <p>
     * 样式合并遵循以下优先级（从高到低）：
     * <ol>
     *   <li>TextSpan 片段级样式 - 用户为特定文本设置的样式</li>
     *   <li>TextBlockStyle 块级样式 - TextElement 上配置的默认样式</li>
     *   <li>baseFont/defaultColor - 从全局配置或系统默认值推导的基础样式</li>
     * </ol>
     *
     * @param span         文本片段
     * @param blockStyle   块级默认样式
     * @param baseFont     基础字体
     * @param defaultColor 默认颜色
     * @return 已解析的文本运行段
     */
    public ResolvedTextRun resolve(TextSpan span, TextBlockStyle blockStyle, Font baseFont, Color defaultColor) {
        BaseTextStyle spanStyle = span.getSpanStyle();
        // 字体名称：片段 > 块级 > 基础字体族名
        String fontName = firstNonNull(spanStyle.getFontName(), blockStyle.getFontName(), baseFont.getFamily());
        // 字体样式：片段 > 块级 > 基础字体样式
        int fontStyle = firstNonNull(spanStyle.getFontStyle(), blockStyle.getFontStyle(), baseFont.getStyle());
        // 字体大小：片段 > 块级 > 基础字体大小
        int fontSize = firstNonNull(spanStyle.getFontSize(), blockStyle.getFontSize(), baseFont.getSize());
        // 颜色：片段 > 块级 > 默认颜色
        Color color = firstNonNull(spanStyle.getColor(), blockStyle.getColor(), defaultColor);
        return new ResolvedTextRun(span.getText(), new ResolvedTextStyle(new Font(fontName, fontStyle, fontSize), color));
    }

    /**
     * 返回三个值中第一个非空项。
     *
     * @param first  第一优先级值
     * @param second 第二优先级值
     * @param third  第三优先级值
     * @param <T>    值类型
     * @return 第一个非空值
     */
    private <T> T firstNonNull(T first, T second, T third) {
        if (first != null) {
            return first;
        }
        if (second != null) {
            return second;
        }
        return third;
    }
}
