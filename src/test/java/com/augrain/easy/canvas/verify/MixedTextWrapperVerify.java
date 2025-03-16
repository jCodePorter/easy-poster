package com.augrain.easy.canvas.verify;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多语种文本拆分验证
 *
 * @author biaoy
 * @since 2025/03/14
 */
public class MixedTextWrapperVerify {

    private static final Map<Character, Integer> charSizeMap = new HashMap<>();

    public static List<String> wrapText(String text, int width, FontMetrics fontMetrics) {
        List<String> lines = new ArrayList<>();
        int size = fontMetrics.getFont().getSize();

        char[] charArray = text.toCharArray();
        int index = 0;
        int currentSize = 0;
        StringBuilder builder = new StringBuilder();

        while (index < charArray.length) {
            int cSize = getCharSize(charArray[index], size, fontMetrics);
            if (currentSize + cSize > width) {
                String string = builder.toString();
                lines.add(string);
                builder.setLength(0);
                currentSize = 0;
            } else {
                currentSize += cSize;
                builder.append(charArray[index]);
            }
            index++;
        }

        if (builder.length() > 0) {
            lines.add(builder.toString());
        }
        return lines;
    }

    private static int getCharSize(char c, int defaultSize, FontMetrics fm) {
        if (isFullWidthChar(c)) {
            return defaultSize;
        }
        return charSizeMap.computeIfAbsent(c, k -> fm.charWidth(c));
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

    public static void main(String[] args) throws Exception {
        BufferedImage baseImg = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = baseImg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.white);
        g.fillRect(0, 0, 500, 500);

        Font font = new Font("微软雅黑", Font.PLAIN, 25);
        g.setFont(font);
        g.setColor(Color.red);
        FontMetrics fontMetrics = g.getFontMetrics(font);

        String text = "这是一个混合文本示例，包含中文、English、日本語等多种语言。This is a mixed text example with 中文, English, and 日本語.";
        int width = 200;
        List<String> wrappedText = wrapText(text, width, fontMetrics);
        int lineNumber = 0;
        for (String line : wrappedText) {
            System.out.println(line);

            g.drawString(line, 30, 30 * ++lineNumber);
        }

        ImageIO.write(baseImg, "png", new File("text_line.png"));
    }
}