package com.bytefuture.easy.poster.element.chart.base;

import java.awt.Color;

/**
 * Shared legend item view model.
 */
public class NamedColorValue {

    private final String name;

    private final Color color;

    private final String displayText;

    public NamedColorValue(String name, Color color, String displayText) {
        this.name = name;
        this.color = color;
        this.displayText = displayText;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public String getDisplayText() {
        return displayText;
    }
}
