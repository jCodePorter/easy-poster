package com.bytefuture.easy.poster.ui.chart;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.chart.FunnelChartElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class FunnelChartBasicTest {

    @Test
    public void testFunnelChartRender() {
        EasyPoster poster = buildBasePoster();
        poster.addFunnelChartElement(600, 480)
                .setTitle("Sales Funnel")
                .setLegendDisplayMode(FunnelChartElement.DisplayMode.NAME_VALUE)
                .setLabelDisplayMode(FunnelChartElement.DisplayMode.NAME_PERCENT)
                .addData("Leads", 1000, new Color(59, 130, 246))
                .addData("Qualified", 500, new Color(16, 185, 129))
                .addData("Negotiation", 250, new Color(245, 158, 11))
                .addData("Won", 100, new Color(168, 85, 247))
                .setPosition(RelativePosition.of(Direction.CENTER));
        poster.asFile("png", "out_funnel_basic.png");
    }

    @Test
    public void testCustomStageColorShouldOverridePalette() {
        Color customColor = new Color(255, 0, 128);
        EasyPoster poster = buildBasePoster();
        poster.addFunnelChartElement(600, 480)
                .setShowLabel(false)
                .setShowLegend(false)
                .setPalette(Arrays.asList(Color.BLUE, Color.GREEN, Color.ORANGE))
                .addData("Stage A", 100, customColor)
                .addData("Stage B", 50)
                .addData("Stage C", 25)
                .setPosition(RelativePosition.of(Direction.CENTER));
        poster.asFile("png", "out_funnel_custom_color.png");
    }

    @Test
    public void testLabelDisplayModes() {
        EasyPoster poster = buildBasePoster();

        poster.addFunnelChartElement(300, 240)
                .setTitle("Name Only")
                .setLabelDisplayMode(FunnelChartElement.DisplayMode.NAME)
                .addData("Stage 1", 100)
                .addData("Stage 2", 50)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        poster.addFunnelChartElement(300, 240)
                .setTitle("Value Only")
                .setLabelDisplayMode(FunnelChartElement.DisplayMode.VALUE)
                .addData("Stage 1", 100)
                .addData("Stage 2", 50)
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.addFunnelChartElement(300, 240)
                .setTitle("Percent Only")
                .setLabelDisplayMode(FunnelChartElement.DisplayMode.PERCENT)
                .addData("Stage 1", 100)
                .addData("Stage 2", 50)
                .setPosition(RelativePosition.of(Direction.TOP_RIGHT));

        poster.asFile("png", "out_funnel_label_modes.png");
    }

    @Test
    public void testLegendWrap() {
        EasyPoster poster = buildBasePoster();
        poster.addFunnelChartElement(480, 360)
                .setTitle("Legend Wrap")
                .setLegendDisplayMode(FunnelChartElement.DisplayMode.NAME)
                .addData("Very Long Stage Name 1", 100)
                .addData("Very Long Stage Name 2", 80)
                .addData("Very Long Stage Name 3", 60)
                .addData("Very Long Stage Name 4", 40)
                .addData("Very Long Stage Name 5", 20)
                .setPosition(RelativePosition.of(Direction.CENTER));
        poster.asFile("png", "out_funnel_legend_wrap.png");
    }

    @Test
    public void testEqualValueStages() {
        EasyPoster poster = buildBasePoster();
        poster.addFunnelChartElement(600, 480)
                .setTitle("Equal Value Stages")
                .setLabelDisplayMode(FunnelChartElement.DisplayMode.NAME_VALUE)
                .addData("Stage A", 100)
                .addData("Stage B", 100)
                .addData("Stage C", 100)
                .addData("Stage D", 100)
                .setPosition(RelativePosition.of(Direction.CENTER));
        poster.asFile("png", "out_funnel_equal_values.png");
    }

    @Test
    public void testSmallStageLabelOutside() {
        EasyPoster poster = new EasyPoster(800, 600);
        poster.addFunnelChartElement(300, 200)
                .setTitle("Small Stage Labels")
                .setLabelDisplayMode(FunnelChartElement.DisplayMode.NAME_PERCENT)
                .addData("Large Stage", 1000)
                .addData("Small Stage", 1)
                .addData("Tiny Stage", 0.1)
                .setPosition(RelativePosition.of(Direction.CENTER));
        poster.asFile("png", "out_funnel_small_labels.png");
    }

    @Test
    public void testResolveRenderStagesShouldComputePercentAndMaxValue() throws Exception {
        FunnelChartElement chart = new FunnelChartElement(300, 240)
                .addData("Visit", 100)
                .addData("Qualified", 60)
                .addData("Won", 40);

        List<?> renderStages = (List<?>) invokeNoArg(chart, "resolveRenderStages");

        Assert.assertEquals(3, renderStages.size());
        Assert.assertEquals(50D, readDoubleField(renderStages.get(0), "percent"), 0.001D);
        Assert.assertEquals(30D, readDoubleField(renderStages.get(1), "percent"), 0.001D);
        Assert.assertEquals(20D, readDoubleField(renderStages.get(2), "percent"), 0.001D);
        Assert.assertEquals(100D, readDoubleField(renderStages.get(2), "maxValue"), 0.001D);
    }

    @Test
    public void testDrawStageLabelShouldUseExternalLeaderLineWhenStageHeightIsTooSmall() throws Exception {
        FunnelChartElement chart = new FunnelChartElement(300, 240)
                .addData("Tiny", 100, new Color(59, 130, 246));
        List<?> renderStages = (List<?>) invokeNoArg(chart, "resolveRenderStages");
        Object renderStage = renderStages.get(0);

        BufferedImage image = new BufferedImage(220, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        try {
            Method drawStageLabel = chart.getClass().getDeclaredMethod(
                    "drawStageLabel",
                    Graphics2D.class,
                    Font.class,
                    renderStage.getClass(),
                    int.class,
                    int.class,
                    int.class,
                    int.class
            );
            drawStageLabel.setAccessible(true);
            drawStageLabel.invoke(chart, graphics, new Font("Dialog", Font.PLAIN, 12), renderStage, 20, 20, 80, 5);
        } finally {
            graphics.dispose();
        }

        Assert.assertTrue(countColorLikePixels(image, Color.GRAY, 8) > 0);
    }

    private EasyPoster buildBasePoster() {
        return new EasyPoster(960, 640);
    }

    private Object invokeNoArg(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method.invoke(target);
    }

    private double readDoubleField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getDouble(target);
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
