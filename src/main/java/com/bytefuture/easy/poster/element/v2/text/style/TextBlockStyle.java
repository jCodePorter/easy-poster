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
public class TextBlockStyle extends BaseTextStyle {

    /**
     * 字体
     */
    private Font font;

    /**
     * 基线
     */
    private BaseLine baseLine = BaseLine.BASE_LINE;

    /**
     * 文本对齐方式
     */
    private TextAlign textAlign = TextAlign.LEFT;

    /**
     * 是否自动换行，设置文本最大宽度
     */
    private int maxTextWidth = 0;

    /**
     * 行高
     */
    private Integer lineHeight;

    /**
     * 最大行数
     */
    private Integer maxLines;

    /**
     * 文本超出最大行数后的处理方式
     */
    private TextOverflow textOverflow = TextOverflow.CLIP;

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
        if (fontSize == null || fontSize <= 0) {
            throw new PosterException("fontSize must be greater than 0");
        }
        super.setFontSize(fontSize);
        return this;
    }

    public TextBlockStyle setFont(String fontName, int fontStyle, int fontSize) {
        setFontName(fontName);
        setFontStyle(fontStyle);
        setFontSize(fontSize);
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

    public TextBlockStyle maxTextWidth(int maxWidth) {
        if (maxWidth <= 0) {
            throw new PosterException("maxWidth must be greater than 0");
        }
        this.maxTextWidth = maxWidth;
        return this;
    }

    /**
     * 设置块级行高。
     *
     * @param lineHeight 行高，单位为像素，必须大于 0
     * @return 当前文本块样式
     */
    public TextBlockStyle setLineHeight(Integer lineHeight) {
        if (lineHeight == null || lineHeight <= 0) {
            throw new PosterException("lineHeight must be greater than 0");
        }
        this.lineHeight = lineHeight;
        return this;
    }

    /**
     * 设置最大行数
     *
     * @param maxLines 最大行数，必须大于 0
     * @return 当前文本块样式
     */
    public TextBlockStyle setMaxLines(Integer maxLines) {
        if (maxLines == null || maxLines <= 0) {
            throw new PosterException("maxLines must be greater than 0");
        }
        this.maxLines = maxLines;
        return this;
    }

    /**
     * 设置超出最大行数后的文本缩略方式
     *
     * @param textOverflow 文本缩略方式
     * @return 当前文本块样式
     */
    public TextBlockStyle setTextOverflow(TextOverflow textOverflow) {
        if (textOverflow == null) {
            throw new PosterException("textOverflow can not be null");
        }
        this.textOverflow = textOverflow;
        return this;
    }

    /**
     * 设置块级文本是否绘制下划线。
     *
     * @param underline 是否绘制下划线
     * @return 当前文本块样式
     */
    public TextBlockStyle setUnderline(Boolean underline) {
        super.setUnderline(underline);
        return this;
    }

    /**
     * 设置块级文本是否绘制删除线。
     *
     * @param strikeThrough 是否绘制删除线
     * @return 当前文本块样式
     */
    public TextBlockStyle setStrikeThrough(Boolean strikeThrough) {
        super.setStrikeThrough(strikeThrough);
        return this;
    }

    /**
     * 设置字间距
     *
     * @param letterSpacing 字间距，单位为像素，必须大于等于 0
     * @return 当前文本块样式
     */
    public TextBlockStyle setLetterSpacing(Integer letterSpacing) {
        if (letterSpacing != null && letterSpacing < 0) {
            throw new PosterException("letterSpacing must be greater than or equal to 0");
        }
        super.setLetterSpacing(letterSpacing);
        return this;
    }
}
