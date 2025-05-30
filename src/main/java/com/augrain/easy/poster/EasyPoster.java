package com.augrain.easy.poster;

import com.augrain.easy.poster.element.IElement;
import com.augrain.easy.poster.element.basic.*;
import com.augrain.easy.poster.exception.PosterException;
import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.model.Config;
import com.augrain.easy.poster.model.PosterContext;
import com.augrain.easy.poster.model.PosterListener;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 画布
 *
 * @author biaoy
 * @since 2025/02/20
 */
public class EasyPoster {
    /**
     * 待绘制的元素集合
     */
    private final List<IElement> renderedElements = new ArrayList<>();

    /**
     * 海报宽度
     */
    private final int posterWidth;

    /**
     * 海报高度
     */
    private final int posterHeight;

    /**
     * 海报监听
     */
    @Setter
    private PosterListener posterListener;

    /**
     * 全局配置
     */
    @Getter
    private final Config config = new Config();

    /**
     * EasyPoster构造方法
     *
     * @param posterWidth  宽
     * @param posterHeight 高
     */
    public EasyPoster(int posterWidth, int posterHeight) {
        this.posterWidth = posterWidth;
        this.posterHeight = posterHeight;
    }

    /**
     * EasyPoster构造方法
     *
     * @param backgroundImg 背景图
     */
    public EasyPoster(BufferedImage backgroundImg) {
        this.posterWidth = backgroundImg.getWidth();
        this.posterHeight = backgroundImg.getHeight();
        addImageElement(backgroundImg);
    }

    /**
     * 添加元素
     */
    public void addElement(IElement element) {
        renderedElements.add(element);
    }

    /**
     * 添加文本
     *
     * @param text 文本
     */
    public TextElement addTextElement(String text) {
        TextElement textElement = new TextElement(text);
        renderedElements.add(textElement);
        return textElement;
    }

    /**
     * 添加图片
     *
     * @param input 输入的图片
     */
    public ImageElement addImageElement(BufferedImage input) {
        ImageElement textElement = new ImageElement(input);
        renderedElements.add(textElement);
        return textElement;
    }

    /**
     * 添加矩形
     *
     * @param width  宽度
     * @param height 高度
     */
    public RectangleElement addRectangleElement(int width, int height) {
        RectangleElement rectElement = new RectangleElement(width, height);
        renderedElements.add(rectElement);
        return rectElement;
    }

    /**
     * 添加圆形
     *
     * @param radius 半径
     */
    public CircleElement addCircleElement(int radius) {
        CircleElement circleElement = new CircleElement(radius);
        renderedElements.add(circleElement);
        return circleElement;
    }

    /**
     * 添加椭圆
     *
     * @param width  宽度
     * @param height 高度
     */
    public CircleElement addOvalElement(int width, int height) {
        CircleElement circleElement = new CircleElement(width, height);
        renderedElements.add(circleElement);
        return circleElement;
    }

    public LineElement addLineElement(Point start, Point end) {
        LineElement lineElement = new LineElement(start, end);
        renderedElements.add(lineElement);
        return lineElement;
    }

    /**
     * 渲染图片，返回图片对象
     */
    private BufferedImage render() throws Exception {
        BufferedImage baseImg = new BufferedImage(posterWidth, posterHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = baseImg.createGraphics();
        // 抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setColor(Color.white);
        g.fillRect(0, 0, posterWidth, posterHeight);

        // 创建PosterContext
        PosterContext posterContext = buildPosterContext(g);

        // 循环绘制各元素
        for (IElement element : renderedElements) {
            element.render(posterContext, posterWidth, posterHeight);
        }
        g.dispose();
        return baseImg;
    }

    private PosterContext buildPosterContext(Graphics2D g) {
        PosterContext posterContext = new PosterContext();
        posterContext.setConfig(config);
        posterContext.setEasyPoster(this);
        posterContext.setGraphics(g);
        return posterContext;
    }

    public void asFile(String format, String filePath) {
        try {
            BufferedImage image = this.render();
            if (posterListener != null) {
                image = posterListener.beforeOut(image);
            }
            ImageIO.write(image, format, new File(filePath));
        } catch (Exception e) {
            throw new PosterException(e);
        }
    }

    public byte[] asBytes(String format) {
        try {
            BufferedImage image = this.render();
            if (posterListener != null) {
                image = posterListener.beforeOut(image);
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, format, output);
            return output.toByteArray();
        } catch (Exception e) {
            throw new PosterException(e);
        }
    }
}
