package com.bytefuture.easy.poster.element.basic;

import com.bytefuture.easy.poster.element.AbstractRepeatableElement;
import com.bytefuture.easy.poster.element.basic.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.element.basic.text.layout.VerticalTextLayoutResult;
import com.bytefuture.easy.poster.element.basic.text.pipeline.TextPipeline;
import com.bytefuture.easy.poster.element.basic.text.render.HorizontalTextRenderer;
import com.bytefuture.easy.poster.element.basic.text.style.TextBlockStyle;
import com.bytefuture.easy.poster.element.basic.text.style.TextOverflow;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.*;
import lombok.AccessLevel;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * V2 版本文本元素
 * 文本内容由元素本身持有，块级样式由 {@link TextBlockStyle} 管理
 * <p>
 * 整体流程分为三大阶段：
 * 1. 样式解析 - 由 {@link TextPipeline} 协调 {@link com.bytefuture.easy.poster.element.basic.text.style.TextStyleResolver} 完成
 * 2. 布局计算 - 由 {@link TextPipeline} 协调 {@link com.bytefuture.easy.poster.element.basic.text.layout.TextLayoutEngine} 完成
 * 3. 渲染绘制 - 由 {@link TextPipeline} 协调 {@link HorizontalTextRenderer} 完成
 *
 * @author biaoy
 * @since 2025/02/21
 */
@Getter
public class TextElement extends AbstractRepeatableElement<TextElement> {

    /**
     * 文本片段列表
     */
    private final List<TextSpan> textSpans;

    /**
     * 块级样式配置
     */
    private final TextBlockStyle blockStyle;

    /**
     * 文本渲染流程编排器
     */
    private final TextPipeline pipeline = new TextPipeline();

    /**
     * 最近一次布局结果，供渲染阶段复用
     */
    @Getter(AccessLevel.NONE)
    private transient Object lastLayout;

    /**
     * 使用纯文本内容创建文本元素
     *
     * @param text 文本内容
     */
    public TextElement(String text) {
        this.blockStyle = new TextBlockStyle();
        this.color = null;
        this.textSpans = Collections.singletonList(TextSpan.of(text, blockStyle));
    }

    /**
     * 使用富文本片段创建文本元素
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
     * 构建文本元素
     *
     * @param text 文本内容
     * @return 文本元素实例
     */
    public static TextElement of(String text) {
        return new TextElement(text);
    }

    /**
     * 构建文本元素
     *
     * @param spans 文本片段
     * @return 文本元素实例
     */
    public static TextElement of(TextSpan... spans) {
        return new TextElement(spans);
    }

    /**
     * 判断当前元素是否没有可渲染文本
     *
     * @return 没有文本内容时返回 {@code true}
     */
    public boolean isEmpty() {
        return textSpans.isEmpty();
    }

    @Override
    public Dimension calculateDimension(PosterContext context, int posterWidth, int posterHeight) {
        Object layout = pipeline.resolveLayout(this, position, context, posterWidth, posterHeight);
        this.lastLayout = layout;
        if (layout instanceof TextLayoutResult) {
            return ((TextLayoutResult) layout).toDimension(rotate);
        }
        return ((VerticalTextLayoutResult) layout).toDimension(rotate);
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        Object layout = lastLayout;
        if (layout == null) {
            layout = pipeline.resolveLayout(this, position, context, posterWidth, posterHeight);
        }
        WritingMode writingMode = blockStyle.getWritingMode();
        if (writingMode == WritingMode.VERTICAL) {
            return pipeline.renderVertical(context, dimension, (VerticalTextLayoutResult) layout, rotate, this.gradient, blockStyle.getColumnDirection());
        }
        return pipeline.render(context, dimension, (TextLayoutResult) layout, rotate, this.gradient);
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
     * 设置字体名称
     *
     * @param fontName 字体名称
     * @return 当前元素
     */
    public TextElement setFontName(String fontName) {
        this.blockStyle.setFontName(fontName);
        return this;
    }

    /**
     * 设置字体样式
     *
     * @param fontStyle 字体样式
     * @return 当前元素
     */
    public TextElement setFontStyle(int fontStyle) {
        this.blockStyle.setFontStyle(fontStyle);
        return this;
    }

    /**
     * 设置字体大小
     *
     * @param fontSize 字体大小
     * @return 当前元素
     */
    public TextElement setFontSize(int fontSize) {
        this.blockStyle.setFontSize(fontSize);
        return this;
    }

    /**
     * 同时设置字体名称、样式和大小
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
     * 设置完整字体对象
     *
     * @param font 字体对象
     * @return 当前元素
     */
    public TextElement setFont(Font font) {
        this.blockStyle.setFont(font);
        return this;
    }

    /**
     * 设置基线策略
     *
     * @param baseLine 基线策略
     * @return 当前元素
     */
    public TextElement setBaseLine(BaseLine baseLine) {
        this.blockStyle.setBaseLine(baseLine);
        return this;
    }

    /**
     * 设置文本对齐方式
     *
     * @param textAlign 对齐方式
     * @return 当前元素
     */
    public TextElement setTextAlign(TextAlign textAlign) {
        this.blockStyle.setTextAlign(textAlign);
        return this;
    }

    /**
     * 启用自动换行
     *
     * @param maxWidth 最大布局宽度
     * @return 当前元素
     */
    public TextElement setMaxTextWidth(int maxWidth) {
        this.blockStyle.setMaxTextWidth(maxWidth);
        return this;
    }

    /**
     * 设置块级行高
     *
     * @param lineHeight 行高，单位为像素
     * @return 当前元素
     */
    public TextElement setLineHeight(int lineHeight) {
        this.blockStyle.setLineHeight(lineHeight);
        return this;
    }

    /**
     * 设置自动换行后的最大行数
     *
     * @param maxLines 最大行数
     * @return 当前元素
     */
    public TextElement setMaxLines(int maxLines) {
        this.blockStyle.setMaxLines(maxLines);
        return this;
    }

    /**
     * 设置超出最大行数后的文本缩略方式
     *
     * @param textOverflow 文本缩略方式
     * @return 当前元素
     */
    public TextElement setTextOverflow(TextOverflow textOverflow) {
        this.blockStyle.setTextOverflow(textOverflow);
        return this;
    }

    /**
     * 设置块级文本是否绘制下划线
     *
     * @param underline 是否绘制下划线
     * @return 当前元素
     */
    public TextElement setUnderline(boolean underline) {
        this.blockStyle.setUnderline(underline);
        return this;
    }

    /**
     * 设置块级文本是否绘制删除线
     *
     * @param strikeThrough 是否绘制删除线
     * @return 当前元素
     */
    public TextElement setStrikeThrough(boolean strikeThrough) {
        this.blockStyle.setStrikeThrough(strikeThrough);
        return this;
    }

    /**
     * 设置字间距
     *
     * @param letterSpacing 字间距，单位为像素
     * @return 当前元素
     */
    public TextElement setLetterSpacing(Integer letterSpacing) {
        this.blockStyle.setLetterSpacing(letterSpacing);
        return this;
    }

    /**
     * 设置书写模式为竖排
     *
     * @return 当前元素
     */
    public TextElement vertical() {
        this.blockStyle.setWritingMode(WritingMode.VERTICAL);
        return this;
    }

    /**
     * 设置书写模式为横排
     *
     * @return 当前元素
     */
    public TextElement horizontal() {
        this.blockStyle.setWritingMode(WritingMode.HORIZONTAL);
        return this;
    }

    /**
     * 设置竖排列方向为从右到左
     *
     * @return 当前元素
     */
    public TextElement columnRightToLeft() {
        this.blockStyle.setColumnDirection(ColumnDirection.RIGHT_TO_LEFT);
        return this;
    }

    /**
     * 设置竖排列方向为从左到右
     *
     * @return 当前元素
     */
    public TextElement columnLeftToRight() {
        this.blockStyle.setColumnDirection(ColumnDirection.LEFT_TO_RIGHT);
        return this;
    }

    /**
     * 设置竖排最大列高
     *
     * @param maxVerticalWidth 最大列高
     * @return 当前元素
     */
    public TextElement maxVerticalWidth(int maxVerticalWidth) {
        this.blockStyle.setMaxVerticalWidth(maxVerticalWidth);
        return this;
    }

    /**
     * 设置竖排列内对齐方式
     *
     * @param verticalAlign 列内对齐方式
     * @return 当前元素
     */
    public TextElement setVerticalAlign(VerticalAlign verticalAlign) {
        this.blockStyle.setVerticalAlign(verticalAlign);
        return this;
    }

    /**
     * 设置竖排列间距（列与列之间的左右留白）
     *
     * @param columnSpacing 列间距，单位为像素
     * @return 当前元素
     */
    public TextElement setColumnSpacing(int columnSpacing) {
        this.blockStyle.setColumnSpacing(columnSpacing);
        return this;
    }

    /**
     * 设置自适应调整文本大小以适应指定宽度
     * 仅对单 TextSpan 的横排文本生效
     *
     * @param targetWidth 目标宽度
     * @param minFontSize 最小字体大小
     * @return 当前元素
     */
    public TextElement setAutoFitText(int targetWidth, int minFontSize) {
        this.blockStyle.setAutoFitText(targetWidth, minFontSize);
        return this;
    }

    /**
     * 设置绘制的其实位置，复用父类提供的 setPosition 方法，简化配置
     *
     * @param point 起始坐标点
     */
    public TextElement setPosition(Point point) {
        super.setPosition(AbsolutePosition.of(point, Direction.TOP_LEFT));
        return this;
    }
}
