package com.bytefuture.easy.poster.element.v2.text.resolve;

import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextStyle;
import lombok.Getter;

import java.util.Objects;

@Getter
public final class ResolvedTextRun {
    private final String text;
    private final ResolvedTextStyle style;

    public ResolvedTextRun(String text, ResolvedTextStyle style) {
        this.text = text == null ? "" : text;
        this.style = Objects.requireNonNull(style, "style");
    }
}
