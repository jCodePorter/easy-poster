package com.bytefuture.easy.poster.element.v2.text.layout;

import lombok.Getter;

@Getter
public final class TextDecorationInsets {
    /** 左侧因描边、阴影、装饰线等产生的外扩距离。 */
    private final int left;
    /** 上侧因描边、阴影、装饰线等产生的外扩距离。 */
    private final int top;
    /** 右侧因描边、阴影、装饰线等产生的外扩距离。 */
    private final int right;
    /** 下侧因描边、阴影、装饰线等产生的外扩距离。 */
    private final int bottom;

    public TextDecorationInsets(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
}
