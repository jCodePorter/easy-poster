package com.bytefuture.easy.poster.text.layout;

import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextOverflowStrategy;
import com.bytefuture.easy.poster.model.TextShadow;
import com.bytefuture.easy.poster.model.TextSpan;
import com.bytefuture.easy.poster.model.TextStroke;
import lombok.Getter;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public final class TextRenderSpec {

    /** 纯文本内容。 */
    private final String text;
    /** 富文本片段集合。 */
    private final List<TextSpan> textSpans;
    /** 文本块定位方式。 */
    private final Position position;
    /** 默认文本颜色。 */
    private final Color color;
    /** 基础字体。 */
    private final Font baseFont;
    /** 文本锚点基线。 */
    private final BaseLine baseLine;
    /** 行高。 */
    private final Integer lineHeight;
    /** 水平对齐方式。 */
    private final TextAlign textAlign;
    /** 溢出策略。 */
    private final TextOverflowStrategy overflowStrategy;
    /** 最大显示行数。 */
    private final Integer maxLines;
    /** 省略符文本。 */
    private final String ellipsis;
    /** 阴影效果。 */
    private final TextShadow shadow;
    /** 描边效果。 */
    private final TextStroke stroke;
    /** 字间距。 */
    private final int letterSpacing;
    /** 背景色。 */
    private final Color textBackgroundColor;
    /** 背景内边距。 */
    private final Margin textPadding;
    /** 背景圆角宽度。 */
    private final int textBackgroundArcWidth;
    /** 背景圆角高度。 */
    private final int textBackgroundArcHeight;
    /** 旋转角度。 */
    private final int rotate;
    /** 是否自动换行。 */
    private final boolean autoWordWrap;
    /** 布局宽度或换行上限宽度。 */
    private final int maxTextWidth;
    /** 是否自动缩放字体。 */
    private final boolean autoFitText;
    /** 自动缩放目标宽度。 */
    private final int autoFitTargetWidth;
    /** 自动缩放最小字号。 */
    private final int autoFitMinFontSize;
    /** 是否绘制下划线。 */
    private final boolean underline;
    /** 是否绘制删除线。 */
    private final boolean strikeThrough;

    public TextRenderSpec(String text, List<TextSpan> textSpans, Position position, Color color, Font baseFont,
                          BaseLine baseLine, Integer lineHeight, TextAlign textAlign, TextOverflowStrategy overflowStrategy,
                          Integer maxLines, String ellipsis, TextShadow shadow, TextStroke stroke, int letterSpacing,
                          Color textBackgroundColor, Margin textPadding, int textBackgroundArcWidth, int textBackgroundArcHeight,
                          int rotate, boolean autoWordWrap, int maxTextWidth, boolean autoFitText, int autoFitTargetWidth,
                          int autoFitMinFontSize, boolean underline, boolean strikeThrough) {
        this.text = text;
        this.textSpans = Collections.unmodifiableList(new ArrayList<TextSpan>(textSpans));
        this.position = position;
        this.color = color;
        this.baseFont = baseFont;
        this.baseLine = baseLine;
        this.lineHeight = lineHeight;
        this.textAlign = textAlign;
        this.overflowStrategy = overflowStrategy;
        this.maxLines = maxLines;
        this.ellipsis = ellipsis;
        this.shadow = shadow;
        this.stroke = stroke;
        this.letterSpacing = letterSpacing;
        this.textBackgroundColor = textBackgroundColor;
        this.textPadding = textPadding;
        this.textBackgroundArcWidth = textBackgroundArcWidth;
        this.textBackgroundArcHeight = textBackgroundArcHeight;
        this.rotate = rotate;
        this.autoWordWrap = autoWordWrap;
        this.maxTextWidth = maxTextWidth;
        this.autoFitText = autoFitText;
        this.autoFitTargetWidth = autoFitTargetWidth;
        this.autoFitMinFontSize = autoFitMinFontSize;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
    }

    public boolean hasRichTextSpans() {
        return !this.textSpans.isEmpty();
    }

    public int resolveWidthLimit() {
        // 显式布局宽度优先；未设置时，自动缩放目标宽度也可作为宽度约束参与换行/裁剪。
        if (this.maxTextWidth > 0) {
            return this.maxTextWidth;
        }
        if (this.autoFitText) {
            return this.autoFitTargetWidth;
        }
        return 0;
    }

    public String normalizedText() {
        // 统一换行符，避免 Windows/Unix 文本在布局上出现分支差异。
        return this.text == null ? "" : this.text.replace("\r\n", "\n").replace('\r', '\n');
    }

    public String cacheKey() {
        // 仅把影响布局和绘制结果的字段纳入缓存 key。
        StringBuilder builder = new StringBuilder();
        builder.append(Objects.toString(this.text, "")).append('|')
                .append(this.textSpans.size()).append('|')
                .append(Objects.toString(this.position, "")).append('|')
                .append(Objects.toString(this.color, "")).append('|')
                .append(Objects.toString(this.baseFont, "")).append('|')
                .append(Objects.toString(this.baseLine, "")).append('|')
                .append(Objects.toString(this.lineHeight, "")).append('|')
                .append(Objects.toString(this.textAlign, "")).append('|')
                .append(Objects.toString(this.overflowStrategy, "")).append('|')
                .append(Objects.toString(this.maxLines, "")).append('|')
                .append(this.ellipsis).append('|')
                .append(Objects.toString(this.shadow, "")).append('|')
                .append(Objects.toString(this.stroke, "")).append('|')
                .append(this.letterSpacing).append('|')
                .append(Objects.toString(this.textBackgroundColor, "")).append('|')
                .append(this.textPadding.getMarginLeft()).append(',')
                .append(this.textPadding.getMarginTop()).append(',')
                .append(this.textPadding.getMarginRight()).append(',')
                .append(this.textPadding.getMarginBottom()).append('|')
                .append(this.textBackgroundArcWidth).append('|')
                .append(this.textBackgroundArcHeight).append('|')
                .append(this.rotate).append('|')
                .append(this.autoWordWrap).append('|')
                .append(this.maxTextWidth).append('|')
                .append(this.autoFitText).append('|')
                .append(this.autoFitTargetWidth).append('|')
                .append(this.autoFitMinFontSize).append('|')
                .append(this.underline).append('|')
                .append(this.strikeThrough);
        for (TextSpan span : this.textSpans) {
            // 富文本缓存需要把片段级样式一并纳入，否则不同片段样式会误命中缓存。
            builder.append('|')
                    .append(Objects.toString(span.getText(), ""))
                    .append(':').append(Objects.toString(span.getColor(), ""))
                    .append(':').append(Objects.toString(span.getFontStyle(), ""))
                    .append(':').append(Objects.toString(span.getUnderline(), ""))
                    .append(':').append(Objects.toString(span.getStrikeThrough(), ""));
        }
        return builder.toString();
    }
}
