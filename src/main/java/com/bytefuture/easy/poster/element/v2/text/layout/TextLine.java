package com.bytefuture.easy.poster.element.v2.text.layout;

import com.bytefuture.easy.poster.element.v2.text.resolve.ResolvedTextRun;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 单行文本布局结果。
 * 包含整行文本、宽度、对齐偏移和行内运行段。
 */
@Getter
public final class TextLine {
    /**
     * 行文本内容。
     */
    private final String text;
    /** 行宽度。 */
    private final int width;
    /** 相对布局起点的 X 轴偏移。 */
    private final int offsetX;
    /** 行内运行段列表。 */
    private final List<ResolvedTextRun> runs;

    /**
     * 创建文本行。
     *
     * @param text 行文本内容
     * @param width 行宽度
     * @param offsetX X 轴偏移
     * @param runs 行内运行段
     */
    public TextLine(String text, int width, int offsetX, List<ResolvedTextRun> runs) {
        this.text = text;
        this.width = width;
        this.offsetX = offsetX;
        this.runs = Collections.unmodifiableList(new ArrayList<ResolvedTextRun>(runs));
    }

    /**
     * 返回一个仅修改对齐偏移的新文本行。
     *
     * @param offsetX 新的 X 轴偏移
     * @return 新文本行实例
     */
    public TextLine withOffsetX(int offsetX) {
        return new TextLine(this.text, this.width, offsetX, this.runs);
    }

    /**
     * 创建空文本行。
     *
     * @return 空文本行
     */
    public static TextLine empty() {
        return new TextLine("", 0, 0, Collections.<ResolvedTextRun>emptyList());
    }
}
