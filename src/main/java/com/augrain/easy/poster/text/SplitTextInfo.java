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

    private String text;

    private int width;

    private SplitTextInfo(String text, int width) {
        this.text = text;
        this.width = width;
    }

    public static SplitTextInfo of(String text, int width) {
        return new SplitTextInfo(text, width);
    }
}
