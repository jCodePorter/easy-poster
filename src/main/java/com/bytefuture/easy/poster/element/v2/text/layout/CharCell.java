package com.bytefuture.easy.poster.element.v2.text.layout;

import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextStyle;
import lombok.Getter;

/**
 * 竖排文本中的单个字符单元
 *
 * @author biaoy
 * @since 2026/05/07
 */
@Getter
public final class CharCell {
    /** 单个字符（Unicode codePoint 字符串） */
    private final String character;

    /** 该字符的最终样式 */
    private final ResolvedTextStyle style;

    /** 相对列起点的 Y 偏移 */
    private final int offsetY;

    /** 字符宽度 */
    private final int width;

    /** 字符高度 */
    private final int height;

    public CharCell(String character, ResolvedTextStyle style, int offsetY, int width, int height) {
        this.character = character;
        this.style = style;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
    }

    /** 创建仅修改 offsetY 的新实例 */
    public CharCell withOffsetY(int offsetY) {
        return new CharCell(this.character, this.style, offsetY, this.width, this.height);
    }
}