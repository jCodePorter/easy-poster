package com.augrain.easy.poster.basic.image;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.element.advance.RepeatElement;
import com.augrain.easy.poster.element.basic.ImageElement;
import com.augrain.easy.poster.model.Scale;
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
    public void testRepeat() throws Exception {
        EasyPoster poster = new EasyPoster(500, 500);

        InputStream inputStream = ImageBasicTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage input = ImageIO.read(inputStream);

        RepeatElement repeatElement = new RepeatElement(
                new ImageElement(input)
                        .crop("1:1")
                        .scale(Scale.byWidth(80))
                        .roundCorner(80)
                        // .rotate(-45)
                        // .setAlpha(0.5f)
        )
                //.setPadding(20, 20);
                .setLayout(4,4);

        poster.addElement(repeatElement);
        poster.asFile("png", "out_image_tile.png");
    }
}
