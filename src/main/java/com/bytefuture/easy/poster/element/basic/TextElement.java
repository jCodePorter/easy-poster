package com.bytefuture.easy.poster.element.basic;

import com.bytefuture.easy.poster.element.AbstractRepeatableElement;
import com.bytefuture.easy.poster.element.IElement;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.Config;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.text.ITextSplitter;
import com.bytefuture.easy.poster.text.SplitTextInfo;
import com.bytefuture.easy.poster.text.TextSplitterSimpleImpl;
import com.bytefuture.easy.poster.utils.RotateUtils;
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
 * 当定位为 AbsolutePosition 时，不支持 direction 属性
 * 同时当为 RelativePosition 时，不支持 baseline 属性
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
    private List<SplitTextPointWrapper> splitTextPointWrapper;

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

    /**
     * 设置文本自动换行
     *
     * @param maxTextWidth 单行文本最大宽度
     */
    public TextElement setAutoWrapText(int maxTextWidth) {
        this.autoWordWrap = true;
        this.maxTextWidth = maxTextWidth;
        return this;
    }

    /**
     * 自适应调整文本大小以适应指定宽度
     * 如果达到最小字体大小后仍无法单行显示，则使用最小字体大小并启用自动换行
     *
     * @param targetWidth 目标宽度
     * @param minFontSize 最小字体大小
     * @return this
     */
    public TextElement setAutoFitText(int targetWidth, int minFontSize) {
        if (this.fontSize == null || this.fontSize < minFontSize) {
            this.fontSize = minFontSize;
        }

        // 临时创建Graphics2D对象来测量文本宽度
        Graphics2D g = createTempGraphics2D();

        // 从当前字体大小开始递减，直到找到适合的字体大小或达到最小字体大小
        int optimalFontSize = this.fontSize;
        FontMetrics fm = g.getFontMetrics(new Font(this.fontName, this.fontStyle != null ? this.fontStyle : Font.PLAIN, optimalFontSize));

        // 计算当前字体大小下的文本宽度
        Rectangle2D textBounds = fm.getStringBounds(this.text, g);
        int textWidth = (int) textBounds.getWidth();

        // 如果文本宽度大于目标宽度，则需要调整字体大小
        if (textWidth > targetWidth) {
            // 估算合适的字体大小
            double scaleRatio = (double) targetWidth / textWidth;
            optimalFontSize = (int) Math.floor(optimalFontSize * scaleRatio);

            // 确保不小于最小字体大小
            optimalFontSize = Math.max(optimalFontSize, minFontSize);

            // 重新计算字体度量
            fm = g.getFontMetrics(new Font(this.fontName, this.fontStyle != null ? this.fontStyle : Font.PLAIN, optimalFontSize));
            textBounds = fm.getStringBounds(this.text, g);
            textWidth = (int) textBounds.getWidth();

            // 如果即使使用最小字体大小仍然超出宽度，则启用自动换行
            if (optimalFontSize == minFontSize && textWidth > targetWidth) {
                this.autoWordWrap = true;
                this.maxTextWidth = targetWidth;
            }
        }

        this.fontSize = optimalFontSize;
        return this;
    }

    /**
     * 创建临时的Graphics2D对象用于文本测量
     * @return Graphics2D对象
     */
    private Graphics2D createTempGraphics2D() {
        // 创建一个临时的BufferedImage来获取Graphics2D对象
        java.awt.image.BufferedImage tempImage = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
        return tempImage.createGraphics();
    }

    /**
     * 设置删除线
     *
     * @param strikeThrough 是否展示删除线
     */
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
        this.splitTextPointWrapper = populatePoint(posterWidth, posterHeight, splitTextInfos, height);
        int width = this.splitTextPointWrapper.stream().map(s -> s.getInfo().getWidth())
                .max(Integer::compareTo).orElse(maxTextWidth);
        // 返回第一个坐标点作为基准元素
        Point firstPoint = this.splitTextPointWrapper.get(0).getPoint();

        BaseLine baseLineCfg = getBaseLineCfg(context);
        Dimension.DimensionBuilder builder = Dimension.builder()
                .width(width)
                .height(height)
                .yOffset(getYOffset(baseLineCfg, fm, height))
                .point(Point.of(firstPoint.getX(), firstPoint.getY()));
        if (this.getRotate() != 0) {
            int[] newBounds = RotateUtils.newBounds(width, height, this.getRotate());
            builder.rotateWidth(newBounds[0])
                    .rotateHeight(newBounds[1]);
        }
        return builder.build();
    }

    /**
     * 获取垂直方向偏移量
     *
     * @param baseLineCfg 文本对齐方式配置
     * @param fm          FontMetrics对象
     * @param height      行高
     * @return 偏移量
     */
    private int getYOffset(BaseLine baseLineCfg, FontMetrics fm, int height) {
        // 如果是绝对布局，通过文本对齐方式进行便宜，
        // 如果是相对布局，则直接偏移 fm.getAscent() 的距离
        return getPosition() instanceof AbsolutePosition ? baseLineCfg.getOffset(fm, height) : fm.getAscent();
    }

    private BaseLine getBaseLineCfg(PosterContext context) {
        return Optional.ofNullable(this.baseLine).orElse(context.getConfig().getBaseLine());
    }

    private List<SplitTextPointWrapper> populatePoint(int posterWidth, int posterHeight, List<SplitTextInfo> splitTextInfos, int height) {
        return splitTextInfos.stream().map(t -> {
            if (position instanceof RelativePosition) {
                Point textPoint = position.calculate(posterWidth, posterHeight, t.getWidth(), height);
                return new SplitTextPointWrapper(t, textPoint);
            } else if (position instanceof AbsolutePosition) {
                return new SplitTextPointWrapper(t, ((AbsolutePosition) position).getPoint());
            } else {
                return new SplitTextPointWrapper(t, Point.ORIGIN_COORDINATE);
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
        int xDiff = dimension.getPoint().getX() - this.splitTextPointWrapper.get(0).getPoint().getX();
        int yDiff = dimension.getPoint().getY() - this.splitTextPointWrapper.get(0).getPoint().getY();

        for (int i = 0; i < this.splitTextPointWrapper.size(); i++) {
            SplitTextPointWrapper wrapper = this.splitTextPointWrapper.get(i);

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
    private static class SplitTextPointWrapper {
        private SplitTextInfo info;

        private Point point;

        public SplitTextPointWrapper(SplitTextInfo info, Point point) {
            this.info = info;
            this.point = point;
        }
    }
}