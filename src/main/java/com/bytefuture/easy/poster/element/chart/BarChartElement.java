package com.bytefuture.easy.poster.element.chart;

import com.bytefuture.easy.poster.element.AbstractDimensionElement;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 柱状图
 *
 * @author biaoy
 * @since 2026/04/11
 */
public class BarChartElement extends AbstractDimensionElement<BarChartElement> {

    private static final List<Color> DEFAULT_PALETTE = Arrays.asList(
            new Color(72, 133, 237),
            new Color(234, 67, 53),
            new Color(52, 168, 83),
            new Color(251, 188, 5),
            new Color(123, 97, 255),
            new Color(0, 172, 193)
    );

    private final List<String> categories = new ArrayList<String>();

    private final List<BarChartSeries> seriesList = new ArrayList<BarChartSeries>();
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");
    private Insets padding = new Insets(24, 24, 24, 24);
    private Color backgroundColor;
    private Color axisColor = new Color(85, 92, 110);
    private Color gridColor = new Color(225, 229, 238);
    private Color labelColor = new Color(71, 77, 92);
    private Color valueLabelColor = new Color(55, 60, 72);
    private String title;
    private boolean showLegend = true;
    private boolean showGrid = true;
    private boolean showValueLabel = true;
    private boolean showAxis = true;
    private boolean showTitle = true;
    private boolean stacked = false;
    private boolean percentStacked = false;
    private boolean showStackTotalLabel = false;
    private int yAxisTickCount = 5;
    private int axisStrokeWidth = 1;
    private int titleFontSize = 18;
    private int labelFontSize = 12;
    private int legendFontSize = 12;
    private int valueLabelFontSize = 11;
    private int legendItemGap = 18;
    private int legendMarkerSize = 10;
    private int maxBarWidth = 56;
    private int minBarWidth = 6;
    private int barArc = 8;
    private int stackTotalLabelGap = 6;
    private int externalLabelGap = 4;
    private int minInsideLabelHeight = 18;
    private StackLabelMode stackLabelMode = StackLabelMode.VALUE_PERCENT;
    private boolean showSmallStackLabelOutside = true;
    private double categoryGapRatio = 0.24D;
    private double barGapRatio = 0.18D;
    private Double minValue;
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

    public BarChartElement setStacked(boolean stacked) {
        this.stacked = stacked;
        if (stacked) {
            this.showStackTotalLabel = false;
        }
        return this;
    }

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

    public BarChartElement setValueRange(Double minValue, Double maxValue) {
        if (minValue != null && maxValue != null && minValue >= maxValue) {
            throw new PosterException("minValue must be less than maxValue");
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
        return this;
    }

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

            ValueRange valueRange = resolveValueRange();
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

    @Override
    public void beforeRender(PosterContext context) {
        super.beforeRender(context);
    }

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
            String text = Optional.ofNullable(series.getName()).orElse("绯诲垪" + (i + 1));
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

    private int calcYAxisLabelWidth(FontMetrics metrics, List<Double> ticks) {
        int max = 0;
        for (Double tick : ticks) {
            max = Math.max(max, metrics.stringWidth(formatValue(tick)));
        }
        return max;
    }

    private void drawGridAndAxis(Graphics2D g, int plotLeft, int plotTop, int plotRight, int plotBottom,
                                 int zeroY, List<Double> ticks, ValueRange valueRange, Font labelFont) {
        g.setFont(labelFont);
        FontMetrics metrics = g.getFontMetrics();
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(axisStrokeWidth));
        for (Double tick : ticks) {
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
        if (showAxis) {
            g.setColor(axisColor);
            g.drawLine(plotLeft, plotTop, plotLeft, plotBottom);
            g.drawLine(plotLeft, zeroY, plotRight, zeroY);
        }
        g.setStroke(oldStroke);
    }

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

    private void drawBars(Graphics2D g, int plotLeft, int plotTop, int plotRight, int plotBottom, int zeroY,
                          int plotWidth, int plotHeight, ValueRange valueRange, Font valueFont) {
        if (stacked) {
            drawStackedBars(g, plotLeft, plotTop, plotBottom, zeroY, plotWidth, plotHeight, valueRange, valueFont);
            return;
        }
        drawGroupedBars(g, plotLeft, plotBottom, zeroY, plotWidth, plotHeight, valueRange, valueFont);
    }

    private void drawGroupedBars(Graphics2D g, int plotLeft, int plotBottom, int zeroY,
                                 int plotWidth, int plotHeight, ValueRange valueRange, Font valueFont) {
        int categoryCount = categories.size();
        int seriesCount = seriesList.size();
        double categoryWidth = (double) plotWidth / categoryCount;
        double categoryGap = Math.max(4D, categoryWidth * categoryGapRatio);
        double groupWidth = categoryWidth - categoryGap;
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
                int y = value >= 0 ? zeroY - barHeight : zeroY;

                g.setColor(resolveSeriesColor(series, seriesIndex));
                fillBar(g, x, y, barWidth, barHeight, true, true);

                if (showValueLabel) {
                    drawValueLabel(g, valueFont, value, x, y, barWidth, barHeight, zeroY);
                }
            }
        }
    }

    private void drawStackedBars(Graphics2D g, int plotLeft, int plotTop, int plotBottom, int zeroY,
                                 int plotWidth, int plotHeight, ValueRange valueRange, Font valueFont) {
        int categoryCount = categories.size();
        double categoryWidth = (double) plotWidth / categoryCount;
        double categoryGap = Math.max(4D, categoryWidth * categoryGapRatio);
        double barWidthDouble = Math.max(minBarWidth, Math.min(maxBarWidth, categoryWidth - categoryGap));

        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            int barWidth = Math.max(1, (int) Math.round(barWidthDouble));
            int x = (int) Math.round(plotLeft + categoryIndex * categoryWidth + (categoryWidth - barWidthDouble) / 2D);
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
                if (Double.compare(value, 0D) == 0) {
                    continue;
                }
                double displayValue = value;
                if (percentStacked) {
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

    private Font fitFontToWidth(Graphics2D g, Font baseFont, String text, int maxWidth) {
        Font font = baseFont;
        FontMetrics metrics = g.getFontMetrics(font);
        if (metrics.stringWidth(text) <= maxWidth) {
            return font;
        }

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

    private void drawStackedValueLabel(Graphics2D g, Font valueFont, double rawValue, double percent,
                                       int x, int y, int barWidth, int barHeight, boolean positive, int zeroY) {
        String text = formatStackedValueLabel(rawValue, percent);
        if (text == null || text.isEmpty()) {
            return;
        }
        Font targetFont = fitFontToWidth(g, valueFont, text, Math.max(12, barWidth - 4));
        FontMetrics metrics = g.getFontMetrics(targetFont);
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

    private void drawExternalStackedValueLabel(Graphics2D g, Font font, String text, int x, int y,
                                               int barWidth, int barHeight, boolean positive, int zeroY) {
        Font targetFont = fitFontToWidth(g, font, text, Math.max(16, barWidth + 28));
        g.setFont(targetFont);
        g.setColor(valueLabelColor);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textX = x + (barWidth - textWidth) / 2;
        int textY;
        if (positive) {
            textY = Math.max(metrics.getAscent(), y - externalLabelGap);
        } else {
            textY = Math.min(zeroY + barHeight + metrics.getAscent() + externalLabelGap,
                    y + barHeight + metrics.getAscent() + externalLabelGap);
        }
        g.drawString(text, textX, textY);
    }

    private Color chooseReadableLabelColor(Color background) {
        if (background == null) {
            return Color.WHITE;
        }
        int brightness = (background.getRed() * 299 + background.getGreen() * 587 + background.getBlue() * 114) / 1000;
        return brightness < 150 ? Color.WHITE : new Color(33, 37, 41);
    }

    private void drawValueLabel(Graphics2D g, Font valueFont, double value, int x, int y,
                                int barWidth, int barHeight, int zeroY) {
        g.setFont(valueFont);
        g.setColor(valueLabelColor);
        FontMetrics metrics = g.getFontMetrics();
        String text = formatValue(value);
        int textWidth = metrics.stringWidth(text);
        int textX = x + (barWidth - textWidth) / 2;
        int textY;
        if (value >= 0) {
            textY = Math.max(metrics.getAscent(), y - 4);
        } else {
            textY = Math.min(zeroY + barHeight + metrics.getAscent() + 4, zeroY + barHeight + metrics.getHeight());
        }
        g.drawString(text, textX, textY);
    }

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

    private Color resolveSeriesColor(BarChartSeries series, int index) {
        return Optional.ofNullable(series.getColor()).orElse(DEFAULT_PALETTE.get(index % DEFAULT_PALETTE.size()));
    }

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

        double finalMin = minValue != null ? minValue : Math.min(0D, dataMin);
        double finalMax = maxValue != null ? maxValue : Math.max(0D, dataMax);
        if (Double.compare(finalMin, finalMax) == 0) {
            finalMax = finalMin + 1D;
        }
        return new ValueRange(finalMin, finalMax);
    }

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

        double finalMin = minValue != null ? minValue : Math.min(0D, dataMin);
        double finalMax = maxValue != null ? maxValue : Math.max(0D, dataMax);
        if (Double.compare(finalMin, finalMax) == 0) {
            finalMax = finalMin + 1D;
        }
        return new ValueRange(finalMin, finalMax);
    }

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

    private List<Double> createTicks(ValueRange valueRange) {
        List<Double> ticks = new ArrayList<Double>();
        double step = (valueRange.max - valueRange.min) / (yAxisTickCount - 1);
        for (int i = 0; i < yAxisTickCount; i++) {
            ticks.add(valueRange.min + step * i);
        }
        return ticks;
    }

    private String formatValue(double value) {
        return decimalFormat.format(value);
    }

    private String formatAxisValue(double value) {
        return percentStacked ? formatValue(value) + "%" : formatValue(value);
    }

    private String formatStackedValueLabel(double rawValue, double percent) {
        if (stackLabelMode == StackLabelMode.VALUE) {
            return formatValue(rawValue);
        }
        if (stackLabelMode == StackLabelMode.PERCENT) {
            return formatValue(percent) + "%";
        }
        return formatValue(rawValue) + "(" + formatValue(percent) + "%)";
    }

    public enum StackLabelMode {
        VALUE,
        PERCENT,
        VALUE_PERCENT
    }

    private static class ValueRange {
        private final double min;
        private final double max;

        private ValueRange(double min, double max) {
            this.min = min;
            this.max = max;
        }
    }
}
