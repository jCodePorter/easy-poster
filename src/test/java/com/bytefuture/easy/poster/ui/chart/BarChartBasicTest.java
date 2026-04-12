package com.bytefuture.easy.poster.ui.chart;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.chart.BarChartElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import org.junit.Test;

import java.awt.*;
import java.util.Arrays;

/**
 * 柱状图基础测试
 */
public class BarChartBasicTest {

    @Test
    public void testGroupedBarChart() {
        EasyPoster poster = new EasyPoster(960, 640);

        poster.addBarChartElement(840, 480)
                .setTitle("门店季度营收")
                .setBackgroundColor(new Color(248, 250, 252))
                .setAxisColor(new Color(82, 89, 104))
                .setGridColor(new Color(225, 230, 238))
                .setLabelColor(new Color(70, 77, 92))
                .setValueLabelColor(new Color(45, 52, 64))
                .setCategories(Arrays.asList("第一季度", "第二季度", "第三季度", "第四季度"))
                .addSeries("直营店", Arrays.asList(128, 156, 174, 203), new Color(59, 130, 246))
                .addSeries("加盟店", Arrays.asList(96, 118, 149, 168), new Color(16, 185, 129))
                .addSeries("线上", Arrays.asList(72, 88, 133, 160), new Color(245, 158, 11))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_bar_chart_basic.png");
    }

    @Test
    public void testBarChartWithNegativeValue() {
        EasyPoster poster = new EasyPoster(960, 640);

        poster.addBarChartElement(840, 480)
                .setTitle("项目盈亏对比")
                .setBackgroundColor(new Color(250, 250, 252))
                .setShowLegend(true)
                .setShowGrid(true)
                .setCategories(Arrays.asList("1月", "2月", "3月", "4月", "5月"))
                .addSeries("净利润", Arrays.asList(38, -12, 56, 22, -8), new Color(37, 99, 235))
                .addSeries("现金流", Arrays.asList(18, -24, 42, 10, 15), new Color(220, 38, 38))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_bar_chart_negative.png");
    }

    @Test
    public void testStackedBarChart() {
        EasyPoster poster = new EasyPoster(960, 640);

        poster.addBarChartElement(840, 480)
                .setTitle("渠道销售构成")
                .setStacked(true)
                .setStackLabelMode(BarChartElement.StackLabelMode.VALUE_PERCENT)
                .setShowSmallStackLabelOutside(true)
                .setBackgroundColor(new Color(248, 250, 252))
                .setCategories(Arrays.asList("华东", "华南", "华北", "西南"))
                .addSeries("私域", Arrays.asList(68, 82, 74, 66), new Color(59, 130, 246))
                .addSeries("电商", Arrays.asList(92, 105, 96, 88), new Color(16, 185, 129))
                .addSeries("分销", Arrays.asList(44, 38, 41, 49), new Color(245, 158, 11))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_bar_chart_stacked.png");
    }

    @Test
    public void testStackedBarChartWithNegativeValue() {
        EasyPoster poster = new EasyPoster(960, 640);

        poster.addBarChartElement(840, 480)
                .setTitle("月度收入与扣减")
                .setStacked(true)
                .setStackLabelMode(BarChartElement.StackLabelMode.VALUE_PERCENT)
                .setShowSmallStackLabelOutside(true)
                .setBackgroundColor(new Color(250, 250, 252))
                .setCategories(Arrays.asList("1月", "2月", "3月", "4月"))
                .addSeries("主营收入", Arrays.asList(80, 92, 88, 96), new Color(37, 99, 235))
                .addSeries("活动补贴", Arrays.asList(18, 10, 16, 12), new Color(5, 150, 105))
                .addSeries("退款", Arrays.asList(-12, -18, -9, -14), new Color(220, 38, 38))
                .addSeries("损耗", Arrays.asList(-6, -5, -7, -4), new Color(249, 115, 22))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_bar_chart_stacked_negative.png");
    }

    @Test
    public void testPercentStackedBarChart() {
        EasyPoster poster = new EasyPoster(960, 640);

        poster.addBarChartElement(840, 480)
                .setTitle("渠道占比分析")
                .setPercentStacked(true)
                .setStackLabelMode(BarChartElement.StackLabelMode.PERCENT)
                .setShowSmallStackLabelOutside(true)
                .setBackgroundColor(new Color(248, 250, 252))
                .setCategories(Arrays.asList("新品", "活动", "复购", "会员"))
                .addSeries("App", Arrays.asList(42, 38, 49, 55), new Color(59, 130, 246))
                .addSeries("小程序", Arrays.asList(28, 34, 26, 22), new Color(16, 185, 129))
                .addSeries("门店", Arrays.asList(18, 16, 14, 13), new Color(245, 158, 11))
                .addSeries("其他", Arrays.asList(12, 12, 11, 10), new Color(168, 85, 247))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_bar_chart_percent_stacked.png");
    }

    @Test
    public void testPercentStackedBarChartWithNegativeValue() {
        EasyPoster poster = new EasyPoster(960, 640);

        poster.addBarChartElement(840, 480)
                .setTitle("收入与扣减占比")
                .setPercentStacked(true)
                .setStackLabelMode(BarChartElement.StackLabelMode.VALUE_PERCENT)
                .setShowSmallStackLabelOutside(true)
                .setBackgroundColor(new Color(250, 250, 252))
                .setCategories(Arrays.asList("1月", "2月", "3月", "4月"))
                .addSeries("主营收入", Arrays.asList(80, 92, 88, 96), new Color(37, 99, 235))
                .addSeries("补贴", Arrays.asList(20, 8, 12, 4), new Color(5, 150, 105))
                .addSeries("退款", Arrays.asList(-12, -18, -9, -14), new Color(220, 38, 38))
                .addSeries("损耗", Arrays.asList(-8, -4, -6, -6), new Color(249, 115, 22))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_bar_chart_percent_stacked_negative.png");
    }
}
