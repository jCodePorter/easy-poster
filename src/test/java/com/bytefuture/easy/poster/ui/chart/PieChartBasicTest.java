package com.bytefuture.easy.poster.ui.chart;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.chart.PieChartElement;
import com.bytefuture.easy.poster.element.chart.PieChartSlice;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

public class PieChartBasicTest {

    @Test
    public void testPieChartRender() {
        EasyPoster poster = buildBasePoster();
        poster.addPieChartElement(480, 480)
                .setTitle("Channel Mix")
                .setLegendDisplayMode(PieChartElement.DisplayMode.NAME_PERCENT)
                .setLabelDisplayMode(PieChartElement.DisplayMode.PERCENT)
                .addSlice("Channel A", 42, new Color(59, 130, 246))
                .addSlice("Channel B", 28, new Color(16, 185, 129))
                .addSlice("Channel C", 18, new Color(245, 158, 11))
                .addSlice("Other", 12, new Color(168, 85, 247))
                .setPosition(RelativePosition.of(Direction.CENTER));
        poster.asFile("png", "out_pie_basic.png");
    }

    @Test
    public void testDonutChartRender() {
        EasyPoster poster = buildBasePoster();
        poster.addPieChartElement(840, 480)
                .setTitle("Task Status")
                .setMode(PieChartElement.PieChartMode.DONUT)
                .setDonutInnerRadiusRatio(0.60D)
                .setLabelDisplayMode(PieChartElement.DisplayMode.NAME_PERCENT)
                .addSlice("Done", 68, new Color(59, 130, 246))
                .addSlice("In Progress", 22, new Color(245, 158, 11))
                .addSlice("Risk", 10, new Color(220, 38, 38))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_pie_donut.png");
    }

    @Test
    public void testRoseChartRender() throws IOException {
        EasyPoster poster = buildBasePoster();
        poster.addPieChartElement(840, 480)
                .setTitle("Region Heat")
                .setMode(PieChartElement.PieChartMode.ROSE)
                .setRoseInnerRadiusRatio(0.18D)
                .setLabelDisplayMode(PieChartElement.DisplayMode.VALUE)
                .addSlice("East", 88, new Color(59, 130, 246))
                .addSlice("South", 66, new Color(16, 185, 129))
                .addSlice("North", 42, new Color(245, 158, 11))
                .addSlice("West", 25, new Color(220, 38, 38))
                .addSlice("Northeast", 18, new Color(168, 85, 247))
                .setPosition(RelativePosition.of(Direction.CENTER));
        poster.asFile("png", "out_pie_rose.png");
    }

    @Test
    public void testCustomSliceColorShouldOverridePalette() throws IOException {
        Color customColor = new Color(255, 0, 128);
        EasyPoster poster = buildBasePoster();
        poster.addPieChartElement(840, 480)
                .setShowLabel(false)
                .setShowLegend(false)
                .setPalette(Arrays.asList(Color.BLUE, Color.GREEN, Color.ORANGE))
                .addSlice("Custom", 60, customColor)
                .addSlice("Default 1", 25)
                .addSlice("Default 2", 15)
                .setPosition(RelativePosition.of(Direction.CENTER));
        BufferedImage image = renderPoster(poster);

        Assert.assertTrue(countColorLikePixels(image, customColor, 12) > 0);
    }

    @Test
    public void testPaletteFallbackShouldWork() throws IOException {
        Color colorA = new Color(12, 90, 210);
        Color colorB = new Color(28, 180, 120);
        Color colorC = new Color(240, 130, 25);
        EasyPoster poster = buildBasePoster();
        poster.addPieChartElement(840, 480)
                .setShowLabel(false)
                .setShowLegend(true)
                .setPalette(Arrays.asList(colorA, colorB, colorC))
                .addSlice("Slice A", 40)
                .addSlice("Slice B", 35)
                .addSlice("Slice C", 25)
                .setPosition(RelativePosition.of(Direction.CENTER));
        BufferedImage image = renderPoster(poster);

        Assert.assertTrue(countColorLikePixels(image, colorA, 18) > 0);
        Assert.assertTrue(countColorLikePixels(image, colorB, 18) > 0);
        Assert.assertTrue(countColorLikePixels(image, colorC, 18) > 0);
    }

    @Test
    public void testLegendWrapAndLabelAutoHide() throws IOException {
        EasyPoster poster = new EasyPoster(560, 420);
        poster.addPieChartElement(420, 300)
                .setTitle("Compact Legend and Labels")
                .setLegendDisplayMode(PieChartElement.DisplayMode.NAME)
                .setLabelDisplayMode(PieChartElement.DisplayMode.NAME_PERCENT)
                .addSlice("Channel One", 18)
                .addSlice("Channel Two", 16)
                .addSlice("Channel Three", 14)
                .addSlice("Channel Four", 12)
                .addSlice("Channel Five", 10)
                .addSlice("Channel Six", 8)
                .addSlice("Channel Seven", 7)
                .addSlice("Channel Eight", 6)
                .addSlice("Channel Nine", 5)
                .addSlice("Channel Ten", 4)
                .setPosition(RelativePosition.of(Direction.CENTER));
        BufferedImage image = renderPoster(poster);

        Assert.assertNotNull(image);
        Assert.assertTrue(image.getWidth() > 0);
        Assert.assertTrue(image.getHeight() > 0);
    }

    @Test
    public void testSliceNormalizationAndPaletteAccess() {
        PieChartSlice slice = PieChartSlice.of("Normalized", 12);
        PieChartElement element = new PieChartElement(200, 200)
                .setPalette(Arrays.asList(Color.RED, Color.GREEN));

        Assert.assertEquals(12D, slice.getValue(), 0.001D);
        Assert.assertEquals(2, element.getPalette().size());
    }

    @Test
    public void testInvalidDonutRatioShouldThrowException() {
        try {
            EasyPoster poster = buildBasePoster();
            poster.addPieChartElement(840, 480)
                    .setMode(PieChartElement.PieChartMode.DONUT)
                    .setDonutInnerRadiusRatio(1.1D)
                    .addSlice("Slice A", 50)
                    .addSlice("Slice B", 50)
                    .setPosition(RelativePosition.of(Direction.CENTER));
            poster.asBytes("png");
            Assert.fail("Expected PosterException");
        } catch (PosterException ex) {
            Assert.assertNotNull(ex.getCause());
            Assert.assertEquals("donutInnerRadiusRatio must be between 0 and 1", ex.getCause().getMessage());
        }
    }

    @Test
    public void testNonPositiveSlicesShouldThrowException() {
        try {
            EasyPoster poster = buildBasePoster();
            poster.addPieChartElement(840, 480)
                    .addSlice("Zero", 0)
                    .addSlice("Negative", -10)
                    .setPosition(RelativePosition.of(Direction.CENTER));
            poster.asBytes("png");
            Assert.fail("Expected PosterException");
        } catch (PosterException ex) {
            Assert.assertNotNull(ex.getCause());
            Assert.assertEquals("pie chart requires at least one positive slice value", ex.getCause().getMessage());
        }
    }

    private EasyPoster buildBasePoster() {
        return new EasyPoster(960, 640);
    }

    private BufferedImage renderPoster(EasyPoster poster) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(poster.asBytes("png")));
    }

    private int countColorLikePixels(BufferedImage image, Color target, int tolerance) {
        int count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color current = new Color(image.getRGB(x, y), true);
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
