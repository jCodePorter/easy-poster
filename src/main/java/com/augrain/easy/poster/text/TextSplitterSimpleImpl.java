package com.augrain.easy.poster.text;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文本拆分器简单实现，属于功能验证的实验版本
 *
 * @author biaoy
 * @since 2025/03/16
 */
public class TextSplitterSimpleImpl implements ITextSplitter {
    private static final Map<Character, Integer> charSizeMap = new HashMap<>();

    @Override
    public List<SplitTextInfo> splitText(String text, int width, FontMetrics fontMetrics) {
        List<SplitTextInfo> lines = new ArrayList<>();
        int size = fontMetrics.getFont().getSize();

        char[] charArray = text.toCharArray();
        int index = 0;
        int currentSize = 0;
        StringBuilder builder = new StringBuilder();
        while (index < charArray.length) {
            int cSize = getCharSize(charArray[index], size, fontMetrics);
            if (currentSize + cSize > width) {
                lines.add(SplitTextInfo.of(builder.toString(), currentSize));
                builder.setLength(0);
                builder.append(charArray[index]);
                currentSize = cSize;
            } else {
                currentSize += cSize;
                builder.append(charArray[index]);
            }
            index++;
        }

        if (builder.length() > 0) {
            lines.add(SplitTextInfo.of(builder.toString(), currentSize));
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
}
