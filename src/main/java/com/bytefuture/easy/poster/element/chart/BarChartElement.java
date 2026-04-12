package com.bytefuture.easy.poster.element.chart;

import com.bytefuture.easy.poster.element.AbstractDimensionElement;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 柱状图元素。
 * 用于在海报中绘制分组柱状图、堆叠柱状图以及百分比堆叠柱状图。
 *
 * @author biaoy
 * @since 2026/04/11
 */
public class BarChartElement extends AbstractDimensionElement<BarChartElement> {

    /**
     * 默认系列配色，未为系列单独指定颜色时按顺序循环使用。
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
     * X 轴类目名称列表，例如月份、城市、部门等。
     */
    private final List<String> categories = new ArrayList<String>();

    /**
     * 图表系列数据集合，每个系列都需要与类目数量一一对应。
     */
    private final List<BarChartSeries> seriesList = new ArrayList<BarChartSeries>();
    /**
     * 数值格式化器，用于坐标轴和数值标签的文本展示。
     */
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");
    /**
     * 图表内容内边距，控制标题、图例、绘图区与外边界的距离。
     */
    private Insets padding = new Insets(24, 24, 24, 24);
    /**
     * 背景色，为 null 时不绘制背景。
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
     * 标题、坐标轴标签、图例文本颜色。
     */
    private Color labelColor = new Color(71, 77, 92);
    /**
     * 柱体外部数值标签颜色。
     */
    private Color valueLabelColor = new Color(55, 60, 72);
    /**
     * 图表标题文本。
     */
    private String title;
    /**
     * 是否显示图例。
     */
    private boolean showLegend = true;
    /**
     * 是否显示横向网格线。
     */
    private boolean showGrid = true;
    /**
     * 是否显示柱体数值标签。
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
     * 是否启用堆叠柱状图模式。
     */
    private boolean stacked = false;
    /**
     * 是否启用百分比堆叠模式。开启后会自动进入堆叠模式。
     */
    private boolean percentStacked = false;
    /**
     * 是否显示堆叠总计标签。当前类中暂未实际使用，但保留配置入口。
     */
    private boolean showStackTotalLabel = false;
    /**
     * Y 轴刻度数量，至少为 2。
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
     * 坐标轴标签字号。
     */
    private int labelFontSize = 12;
    /**
     * 图例字号。
     */
    private int legendFontSize = 12;
    /**
     * 柱体数值标签字号。
     */
    private int valueLabelFontSize = 11;
    /**
     * 图例项之间的水平间距。
     */
    private int legendItemGap = 18;
    /**
     * 图例色块尺寸。
     */
    private int legendMarkerSize = 10;
    /**
     * 柱体允许的最大宽度。
     */
    private int maxBarWidth = 56;
    /**
     * 柱体允许的最小宽度。
     */
    private int minBarWidth = 6;
    /**
     * 柱体圆角大小，0 表示直角。
     */
    private int barArc = 8;
    /**
     * 堆叠总计标签与柱体之间的间距。当前类中暂未实际使用。
     */
    private int stackTotalLabelGap = 6;
    /**
     * 外部标签与柱体之间的间距。
     */
    private int externalLabelGap = 4;
    /**
     * 堆叠标签允许绘制在柱体内部的最小高度阈值。
     */
    private int minInsideLabelHeight = 18;
    /**
     * 堆叠标签展示模式：值、百分比，或值加百分比。
     */
    private StackLabelMode stackLabelMode = StackLabelMode.VALUE_PERCENT;
    /**
     * 堆叠段太小时，是否将标签绘制到柱体外部。
     */
    private boolean showSmallStackLabelOutside = true;
    /**
     * 类目之间的间隔比例，基于单个类目宽度计算。
     */
    private double categoryGapRatio = 0.24D;
    /**
     * 同一类目下柱子之间的间隔比例，基于单根柱宽计算。
     */
    private double barGapRatio = 0.18D;
    /**
     * 手动指定的最小值，为 null 时自动推导。
     */
    private Double minValue;
    /**
     * 手动指定的最大值，为 null 时自动推导。
     */
    private Double maxValue;

    public BarChartElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public BarChartElement setTitle(String title) {
        this.title = title;
        return this;
    }

    public BarChartElement setPadding(Insets padding) {
        this.padding = padding;
        return this;
    }

    public BarChartElement setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public BarChartElement setAxisColor(Color axisColor) {
        this.axisColor = axisColor;
        return this;
    }

    public BarChartElement setGridColor(Color gridColor) {
        this.gridColor = gridColor;
        return this;
    }

    public BarChartElement setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
        return this;
    }

    public BarChartElement setValueLabelColor(Color valueLabelColor) {
        this.valueLabelColor = valueLabelColor;
        return this;
    }

    public BarChartElement setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
        return this;
    }

    public BarChartElement setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        return this;
    }

    public BarChartElement setShowValueLabel(boolean showValueLabel) {
        this.showValueLabel = showValueLabel;
        return this;
    }

    public BarChartElement setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        return this;
    }

    public BarChartElement setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    /**
     * 设置是否为普通堆叠柱状图。
     * 开启后会关闭堆叠总计标签，避免与堆叠内部标签冲突。
     */
    public BarChartElement setStacked(boolean stacked) {
        this.stacked = stacked;
        if (stacked) {
            this.showStackTotalLabel = false;
        }
        return this;
    }

    /**
     * 设置是否为百分比堆叠柱状图。
     * 开启后会自动启用堆叠模式，并关闭堆叠总计标签。
     */
    public BarChartElement setPercentStacked(boolean percentStacked) {
        this.percentStacked = percentStacked;
        if (percentStacked) {
            this.stacked = true;
            this.showStackTotalLabel = false;
        }
        return this;
    }

    public BarChartElement setShowStackTotalLabel(boolean showStackTotalLabel) {
        this.showStackTotalLabel = showStackTotalLabel;
        return this;
    }

    public BarChartElement setYAxisTickCount(int yAxisTickCount) {
        if (yAxisTickCount < 2) {
            throw new PosterException("yAxisTickCount must be greater than or equal to 2");
        }
        this.yAxisTickCount = yAxisTickCount;
        return this;
    }

    public BarChartElement setAxisStrokeWidth(int axisStrokeWidth) {
        this.axisStrokeWidth = Math.max(1, axisStrokeWidth);
        return this;
    }

    public BarChartElement setTitleFontSize(int titleFontSize) {
        this.titleFontSize = titleFontSize;
        return this;
    }

    public BarChartElement setLabelFontSize(int labelFontSize) {
        this.labelFontSize = labelFontSize;
        return this;
    }

    public BarChartElement setLegendFontSize(int legendFontSize) {
        this.legendFontSize = legendFontSize;
        return this;
    }

    public BarChartElement setValueLabelFontSize(int valueLabelFontSize) {
        this.valueLabelFontSize = valueLabelFontSize;
        return this;
    }

    public BarChartElement setLegendItemGap(int legendItemGap) {
        this.legendItemGap = legendItemGap;
        return this;
    }

    public BarChartElement setLegendMarkerSize(int legendMarkerSize) {
        this.legendMarkerSize = legendMarkerSize;
        return this;
    }

    public BarChartElement setMaxBarWidth(int maxBarWidth) {
        this.maxBarWidth = maxBarWidth;
        return this;
    }

    public BarChartElement setMinBarWidth(int minBarWidth) {
        this.minBarWidth = minBarWidth;
        return this;
    }

    public BarChartElement setBarArc(int barArc) {
        this.barArc = Math.max(0, barArc);
        return this;
    }

    public BarChartElement setStackTotalLabelGap(int stackTotalLabelGap) {
        this.stackTotalLabelGap = Math.max(0, stackTotalLabelGap);
        return this;
    }

    public BarChartElement setExternalLabelGap(int externalLabelGap) {
        this.externalLabelGap = Math.max(0, externalLabelGap);
        return this;
    }

    public BarChartElement setMinInsideLabelHeight(int minInsideLabelHeight) {
        this.minInsideLabelHeight = Math.max(8, minInsideLabelHeight);
        return this;
    }

    public BarChartElement setStackLabelMode(StackLabelMode stackLabelMode) {
        this.stackLabelMode = Optional.ofNullable(stackLabelMode).orElse(StackLabelMode.VALUE_PERCENT);
        return this;
    }

    public BarChartElement setShowSmallStackLabelOutside(boolean showSmallStackLabelOutside) {
        this.showSmallStackLabelOutside = showSmallStackLabelOutside;
        return this;
    }

    public BarChartElement setCategoryGapRatio(double categoryGapRatio) {
        this.categoryGapRatio = categoryGapRatio;
        return this;
    }

    public BarChartElement setBarGapRatio(double barGapRatio) {
        this.barGapRatio = barGapRatio;
        return this;
    }

    /**
     * 设置 Y 轴显示范围。
     * 任一端为 null 时表示沿用自动计算结果。
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
     * 设置所有类目名称，会覆盖原有类目列表。
     */
    public BarChartElement setCategories(List<String> categories) {
        this.categories.clear();
        if (categories != null) {
            this.categories.addAll(categories);
        }
        return this;
    }

    public BarChartElement addCategory(String category) {
        this.categories.add(category);
        return this;
    }

    /**
     * 添加一个数据系列。
     */
    public BarChartElement addSeries(BarChartSeries series) {
        if (series == null) {
            return this;
        }
        this.seriesList.add(series);
        return this;
    }

    public BarChartElement addSeries(String name, List<? extends Number> values) {
        return addSeries(BarChartSeries.of(name, values));
    }

    public BarChartElement addSeries(String name, List<? extends Number> values, Color color) {
        return addSeries(BarChartSeries.of(name, values).setColor(color));
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        // 先校验输入数据，避免在绘制阶段出现数组越界或无效尺寸。
        validateData();
        Graphics2D graphics = context.getGraphics();
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            Point origin = dimension.getPoint();
            // 有背景色时，先绘制图表背景区域。
            if (backgroundColor != null) {
                g.setColor(backgroundColor);
                g.fillRect(origin.getX(), origin.getY(), width, height);
            }

            // 优先使用上下文中已构造好的字体，否则按配置动态创建默认字体。
            Font baseFont = Optional.ofNullable(context.getConfig().getFont()).orElse(
                    new Font(context.getConfig().getFontName(), context.getConfig().getFontStyle(), context.getConfig().getFontSize())
            );

            // 先计算数值区间，再派生不同用途的字体。
            ValueRange valueRange = resolveValueRange();
            Font titleFont = baseFont.deriveFont(Font.BOLD, (float) titleFontSize);
            Font labelFont = baseFont.deriveFont(Font.PLAIN, (float) labelFontSize);
            Font legendFont = baseFont.deriveFont(Font.PLAIN, (float) legendFontSize);
            Font valueFont = baseFont.deriveFont(Font.PLAIN, (float) valueLabelFontSize);

            // 先扣除内边距，得到图表内部可用区域。
            int innerLeft = origin.getX() + padding.left;
            int innerTop = origin.getY() + padding.top;
            int innerRight = origin.getX() + width - padding.right;
            int innerBottom = origin.getY() + height - padding.bottom;

            // 标题和图例会逐步占用顶部空间。
            int titleHeight = drawTitle(g, innerLeft, innerTop, innerRight, titleFont);
            innerTop += titleHeight;

            int legendHeight = drawLegend(g, innerLeft, innerTop, innerRight, legendFont);
            innerTop += legendHeight;

            // 根据刻度文本宽度预留 Y 轴标签区域。
            List<Double> ticks = createTicks(valueRange);
            g.setFont(labelFont);
            FontMetrics labelMetrics = g.getFontMetrics();
            int yAxisLabelWidth = calcYAxisLabelWidth(labelMetrics, ticks);

            // X 轴类目标签和数值标签都需要预留底部空间。
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
            if (plotWidth <= 0 || plotHeight <= 0) {
                throw new PosterException("chart drawable area is too small");
            }

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 0 刻度所在位置既决定 X 轴绘制位置，也决定正负柱子的生长方向。
            int zeroY = calculateZeroY(plotTop, plotBottom, plotHeight, valueRange);

            drawGridAndAxis(g, plotLeft, plotTop, plotRight, plotBottom, zeroY, ticks, valueRange, labelFont);
            drawBars(g, plotLeft, plotTop, plotRight, plotBottom, zeroY, plotWidth, plotHeight, valueRange, valueFont);
            drawXAxisLabels(g, plotLeft, plotBottom, plotWidth, labelFont);
        } finally {
            g.dispose();
        }
        return dimension.getPoint();
    }

    @Override
    public void beforeRender(PosterContext context) {
        super.beforeRender(context);
    }

    /**
     * 校验图表尺寸和数据结构是否合法。
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
        for (BarChartSeries series : seriesList) {
            if (series.getValues().size() != categories.size()) {
                throw new PosterException("series value size must match category size");
            }
        }
    }

    /**
     * 绘制标题，并返回标题实际占用的高度。
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
     * 绘制图例，并返回图例实际占用的高度。
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
            BarChartSeries series = seriesList.get(i);
            String text = Optional.ofNullable(series.getName()).orElse(String.valueOf((i + 1)));
            int textWidth = metrics.stringWidth(text);
            int itemWidth = legendMarkerSize + 6 + textWidth + legendItemGap;
            // 当前行放不下时自动换行，避免图例越界。
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
     * 计算 Y 轴标签所需的最大宽度，用于为绘图区留出左边距。
     */
    private int calcYAxisLabelWidth(FontMetrics metrics, List<Double> ticks) {
        int max = 0;
        for (Double tick : ticks) {
            max = Math.max(max, metrics.stringWidth(formatValue(tick)));
        }
        return max;
    }

    /**
     * 绘制网格线、Y 轴刻度文本以及坐标轴线。
     */
    private void drawGridAndAxis(Graphics2D g, int plotLeft, int plotTop, int plotRight, int plotBottom,
                                 int zeroY, List<Double> ticks, ValueRange valueRange, Font labelFont) {
        g.setFont(labelFont);
        FontMetrics metrics = g.getFontMetrics();
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(axisStrokeWidth));
        for (Double tick : ticks) {
            // 每个刻度都要换算到绘图区中的实际像素坐标。
            double ratio = (tick - valueRange.min) / (valueRange.max - valueRange.min);
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
        // 坐标轴可单独关闭，此时仅保留网格线与文字。
        if (showAxis) {
            g.setColor(axisColor);
            g.drawLine(plotLeft, plotTop, plotLeft, plotBottom);
            g.drawLine(plotLeft, zeroY, plotRight, zeroY);
        }
        g.setStroke(oldStroke);
    }

    /**
     * 计算数值 0 对应的 Y 坐标。
     * 全正数时基线在底部，全负数时基线在顶部，正负混合时按比例计算。
     */
    private int calculateZeroY(int plotTop, int plotBottom, int plotHeight, ValueRange valueRange) {
        if (valueRange.min >= 0) {
            return plotBottom;
        }
        if (valueRange.max <= 0) {
            return plotTop;
        }
        double baselineRatio = (0D - valueRange.min) / (valueRange.max - valueRange.min);
        return plotBottom - (int) Math.round(plotHeight * baselineRatio);
    }

    /**
     * 按当前模式分发绘制逻辑：
     * `stacked=true` 时绘制堆叠柱，否则绘制分组柱。
     */
    private void drawBars(Graphics2D g, int plotLeft, int plotTop, int plotRight, int plotBottom, int zeroY,
                          int plotWidth, int plotHeight, ValueRange valueRange, Font valueFont) {
        if (stacked) {
            drawStackedBars(g, plotLeft, plotTop, plotBottom, zeroY, plotWidth, plotHeight, valueRange, valueFont);
            return;
        }
        drawGroupedBars(g, plotLeft, plotBottom, zeroY, plotWidth, plotHeight, valueRange, valueFont);
    }

    /**
     * 绘制分组柱状图。
     * 同一类目下每个系列独立成柱，并按间距依次排开。
     */
    private void drawGroupedBars(Graphics2D g, int plotLeft, int plotBottom, int zeroY,
                                 int plotWidth, int plotHeight, ValueRange valueRange, Font valueFont) {
        int categoryCount = categories.size();
        int seriesCount = seriesList.size();
        double categoryWidth = (double) plotWidth / categoryCount;
        double categoryGap = Math.max(4D, categoryWidth * categoryGapRatio);
        double groupWidth = categoryWidth - categoryGap;
        // 如果类目间距过大导致组宽度小于等于 0，则回退为类目宽度的 80%。
        if (groupWidth <= 0) {
            groupWidth = categoryWidth * 0.8D;
        }

        double computedBarWidth = groupWidth / (seriesCount + Math.max(0, seriesCount - 1) * barGapRatio);
        computedBarWidth = Math.max(minBarWidth, Math.min(maxBarWidth, computedBarWidth));
        double actualGroupWidth = computedBarWidth * (seriesCount + Math.max(0, seriesCount - 1) * barGapRatio);
        double barGap = seriesCount <= 1 ? 0 : computedBarWidth * barGapRatio;

        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            double groupStart = plotLeft + categoryWidth * categoryIndex + (categoryWidth - actualGroupWidth) / 2D;
            for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
                BarChartSeries series = seriesList.get(seriesIndex);
                double value = series.getValues().get(categoryIndex);
                double normalized = Math.abs(value) / (valueRange.max - valueRange.min);
                int barHeight = (int) Math.round(normalized * plotHeight);
                int barWidth = Math.max(1, (int) Math.round(computedBarWidth));
                int x = (int) Math.round(groupStart + seriesIndex * (computedBarWidth + barGap));
                // 分组模式下，正值向上生长，负值向下生长。
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
     * 绘制堆叠柱状图。
     * 正值和负值分别以 0 为基线向两侧堆叠，百分比模式下会先换算为百分比后再绘制。
     */
    private void drawStackedBars(Graphics2D g, int plotLeft, int plotTop, int plotBottom, int zeroY,
                                 int plotWidth, int plotHeight, ValueRange valueRange, Font valueFont) {
        int categoryCount = categories.size();
        double categoryWidth = (double) plotWidth / categoryCount;
        double categoryGap = Math.max(4D, categoryWidth * categoryGapRatio);
        double barWidthDouble = Math.max(minBarWidth, Math.min(maxBarWidth, categoryWidth - categoryGap));

        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            int barWidth = Math.max(1, (int) Math.round(barWidthDouble));
            int x = (int) Math.round(plotLeft + categoryIndex * categoryWidth + (categoryWidth - barWidthDouble) / 2D);
            // 正负值分别累计，避免同一类目同时包含正负值时相互覆盖。
            double positiveBase = 0D;
            double negativeBase = 0D;
            double positiveTotal = getCategoryPositiveTotal(categoryIndex);
            double negativeTotal = getCategoryNegativeTotal(categoryIndex);

            int positiveCount = countVisibleSegments(categoryIndex, true);
            int negativeCount = countVisibleSegments(categoryIndex, false);
            int positiveIndex = 0;
            int negativeIndex = 0;

            for (int seriesIndex = 0; seriesIndex < seriesList.size(); seriesIndex++) {
                BarChartSeries series = seriesList.get(seriesIndex);
                double value = series.getValues().get(categoryIndex);
                // 0 值段不绘制，可减少无意义的极细线段。
                if (Double.compare(value, 0D) == 0) {
                    continue;
                }
                double displayValue = value;
                if (percentStacked) {
                    // 百分比堆叠需要按当前正向/负向总量归一化。
                    double divisor = value > 0 ? positiveTotal : Math.abs(negativeTotal);
                    if (Double.compare(divisor, 0D) != 0) {
                        displayValue = value / divisor * 100D;
                    } else {
                        // 总量为 0 时无法计算占比，直接按 0 处理，避免除零。
                        displayValue = 0D;
                    }
                }

                double startValue;
                double endValue;
                boolean positive = displayValue > 0;
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

                // 通过正负方向分别判断当前段是否位于外层，从而决定哪一端需要圆角。
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
     * 绘制单根柱体，并按需要控制顶部/底部圆角。
     */
    private void fillBar(Graphics2D g, int x, int y, int barWidth, int barHeight, boolean roundTop, boolean roundBottom) {
        if (barHeight <= 0 || barWidth <= 0) {
            return;
        }
        // 不需要圆角时，直接走矩形填充即可。
        if (barArc <= 0 || (!roundTop && !roundBottom)) {
            g.fillRect(x, y, barWidth, barHeight);
            return;
        }
        if (roundTop && roundBottom) {
            g.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, barArc, barArc));
            return;
        }

        // 只保留一端圆角时，先画圆角矩形，再补一段直角矩形覆盖另一端。
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
     * 统计指定类目下正向或负向的有效堆叠段数量。
     */
    private int countVisibleSegments(int categoryIndex, boolean positive) {
        int count = 0;
        for (BarChartSeries series : seriesList) {
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
     * 计算指定类目下所有正值之和。
     */
    private double getCategoryPositiveTotal(int categoryIndex) {
        double total = 0D;
        for (BarChartSeries series : seriesList) {
            double value = series.getValues().get(categoryIndex);
            if (value > 0) {
                total += value;
            }
        }
        return total;
    }

    /**
     * 计算指定类目下所有负值之和。
     */
    private double getCategoryNegativeTotal(int categoryIndex) {
        double total = 0D;
        for (BarChartSeries series : seriesList) {
            double value = series.getValues().get(categoryIndex);
            if (value < 0) {
                total += value;
            }
        }
        return total;
    }

    /**
     * 将数值映射为绘图区中的 Y 坐标。
     */
    private int valueToY(double value, int plotTop, int plotBottom, ValueRange valueRange) {
        double ratio = (value - valueRange.min) / (valueRange.max - valueRange.min);
        return plotBottom - (int) Math.round(ratio * (plotBottom - plotTop));
    }

    private void drawCenteredLabel(Graphics2D g, Font font, String text, int left, int baselineY, int width) {
        Font targetFont = fitFontToWidth(g, font, text, Math.max(12, width - 4));
        g.setFont(targetFont);
        g.setColor(valueLabelColor);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textX = left + (width - textWidth) / 2;
        g.drawString(text, textX, baselineY);
    }

    /**
     * 根据最大宽度自适应缩小字体，避免文本超出柱体可用宽度。
     */
    private Font fitFontToWidth(Graphics2D g, Font baseFont, String text, int maxWidth) {
        Font font = baseFont;
        FontMetrics metrics = g.getFontMetrics(font);
        if (metrics.stringWidth(text) <= maxWidth) {
            return font;
        }

        // 最小缩小到 8 号字体，避免文本不可读。
        float size = font.getSize2D();
        while (size > 8F) {
            size -= 1F;
            Font candidate = font.deriveFont(size);
            Rectangle2D bounds = g.getFontMetrics(candidate).getStringBounds(text, g);
            if (bounds.getWidth() <= maxWidth) {
                return candidate;
            }
        }
        return font.deriveFont(8F);
    }

    /**
     * 计算堆叠段在其所属正向或负向总量中的占比。
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
     * 绘制堆叠段标签。
     * 优先绘制在柱体内部，空间不足时可退化为外部标签。
     */
    private void drawStackedValueLabel(Graphics2D g, Font valueFont, double rawValue, double percent,
                                       int x, int y, int barWidth, int barHeight, boolean positive, int zeroY) {
        String text = formatStackedValueLabel(rawValue, percent);
        if (text == null || text.isEmpty()) {
            return;
        }
        Font targetFont = fitFontToWidth(g, valueFont, text, Math.max(12, barWidth - 4));
        FontMetrics metrics = g.getFontMetrics(targetFont);
        // 堆叠段过矮时，内部标签会遮挡或溢出，因此按配置转为外部标签。
        if (barHeight < Math.max(minInsideLabelHeight, metrics.getHeight() + 4)) {
            if (showSmallStackLabelOutside) {
                drawExternalStackedValueLabel(g, targetFont, text, x, y, barWidth, barHeight, positive, zeroY);
            }
            return;
        }

        g.setFont(targetFont);
        g.setColor(chooseReadableLabelColor(g.getColor()));
        int textWidth = metrics.stringWidth(text);
        int textX = x + (barWidth - textWidth) / 2;
        int textY = y + (barHeight - metrics.getHeight()) / 2 + metrics.getAscent();
        g.drawString(text, textX, textY);
    }

    /**
     * 为较小的堆叠段绘制外部标签。
     */
    private void drawExternalStackedValueLabel(Graphics2D g, Font font, String text, int x, int y,
                                               int barWidth, int barHeight, boolean positive, int zeroY) {
        Font targetFont = fitFontToWidth(g, font, text, Math.max(16, barWidth + 28));
        g.setFont(targetFont);
        g.setColor(valueLabelColor);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textX = x + (barWidth - textWidth) / 2;
        int textY;
        // 正值标签绘制在段上方，负值标签绘制在段下方。
        if (positive) {
            textY = Math.max(metrics.getAscent(), y - externalLabelGap);
        } else {
            textY = Math.min(zeroY + barHeight + metrics.getAscent() + externalLabelGap,
                    y + barHeight + metrics.getAscent() + externalLabelGap);
        }
        g.drawString(text, textX, textY);
    }

    /**
     * 根据背景色亮度自动选择深色或浅色文字，提升可读性。
     */
    private Color chooseReadableLabelColor(Color background) {
        if (background == null) {
            return Color.WHITE;
        }
        int brightness = (background.getRed() * 299 + background.getGreen() * 587 + background.getBlue() * 114) / 1000;
        return brightness < 150 ? Color.WHITE : new Color(33, 37, 41);
    }

    /**
     * 绘制普通柱状图的数值标签。
     */
    private void drawValueLabel(Graphics2D g, Font valueFont, double value, int x, int y,
                                int barWidth, int barHeight, int zeroY) {
        g.setFont(valueFont);
        g.setColor(valueLabelColor);
        FontMetrics metrics = g.getFontMetrics();
        String text = formatValue(value);
        int textWidth = metrics.stringWidth(text);
        int textX = x + (barWidth - textWidth) / 2;
        int textY;
        // 正值标签放在柱顶上方，负值标签放在柱底下方。
        if (value >= 0) {
            textY = Math.max(metrics.getAscent(), y - 4);
        } else {
            textY = Math.min(zeroY + barHeight + metrics.getAscent() + 4, zeroY + barHeight + metrics.getHeight());
        }
        g.drawString(text, textX, textY);
    }

    /**
     * 绘制 X 轴类目标签。
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
     * 解析系列颜色，优先使用系列自定义颜色，否则回退到默认调色板。
     */
    private Color resolveSeriesColor(BarChartSeries series, int index) {
        return Optional.ofNullable(series.getColor()).orElse(DEFAULT_PALETTE.get(index % DEFAULT_PALETTE.size()));
    }

    /**
     * 解析图表的数值范围。
     * 普通模式按单值范围计算，堆叠模式按类目累计值范围计算。
     */
    private ValueRange resolveValueRange() {
        if (stacked) {
            return resolveStackedValueRange();
        }
        double dataMin = Double.MAX_VALUE;
        double dataMax = -Double.MAX_VALUE;
        for (BarChartSeries series : seriesList) {
            for (Double value : series.getValues()) {
                dataMin = Math.min(dataMin, value);
                dataMax = Math.max(dataMax, value);
            }
        }

        if (dataMin == Double.MAX_VALUE) {
            dataMin = 0D;
            dataMax = 0D;
        }

        // 未显式指定范围时，至少保证 0 在坐标轴可见范围内。
        double finalMin = minValue != null ? minValue : Math.min(0D, dataMin);
        double finalMax = maxValue != null ? maxValue : Math.max(0D, dataMax);
        if (Double.compare(finalMin, finalMax) == 0) {
            // 避免最大值和最小值相同导致后续比例计算除零。
            finalMax = finalMin + 1D;
        }
        return new ValueRange(finalMin, finalMax);
    }

    /**
     * 解析堆叠柱状图的数值范围。
     * 每个类目分别累计正值和负值，再取全局最小/最大。
     */
    private ValueRange resolveStackedValueRange() {
        if (percentStacked) {
            return resolvePercentStackedValueRange();
        }
        double dataMin = Double.MAX_VALUE;
        double dataMax = -Double.MAX_VALUE;
        for (int i = 0; i < categories.size(); i++) {
            double positiveSum = 0D;
            double negativeSum = 0D;
            for (BarChartSeries series : seriesList) {
                double value = series.getValues().get(i);
                if (value >= 0) {
                    positiveSum += value;
                } else {
                    negativeSum += value;
                }
            }
            dataMin = Math.min(dataMin, negativeSum);
            dataMax = Math.max(dataMax, positiveSum);
        }

        if (dataMin == Double.MAX_VALUE) {
            dataMin = 0D;
            dataMax = 0D;
        }

        // 堆叠模式同样默认保证 0 在可见范围内。
        double finalMin = minValue != null ? minValue : Math.min(0D, dataMin);
        double finalMax = maxValue != null ? maxValue : Math.max(0D, dataMax);
        if (Double.compare(finalMin, finalMax) == 0) {
            finalMax = finalMin + 1D;
        }
        return new ValueRange(finalMin, finalMax);
    }

    /**
     * 解析百分比堆叠模式的数值范围。
     * 仅正值时范围为 0~100，仅负值时范围为 -100~0，混合时范围为 -100~100。
     */
    private ValueRange resolvePercentStackedValueRange() {
        boolean hasPositive = false;
        boolean hasNegative = false;
        for (int i = 0; i < categories.size(); i++) {
            if (Double.compare(getCategoryPositiveTotal(i), 0D) > 0) {
                hasPositive = true;
            }
            if (Double.compare(getCategoryNegativeTotal(i), 0D) < 0) {
                hasNegative = true;
            }
        }
        double finalMin = minValue != null ? minValue : (hasNegative ? -100D : 0D);
        double finalMax = maxValue != null ? maxValue : (hasPositive ? 100D : 0D);
        if (Double.compare(finalMin, finalMax) == 0) {
            finalMax = finalMin + 1D;
        }
        return new ValueRange(finalMin, finalMax);
    }

    /**
     * 按当前数值范围生成等距 Y 轴刻度。
     */
    private List<Double> createTicks(ValueRange valueRange) {
        List<Double> ticks = new ArrayList<Double>();
        double step = (valueRange.max - valueRange.min) / (yAxisTickCount - 1);
        for (int i = 0; i < yAxisTickCount; i++) {
            ticks.add(valueRange.min + step * i);
        }
        return ticks;
    }

    /**
     * 普通数值格式化。
     */
    private String formatValue(double value) {
        return decimalFormat.format(value);
    }

    /**
     * 坐标轴数值格式化。
     * 百分比堆叠模式下会自动追加百分号。
     */
    private String formatAxisValue(double value) {
        return percentStacked ? formatValue(value) + "%" : formatValue(value);
    }

    /**
     * 生成堆叠段标签文本。
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

    /**
     * 堆叠标签展示模式。
     */
    @Getter
    @AllArgsConstructor
    public enum StackLabelMode {
        VALUE("数值"),
        PERCENT("百分比"),
        VALUE_PERCENT("数值（百分比）");
        private String desc;
    }

    /**
     * 数值范围对象，保存渲染时使用的最小值与最大值。
     */
    private static class ValueRange {
        /**
         * 当前渲染范围的最小值。
         */
        private final double min;
        /**
         * 当前渲染范围的最大值。
         */
        private final double max;

        private ValueRange(double min, double max) {
            this.min = min;
            this.max = max;
        }
    }
}
