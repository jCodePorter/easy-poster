package com.augrain.easy.canvas.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 全局配置
 *
 * @author biaoy
 * @since 2025/04/07
 */
@Getter
@Setter
public class Config {

    /**
     * 调试模式，目前仅用于绘制各元素的外边框
     */
    private boolean debug = false;
}
