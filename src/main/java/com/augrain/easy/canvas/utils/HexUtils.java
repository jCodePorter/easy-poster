package com.augrain.easy.canvas.utils;

import java.awt.*;

/**
 * 十六进制
 *
 * @author biaoy
 * @since 2025/03/24
 */
public class HexUtils {

    /**
     * 将十六进制颜色字符串转换为 Color 对象
     *
     * @param hexColor 支持的格式: "#RRGGBB" 或 "#AARRGGBB" 或 "RRGGBB"  或 "AARRGGBB"
     * @return 对应的 Color 对象
     * @throws IllegalArgumentException 如果颜色字符串格式无效
     */
    public static Color hexToColor(String hexColor) {
        String hex = hexColor.startsWith("#") ? hexColor.substring(1) : hexColor;
        switch (hex.length()) {
            case 6:
                return new Color(
                        Integer.parseInt(hex.substring(0, 2), 16), // R
                        Integer.parseInt(hex.substring(2, 4), 16), // G
                        Integer.parseInt(hex.substring(4, 6), 16)  // B
                );
            case 8:
                return new Color(
                        Integer.parseInt(hex.substring(2, 4), 16), // R
                        Integer.parseInt(hex.substring(4, 6), 16), // G
                        Integer.parseInt(hex.substring(6, 8), 16), // B
                        Integer.parseInt(hex.substring(0, 2), 16)  // A
                );
            default:
                throw new IllegalArgumentException("Invalid hex color format: " + hexColor);
        }
    }
}
