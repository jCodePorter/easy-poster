package com.augrain.easy.poster.geometry;

import lombok.*;

/**
 * 尺寸
 *
 * @author biaoy
 * @since 2025/03/05
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Dimension {

    /**
     * 原始宽度
     */
    private int width;

    /**
     * 原始高度
     */
    private int height;

    /**
     * 旋转后宽度
     */
    private int rotateWidth;

    /**
     * 旋转后高度
     */
    private int rotateHeight;

    /**
     * 偏移量，微调整，用于文本渲染时使用
     */
    private int xOffset;

    private int yOffset;

    /**
     * 计算出待渲染的位置，相对于坐标原点
     */
    private Point point;

    public int widthDiff() {
        if (rotateWidth != 0) {
            return rotateWidth - width;
        }
        return 0;
    }

    public int heightDiff() {
        if (rotateHeight != 0) {
            return rotateHeight - height;
        }
        return 0;
    }
}
