package com.bytefuture.easy.poster.ui.chart;

import com.bytefuture.easy.poster.EasyPoster;
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

/**
 * 折线图基础测试
 */
public class LineChartBasicTest {

    @Test
    public void testLineChartRender() {
        EasyPoster poster = buildBasePoster();
        poster.addLineChartElement(840, 480)
                .setTitle("季度趋势")
                .setBackgroundColor(new Color(248, 250, 252))
                .setCategories(Arrays.asList("第一季度", "第二季度", "第三季度", "第四季度"))
                .addSeries("营收", Arrays.asList(128, 156, 174, 203), new Color(59, 130, 246))
                .addSeries("利润", Arrays.asList(52, 68, 80, 94), new Color(16, 185, 129))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_line_chart_basic.png");
    }

    @Test
    public void testLineChartWithNegativeValue() {
        EasyPoster poster = buildBasePoster();
        poster.addLineChartElement(840, 480)
                .setTitle("月度波动")
                .setBackgroundColor(new Color(250, 250, 252))
                .setCategories(Arrays.asList("1月", "2月", "3月", "4月", "5月"))
                .addSeries("净利润", Arrays.asList(18, -12, 26, 10, -5), new Color(37, 99, 235))
                .addSeries("现金流", Arrays.asList(8, -20, 16, 4, 11), new Color(220, 38, 38))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_line_chart_negative.png");
    }

    @Test
    public void testSmoothLineChartRender() {
        EasyPoster poster = buildBasePoster();
        poster.addLineChartElement(840, 480)
                .setTitle("平滑趋势")
                .setSmoothTension(0.65D)
                .setBackgroundColor(new Color(248, 250, 252))
                .setCategories(Arrays.asList("第一季度", "第二季度", "第三季度", "第四季度"))
                .addSeries("营收", Arrays.asList(128, 156, 120, 203), new Color(59, 130, 246))
                .addSeries("利润", Arrays.asList(52, 68, 44, 94), new Color(16, 185, 129))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_line_chart_smooth.png");
    }

    @Test
    public void testBezierSmoothLineChartRender() {
        EasyPoster poster = buildBasePoster();
        poster.addLineChartElement(840, 480)
                .setTitle("贝塞尔平滑趋势")
                .setSmoothAlgorithm(com.bytefuture.easy.poster.element.chart.LineChartElement.SmoothAlgorithm.BEZIER)
                .setSmoothTension(0.65D)
                .setBackgroundColor(new Color(248, 250, 252))
                .setCategories(Arrays.asList("第一季度", "第二季度", "第三季度", "第四季度"))
                .addSeries("营收", Arrays.asList(128, 156, 120, 203), new Color(59, 130, 246))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_line_chart_bezier_smooth.png");
    }

    @Test
    public void testMonotoneSmoothLineChartRender() {
        EasyPoster poster = buildBasePoster();
        poster.addLineChartElement(840, 480)
                .setTitle("单调平滑趋势")
                .setSmoothAlgorithm(com.bytefuture.easy.poster.element.chart.LineChartElement.SmoothAlgorithm.MONOTONE)
                .setSmoothTension(0.65D)
                .setBackgroundColor(new Color(248, 250, 252))
                .setCategories(Arrays.asList("第一季度", "第二季度", "第三季度", "第四季度"))
                .addSeries("营收", Arrays.asList(128, 156, 120, 203), new Color(59, 130, 246))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_line_chart_monotone_smooth.png");
    }

    @Test
    public void testZeroSmoothTensionShouldFallbackToStraightLine() {
        EasyPoster poster = buildBasePoster();
        poster.addLineChartElement(840, 480)
                .setTitle("零张力仍为直线")
                .setSmoothAlgorithm(com.bytefuture.easy.poster.element.chart.LineChartElement.SmoothAlgorithm.MONOTONE)
                .setSmoothTension(0D)
                .setBackgroundColor(new Color(248, 250, 252))
                .setCategories(Arrays.asList("第一季度", "第二季度", "第三季度", "第四季度"))
                .addSeries("营收", Arrays.asList(128, 156, 120, 203), new Color(59, 130, 246))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_line_chart_zero_tension.png");
    }

    @Test
    public void testCustomSeriesColorShouldBeUsed() throws IOException {
        Color lineColor = new Color(255, 0, 128);
        EasyPoster poster = buildBasePoster();
        poster.addLineChartElement(840, 480)
                .setShowLegend(false)
                .setShowValueLabel(false)
                .setCategories(Arrays.asList("一", "二", "三", "四"))
                .addSeries("自定义", Arrays.asList(20, 60, 40, 80), lineColor)
                .setPosition(RelativePosition.of(Direction.CENTER));
        BufferedImage image = renderPoster(poster);

        Assert.assertTrue(countColorLikePixels(image, lineColor, 16) > 0);
    }

    @Test
    public void testCategoryAndValueCountMismatchShouldThrowException() {
        try {
            EasyPoster poster = buildBasePoster();
            poster.addLineChartElement(840, 480)
                    .setCategories(Arrays.asList("一", "二", "三"))
                    .addSeries("异常数据", Arrays.asList(10, 20))
                    .setPosition(RelativePosition.of(Direction.CENTER));
            poster.asBytes("png");
            Assert.fail("Expected PosterException");
        } catch (PosterException ex) {
            Assert.assertNotNull(ex.getCause());
            Assert.assertEquals("series value size must match category size", ex.getCause().getMessage());
        }
    }

    @Test
    public void testInvalidSmoothTensionShouldThrowException() {
        try {
            buildBasePoster().addLineChartElement(840, 480)
                    .setSmoothTension(1.2D);
            Assert.fail("Expected PosterException");
        } catch (PosterException ex) {
            Assert.assertEquals("smoothTension must be between 0 and 1", ex.getMessage());
        }
    }

    @Test
    public void testNullSmoothAlgorithmShouldThrowException() {
        try {
            buildBasePoster().addLineChartElement(840, 480)
                    .setSmoothAlgorithm(null);
            Assert.fail("Expected PosterException");
        } catch (PosterException ex) {
            Assert.assertEquals("smoothAlgorithm can not be null", ex.getMessage());
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
