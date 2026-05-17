package com.bytefuture.easy.poster.ui.basic.text;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.basic.TextElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.VerticalAlign;
import com.bytefuture.easy.poster.model.TextSpan;
import org.junit.Test;

import java.awt.*;

public class ElementTextVerticalTest {

    @Test
    public void shouldOutputVerticalRightToLeftPng() {
        EasyPoster poster = createPoster(400, 500);

        poster.addElement(
                TextElement.of("春眠不觉晓处处闻啼鸟夜来风雨声花落知多少")
                        .vertical()
                        .columnRightToLeft()
                        .maxVerticalWidth(400)
                        .setFontSize(28)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_rl.png");
    }

    @Test
    public void shouldOutputVerticalLeftToRightPng() {
        EasyPoster poster = createPoster(400, 500);

        poster.addElement(
                TextElement.of("春眠不觉晓处处闻啼鸟夜来风雨声花落知多少")
                        .vertical()
                        .columnLeftToRight()
                        .maxVerticalWidth(400)
                        .setFontSize(28)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_lr.png");
    }

    @Test
    public void shouldOutputVerticalWithLineHeightPng() {
        EasyPoster poster = createPoster(400, 500);

        poster.addElement(
                TextElement.of("春眠不觉晓处处闻啼鸟夜来风雨声花落知多少")
                        .vertical()
                        .columnRightToLeft()
                        .maxVerticalWidth(400)
                        .setLineHeight(36)
                        .setFontSize(28)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_line_height.png");
    }

    @Test
    public void shouldOutputVerticalCenterAlignPng() {
        EasyPoster poster = createPoster(400, 500);

        poster.addElement(
                TextElement.of("春眠不觉晓")
                        .vertical()
                        .columnRightToLeft()
                        .maxVerticalWidth(400)
                        .verticalAlign(VerticalAlign.CENTER)
                        .setFontSize(28)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_center_align.png");
    }

    @Test
    public void shouldOutputVerticalRichSpanPng() {
        EasyPoster poster = createPoster(400, 500);

        poster.addElement(
                TextElement.of(
                        TextSpan.of("春眠不觉晓").setColor(Color.RED),
                        TextSpan.of("处处闻啼鸟").setColor(new Color(30, 120, 255))
                )
                        .vertical()
                        .columnRightToLeft()
                        .maxVerticalWidth(400)
                        .setFontSize(28)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_rich_span.png");
    }

    @Test
    public void shouldOutputVerticalAutoWrapPng() {
        EasyPoster poster = createPoster(400, 300);

        poster.addElement(
                TextElement.of("春眠不觉晓处处闻啼鸟夜来风雨声花落知多少")
                        .vertical()
                        .columnRightToLeft()
                        .maxVerticalWidth(300)
                        .setFontSize(28)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_auto_wrap.png");
    }

    @Test
    public void shouldOutputVerticalNewlinePng() {
        EasyPoster poster = createPoster(400, 500);

        poster.addElement(
                TextElement.of("春眠不觉晓\n处处闻啼鸟\n夜来风雨声")
                        .vertical()
                        .columnRightToLeft()
                        .setFontSize(28)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_newline.png");
    }

    @Test
    public void shouldOutputVerticalColumnSpacingPng() {
        EasyPoster poster = createPoster(500, 500);

        poster.addElement(
                TextElement.of("春眠不觉晓处处闻啼鸟夜来风雨声花落知多少")
                        .vertical()
                        .columnRightToLeft()
                        .maxVerticalWidth(400)
                        .columnSpacing(20)
                        .setFontSize(28)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_column_spacing.png");
    }

    @Test
    public void shouldOutputVerticalColumnSpacingLRPng() {
        EasyPoster poster = createPoster(500, 500);

        poster.addElement(
                TextElement.of("春眠不觉晓处处闻啼鸟夜来风雨声花落知多少")
                        .vertical()
                        .columnLeftToRight()
                        .maxVerticalWidth(400)
                        .columnSpacing(20)
                        .setFontSize(28)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_column_spacing_lr.png");
    }

    private EasyPoster createPoster(int width, int height) {
        EasyPoster poster = new EasyPoster(width, height);
        poster.getConfig().setFontName("Dialog");
        poster.getConfig().setFontSize(18);
        poster.getConfig().setColor(new Color(32, 32, 32));
        return poster;
    }
}