package com.bytefuture.easy.poster.ui.v2;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.VerticalAlign;
import com.bytefuture.easy.poster.model.VerticalDirection;
import org.junit.Test;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;

/**
 * Visual UI tests for V2 vertical text rendering.
 * Each test writes a PNG for manual inspection.
 */
public class V2TextElementVerticalTest {

    @Test
    public void shouldRenderVerticalDirectionVariantsToPng() {
        EasyPoster poster = newPoster(860, 420);
        addPanel(poster, 40, 40, 340, 320, new Color(246, 249, 255));
        addPanel(poster, 460, 40, 340, 320, new Color(255, 249, 242));

        addLabel(poster, "LEFT_TO_RIGHT", 60, 64);
        addLabel(poster, "RIGHT_TO_LEFT", 480, 64);

        poster.addElement(TextElement.builder("unused")
                .vertical(Arrays.asList("春眠不觉晓", "处处闻啼鸟", "夜来风雨声", "花落知多少"))
                .font("Microsoft YaHei", Font.BOLD, 28)
                .color(new Color(44, 62, 80))
                .layoutHeight(220)
                .columnSpacing(18)
                .verticalDirection(VerticalDirection.LEFT_TO_RIGHT)
                .verticalAlign(VerticalAlign.TOP)
                .textBackground(new Color(255, 255, 255), 16)
                .textBackgroundArc(16)
                .position(AbsolutePosition.of(Point.of(96, 106), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder("unused")
                .vertical(Arrays.asList("春眠不觉晓", "处处闻啼鸟", "夜来风雨声", "花落知多少"))
                .font("Microsoft YaHei", Font.BOLD, 28)
                .color(new Color(120, 72, 42))
                .layoutHeight(220)
                .columnSpacing(18)
                .verticalDirection(VerticalDirection.RIGHT_TO_LEFT)
                .verticalAlign(VerticalAlign.TOP)
                .textBackground(new Color(255, 255, 255), 16)
                .textBackgroundArc(16)
                .position(AbsolutePosition.of(Point.of(516, 106), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_vertical_directions.png");
    }

    @Test
    public void shouldRenderVerticalAlignmentVariantsToPng() {
        EasyPoster poster = newPoster(1180, 500);
        addPanel(poster, 40, 40, 240, 360, new Color(246, 249, 255));
        addPanel(poster, 320, 40, 240, 360, new Color(255, 249, 242));
        addPanel(poster, 600, 40, 240, 360, new Color(245, 252, 247));
        addPanel(poster, 880, 40, 240, 360, new Color(252, 245, 248));

        addLabel(poster, "TOP", 60, 64);
        addLabel(poster, "MIDDLE", 340, 64);
        addLabel(poster, "BOTTOM", 620, 64);
        addLabel(poster, "JUSTIFY", 900, 64);

        poster.addElement(buildVerticalAlignmentSample(VerticalAlign.TOP, 120, 108, new Color(52, 73, 94)));
        poster.addElement(buildVerticalAlignmentSample(VerticalAlign.MIDDLE, 400, 108, new Color(41, 128, 185)));
        poster.addElement(buildVerticalAlignmentSample(VerticalAlign.BOTTOM, 680, 108, new Color(39, 174, 96)));
        poster.addElement(buildVerticalAlignmentSample(VerticalAlign.JUSTIFY, 960, 108, new Color(142, 68, 173)));

        poster.asFile("png", "out_v2_text_vertical_alignments.png");
    }

    @Test
    public void shouldRenderVerticalStringAutoSplitToPng() {
        EasyPoster poster = newPoster(760, 420);
        addPanel(poster, 40, 40, 300, 320, new Color(246, 249, 255));
        addPanel(poster, 400, 40, 300, 320, new Color(255, 249, 242));

        addLabel(poster, "vertical(String) auto split", 60, 64);
        addLabel(poster, "explicit columns", 420, 64);

        poster.addElement(TextElement.builder("unused")
                .vertical("天地玄黄宇宙洪荒日月盈昃辰宿列张")
                .font("Microsoft YaHei", Font.BOLD, 28)
                .color(new Color(44, 62, 80))
                .lineHeight(34)
                .layoutHeight(210)
                .columnSpacing(14)
                .verticalDirection(VerticalDirection.LEFT_TO_RIGHT)
                .verticalAlign(VerticalAlign.JUSTIFY)
                .textBackground(new Color(255, 255, 255), 16)
                .textBackgroundArc(16)
                .position(AbsolutePosition.of(Point.of(92, 106), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder("unused")
                .vertical(Arrays.asList("天地玄黄", "宇宙洪荒", "日月盈昃", "辰宿列张"))
                .font("Microsoft YaHei", Font.BOLD, 28)
                .color(new Color(120, 72, 42))
                .lineHeight(34)
                .layoutHeight(210)
                .columnSpacing(14)
                .verticalDirection(VerticalDirection.LEFT_TO_RIGHT)
                .verticalAlign(VerticalAlign.JUSTIFY)
                .textBackground(new Color(255, 255, 255), 16)
                .textBackgroundArc(16)
                .position(AbsolutePosition.of(Point.of(452, 106), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_vertical_auto_split.png");
    }

    private TextElement buildVerticalAlignmentSample(VerticalAlign align, int x, int y, Color color) {
        return TextElement.builder("unused")
                .vertical(Arrays.asList("山河"))
                .font("Microsoft YaHei", Font.BOLD, 40)
                .color(color)
                .layoutHeight(240)
                .columnSpacing(0)
                .verticalDirection(VerticalDirection.LEFT_TO_RIGHT)
                .verticalAlign(align)
                .textBackground(new Color(255, 255, 255), 20)
                .textBackgroundArc(18)
                .position(AbsolutePosition.of(Point.of(x, y), Direction.TOP_LEFT))
                .build();
    }

    private EasyPoster newPoster(int width, int height) {
        EasyPoster poster = new EasyPoster(width, height);
        poster.addRectangleElement(width, height)
                .setColor(Color.WHITE)
                .setPosition(AbsolutePosition.of(Point.ORIGIN_COORDINATE, Direction.TOP_LEFT));
        return poster;
    }

    private void addLabel(EasyPoster poster, String text, int x, int y) {
        poster.addElement(TextElement.builder(text)
                .font("Dialog", Font.BOLD, 16)
                .color(new Color(110, 110, 110))
                .position(AbsolutePosition.of(Point.of(x, y), Direction.TOP_LEFT))
                .build());
    }

    private void addPanel(EasyPoster poster, int x, int y, int width, int height, Color backgroundColor) {
        poster.addRectangleElement(width, height)
                .setArc(18)
                .setColor(backgroundColor)
                .setPosition(AbsolutePosition.of(Point.of(x, y), Direction.TOP_LEFT));

        poster.addRectangleElement(width, height)
                .setArc(18)
                .setBorderSize(2)
                .setColor(new Color(224, 230, 236))
                .setPosition(AbsolutePosition.of(Point.of(x, y), Direction.TOP_LEFT));
    }
}
