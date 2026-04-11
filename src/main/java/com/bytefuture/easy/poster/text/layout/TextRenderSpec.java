package com.bytefuture.easy.poster.text.layout;

import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextOverflowStrategy;
import com.bytefuture.easy.poster.model.TextShadow;
import com.bytefuture.easy.poster.model.TextSpan;
import com.bytefuture.easy.poster.model.TextStroke;
import lombok.Getter;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public final class TextRenderSpec {

    private final String text;
    private final List<TextSpan> textSpans;
    private final Position position;
    private final Color color;
    private final Font baseFont;
    private final BaseLine baseLine;
    private final Integer lineHeight;
    private final TextAlign textAlign;
    private final TextOverflowStrategy overflowStrategy;
    private final Integer maxLines;
    private final String ellipsis;
    private final TextShadow shadow;
    private final TextStroke stroke;
    private final int letterSpacing;
    private final Color textBackgroundColor;
    private final Margin textPadding;
    private final int textBackgroundArcWidth;
    private final int textBackgroundArcHeight;
    private final int rotate;
    private final boolean autoWordWrap;
    private final int maxTextWidth;
    private final boolean autoFitText;
    private final int autoFitTargetWidth;
    private final int autoFitMinFontSize;
    private final boolean underline;
    private final boolean strikeThrough;

    public TextRenderSpec(String text, List<TextSpan> textSpans, Position position, Color color, Font baseFont,
                          BaseLine baseLine, Integer lineHeight, TextAlign textAlign, TextOverflowStrategy overflowStrategy,
                          Integer maxLines, String ellipsis, TextShadow shadow, TextStroke stroke, int letterSpacing,
                          Color textBackgroundColor, Margin textPadding, int textBackgroundArcWidth, int textBackgroundArcHeight,
                          int rotate, boolean autoWordWrap, int maxTextWidth, boolean autoFitText, int autoFitTargetWidth,
                          int autoFitMinFontSize, boolean underline, boolean strikeThrough) {
        this.text = text;
        this.textSpans = Collections.unmodifiableList(new ArrayList<TextSpan>(textSpans));
        this.position = position;
        this.color = color;
        this.baseFont = baseFont;
        this.baseLine = baseLine;
        this.lineHeight = lineHeight;
        this.textAlign = textAlign;
        this.overflowStrategy = overflowStrategy;
        this.maxLines = maxLines;
        this.ellipsis = ellipsis;
        this.shadow = shadow;
        this.stroke = stroke;
        this.letterSpacing = letterSpacing;
        this.textBackgroundColor = textBackgroundColor;
        this.textPadding = textPadding;
        this.textBackgroundArcWidth = textBackgroundArcWidth;
        this.textBackgroundArcHeight = textBackgroundArcHeight;
        this.rotate = rotate;
        this.autoWordWrap = autoWordWrap;
        this.maxTextWidth = maxTextWidth;
        this.autoFitText = autoFitText;
        this.autoFitTargetWidth = autoFitTargetWidth;
        this.autoFitMinFontSize = autoFitMinFontSize;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
    }

    public boolean hasRichTextSpans() {
        return !this.textSpans.isEmpty();
    }

    public int resolveWidthLimit() {
        if (this.maxTextWidth > 0) {
            return this.maxTextWidth;
        }
        if (this.autoFitText) {
            return this.autoFitTargetWidth;
        }
        return 0;
    }

    public String normalizedText() {
        return this.text == null ? "" : this.text.replace("\r\n", "\n").replace('\r', '\n');
    }

    public String cacheKey() {
        StringBuilder builder = new StringBuilder();
        builder.append(Objects.toString(this.text, "")).append('|')
                .append(this.textSpans.size()).append('|')
                .append(Objects.toString(this.position, "")).append('|')
                .append(Objects.toString(this.color, "")).append('|')
                .append(Objects.toString(this.baseFont, "")).append('|')
                .append(Objects.toString(this.baseLine, "")).append('|')
                .append(Objects.toString(this.lineHeight, "")).append('|')
                .append(Objects.toString(this.textAlign, "")).append('|')
                .append(Objects.toString(this.overflowStrategy, "")).append('|')
                .append(Objects.toString(this.maxLines, "")).append('|')
                .append(this.ellipsis).append('|')
                .append(Objects.toString(this.shadow, "")).append('|')
                .append(Objects.toString(this.stroke, "")).append('|')
                .append(this.letterSpacing).append('|')
                .append(Objects.toString(this.textBackgroundColor, "")).append('|')
                .append(this.textPadding.getMarginLeft()).append(',')
                .append(this.textPadding.getMarginTop()).append(',')
                .append(this.textPadding.getMarginRight()).append(',')
                .append(this.textPadding.getMarginBottom()).append('|')
                .append(this.textBackgroundArcWidth).append('|')
                .append(this.textBackgroundArcHeight).append('|')
                .append(this.rotate).append('|')
                .append(this.autoWordWrap).append('|')
                .append(this.maxTextWidth).append('|')
                .append(this.autoFitText).append('|')
                .append(this.autoFitTargetWidth).append('|')
                .append(this.autoFitMinFontSize).append('|')
                .append(this.underline).append('|')
                .append(this.strikeThrough);
        for (TextSpan span : this.textSpans) {
            builder.append('|')
                    .append(Objects.toString(span.getText(), ""))
                    .append(':').append(Objects.toString(span.getColor(), ""))
                    .append(':').append(Objects.toString(span.getFontStyle(), ""))
                    .append(':').append(Objects.toString(span.getUnderline(), ""))
                    .append(':').append(Objects.toString(span.getStrikeThrough(), ""));
        }
        return builder.toString();
    }
}
