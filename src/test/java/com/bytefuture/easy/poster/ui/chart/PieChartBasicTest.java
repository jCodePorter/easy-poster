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

/**
 * 饼图元素基础测试。
 */
public class PieChartBasicTest {

    /**
     * 验证普通饼图可以通过 EasyPoster 入口正常渲染。
     */
    @Test
    public void testPieChartRender() {
        EasyPoster poster = buildBasePoster();
        poster.addPieChartElement(480, 480)
                .setTitle("渠道占比分布")
                .setLegendDisplayMode(PieChartElement.DisplayMode.NAME_PERCENT)
                .setLabelDisplayMode(PieChartElement.DisplayMode.PERCENT)
                .addSlice("渠道A", 42, new Color(59, 130, 246))
                .addSlice("渠道B", 28, new Color(16, 185, 129))
                .addSlice("渠道C", 18, new Color(245, 158, 11))
                .addSlice("其他", 12, new Color(168, 85, 247))
                .setPosition(RelativePosition.of(Direction.CENTER));
        poster.asFile("png", "out_pie_basic.png");
    }

    /**
     * 验证环形图模式会保留中心留白。
     */
    @Test
    public void testDonutChartRender() {
        EasyPoster poster = buildBasePoster();
        poster.addPieChartElement(840, 480)
                .setTitle("任务状态")
                .setMode(PieChartElement.PieChartMode.DONUT)
                .setDonutInnerRadiusRatio(0.60D)
                .setLabelDisplayMode(PieChartElement.DisplayMode.NAME_PERCENT)
                .addSlice("已完成", 68, new Color(59, 130, 246))
                .addSlice("进行中", 22, new Color(245, 158, 11))
                .addSlice("风险", 10, new Color(220, 38, 38))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_pie_donut.png");
    }

    /**
     * 验证玫瑰图模式可以正常渲染。
     *
     * @throws IOException 图像读取异常
     */
    @Test
    public void testRoseChartRender() throws IOException {
        EasyPoster poster = buildBasePoster();
        poster.addPieChartElement(840, 480)
                .setTitle("区域热度")
                .setMode(PieChartElement.PieChartMode.ROSE)
                .setRoseInnerRadiusRatio(0.18D)
                .setLabelDisplayMode(PieChartElement.DisplayMode.VALUE)
                .addSlice("华东", 88, new Color(59, 130, 246))
                .addSlice("华南", 66, new Color(16, 185, 129))
                .addSlice("华北", 42, new Color(245, 158, 11))
                .addSlice("西南", 25, new Color(220, 38, 38))
                .addSlice("东北", 18, new Color(168, 85, 247))
                .setPosition(RelativePosition.of(Direction.CENTER));
        poster.asFile("png", "out_pie_rose.png");
    }

    /**
     * 验证自定义颜色优先于默认调色板。
     *
     * @throws IOException 图像读取异常
     */
    @Test
    public void testCustomSliceColorShouldOverridePalette() throws IOException {
        Color customColor = new Color(255, 0, 128);
        EasyPoster poster = buildBasePoster();
        poster.addPieChartElement(840, 480)
                .setShowLabel(false)
                .setShowLegend(false)
                .setPalette(Arrays.asList(Color.BLUE, Color.GREEN, Color.ORANGE))
                .addSlice("自定义", 60, customColor)
                .addSlice("默认1", 25)
                .addSlice("默认2", 15)
                .setPosition(RelativePosition.of(Direction.CENTER));
        BufferedImage image = renderPoster(poster);

        Assert.assertTrue(countColorLikePixels(image, customColor, 12) > 0);
    }

    /**
     * 验证未指定颜色时会按配置调色板回退。
     *
     * @throws IOException 图像读取异常
     */
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
                .addSlice("切片A", 40)
                .addSlice("切片B", 35)
                .addSlice("切片C", 25)
                .setPosition(RelativePosition.of(Direction.CENTER));
        BufferedImage image = renderPoster(poster);

        Assert.assertTrue(countColorLikePixels(image, colorA, 18) > 0);
        Assert.assertTrue(countColorLikePixels(image, colorB, 18) > 0);
        Assert.assertTrue(countColorLikePixels(image, colorC, 18) > 0);
    }

    /**
     * 验证图例换行和标签自动隐藏路径不会导致渲染失败。
     *
     * @throws IOException 图像读取异常
     */
    @Test
    public void testLegendWrapAndLabelAutoHide() throws IOException {
        EasyPoster poster = new EasyPoster(560, 420);
        poster.addPieChartElement(420, 300)
                .setTitle("小空间图例与标签")
                .setLegendDisplayMode(PieChartElement.DisplayMode.NAME)
                .setLabelDisplayMode(PieChartElement.DisplayMode.NAME_PERCENT)
                .addSlice("渠道一渠道一", 18)
                .addSlice("渠道二渠道二", 16)
                .addSlice("渠道三渠道三", 14)
                .addSlice("渠道四渠道四", 12)
                .addSlice("渠道五渠道五", 10)
                .addSlice("渠道六渠道六", 8)
                .addSlice("渠道七渠道七", 7)
                .addSlice("渠道八渠道八", 6)
                .addSlice("渠道九渠道九", 5)
                .addSlice("渠道十渠道十", 4)
                .setPosition(RelativePosition.of(Direction.CENTER));
        BufferedImage image = renderPoster(poster);

        Assert.assertNotNull(image);
        Assert.assertTrue(image.getWidth() > 0);
        Assert.assertTrue(image.getHeight() > 0);
    }

    /**
     * 验证切片数据会标准化为 double 并暴露当前调色板。
     */
    @Test
    public void testSliceNormalizationAndPaletteAccess() {
        PieChartSlice slice = PieChartSlice.of("标准化", 12);
        PieChartElement element = new PieChartElement(200, 200)
                .setPalette(Arrays.asList(Color.RED, Color.GREEN));

        Assert.assertEquals(12D, slice.getValue(), 0.001D);
        Assert.assertEquals(2, element.getPalette().size());
    }

    /**
     * 验证非法环形图比例会抛出异常。
     */
    @Test
    public void testInvalidDonutRatioShouldThrowException() {
        try {
            EasyPoster poster = buildBasePoster();
            poster.addPieChartElement(840, 480)
                    .setMode(PieChartElement.PieChartMode.DONUT)
                    .setDonutInnerRadiusRatio(1.1D)
                    .addSlice("切片A", 50)
                    .addSlice("切片B", 50)
                    .setPosition(RelativePosition.of(Direction.CENTER));
            poster.asBytes("png");
            Assert.fail("Expected PosterException");
        } catch (PosterException ex) {
            Assert.assertNotNull(ex.getCause());
            Assert.assertEquals("donutInnerRadiusRatio must be between 0 and 1", ex.getCause().getMessage());
        }
    }

    /**
     * 验证没有正数切片时会抛出异常。
     */
    @Test
    public void testNonPositiveSlicesShouldThrowException() {
        try {
            EasyPoster poster = buildBasePoster();
            poster.addPieChartElement(840, 480)
                    .addSlice("零值", 0)
                    .addSlice("负值", -10)
                    .setPosition(RelativePosition.of(Direction.CENTER));
            poster.asBytes("png");
            Assert.fail("Expected PosterException");
        } catch (PosterException ex) {
            Assert.assertNotNull(ex.getCause());
            Assert.assertEquals("pie chart requires at least one positive slice value", ex.getCause().getMessage());
        }
    }

    /**
     * 构建基础海报对象。
     *
     * @return 海报对象
     */
    private EasyPoster buildBasePoster() {
        return new EasyPoster(960, 640);
    }

    /**
     * 渲染海报图像。
     */
    private BufferedImage renderPoster(EasyPoster poster) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(poster.asBytes("png")));
    }

    /**
     * 统计近似颜色像素数量。
     *
     * @param image     输出图像
     * @param target    目标颜色
     * @param tolerance 容差
     * @return 近似颜色像素数量
     */
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
