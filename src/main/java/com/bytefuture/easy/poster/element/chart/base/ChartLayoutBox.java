package com.bytefuture.easy.poster.element.chart.base;

import lombok.Getter;
import lombok.Setter;

/**
 * Mutable chart layout box used during title/legend/plot layout.
 */
@Getter
@Setter
public class ChartLayoutBox {

    private final int left;

    private int top;

    private final int right;

    private final int bottom;

    public ChartLayoutBox(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void shiftTop(int delta) {
        this.top += delta;
    }

    public int width() {
        return Math.max(0, right - left);
    }

    public int height() {
        return Math.max(0, bottom - top);
    }
}
