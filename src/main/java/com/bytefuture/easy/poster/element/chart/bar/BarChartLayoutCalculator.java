package com.bytefuture.easy.poster.element.chart.bar;

/**
 * Resolves reusable grouped/stacked bar layout metrics from the plot area.
 */
public class BarChartLayoutCalculator {

    public GroupedLayout calculateGrouped(int plotLeft, int plotWidth, int categoryCount, int seriesCount,
                                          double categoryGapRatio, double barGapRatio,
                                          int minBarWidth, int maxBarWidth) {
        double categoryWidth = (double) plotWidth / categoryCount;
        double categoryGap = Math.max(4D, categoryWidth * categoryGapRatio);
        double groupWidth = categoryWidth - categoryGap;
        if (groupWidth <= 0) {
            groupWidth = categoryWidth * 0.8D;
        }

        double computedBarWidth = groupWidth / (seriesCount + Math.max(0, seriesCount - 1) * barGapRatio);
        computedBarWidth = Math.max(minBarWidth, Math.min(maxBarWidth, computedBarWidth));
        double actualGroupWidth = computedBarWidth * (seriesCount + Math.max(0, seriesCount - 1) * barGapRatio);
        double barGap = seriesCount <= 1 ? 0D : computedBarWidth * barGapRatio;
        return new GroupedLayout(plotLeft, categoryWidth, computedBarWidth, barGap, actualGroupWidth);
    }

    public StackedLayout calculateStacked(int plotLeft, int plotWidth, int categoryCount,
                                          double categoryGapRatio, int minBarWidth, int maxBarWidth) {
        double categoryWidth = (double) plotWidth / categoryCount;
        double categoryGap = Math.max(4D, categoryWidth * categoryGapRatio);
        double barWidth = Math.max(minBarWidth, Math.min(maxBarWidth, categoryWidth - categoryGap));
        return new StackedLayout(plotLeft, categoryWidth, barWidth);
    }

    public static class GroupedLayout {

        private final int plotLeft;

        private final double categoryWidth;

        private final double barWidth;

        private final double barGap;

        private final double actualGroupWidth;

        GroupedLayout(int plotLeft, double categoryWidth, double barWidth, double barGap, double actualGroupWidth) {
            this.plotLeft = plotLeft;
            this.categoryWidth = categoryWidth;
            this.barWidth = barWidth;
            this.barGap = barGap;
            this.actualGroupWidth = actualGroupWidth;
        }

        public double getCategoryWidth() {
            return categoryWidth;
        }

        public int getBarWidth() {
            return Math.max(1, (int) Math.round(barWidth));
        }

        public double getBarGap() {
            return barGap;
        }

        public int resolveBarX(int categoryIndex, int seriesIndex) {
            double groupStart = plotLeft + categoryWidth * categoryIndex + (categoryWidth - actualGroupWidth) / 2D;
            return (int) Math.round(groupStart + seriesIndex * (barWidth + barGap));
        }
    }

    public static class StackedLayout {

        private final int plotLeft;

        private final double categoryWidth;

        private final double barWidth;

        StackedLayout(int plotLeft, double categoryWidth, double barWidth) {
            this.plotLeft = plotLeft;
            this.categoryWidth = categoryWidth;
            this.barWidth = barWidth;
        }

        public double getCategoryWidth() {
            return categoryWidth;
        }

        public int getBarWidth() {
            return Math.max(1, (int) Math.round(barWidth));
        }

        public int resolveBarX(int categoryIndex) {
            return (int) Math.round(plotLeft + categoryWidth * categoryIndex + (categoryWidth - barWidth) / 2D);
        }
    }
}
