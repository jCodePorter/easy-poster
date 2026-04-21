package com.bytefuture.easy.poster.element.chart.bar;

import com.bytefuture.easy.poster.element.chart.BarChartSeries;
import com.bytefuture.easy.poster.element.chart.base.ChartValueRange;

import java.util.List;

/**
 * Resolves the numeric domain used by bar charts across grouped, stacked and percent-stacked modes.
 */
public class BarChartRangeResolver {

    public ChartValueRange resolve(List<String> categories, List<BarChartSeries> seriesList,
                                   boolean stacked, boolean percentStacked,
                                   Double minValue, Double maxValue) {
        if (stacked) {
            return resolveStackedRange(categories, seriesList, percentStacked, minValue, maxValue);
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
        return createRange(minValue, maxValue, dataMin, dataMax);
    }

    private ChartValueRange resolveStackedRange(List<String> categories, List<BarChartSeries> seriesList,
                                                boolean percentStacked, Double minValue, Double maxValue) {
        if (percentStacked) {
            return resolvePercentStackedRange(categories, seriesList, minValue, maxValue);
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
        return createRange(minValue, maxValue, dataMin, dataMax);
    }

    private ChartValueRange resolvePercentStackedRange(List<String> categories, List<BarChartSeries> seriesList,
                                                       Double minValue, Double maxValue) {
        boolean hasPositive = false;
        boolean hasNegative = false;
        for (int i = 0; i < categories.size(); i++) {
            if (Double.compare(getCategoryPositiveTotal(seriesList, i), 0D) > 0) {
                hasPositive = true;
            }
            if (Double.compare(getCategoryNegativeTotal(seriesList, i), 0D) < 0) {
                hasNegative = true;
            }
        }

        double finalMin = minValue != null ? minValue : (hasNegative ? -100D : 0D);
        double finalMax = maxValue != null ? maxValue : (hasPositive ? 100D : 0D);
        if (Double.compare(finalMin, finalMax) == 0) {
            finalMax = finalMin + 1D;
        }
        return new ChartValueRange(finalMin, finalMax);
    }

    private double getCategoryPositiveTotal(List<BarChartSeries> seriesList, int categoryIndex) {
        double total = 0D;
        for (BarChartSeries series : seriesList) {
            double value = series.getValues().get(categoryIndex);
            if (value > 0) {
                total += value;
            }
        }
        return total;
    }

    private double getCategoryNegativeTotal(List<BarChartSeries> seriesList, int categoryIndex) {
        double total = 0D;
        for (BarChartSeries series : seriesList) {
            double value = series.getValues().get(categoryIndex);
            if (value < 0) {
                total += value;
            }
        }
        return total;
    }

    private ChartValueRange createRange(Double minValue, Double maxValue, double dataMin, double dataMax) {
        double finalMin = minValue != null ? minValue : Math.min(0D, dataMin);
        double finalMax = maxValue != null ? maxValue : Math.max(0D, dataMax);
        if (Double.compare(finalMin, finalMax) == 0) {
            finalMax = finalMin + 1D;
        }
        return new ChartValueRange(finalMin, finalMax);
    }
}
