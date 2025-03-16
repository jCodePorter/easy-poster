package com.augrain.easy.canvas.element.advance;

import com.augrain.easy.canvas.element.AbstractRepeatableElement;
import com.augrain.easy.canvas.element.IElement;
import com.augrain.easy.canvas.geometry.Dimension;
import com.augrain.easy.canvas.geometry.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

/**
 * 平铺元素，可以组合基础元素，部分高级元素
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

    public RepeatElement setLayout(int rows, int cols, Margin margin) {
        this.layout = new Layout(rows, cols, margin);
        return this;
    }

    @Override
    public CoordinatePoint render(Graphics2D g, int canvasWidth, int canvasHeight) {
        basicElement.beforeRender(g);

        Dimension dimension = basicElement.calculateDimension(g, canvasWidth, canvasHeight);
        int elementWidth = Math.max(dimension.getRotateWidth(), dimension.getWidth());
        int elementHeight = Math.max(dimension.getRotateHeight(), dimension.getHeight());

        if (this.layout != null) {
            return doLayoutTile(g, canvasWidth, canvasHeight, elementWidth, elementHeight, dimension);
        } else {
            return doNormalTile(g, canvasWidth, canvasHeight, elementWidth, elementHeight, dimension);
        }
    }

    private CoordinatePoint doLayoutTile(Graphics2D g, int canvasWidth, int canvasHeight, int elementWidth, int elementHeight, Dimension dimension) {
        Margin margin = this.layout.getMargin();
        int visualWidth = canvasWidth - margin.getMarginLeft() - margin.getMarginRight();
        int visualHeight = canvasHeight - margin.getMarginTop() - margin.getMarginBottom();

        int xPadding = (visualWidth - this.layout.getRows() * elementWidth) / (this.layout.getRows() - 1);
        int yPadding = (visualHeight - this.layout.getCols() * elementHeight) / (this.layout.getCols() - 1);

        // 元素由于可能存在旋转倾斜，导致第一行元素跳出画板可视范围，因此向下向右做些微调
        int xOffset = dimension.widthDiff() / 2 + margin.getMarginLeft();
        int yOffset = Math.abs(dimension.heightDiff()) / 2 + margin.getMarginTop();

        for (int j = 0; j < this.layout.getCols(); j++) {
            for (int i = 0; i < this.layout.getRows(); i++) {
                int x = i * (elementWidth + xPadding) + xOffset;
                int y = j * (elementHeight + yPadding) + yOffset;

                Margin elementMargin = Margin.of(0);
                elementMargin.setMarginLeft(x);
                elementMargin.setMarginTop(y);
                basicElement.setPosition(RelativePosition.of(Positions.TOP_LEFT, elementMargin));
                CoordinatePoint coordinatePoint = basicElement.reCalculatePosition(canvasWidth, canvasHeight, dimension);
                dimension.setPoint(coordinatePoint);
                basicElement.doRender(g, dimension, canvasWidth, canvasHeight);
            }
        }
        return CoordinatePoint.ORIGIN_COORDINATE;
    }

    private CoordinatePoint doNormalTile(Graphics2D g, int canvasWidth, int canvasHeight,
                                         int elementWidth, int elementHeight, Dimension dimension) {
        int rowsNumber = getRowNumber(elementWidth, canvasWidth);
        int columnsNumber = getColumnsNumber(elementHeight, canvasHeight);

        // 元素由于可能存在旋转倾斜，导致第一行元素跳出画板可视范围，因此向下向右做些微调
        int xOffset = dimension.widthDiff() / 2;
        int yOffset = Math.abs(dimension.heightDiff()) / 2;

        for (int j = 0; j < columnsNumber; j++) {
            for (int i = 0; i < rowsNumber; i++) {
                int x = i * (elementWidth + this.xPadding) + xOffset;
                int y = j * (elementHeight + this.yPadding) + yOffset;

                Margin margin = Margin.of().setMarginLeft(x).setMarginTop(y);
                basicElement.setPosition(RelativePosition.of(Positions.TOP_LEFT, margin));
                CoordinatePoint coordinatePoint = basicElement.reCalculatePosition(canvasWidth, canvasHeight, dimension);
                dimension.setPoint(coordinatePoint);
                basicElement.doRender(g, dimension, canvasWidth, canvasHeight);
            }
        }
        return CoordinatePoint.ORIGIN_COORDINATE;
    }

    /**
     * 获取行数
     * x * elementWidth + (x - 1) * xPadding = canvasWidth
     * x * (elementWidth + xPadding) = xPadding + canvasWidth
     * x = (xPadding + canvasWidth) / (elementWidth + xPadding)
     **/
    public int getRowNumber(int elementWidth, int canvasWidth) {
        int rows = (xPadding + canvasWidth) / (elementWidth + xPadding);
        int left = (xPadding + canvasWidth) % (elementWidth + xPadding);
        if (left == 0) {
            return rows;
        }
        return rows + 1;
    }

    /**
     * 获取列数
     **/
    public int getColumnsNumber(int elementHeight, int canvasHeight) {
        int cols = (yPadding + canvasHeight) / (elementHeight + yPadding);
        int left = (yPadding + canvasHeight) % (elementHeight + yPadding);
        if (left == 0) {
            return cols;
        }
        return cols + 1;
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
