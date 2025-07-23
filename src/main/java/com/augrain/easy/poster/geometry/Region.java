package com.augrain.easy.poster.geometry;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 绘制区域
 *
 * @author biaoy
 * @since 2025/07/23
 */
@Getter
@Setter
@ToString
public class Region {

    private Point point;

    private int width;

    private int height;
}
