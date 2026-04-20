package com.bytefuture.easy.poster.element.chart.base;

import java.awt.Color;
import java.awt.Insets;

/**
 * Shared visual style primitives for chart elements.
 */
public class ChartStyle {

    private Insets padding = new Insets(24, 24, 24, 24);

    private Color backgroundColor;

    private Color labelColor = new Color(71, 77, 92);

    public Insets getPadding() {
        return padding;
    }

    public void setPadding(Insets padding) {
        this.padding = padding;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
    }
}
