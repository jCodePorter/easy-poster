package com.bytefuture.easy.poster.ui.v2;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.basic.TextElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import org.junit.Test;

import java.awt.*;

public class V2VerticalPunctuationUiPngTest {

    @Test
    public void shouldOutputVerticalWithAvoidHeadPunctuation() {
        EasyPoster poster = createPoster(400, 500);

        poster.addElement(
                TextElement.of("春眠不觉晓。处处闻啼鸟。夜来风雨声。花落知多少。")
                        .vertical()
                        .columnRightToLeft()
                        .maxVerticalWidth(400)
                        .setFontSize(28)
                        .setLineHeight(36)
                        .columnSpacing(10)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_avoid_head.png");
    }

    @Test
    public void shouldOutputVerticalWithAvoidTailPunctuation() {
        EasyPoster poster = createPoster(400, 500);

        poster.addElement(
                TextElement.of("春眠不觉晓，处处闻啼鸟，夜来风雨声，花落知多少，")
                        .vertical()
                        .columnRightToLeft()
                        .maxVerticalWidth(400)
                        .setFontSize(28)
                        .setLineHeight(36)
                        .columnSpacing(10)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_avoid_tail.png");
    }

    @Test
    public void shouldOutputVerticalWithBracketPair() {
        EasyPoster poster = createPoster(400, 500);

        poster.addElement(
                TextElement.of("春眠（不觉）晓，处处闻啼鸟")
                        .vertical()
                        .columnRightToLeft()
                        .maxVerticalWidth(400)
                        .setFontSize(28)
                        .setLineHeight(36)
                        .columnSpacing(10)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_bracket_pair.png");
    }

    @Test
    public void shouldOutputVerticalWithEllipsis() {
        EasyPoster poster = createPoster(400, 500);

        poster.addElement(
                TextElement.of("春眠不觉晓……处处闻啼鸟……")
                        .vertical()
                        .columnRightToLeft()
                        .maxVerticalWidth(400)
                        .setFontSize(28)
                        .setLineHeight(36)
                        .columnSpacing(10)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_ellipsis.png");
    }

    @Test
    public void shouldOutputVerticalMixedPunctuation() {
        EasyPoster poster = createPoster(400, 500);

        poster.addElement(
                TextElement.of("春眠不觉晓，处处闻啼鸟。夜来（风雨声）……花落知多少。")
                        .vertical()
                        .columnRightToLeft()
                        .maxVerticalWidth(400)
                        .setFontSize(28)
                        .setLineHeight(36)
                        .columnSpacing(10)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_vertical_mixed_punctuation.png");
    }

    private EasyPoster createPoster(int width, int height) {
        EasyPoster poster = new EasyPoster(width, height);
        poster.getConfig().setFontName("Dialog");
        poster.getConfig().setFontSize(18);
        poster.getConfig().setColor(new Color(32, 32, 32));
        return poster;
    }
}