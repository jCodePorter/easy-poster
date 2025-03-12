package com.augrain.easy.canvas;

import com.augrain.easy.canvas.element.IElement;
import com.augrain.easy.canvas.element.basic.ImageElement;
import com.augrain.easy.canvas.element.basic.TextElement;

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
     * 底图
     */
    private BufferedImage baseImg;

    /**
     * 画布宽度
     */
    private final int canvasWidth;

    /**
     * 画布高度
     */
    private final int canvasHeight;

    /**
     * 是否已完成渲染
     */
    private boolean finished;

    /**
     * Canvas构造方法
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

    public void addElement(IElement element) {
        renderedElements.add(element);
    }

    public TextElement addTextElement(String text) {
        TextElement textElement = new TextElement(text);
        renderedElements.add(textElement);
        return textElement;
    }

    public ImageElement addImageElement(BufferedImage input) {
        ImageElement textElement = new ImageElement(input);
        renderedElements.add(textElement);
        return textElement;
    }

    /**
     * 渲染图片，返回图片对象
     */
    public BufferedImage render() throws Exception {
        baseImg = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = baseImg.createGraphics();
        // 抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setColor(Color.white);
        g.fillRect(0, 0, canvasWidth, canvasHeight);

        // 循环绘制各元素
        for (IElement element : renderedElements) {
            element.render(g, canvasWidth, canvasHeight);
        }
        g.dispose();
        this.finished = true;
        return baseImg;
    }

    public void asFile(String format, String filePath) {
        try {
            if (!finished) {
                this.render();
            }
            ImageIO.write(this.baseImg, format, new File(filePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] asBytes(String format) {
        try {
            if (!finished) {
                this.render();
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(this.baseImg, format, output);
            return output.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
