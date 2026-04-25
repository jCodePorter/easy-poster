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
import com.bytefuture.easy.poster.element.v2.text.split.ITextSplitter;
import com.bytefuture.easy.poster.element.v2.text.split.SplitTextInfo;
import com.bytefuture.easy.poster.element.v2.text.split.TextSplitRequest;
import com.bytefuture.easy.poster.element.v2.text.split.TextSplitResult;
import com.bytefuture.easy.poster.element.v2.text.split.TextSplitterSimpleImpl;
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
     * 是否启用自适应文本
     */
    private boolean autoFitText = false;

    /**
     * 自适应文本的目标宽度
     */
    private int autoFitTargetWidth;

    /**
     * 自适应文本的最小字体大小
     */
    private int autoFitMinFontSize;

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
     * 设置自适应调整文本大小以适应指定宽度
     * 注意：这是一个延迟计算的方法，实际计算会在 calculateDimension 时执行
     * 这样可以确保在调用此方法后，修改字体样式等参数仍然生效
     *
     * @param targetWidth 目标宽度
     * @param minFontSize 最小字体大小
     * @return this
     */
    public TextElement setAutoFitText(int targetWidth, int minFontSize) {
        this.autoFitText = true;
        this.autoFitTargetWidth = targetWidth;
        this.autoFitMinFontSize = minFontSize;
        return this;
    }

    /**
     * 执行自适应文本大小计算
     * 此方法会在 calculateDimension 时自动调用
     */
    private void performAutoFitText(Graphics2D g) {
        if (!this.autoFitText) {
            return;
        }

        // 如果已经计算过，跳过
        if (this.fontSize != null && this.fontSize >= this.autoFitMinFontSize) {
            // 检查当前字体是否仍然适合目标宽度
            Font currentFont = new Font(
                Optional.ofNullable(this.fontName).orElse(g.getFont().getFamily()),
                Optional.ofNullable(this.fontStyle).orElse(g.getFont().getStyle()),
                this.fontSize
            );
            FontMetrics fm = g.getFontMetrics(currentFont);
            Rectangle2D textBounds = fm.getStringBounds(this.text, g);
            int textWidth = (int) textBounds.getWidth();
            
            // 如果当前字体大小已经适合，不需要重新计算
            if (textWidth <= this.autoFitTargetWidth) {
                return;
            }
        }

        int currentFontSize = Optional.ofNullable(this.fontSize).orElse(this.autoFitMinFontSize);
        if (currentFontSize < this.autoFitMinFontSize) {
            currentFontSize = this.autoFitMinFontSize;
        }

        // 从当前字体大小开始计算
        int optimalFontSize = currentFontSize;
        Font font = new Font(
            Optional.ofNullable(this.fontName).orElse(g.getFont().getFamily()),
            Optional.ofNullable(this.fontStyle).orElse(g.getFont().getStyle()),
            optimalFontSize
        );
        FontMetrics fm = g.getFontMetrics(font);

        // 计算当前字体大小下的文本宽度
        Rectangle2D textBounds = fm.getStringBounds(this.text, g);
        int textWidth = (int) textBounds.getWidth();

        // 如果文本宽度大于目标宽度，则需要调整字体大小
        if (textWidth > this.autoFitTargetWidth) {
            // 估算合适的字体大小
            double scaleRatio = (double) this.autoFitTargetWidth / textWidth;
            optimalFontSize = (int) Math.floor(optimalFontSize * scaleRatio);

            // 确保不小于最小字体大小
            optimalFontSize = Math.max(optimalFontSize, this.autoFitMinFontSize);

            // 重新计算字体度量
            font = new Font(
                Optional.ofNullable(this.fontName).orElse(g.getFont().getFamily()),
                Optional.ofNullable(this.fontStyle).orElse(g.getFont().getStyle()),
                optimalFontSize
            );
            fm = g.getFontMetrics(font);
            textBounds = fm.getStringBounds(this.text, g);
            textWidth = (int) textBounds.getWidth();

            // 如果即使使用最小字体大小仍然超出宽度，则启用自动换行
            if (optimalFontSize == this.autoFitMinFontSize && textWidth > this.autoFitTargetWidth) {
                this.autoWordWrap = true;
                this.maxTextWidth = this.autoFitTargetWidth;
            }
        }

        this.fontSize = optimalFontSize;
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
        
        // 执行自适应文本大小计算（延迟计算）
        performAutoFitText(g);
        
        // 设置字体，确保后续计算使用正确的字体
        g.setFont(getFont(context.getConfig()));
        FontMetrics fm = g.getFontMetrics();

        // 行高处理
        Integer lineHeightCfg = Optional.ofNullable(context.getConfig().getLineHeight()).orElse(this.lineHeight);

        // 单行高度
        int singleLineHeight = Optional.ofNullable(lineHeightCfg).orElse(fm.getHeight());

        // 文本拆分
        List<SplitTextInfo> splitTextInfos = getSplitTextInfos(fm, g);

        // 计算文本总高度（考虑多行）
        int totalHeight = singleLineHeight * splitTextInfos.size();

        // 计算折算文本的起始坐标点
        this.splitTextPointWrapper = populatePoint(posterWidth, posterHeight, splitTextInfos, totalHeight);
        int width = this.splitTextPointWrapper.stream().map(s -> s.getInfo().getWidth())
                .max(Integer::compareTo).orElse(maxTextWidth);
        // 返回第一个坐标点作为基准元素
        Point firstPoint = this.splitTextPointWrapper.get(0).getPoint();

        BaseLine baseLineCfg = getBaseLineCfg(context);
        Dimension.DimensionBuilder builder = Dimension.builder()
                .width(width)
                .height(totalHeight)
                .yOffset(getYOffset(baseLineCfg, fm, singleLineHeight))
                .point(Point.of(firstPoint.getX(), firstPoint.getY()));
        if (this.getRotate() != 0) {
            int[] newBounds = RotateUtils.newBounds(width, totalHeight, this.getRotate());
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
            TextSplitResult result = splitter.split(TextSplitRequest.of(text, maxTextWidth, fm));
            splitTextInfos = result.getLines();
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
        int lineHeight = resolveLineHeight(dimension);

        for (int i = 0; i < this.splitTextPointWrapper.size(); i++) {
            SplitTextPointWrapper wrapper = this.splitTextPointWrapper.get(i);

            int startX = wrapper.getPoint().getX() + dimension.getXOffset() + xDiff;
            int startY = wrapper.getPoint().getY() + dimension.getYOffset() + i * lineHeight + yDiff;
            if (this.getRotate() != 0) {
                double rotateX = point.getX() + dimension.getWidth() / 2.0;
                double rotateY = point.getY() + lineHeight / 2.0 + i * lineHeight;

                AffineTransform rotateTransform = AffineTransform.getRotateInstance(Math.toRadians(rotate), rotateX, rotateY);
                AffineTransform savedTransform = g.getTransform();
                g.setTransform(rotateTransform);
                doDrawText(context, wrapper.getInfo().getText(), startX, startY, dimension, lineHeight);
                g.setTransform(savedTransform);
            } else {
                doDrawText(context, wrapper.getInfo().getText(), startX, startY, dimension, lineHeight);
            }
        }
        return dimension.getPoint();
    }

    private int resolveLineHeight(Dimension dimension) {
        if (this.splitTextPointWrapper == null || this.splitTextPointWrapper.isEmpty()) {
            return dimension.getHeight();
        }
        return Math.max(1, dimension.getHeight() / this.splitTextPointWrapper.size());
    }

    private void doDrawText(PosterContext context, String text, int startX, int startY, Dimension dimension, int lineHeight) {
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
            g.drawRect(startX, baseY, dimension.getWidth(), lineHeight);
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
