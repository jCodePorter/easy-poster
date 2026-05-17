package com.bytefuture.easy.poster.element.basic.text.layout;

import com.bytefuture.easy.poster.element.basic.text.style.ResolvedTextStyle;
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

    /** 是否为挤压放置的标点（与相邻字符重叠，不占独立位置） */
    private final boolean squeezed;

    /** 是否为压缩高度的标点（如省略号压缩为单字符高度） */
    private final boolean compressed;

    /** 是否为省略号单元（两个连续 U+2026 组合） */
    private final boolean ellipsisUnit;

    public CharCell(String character, ResolvedTextStyle style, int offsetY, int width, int height) {
        this(character, style, offsetY, width, height, false, false, false);
    }

    public CharCell(String character, ResolvedTextStyle style, int offsetY, int width, int height,
                    boolean squeezed, boolean compressed, boolean ellipsisUnit) {
        this.character = character;
        this.style = style;
        this.offsetY = offsetY;
        this.width = width;
        this.height = compressed ? height / 2 : height;
        this.squeezed = squeezed;
        this.compressed = compressed;
        this.ellipsisUnit = ellipsisUnit;
    }

    /** 创建仅修改 offsetY 的新实例 */
    public CharCell withOffsetY(int offsetY) {
        return new CharCell(this.character, this.style, offsetY, this.width, this.height,
                this.squeezed, this.compressed, this.ellipsisUnit);
    }

    /** 创建仅修改 squeezed 标记的新实例 */
    public CharCell withSqueezed(boolean squeezed) {
        return new CharCell(this.character, this.style, this.offsetY, this.width, this.height,
                squeezed, this.compressed, this.ellipsisUnit);
    }

    /** 创建仅修改 compressed 标记的新实例（同时调整高度） */
    public CharCell withCompressed(boolean compressed) {
        if (compressed && !this.compressed) {
            // 压缩时高度变为原始高度的一半
            int originalHeight = this.ellipsisUnit ? this.height * 2 : this.height;
            int compressedHeight = originalHeight / 2;
            return new CharCell(this.character, this.style, this.offsetY, this.width, compressedHeight,
                    this.squeezed, true, this.ellipsisUnit);
        }
        return new CharCell(this.character, this.style, this.offsetY, this.width, this.height,
                this.squeezed, this.compressed, this.ellipsisUnit);
    }
}