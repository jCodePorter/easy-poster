package com.bytefuture.easy.poster.element.v2.text.resolve;

import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextStyle;
import com.bytefuture.easy.poster.element.v2.text.style.TextBlockStyle;
import com.bytefuture.easy.poster.element.v2.text.style.TextSpanStyle;
import com.bytefuture.easy.poster.model.TextSpan;

import java.awt.Color;
import java.awt.Font;

public final class TextStyleResolver {

    public ResolvedTextRun resolve(TextSpan span, TextBlockStyle blockStyle, Font baseFont, Color defaultColor) {
        TextSpanStyle spanStyle = span.getStyle();
        String fontName = firstNonNull(spanStyle.getFontName(), blockStyle.getFontName(), baseFont.getFamily());
        int fontStyle = firstNonNull(spanStyle.getFontStyle(), blockStyle.getFontStyle(), Integer.valueOf(baseFont.getStyle())).intValue();
        int fontSize = firstNonNull(spanStyle.getFontSize(), blockStyle.getFontSize(), Integer.valueOf(baseFont.getSize())).intValue();
        Color color = firstNonNull(spanStyle.getColor(), blockStyle.getColor(), defaultColor);
        return new ResolvedTextRun(span.getText(), new ResolvedTextStyle(new Font(fontName, fontStyle, fontSize), color));
    }

    private <T> T firstNonNull(T first, T second, T third) {
        if (first != null) {
            return first;
        }
        if (second != null) {
            return second;
        }
        return third;
    }
}
