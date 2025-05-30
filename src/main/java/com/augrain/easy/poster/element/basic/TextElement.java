package com.augrain.easy.poster.element.basic;

import com.augrain.easy.poster.element.AbstractRepeatableElement;
import com.augrain.easy.poster.element.IElement;
import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.geometry.Dimension;
import com.augrain.easy.poster.model.BaseLine;
import com.augrain.easy.poster.model.PosterContext;
import com.augrain.easy.poster.model.Config;
import com.augrain.easy.poster.text.ITextSplitter;
import com.augrain.easy.poster.text.TextSplitterSimpleImpl;
import com.augrain.easy.poster.utils.RotateUtils;
import lombok.Getter;

import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 文本元素，java中文本字符串在绘制时，按照字体排印学中原则，坐标点 y 值，即绘制文本的base line
 *
 * @author biaoy
 * @since 2025/02/21
 */
@Getter
public class TextElement extends AbstractRepeatableElement<TextElement> implements IElement {

    /**
     * 待绘制的文本
     */
    private final String text;

    /**
     * 字体颜色，默认为黑色
     */
    private Color fontColor;

    /**
     * 字体名称，默认为微软雅黑
     */
    private String fontName;

    /**
     * 字体样式，加粗，斜体，比如：Font.BOLD, Font.ITALIC，或者 Font.BOLD | Font.ITALIC
     */
    private Integer fontStyle;

    /**
     * 字体大小，默认12pt
     */
    private Integer fontSize;

    /**
     * 自定义字体
     */
    private Font font;

    /**
     * 文本对齐方式，默认居中对齐
     */
    private BaseLine baseLine;

    /**
     * 行高
     */
    private Integer lineHeight;

    /**
     * 是否自动换行
     */
    private boolean autoWordWrap = false;

    /**
     * 最大文本宽度
     */
    private int maxTextWidth;

    /**
     * 删除线
     */
    private boolean strikeThrough = false;

    // 程序处理过程中所需数据
    private List<String> splitText;

    public TextElement(String text) {
        this.text = text;
    }

    public static TextElement of(String text) {
        return new TextElement(text);
    }

    public TextElement setFontColor(Color fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    public TextElement setFontName(String fontName) {
        this.fontName = fontName;
        return this;
    }

    public TextElement setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public TextElement setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
        return this;
    }

    public TextElement setFont(String fontName, int fontStyle, int fontSize) {
        this.fontName = fontName;
        this.fontStyle = fontStyle;
        this.fontSize = fontSize;
        return this;
    }

    public TextElement setFont(Font font) {
        this.font = font;
        return this;
    }

    public TextElement setBaseLine(BaseLine baseLine) {
        this.baseLine = baseLine;
        return this;
    }

    public TextElement setLineHeight(Integer lineHeight) {
        this.lineHeight = lineHeight;
        return this;
    }

    public TextElement setAutoWrapText(int maxTextWidth) {
        this.autoWordWrap = true;
        this.maxTextWidth = maxTextWidth;
        return this;
    }

    public TextElement setStrikeThrough(boolean strikeThrough) {
        this.strikeThrough = strikeThrough;
        return this;
    }

    private Font getFont(Config config) {
        Font fontConfig = Optional.ofNullable(this.font).orElse(config.getFont());
        if (fontConfig != null) {
            return fontConfig;
        }
        return new Font(Optional.ofNullable(this.fontName).orElse(config.getFontName()),
                Optional.ofNullable(this.fontStyle).orElse(config.getFontStyle()),
                Optional.ofNullable(this.fontSize).orElse(config.getFontSize()));
    }

    @Override
    public Dimension calculateDimension(PosterContext context, int posterWidth, int posterHeight) {
        Graphics2D g = context.getGraphics();
        FontMetrics fm = g.getFontMetrics();

        // 行高处理
        Integer lineHeightCfg = Optional.ofNullable(context.getConfig().getLineHeight()).orElse(this.lineHeight);

        // 文本宽高
        int width;
        int height = Optional.ofNullable(lineHeightCfg).orElse(fm.getHeight());
        if (autoWordWrap) {
            ITextSplitter splitter = new TextSplitterSimpleImpl();
            this.splitText = splitter.splitText(text, maxTextWidth, fm);
            width = maxTextWidth;
        } else {
            Rectangle2D textBounds = fm.getStringBounds(text, g);
            width = (int) textBounds.getWidth();
            this.splitText = Collections.singletonList(this.text);
        }

        Point point = Point.ORIGIN_COORDINATE;
        if (position != null) {
            point = position.calculate(posterWidth, posterHeight, width, height);
        }

        BaseLine baseLineCfg = Optional.ofNullable(this.baseLine).orElse(context.getConfig().getBaseLine());
        Dimension.DimensionBuilder builder = Dimension.builder()
                .width(width)
                .height(height)
                .yOffset(baseLineCfg.getOffset(fm, height))
                .point(point);
        if (this.getRotate() != 0) {
            int[] newBounds = RotateUtils.newBounds(width, height, this.getRotate());
            builder.rotateWidth(newBounds[0])
                    .rotateHeight(newBounds[1]);
        }
        return builder.build();
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        super.gradient(context, dimension);
        Point point = dimension.getPoint();

        Graphics2D g = context.getGraphics();
        for (int i = 0; i < this.splitText.size(); i++) {
            int startX = point.getX() + dimension.getXOffset();
            int startY = point.getY() + dimension.getYOffset() + i * dimension.getHeight();
            if (this.getRotate() != 0) {
                double rotateX = point.getX() + dimension.getWidth() / 2.0;
                double rotateY = point.getY() + dimension.getHeight() / 2.0 + i * dimension.getHeight();

                AffineTransform rotateTransform = AffineTransform.getRotateInstance(Math.toRadians(rotate), rotateX, rotateY);
                AffineTransform savedTransform = g.getTransform();
                g.setTransform(rotateTransform);
                doDrawText(context, splitText.get(i), startX, startY, dimension);
                g.setTransform(savedTransform);
            } else {
                doDrawText(context, splitText.get(i), startX, startY, dimension);
            }
        }
        return dimension.getPoint();
    }

    private void doDrawText(PosterContext context, String text, int startX, int startY, Dimension dimension) {
        Graphics2D g = context.getGraphics();

        if (context.getConfig().isDebug()) {
            FontMetrics fontMetrics = g.getFontMetrics();
            LineMetrics lineMetrics = fontMetrics.getLineMetrics(text, g);
            float ascent = lineMetrics.getAscent();
            int diffHeight = (Optional.ofNullable(this.lineHeight).orElse(fontMetrics.getHeight()) - fontMetrics.getHeight()) / 2;
            g.drawRect(startX, (int) (startY - ascent - diffHeight), dimension.getWidth(), dimension.getHeight());
        }
        if (this.strikeThrough) {
            AttributedString as = new AttributedString(text);
            as.addAttribute(TextAttribute.FONT, g.getFont());
            as.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, 0, text.length());
            g.drawString(as.getIterator(), startX, startY);
        } else {
            g.drawString(text, startX, startY);
        }
    }

    @Override
    public void beforeRender(PosterContext context) {
        super.beforeRender(context);
        Graphics2D g = context.getGraphics();
        g.setFont(getFont(context.getConfig()));
        g.setColor(Optional.ofNullable(this.fontColor).orElse(context.getConfig().getColor()));
    }
}