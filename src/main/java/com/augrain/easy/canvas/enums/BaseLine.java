package com.augrain.easy.canvas.enums;

import java.awt.*;
import java.util.function.Function;

/**
 * 文本对齐方式
 *
 * @author biaoy
 * @since 2025/03/13
 */
public enum BaseLine {
    BASE_LINE("基线", fm -> fm.getHeight() / 2),

    TOP("顶部（即ascent线的位置）", fm -> fm.getAscent() + fm.getHeight() / 2),

    BOTTOM("底部（即descent线的位置）", fm -> -fm.getDescent() + fm.getHeight() / 2),

    CENTER("中心（即centerY线的位置）", fm -> fm.getDescent() + fm.getHeight() / 2);

    private final String text;

    private final Function<FontMetrics, Integer> textOffset;

    BaseLine(String text, Function<FontMetrics, Integer> textOffset) {
        this.text = text;
        this.textOffset = textOffset;
    }

    public int getOffset(FontMetrics fm) {
        return textOffset.apply(fm);
    }
}
