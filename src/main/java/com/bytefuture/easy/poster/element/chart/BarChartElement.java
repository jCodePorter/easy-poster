package com.bytefuture.easy.poster.element.chart;

import com.bytefuture.easy.poster.element.AbstractDimensionElement;
import com.bytefuture.easy.poster.element.chart.bar.BarChartLabelRenderer;
import com.bytefuture.easy.poster.element.chart.bar.BarChartLayoutCalculator;
import com.bytefuture.easy.poster.element.chart.bar.BarChartRangeResolver;
import com.bytefuture.easy.poster.element.chart.base.ChartSeries;
import com.bytefuture.easy.poster.element.chart.base.ChartValueRange;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 柱状图元素，支持分组、堆叠和百分比堆叠三种绘制模式。
 */
public class BarChartElement extends AbstractDimensionElement<BarChartElement> {

    /**
     * 分组/堆叠布局计算器，负责统一柱宽和间距。
     */
    private static final BarChartLayoutCalculator LAYOUT_CALCULATOR = new BarChartLayoutCalculator();

    /**
     * 数值标签绘制器，负责柱内外标签布局。
     */
    private static final BarChartLabelRenderer LABEL_RENDERER = new BarChartLabelRenderer();

    /**
     * 数值范围解析器，负责推导 Y 轴上下界。
     */
    private static final BarChartRangeResolver RANGE_RESOLVER = new BarChartRangeResolver();

    /**
     * 默认调色板，在系列未显式指定颜色时按顺序回退。
     */
    private static final List<Color> DEFAULT_PALETTE = Arrays.asList(
            new Color(72, 133, 237),
            new Color(234, 67, 53),
            new Color(52, 168, 83),
            new Color(251, 188, 5),
            new Color(123, 97, 255),
            new Color(0, 172, 193)
    );

    /**
     * 横轴分类列表。
     */
    private final List<String> categories = new ArrayList<String>();

    /**
     * 图表中的数据系列列表。
     */
    private final List<ChartSeries> seriesList = new ArrayList<ChartSeries>();

    /**
     * 统一的数值格式化器。
     */
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");

    /**
     * 图表内边距。
     */
    private Insets padding = new Insets(24, 24, 24, 24);
    /**
     * 图表背景色。
     */
    private Color backgroundColor;
    /**
     * 坐标轴颜色。
     */
    private Color axisColor = new Color(85, 92, 110);
    /**
     * 网格线颜色。
     */
    private Color gridColor = new Color(225, 229, 238);
    /**
     * 标题和坐标文本颜色。
     */
    private Color labelColor = new Color(71, 77, 92);
    /**
     * 柱体数值标签颜色。
     */
    private Color valueLabelColor = new Color(55, 60, 72);
    /**
     * 图表标题。
     */
    private String title;
    /**
     * 是否显示图例。
     */
    private boolean showLegend = true;
    /**
     * 是否显示网格线。
     */
    private boolean showGrid = true;
    /**
     * 是否显示数值标签。
     */
    private boolean showValueLabel = true;
    /**
     * 是否显示坐标轴。
     */
    private boolean showAxis = true;
    /**
     * 是否显示标题。
     */
    private boolean showTitle = true;
    /**
     * 是否启用普通堆叠。
     */
    private boolean stacked = false;
    /**
     * 是否启用百分比堆叠。
     */
    private boolean percentStacked = false;
    /**
     * 是否显示堆叠总标签。
     */
    private boolean showStackTotalLabel = false;
    /**
     * Y 轴刻度数量。
     */
    private int yAxisTickCount = 5;
    /**
     * 坐标轴线宽。
     */
    private int axisStrokeWidth = 1;
    /**
     * 标题字号。
     */
    private int titleFontSize = 18;
    /**
     * 坐标与分类标签字号。
     */
    private int labelFontSize = 12;
    /**
     * 图例字号。
     */
    private int legendFontSize = 12;
    /**
     * 数值标签字号。
     */
    private int valueLabelFontSize = 11;
    /**
     * 图例项之间的间距。
     */
    private int legendItemGap = 18;
    /**
     * 图例色块尺寸。
     */
    private int legendMarkerSize = 10;
    /**
     * 柱体最大宽度。
     */
    private int maxBarWidth = 56;
    /**
     * 柱体最小宽度。
     */
    private int minBarWidth = 6;
    /**
     * 柱体圆角半径。
     */
    private int barArc = 8;
    /**
     * 堆叠总标签与柱体的间距。
     */
    private int stackTotalLabelGap = 6;
    /**
     * 外置标签与柱体的间距。
     */
    private int externalLabelGap = 4;
    /**
     * 标签绘制在柱内所需的最小高度。
     */
    private int minInsideLabelHeight = 18;

    /**
     * 堆叠标签展示模式。
     */
    private StackLabelMode stackLabelMode = StackLabelMode.VALUE_PERCENT;

    /**
     * 小堆叠块是否允许将标签绘制到外侧。
     */
    private boolean showSmallStackLabelOutside = true;
    /**
     * 分类组之间的间距比例。
     */
    private double categoryGapRatio = 0.24D;
    /**
     * 同组柱子之间的间距比例。
     */
    private double barGapRatio = 0.18D;
    /**
     * 手动指定的最小值。
     */
    private Double minValue;
    /**
     * 手动指定的最大值。
     */
    private Double maxValue;

    /**
     * 创建指定宽高的柱状图元素。
     */
    public BarChartElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 设置图表标题。
     */
    public BarChartElement setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置图表内边距。
     */
    public BarChartElement setPadding(Insets padding) {
        this.padding = padding;
        return this;
    }

    /**
     * 设置图表背景色。
     */
    public BarChartElement setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * 设置坐标轴颜色。
     */
    public BarChartElement setAxisColor(Color axisColor) {
        this.axisColor = axisColor;
        return this;
    }

    /**
     * 设置网格线颜色。
     */
    public BarChartElement setGridColor(Color gridColor) {
        this.gridColor = gridColor;
        return this;
    }

    /**
     * 设置标题和坐标标签共用的文本颜色。
     */
    public BarChartElement setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
        return this;
    }

    /**
     * 设置柱体数值标签颜色。
     */
    public BarChartElement setValueLabelColor(Color valueLabelColor) {
        this.valueLabelColor = valueLabelColor;
        return this;
    }

    /**
     * 设置是否显示图例。
     */
    public BarChartElement setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
        return this;
    }

    /**
     * 设置是否显示网格线。
     */
    public BarChartElement setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        return this;
    }

    /**
     * 设置是否显示数值标签。
     */
    public BarChartElement setShowValueLabel(boolean showValueLabel) {
        this.showValueLabel = showValueLabel;
        return this;
    }

    /**
     * 设置是否显示坐标轴。
     */
    public BarChartElement setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        return this;
    }

    /**
     * 设置是否显示标题。
     */
    public BarChartElement setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    /**
     * 设置是否启用普通堆叠模式。
     */
    public BarChartElement setStacked(boolean stacked) {
        this.stacked = stacked;
        if (stacked) {
            // 启用堆叠后，普通标签和总标签会争夺空间，因此默认关闭总标签。
            this.showStackTotalLabel = false;
        }
        return this;
    }

    /**
     * 设置是否启用百分比堆叠模式。
     */
    public BarChartElement setPercentStacked(boolean percentStacked) {
        this.percentStacked = percentStacked;
        if (percentStacked) {
            // 百分比堆叠天然依赖堆叠布局，因此这里强制联动打开 stacked。
            this.stacked = true;
            this.showStackTotalLabel = false;
        }
        return this;
    }

    /**
     * 设置是否显示堆叠总标签。
     */
    public BarChartElement setShowStackTotalLabel(boolean showStackTotalLabel) {
        this.showStackTotalLabel = showStackTotalLabel;
        return this;
    }

    /**
     * 设置 Y 轴刻度数量。
     */
    public BarChartElement setYAxisTickCount(int yAxisTickCount) {
        if (yAxisTickCount < 2) {
            throw new PosterException("yAxisTickCount must be greater than or equal to 2");
        }
        this.yAxisTickCount = yAxisTickCount;
        return this;
    }

    /**
     * 设置坐标轴线宽。
     */
    public BarChartElement setAxisStrokeWidth(int axisStrokeWidth) {
        this.axisStrokeWidth = Math.max(1, axisStrokeWidth);
        return this;
    }

    /**
     * 设置标题字号。
     */
    public BarChartElement setTitleFontSize(int titleFontSize) {
        this.titleFontSize = titleFontSize;
        return this;
    }

    /**
     * 设置坐标与分类标签字号。
     */
    public BarChartElement setLabelFontSize(int labelFontSize) {
        this.labelFontSize = labelFontSize;
        return this;
    }

    /**
     * 设置图例字号。
     */
    public BarChartElement setLegendFontSize(int legendFontSize) {
        this.legendFontSize = legendFontSize;
        return this;
    }

    /**
     * 设置数值标签字号。
     */
    public BarChartElement setValueLabelFontSize(int valueLabelFontSize) {
        this.valueLabelFontSize = valueLabelFontSize;
        return this;
    }

    /**
     * 设置图例项之间的间距。
     */
    public BarChartElement setLegendItemGap(int legendItemGap) {
        this.legendItemGap = legendItemGap;
        return this;
    }

    /**
     * 设置图例色块尺寸。
     */
    public BarChartElement setLegendMarkerSize(int legendMarkerSize) {
        this.legendMarkerSize = legendMarkerSize;
        return this;
    }

    /**
     * 设置柱体最大宽度。
     */
    public BarChartElement setMaxBarWidth(int maxBarWidth) {
        this.maxBarWidth = maxBarWidth;
        return this;
    }

    /**
     * 设置柱体最小宽度。
     */
    public BarChartElement setMinBarWidth(int minBarWidth) {
        this.minBarWidth = minBarWidth;
        return this;
    }

    /**
     * 设置柱体圆角半径。
     */
    public BarChartElement setBarArc(int barArc) {
        this.barArc = Math.max(0, barArc);
        return this;
    }

    /**
     * 设置堆叠总标签与柱体之间的间距。
     */
    public BarChartElement setStackTotalLabelGap(int stackTotalLabelGap) {
        this.stackTotalLabelGap = Math.max(0, stackTotalLabelGap);
        return this;
    }

    /**
     * 设置外置标签与柱体之间的间距。
     */
    public BarChartElement setExternalLabelGap(int externalLabelGap) {
        this.externalLabelGap = Math.max(0, externalLabelGap);
        return this;
    }

    /**
     * 设置标签绘制在柱内所需的最小高度。
     */
    public BarChartElement setMinInsideLabelHeight(int minInsideLabelHeight) {
        this.minInsideLabelHeight = Math.max(8, minInsideLabelHeight);
        return this;
    }

    /**
     * 设置堆叠标签展示模式。
     */
    public BarChartElement setStackLabelMode(StackLabelMode stackLabelMode) {
        this.stackLabelMode = Optional.ofNullable(stackLabelMode).orElse(StackLabelMode.VALUE_PERCENT);
        return this;
    }

    /**
     * 设置小堆叠块是否允许把标签绘制到外侧。
     */
    public BarChartElement setShowSmallStackLabelOutside(boolean showSmallStackLabelOutside) {
        this.showSmallStackLabelOutside = showSmallStackLabelOutside;
        return this;
    }

    /**
     * 设置分类组之间的间距比例。
     */
    public BarChartElement setCategoryGapRatio(double categoryGapRatio) {
        this.categoryGapRatio = categoryGapRatio;
        return this;
    }

    /**
     * 设置同组柱子之间的间距比例。
     */
    public BarChartElement setBarGapRatio(double barGapRatio) {
        this.barGapRatio = barGapRatio;
        return this;
    }

    /**
     * 手动指定 Y 轴值域。
     */
    public BarChartElement setValueRange(Double minValue, Double maxValue) {
        if (minValue != null && maxValue != null && minValue >= maxValue) {
            throw new PosterException("minValue must be less than maxValue");
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
        return this;
    }

    /**
     * 批量替换横轴分类。
     */
    public BarChartElement setCategories(List<String> categories) {
        this.categories.clear();
        if (categories != null) {
            this.categories.addAll(categories);
        }
        return this;
    }

    /**
     * 添加单个分类标签。
     */
    public BarChartElement addCategory(String category) {
        this.categories.add(category);
        return this;
    }

    /**
     * 添加数据系列对象。
     */
    public BarChartElement addSeries(ChartSeries series) {
        if (series == null) {
            return this;
        }
        this.seriesList.add(series);
        return this;
    }

    /**
     * 通过名称和值列表添加数据系列。
     */
    public BarChartElement addSeries(String name, List<? extends Number> values) {
        return addSeries(ChartSeries.of(name, values));
    }

    /**
     * 通过名称、值列表和颜色添加数据系列。
     */
    public BarChartElement addSeries(String name, List<? extends Number> values, Color color) {
        return addSeries(ChartSeries.of(name, values).setColor(color));
    }

    /**
     * 完整执行柱状图渲染，包括标题、图例、坐标轴、柱体和标签布局。
     */
    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        validateData();
        Graphics2D graphics = context.getGraphics();
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            Point origin = dimension.getPoint();
            if (backgroundColor != null) {
                g.setColor(backgroundColor);
                g.fillRect(origin.getX(), origin.getY(), width, height);
            }
            Font baseFont = Optional.ofNullable(context.getConfig().getFont()).orElse(
                    new Font(context.getConfig().getFontName(), context.getConfig().getFontStyle(), context.getConfig().getFontSize())
            );
            ChartValueRange valueRange = resolveValueRange();
            Font titleFont = baseFont.deriveFont(Font.BOLD, (float) titleFontSize);
            Font labelFont = baseFont.deriveFont(Font.PLAIN, (float) labelFontSize);
            Font legendFont = baseFont.deriveFont(Font.PLAIN, (float) legendFontSize);
            Font valueFont = baseFont.deriveFont(Font.PLAIN, (float) valueLabelFontSize);
            int innerLeft = origin.getX() + padding.left;
            int innerTop = origin.getY() + padding.top;
            int innerRight = origin.getX() + width - padding.right;
            int innerBottom = origin.getY() + height - padding.bottom;
            int titleHeight = drawTitle(g, innerLeft, innerTop, innerRight, titleFont);
            innerTop += titleHeight;

            int legendHeight = drawLegend(g, innerLeft, innerTop, innerRight, legendFont);
            innerTop += legendHeight;
            List<Double> ticks = createTicks(valueRange);
            g.setFont(labelFont);
            FontMetrics labelMetrics = g.getFontMetrics();
            int yAxisLabelWidth = calcYAxisLabelWidth(labelMetrics, ticks);
            int xAxisLabelAreaHeight = labelMetrics.getHeight() + 8;
            if (showValueLabel) {
                g.setFont(valueFont);
                xAxisLabelAreaHeight += g.getFontMetrics().getHeight() / 4;
            }
            int plotLeft = innerLeft + yAxisLabelWidth + 10;
            int plotTop = innerTop + 4;
            int plotRight = innerRight;
            int plotBottom = innerBottom - xAxisLabelAreaHeight;
            int plotWidth = plotRight - plotLeft;
            int plotHeight = plotBottom - plotTop;
            // 标题、图例和标签已经占满可用空间时，继续绘制没有意义，直接中断。
            if (plotWidth <= 0 || plotHeight <= 0) {
                throw new PosterException("chart drawable area is too small");
            }

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int zeroY = calculateZeroY(plotTop, plotBottom, plotHeight, valueRange);

            drawGridAndAxis(g, plotLeft, plotTop, plotRight, plotBottom, zeroY, ticks, valueRange, labelFont);
            drawBars(g, plotLeft, plotTop, plotRight, plotBottom, zeroY, plotWidth, plotHeight, valueRange, valueFont);
            drawXAxisLabels(g, plotLeft, plotBottom, plotWidth, labelFont);
        } finally {
            g.dispose();
        }
        return dimension.getPoint();
    }

    /**
     * 走父类预处理流程。
     */
    @Override
    public void beforeRender(PosterContext context) {
        super.beforeRender(context);
    }

    /**
     * 校验渲染前置条件，确保分类和系列数据能够一一对应。
     */
    private void validateData() {
        if (width <= 0 || height <= 0) {
            throw new PosterException("chart width and height must be greater than 0");
        }
        if (categories.isEmpty()) {
            throw new PosterException("chart categories can not be empty");
        }
        if (seriesList.isEmpty()) {
            throw new PosterException("chart series can not be empty");
        }
        for (ChartSeries series : seriesList) {
            if (series.getValues().size() != categories.size()) {
                throw new PosterException("series value size must match category size");
            }
        }
    }

    /**
     * 绘制标题并返回其占用高度。
     */
    private int drawTitle(Graphics2D g, int left, int top, int right, Font titleFont) {
        if (!showTitle || title == null || title.trim().isEmpty()) {
            return 0;
        }
        g.setFont(titleFont);
        g.setColor(labelColor);
        FontMetrics metrics = g.getFontMetrics();
        int textY = top + metrics.getAscent();
        g.drawString(title, left, textY);
        return metrics.getHeight() + 10;
    }

    /**
     * 绘制图例并返回其占用高度。
     */
    private int drawLegend(Graphics2D g, int left, int top, int right, Font legendFont) {
        if (!showLegend) {
            return 0;
        }
        g.setFont(legendFont);
        FontMetrics metrics = g.getFontMetrics();
        int x = left;
        int baseline = top + metrics.getAscent();
        int rowHeight = Math.max(metrics.getHeight(), legendMarkerSize) + 6;
        int usedHeight = rowHeight;
        for (int i = 0; i < seriesList.size(); i++) {
            ChartSeries series = seriesList.get(i);
            String text = Optional.ofNullable(series.getName()).orElse(String.valueOf((i + 1)));
            int textWidth = metrics.stringWidth(text);
            int itemWidth = legendMarkerSize + 6 + textWidth + legendItemGap;
            if (x + itemWidth > right && x > left) {
                x = left;
                baseline += rowHeight;
                usedHeight += rowHeight;
            }
            g.setColor(resolveSeriesColor(series, i));
            int markerY = baseline - metrics.getAscent() + Math.max(0, (metrics.getHeight() - legendMarkerSize) / 2);
            g.fillRoundRect(x, markerY, legendMarkerSize, legendMarkerSize, 4, 4);
            g.setColor(labelColor);
            g.drawString(text, x + legendMarkerSize + 6, baseline);
            x += itemWidth;
        }
        return usedHeight + 4;
    }

    /**
     * 计算 Y 轴刻度文本的最大宽度。
     */
    private int calcYAxisLabelWidth(FontMetrics metrics, List<Double> ticks) {
        int max = 0;
        for (Double tick : ticks) {
            max = Math.max(max, metrics.stringWidth(formatValue(tick)));
        }
        return max;
    }

    /**
     * 绘制网格线和坐标轴，并输出 Y 轴刻度文本。
     */
    private void drawGridAndAxis(Graphics2D g, int plotLeft, int plotTop, int plotRight, int plotBottom,
                                 int zeroY, List<Double> ticks, ChartValueRange valueRange, Font labelFont) {
        g.setFont(labelFont);
        FontMetrics metrics = g.getFontMetrics();
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(axisStrokeWidth));
        for (Double tick : ticks) {
            double ratio = (tick - valueRange.getMin()) / (valueRange.getMax() - valueRange.getMin());
            int y = plotBottom - (int) Math.round(ratio * (plotBottom - plotTop));
            if (showGrid) {
                g.setColor(gridColor);
                g.drawLine(plotLeft, y, plotRight, y);
            }
            g.setColor(labelColor);
            String tickText = formatAxisValue(tick);
            int textWidth = metrics.stringWidth(tickText);
            g.drawString(tickText, plotLeft - textWidth - 10, y + metrics.getAscent() / 2 - 2);
        }
        if (showAxis) {
            g.setColor(axisColor);
            g.drawLine(plotLeft, plotTop, plotLeft, plotBottom);
            g.drawLine(plotLeft, zeroY, plotRight, zeroY);
        }
        g.setStroke(oldStroke);
    }

    /**
     * 计算数值 0 在绘图区对应的 Y 坐标。
     */
    private int calculateZeroY(int plotTop, int plotBottom, int plotHeight, ChartValueRange valueRange) {
        // 全为正数时，零轴贴近底边。
        if (valueRange.getMin() >= 0) {
            return plotBottom;
        }
        // 全为负数时，零轴贴近顶边。
        if (valueRange.getMax() <= 0) {
            return plotTop;
        }
        // 同时包含正负值时，按真实比例插值计算零轴位置。
        double baselineRatio = (0D - valueRange.getMin()) / (valueRange.getMax() - valueRange.getMin());
        return plotBottom - (int) Math.round(plotHeight * baselineRatio);
    }

    /**
     * 按当前模式在分组柱和堆叠柱之间分派绘制逻辑。
     */
    private void drawBars(Graphics2D g, int plotLeft, int plotTop, int plotRight, int plotBottom, int zeroY,
                          int plotWidth, int plotHeight, ChartValueRange valueRange, Font valueFont) {
        if (stacked) {
            drawStackedBars(g, plotLeft, plotTop, plotBottom, zeroY, plotWidth, plotHeight, valueRange, valueFont);
            return;
        }
        drawGroupedBars(g, plotLeft, plotBottom, zeroY, plotWidth, plotHeight, valueRange, valueFont);
    }

    /**
     * 绘制普通分组柱状图。
     */
    private void drawGroupedBars(Graphics2D g, int plotLeft, int plotBottom, int zeroY,
                                 int plotWidth, int plotHeight, ChartValueRange valueRange, Font valueFont) {
        int categoryCount = categories.size();
        int seriesCount = seriesList.size();
        BarChartLayoutCalculator.GroupedLayout layout = LAYOUT_CALCULATOR.calculateGrouped(
                plotLeft, plotWidth, categoryCount, seriesCount,
                categoryGapRatio, barGapRatio, minBarWidth, maxBarWidth
        );
        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
                ChartSeries series = seriesList.get(seriesIndex);
                double value = series.getValues().get(categoryIndex);
                // 柱高始终按值域跨度归一化，负值仅改变起点，不改变高度绝对值。
                double normalized = Math.abs(value) / (valueRange.getMax() - valueRange.getMin());
                int barHeight = (int) Math.round(normalized * plotHeight);
                int barWidth = layout.getBarWidth();
                int x = layout.resolveBarX(categoryIndex, seriesIndex);
                int y = value >= 0 ? zeroY - barHeight : zeroY;

                g.setColor(resolveSeriesColor(series, seriesIndex));
                fillBar(g, x, y, barWidth, barHeight, true, true);

                if (showValueLabel) {
                    drawValueLabel(g, valueFont, value, x, y, barWidth, barHeight, zeroY);
                }
            }
        }
    }

    /**
     * 绘制堆叠柱和百分比堆叠柱。
     */
    private void drawStackedBars(Graphics2D g, int plotLeft, int plotTop, int plotBottom, int zeroY,
                                 int plotWidth, int plotHeight, ChartValueRange valueRange, Font valueFont) {
        int categoryCount = categories.size();
        BarChartLayoutCalculator.StackedLayout layout = LAYOUT_CALCULATOR.calculateStacked(
                plotLeft, plotWidth, categoryCount, categoryGapRatio, minBarWidth, maxBarWidth
        );

        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            int barWidth = layout.getBarWidth();
            int x = layout.resolveBarX(categoryIndex);
            double positiveBase = 0D;
            double negativeBase = 0D;
            double positiveTotal = getCategoryPositiveTotal(categoryIndex);
            double negativeTotal = getCategoryNegativeTotal(categoryIndex);

            int positiveCount = countVisibleSegments(categoryIndex, true);
            int negativeCount = countVisibleSegments(categoryIndex, false);
            int positiveIndex = 0;
            int negativeIndex = 0;

            for (int seriesIndex = 0; seriesIndex < seriesList.size(); seriesIndex++) {
                ChartSeries series = seriesList.get(seriesIndex);
                double value = series.getValues().get(categoryIndex);
                if (Double.compare(value, 0D) == 0) {
                    continue;
                }
                double displayValue = value;
                if (percentStacked) {
                    // 百分比堆叠时，正负值分别按各自方向的总量归一化，避免相互抵消。
                    double divisor = value > 0 ? positiveTotal : Math.abs(negativeTotal);
                    if (Double.compare(divisor, 0D) != 0) {
                        displayValue = value / divisor * 100D;
                    } else {
                        displayValue = 0D;
                    }
                }

                double startValue;
                double endValue;
                boolean positive = displayValue > 0;
                // 正值和负值分别沿零轴两侧独立累加，保证混合数据时柱段方向正确。
                if (positive) {
                    startValue = positiveBase;
                    positiveBase += displayValue;
                    endValue = positiveBase;
                } else {
                    startValue = negativeBase;
                    negativeBase += displayValue;
                    endValue = negativeBase;
                }

                int segmentTop = valueToY(endValue, plotTop, plotBottom, valueRange);
                int segmentBottom = valueToY(startValue, plotTop, plotBottom, valueRange);
                int y = Math.min(segmentTop, segmentBottom);
                int barHeight = Math.abs(segmentBottom - segmentTop);
                if (barHeight <= 0) {
                    barHeight = 1;
                }
                // 只有堆叠边缘段保留圆角，中间段保持直角以便拼成连续整体。
                boolean roundStart = positive ? positiveIndex == positiveCount - 1 : negativeIndex == 0;
                boolean roundEnd = positive ? positiveIndex == 0 : negativeIndex == negativeCount - 1;
                if (positive) {
                    positiveIndex++;
                } else {
                    negativeIndex++;
                }

                g.setColor(resolveSeriesColor(series, seriesIndex));
                fillBar(g, x, y, barWidth, barHeight, roundStart, roundEnd);

                if (showValueLabel) {
                    double percent = resolveStackSegmentPercent(value, positiveTotal, negativeTotal);
                    drawStackedValueLabel(g, valueFont, value, percent, x, y, barWidth, barHeight, positive, zeroY);
                }
            }
        }
    }

    /**
     * 根据圆角策略填充单个柱段。
     */
    private void fillBar(Graphics2D g, int x, int y, int barWidth, int barHeight, boolean roundTop, boolean roundBottom) {
        if (barHeight <= 0 || barWidth <= 0) {
            return;
        }
        if (barArc <= 0 || (!roundTop && !roundBottom)) {
            g.fillRect(x, y, barWidth, barHeight);
            return;
        }
        if (roundTop && roundBottom) {
            g.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, barArc, barArc));
            return;
        }
        int arcInset = Math.min(barHeight / 2, Math.max(1, barArc / 2));
        if (roundTop) {
            g.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, barArc, barArc));
            if (barHeight > arcInset) {
                g.fillRect(x, y + arcInset, barWidth, barHeight - arcInset);
            }
            return;
        }

        g.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, barArc, barArc));
        if (barHeight > arcInset) {
            g.fillRect(x, y, barWidth, barHeight - arcInset);
        }
    }

    /**
     * 统计当前分类下可见的正向或负向堆叠段数量。
     */
    private int countVisibleSegments(int categoryIndex, boolean positive) {
        int count = 0;
        for (ChartSeries series : seriesList) {
            double value = series.getValues().get(categoryIndex);
            if (positive && value > 0) {
                count++;
            } else if (!positive && value < 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * 汇总当前分类下所有正值。
     */
    private double getCategoryPositiveTotal(int categoryIndex) {
        double total = 0D;
        for (ChartSeries series : seriesList) {
            double value = series.getValues().get(categoryIndex);
            if (value > 0) {
                total += value;
            }
        }
        return total;
    }

    /**
     * 汇总当前分类下所有负值。
     */
    private double getCategoryNegativeTotal(int categoryIndex) {
        double total = 0D;
        for (ChartSeries series : seriesList) {
            double value = series.getValues().get(categoryIndex);
            if (value < 0) {
                total += value;
            }
        }
        return total;
    }

    /**
     * 将数值映射到绘图区的 Y 坐标。
     */
    private int valueToY(double value, int plotTop, int plotBottom, ChartValueRange valueRange) {
        double ratio = (value - valueRange.getMin()) / (valueRange.getMax() - valueRange.getMin());
        return plotBottom - (int) Math.round(ratio * (plotBottom - plotTop));
    }

    /**
     * 计算堆叠段在各自正负方向中的占比。
     */
    private double resolveStackSegmentPercent(double value, double positiveTotal, double negativeTotal) {
        if (Double.compare(value, 0D) > 0) {
            if (Double.compare(positiveTotal, 0D) == 0) {
                return 0D;
            }
            return value / positiveTotal * 100D;
        }
        if (Double.compare(value, 0D) < 0) {
            if (Double.compare(negativeTotal, 0D) == 0) {
                return 0D;
            }
            return value / Math.abs(negativeTotal) * 100D;
        }
        return 0D;
    }

    /**
     * 绘制堆叠柱的数值标签。
     */
    private void drawStackedValueLabel(Graphics2D g, Font valueFont, double rawValue, double percent,
                                       int x, int y, int barWidth, int barHeight, boolean positive, int zeroY) {
        LABEL_RENDERER.drawStackedValueLabel(
                g, valueFont, valueLabelColor, formatStackedValueLabel(rawValue, percent),
                x, y, barWidth, barHeight, positive, zeroY,
                minInsideLabelHeight, showSmallStackLabelOutside, externalLabelGap
        );
    }

    /**
     * 绘制普通柱状图的数值标签。
     */
    private void drawValueLabel(Graphics2D g, Font valueFont, double value, int x, int y,
                                int barWidth, int barHeight, int zeroY) {
        LABEL_RENDERER.drawValueLabel(
                g, valueFont, valueLabelColor, formatValue(value),
                value, x, y, barWidth, barHeight, zeroY
        );
    }

    /**
     * 绘制横轴分类文本。
     */
    private void drawXAxisLabels(Graphics2D g, int plotLeft, int plotBottom, int plotWidth, Font labelFont) {
        g.setFont(labelFont);
        g.setColor(labelColor);
        FontMetrics metrics = g.getFontMetrics();
        double categoryWidth = (double) plotWidth / categories.size();
        for (int i = 0; i < categories.size(); i++) {
            String category = Optional.ofNullable(categories.get(i)).orElse("");
            int textWidth = metrics.stringWidth(category);
            int x = (int) Math.round(plotLeft + i * categoryWidth + (categoryWidth - textWidth) / 2D);
            int y = plotBottom + metrics.getAscent() + 6;
            g.drawString(category, x, y);
        }
    }

    /**
     * 解析系列最终颜色。
     */
    private Color resolveSeriesColor(ChartSeries series, int index) {
        return Optional.ofNullable(series.getColor()).orElse(DEFAULT_PALETTE.get(index % DEFAULT_PALETTE.size()));
    }

    /**
     * 解析当前图表的值域范围。
     */
    private ChartValueRange resolveValueRange() {
        return RANGE_RESOLVER.resolve(categories, seriesList, stacked, percentStacked, minValue, maxValue);
    }

    /**
     * 按值域和刻度数量生成 Y 轴刻度。
     */
    private List<Double> createTicks(ChartValueRange valueRange) {
        List<Double> ticks = new ArrayList<Double>();
        double step = (valueRange.getMax() - valueRange.getMin()) / (yAxisTickCount - 1);
        for (int i = 0; i < yAxisTickCount; i++) {
            ticks.add(valueRange.getMin() + step * i);
        }
        return ticks;
    }

    /**
     * 格式化普通数值文本。
     */
    private String formatValue(double value) {
        return decimalFormat.format(value);
    }

    /**
     * 格式化坐标轴数值文本。
     */
    private String formatAxisValue(double value) {
        return percentStacked ? formatValue(value) + "%" : formatValue(value);
    }

    /**
     * 按堆叠标签模式拼装最终展示文本。
     */
    private String formatStackedValueLabel(double rawValue, double percent) {
        if (stackLabelMode == StackLabelMode.VALUE) {
            return formatValue(rawValue);
        }
        if (stackLabelMode == StackLabelMode.PERCENT) {
            return formatValue(percent) + "%";
        }
        return formatValue(rawValue) + "(" + formatValue(percent) + "%)";
    }

    @Getter
    @AllArgsConstructor
    /**
     * 堆叠柱标签展示模式。
     */
    public enum StackLabelMode {
        VALUE("value"),
        PERCENT("percent"),
        VALUE_PERCENT("value_percent");
        private String desc;
    }
}
