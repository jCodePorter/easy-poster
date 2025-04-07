package com.augrain.easy.canvas;

import com.augrain.easy.canvas.element.IElement;
import com.augrain.easy.canvas.element.basic.*;
import com.augrain.easy.canvas.exception.CanvasException;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.model.CanvasContext;
import com.augrain.easy.canvas.model.CanvasListener;
import com.augrain.easy.canvas.model.Config;
import com.sun.corba.se.impl.orbutil.concurrent.CondVar;
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
public class EasyCanvas {
    /**
     * 待绘制的元素集合
     */
    private final List<IElement> renderedElements = new ArrayList<>();

    /**
     * 画布宽度
     */
    private final int canvasWidth;

    /**
     * 画布高度
     */
    private final int canvasHeight;

    /**
     * canvas监听
     */
    @Setter
    private CanvasListener canvasListener;

    @Getter
    private final Config config = new Config();

    /**
     * Canvas构造方法
     *
     * @param canvasWidth  画布宽
     * @param canvasHeight 画布高
     */
    public EasyCanvas(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    /**
     * Canvas构造方法
     *
     * @param backgroundImg 背景图
     */
    public EasyCanvas(BufferedImage backgroundImg) {
        this.canvasWidth = backgroundImg.getWidth();
        this.canvasHeight = backgroundImg.getHeight();
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

    public LineElement addLineElement(CoordinatePoint start, CoordinatePoint end) {
        LineElement lineElement = new LineElement(start, end);
        renderedElements.add(lineElement);
        return lineElement;
    }

    /**
     * 渲染图片，返回图片对象
     */
    private BufferedImage render() throws Exception {
        BufferedImage baseImg = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = baseImg.createGraphics();
        // 抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setColor(Color.white);
        g.fillRect(0, 0, canvasWidth, canvasHeight);

        // 创建CanvasContext
        CanvasContext canvasContext = buildCanvasContext(g);

        // 循环绘制各元素
        for (IElement element : renderedElements) {
            element.render(canvasContext, canvasWidth, canvasHeight);
        }
        g.dispose();
        return baseImg;
    }

    private CanvasContext buildCanvasContext(Graphics2D g) {
        CanvasContext canvasContext = new CanvasContext();
        canvasContext.setConfig(config);
        canvasContext.setEasyCanvas(this);
        canvasContext.setGraphics(g);
        return canvasContext;
    }

    public void asFile(String format, String filePath) {
        try {
            BufferedImage image = this.render();
            if (canvasListener != null) {
                image = canvasListener.beforeOut(image);
            }
            ImageIO.write(image, format, new File(filePath));
        } catch (Exception e) {
            throw new CanvasException(e);
        }
    }

    public byte[] asBytes(String format) {
        try {
            BufferedImage image = this.render();
            if (canvasListener != null) {
                image = canvasListener.beforeOut(image);
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, format, output);
            return output.toByteArray();
        } catch (Exception e) {
            throw new CanvasException(e);
        }
    }
}
