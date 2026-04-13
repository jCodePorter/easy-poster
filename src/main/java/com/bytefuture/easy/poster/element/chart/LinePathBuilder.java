package com.bytefuture.easy.poster.element.chart;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * 折线路径构建器
 *
 * @author biaoy
 * @since 2026/04/13
 */
public interface LinePathBuilder {

    Shape buildPath(List<Point2D.Double> points, double tension);
}
