package com.bytefuture.easy.poster.element.v2.text.style;

import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.element.v2.text.resolve.ResolvedStyleContext;
import com.bytefuture.easy.poster.element.v2.text.resolve.ResolvedTextRun;
import com.bytefuture.easy.poster.model.Config;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.TextSpan;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文本样式解析器
 * 承担样式解析流程的全部职责，包括基础字体、默认颜色和文本运行单元的解析
 *
 * <h3>样式合并规则：</h3>
 * <pre>
 * 字体名称: span.fontName > block.fontName > baseFont.family
 * 字体样式: span.fontStyle > block.fontStyle > baseFont.style
 * 字体大小: span.fontSize > block.fontSize > baseFont.size
 * 颜色:     span.color > block.color > defaultColor
 * </pre>
 *
 * @author biaoy
 * @since 2026/04/26
 */
public class TextStyleResolver {

    /**
     * 执行完整的样式解析流程
     *
     * @param element 文本元素
     * @param context 海报上下文
     * @return 样式解析结果
     */
    public ResolvedStyleContext resolve(TextElement element, PosterContext context) {
        TextBlockStyle blockStyle = element.getBlockStyle();
        Font baseFont = resolveBaseFont(blockStyle, context.getConfig());
        Color defaultColor = resolveDefaultColor(blockStyle, context.getConfig());
        List<ResolvedTextRun> runs = resolveRuns(element.getTextSpans(), blockStyle, baseFont, defaultColor);
        return new ResolvedStyleContext(baseFont, defaultColor, runs, blockStyle);
    }

    /**
     * 解析文本块默认字体
     *
     * @param blockStyle   文本块样式
     * @param globalConfig 全局配置
     * @return 默认字体
     */
    public Font resolveBaseFont(TextBlockStyle blockStyle, Config globalConfig) {
        // 块级样式直接给出 Font 实例时优先级最高，避免再次从名称、字号、样式重建
        if (blockStyle.getFont() != null) {
            return blockStyle.getFont();
        }
        String fontName = blockStyle.getFontName() != null ? blockStyle.getFontName() : globalConfig.getFontName();
        int fontStyle = blockStyle.getFontStyle() != null ? blockStyle.getFontStyle() : Font.PLAIN;
        int fontSize = blockStyle.getFontSize() != null ? blockStyle.getFontSize() : globalConfig.getFontSize();
        return new Font(fontName, fontStyle, fontSize);
    }

    /**
     * 解析文本块默认颜色
     *
     * @param blockStyle   文本块样式
     * @param globalConfig 全局配置
     * @return 默认颜色
     */
    public Color resolveDefaultColor(TextBlockStyle blockStyle, Config globalConfig) {
        // 块级配置优先，其次回落到全局配置，最后兜底黑色，保证渲染阶段一定有可用颜色
        if (blockStyle.getColor() != null) {
            return blockStyle.getColor();
        }
        if (globalConfig != null && globalConfig.getColor() != null) {
            return globalConfig.getColor();
        }
        return Color.BLACK;
    }

    /**
     * 批量解析文本片段为运行单元
     *
     * @param spans        原始文本片段集合
     * @param blockStyle   文本块样式
     * @param baseFont     解析得到的基础字体
     * @param defaultColor 解析得到的默认颜色
     * @return 样式已收敛的文本运行单元列表
     */
    public List<ResolvedTextRun> resolveRuns(List<TextSpan> spans, TextBlockStyle blockStyle,
                                             Font baseFont, Color defaultColor) {
        if (spans.isEmpty()) {
            return Collections.emptyList();
        }
        List<ResolvedTextRun> runs = new ArrayList<>(spans.size());
        for (TextSpan span : spans) {
            runs.add(resolve(span, blockStyle, baseFont, defaultColor));
        }
        return runs;
    }

    /**
     * 解析单个文本片段的最终样式
     * <p>
     * 样式合并遵循以下优先级（从高到低）：
     * <ol>
     *   <li>TextSpan 片段级样式 - 用户为特定文本设置的样式</li>
     *   <li>TextBlockStyle 块级样式 - TextElement 上配置的默认样式</li>
     *   <li>baseFont/defaultColor - 从全局配置或系统默认值推导的基础样式</li>
     * </ol>
     *
     * @param span         文本片段
     * @param blockStyle   块级默认样式
     * @param baseFont     基础字体
     * @param defaultColor 默认颜色
     * @return 已解析的文本运行段
     */
    public ResolvedTextRun resolve(TextSpan span, TextBlockStyle blockStyle, Font baseFont, Color defaultColor) {
        BaseTextStyle spanStyle = span.getSpanStyle();
        // 字体名称：片段 > 块级 > 基础字体族名
        String fontName = firstNonNull(spanStyle.getFontName(), blockStyle.getFontName(), baseFont.getFamily());
        // 字体样式：片段 > 块级 > 基础字体样式
        int fontStyle = firstNonNull(spanStyle.getFontStyle(), blockStyle.getFontStyle(), baseFont.getStyle());
        // 字体大小：片段 > 块级 > 基础字体大小
        int fontSize = firstNonNull(spanStyle.getFontSize(), blockStyle.getFontSize(), baseFont.getSize());
        // 颜色：片段 > 块级 > 默认颜色
        Color color = firstNonNull(spanStyle.getColor(), blockStyle.getColor(), defaultColor);
        boolean underline = firstNonNull(spanStyle.getUnderline(), blockStyle.getUnderline(), Boolean.FALSE);
        boolean strikeThrough = firstNonNull(spanStyle.getStrikeThrough(), blockStyle.getStrikeThrough(), Boolean.FALSE);
        // 字间距：片段 > 块级 > 默认 0
        int letterSpacing = firstNonNull(spanStyle.getLetterSpacing(), blockStyle.getLetterSpacing(), 0);
        return new ResolvedTextRun(span.getText(),
                new ResolvedTextStyle(new Font(fontName, fontStyle, fontSize), color, underline, strikeThrough, letterSpacing));
    }

    /**
     * 返回三个值中第一个非空项
     *
     * @param first  第一优先级值
     * @param second 第二优先级值
     * @param third  第三优先级值
     * @param <T>    值类型
     * @return 第一个非空值
     */
    private <T> T firstNonNull(T first, T second, T third) {
        if (first != null) {
            return first;
        }
        if (second != null) {
            return second;
        }
        return third;
    }

    /**
     * 返回三个值中第一个非空项（Integer 转 int）
     *
     * @param first  第一优先级值
     * @param second 第二优先级值
     * @param third  第三优先级值（默认值）
     * @return 第一个非空值或默认值
     */
    private int firstNonNull(Integer first, Integer second, int third) {
        if (first != null) {
            return first;
        }
        if (second != null) {
            return second;
        }
        return third;
    }
}