package com.augrain.easy.canvas.basic.image;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.geometry.Margin;
import com.augrain.easy.canvas.geometry.Positions;
import com.augrain.easy.canvas.geometry.RelativePosition;
import com.augrain.easy.canvas.utils.ImageUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

/**
 * 图片测试
 *
 * @author biaoy
 * @since 2025/03/05
 */
public class ImageBasicTest {

    @Test
    public void testBasic() throws Exception {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        InputStream inputStream = ImageBasicTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage read = ImageIO.read(inputStream);
        canvas.addImageElement(read);

        BufferedImage combine = canvas.render();
        ImageIO.write(combine, "png", new File("img_basic.png"));
    }

    @Test
    public void testRelativePosition() throws Exception {
        EasyCanvas canvas = new EasyCanvas(600, 600);

        InputStream inputStream = ImageBasicTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage read = ImageIO.read(inputStream);

        for (Positions position : Positions.values()) {
            canvas.addImageElement(read)
                    .setPosition(RelativePosition.of(position, Margin.of(10)));
        }

        BufferedImage combine = canvas.render();
        ImageIO.write(combine, "png", new File("img_position.png"));
    }

    @Test
    public void testRotate() throws Exception {
        InputStream inputStream = ImageBasicTest.class.getClassLoader().getResourceAsStream("lotus.png");
        BufferedImage read = ImageIO.read(inputStream);

        BufferedImage rotate = ImageUtils.rotate(read, -45);
        ImageIO.write(rotate, "png", new File("img_rotate.png"));
    }

}
