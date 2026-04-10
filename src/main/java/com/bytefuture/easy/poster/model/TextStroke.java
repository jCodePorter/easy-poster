package com.bytefuture.easy.poster.model;

import lombok.Getter;

import java.awt.*;

/**
 * Simple text stroke style.
 */
@Getter
public class TextStroke {

    private final Color color;

    private final float width;

    private TextStroke(Color color, float width) {
        this.color = color;
        this.width = width;
    }

    public static TextStroke of(Color color, float width) {
        return new TextStroke(color, width);
    }
}
