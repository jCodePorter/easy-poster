package com.bytefuture.easy.poster.ui.chart;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.chart.FunnelChartElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import org.junit.Test;

import java.awt.*;
import java.util.Arrays;

public class FunnelChartBasicTest {

    @Test
    public void testFunnelChartRender() {
        EasyPoster poster = buildBasePoster();
        poster.addFunnelChartElement(600, 480)
                .setTitle("Sales Funnel")
                .setLegendDisplayMode(FunnelChartElement.DisplayMode.NAME_VALUE)
                .setLabelDisplayMode(FunnelChartElement.DisplayMode.NAME_PERCENT)
                .addStage("Leads", 1000, new Color(59, 130, 246))
                .addStage("Qualified", 500, new Color(16, 185, 129))
                .addStage("Negotiation", 250, new Color(245, 158, 11))
                .addStage("Won", 100, new Color(168, 85, 247))
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
                .addStage("Stage A", 100, customColor)
                .addStage("Stage B", 50)
                .addStage("Stage C", 25)
                .setPosition(RelativePosition.of(Direction.CENTER));
        poster.asFile("png", "out_funnel_custom_color.png");
    }

    @Test
    public void testLabelDisplayModes() {
        EasyPoster poster = buildBasePoster();

        poster.addFunnelChartElement(300, 240)
                .setTitle("Name Only")
                .setLabelDisplayMode(FunnelChartElement.DisplayMode.NAME)
                .addStage("Stage 1", 100)
                .addStage("Stage 2", 50)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        poster.addFunnelChartElement(300, 240)
                .setTitle("Value Only")
                .setLabelDisplayMode(FunnelChartElement.DisplayMode.VALUE)
                .addStage("Stage 1", 100)
                .addStage("Stage 2", 50)
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.addFunnelChartElement(300, 240)
                .setTitle("Percent Only")
                .setLabelDisplayMode(FunnelChartElement.DisplayMode.PERCENT)
                .addStage("Stage 1", 100)
                .addStage("Stage 2", 50)
                .setPosition(RelativePosition.of(Direction.TOP_RIGHT));

        poster.asFile("png", "out_funnel_label_modes.png");
    }

    @Test
    public void testLegendWrap() {
        EasyPoster poster = buildBasePoster();
        poster.addFunnelChartElement(480, 360)
                .setTitle("Legend Wrap")
                .setLegendDisplayMode(FunnelChartElement.DisplayMode.NAME)
                .addStage("Very Long Stage Name 1", 100)
                .addStage("Very Long Stage Name 2", 80)
                .addStage("Very Long Stage Name 3", 60)
                .addStage("Very Long Stage Name 4", 40)
                .addStage("Very Long Stage Name 5", 20)
                .setPosition(RelativePosition.of(Direction.CENTER));
        poster.asFile("png", "out_funnel_legend_wrap.png");
    }

    @Test
    public void testEqualValueStages() {
        EasyPoster poster = buildBasePoster();
        poster.addFunnelChartElement(600, 480)
                .setTitle("Equal Value Stages")
                .setLabelDisplayMode(FunnelChartElement.DisplayMode.NAME_VALUE)
                .addStage("Stage A", 100)
                .addStage("Stage B", 100)
                .addStage("Stage C", 100)
                .addStage("Stage D", 100)
                .setPosition(RelativePosition.of(Direction.CENTER));
        poster.asFile("png", "out_funnel_equal_values.png");
    }

    @Test
    public void testSmallStageLabelOutside() {
        EasyPoster poster = new EasyPoster(800, 600);
        poster.addFunnelChartElement(300, 200)
                .setTitle("Small Stage Labels")
                .setLabelDisplayMode(FunnelChartElement.DisplayMode.NAME_PERCENT)
                .addStage("Large Stage", 1000)
                .addStage("Small Stage", 1)
                .addStage("Tiny Stage", 0.1)
                .setPosition(RelativePosition.of(Direction.CENTER));
        poster.asFile("png", "out_funnel_small_labels.png");
    }

    private EasyPoster buildBasePoster() {
        return new EasyPoster(960, 640);
    }
}
