package com.bytefuture.easy.poster.element.v2.text.resolve;

import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextStyle;
import lombok.Getter;

import java.util.Objects;

/**
 * 已解析的文本运行段。
 * 每个运行段包含一段连续文本以及它的最终绘制样式。
 */
@Getter
public final class ResolvedTextRun {
    /**
     * 运行段文本。
     */
    private final String text;
    /** 运行段样式。 */
    private final ResolvedTextStyle style;

    /**
     * 创建文本运行段。
     *
     * @param text 运行段文本
     * @param style 运行段样式
     */
    public ResolvedTextRun(String text, ResolvedTextStyle style) {
        this.text = text == null ? "" : text;
        this.style = Objects.requireNonNull(style, "style");
    }
}
