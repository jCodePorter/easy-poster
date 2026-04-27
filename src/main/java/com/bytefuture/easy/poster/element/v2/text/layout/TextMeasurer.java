package com.bytefuture.easy.poster.element.v2.text.layout;

import java.awt.Font;
import java.awt.Graphics2D;

/**
 * 文本测量工具类
 * 提供字体和文本的尺寸测量能力
 *
 * @author biaoy
 * @since 2026/04/27
 */
public class TextMeasurer {

    /**
     * 测量文本像素宽度
     *
     * @param graphics 图形上下文
     * @param text     目标文本
     * @param font     测量字体
     * @return 像素宽度
     */
    public int measureWidth(Graphics2D graphics, String text, Font font) {
        return graphics.getFontMetrics(font).stringWidth(text);
    }

    /**
     * 获取字体整体高度
     *
     * @param graphics 图形上下文
     * @param font     目标字体
     * @return 字体高度
     */
    public int getFontHeight(Graphics2D graphics, Font font) {
        return graphics.getFontMetrics(font).getHeight();
    }

    /**
     * 获取字体 ascent（基线到顶部距离）
     *
     * @param graphics 图形上下文
     * @param font     目标字体
     * @return ascent 值
     */
    public int getAscent(Graphics2D graphics, Font font) {
        return graphics.getFontMetrics(font).getAscent();
    }
}