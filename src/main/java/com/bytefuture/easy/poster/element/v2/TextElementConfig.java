package com.bytefuture.easy.poster.element.v2;

import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.model.*;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文本元素配置 - 不可变的数据载体。
 * <p>
 * 设计原则：
 * <ul>
 *   <li>完全不可变（Immutable），线程安全</li>
 *   <li>只存储配置，不包含任何业务逻辑</li>
 *   <li>通过 Builder 模式构建</li>
 *   <li>与 V1 完全解耦，不继承任何类</li>
 * </ul>
 *
 * @author biaoy
 * @since 2025/04/15
 */
@Getter
public final class TextElementConfig {

    // ========== 文本内容 ==========
    /** 主文本内容 */
    private final String text;
    /** 富文本片段 */
    private final List<TextSpan> textSpans;

    // ========== 字体配置 ==========
    /** 字体名称 */
    private final String fontName;
    /** 字体样式 */
    private final int fontStyle;
    /** 字体大小 */
    private final int fontSize;
    private final Font font;

    // ========== 布局配置 ==========
    /** 基线类型 */
    private final BaseLine baseLine;
    /** 行高（null表示使用字体默认） */
    private final Integer lineHeight;
    /** 对齐方式 */
    private final TextAlign textAlign;
    /** 溢出策略 */
    private final TextOverflowStrategy overflowStrategy;
    /** 最大行数 */
    private final Integer maxLines;
    /** 省略符 */
    private final String ellipsis;

    // ========== 宽度控制 ==========
    /** 是否自动换行 */
    private final boolean autoWordWrap;
    /** 最大文本宽度 */
    private final int maxTextWidth;
    /** 是否自动适配字体 */
    private final boolean autoFitText;
    /** 自动适配目标宽度 */
    private final int autoFitTargetWidth;
    /** 自动适配最小字号 */
    private final int autoFitMinFontSize;

    // ========== 装饰效果 ==========
    /** 字间距 */
    private final int letterSpacing;
    /** 下划线 */
    private final boolean underline;
    /** 删除线 */
    private final boolean strikeThrough;
    /** 阴影 */
    private final TextShadow shadow;
    /** 描边 */
    private final TextStroke stroke;

    // ========== 背景配置 ==========
    /** 背景色 */
    private final Color textBackgroundColor;
    /** 背景内边距 */
    private final Margin textPadding;
    /** 背景圆角宽度 */
    private final int textBackgroundArcWidth;
    /** 背景圆角高度 */
    private final int textBackgroundArcHeight;

    // ========== 拆分器 ==========
    /** 文本拆分器（用于自动换行） */
    private final com.bytefuture.easy.poster.text.split.ITextSplitter textSplitter;

    /**
     * 私有构造函数，强制使用 Builder。
     */
    private TextElementConfig(Builder builder) {
        this.text = builder.text;
        this.textSpans = Collections.unmodifiableList(new ArrayList<>(builder.textSpans));
        this.fontName = builder.fontName;
        this.fontStyle = builder.fontStyle;
        this.fontSize = builder.fontSize;
        this.font = builder.font;
        this.baseLine = builder.baseLine;
        this.lineHeight = builder.lineHeight;
        this.textAlign = builder.textAlign;
        this.overflowStrategy = builder.overflowStrategy;
        this.maxLines = builder.maxLines;
        this.ellipsis = builder.ellipsis;
        this.autoWordWrap = builder.autoWordWrap;
        this.maxTextWidth = builder.maxTextWidth;
        this.autoFitText = builder.autoFitText;
        this.autoFitTargetWidth = builder.autoFitTargetWidth;
        this.autoFitMinFontSize = builder.autoFitMinFontSize;
        this.letterSpacing = builder.letterSpacing;
        this.underline = builder.underline;
        this.strikeThrough = builder.strikeThrough;
        this.shadow = builder.shadow;
        this.stroke = builder.stroke;
        this.textBackgroundColor = builder.textBackgroundColor;
        this.textPadding = builder.textPadding;
        this.textBackgroundArcWidth = builder.textBackgroundArcWidth;
        this.textBackgroundArcHeight = builder.textBackgroundArcHeight;
        this.textSplitter = builder.textSplitter;
    }

    /**
     * 判断是否为富文本。
     */
    public boolean isRichText() {
        return !textSpans.isEmpty();
    }

    /**
     * 判断是否为空文本。
     */
    public boolean isEmpty() {
        return (text == null || text.isEmpty()) && textSpans.isEmpty();
    }

    /**
     * 获取有效的宽度限制。
     */
    public int getEffectiveWidthLimit() {
        if (maxTextWidth > 0) return maxTextWidth;
        if (autoFitText) return autoFitTargetWidth;
        return 0;
    }

    /**
     * Builder 模式构造器。
     */
    public static Builder builder(String text) {
        return new Builder(text);
    }

    public static Builder builder(TextSpan... spans) {
        return new Builder(spans);
    }

    public static final class Builder {
        // 文本内容
        private String text;
        private final List<TextSpan> textSpans = new ArrayList<>();

        // 字体配置
        private String fontName;
        private int fontStyle = Font.PLAIN;
        private int fontSize = 16;
        private Font font;

        // 布局配置
        private BaseLine baseLine = BaseLine.BASE_LINE;
        private Integer lineHeight;
        private TextAlign textAlign;
        private TextOverflowStrategy overflowStrategy;
        private Integer maxLines;
        private String ellipsis = "...";

        // 宽度控制
        private boolean autoWordWrap = false;
        private int maxTextWidth = 0;
        private boolean autoFitText = false;
        private int autoFitTargetWidth = 0;
        private int autoFitMinFontSize = 8;

        // 装饰效果
        private int letterSpacing = 0;
        private boolean underline = false;
        private boolean strikeThrough = false;
        private TextShadow shadow;
        private TextStroke stroke;

        // 背景配置
        private Color textBackgroundColor;
        private Margin textPadding = Margin.of(0);
        private int textBackgroundArcWidth = 0;
        private int textBackgroundArcHeight = 0;

        // 拆分器
        private com.bytefuture.easy.poster.text.split.ITextSplitter textSplitter;

        private Builder(String text) {
            this.text = text;
        }

        private Builder(TextSpan[] spans) {
            this.text = "";
            if (spans != null) {
                for (TextSpan span : spans) {
                    if (span != null) {
                        this.textSpans.add(span);
                    }
                }
            }
        }

        // ========== 字体配置 ==========
        public Builder fontName(String fontName) {
            this.fontName = fontName;
            return this;
        }

        public Builder fontStyle(int fontStyle) {
            this.fontStyle = fontStyle;
            return this;
        }

        public Builder fontSize(int fontSize) {
            if (fontSize <= 0) throw new IllegalArgumentException("fontSize must be positive");
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
            if (font == null) throw new IllegalArgumentException("font cannot be null");
            this.font = font;
            return this;
        }

        // ========== 布局配置 ==========
        public Builder baseLine(BaseLine baseLine) {
            this.baseLine = baseLine;
            return this;
        }

        public Builder lineHeight(int lineHeight) {
            if (lineHeight <= 0) throw new IllegalArgumentException("lineHeight must be positive");
            this.lineHeight = lineHeight;
            return this;
        }

        public Builder textAlign(TextAlign textAlign) {
            if (textAlign == null) throw new IllegalArgumentException("textAlign cannot be null");
            this.textAlign = textAlign;
            return this;
        }

        public Builder overflowStrategy(TextOverflowStrategy overflowStrategy) {
            this.overflowStrategy = overflowStrategy;
            return this;
        }

        public Builder maxLines(int maxLines) {
            if (maxLines <= 0) throw new IllegalArgumentException("maxLines must be positive");
            this.maxLines = maxLines;
            return this;
        }

        public Builder ellipsis(String ellipsis) {
            if (ellipsis == null) throw new IllegalArgumentException("ellipsis cannot be null");
            this.ellipsis = ellipsis;
            return this;
        }

        // ========== 宽度控制 ==========
        public Builder autoWordWrap(int maxWidth) {
            if (maxWidth <= 0) throw new IllegalArgumentException("maxWidth must be positive");
            this.autoWordWrap = true;
            this.maxTextWidth = maxWidth;
            return this;
        }

        public Builder layoutWidth(int layoutWidth) {
            if (layoutWidth <= 0) throw new IllegalArgumentException("layoutWidth must be positive");
            this.maxTextWidth = layoutWidth;
            return this;
        }

        public Builder autoFitText(int targetWidth, int minFontSize) {
            if (targetWidth <= 0) throw new IllegalArgumentException("targetWidth must be positive");
            if (minFontSize <= 0) throw new IllegalArgumentException("minFontSize must be positive");
            this.autoFitText = true;
            this.autoFitTargetWidth = targetWidth;
            this.autoFitMinFontSize = minFontSize;
            return this;
        }

        // ========== 装饰效果 ==========
        public Builder letterSpacing(int letterSpacing) {
            this.letterSpacing = letterSpacing;
            return this;
        }

        public Builder underline(boolean underline) {
            this.underline = underline;
            return this;
        }

        public Builder strikeThrough(boolean strikeThrough) {
            this.strikeThrough = strikeThrough;
            return this;
        }

        public Builder shadow(Color color, int offsetX, int offsetY) {
            if (color == null) throw new IllegalArgumentException("shadow color cannot be null");
            this.shadow = TextShadow.of(color, offsetX, offsetY);
            return this;
        }

        public Builder shadow(TextShadow shadow) {
            if (shadow == null) throw new IllegalArgumentException("shadow cannot be null");
            this.shadow = shadow;
            return this;
        }

        public Builder stroke(Color color, float width) {
            if (color == null) throw new IllegalArgumentException("stroke color cannot be null");
            if (width <= 0) throw new IllegalArgumentException("stroke width must be positive");
            this.stroke = TextStroke.of(color, width);
            return this;
        }

        public Builder stroke(TextStroke stroke) {
            if (stroke == null) throw new IllegalArgumentException("stroke cannot be null");
            this.stroke = stroke;
            return this;
        }

        // ========== 背景配置 ==========
        public Builder textBackground(Color color) {
            if (color == null) throw new IllegalArgumentException("textBackgroundColor cannot be null");
            this.textBackgroundColor = color;
            return this;
        }

        public Builder textBackground(Color color, int padding) {
            if (color == null) throw new IllegalArgumentException("textBackgroundColor cannot be null");
            this.textBackgroundColor = color;
            this.textPadding = Margin.of(padding);
            return this;
        }

        public Builder textBackground(Color color, Margin padding) {
            if (color == null) throw new IllegalArgumentException("textBackgroundColor cannot be null");
            if (padding == null) throw new IllegalArgumentException("textPadding cannot be null");
            this.textBackgroundColor = color;
            this.textPadding = padding;
            return this;
        }

        public Builder textPadding(int padding) {
            if (padding < 0) throw new IllegalArgumentException("padding cannot be negative");
            this.textPadding = Margin.of(padding);
            return this;
        }

        public Builder textPadding(int horizontal, int vertical) {
            if (horizontal < 0 || vertical < 0) throw new IllegalArgumentException("padding cannot be negative");
            this.textPadding = Margin.of(horizontal, vertical);
            return this;
        }

        public Builder textPadding(int left, int top, int right, int bottom) {
            if (left < 0 || top < 0 || right < 0 || bottom < 0) {
                throw new IllegalArgumentException("padding cannot be negative");
            }
            this.textPadding = Margin.of(left, top, right, bottom);
            return this;
        }

        public Builder textBackgroundArc(int arc) {
            if (arc < 0) throw new IllegalArgumentException("arc cannot be negative");
            this.textBackgroundArcWidth = arc;
            this.textBackgroundArcHeight = arc;
            return this;
        }

        public Builder textBackgroundArc(int arcWidth, int arcHeight) {
            if (arcWidth < 0 || arcHeight < 0) throw new IllegalArgumentException("arc cannot be negative");
            this.textBackgroundArcWidth = arcWidth;
            this.textBackgroundArcHeight = arcHeight;
            return this;
        }

        // ========== 拆分器 ==========
        public Builder textSplitter(com.bytefuture.easy.poster.text.split.ITextSplitter splitter) {
            if (splitter == null) throw new IllegalArgumentException("textSplitter cannot be null");
            this.textSplitter = splitter;
            return this;
        }

        public Builder textSpan(TextSpan span) {
            if (span == null) throw new IllegalArgumentException("textSpan cannot be null");
            this.textSpans.add(span);
            return this;
        }

        public Builder textSpans(List<TextSpan> spans) {
            if (spans != null) {
                this.textSpans.addAll(spans);
            }
            return this;
        }

        public TextElementConfig build() {
            return new TextElementConfig(this);
        }
    }
}
