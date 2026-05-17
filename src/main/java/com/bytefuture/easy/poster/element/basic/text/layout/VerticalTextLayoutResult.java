package com.bytefuture.easy.poster.element.basic.text.layout;

import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.utils.RotateUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 竖排文本布局结果
 *
 * @author biaoy
 * @since 2026/05/07
 */
@Getter
public class VerticalTextLayoutResult {
    /**
     * 文本布局起点
     */
    private final Point point;

    /**
     * 布局宽度（所有列宽度之和）
     */
    private final int width;

    /**
     * 布局高度（最高列的高度，或 maxVerticalWidth）
     */
    private final int height;

    /**
     * 统一列宽/列间距（= lineHeight）
     */
    private final int lineHeight;

    /**
     * 列间距（列与列之间的左右留白）
     */
    private final int columnSpacing;

    /**
     * 列内基线偏移
     */
    private final int baselineOffset;

    /**
     * 绘制时的 Y 轴偏移
     */
    private final int yOffset;

    /**
     * 布局后的文本列列表
     */
    private final List<TextColumn> columns;

    public VerticalTextLayoutResult(Point point, int width, int height, int lineHeight,
                                    int columnSpacing, int baselineOffset, int yOffset, List<TextColumn> columns) {
        this.point = point;
        this.width = width;
        this.height = height;
        this.lineHeight = lineHeight;
        this.columnSpacing = columnSpacing;
        this.baselineOffset = baselineOffset;
        this.yOffset = yOffset;
        this.columns = Collections.unmodifiableList(new ArrayList<>(columns));
    }

    /**
     * 创建空布局结果
     */
    public static VerticalTextLayoutResult empty(Position position) {
        Point point = position == null ? Point.ORIGIN_COORDINATE : position.calculate(0, 0, 0, 0);
        return new VerticalTextLayoutResult(point, 0, 0, 0, 0, 0, 0, Collections.emptyList());
    }

    /**
     * 转换为元素尺寸对象
     */
    public Dimension toDimension(int rotate) {
        Dimension.DimensionBuilder builder = Dimension.builder()
                .width(this.width)
                .height(this.height)
                .xOffset(0)
                .yOffset(this.yOffset)
                .point(this.point);
        if (rotate != 0) {
            int[] bounds = RotateUtils.newBounds(this.width, this.height, rotate);
            builder.rotateWidth(bounds[0]).rotateHeight(bounds[1]);
        }
        return builder.build();
    }
}