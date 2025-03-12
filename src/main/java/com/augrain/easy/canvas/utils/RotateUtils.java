package com.augrain.easy.canvas.utils;

/**
 * 旋转工具类
 *
 * @author biaoy
 * @since 2025/03/05
 */
public class RotateUtils {

    private RotateUtils() {

    }

    /**
     * 根据旋转角度，计算旋转后宽高
     *
     * @param width  原始宽度
     * @param height 原始高度
     * @param angle 旋转角度
     * @return 新的宽高
     */
    public static int[] newBounds(int width, int height, int angle) {
        // 旋转角度（角度转弧度）
        double radian = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radian));
        double cos = Math.abs(Math.cos(radian));
        // 计算旋转后的宽度和高度
        int newWidth = (int) Math.floor(width * cos + height * sin);
        int newHeight = (int) Math.floor(height * cos + width * sin);
        return new int[]{newWidth, newHeight};
    }
}
