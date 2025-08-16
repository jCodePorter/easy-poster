package com.augrain.easy.poster.element.advance;

import com.augrain.easy.poster.element.AbstractRepeatableElement;
import com.augrain.easy.poster.element.IElement;
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
    private int xInterval = 0;

    /**
     * 垂直间隔
     */
    private int yInterval = 0;

    /**
     * 自定义布局
     */
    private Layout layout;

    public RepeatElement(AbstractRepeatableElement basicElement) {
        this.basicElement = basicElement;
    }

    /**
     * 设置水平间隔
     *
     * @param xInterval 水平间隔
     * @return this
     */
    public RepeatElement setXInterval(int xInterval) {
        this.xInterval = xInterval;
        return this;
    }

    /**
     * 设置垂直间隔
     *
     * @param yInterval 垂直间隔
     * @return this
     */
    public RepeatElement setYInterval(int yInterval) {
        this.yInterval = yInterval;
        return this;
    }

    /**
     * 设置间隔
     *
     * @param xInterval 水平间隔
     * @param yInterval 垂直间隔
     * @return this
     */
    public RepeatElement setInterval(int xInterval, int yInterval) {
        this.xInterval = xInterval;
        this.yInterval = yInterval;
        return this;
    }

    /**
     * 自定义平铺布局
     *
     * @param rows 行数
     * @param cols 列数
     */
    public RepeatElement setLayout(int rows, int cols) {
        this.layout = new Layout(rows, cols, null);
        return this;
    }

    /**
     * 自定义平铺布局
     *
     * @param rows   行数
     * @param cols   列数
     * @param margin 页边距
     */
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
                int x = i * (elementWidth + result.xInterval) + result.xOffset;
                int y = j * (elementHeight + result.yInterval) + result.yOffset;

                Margin elementMargin = Margin.of().setMarginLeft(x).setMarginTop(y);
                basicElement.setPosition(RelativePosition.of(Direction.TOP_LEFT, elementMargin));
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
            int rows = calcRowNumber(elementWidth, posterWidth);
            int cols = calcColumnsNumber(elementHeight, posterHeight);
            return new RepeatConfig(this.xInterval, this.yInterval, xOffset, yOffset, rows, cols);
        }
    }

    /**
     * 获取行数
     * x * elementWidth + (x - 1) * xInterval = posterWidth
     * x * (elementWidth + xInterval) = xInterval + posterWidth
     * x = (xInterval + posterWidth) / (elementWidth + xInterval)
     **/
    private int calcRowNumber(int elementWidth, int posterWidth) {
        int rows = (xInterval + posterWidth) / (elementWidth + xInterval);
        int left = (xInterval + posterWidth) % (elementWidth + xInterval);
        if (left == 0) {
            return rows;
        }
        return rows + 1;
    }

    /**
     * 获取列数
     **/
    private int calcColumnsNumber(int elementHeight, int posterHeight) {
        int cols = (yInterval + posterHeight) / (elementHeight + yInterval);
        int left = (yInterval + posterHeight) % (elementHeight + yInterval);
        if (left == 0) {
            return cols;
        }
        return cols + 1;
    }

    /**
     * 平铺配置
     */
    private static class RepeatConfig {
        /**
         * x轴间隔
         */
        public final int xInterval;

        /**
         * y轴间隔
         */
        public final int yInterval;

        /**
         * x轴坐标修正偏移量
         */
        public final int xOffset;

        /**
         * y轴坐标修正偏移量
         */
        public final int yOffset;

        /**
         * 行数
         */
        public final int rows;

        /**
         * 列数
         */
        public final int cols;

        public RepeatConfig(int xInterval, int yInterval, int xOffset, int yOffset, int rows, int cols) {
            this.xInterval = xInterval;
            this.yInterval = yInterval;
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
