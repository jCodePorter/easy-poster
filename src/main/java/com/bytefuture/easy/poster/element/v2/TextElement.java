package com.bytefuture.easy.poster.element.v2;

import com.bytefuture.easy.poster.element.AbstractRepeatableElement;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextSpan;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

public class TextElement extends AbstractRepeatableElement<TextElement> {

    private final TextElementConfig config;
    private final TextLayoutEngine layoutEngine;
    private final TextRenderer renderer;
    private transient TextLayoutResult lastLayout;

    public TextElement(String text) {
        this(TextElementConfig.builder(text).build());
    }

    public TextElement(TextElementConfig config) {
        this.config = config;
        this.layoutEngine = new TextLayoutEngine();
        this.renderer = new TextRenderer();
        this.color = null;
    }

    public static TextElement of(String text) {
        return new TextElement(text);
    }

    public static TextElement rich(TextSpan... spans) {
        return new TextElement(TextElementConfig.builder(spans).build());
    }

    public static Builder builder(String text) {
        return new Builder(text);
    }

    public static Builder builder(TextSpan... spans) {
        return new Builder(spans);
    }

    @Override
    public Dimension calculateDimension(PosterContext context, int posterWidth, int posterHeight) {
        TextLayoutResult layout = layoutEngine.layout(config, position, rotate, color, context, posterWidth, posterHeight);
        this.lastLayout = layout;
        return layout.toDimension(rotate);
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        TextLayoutResult layout = lastLayout;
        if (layout == null) {
            layout = layoutEngine.layout(config, position, rotate, color, context, posterWidth, posterHeight);
            this.lastLayout = layout;
        }
        return renderer.render(context, dimension, layout, rotate);
    }

    public TextElementConfig getConfig() {
        return config;
    }

    public TextLayoutResult getLastLayout() {
        return lastLayout;
    }

    public static final class Builder {
        private final TextElementConfig.Builder configBuilder;
        private Position position;
        private float alpha = 1F;
        private int rotate = 0;

        private Builder(String text) {
            this.configBuilder = TextElementConfig.builder(text);
        }

        private Builder(TextSpan[] spans) {
            this.configBuilder = TextElementConfig.builder(spans);
        }

        public Builder color(Color color) {
            this.configBuilder.color(color);
            return this;
        }

        public Builder color(String hexColor) {
            this.configBuilder.color(hexColor);
            return this;
        }

        public Builder fontName(String fontName) {
            this.configBuilder.fontName(fontName);
            return this;
        }

        public Builder fontStyle(int fontStyle) {
            this.configBuilder.fontStyle(fontStyle);
            return this;
        }

        public Builder fontSize(int fontSize) {
            this.configBuilder.fontSize(fontSize);
            return this;
        }

        public Builder font(String fontName, int fontStyle, int fontSize) {
            this.configBuilder.font(fontName, fontStyle, fontSize);
            return this;
        }

        public Builder font(Font font) {
            this.configBuilder.font(font);
            return this;
        }

        public Builder baseLine(BaseLine baseLine) {
            this.configBuilder.baseLine(baseLine);
            return this;
        }

        public Builder textAlign(TextAlign textAlign) {
            this.configBuilder.textAlign(textAlign);
            return this;
        }

        public Builder autoWordWrap(int maxWidth) {
            this.configBuilder.autoWordWrap(maxWidth);
            return this;
        }

        public Builder layoutWidth(int layoutWidth) {
            this.configBuilder.layoutWidth(layoutWidth);
            return this;
        }

        public Builder textSpan(TextSpan span) {
            this.configBuilder.textSpan(span);
            return this;
        }

        public Builder textSpans(List<TextSpan> spans) {
            this.configBuilder.textSpans(spans);
            return this;
        }

        public Builder position(Position position) {
            this.position = position;
            return this;
        }

        public Builder alpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        public Builder rotate(int rotate) {
            this.rotate = rotate;
            return this;
        }

        public TextElement build() {
            TextElement element = new TextElement(configBuilder.build());
            element.position = this.position;
            element.alpha = this.alpha;
            element.rotate = this.rotate;
            return element;
        }
    }
}
