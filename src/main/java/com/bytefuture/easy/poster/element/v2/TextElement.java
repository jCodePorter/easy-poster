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
import lombok.Getter;

import java.awt.*;
import java.util.List;

/**
 * V2 版本文本元素。
 * 负责将文本配置转换为布局结果，并在渲染阶段输出到画布。
 */
public class TextElement extends AbstractRepeatableElement<TextElement> {

    /**
     * 文本元素的静态配置。
     */
    @Getter
    private final TextElementConfig config;
    /**
     * 文本布局计算器。
     */
    private final TextLayoutEngine layoutEngine;
    /**
     * 文本绘制器。
     */
    private final TextRenderer renderer;
    /**
     * 最近一次布局结果，供渲染阶段复用。
     */
    @Getter
    private transient TextLayoutResult lastLayout;

    /**
     * 使用纯文本内容创建文本元素。
     *
     * @param text 文本内容
     */
    private TextElement(String text) {
        this(TextElementConfig.builder(text).build());
    }

    /**
     * 使用完整配置创建文本元素。
     *
     * @param config 文本配置
     */
    private TextElement(TextElementConfig config) {
        this.config = config;
        this.layoutEngine = new TextLayoutEngine();
        this.renderer = new TextRenderer();
        // 文本元素的样式由 TextElementConfig 统一管理，禁用父类 color 字段
        this.color = null;
    }

    /**
     * 创建纯文本元素。
     *
     * @param text 文本内容
     * @return 文本元素实例
     */
    public static TextElement of(String text) {
        return new TextElement(text);
    }

    /**
     * 创建富文本元素。
     *
     * @param spans 文本片段列表
     * @return 文本元素实例
     */
    public static TextElement of(TextSpan... spans) {
        return new TextElement(TextElementConfig.builder(spans).build());
    }

    /**
     * 创建纯文本构建器。
     *
     * @param text 文本内容
     * @return 构建器实例
     */
    public static Builder builder(String text) {
        return new Builder(text);
    }

    /**
     * 创建富文本构建器。
     *
     * @param spans 文本片段列表
     * @return 构建器实例
     */
    public static Builder builder(TextSpan... spans) {
        return new Builder(spans);
    }

    /**
     * 计算文本最终占用尺寸，并缓存布局结果。
     *
     * @param context      海报上下文
     * @param posterWidth  海报宽度
     * @param posterHeight 海报高度
     * @return 文本布局尺寸
     */
    @Override
    public Dimension calculateDimension(PosterContext context, int posterWidth, int posterHeight) {
        // 文本样式完全由 config 管理，不再传递 overrideColor
        TextLayoutResult layout = layoutEngine.layout(config, position, rotate, context, posterWidth, posterHeight);
        this.lastLayout = layout;
        return layout.toDimension(rotate);
    }

    /**
     * 按最近一次布局结果渲染文本；若无缓存则即时重新布局。
     *
     * @param context      海报上下文
     * @param dimension    元素尺寸
     * @param posterWidth  海报宽度
     * @param posterHeight 海报高度
     * @return 文本渲染起点
     */
    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        TextLayoutResult layout = lastLayout;
        if (layout == null) {
            // 渲染阶段兜底重新布局，避免跳过 calculateDimension 时无法输出文本。
            layout = layoutEngine.layout(config, position, rotate, context, posterWidth, posterHeight);
            this.lastLayout = layout;
        }
        return renderer.render(context, dimension, layout, rotate);
    }

    /**
     * 文本元素构建器。
     */
    public static final class Builder {
        /**
         * 内部文本配置构建器。
         */
        private final TextElementConfig.Builder configBuilder;
        /**
         * 元素定位方式。
         */
        private Position position;
        /**
         * 绘制透明度。
         */
        private float alpha = 1F;
        /**
         * 旋转角度。
         */
        private int rotate = 0;

        private Builder(String text) {
            this.configBuilder = TextElementConfig.builder(text);
        }

        private Builder(TextSpan[] spans) {
            this.configBuilder = TextElementConfig.builder(spans);
        }

        /**
         * 设置默认文本颜色。
         * <p>
         * 注意：此方法设置的是块级默认颜色，会被 TextSpan 的片段级颜色覆盖。
         *
         * @param color 颜色值
         * @return 当前构建器
         */
        public Builder color(Color color) {
            this.configBuilder.color(color);
            return this;
        }

        /**
         * 设置默认文本颜色。
         * <p>
         * 注意：此方法设置的是块级默认颜色，会被 TextSpan 的片段级颜色覆盖。
         *
         * @param hexColor 十六进制颜色值
         * @return 当前构建器
         */
        public Builder color(String hexColor) {
            this.configBuilder.color(hexColor);
            return this;
        }

        /**
         * 设置字体名称。
         *
         * @param fontName 字体名称
         * @return 当前构建器
         */
        public Builder fontName(String fontName) {
            this.configBuilder.fontName(fontName);
            return this;
        }

        /**
         * 设置字体样式。
         *
         * @param fontStyle 字体样式
         * @return 当前构建器
         */
        public Builder fontStyle(int fontStyle) {
            this.configBuilder.fontStyle(fontStyle);
            return this;
        }

        /**
         * 设置字体大小。
         *
         * @param fontSize 字体大小
         * @return 当前构建器
         */
        public Builder fontSize(int fontSize) {
            this.configBuilder.fontSize(fontSize);
            return this;
        }

        /**
         * 同时设置字体名称、样式和大小。
         *
         * @param fontName  字体名称
         * @param fontStyle 字体样式
         * @param fontSize  字体大小
         * @return 当前构建器
         */
        public Builder font(String fontName, int fontStyle, int fontSize) {
            this.configBuilder.font(fontName, fontStyle, fontSize);
            return this;
        }

        /**
         * 设置完整字体对象。
         *
         * @param font 字体对象
         * @return 当前构建器
         */
        public Builder font(Font font) {
            this.configBuilder.font(font);
            return this;
        }

        /**
         * 设置基线策略。
         *
         * @param baseLine 基线类型
         * @return 当前构建器
         */
        public Builder baseLine(BaseLine baseLine) {
            this.configBuilder.baseLine(baseLine);
            return this;
        }

        /**
         * 设置文本对齐方式。
         *
         * @param textAlign 对齐方式
         * @return 当前构建器
         */
        public Builder textAlign(TextAlign textAlign) {
            this.configBuilder.textAlign(textAlign);
            return this;
        }

        /**
         * 启用自动换行。
         *
         * @param maxWidth 最大文本宽度
         * @return 当前构建器
         */
        public Builder autoWordWrap(int maxWidth) {
            this.configBuilder.autoWordWrap(maxWidth);
            return this;
        }

        /**
         * 设置布局宽度。
         *
         * @param layoutWidth 布局宽度，大于 0 时按该宽度换行
         * @return 当前构建器
         */
        public Builder layoutWidth(int layoutWidth) {
            this.configBuilder.layoutWidth(layoutWidth);
            return this;
        }

        /**
         * 追加一个富文本片段。
         *
         * @param span 文本片段
         * @return 当前构建器
         */
        public Builder textSpan(TextSpan span) {
            this.configBuilder.textSpan(span);
            return this;
        }

        /**
         * 追加多个富文本片段。
         *
         * @param spans 文本片段列表
         * @return 当前构建器
         */
        public Builder textSpans(List<TextSpan> spans) {
            this.configBuilder.textSpans(spans);
            return this;
        }

        /**
         * 设置元素位置。
         *
         * @param position 位置定义
         * @return 当前构建器
         */
        public Builder position(Position position) {
            this.position = position;
            return this;
        }

        /**
         * 设置透明度。
         *
         * @param alpha 透明度
         * @return 当前构建器
         */
        public Builder alpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        /**
         * 设置旋转角度。
         *
         * @param rotate 旋转角度
         * @return 当前构建器
         */
        public Builder rotate(int rotate) {
            this.rotate = rotate;
            return this;
        }

        /**
         * 构建文本元素。
         *
         * @return 文本元素实例
         */
        public TextElement build() {
            TextElement element = new TextElement(configBuilder.build());
            element.position = this.position;
            element.alpha = this.alpha;
            element.rotate = this.rotate;
            return element;
        }
    }
}
