package com.bytefuture.easy.poster.func.advance;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.advance.ContainerElement;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.ClearType;
import com.bytefuture.easy.poster.model.FloatType;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

/**
 * 容器浮动布局功能测试
 *
 * @author biaoy
 * @since 2026/05/14
 */
public class ContainerFloatLayoutFunctionalTest {

    @Test
    public void shouldRenderLeftFloatElements() throws Exception {
        EasyPoster poster = new EasyPoster(400, 300);
        Color containerBg = new Color(245, 245, 245);
        Color child1Color = new Color(230, 120, 120);
        Color child2Color = new Color(100, 170, 230);
        Color child3Color = new Color(120, 190, 120);

        ContainerElement container = ContainerElement.of(360, 260)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setBackgroundColor(containerBg)
                .setBorderSize(0);

        container.addChild(new RectangleElement(80, 60).setColor(child1Color).setFloatType(FloatType.LEFT));
        container.addChild(new RectangleElement(100, 70).setColor(child2Color).setFloatType(FloatType.LEFT));
        container.addChild(new RectangleElement(120, 50).setColor(child3Color).setClearType(ClearType.LEFT));

        poster.addElement(container);
        byte[] bytes = poster.asBytes("png");

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        int containerCenterX = 200;
        int containerCenterY = 150;

        // 验证第一个元素在左侧
        int child1X = containerCenterX - 160 + 20; // container start x + padding
        int child1Y = containerCenterY - 110 + 20;
        Color pixel1 = new Color(image.getRGB(child1X + 40, child1Y + 30));
        Assert.assertTrue("Child 1 should be red", pixel1.getRed() > 200);

        // 验证第二个元素在第一个元素右侧
        int child2X = child1X + 80;
        int child2Y = child1Y;
        Color pixel2 = new Color(image.getRGB(child2X + 50, child2Y + 35));
        Assert.assertTrue("Child 2 should be blue", pixel2.getBlue() > 150);

        // 验证第三个元素在下一行
        int child3X = containerCenterX - 160 + 20;
        int child3Y = child1Y + 70 + 20; // 第一行高度 + gap
        Color pixel3 = new Color(image.getRGB(child3X + 60, child3Y + 25));
        Assert.assertTrue("Child 3 should be green", pixel3.getGreen() > 150);
    }

    @Test
    public void shouldRenderRightFloatElements() throws Exception {
        EasyPoster poster = new EasyPoster(400, 300);
        Color child1Color = new Color(230, 120, 120);
        Color child2Color = new Color(100, 170, 230);

        ContainerElement container = ContainerElement.of(360, 260)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setBackgroundColor(Color.WHITE)
                .setBorderSize(0);

        container.addChild(new RectangleElement(80, 60).setColor(child1Color).setFloatType(FloatType.RIGHT));
        container.addChild(new RectangleElement(100, 70).setColor(child2Color).setFloatType(FloatType.RIGHT));

        poster.addElement(container);
        byte[] bytes = poster.asBytes("png");

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        int containerCenterX = 200;
        int containerCenterY = 150;

        // 验证第一个元素在右侧
        int child1X = containerCenterX + 160 - 20 - 80;
        int child1Y = containerCenterY - 110 + 20;
        Color pixel1 = new Color(image.getRGB(child1X + 40, child1Y + 30));
        Assert.assertTrue("Child 1 should be red", pixel1.getRed() > 200);
    }

    @Test
    public void shouldClearLeftFloat() throws Exception {
        EasyPoster poster = new EasyPoster(400, 300);
        Color child1Color = new Color(230, 120, 120);
        Color child3Color = new Color(120, 190, 120);

        ContainerElement container = ContainerElement.of(360, 260)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setBackgroundColor(Color.WHITE)
                .setBorderSize(0);

        container.addChild(new RectangleElement(80, 60).setColor(child1Color).setFloatType(FloatType.LEFT));
        container.addChild(new RectangleElement(120, 50).setColor(child3Color).setClearType(ClearType.LEFT));

        poster.addElement(container);
        byte[] bytes = poster.asBytes("png");

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        int containerCenterX = 200;
        int containerCenterY = 150;

        // 验证第三个元素在下一行
        int child3Y = containerCenterY - 110 + 20 + 60 + 10; // 第一行高度 + gap
        int child3X = containerCenterX - 160 + 20;
        Color pixel3 = new Color(image.getRGB(child3X + 60, child3Y + 25));
        Assert.assertTrue("Child 3 should be green and below child 1", pixel3.getGreen() > 150);
        Assert.assertTrue("Child 3 Y should be below child 1", child3Y > containerCenterY - 110 + 20);
    }
}
