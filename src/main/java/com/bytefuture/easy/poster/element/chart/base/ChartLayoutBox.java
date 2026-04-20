package com.bytefuture.easy.poster.element.chart.base;

/**
 * Mutable chart layout box used during title/legend/plot layout.
 */
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

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public void shiftTop(int delta) {
        this.top += delta;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int width() {
        return Math.max(0, right - left);
    }

    public int height() {
        return Math.max(0, bottom - top);
    }
}
