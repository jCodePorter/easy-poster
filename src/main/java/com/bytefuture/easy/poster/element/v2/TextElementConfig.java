package com.bytefuture.easy.poster.element.v2;

import cn.augrain.easy.tool.support.ColorUtils;
import com.bytefuture.easy.poster.element.v2.text.style.TextBlockStyle;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextSpan;
import lombok.Getter;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public final class TextElementConfig {
    private final String text;
    private final List<TextSpan> textSpans;
    private final Color color;
    private final String fontName;
    private final int fontStyle;
    private final int fontSize;
    private final Font font;
    private final BaseLine baseLine;
    private final TextAlign textAlign;
    private final boolean autoWordWrap;
    private final int maxTextWidth;

    private TextElementConfig(Builder builder) {
        this.text = builder.text;
        this.textSpans = Collections.unmodifiableList(new ArrayList<TextSpan>(builder.textSpans));
        this.color = builder.color;
        this.fontName = builder.fontName;
        this.fontStyle = builder.fontStyle;
        this.fontSize = builder.fontSize;
        this.font = builder.font;
        this.baseLine = builder.baseLine;
        this.textAlign = builder.textAlign;
        this.autoWordWrap = builder.autoWordWrap;
        this.maxTextWidth = builder.maxTextWidth;
    }

    public boolean isEmpty() {
        return (text == null || text.isEmpty()) && textSpans.isEmpty();
    }

    public List<TextSpan> toRichSpans() {
        if (!textSpans.isEmpty()) {
            return textSpans;
        }
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(TextSpan.of(text));
    }

    public TextBlockStyle toBlockStyle() {
        TextBlockStyle style = new TextBlockStyle();
        style.setColor(this.color);
        style.setFontName(this.fontName);
        style.setFontStyle(Integer.valueOf(this.fontStyle));
        style.setFontSize(Integer.valueOf(this.fontSize));
        return style;
    }

    public static Builder builder(String text) {
        return new Builder(text);
    }

    public static Builder builder(TextSpan... spans) {
        return new Builder(spans);
    }

    public static final class Builder {
        private String text;
        private final List<TextSpan> textSpans = new ArrayList<TextSpan>();
        private Color color;
        private String fontName;
        private int fontStyle = Font.PLAIN;
        private int fontSize = 16;
        private Font font;
        private BaseLine baseLine = BaseLine.BASE_LINE;
        private TextAlign textAlign = TextAlign.LEFT;
        private boolean autoWordWrap = false;
        private int maxTextWidth = 0;

        private Builder(String text) {
            this.text = text;
        }

        private Builder(TextSpan[] spans) {
            this.text = null;
            if (spans != null) {
                for (TextSpan span : spans) {
                    if (span != null) {
                        this.textSpans.add(span);
                    }
                }
            }
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder color(String hexColor) {
            this.color = ColorUtils.hexToColor(hexColor);
            return this;
        }

        public Builder fontName(String fontName) {
            this.fontName = fontName;
            return this;
        }

        public Builder fontStyle(int fontStyle) {
            this.fontStyle = fontStyle;
            return this;
        }

        public Builder fontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public Builder font(String fontName, int fontStyle, int fontSize) {
            this.fontName = fontName;
            this.fontStyle = fontStyle;
            this.fontSize = fontSize;
            return this;
        }

        public Builder font(Font font) {
            this.font = font;
            return this;
        }

        public Builder baseLine(BaseLine baseLine) {
            this.baseLine = baseLine;
            return this;
        }

        public Builder textAlign(TextAlign textAlign) {
            this.textAlign = textAlign;
            return this;
        }

        public Builder autoWordWrap(int maxWidth) {
            this.autoWordWrap = true;
            this.maxTextWidth = maxWidth;
            return this;
        }

        public Builder layoutWidth(int layoutWidth) {
            this.autoWordWrap = layoutWidth > 0;
            this.maxTextWidth = layoutWidth;
            return this;
        }

        public Builder textSpan(TextSpan span) {
            if (span != null) {
                this.textSpans.add(span);
            }
            return this;
        }

        public Builder textSpans(List<TextSpan> spans) {
            if (spans != null) {
                for (TextSpan span : spans) {
                    if (span != null) {
                        this.textSpans.add(span);
                    }
                }
            }
            return this;
        }

        public TextElementConfig build() {
            return new TextElementConfig(this);
        }
    }
}
