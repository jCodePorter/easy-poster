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
     * 创建最终样式对象
     *
     * @param font          最终字体
     * @param color         最终颜色
     * @param underline     是否绘制下划线
     * @param strikeThrough 是否绘制删除线
     * @param letterSpacing 字间距
     */
    public ResolvedTextStyle(Font font, Color color, boolean underline, boolean strikeThrough, int letterSpacing) {
        this.font = Objects.requireNonNull(font, "font");
        this.color = Objects.requireNonNull(color, "color");
        this.underline = underline;
        this.strikeThrough = strikeThrough;
        this.letterSpacing = letterSpacing;
    }
}
