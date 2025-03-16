package com.augrain.easy.canvas.element.basic;

import com.augrain.easy.canvas.element.AbstractElement;
import com.augrain.easy.canvas.element.IElement;
import com.augrain.easy.canvas.enums.BaseLine;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;
import com.augrain.easy.canvas.utils.RotateUtils;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Optional;

/**
 * 文本元素，java中文本字符串在绘制时，按照字体排印学中原则，坐标点 y 值，即绘制文本的base line
 *
 * @author biaoy
 * @since 2025/02/21
 */
@Getter
public class TextElement extends AbstractElement implements IElement {

    private final String text;

    /**
     * 字体颜色，默认为黑色
     */
    private Color fontColor = Color.BLACK;

    /**
     * 字体名称，默认为微软雅黑
     */
    private String fontName = "微软雅黑";

    /**
     * 字体样式，加粗，斜体，比如：Font.BOLD, Font.ITALIC，或者 Font.BOLD | Font.ITALIC
     */
    private int fontStyle = Font.PLAIN;

    /**
     * 字体大小，默认12pt
     */
    private int fontSize = 12;

    /**
     * 自定义字体
     */
    private Font font;

    /**
     * 文本对齐方式，默认居中对齐
     */
    private BaseLine baseLine = BaseLine.CENTER;

    /**
     * 行高
     */
    private Integer lineHeight;

    public TextElement(String text) {
        this.text = text;
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

    public Font getFont() {
        if (this.font != null) {
            return this.font;
        }
        return new Font(this.fontName, this.fontStyle, this.fontSize);
    }

    @Override
    public Dimension calDimension(Graphics2D g, int canvasWidth, int canvasHeight) {
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D textBounds = fm.getStringBounds(text, g);
        int width = (int) textBounds.getWidth();
        int height = Optional.ofNullable(this.lineHeight).orElse((int) textBounds.getHeight());

        CoordinatePoint point = CoordinatePoint.ORIGIN_COORDINATE;
        if (position != null) {
            point = position.calculate(canvasWidth, canvasHeight, width, height);
        }

        Dimension.DimensionBuilder builder = Dimension.builder()
                .width(width)
                .height(height)
                .yOffset(baseLine.getOffset(fm, height))
                .point(point);
        if (this.getRotate() != 0) {
            int[] newBounds = RotateUtils.newBounds(width, height, this.getRotate());
            builder.rotateWidth(newBounds[0])
                    .rotateHeight(newBounds[1]);
        }
        return builder.build();
    }

    @Override
    public CoordinatePoint doRender(Graphics2D g, Dimension dimension, int canvasWidth, int canvasHeight) {
        String text = this.getText();

        CoordinatePoint point;
        if (isPositionUpdated()) {
            point = getPosition().calculate(canvasWidth, canvasHeight, dimension.getWidth(), dimension.getHeight());
        } else {
            point = dimension.getPoint();
        }

        int startX = point.getX() + dimension.getXOffset();
        int startY = point.getY() + dimension.getYOffset();
        if (this.getRotate() != 0) {
            double rotateX = point.getX() + dimension.getWidth() / 2.0;
            double rotateY = point.getY() + dimension.getHeight() / 2.0;

            AffineTransform rotateTransform = AffineTransform.getRotateInstance(Math.toRadians(rotate), rotateX, rotateY);
            AffineTransform savedTransform = g.getTransform();
            g.setTransform(rotateTransform);
            g.drawString(text, startX, startY);
            g.setTransform(savedTransform);
        } else {
            g.drawString(text, startX, startY);
        }
        return CoordinatePoint.of(startX, startY);

    }

    @Override
    public void beforeRender(Graphics2D g) {
        g.setFont(getFont());
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
        g.setColor(getFontColor());
    }
}