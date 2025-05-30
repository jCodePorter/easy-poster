package com.augrain.easy.poster.geometry;

/**
 * 位置默认9种方向
 *
 * @author biaoy
 * @since 2025/03/03
 */
public enum Direction implements Position {
    /**
     * 居中
     */
    CENTER() {
        @Override
        public Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
            int x = (enclosingWidth - elementWidth) / 2;
            int y = (enclosingHeight - elementHeight) / 2;
            return Point.of(x, y);
        }
    },

    /**
     * 左侧居中
     */
    LEFT_CENTER() {
        @Override
        public Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
            int x = margin.getMarginLeft();
            int y = (enclosingHeight - elementHeight) / 2;
            return Point.of(x, y);
        }
    },

    /**
     * 右侧居中
     */
    RIGHT_CENTER() {
        @Override
        public Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
            int x = (enclosingWidth - elementWidth - margin.getMarginRight());
            int y = (enclosingHeight - elementHeight) / 2;
            return Point.of(x, y);
        }
    },

    /**
     * 左上角
     */
    TOP_LEFT() {
        @Override
        public Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
            int x = margin.getMarginLeft();
            int y = margin.getMarginTop();
            return Point.of(x, y);
        }
    },

    /**
     * 顶部居中
     */
    TOP_CENTER() {
        @Override
        public Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
            int x = (enclosingWidth - elementWidth) / 2;
            int y = margin.getMarginTop();
            return Point.of(x, y);
        }
    },

    /**
     * 右上角
     */
    TOP_RIGHT() {
        @Override
        public Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
            int x = (enclosingWidth - elementWidth - margin.getMarginRight());
            int y = margin.getMarginTop();
            return Point.of(x, y);
        }
    },

    /**
     * 左下角
     */
    LEFT_BOTTOM() {
        @Override
        public Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
            int x = margin.getMarginLeft();
            int y = enclosingHeight - elementHeight - margin.getMarginBottom();
            return Point.of(x, y);
        }
    },

    /**
     * 底部居中
     */
    BOTTOM_CENTER() {
        @Override
        public Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
            int x = (enclosingWidth - elementWidth) / 2;
            int y = enclosingHeight - elementHeight - margin.getMarginBottom();
            return Point.of(x, y);
        }
    },

    /**
     * 右下角
     */
    RIGHT_BOTTOM() {
        @Override
        public Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin) {
            int x = enclosingWidth - elementWidth - margin.getMarginRight();
            int y = enclosingHeight - elementHeight - margin.getMarginBottom();
            return Point.of(x, y);
        }
    }
}
