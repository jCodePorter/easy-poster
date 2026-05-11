package com.bytefuture.easy.poster.element.v2.text.layout;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 竖排文本中的单列布局结果
 *
 * @author biaoy
 * @since 2026/05/07
 */
@Getter
public class TextColumn {
    /** 列内文本内容 */
    private final String text;

    /** 列宽（= lineHeight） */
    private final int width;

    /** 列高（列内所有字符高度之和） */
    private final int height;

    /** 相对文本块起点的 X 偏移（列对齐偏移） */
    private final int offsetX;

    /** 列内字符单元列表 */
    private final List<CharCell> characters;

    public TextColumn(String text, int width, int height, int offsetX, List<CharCell> characters) {
        this.text = text;
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.characters = Collections.unmodifiableList(new ArrayList<>(characters));
    }

    /** 创建仅修改 offsetX 和 characters 的新实例 */
    public TextColumn withLayout(int offsetX, List<CharCell> characters) {
        return new TextColumn(this.text, this.width, this.height, offsetX, characters);
    }

    /**
     * 根据新的 CharCell 列表重建 TextColumn（重新计算 text、height）
     * squeezed 字符不计入列高度
     *
     * @param newCharacters 新的字符单元列表
     * @return 重建后的 TextColumn
     */
    public TextColumn rebuild(List<CharCell> newCharacters) {
        StringBuilder textBuilder = new StringBuilder();
        int totalHeight = 0;
        for (CharCell cell : newCharacters) {
            textBuilder.append(cell.getCharacter());
            if (!cell.isSqueezed()) {
                totalHeight += cell.getHeight();
            }
        }
        return new TextColumn(textBuilder.toString(), this.width, totalHeight, this.offsetX, newCharacters);
    }

    /** 创建空列 */
    public static TextColumn empty() {
        return new TextColumn("", 0, 0, 0, Collections.emptyList());
    }
}