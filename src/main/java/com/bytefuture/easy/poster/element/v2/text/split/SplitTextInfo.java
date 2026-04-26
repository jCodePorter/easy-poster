package com.bytefuture.easy.poster.element.v2.text.split;

import lombok.Getter;
import lombok.Setter;

/**
 * 单行拆分结果。
 */
@Getter
@Setter
public class SplitTextInfo {

    /**
     * 单行文本内容。
     */
    private String text;

    /** 单行文本实际宽度。 */
    private int width;

    private SplitTextInfo(String text, int width) {
        this.text = text;
        this.width = width;
    }

    /**
     * 创建单行拆分结果。
     *
     * @param text 行文本内容
     * @param width 行宽度
     * @return 单行拆分结果
     */
    public static SplitTextInfo of(String text, int width) {
        return new SplitTextInfo(text, width);
    }
}
