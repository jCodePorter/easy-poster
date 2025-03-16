package com.augrain.easy.canvas.image;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.element.advance.TileElement;
import com.augrain.easy.canvas.element.basic.ImageElement;
import com.augrain.easy.canvas.element.basic.RectangleElement;
import com.augrain.easy.canvas.geometry.Margin;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * 矩形基本测试
 *
 * @author biaoy
 * @since 2025/03/16
 */
public class ImageTileTest {

    @Test
    public void testBasicImage() throws Exception {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        InputStream inputStream = ImageBasicTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage input = ImageIO.read(inputStream);

        TileElement tileElement = new TileElement(new ImageElement(input)).setPadding(20, 20);

        canvas.addElement(tileElement);
        canvas.asFile("png", "image_tile.png");
    }
}
