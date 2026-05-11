package com.bytefuture.easy.poster.func.advance;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.model.ContainerAlign;
import com.bytefuture.easy.poster.element.advance.ContainerElement;
import com.bytefuture.easy.poster.model.ContainerLayoutMode;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.LocalAbsolutePosition;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

/**
 * @author Codex
 * @since 2026/05/01
 */
public class ContainerElementFunctionalTest {

    @Test
    public void shouldRenderChildRelativeToContainerContentBox() throws Exception {
        EasyPoster poster = new EasyPoster(240, 220);
        Color background = new Color(245, 245, 245);
        Color childColor = new Color(220, 70, 70);

        ContainerElement container = ContainerElement.of(120, 100)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(40, 50, 0, 0)))
                .setPadding(Margin.of(10))
                .setBackgroundColor(background)
                .addChild(new RectangleElement(20, 20)
                        .setColor(childColor)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)));

        poster.addElement(container);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(poster.asBytes("png")));
        Assert.assertTrue(countColorLikePixels(image, childColor, 5, 50, 60, 20, 20) > 300);
        Assert.assertEquals(0, countColorLikePixels(image, childColor, 5, 40, 50, 8, 8));
        Assert.assertTrue(countColorLikePixels(image, background, 5, 42, 52, 100, 80) > 4000);
    }

    @Test
    public void shouldApplyLocalAbsolutePositionInsideContainer() throws Exception {
        EasyPoster poster = new EasyPoster(260, 220);
        Color childColor = new Color(60, 140, 220);

        ContainerElement container = ContainerElement.of(140, 120)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(50, 40, 0, 0)))
                .setPadding(Margin.of(12))
                .addChild(new RectangleElement(24, 24)
                        .setColor(childColor)
                        .setPosition(LocalAbsolutePosition.of(Point.of(18, 16))));

        poster.addElement(container);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(poster.asBytes("png")));
        Assert.assertTrue(countColorLikePixels(image, childColor, 5, 80, 68, 24, 24) > 450);
        Assert.assertEquals(0, countColorLikePixels(image, childColor, 5, 65, 53, 8, 8));
    }

    @Test
    public void shouldMoveChildrenWhenContainerMoves() throws Exception {
        Color childColor = new Color(90, 180, 100);

        BufferedImage leftImage = renderWithContainerOffset(20, 30, childColor);
        BufferedImage rightImage = renderWithContainerOffset(80, 70, childColor);

        Assert.assertTrue(countColorLikePixels(leftImage, childColor, 5, 34, 44, 16, 16) > 200);
        Assert.assertEquals(0, countColorLikePixels(leftImage, childColor, 5, 94, 84, 16, 16));
        Assert.assertTrue(countColorLikePixels(rightImage, childColor, 5, 94, 84, 16, 16) > 200);
        Assert.assertEquals(0, countColorLikePixels(rightImage, childColor, 5, 34, 44, 16, 16));
    }

    @Test
    public void shouldRenderRoundedBackgroundWithoutPaintingSharpCorners() throws Exception {
        EasyPoster poster = new EasyPoster(240, 220);
        Color background = new Color(235, 240, 250);

        ContainerElement container = ContainerElement.of(120, 100)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(40, 50, 0, 0)))
                .setBackgroundColor(background)
                .setArc(36);

        poster.addElement(container);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(poster.asBytes("png")));
        Assert.assertEquals(Color.WHITE.getRGB(), image.getRGB(40, 50));
        Assert.assertTrue(isColorLike(new Color(image.getRGB(100, 100), true), background, 5));
    }

    @Test
    public void shouldClipChildInsideRoundedContentArea() throws Exception {
        EasyPoster poster = new EasyPoster(260, 240);
        Color background = new Color(240, 244, 252);
        Color childColor = new Color(220, 90, 90);

        ContainerElement container = ContainerElement.of(140, 120)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(50, 60, 0, 0)))
                .setPadding(Margin.of(10))
                .setBackgroundColor(background)
                .setArc(40)
                .setClipContent(true)
                .addChild(new RectangleElement(180, 160)
                        .setColor(childColor)
                        .setPosition(RelativePosition.of(Direction.CENTER)));

        poster.addElement(container);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(poster.asBytes("png")));
        Assert.assertTrue(isColorLike(new Color(image.getRGB(120, 130), true), childColor, 5));
        Assert.assertFalse(isColorLike(new Color(image.getRGB(60, 70), true), childColor, 5));
        Assert.assertTrue(isColorLike(new Color(image.getRGB(60, 70), true), background, 20)
                || image.getRGB(60, 70) == Color.WHITE.getRGB());
    }

    @Test
    public void shouldLayoutChildrenVerticallyAndAutoGrowHeight() throws Exception {
        EasyPoster poster = new EasyPoster(260, 260);
        Color first = new Color(220, 90, 90);
        Color second = new Color(80, 150, 220);
        Color third = new Color(90, 180, 110);

        ContainerElement container = ContainerElement.of(120, 0)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(40, 30, 0, 0)))
                .setPadding(Margin.of(10))
                .setLayoutMode(ContainerLayoutMode.VERTICAL)
                .setGap(8)
                .setAlignItems(ContainerAlign.CENTER)
                .setBackgroundColor(new Color(245, 245, 245))
                .addChild(new RectangleElement(30, 20).setColor(first))
                .addChild(new RectangleElement(40, 24).setColor(second))
                .addChild(new RectangleElement(20, 16).setColor(third));

        poster.addElement(container);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(poster.asBytes("png")));
        Assert.assertTrue(countColorLikePixels(image, first, 5, 84, 40, 30, 20) > 400);
        Assert.assertTrue(countColorLikePixels(image, second, 5, 79, 68, 40, 24) > 700);
        Assert.assertTrue(countColorLikePixels(image, third, 5, 89, 100, 20, 16) > 250);
        Assert.assertTrue(countColorLikePixels(image, Color.WHITE, 5, 40, 150, 120, 10) > 900);
    }

    @Test
    public void shouldLayoutChildrenHorizontallyAndAutoGrowWidth() throws Exception {
        EasyPoster poster = new EasyPoster(320, 220);
        Color first = new Color(220, 90, 90);
        Color second = new Color(80, 150, 220);
        Color third = new Color(90, 180, 110);

        ContainerElement container = ContainerElement.of(0, 100)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(30, 40, 0, 0)))
                .setPadding(Margin.of(10))
                .setLayoutMode(ContainerLayoutMode.HORIZONTAL)
                .setGap(12)
                .setJustifyContent(ContainerAlign.CENTER)
                .setAlignItems(ContainerAlign.END)
                .setBackgroundColor(new Color(245, 245, 245))
                .addChild(new RectangleElement(24, 16).setColor(first))
                .addChild(new RectangleElement(36, 24).setColor(second))
                .addChild(new RectangleElement(20, 20).setColor(third));

        poster.addElement(container);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(poster.asBytes("png")));
        Assert.assertTrue(countColorLikePixels(image, first, 5, 40, 114, 24, 16) > 250);
        Assert.assertTrue(countColorLikePixels(image, second, 5, 76, 106, 36, 24) > 650);
        Assert.assertTrue(countColorLikePixels(image, third, 5, 124, 110, 20, 20) > 350);
        Assert.assertTrue(countColorLikePixels(image, Color.WHITE, 5, 160, 40, 80, 100) > 7000);
    }

    @Test
    public void shouldApplyChildMarginInVerticalFlowLayout() throws Exception {
        EasyPoster poster = new EasyPoster(280, 260);
        Color background = new Color(245, 245, 245);
        Color first = new Color(220, 90, 90);
        Color second = new Color(80, 150, 220);

        ContainerElement container = ContainerElement.of(140, 0)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(40, 30, 0, 0)))
                .setPadding(Margin.of(10))
                .setLayoutMode(ContainerLayoutMode.VERTICAL)
                .setGap(6)
                .setBackgroundColor(background);

        container.addChild(new RectangleElement(30, 20).setColor(first), Margin.of(12, 8, 0, 4));
        container.addChild(new RectangleElement(40, 24).setColor(second), Margin.of(20, 10, 0, 0));
        poster.addElement(container);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(poster.asBytes("png")));
        Assert.assertTrue(countColorLikePixels(image, first, 5, 62, 48, 30, 20) > 400);
        Assert.assertTrue(countColorLikePixels(image, second, 5, 70, 88, 40, 24) > 700);
        Assert.assertTrue(countColorLikePixels(image, background, 5, 50, 48, 10, 20) > 150);
        Assert.assertTrue(countColorLikePixels(image, background, 5, 50, 76, 80, 10) > 500);
    }

    @Test
    public void shouldApplyChildMarginInHorizontalFlowLayout() throws Exception {
        EasyPoster poster = new EasyPoster(320, 220);
        Color background = new Color(245, 245, 245);
        Color first = new Color(220, 90, 90);
        Color second = new Color(80, 150, 220);

        ContainerElement container = ContainerElement.of(0, 120)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(30, 40, 0, 0)))
                .setPadding(Margin.of(10))
                .setLayoutMode(ContainerLayoutMode.HORIZONTAL)
                .setGap(8)
                .setBackgroundColor(background);

        container.addChild(new RectangleElement(24, 24).setColor(first), Margin.of(6, 12, 10, 0));
        container.addChild(new RectangleElement(30, 30).setColor(second), Margin.of(14, 0, 0, 18));
        poster.addElement(container);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(poster.asBytes("png")));
        Assert.assertTrue(countColorLikePixels(image, first, 5, 46, 62, 24, 24) > 500);
        Assert.assertTrue(countColorLikePixels(image, second, 5, 102, 50, 30, 30) > 800);
        Assert.assertTrue(countColorLikePixels(image, background, 5, 40, 62, 6, 24) > 120);
        Assert.assertTrue(countColorLikePixels(image, background, 5, 80, 50, 14, 30) > 300);
    }

    /**
     * 渲染指定偏移下的容器图片
     *
     * @param offsetX 容器横向偏移
     * @param offsetY 容器纵向偏移
     * @param childColor 子元素颜色
     * @return 图片对象
     * @throws Exception 读取异常
     */
    private BufferedImage renderWithContainerOffset(int offsetX, int offsetY, Color childColor) throws Exception {
        EasyPoster poster = new EasyPoster(220, 180);
        ContainerElement container = ContainerElement.of(100, 80)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(offsetX, offsetY, 0, 0)))
                .setPadding(Margin.of(8))
                .addChild(new RectangleElement(16, 16)
                        .setColor(childColor)
                        .setPosition(LocalAbsolutePosition.of(Point.of(6, 6))));
        poster.addElement(container);
        return ImageIO.read(new ByteArrayInputStream(poster.asBytes("png")));
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

    /**
     * 判断颜色是否近似
     *
     * @param current 当前颜色
     * @param target 目标颜色
     * @param tolerance 容差
     * @return 是否近似
     */
    private boolean isColorLike(Color current, Color target, int tolerance) {
        return Math.abs(current.getRed() - target.getRed()) <= tolerance
                && Math.abs(current.getGreen() - target.getGreen()) <= tolerance
                && Math.abs(current.getBlue() - target.getBlue()) <= tolerance;
    }
}
