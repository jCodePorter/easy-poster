package com.augrain.easy.poster.text;

import lombok.Getter;
import lombok.Setter;

/**
 * 拆分的文本信息
 *
 * @author biaoy
 * @since 2025/07/13
 */
@Getter
@Setter
public class SplitTextInfo {

    /**
     * 单行文本内容
     */
    private String text;

    /**
     * 单行文本实际宽度
     */
    private int width;

    private SplitTextInfo(String text, int width) {
        this.text = text;
        this.width = width;
    }

    public static SplitTextInfo of(String text, int width) {
        return new SplitTextInfo(text, width);
    }
}
