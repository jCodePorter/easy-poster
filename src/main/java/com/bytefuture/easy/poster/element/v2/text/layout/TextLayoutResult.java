package com.bytefuture.easy.poster.element.v2.text.layout;

import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Position;
import com.bytefuture.easy.poster.utils.RotateUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public final class TextLayoutResult {
    private final Point point;
    private final int width;
    private final int height;
    private final int lineHeight;
    private final int baselineOffset;
    private final int yOffset;
    private final List<TextLine> lines;

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

    public static TextLayoutResult empty(Position position) {
        Point point = position == null ? Point.ORIGIN_COORDINATE : position.calculate(0, 0, 0, 0);
        return new TextLayoutResult(point, 0, 0, 0, 0, 0, Collections.<TextLine>emptyList());
    }
}
