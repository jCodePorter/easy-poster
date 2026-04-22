package com.bytefuture.easy.poster.element.v2;

import com.bytefuture.easy.poster.element.AbstractRepeatableElement;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.model.TextLayoutMode;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.TextSpan;
import com.bytefuture.easy.poster.model.VerticalAlign;
import com.bytefuture.easy.poster.model.VerticalDirection;
import com.bytefuture.easy.poster.text.layout.TextLayoutResult;

import java.awt.*;
import java.util.List;

/**
 * Text element V2.
 *
 * @author biaoy
 * @since 2025/04/15
 */
public class TextElement extends AbstractRepeatableElement<TextElement> {

    /** Text config */
    private final TextElementConfig config;

    /** Layout engine */
    private final TextLayoutEngine layoutEngine;

    /** Renderer */
    private final TextRenderer renderer;

    /** Cached layout result */
    private transient TextLayoutResult lastLayout;

    public TextElement(TextElementConfig config) {
        this.config = config;
        this.layoutEngine = new TextLayoutEngine();
        this.renderer = new TextRenderer();
    }

    public static TextElement of(String text) {
        return new TextElement(TextElementConfig.builder(text).build());
    }

    public static TextElement rich(TextSpan... spans) {
        return new TextElement(TextElementConfig.builder(spans).build());
    }

    public static TextElement html(String html) {
        return new TextElement(TextElementConfig.builderHtml(html).build());
    }

    public static TextElement.Builder builder(String text) {
        return new TextElement.Builder(text);
    }

    public static TextElement.Builder builder(TextSpan... spans) {
        return new TextElement.Builder(spans);
    }

    public static TextElement.Builder builderHtml(String html) {
        return new TextElement.Builder(TextElementConfig.builderHtml(html));
    }

    @Override
    public Dimension calculateDimension(PosterContext context, int posterWidth, int posterHeight) {
        TextLayoutResult layout = layoutEngine.layout(config, position, rotate, context, posterWidth, posterHeight);
        this.lastLayout = layout;
        return layout.toDimension(rotate);
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        TextLayoutResult layout = lastLayout;
        if (layout == null) {
            layout = layoutEngine.layout(config, position, rotate, context, posterWidth, posterHeight);
            this.lastLayout = layout;
        }
        return renderer.render(config, rotate, context, dimension, layout);
    }

    @Override
    public void beforeRender(PosterContext context) {
        super.beforeRender(context);
        if (gradient != null && lastLayout != null) {
            context.getGraphics().setPaint(gradient.toGradient(lastLayout.toDimension(rotate)));
        } else {
            context.getGraphics().setPaint(color != null ? color : context.getConfig().getColor());
        }
    }

    public TextElementConfig getConfig() {
        return config;
    }

    public static final class Builder {
        private TextElementConfig.Builder configBuilder;
        private Position position;
        private float alpha = 1F;
        private int rotate = 0;
        private Color color = Color.BLACK;
        private com.bytefuture.easy.poster.model.Gradient gradient;

        private Builder(String text) {
            this.configBuilder = TextElementConfig.builder(text);
        }

        private Builder(TextSpan[] spans) {
            this.configBuilder = TextElementConfig.builder(spans);
        }

        private Builder(TextElementConfig.Builder configBuilder) {
            this.configBuilder = configBuilder;
        }

        public Builder fontName(String fontName) {
            configBuilder.fontName(fontName);
            return this;
        }

        public Builder fontStyle(int fontStyle) {
            configBuilder.fontStyle(fontStyle);
            return this;
        }

        public Builder fontSize(int fontSize) {
            configBuilder.fontSize(fontSize);
            return this;
        }

        public Builder font(String fontName, int fontStyle, int fontSize) {
            configBuilder.font(fontName, fontStyle, fontSize);
            return this;
        }

        public Builder font(Font font) {
            configBuilder.font(font);
            return this;
        }

        public Builder baseLine(com.bytefuture.easy.poster.model.BaseLine baseLine) {
            configBuilder.baseLine(baseLine);
            return this;
        }

        public Builder lineHeight(int lineHeight) {
            configBuilder.lineHeight(lineHeight);
            return this;
        }

        public Builder textAlign(com.bytefuture.easy.poster.model.TextAlign textAlign) {
            configBuilder.textAlign(textAlign);
            return this;
        }

        public Builder textLayoutMode(TextLayoutMode textLayoutMode) {
            configBuilder.textLayoutMode(textLayoutMode);
            return this;
        }

        public Builder vertical(String text) {
            configBuilder.vertical(text);
            return this;
        }

        public Builder vertical(List<String> columns) {
            configBuilder.vertical(columns);
            return this;
        }

        public Builder verticalDirection(VerticalDirection verticalDirection) {
            configBuilder.verticalDirection(verticalDirection);
            return this;
        }

        public Builder verticalAlign(VerticalAlign verticalAlign) {
            configBuilder.verticalAlign(verticalAlign);
            return this;
        }

        public Builder layoutHeight(int layoutHeight) {
            configBuilder.layoutHeight(layoutHeight);
            return this;
        }

        public Builder columnSpacing(int columnSpacing) {
            configBuilder.columnSpacing(columnSpacing);
            return this;
        }

        public Builder overflowStrategy(com.bytefuture.easy.poster.model.TextOverflowStrategy strategy) {
            configBuilder.overflowStrategy(strategy);
            return this;
        }

        public Builder maxLines(int maxLines) {
            configBuilder.maxLines(maxLines);
            return this;
        }

        public Builder ellipsis(String ellipsis) {
            configBuilder.ellipsis(ellipsis);
            return this;
        }

        public Builder autoWordWrap(int maxWidth) {
            configBuilder.autoWordWrap(maxWidth);
            return this;
        }

        public Builder layoutWidth(int layoutWidth) {
            configBuilder.layoutWidth(layoutWidth);
            return this;
        }

        public Builder autoFitText(int targetWidth, int minFontSize) {
            configBuilder.autoFitText(targetWidth, minFontSize);
            return this;
        }

        public Builder letterSpacing(int spacing) {
            configBuilder.letterSpacing(spacing);
            return this;
        }

        public Builder underline(boolean underline) {
            configBuilder.underline(underline);
            return this;
        }

        public Builder strikeThrough(boolean strike) {
            configBuilder.strikeThrough(strike);
            return this;
        }

        public Builder shadow(Color color, int offsetX, int offsetY) {
            configBuilder.shadow(color, offsetX, offsetY);
            return this;
        }

        public Builder shadow(com.bytefuture.easy.poster.model.TextShadow shadow) {
            configBuilder.shadow(shadow);
            return this;
        }

        public Builder stroke(Color color, float width) {
            configBuilder.stroke(color, width);
            return this;
        }

        public Builder stroke(com.bytefuture.easy.poster.model.TextStroke stroke) {
            configBuilder.stroke(stroke);
            return this;
        }

        public Builder textBackground(Color color) {
            configBuilder.textBackground(color);
            return this;
        }

        public Builder textBackground(Color color, int padding) {
            configBuilder.textBackground(color, padding);
            return this;
        }

        public Builder textBackground(Color color, com.bytefuture.easy.poster.geometry.Margin padding) {
            configBuilder.textBackground(color, padding);
            return this;
        }

        public Builder textPadding(int padding) {
            configBuilder.textPadding(padding);
            return this;
        }

        public Builder textPadding(int horizontal, int vertical) {
            configBuilder.textPadding(horizontal, vertical);
            return this;
        }

        public Builder textPadding(int left, int top, int right, int bottom) {
            configBuilder.textPadding(left, top, right, bottom);
            return this;
        }

        public Builder textBackgroundArc(int arc) {
            configBuilder.textBackgroundArc(arc);
            return this;
        }

        public Builder textBackgroundArc(int arcWidth, int arcHeight) {
            configBuilder.textBackgroundArc(arcWidth, arcHeight);
            return this;
        }

        public Builder textSplitter(com.bytefuture.easy.poster.text.split.ITextSplitter splitter) {
            configBuilder.textSplitter(splitter);
            return this;
        }

        public Builder textSpan(TextSpan span) {
            configBuilder.textSpan(span);
            return this;
        }

        public Builder textSpans(java.util.List<TextSpan> spans) {
            configBuilder.textSpans(spans);
            return this;
        }

        public Builder position(Position position) {
            this.position = position;
            return this;
        }

        public Builder alpha(float alpha) {
            if (alpha < 0 || alpha > 1) {
                throw new IllegalArgumentException("alpha must be between 0 and 1");
            }
            this.alpha = alpha;
            return this;
        }

        public Builder rotate(int degrees) {
            this.rotate = degrees;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder color(String hexColor) {
            this.color = cn.augrain.easy.tool.support.ColorUtils.hexToColor(hexColor);
            return this;
        }

        public Builder gradient(com.bytefuture.easy.poster.model.Gradient gradient) {
            this.gradient = gradient;
            return this;
        }

        public TextElement build() {
            TextElementConfig config = configBuilder.build();
            TextElement element = new TextElement(config);
            element.position = this.position;
            element.alpha = this.alpha;
            element.rotate = this.rotate;
            element.color = this.color;
            element.gradient = this.gradient;
            return element;
        }
    }
}
