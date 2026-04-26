package com.bytefuture.easy.poster.element.v2;

import com.bytefuture.easy.poster.element.AbstractRepeatableElement;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.element.v2.text.style.TextBlockStyle;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.TextSpan;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * V2 版本文本元素。
 * 文本内容由元素本身持有，块级样式由 {@link TextBlockStyle} 管理。
 */
@Getter
public class TextElement extends AbstractRepeatableElement<TextElement> {

    /**
     * 文本片段列表。
     */
    private final List<TextSpan> textSpans;
    /**
     * 块级样式配置。
     */
    private final TextBlockStyle blockStyle;
    /**
     * 文本布局计算器。
     */
    private final TextLayoutEngine layoutEngine = new TextLayoutEngine();
    /**
     * 文本绘制器。
     */
    private final TextRenderer renderer = new TextRenderer();
    /**
     * 最近一次布局结果，供渲染阶段复用。
     */
    private transient TextLayoutResult lastLayout;

    /**
     * 使用纯文本内容创建文本元素。
     *
     * @param text 文本内容
     */
    public TextElement(String text) {
        this.blockStyle = new TextBlockStyle();
        this.color = null;
        this.textSpans = Collections.singletonList(TextSpan.of(text, blockStyle));
    }

    /**
     * 使用富文本片段创建文本元素。
     *
     * @param spans 文本片段
     */
    public TextElement(TextSpan... spans) {
        List<TextSpan> values = new ArrayList<>();
        if (spans != null) {
            for (TextSpan span : spans) {
                if (span != null) {
                    values.add(span);
                }
            }
        }
        this.textSpans = Collections.unmodifiableList(values);
        this.blockStyle = new TextBlockStyle();
        this.color = null;
    }

    /**
     * 构建文本元素。
     *
     * @param text 文本内容
     * @return 文本元素实例
     */
    public static TextElement of(String text) {
        return new TextElement(text);
    }

    /**
     * 构建文本元素。
     *
     * @param spans 文本片段
     * @return 文本元素实例
     */
    public static TextElement of(TextSpan... spans) {
        return new TextElement(spans);
    }

    /**
     * 判断当前元素是否没有可渲染文本。
     *
     * @return 没有文本内容时返回 {@code true}
     */
    public boolean isEmpty() {
        return textSpans.isEmpty();
    }

    @Override
    public Dimension calculateDimension(PosterContext context, int posterWidth, int posterHeight) {
        TextLayoutResult layout = layoutEngine.layout(this, position, rotate, context, posterWidth, posterHeight);
        this.lastLayout = layout;
        return layout.toDimension(rotate);
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        TextLayoutResult layout = lastLayout;
        if (layout == null) {
            layout = layoutEngine.layout(this, position, rotate, context, posterWidth, posterHeight);
            this.lastLayout = layout;
        }
        return renderer.render(context, dimension, layout, rotate);
    }

    @Override
    public TextElement setColor(Color color) {
        this.blockStyle.setColor(color);
        this.color = null;
        return this;
    }

    @Override
    public TextElement setColor(String color) {
        this.blockStyle.setColor(color);
        this.color = null;
        return this;
    }

    /**
     * 设置字体名称。
     *
     * @param fontName 字体名称
     * @return 当前元素
     */
    public TextElement setFontName(String fontName) {
        this.blockStyle.setFontName(fontName);
        return this;
    }

    /**
     * 设置字体样式。
     *
     * @param fontStyle 字体样式
     * @return 当前元素
     */
    public TextElement setFontStyle(int fontStyle) {
        this.blockStyle.setFontStyle(fontStyle);
        return this;
    }

    /**
     * 设置字体大小。
     *
     * @param fontSize 字体大小
     * @return 当前元素
     */
    public TextElement setFontSize(int fontSize) {
        this.blockStyle.setFontSize(fontSize);
        return this;
    }

    /**
     * 同时设置字体名称、样式和大小。
     *
     * @param fontName  字体名称
     * @param fontStyle 字体样式
     * @param fontSize  字体大小
     * @return 当前元素
     */
    public TextElement setFont(String fontName, int fontStyle, int fontSize) {
        this.blockStyle.setFont(fontName, fontStyle, fontSize);
        return this;
    }

    /**
     * 设置完整字体对象。
     *
     * @param font 字体对象
     * @return 当前元素
     */
    public TextElement setFont(Font font) {
        this.blockStyle.setFont(font);
        return this;
    }

    /**
     * 设置基线策略。
     *
     * @param baseLine 基线策略
     * @return 当前元素
     */
    public TextElement setBaseLine(com.bytefuture.easy.poster.model.BaseLine baseLine) {
        this.blockStyle.setBaseLine(baseLine);
        return this;
    }

    /**
     * 设置文本对齐方式。
     *
     * @param textAlign 对齐方式
     * @return 当前元素
     */
    public TextElement setTextAlign(com.bytefuture.easy.poster.model.TextAlign textAlign) {
        this.blockStyle.setTextAlign(textAlign);
        return this;
    }

    /**
     * 启用自动换行。
     *
     * @param maxWidth 最大布局宽度
     * @return 当前元素
     */
    public TextElement setAutoWordWrap(int maxWidth) {
        this.blockStyle.setAutoWordWrap(maxWidth);
        return this;
    }

    /**
     * 设置布局宽度。
     *
     * @param layoutWidth 布局宽度
     * @return 当前元素
     */
    public TextElement setLayoutWidth(int layoutWidth) {
        this.blockStyle.setLayoutWidth(layoutWidth);
        return this;
    }
}
