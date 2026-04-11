package com.bytefuture.easy.poster.text.layout;

import com.bytefuture.easy.poster.element.basic.EnhanceTextElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.Config;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextOverflowStrategy;
import com.bytefuture.easy.poster.model.TextSpan;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Optional;

public final class TextRenderSpecFactory {

    private TextRenderSpecFactory() {
    }

    public static TextRenderSpec from(EnhanceTextElement element, Config config) {
        return new TextRenderSpec(
                element.getText(),
                new ArrayList<>(element.getTextSpans()),
                element.getPosition(),
                Optional.ofNullable(element.getColor()).orElse(config.getColor()),
                resolveBaseFont(element, config),
                Optional.ofNullable(element.getBaseLine()).orElse(config.getBaseLine()),
                element.getLineHeight() != null ? element.getLineHeight() : config.getLineHeight(),
                resolveTextAlign(element),
                resolveOverflowStrategy(element),
                element.getMaxLines(),
                element.getEllipsis(),
                element.getShadow(),
                element.getStroke(),
                element.getLetterSpacing(),
                element.getTextBackgroundColor(),
                copyMargin(element.getTextPadding()),
                element.getTextBackgroundArcWidth(),
                element.getTextBackgroundArcHeight(),
                element.getRotate(),
                element.isAutoWordWrap(),
                element.getMaxTextWidth(),
                element.isAutoFitText(),
                element.getAutoFitTargetWidth(),
                element.getAutoFitMinFontSize(),
                element.isUnderline(),
                element.isStrikeThrough()
        );
    }

    private static Font resolveBaseFont(EnhanceTextElement element, Config config) {
        Font baseFont = Optional.ofNullable(element.getFont()).orElse(config.getFont());
        if (baseFont != null) {
            if (element.getFontName() != null) {
                return new Font(
                        element.getFontName(),
                        Optional.ofNullable(element.getFontStyle()).orElse(baseFont.getStyle()),
                        Optional.ofNullable(element.getFontSize()).orElse(baseFont.getSize())
                );
            }

            int resolvedStyle = Optional.ofNullable(element.getFontStyle()).orElse(baseFont.getStyle());
            int resolvedSize = Optional.ofNullable(element.getFontSize()).orElse(baseFont.getSize());
            if (resolvedStyle == baseFont.getStyle() && resolvedSize == baseFont.getSize()) {
                return baseFont;
            }
            return baseFont.deriveFont(resolvedStyle, (float) resolvedSize);
        }

        return new Font(
                Optional.ofNullable(element.getFontName()).orElse(config.getFontName()),
                Optional.ofNullable(element.getFontStyle()).orElse(config.getFontStyle()),
                Optional.ofNullable(element.getFontSize()).orElse(config.getFontSize())
        );
    }

    private static TextOverflowStrategy resolveOverflowStrategy(EnhanceTextElement element) {
        if (element.getOverflowStrategy() != null) {
            return element.getOverflowStrategy();
        }
        if (element.isAutoWordWrap()) {
            return TextOverflowStrategy.WRAP;
        }
        return TextOverflowStrategy.VISIBLE;
    }

    private static TextAlign resolveTextAlign(EnhanceTextElement element) {
        if (element.getTextAlign() != null) {
            return element.getTextAlign();
        }
        Position position = element.getPosition();
        if (position instanceof RelativePosition) {
            Direction direction = ((RelativePosition) position).getDirection();
            if (direction == Direction.CENTER || direction == Direction.TOP_CENTER || direction == Direction.BOTTOM_CENTER) {
                return TextAlign.CENTER;
            }
            if (direction == Direction.TOP_RIGHT || direction == Direction.RIGHT_CENTER || direction == Direction.RIGHT_BOTTOM) {
                return TextAlign.RIGHT;
            }
        }
        return TextAlign.LEFT;
    }

    private static Margin copyMargin(Margin margin) {
        return Margin.of(margin.getMarginLeft(), margin.getMarginTop(),
                margin.getMarginRight(), margin.getMarginBottom());
    }
}
