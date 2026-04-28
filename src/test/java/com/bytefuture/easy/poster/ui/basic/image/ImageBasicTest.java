package com.bytefuture.easy.poster.ui.basic.image;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.basic.ImageElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.utils.ImageUtils;
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
        EasyPoster poster = new EasyPoster(500, 500);

        InputStream inputStream = ImageBasicTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage read = ImageIO.read(inputStream);
        poster.addImageElement(read);
        poster.asFile("png", "out_img_basic.png");
    }

    @Test
    public void testRelativePosition() throws Exception {
        EasyPoster poster = new EasyPoster(600, 600);

        InputStream inputStream = ImageBasicTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage read = ImageIO.read(inputStream);

        for (Direction position : Direction.values()) {
            ImageElement imageElement = new ImageElement(read, 100, 100);
            imageElement.setPosition(RelativePosition.of(position, Margin.of(10)));
            poster.addElement(imageElement);
        }
        poster.asFile("png", "out_img_position.png");
    }

    @Test
    public void testRotate() throws Exception {
        InputStream inputStream = ImageBasicTest.class.getClassLoader().getResourceAsStream("lotus.png");
        BufferedImage read = ImageIO.read(inputStream);

        BufferedImage rotate = ImageUtils.rotate(read, -45);
        ImageIO.write(rotate, "png", new File("out_img_rotate.png"));
    }

    @Test
    public void testRoundCorner() throws Exception {
        InputStream inputStream = ImageBasicTest.class.getClassLoader().getResourceAsStream("lotus.png");
        BufferedImage read = ImageIO.read(inputStream);

        BufferedImage roundedCorner = ImageUtils.roundedCorner(read, 1000);
        ImageIO.write(roundedCorner, "png", new File("out_img_round_corner.png"));
    }

}
