package com.augrain.easy.poster.geometry;

/**
 * 位置
 *
 * @author biaoy
 * @since 2025/03/03
 */
public interface Position {

    default Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight) {
        return calculate(enclosingWidth, enclosingHeight, elementWidth, elementHeight, Margin.DEFAULT);
    }

    /**
     * 计算位置
     *
     * @param enclosingWidth  封闭区域宽度
     * @param enclosingHeight 封闭区域高度
     * @param elementWidth    元素宽度
     * @param elementHeight   元素高度
     * @param margin          边距
     * @return 坐标点
     */
    Point calculate(int enclosingWidth, int enclosingHeight, int elementWidth, int elementHeight, Margin margin);
}
