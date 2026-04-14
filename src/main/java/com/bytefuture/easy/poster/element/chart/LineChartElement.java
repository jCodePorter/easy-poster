package com.bytefuture.easy.poster.element.chart;

import com.bytefuture.easy.poster.element.AbstractDimensionElement;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 折线图元素
 *
 * @author biaoy
 * @since 2026/04/13
 */
public class LineChartElement extends AbstractDimensionElement<LineChartElement> {
    private static final LinePathBuilderFactory LINE_PATH_BUILDER_FACTORY = new LinePathBuilderFactory();

    private static final List<Color> DEFAULT_PALETTE = Arrays.asList(
            new Color(72, 133, 237),
            new Color(234, 67, 53),
            new Color(52, 168, 83),
            new Color(251, 188, 5),
            new Color(123, 97, 255),
            new Color(0, 172, 193)
    );

    private final List<String> categories = new ArrayList<String>();

    private final List<LineChartSeries> seriesList = new ArrayList<LineChartSeries>();

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

    private int yAxisTickCount = 5;

    private int axisStrokeWidth = 1;

    private int lineStrokeWidth = 3;

    private int markerRadius = 4;

    private int titleFontSize = 18;

    private int labelFontSize = 12;

    private int legendFontSize = 12;

    private int valueLabelFontSize = 11;

    private int legendItemGap = 18;

    private int legendMarkerSize = 10;

    private SmoothAlgorithm smoothAlgorithm = SmoothAlgorithm.BEZIER;

    private double smoothTension = 0D;

    private Double minValue;

    private Double maxValue;

    public LineChartElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public LineChartElement setTitle(String title) {
        this.title = title;
        return this;
    }

    public LineChartElement setPadding(Insets padding) {
        if (padding == null) {
            throw new PosterException("padding can not be null");
        }
        this.padding = padding;
        return this;
    }

    public LineChartElement setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public LineChartElement setAxisColor(Color axisColor) {
        this.axisColor = axisColor;
        return this;
    }

    public LineChartElement setGridColor(Color gridColor) {
        this.gridColor = gridColor;
        return this;
    }

    public LineChartElement setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
        return this;
    }

    public LineChartElement setValueLabelColor(Color valueLabelColor) {
        this.valueLabelColor = valueLabelColor;
        return this;
    }

    public LineChartElement setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
        return this;
    }

    public LineChartElement setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        return this;
    }

    public LineChartElement setShowValueLabel(boolean showValueLabel) {
        this.showValueLabel = showValueLabel;
        return this;
    }

    public LineChartElement setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        return this;
    }

    public LineChartElement setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    public LineChartElement setYAxisTickCount(int yAxisTickCount) {
        if (yAxisTickCount < 2) {
            throw new PosterException("yAxisTickCount must be greater than or equal to 2");
        }
        this.yAxisTickCount = yAxisTickCount;
        return this;
    }

    public LineChartElement setAxisStrokeWidth(int axisStrokeWidth) {
        this.axisStrokeWidth = Math.max(1, axisStrokeWidth);
        return this;
    }

    public LineChartElement setLineStrokeWidth(int lineStrokeWidth) {
        this.lineStrokeWidth = Math.max(1, lineStrokeWidth);
        return this;
    }

    public LineChartElement setMarkerRadius(int markerRadius) {
        this.markerRadius = Math.max(1, markerRadius);
        return this;
    }

    public LineChartElement setTitleFontSize(int titleFontSize) {
        this.titleFontSize = Math.max(1, titleFontSize);
        return this;
    }

    public LineChartElement setLabelFontSize(int labelFontSize) {
        this.labelFontSize = Math.max(1, labelFontSize);
        return this;
    }

    public LineChartElement setLegendFontSize(int legendFontSize) {
        this.legendFontSize = Math.max(1, legendFontSize);
        return this;
    }

    public LineChartElement setValueLabelFontSize(int valueLabelFontSize) {
        this.valueLabelFontSize = Math.max(1, valueLabelFontSize);
        return this;
    }

    public LineChartElement setLegendItemGap(int legendItemGap) {
        this.legendItemGap = Math.max(0, legendItemGap);
        return this;
    }

    public LineChartElement setLegendMarkerSize(int legendMarkerSize) {
        this.legendMarkerSize = Math.max(1, legendMarkerSize);
        return this;
    }

    public LineChartElement setSmoothAlgorithm(SmoothAlgorithm smoothAlgorithm) {
        if (smoothAlgorithm == null) {
            throw new PosterException("smoothAlgorithm can not be null");
        }
        this.smoothAlgorithm = smoothAlgorithm;
        return this;
    }

    public LineChartElement setSmoothTension(double smoothTension) {
        if (smoothTension < 0D || smoothTension > 1D) {
            throw new PosterException("smoothTension must be between 0 and 1");
        }
        this.smoothTension = smoothTension;
        return this;
    }

    public LineChartElement setValueRange(Double minValue, Double maxValue) {
        if (minValue != null && maxValue != null && minValue >= maxValue) {
            throw new PosterException("minValue must be less than maxValue");
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
        return this;
    }

    public LineChartElement setCategories(List<String> categories) {
        this.categories.clear();
        if (categories != null) {
            this.categories.addAll(categories);
        }
        return this;
    }

    public LineChartElement addCategory(String category) {
        this.categories.add(category);
        return this;
    }

    public LineChartElement addSeries(LineChartSeries series) {
        if (series != null) {
            this.seriesList.add(series);
        }
        return this;
    }

    public LineChartElement addSeries(String name, List<? extends Number> values) {
        return addSeries(LineChartSeries.of(name, values));
    }

    public LineChartElement addSeries(String name, List<? extends Number> values, Color color) {
        return addSeries(LineChartSeries.of(name, values).setColor(color));
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
            Font titleFont = baseFont.deriveFont(Font.BOLD, (float) titleFontSize);
            Font labelFont = baseFont.deriveFont(Font.PLAIN, (float) labelFontSize);
            Font legendFont = baseFont.deriveFont(Font.PLAIN, (float) legendFontSize);
            Font valueFont = baseFont.deriveFont(Font.PLAIN, (float) valueLabelFontSize);
            ValueRange valueRange = resolveValueRange();

            int innerLeft = origin.getX() + padding.left;
            int innerTop = origin.getY() + padding.top;
            int innerRight = origin.getX() + width - padding.right;
            int innerBottom = origin.getY() + height - padding.bottom;

            innerTop += drawTitle(g, innerLeft, innerTop, titleFont);
            innerTop += drawLegend(g, innerLeft, innerTop, innerRight, legendFont);

            List<Double> ticks = createTicks(valueRange);
            g.setFont(labelFont);
            FontMetrics labelMetrics = g.getFontMetrics();
            int yAxisLabelWidth = calcYAxisLabelWidth(labelMetrics, ticks);
            int xAxisLabelHeight = labelMetrics.getHeight() + 10;

            int plotLeft = innerLeft + yAxisLabelWidth + 10;
            int plotTop = innerTop + 4;
            int plotRight = innerRight;
            int plotBottom = innerBottom - xAxisLabelHeight;
            int plotWidth = plotRight - plotLeft;
            int plotHeight = plotBottom - plotTop;
            if (plotWidth <= 0 || plotHeight <= 0) {
                throw new PosterException("chart drawable area is too small");
            }

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int axisY = resolveAxisY(plotTop, plotBottom, valueRange);

            drawGridAndAxis(g, plotLeft, plotTop, plotRight, plotBottom, ticks, valueRange, labelFont, axisY);
            drawLines(g, plotLeft, plotTop, plotWidth, plotBottom, valueRange, valueFont);
            drawXAxisLabels(g, plotLeft, plotBottom, plotWidth, labelFont);
        } finally {
            g.dispose();
        }
        return dimension.getPoint();
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
        for (LineChartSeries series : seriesList) {
            if (series.getValues().size() != categories.size()) {
                throw new PosterException("series value size must match category size");
            }
        }
    }

    private int drawTitle(Graphics2D g, int left, int top, Font titleFont) {
        if (!showTitle || title == null || title.trim().isEmpty()) {
            return 0;
        }
        g.setFont(titleFont);
        g.setColor(labelColor);
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(title.trim(), left, top + metrics.getAscent());
        return metrics.getHeight() + 10;
    }

    private int drawLegend(Graphics2D g, int left, int top, int right, Font legendFont) {
        if (!showLegend) {
            return 0;
        }
        g.setFont(legendFont);
        FontMetrics metrics = g.getFontMetrics();
        int x = left;
        int y = top;
        int rowHeight = Math.max(metrics.getHeight(), legendMarkerSize) + 6;
        int rows = 1;
        for (int i = 0; i < seriesList.size(); i++) {
            LineChartSeries series = seriesList.get(i);
            String text = Optional.ofNullable(series.getName()).orElse("");
            int itemWidth = legendMarkerSize + 8 + metrics.stringWidth(text) + legendItemGap;
            if (x > left && x + itemWidth > right) {
                rows++;
                x = left;
                y += rowHeight;
            }
            int baseline = y + metrics.getAscent();
            int markerY = baseline - metrics.getAscent() + Math.max(0, (metrics.getHeight() - legendMarkerSize) / 2);
            g.setColor(resolveSeriesColor(series, i));
            g.fillRoundRect(x, markerY, legendMarkerSize, legendMarkerSize, 4, 4);
            g.setColor(labelColor);
            g.drawString(text, x + legendMarkerSize + 8, baseline);
            x += itemWidth;
        }
        return rows * rowHeight;
    }

    private int calcYAxisLabelWidth(FontMetrics metrics, List<Double> ticks) {
        int maxWidth = 0;
        for (Double tick : ticks) {
            maxWidth = Math.max(maxWidth, metrics.stringWidth(formatValue(tick)));
        }
        return maxWidth;
    }

    private void drawGridAndAxis(Graphics2D g, int plotLeft, int plotTop, int plotRight, int plotBottom,
                                 List<Double> ticks, ValueRange valueRange, Font labelFont, int axisY) {
        g.setFont(labelFont);
        FontMetrics metrics = g.getFontMetrics();
        for (Double tick : ticks) {
            int y = valueToY(tick, plotTop, plotBottom, valueRange);
            if (showGrid) {
                g.setColor(gridColor);
                g.drawLine(plotLeft, y, plotRight, y);
            }
            g.setColor(labelColor);
            String text = formatValue(tick);
            int textWidth = metrics.stringWidth(text);
            g.drawString(text, plotLeft - textWidth - 10, y + metrics.getAscent() / 2 - 2);
        }

        if (!showAxis) {
            return;
        }
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(axisStrokeWidth));
        g.setColor(axisColor);
        g.drawLine(plotLeft, plotTop, plotLeft, plotBottom);
        g.drawLine(plotLeft, axisY, plotRight, axisY);
        g.setStroke(oldStroke);
    }

    private void drawLines(Graphics2D g, int plotLeft, int plotTop, int plotWidth, int plotBottom,
                           ValueRange valueRange, Font valueFont) {
        Stroke oldStroke = g.getStroke();
        LinePathBuilder pathBuilder = LINE_PATH_BUILDER_FACTORY.resolve(smoothTension, smoothAlgorithm);
        for (int seriesIndex = 0; seriesIndex < seriesList.size(); seriesIndex++) {
            LineChartSeries series = seriesList.get(seriesIndex);
            Color color = resolveSeriesColor(series, seriesIndex);
            List<Point2D.Double> points = new ArrayList<Point2D.Double>();
            int[] xPoints = new int[categories.size()];
            int[] yPoints = new int[categories.size()];
            for (int i = 0; i < categories.size(); i++) {
                xPoints[i] = resolvePointX(plotLeft, plotWidth, i);
                yPoints[i] = valueToY(series.getValues().get(i), plotTop, plotBottom, valueRange);
                points.add(new Point2D.Double(xPoints[i], yPoints[i]));
            }

            g.setColor(color);
            g.setStroke(new BasicStroke(lineStrokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(pathBuilder.buildPath(points, smoothTension));

            for (int i = 0; i < xPoints.length; i++) {
                drawMarker(g, color, xPoints[i], yPoints[i]);
                if (showValueLabel) {
                    drawValueLabel(g, valueFont, series.getValues().get(i), xPoints[i], yPoints[i]);
                }
            }
        }
        g.setStroke(oldStroke);
    }

    private void drawMarker(Graphics2D g, Color color, int centerX, int centerY) {
        int diameter = markerRadius * 2;
        g.setColor(Color.WHITE);
        g.fill(new Ellipse2D.Double(centerX - markerRadius, centerY - markerRadius, diameter, diameter));
        g.setColor(color);
        g.fill(new Ellipse2D.Double(centerX - markerRadius + 1, centerY - markerRadius + 1,
                Math.max(1, diameter - 2), Math.max(1, diameter - 2)));
    }

    private void drawValueLabel(Graphics2D g, Font valueFont, double value, int centerX, int centerY) {
        g.setFont(valueFont);
        g.setColor(valueLabelColor);
        FontMetrics metrics = g.getFontMetrics();
        String text = formatValue(value);
        int textWidth = metrics.stringWidth(text);
        int x = centerX - textWidth / 2;
        int y = value >= 0 ? centerY - markerRadius - 6 : centerY + markerRadius + metrics.getAscent() + 2;
        g.drawString(text, x, y);
    }

    private void drawXAxisLabels(Graphics2D g, int plotLeft, int plotBottom, int plotWidth, Font labelFont) {
        g.setFont(labelFont);
        g.setColor(labelColor);
        FontMetrics metrics = g.getFontMetrics();
        for (int i = 0; i < categories.size(); i++) {
            String category = Optional.ofNullable(categories.get(i)).orElse("");
            int textWidth = metrics.stringWidth(category);
            int x = resolvePointX(plotLeft, plotWidth, i) - textWidth / 2;
            int y = plotBottom + metrics.getAscent() + 6;
            g.drawString(category, x, y);
        }
    }

    private int resolvePointX(int plotLeft, int plotWidth, int index) {
        if (categories.size() == 1) {
            return plotLeft + plotWidth / 2;
        }
        double step = (double) plotWidth / (categories.size() - 1);
        return (int) Math.round(plotLeft + step * index);
    }

    private Color resolveSeriesColor(LineChartSeries series, int index) {
        return Optional.ofNullable(series.getColor()).orElse(DEFAULT_PALETTE.get(index % DEFAULT_PALETTE.size()));
    }

    private ValueRange resolveValueRange() {
        double dataMin = Double.MAX_VALUE;
        double dataMax = -Double.MAX_VALUE;
        for (LineChartSeries series : seriesList) {
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

    private List<Double> createTicks(ValueRange valueRange) {
        List<Double> ticks = new ArrayList<Double>();
        double step = (valueRange.max - valueRange.min) / (yAxisTickCount - 1);
        for (int i = 0; i < yAxisTickCount; i++) {
            ticks.add(valueRange.min + step * i);
        }
        return ticks;
    }

    /**
     * 计算横轴在绘图区中的 Y 坐标；当数据整体全为正数或全为负数时，横轴贴边绘制。
     */
    private int resolveAxisY(int plotTop, int plotBottom, ValueRange valueRange) {
        if (valueRange.min > 0D) {
            return plotBottom;
        }
        if (valueRange.max < 0D) {
            return plotTop;
        }
        return valueToY(0D, plotTop, plotBottom, valueRange);
    }

    private int valueToY(double value, int plotTop, int plotBottom, ValueRange valueRange) {
        double ratio = (value - valueRange.min) / (valueRange.max - valueRange.min);
        return plotBottom - (int) Math.round(ratio * (plotBottom - plotTop));
    }

    private String formatValue(double value) {
        return decimalFormat.format(value);
    }

    private static class ValueRange {
        private final double min;
        private final double max;

        private ValueRange(double min, double max) {
            this.min = min;
            this.max = max;
        }
    }

    public enum SmoothAlgorithm {
        BEZIER,
        MONOTONE
    }
}
