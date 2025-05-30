package com.augrain.easy.poster.model;

import com.augrain.easy.poster.exception.PosterException;
import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.geometry.Dimension;
import com.augrain.easy.poster.utils.HexUtils;

import java.awt.*;
import java.util.Arrays;

/**
 * 渐变
 *
 * @author biaoy
 * @since 2025/03/24
 */
public class Gradient {

    /**
     * 颜色组
     */
    private final Color[] colors;

    /**
     * 渐变方向
     */
    private final GradientDirection direction;

    /**
     * 帧
     */
    private float[] fractions;

    private Gradient(Color[] colors, GradientDirection direction) {
        if (colors.length < 2) {
            throw new PosterException("colors.length < 2");
        }
        this.colors = colors;
        this.direction = direction;
    }

    public Gradient setFractions(float[] fractions) {
        if (fractions.length != colors.length) {
            throw new PosterException("fractions.length != colors.length");
        }
        this.fractions = fractions;
        return this;
    }

    public static Gradient of(Color[] colors, GradientDirection direction) {
        return new Gradient(colors, direction);
    }

    public static Gradient of(String[] colors, GradientDirection direction) {
        return new Gradient(transformColor(colors), direction);
    }

    private static Color[] transformColor(String[] colors) {
        return Arrays.stream(colors).map(HexUtils::hexToColor).toArray(Color[]::new);
    }

    public Paint toGradient(Dimension dimension) {
        Point[] coordinatePoints = direction.calcStartEnd(dimension);
        Point start = coordinatePoints[0];
        Point end = coordinatePoints[1];

        return new LinearGradientPaint(
                new java.awt.Point(start.getX(), start.getY()), new java.awt.Point(end.getX(), end.getY()), getFractions(), colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);
    }

    private float[] getFractions() {
        if (this.fractions != null) {
            return this.fractions;
        }

        float[] fractions = new float[colors.length];
        float interval = 1.0F / colors.length;
        for (int i = 0; i < fractions.length - 1; i++) {
            fractions[i] = interval * i;
        }
        fractions[fractions.length - 1] = 1.0F;
        return fractions;
    }
}
