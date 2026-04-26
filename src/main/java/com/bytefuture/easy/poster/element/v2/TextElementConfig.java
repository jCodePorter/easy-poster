package com.bytefuture.easy.poster.element.v2;

import cn.augrain.easy.tool.support.ColorUtils;
import com.bytefuture.easy.poster.element.v2.text.style.TextBlockStyle;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextSpan;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文本元素静态配置。
 * 封装纯文本、富文本、字体、颜色和换行策略等布局输入参数。
 *
 * <h3>样式优先级（从高到低）：</h3>
 * <ol>
 *   <li>{@link TextSpan} 片段级样式 - 每个文本片段可独立设置颜色、字体</li>
 *   <li>块级默认样式（本类配置） - 通过 Builder 的 color()、font() 等方法设置</li>
 *   <li>全局配置 - 来自 {@link com.bytefuture.easy.poster.model.Config}</li>
 *   <li>系统默认值 - Color.BLACK、Font.PLAIN、16px</li>
 * </ol>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 纯文本：所有文字使用统一样式
 * TextElement.of("Hello World")
 *     .color(Color.RED)
 *     .fontSize(24);
 *
 * // 富文本：不同片段使用不同样式
 * TextElement.rich(
 *     TextSpan.of("Hello").setColor(Color.RED),
 *     TextSpan.of(" World").setColor(Color.BLUE).setFontSize(32)
 * );
 * }</pre>
 */
@Getter
public final class TextElementConfig {
    /**
     * 纯文本内容。
     */
    private final String text;
    /** 富文本片段列表。 */
    private final List<TextSpan> textSpans;
    /** 默认文字颜色。 */
    private final Color color;
    /** 默认字体名称。 */
    private final String fontName;
    /** 默认字体样式。 */
    private final int fontStyle;
    /** 默认字体大小。 */
    private final int fontSize;
    /** 完整字体对象，优先级高于字体名称和字号配置。 */
    private final Font font;
    /** 基线策略。 */
    private final BaseLine baseLine;
    /** 多行文本对齐方式。 */
    private final TextAlign textAlign;
    /** 是否启用自动换行。 */
    private final boolean autoWordWrap;
    /** 文本最大布局宽度。 */
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

    /**
     * 判断当前配置是否没有可渲染文本。
     *
     * @return 没有文本内容时返回 {@code true}
     */
    public boolean isEmpty() {
        return (text == null || text.isEmpty()) && textSpans.isEmpty();
    }

    /**
     * 将配置转换为富文本片段列表。
     *
     * @return 富文本片段列表
     */
    public List<TextSpan> toRichSpans() {
        if (!textSpans.isEmpty()) {
            return textSpans;
        }
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(TextSpan.of(text));
    }

    /**
     * 转换为块级默认样式，用于与片段样式合并。
     * <p>
     * 注意：此方法返回的样式仅包含当前配置中显式设置的属性，
     * 未设置的属性为 null，由样式解析器进行级联填充。
     *
     * @return 块级样式
     */
    public TextBlockStyle toBlockStyle() {
        TextBlockStyle style = new TextBlockStyle();
        style.setColor(this.color);
        style.setFontName(this.fontName);
        style.setFontStyle(this.fontStyle);
        style.setFontSize(this.fontSize);
        return style;
    }

    /**
     * 创建纯文本配置构建器。
     *
     * @param text 文本内容
     * @return 构建器实例
     */
    public static Builder builder(String text) {
        return new Builder(text);
    }

    /**
     * 创建富文本配置构建器。
     *
     * @param spans 文本片段列表
     * @return 构建器实例
     */
    public static Builder builder(TextSpan... spans) {
        return new Builder(spans);
    }

    /**
     * 文本配置构建器。
     */
    public static final class Builder {
        /** 纯文本内容。 */
        private String text;
        /** 富文本片段列表。 */
        private final List<TextSpan> textSpans = new ArrayList<>();
        /** 默认颜色。 */
        private Color color;
        /** 默认字体名称。 */
        private String fontName;
        /** 默认字体样式。 */
        private int fontStyle = Font.PLAIN;
        /** 默认字体大小。 */
        private int fontSize = 16;
        /** 完整字体对象。 */
        private Font font;
        /** 基线策略。 */
        private BaseLine baseLine = BaseLine.BASE_LINE;
        /** 文本对齐方式。 */
        private TextAlign textAlign = TextAlign.LEFT;
        /** 是否自动换行。 */
        private boolean autoWordWrap = false;
        /** 最大布局宽度。 */
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

        /**
         * 设置默认颜色。
         *
         * @param color 颜色值
         * @return 当前构建器
         */
        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        /**
         * 设置默认颜色。
         *
         * @param hexColor 十六进制颜色值
         * @return 当前构建器
         */
        public Builder color(String hexColor) {
            this.color = ColorUtils.hexToColor(hexColor);
            return this;
        }

        /**
         * 设置字体名称。
         *
         * @param fontName 字体名称
         * @return 当前构建器
         */
        public Builder fontName(String fontName) {
            this.fontName = fontName;
            return this;
        }

        /**
         * 设置字体样式。
         *
         * @param fontStyle 字体样式
         * @return 当前构建器
         */
        public Builder fontStyle(int fontStyle) {
            this.fontStyle = fontStyle;
            return this;
        }

        /**
         * 设置字体大小。
         *
         * @param fontSize 字体大小
         * @return 当前构建器
         */
        public Builder fontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        /**
         * 同时设置字体名称、样式和大小。
         *
         * @param fontName 字体名称
         * @param fontStyle 字体样式
         * @param fontSize 字体大小
         * @return 当前构建器
         */
        public Builder font(String fontName, int fontStyle, int fontSize) {
            this.fontName = fontName;
            this.fontStyle = fontStyle;
            this.fontSize = fontSize;
            return this;
        }

        /**
         * 设置完整字体对象。
         *
         * @param font 字体对象
         * @return 当前构建器
         */
        public Builder font(Font font) {
            this.font = font;
            return this;
        }

        /**
         * 设置基线策略。
         *
         * @param baseLine 基线类型
         * @return 当前构建器
         */
        public Builder baseLine(BaseLine baseLine) {
            this.baseLine = baseLine;
            return this;
        }

        /**
         * 设置文本对齐方式。
         *
         * @param textAlign 对齐方式
         * @return 当前构建器
         */
        public Builder textAlign(TextAlign textAlign) {
            this.textAlign = textAlign;
            return this;
        }

        /**
         * 启用自动换行。
         *
         * @param maxWidth 最大布局宽度
         * @return 当前构建器
         */
        public Builder autoWordWrap(int maxWidth) {
            this.autoWordWrap = true;
            this.maxTextWidth = maxWidth;
            return this;
        }

        /**
         * 设置布局宽度。
         *
         * @param layoutWidth 布局宽度
         * @return 当前构建器
         */
        public Builder layoutWidth(int layoutWidth) {
            this.autoWordWrap = layoutWidth > 0;
            this.maxTextWidth = layoutWidth;
            return this;
        }

        /**
         * 追加一个文本片段。
         *
         * @param span 文本片段
         * @return 当前构建器
         */
        public Builder textSpan(TextSpan span) {
            if (span != null) {
                this.textSpans.add(span);
            }
            return this;
        }

        /**
         * 追加多个文本片段。
         *
         * @param spans 文本片段列表
         * @return 当前构建器
         */
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

        /**
         * 构建文本配置。
         *
         * @return 文本配置实例
         */
        public TextElementConfig build() {
            return new TextElementConfig(this);
        }
    }
}
