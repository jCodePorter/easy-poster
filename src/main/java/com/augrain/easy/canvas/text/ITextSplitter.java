package com.augrain.easy.canvas.text;

import java.awt.*;
import java.util.List;

/**
 * 文本拆分器
 *
 * @author biaoy
 * @since 2025/03/16
 */
public interface ITextSplitter {

    List<String> splitText(String text, int width, FontMetrics fontMetrics);
}
