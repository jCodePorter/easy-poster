package com.bytefuture.easy.poster.element.v2.text.resolve;

import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextStyle;
import com.bytefuture.easy.poster.element.v2.text.style.TextBlockStyle;
import com.bytefuture.easy.poster.element.v2.text.style.TextSpanStyle;
import com.bytefuture.easy.poster.model.TextSpan;

import java.awt.*;

/**
 * 文本样式解析器。
 * 按片段样式、块级样式、基础字体和默认颜色的优先级生成最终样式。
 */
public final class TextStyleResolver {

    /**
     * 解析单个文本片段的最终样式。
     *
     * @param span         文本片段
     * @param blockStyle   块级默认样式
     * @param baseFont     基础字体
     * @param defaultColor 默认颜色
     * @return 已解析的文本运行段
     */
    public ResolvedTextRun resolve(TextSpan span, TextBlockStyle blockStyle, Font baseFont, Color defaultColor) {
        TextSpanStyle spanStyle = span.getStyle();
        String fontName = firstNonNull(spanStyle.getFontName(), blockStyle.getFontName(), baseFont.getFamily());
        int fontStyle = firstNonNull(spanStyle.getFontStyle(), blockStyle.getFontStyle(), Integer.valueOf(baseFont.getStyle())).intValue();
        int fontSize = firstNonNull(spanStyle.getFontSize(), blockStyle.getFontSize(), Integer.valueOf(baseFont.getSize())).intValue();
        Color color = firstNonNull(spanStyle.getColor(), blockStyle.getColor(), defaultColor);
        return new ResolvedTextRun(span.getText(), new ResolvedTextStyle(new Font(fontName, fontStyle, fontSize), color));
    }

    /**
     * 返回三个值中第一个非空项。
     *
     * @param first 第一优先级值
     * @param second 第二优先级值
     * @param third 第三优先级值
     * @param <T> 值类型
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
