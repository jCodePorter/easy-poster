package com.bytefuture.easy.poster.text.layout;

import lombok.Getter;

@Getter
public final class TextPaddingInsets {
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    public TextPaddingInsets(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
}
