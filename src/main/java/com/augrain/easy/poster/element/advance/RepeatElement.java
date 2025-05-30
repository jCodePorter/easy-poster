package com.augrain.easy.poster.element.advance;

import com.augrain.easy.poster.element.AbstractRepeatableElement;
import com.augrain.easy.poster.element.IElement;
import com.augrain.easy.poster.geometry.Dimension;
import com.augrain.easy.poster.geometry.*;
import com.augrain.easy.poster.model.PosterContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 平铺元素，可以组合基础元素，部分高级元素（部分特性）
 *
 * @author biaoy
 * @since 2025/03/05
 */
public class RepeatElement implements IElement {

    /**
     * 待平铺的图片
     */
    private final AbstractRepeatableElement basicElement;

    /**
     * 水平间隔
     */
    private int xPadding = 0;

    /**
     * 垂直间隔
     */
    private int yPadding = 0;

    /**
     * 自定义布局
     */
    private Layout layout;

    public RepeatElement(AbstractRepeatableElement basicElement) {
        this.basicElement = basicElement;
    }

    public RepeatElement setXPadding(int xPadding) {
        this.xPadding = xPadding;
        return this;
    }

    public RepeatElement setYPadding(int yPadding) {
        this.yPadding = yPadding;
        return this;
    }

    public RepeatElement setPadding(int xPadding, int yPadding) {
        this.xPadding = xPadding;
        this.yPadding = yPadding;
        return this;
    }

    public RepeatElement setLayout(int rows, int cols) {
        this.layout = new Layout(rows, cols, null);
        return this;
    }

    public RepeatElement setLayout(int rows, int cols, Margin margin) {
        this.layout = new Layout(rows, cols, margin);
        return this;
    }

    @Override
    public Point render(PosterContext context, int posterWidth, int posterHeight) {
        basicElement.beforeRender(context);

        Dimension dimension = basicElement.calculateDimension(context, posterWidth, posterHeight);
        int elementWidth = Math.max(dimension.getRotateWidth(), dimension.getWidth());
        int elementHeight = Math.max(dimension.getRotateHeight(), dimension.getHeight());

        return doRepeat(context, posterWidth, posterHeight, elementWidth, elementHeight, dimension);
    }

    private Point doRepeat(PosterContext context, int posterWidth, int posterHeight, int elementWidth, int elementHeight, Dimension dimension) {
        RepeatConfig result = getRepeatConfig(posterWidth, posterHeight, elementWidth, elementHeight, dimension);

        for (int j = 0; j < result.cols; j++) {
            for (int i = 0; i < result.rows; i++) {
                int x = i * (elementWidth + result.xPadding) + result.xOffset;
                int y = j * (elementHeight + result.yPadding) + result.yOffset;

                Margin elementMargin = Margin.of().setMarginLeft(x).setMarginTop(y);
                basicElement.setPosition(RelativePosition.of(Direction.TOP_LEFT, elementMargin));
                Point coordinatePoint = basicElement.reCalculatePosition(posterWidth, posterHeight, dimension);
                dimension.setPoint(coordinatePoint);
                basicElement.doRender(context, dimension, posterWidth, posterHeight);
            }
        }
        return Point.ORIGIN_COORDINATE;
    }

    private RepeatConfig getRepeatConfig(int posterWidth, int posterHeight, int elementWidth, int elementHeight, Dimension dimension) {
        if (this.layout != null) {
            Margin margin = this.layout.getMargin();

            int xInterval;
            int yInterval;
            int xOffset;
            int yOffset;
            if (margin != null) {
                int visualWidth = posterWidth - margin.getMarginLeft() - margin.getMarginRight();
                int visualHeight = posterHeight - margin.getMarginTop() - margin.getMarginBottom();
                xInterval = (visualWidth - this.layout.getRows() * elementWidth) / (this.layout.getRows() - 1);
                yInterval = (visualHeight - this.layout.getCols() * elementHeight) / (this.layout.getCols() - 1);
                // 元素由于可能存在旋转倾斜，导致第一行元素跳出画板可视范围，因此向下向右做些微调
                xOffset = dimension.widthDiff() / 2 + margin.getMarginLeft();
                yOffset = Math.abs(dimension.heightDiff()) / 2 + margin.getMarginTop();
            } else {
                xInterval = (posterWidth - this.layout.getRows() * elementWidth) / (this.layout.getRows() + 1);
                yInterval = (posterHeight - this.layout.getCols() * elementHeight) / (this.layout.getCols() + 1);
                xOffset = dimension.widthDiff() / 2 + xInterval;
                yOffset = Math.abs(dimension.heightDiff()) / 2 + yInterval;
            }
            int rows = this.layout.getRows();
            int cols = this.layout.getCols();
            return new RepeatConfig(xInterval, yInterval, xOffset, yOffset, rows, cols);

        } else {
            int xOffset = dimension.widthDiff() / 2;
            int yOffset = Math.abs(dimension.heightDiff()) / 2;
            int rows = getRowNumber(elementWidth, posterWidth);
            int cols = getColumnsNumber(elementHeight, posterHeight);
            return new RepeatConfig(this.xPadding, this.yPadding, xOffset, yOffset, rows, cols);
        }
    }

    /**
     * 获取行数
     * x * elementWidth + (x - 1) * xPadding = posterWidth
     * x * (elementWidth + xPadding) = xPadding + posterWidth
     * x = (xPadding + posterWidth) / (elementWidth + xPadding)
     **/
    private int getRowNumber(int elementWidth, int posterWidth) {
        int rows = (xPadding + posterWidth) / (elementWidth + xPadding);
        int left = (xPadding + posterWidth) % (elementWidth + xPadding);
        if (left == 0) {
            return rows;
        }
        return rows + 1;
    }

    /**
     * 获取列数
     **/
    private int getColumnsNumber(int elementHeight, int posterHeight) {
        int cols = (yPadding + posterHeight) / (elementHeight + yPadding);
        int left = (yPadding + posterHeight) % (elementHeight + yPadding);
        if (left == 0) {
            return cols;
        }
        return cols + 1;
    }

    private static class RepeatConfig {
        public final int xPadding;
        public final int yPadding;
        public final int xOffset;
        public final int yOffset;
        public final int rows;
        public final int cols;

        public RepeatConfig(int xPadding, int yPadding, int xOffset, int yOffset, int rows, int cols) {
            this.xPadding = xPadding;
            this.yPadding = yPadding;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.rows = rows;
            this.cols = cols;
        }
    }

    /**
     * 自定义平铺布局，根据传入的行数和列数，自动计算元素之间的间距
     */
    @Getter
    @AllArgsConstructor
    private static class Layout {
        /**
         * 行数
         */
        private int rows;

        /**
         * 列数
         */
        private int cols;

        /**
         * 页边距
         */
        private Margin margin;
    }
}
