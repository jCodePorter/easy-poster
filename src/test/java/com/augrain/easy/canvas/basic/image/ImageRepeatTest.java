package com.augrain.easy.canvas.basic.image;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.element.advance.RepeatElement;
import com.augrain.easy.canvas.element.basic.ImageElement;
import com.augrain.easy.canvas.enums.ZoomMode;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * 矩形基本测试
 *
 * @author biaoy
 * @since 2025/03/16
 */
public class ImageRepeatTest {

    @Test
    public void testBasicImage() throws Exception {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        InputStream inputStream = ImageBasicTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage input = ImageIO.read(inputStream);

        RepeatElement repeatElement = new RepeatElement(
                new ImageElement(input)
                        .scale(50, 50, ZoomMode.WIDTH_HEIGHT)
                        .rotate(-45)
        ).setPadding(20, 20);

        canvas.addElement(repeatElement);
        canvas.asFile("png", "image_tile.png");
    }
}
