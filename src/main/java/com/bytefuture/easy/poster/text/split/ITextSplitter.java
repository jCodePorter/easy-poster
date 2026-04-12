package com.bytefuture.easy.poster.text.split;

import java.awt.FontMetrics;
import java.util.List;

/**
 * 文本拆分器接口。
 * 负责把一段文本按给定宽度切成多行。
 */
public interface ITextSplitter {

    /**
     * 按请求参数拆分文本。
     *
     * @param request 拆分请求
     * @return 拆分结果
     */
    TextSplitResult split(TextSplitRequest request);

    /**
     * 兼容旧调用方式的快捷方法。
     *
     * @param text 文本内容
     * @param width 最大宽度
     * @param fontMetrics 字体度量
     * @return 分行结果
     */
    default List<SplitTextInfo> splitText(String text, int width, FontMetrics fontMetrics) {
        return split(TextSplitRequest.of(text, width, fontMetrics)).getLines();
    }
}
