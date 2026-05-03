package com.bytefuture.easy.poster.element.v2.text.style;

import lombok.Getter;

import java.util.Objects;

/**
 * 某段文本的最终样式结果
 */
@Getter
public class ResolvedTextRun {

    /**
     * 运行段文本
     */
    private final String text;

    /**
     * 运行段样式
     */
    private final ResolvedTextStyle style;

    /**
     * 创建文本运行段。
     *
     * @param text  运行段文本
     * @param style 运行段样式
     */
    public ResolvedTextRun(String text, ResolvedTextStyle style) {
        this.text = text == null ? "" : text;
        this.style = Objects.requireNonNull(style, "style");
    }
}
