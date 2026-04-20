package com.bytefuture.easy.poster.element.chart.base;

import com.bytefuture.easy.poster.model.PosterContext;

import java.awt.Color;
import java.awt.Font;
import java.util.Optional;

/**
 * Shared chart text and color helpers.
 */
public final class ChartTextSupport {

    private ChartTextSupport() {
    }

    public static Font resolveBaseFont(PosterContext context) {
        return Optional.ofNullable(context.getConfig().getFont()).orElse(
                new Font(context.getConfig().getFontName(), context.getConfig().getFontStyle(), context.getConfig().getFontSize())
        );
    }

    public static Color chooseReadableTextColor(Color background) {
        if (background == null) {
            return Color.WHITE;
        }
        int brightness = (background.getRed() * 299 + background.getGreen() * 587 + background.getBlue() * 114) / 1000;
        return brightness < 150 ? Color.WHITE : new Color(33, 37, 41);
    }
}
