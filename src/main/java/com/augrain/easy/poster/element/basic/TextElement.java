package com.augrain.easy.poster.element.basic;

import com.augrain.easy.poster.element.AbstractRepeatableElement;
import com.augrain.easy.poster.element.IElement;
import com.augrain.easy.poster.geometry.AbsolutePosition;
import com.augrain.easy.poster.geometry.Dimension;
import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.geometry.RelativePosition;
import com.augrain.easy.poster.model.BaseLine;
import com.augrain.easy.poster.model.Config;
import com.augrain.easy.poster.model.PosterContext;
import com.augrain.easy.poster.text.ITextSplitter;
import com.augrain.easy.poster.text.SplitTextInfo;
import com.augrain.easy.poster.text.TextSplitterSimpleImpl;
import com.augrain.easy.poster.utils.RotateUtils;
import lombok.Data;
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
import java.util.stream.Collectors;

/**
 * 文本元素，java中文本字符串在绘制时，按照字体排印学中原则，坐标点 y 值，即绘制文本的base line
 * <p>
 * TODO 文本，不应该支持 AbsolutePosition中的direction属性，而应该使用左对齐和右对齐；同时当为RelativePosition时，不支持baseline，相关属性互相冲突
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
    private List<SplitTextWrapper> splitText;

    public TextElement(String text) {
        this.text = text;
    }

    public static TextElement of(String text) {
        return new TextElement(text);
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
        int height = Optional.ofNullable(lineHeightCfg).orElse(fm.getHeight());

        // 文本拆分
        List<SplitTextInfo> splitTextInfos = getSplitTextInfos(fm, g);

        // 计算折算文本的起始坐标点
        this.splitText = calcPoint(posterWidth, posterHeight, splitTextInfos, height);
        int width = this.splitText.stream().map(s -> s.getInfo().getWidth())
                .max(Integer::compareTo).orElse(maxTextWidth);
        // 返回第一个坐标点作为基准元素
        Point firstPoint = this.splitText.get(0).getPoint();

        BaseLine baseLineCfg = getBaseLineCfg(context);
        Dimension.DimensionBuilder builder = Dimension.builder()
                .width(width)
                .height(height)
                .yOffset(getPosition() instanceof AbsolutePosition ? baseLineCfg.getOffset(fm, height) : 0)
                .point(Point.of(firstPoint.getX(), firstPoint.getY()));
        if (this.getRotate() != 0) {
            int[] newBounds = RotateUtils.newBounds(width, height, this.getRotate());
            builder.rotateWidth(newBounds[0])
                    .rotateHeight(newBounds[1]);
        }
        return builder.build();
    }

    private BaseLine getBaseLineCfg(PosterContext context) {
        return Optional.ofNullable(this.baseLine).orElse(context.getConfig().getBaseLine());
    }

    private List<SplitTextWrapper> calcPoint(int posterWidth, int posterHeight, List<SplitTextInfo> splitTextInfos, int height) {
        return splitTextInfos.stream().map(t -> {
            if (position instanceof RelativePosition) {
                Point textPoint = position.calculate(posterWidth, posterHeight, t.getWidth(), height);
                return new SplitTextWrapper(t, textPoint);
            } else if (position instanceof AbsolutePosition) {
                return new SplitTextWrapper(t, ((AbsolutePosition) position).getPoint());
            } else {
                return new SplitTextWrapper(t, Point.ORIGIN_COORDINATE);
            }
        }).collect(Collectors.toList());
    }

    private List<SplitTextInfo> getSplitTextInfos(FontMetrics fm, Graphics2D g) {
        List<SplitTextInfo> splitTextInfos;
        if (autoWordWrap) {
            ITextSplitter splitter = new TextSplitterSimpleImpl();
            splitTextInfos = splitter.splitText(text, maxTextWidth, fm);
        } else {
            Rectangle2D textBounds = fm.getStringBounds(text, g);
            splitTextInfos = Collections.singletonList(SplitTextInfo.of(this.text, (int) textBounds.getWidth()));
        }
        return splitTextInfos;
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        super.gradient(context, dimension);
        Graphics2D g = context.getGraphics();

        // 计算基准坐标被父元素修改调整的差值
        Point point = dimension.getPoint();
        int xDiff = dimension.getPoint().getX() - this.splitText.get(0).getPoint().getX();
        int yDiff = dimension.getPoint().getY() - this.splitText.get(0).getPoint().getY();

        for (int i = 0; i < this.splitText.size(); i++) {
            SplitTextWrapper wrapper = this.splitText.get(i);

            int startX = wrapper.getPoint().getX() + dimension.getXOffset() + xDiff;
            int startY = wrapper.getPoint().getY() + dimension.getYOffset() + i * dimension.getHeight() + yDiff;
            if (this.getRotate() != 0) {
                double rotateX = point.getX() + dimension.getWidth() / 2.0;
                double rotateY = point.getY() + dimension.getHeight() / 2.0 + i * dimension.getHeight();

                AffineTransform rotateTransform = AffineTransform.getRotateInstance(Math.toRadians(rotate), rotateX, rotateY);
                AffineTransform savedTransform = g.getTransform();
                g.setTransform(rotateTransform);
                doDrawText(context, wrapper.getInfo().getText(), startX, startY, dimension);
                g.setTransform(savedTransform);
            } else {
                doDrawText(context, wrapper.getInfo().getText(), startX, startY, dimension);
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

            int baseY = (int) (startY - ascent - diffHeight);
            BaseLine baseLineCfg = getBaseLine();
            if (baseLineCfg == BaseLine.TOP) {
                baseY += diffHeight;
            } else if (baseLineCfg == BaseLine.BOTTOM) {
                baseY -= diffHeight;
            }
            g.drawRect(startX, baseY, dimension.getWidth(), dimension.getHeight());
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
        g.setColor(Optional.ofNullable(this.color).orElse(context.getConfig().getColor()));
    }

    @Override
    public void debug(PosterContext context, Dimension dimension) {
        // 文本由于需要换行，覆盖父类方法，啥也不做，具体执行渲染时，再处理
    }

    @Data
    private static class SplitTextWrapper {
        private SplitTextInfo info;

        private Point point;

        public SplitTextWrapper(SplitTextInfo info, Point point) {
            this.info = info;
            this.point = point;
        }
    }
}