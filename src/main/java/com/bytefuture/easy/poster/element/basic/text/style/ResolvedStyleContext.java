package com.bytefuture.easy.poster.element.basic.text.style;

import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 样式解析结果容器
 * 包含样式解析阶段产出的所有数据，供布局计算阶段使用
 *
 * @author biaoy
 * @since 2026/04/27
 */
@Getter
public class ResolvedStyleContext {
    /**
     * 基础字体
     */
    private final Font baseFont;

    /**
     * 默认颜色
     */
    private final Color defaultColor;

    /**
     * 已解析的文本
     */
    private final List<ResolvedTextSpan> resolvedTextSpans;

    /**
     * 块级样式引用
     */
    private final TextBlockStyle blockStyle;

    /**
     * 创建样式解析结果
     *
     * @param baseFont     基础字体
     * @param defaultColor 默认颜色
     * @param runs         已解析的文本运行单元
     * @param blockStyle   块级样式
     */
    public ResolvedStyleContext(Font baseFont, Color defaultColor,
                                List<ResolvedTextSpan> runs, TextBlockStyle blockStyle) {
        this.baseFont = baseFont;
        this.defaultColor = defaultColor;
        this.resolvedTextSpans = Collections.unmodifiableList(new ArrayList<>(runs));
        this.blockStyle = blockStyle;
    }
}