package com.augrain.easy.canvas.geometry;

import lombok.Getter;
import lombok.ToString;

/**
 * 边距
 *
 * @author biaoy
 * @since 2025/03/03
 */
@Getter
@ToString
public class Margin {

    public static Margin DEFAULT = Margin.of(0);

    private int marginLeft = 0;

    private int marginRight = 0;

    private int marginTop = 0;

    private int marginBottom = 0;

    private Margin() {
    }

    private Margin(int marginLeft, int marginTop, int marginRight, int marginBottom) {
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
    }

    public Margin setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public Margin setMarginRight(int marginRight) {
        this.marginRight = marginRight;
        return this;
    }

    public Margin setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        return this;
    }

    public Margin setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
        return this;
    }

    public static Margin of() {
        return of(0);
    }

    public static Margin of(int margin) {
        return new Margin(margin, margin, margin, margin);
    }

    public static Margin of(int marginLeftRight, int marginTopBottom) {
        return new Margin(marginLeftRight, marginTopBottom, marginLeftRight, marginTopBottom);
    }

    public static Margin of(int marginLeft, int marginTop, int marginRight, int marginBottom) {
        return new Margin(marginLeft, marginTop, marginRight, marginBottom);
    }
}
