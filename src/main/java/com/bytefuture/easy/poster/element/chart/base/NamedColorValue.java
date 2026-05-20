package com.bytefuture.easy.poster.element.chart.base;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * Shared legend item view model.
 */
@Getter
@Setter
public class NamedColorValue {

    private final String name;

    private final Color color;

    private final String displayText;

    public NamedColorValue(String name, Color color, String displayText) {
        this.name = name;
        this.color = color;
        this.displayText = displayText;
    }
}
