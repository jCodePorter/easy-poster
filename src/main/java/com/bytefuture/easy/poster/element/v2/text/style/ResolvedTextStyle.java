package com.bytefuture.easy.poster.element.v2.text.style;

import lombok.Getter;

import java.awt.*;
import java.util.Objects;

/**
 * 已解析的最终文本样式。
 * 用于渲染阶段直接绘制，不再依赖级联合并。
 */
@Getter
public final class ResolvedTextStyle {
    /**
     * 最终字体
     */
    private final Font font;

    /**
     * 最终颜色
     */
    private final Color color;

    /**
     * 创建最终样式对象。
     *
     * @param font  最终字体
     * @param color 最终颜色
     */
    public ResolvedTextStyle(Font font, Color color) {
        this.font = Objects.requireNonNull(font, "font");
        this.color = Objects.requireNonNull(color, "color");
    }
}
