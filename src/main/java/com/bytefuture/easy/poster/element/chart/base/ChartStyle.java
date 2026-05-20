package com.bytefuture.easy.poster.element.chart.base;

import lombok.Getter;
import lombok.Setter;

import java.awt.Color;
import java.awt.Insets;

/**
 * Shared visual style primitives for chart elements.
 */
@Getter
@Setter
public class ChartStyle {

    private Insets padding = new Insets(24, 24, 24, 24);

    private Color backgroundColor;

    private Color labelColor = new Color(71, 77, 92);
}
