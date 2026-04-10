package com.bytefuture.easy.poster.model;

import lombok.Getter;

import java.awt.*;

/**
 * Simple text shadow style.
 */
@Getter
public class TextShadow {

    private final Color color;

    private final int offsetX;

    private final int offsetY;

    private TextShadow(Color color, int offsetX, int offsetY) {
        this.color = color;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public static TextShadow of(Color color, int offsetX, int offsetY) {
        return new TextShadow(color, offsetX, offsetY);
    }
}
