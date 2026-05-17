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
 * 文本布局结果。
 * 包含文本在画布中的位置、尺寸、基线信息以及逐行布局数据。
 */
@Getter
public class TextLayoutResult {
    /**
     * 文本布局起点
     */
    private final Point point;

    /**
     * 布局宽度
     */
    private final int width;

    /**
     * 布局高度。
     */
    private final int height;

    /**
     * 统一行高
     */
    private final int lineHeight;

    /**
     * 基线偏移
     */
    private final int baselineOffset;

    /**
     * 绘制时的 Y 轴偏移
     */
    private final int yOffset;

    /**
     * 布局后的文本行列表
     */
    private final List<TextLine> lines;

    /**
     * 创建文本布局结果。
     *
     * @param point          布局起点
     * @param width          布局宽度
     * @param height         布局高度
     * @param lineHeight     行高
     * @param baselineOffset 基线偏移
     * @param yOffset        绘制 Y 偏移
     * @param lines          文本行列表
     */
    public TextLayoutResult(Point point, int width, int height, int lineHeight,
                            int baselineOffset, int yOffset, List<TextLine> lines) {
        this.point = point;
        this.width = width;
        this.height = height;
        this.lineHeight = lineHeight;
        this.baselineOffset = baselineOffset;
        this.yOffset = yOffset;
        this.lines = Collections.unmodifiableList(new ArrayList<TextLine>(lines));
    }

    /**
     * 创建空布局结果。
     *
     * @param position 位置定义
     * @return 空布局结果
     */
    public static TextLayoutResult empty(Position position) {
        Point point = position == null ? Point.ORIGIN_COORDINATE : position.calculate(0, 0, 0, 0);
        return new TextLayoutResult(point, 0, 0, 0, 0, 0, Collections.emptyList());
    }

    /**
     * 转换为元素尺寸对象。
     *
     * @param rotate 旋转角度
     * @return 元素尺寸
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
