package com.bytefuture.easy.poster.text.layout;

import lombok.Getter;

@Getter
public final class TextPaddingInsets {
    /** 左内边距。 */
    private final int left;
    /** 上内边距。 */
    private final int top;
    /** 右内边距。 */
    private final int right;
    /** 下内边距。 */
    private final int bottom;

    public TextPaddingInsets(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
}
