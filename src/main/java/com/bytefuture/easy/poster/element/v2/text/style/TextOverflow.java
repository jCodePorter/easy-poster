package com.bytefuture.easy.poster.element.v2.text.style;

/**
 * 文本超出最大行数后的处理方式
 *
 * @author biaoy
 * @since 2026/05/03
 */
public enum TextOverflow {
    /**
     * 直接裁剪，不追加省略符
     */
    CLIP,

    /**
     * 裁剪后追加省略符
     */
    ELLIPSIS
}
