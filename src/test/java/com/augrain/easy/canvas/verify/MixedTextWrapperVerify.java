package com.augrain.easy.canvas.verify;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * 多语种文本拆分验证
 *
 * @author biaoy
 * @since 2025/03/14
 */
public class MixedTextWrapperVerify {

    public static List<String> wrapText(String text, int width, FontMetrics fontMetrics) {
        List<String> lines = new ArrayList<>();

        char[] charArray = text.toCharArray();
        StringBuilder currentText = new StringBuilder();
        int currentSize = 0;

        // 全角字体宽度与字体大小一致
        int size = fontMetrics.getFont().getSize();

        int currentIndex = 0;
        int endIndex = 0;
        while (true) {
            if (isFullWidthChar(charArray[currentIndex])) {
                endIndex = lookUpMoreFullChar(currentIndex, charArray);

                int charNum = width / size;
                int temp = currentIndex;
                while (temp < endIndex) {
                    String sub = text.substring(temp, Math.min(temp + charNum, endIndex));
                    int tempSize = sub.length() * size;

                    if (currentSize + tempSize >= width) {
                        lines.add(sub);
                    } else {
                        currentText.append(sub);
                        currentSize = tempSize;
                    }
                    temp += charNum;
                }
                currentIndex = endIndex;
            } else {

            }
            if (endIndex == charArray.length) {
                break;
            }
        }

        if (currentText.length() > 0) {
            lines.add(currentText.toString());
        }
        return lines;
    }

    private static int lookUpMoreFullChar(int current, char[] charArray) {
        for (int i = current; i < charArray.length; i++) {
            if (!isFullWidthChar(charArray[i])) {
                return i;
            }
        }
        return charArray.length;
    }

    /**
     * 判断字符是否是全角字符
     */
    private static boolean isFullWidthChar(char c) {
        return (c >= '\u4E00' && c <= '\u9FFF') || // 中文字符
                (c >= '\u3040' && c <= '\u30FF') || // 日文字符
                (c >= '\uFF01' && c <= '\uFF5E');  // 全角标点符号
    }

    /**
     * 是否是全角标点符号
     */
    private static boolean isFullPunctuation(char c) {
        return c >= '\uFF01' && c <= '\uFF5E';
    }

    /**
     * 判断字符是否是标点符号
     */
    private static boolean isPunctuation(char c) {
        String p = ",.!?;:，。！？；：【】{}[]";
        return p.indexOf(c) >= 0;
    }

    public static void main(String[] args) {
        // String text = "这是一个混合文本示例，包含中文、English、日本語等多种语言。This is a mixed text example with 中文, English, and 日本語.";
        String text = "这是一段中文基本测试文本没有标点符号没有特殊字符！！";
        int width = 100;

        BufferedImage baseImg = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = baseImg.createGraphics();

        Font font = new Font("微软雅黑", Font.PLAIN, 25);
        g.setFont(font);

        FontMetrics fontMetrics = g.getFontMetrics(font);

        List<String> wrappedText = wrapText(text, width, fontMetrics);
        for (String line : wrappedText) {
            System.out.println(line);
        }
    }
}