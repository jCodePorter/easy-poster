package com.augrain.easy.canvas.verify;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 文本宽度验证
 *
 * @author biaoy
 * @since 2025/03/14
 */
public class TextWidthVerify {

    public static void main(String[] args) {
        BufferedImage baseImg = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = baseImg.createGraphics();

        Font font = new Font("微软雅黑", Font.PLAIN, 25);
        g.setFont(font);

        FontMetrics fontMetrics = g.getFontMetrics(font);

        String str = "abcdefghijklmnopqrstuvwxyz";
        // String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        // String str = "空间引力很牛逼，。【】カタカナ";
        char[] charArray = str.toCharArray();

        int size = 0;
        for (char c : charArray) {
            size += fontMetrics.stringWidth(String.valueOf(c));
        }
        System.out.println(size / str.length());
    }
}
