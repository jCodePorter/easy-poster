package com.bytefuture.easy.poster.element.chart;

/**
 * 折线路径构建器工厂
 *
 * @author biaoy
 * @since 2026/04/13
 */
public class LinePathBuilderFactory {

    private static final LinePathBuilder STRAIGHT = new StraightLinePathBuilder();

    private static final LinePathBuilder BEZIER = new SmoothLinePathBuilder();

    private static final LinePathBuilder MONOTONE = new MonotoneSmoothLinePathBuilder();

    public LinePathBuilder resolve(double smoothTension, LineChartElement.SmoothAlgorithm smoothAlgorithm) {
        if (smoothTension <= 0D) {
            return STRAIGHT;
        }
        if (smoothAlgorithm == LineChartElement.SmoothAlgorithm.MONOTONE) {
            return MONOTONE;
        }
        return BEZIER;
    }
}
