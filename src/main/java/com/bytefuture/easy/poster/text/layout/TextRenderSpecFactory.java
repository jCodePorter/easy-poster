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
        // 这里负责把“元素局部配置 + 全局默认配置”折叠成一份不可变渲染规格。
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
                // 显式指定了 fontName 时，直接构建新字体，避免继承旧字体名称。
                return new Font(
                        element.getFontName(),
                        Optional.ofNullable(element.getFontStyle()).orElse(baseFont.getStyle()),
                        Optional.ofNullable(element.getFontSize()).orElse(baseFont.getSize())
                );
            }

            int resolvedStyle = Optional.ofNullable(element.getFontStyle()).orElse(baseFont.getStyle());
            int resolvedSize = Optional.ofNullable(element.getFontSize()).orElse(baseFont.getSize());
            if (resolvedStyle == baseFont.getStyle() && resolvedSize == baseFont.getSize()) {
                // 样式和字号都未变化时直接复用，减少对象创建。
                return baseFont;
            }
            return baseFont.deriveFont(resolvedStyle, (float) resolvedSize);
        }

        // 连基础字体对象都没有时，退化为按配置项直接 new Font。
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
        // 开启自动换行但未显式指定溢出策略时，默认使用 WRAP。
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
            // 相对定位在未显式设置对齐方式时，会推导一个更符合视觉预期的默认对齐。
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
