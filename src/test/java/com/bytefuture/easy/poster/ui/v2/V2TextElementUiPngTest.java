package com.bytefuture.easy.poster.ui.v2;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextSpan;
import org.junit.Test;

import java.awt.*;

public class V2TextElementUiPngTest {

    @Test
    public void shouldOutputPlainAsRichPreviewPng() {
        EasyPoster poster = createPoster(520, 180);

        poster.addElement(
                TextElement.of("plain text goes through rich pipeline")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setColor(Color.BLACK)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_text_plain_as_rich.png");
    }

    @Test
    public void shouldOutputRichTextPreviewPng() {
        EasyPoster poster = createPoster(520, 180);

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Hello ").setColor(new Color(220, 40, 40)),
                                TextSpan.of("Rich ").setColor(new Color(30, 120, 255)).setFontStyle(Font.BOLD),
                                TextSpan.of("Text").setColor(new Color(20, 160, 90)).setFontSize(34))
                        .setFontName("Dialog")
                        .setFontSize(30)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_text_rich_text.png");
    }

    @Test
    public void shouldOutputSpanOverridePreviewPng() {
        EasyPoster poster = createPoster(560, 220);

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Block defaults "),
                                TextSpan.of("red small").setColor(Color.RED).setFontSize(12),
                                TextSpan.of("bigger").setFontSize(36).setColor(new Color(70, 90, 220)))
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(26)
                        .setColor(new Color(40, 40, 40))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_text_span_override.png");
    }

    @Test
    public void shouldOutputWrapPreviewPng() {
        EasyPoster poster = createPoster(560, 260);

        poster.addElement(
                TextElement.of("alpha beta gamma delta epsilon zeta eta theta iota kappa")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setAutoWordWrap(220)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_text_wrap.png");
    }

    @Test
    public void shouldOutputAlignmentPreviewPng() {
        EasyPoster poster = createPoster(560, 320);

        poster.addElement(
                TextElement.of("LEFT")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setAutoWordWrap(260)
                        .setTextAlign(TextAlign.LEFT)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 24, 0, 0)))
        );

        poster.addElement(
                TextElement.of("CENTER")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setAutoWordWrap(260)
                        .setTextAlign(TextAlign.CENTER)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 110, 0, 0)))
        );

        poster.addElement(
                TextElement.of("RIGHT")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setAutoWordWrap(260)
                        .setTextAlign(TextAlign.RIGHT)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 196, 0, 0)))
        );

        poster.asFile("png", "out_v2_text_alignment.png");
    }

    @Test
    public void shouldOutputCustomLineHeightPreviewPng() {
        EasyPoster poster = createPoster(560, 320);

        poster.addElement(
                TextElement.of("alpha beta gamma delta epsilon zeta eta theta iota kappa")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setAutoWordWrap(220)
                        .setLineHeight(80)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_text_line_height.png");
    }

    @Test
    public void shouldOutputTextDecorationPreviewPng() {
        EasyPoster poster = createPoster(560, 220);

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Underline ").setUnderline(true).setColor(new Color(220, 40, 40)),
                                TextSpan.of("Strike ").setStrikeThrough(true).setColor(new Color(30, 120, 255)),
                                TextSpan.of("Both").setUnderline(true).setStrikeThrough(true).setColor(new Color(20, 160, 90)))
                        .setFontSize(30)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_text_decoration.png");
    }

    private EasyPoster createPoster(int width, int height) {
        EasyPoster poster = new EasyPoster(width, height);
        poster.getConfig().setFontName("Dialog");
        poster.getConfig().setFontSize(18);
        poster.getConfig().setColor(new Color(32, 32, 32));
        return poster;
    }
}
