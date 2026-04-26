package com.bytefuture.easy.poster.element.v2.text.style;

import cn.augrain.easy.tool.support.ColorUtils;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.TextAlign;
import lombok.Getter;

import java.awt.*;

/**
 * 文本块级默认样式。
 * 除了基础字体颜色属性外，还负责承载基线、对齐与换行等布局参数。
 */
@Getter
public final class TextBlockStyle extends BaseTextStyle {
    private Font font;
    private BaseLine baseLine = BaseLine.BASE_LINE;
    private TextAlign textAlign = TextAlign.LEFT;
    private boolean autoWordWrap = false;
    private int maxTextWidth = 0;
    private Integer lineHeight;

    public TextBlockStyle setColor(Color color) {
        if (color == null) {
            throw new PosterException("text color can not be null");
        }
        super.setColor(color);
        return this;
    }

    public TextBlockStyle setColor(String color) {
        return setColor(ColorUtils.hexToColor(color));
    }

    public TextBlockStyle setFontName(String fontName) {
        if (fontName == null) {
            throw new PosterException("fontName can not be null");
        }
        super.setFontName(fontName);
        return this;
    }

    public TextBlockStyle setFontStyle(Integer fontStyle) {
        super.setFontStyle(fontStyle);
        return this;
    }

    public TextBlockStyle setFontSize(Integer fontSize) {
        if (fontSize == null || fontSize.intValue() <= 0) {
            throw new PosterException("fontSize must be greater than 0");
        }
        super.setFontSize(fontSize);
        return this;
    }

    public TextBlockStyle setFont(String fontName, int fontStyle, int fontSize) {
        setFontName(fontName);
        setFontStyle(Integer.valueOf(fontStyle));
        setFontSize(Integer.valueOf(fontSize));
        this.font = null;
        return this;
    }

    public TextBlockStyle setFont(Font font) {
        if (font == null) {
            throw new PosterException("font can not be null");
        }
        this.font = font;
        return this;
    }

    public TextBlockStyle setBaseLine(BaseLine baseLine) {
        if (baseLine == null) {
            throw new PosterException("baseLine can not be null");
        }
        this.baseLine = baseLine;
        return this;
    }

    public TextBlockStyle setTextAlign(TextAlign textAlign) {
        if (textAlign == null) {
            throw new PosterException("textAlign can not be null");
        }
        this.textAlign = textAlign;
        return this;
    }

    public TextBlockStyle setAutoWordWrap(int maxWidth) {
        if (maxWidth <= 0) {
            throw new PosterException("maxWidth must be greater than 0");
        }
        this.autoWordWrap = true;
        this.maxTextWidth = maxWidth;
        return this;
    }

    public TextBlockStyle setLayoutWidth(int layoutWidth) {
        this.autoWordWrap = layoutWidth > 0;
        this.maxTextWidth = layoutWidth;
        return this;
    }

    /**
     * 设置块级行高。
     *
     * @param lineHeight 行高，单位为像素，必须大于 0
     * @return 当前文本块样式
     */
    public TextBlockStyle setLineHeight(Integer lineHeight) {
        if (lineHeight == null || lineHeight.intValue() <= 0) {
            throw new PosterException("lineHeight must be greater than 0");
        }
        this.lineHeight = lineHeight;
        return this;
    }
}
