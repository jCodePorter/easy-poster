package com.bytefuture.easy.poster.element.v2.text.style;

import lombok.Getter;

import java.awt.*;
import java.util.Objects;

/**
 * 已解析的最终文本样式
 * 用于渲染阶段直接绘制，不再依赖级联合并
 *
 * @author biaoy
 * @since 2026/04/26
 */
@Getter
public class ResolvedTextStyle {
    /**
     * 最终字体
     */
    private final Font font;

    /**
     * 最终颜色
     */
    private final Color color;

    /**
     * 是否来自 span 显式颜色配置
     */
    private final boolean spanColorOverride;

    /**
     * 是否绘制下划线
     */
    private final boolean underline;

    /**
     * 是否绘制删除线
     */
    private final boolean strikeThrough;

    /**
     * 字间距
     */
    private final int letterSpacing;

    /**
     * 文本背景色
     */
    private final Color backgroundColor;

    /**
     * 文本背景内边距
     */
    private final int backgroundPadding;

    /**
     * 文本背景圆角半径
     */
    private final int backgroundRadius;

    /**
     * 创建最终样式对象
     *
     * @param font          最终字体
     * @param color         最终颜色
     * @param spanColorOverride 是否来自 span 显式颜色配置
     * @param underline         是否绘制下划线
     * @param strikeThrough     是否绘制删除线
     * @param letterSpacing     字间距
     */
    public ResolvedTextStyle(Font font, Color color, boolean spanColorOverride,
                             boolean underline, boolean strikeThrough, int letterSpacing,
                             Color backgroundColor, int backgroundPadding, int backgroundRadius) {
        this.font = Objects.requireNonNull(font, "font");
        this.color = Objects.requireNonNull(color, "color");
        this.spanColorOverride = spanColorOverride;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
        this.letterSpacing = letterSpacing;
        this.backgroundColor = backgroundColor;
        this.backgroundPadding = backgroundPadding;
        this.backgroundRadius = backgroundRadius;
    }
}
