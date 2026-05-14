package com.bytefuture.easy.poster.func.advance;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.advance.ComposeElement;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

/**
 * @author biaoy
 * @since 2026/04/29
 */
public class ComposeElementFunctionalTest {

    @Test
    public void shouldApplyComposeElementOuterPosition() throws Exception {
        EasyPoster poster = new EasyPoster(320, 320);
        Color targetColor = new Color(220, 80, 80);

        ComposeElement composeElement = ComposeElement.of(
                        new RectangleElement(80, 80)
                                .setColor(targetColor)
                                .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(120, 140, 0, 0)));

        poster.addElement(composeElement);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(poster.asBytes("png")));
        Assert.assertTrue(countColorLikePixels(image, targetColor, 10, 120, 140, 90, 90) > 2000);
        Assert.assertEquals(0, countColorLikePixels(image, targetColor, 10, 0, 0, 100, 100));
    }

    /**
     * 统计目标区域内的近似颜色像素
     *
     * @param image 图片
     * @param target 目标颜色
     * @param tolerance 容差
     * @param startX 起始 X
     * @param startY 起始 Y
     * @param width 区域宽度
     * @param height 区域高度
     * @return 像素数量
     */
    private int countColorLikePixels(BufferedImage image, Color target, int tolerance,
                                     int startX, int startY, int width, int height) {
        int count = 0;
        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                Color current = new Color(image.getRGB(x, y), true);
                if (current.getAlpha() == 0) {
                    continue;
                }
                if (Math.abs(current.getRed() - target.getRed()) <= tolerance
                        && Math.abs(current.getGreen() - target.getGreen()) <= tolerance
                        && Math.abs(current.getBlue() - target.getBlue()) <= tolerance) {
                    count++;
                }
            }
        }
        return count;
    }
}
