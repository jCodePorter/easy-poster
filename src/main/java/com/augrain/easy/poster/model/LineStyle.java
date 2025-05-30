package com.augrain.easy.poster.model;

import java.awt.*;
import java.util.function.Function;

/**
 * 线段风格
 *
 * @author biaoy
 * @since 2025/03/22
 */
public enum LineStyle {

    DASH(i -> {
        // 10 像素实线，5 像素空白
        float[] dashPattern = {10, 5};
        return new BasicStroke(i, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0);
    }),

    DOT(i -> {
        // 2 像素实线，5 像素空白
        float[] dotPattern = {2, 5};
        return new BasicStroke(i, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dotPattern, 0);
    }),

    DASH_DOT(i -> {
        // 10 像素实线，5 像素空白，2 像素实线，5 像素空白
        float[] dashDotPattern = {10, 5, 2, 5};
        return new BasicStroke(i, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10, dashDotPattern, 0);
    });

    private final Function<Integer, BasicStroke> convert;

    LineStyle(Function<Integer, BasicStroke> convert) {
        this.convert = convert;
    }

    public BasicStroke toStroke(int width) {
        return convert.apply(width);
    }
}
