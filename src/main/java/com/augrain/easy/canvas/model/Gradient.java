package com.augrain.easy.canvas.model;

import com.augrain.easy.canvas.exception.CanvasException;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;
import com.augrain.easy.canvas.utils.HexUtils;

import java.awt.*;
import java.util.Arrays;

/**
 * 渐变
 *
 * @author biaoy
 * @since 2025/03/24
 */
public class Gradient {

    private Color[] colors;

    private GradientDirection direction;

    private Gradient(Color[] colors, GradientDirection direction) {
        if (colors.length < 2) {
            throw new CanvasException("colors.length < 2");
        }
        this.colors = colors;
        this.direction = direction;
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
        CoordinatePoint[] coordinatePoints = direction.calcStartEnd(dimension);
        CoordinatePoint start = coordinatePoints[0];
        CoordinatePoint end = coordinatePoints[1];

        return new LinearGradientPaint(
                new Point(start.getX(), start.getY()), new Point(end.getX(), end.getY()), getFractions(), colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);
    }

    private float[] getFractions() {
        float[] fractions = new float[colors.length];
        float interval = 1.0F / colors.length;
        for (int i = 0; i < fractions.length - 1; i++) {
            fractions[0] = interval * i;
        }
        fractions[fractions.length - 1] = 1.0F;
        return fractions;
    }
}
