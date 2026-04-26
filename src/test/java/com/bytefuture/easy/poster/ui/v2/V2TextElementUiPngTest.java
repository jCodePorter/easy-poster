package com.bytefuture.easy.poster.ui.v2;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextSpan;
import org.junit.Test;

import java.awt.Color;
import java.awt.Font;

public class V2TextElementUiPngTest {

    @Test
    public void shouldOutputPlainAsRichPreviewPng() {
        EasyPoster poster = createPoster(520, 180);

        poster.addElement(
                TextElement.builder("plain text goes through rich pipeline")
                        .fontName("Dialog")
                        .fontSize(28)
                        .color(Color.BLACK)
                        .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
                        .build()
        );

        poster.asFile("png", "out_v2_text_plain_as_rich.png");
    }

    @Test
    public void shouldOutputRichTextPreviewPng() {
        EasyPoster poster = createPoster(520, 180);

        poster.addElement(
                TextElement.builder(
                                TextSpan.of("Hello ").setColor(new Color(220, 40, 40)),
                                TextSpan.of("Rich ").setColor(new Color(30, 120, 255)).setFontStyle(Font.BOLD),
                                TextSpan.of("Text").setColor(new Color(20, 160, 90)).setFontSize(34))
                        .fontName("Dialog")
                        .fontSize(30)
                        .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
                        .build()
        );

        poster.asFile("png", "out_v2_text_rich_text.png");
    }

    @Test
    public void shouldOutputSpanOverridePreviewPng() {
        EasyPoster poster = createPoster(560, 220);

        poster.addElement(
                TextElement.builder(
                                TextSpan.of("Block defaults "),
                                TextSpan.of("red ").setColor(Color.RED),
                                TextSpan.of("bigger").setFontSize(36).setColor(new Color(70, 90, 220)))
                        .fontName("Dialog")
                        .fontStyle(Font.BOLD)
                        .fontSize(26)
                        .color(new Color(40, 40, 40))
                        .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
                        .build()
        );

        poster.asFile("png", "out_v2_text_span_override.png");
    }

    @Test
    public void shouldOutputWrapPreviewPng() {
        EasyPoster poster = createPoster(560, 260);

        poster.addElement(
                TextElement.builder("alpha beta gamma delta epsilon zeta eta theta iota kappa")
                        .fontName("Dialog")
                        .fontSize(28)
                        .autoWordWrap(220)
                        .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
                        .build()
        );

        poster.asFile("png", "out_v2_text_wrap.png");
    }

    @Test
    public void shouldOutputAlignmentPreviewPng() {
        EasyPoster poster = createPoster(560, 320);

        poster.addElement(
                TextElement.builder("LEFT")
                        .fontName("Dialog")
                        .fontSize(28)
                        .autoWordWrap(260)
                        .textAlign(TextAlign.LEFT)
                        .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 24, 0, 0)))
                        .build()
        );

        poster.addElement(
                TextElement.builder("CENTER")
                        .fontName("Dialog")
                        .fontSize(28)
                        .autoWordWrap(260)
                        .textAlign(TextAlign.CENTER)
                        .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 110, 0, 0)))
                        .build()
        );

        poster.addElement(
                TextElement.builder("RIGHT")
                        .fontName("Dialog")
                        .fontSize(28)
                        .autoWordWrap(260)
                        .textAlign(TextAlign.RIGHT)
                        .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 196, 0, 0)))
                        .build()
        );

        poster.asFile("png", "out_v2_text_alignment.png");
    }

    private EasyPoster createPoster(int width, int height) {
        EasyPoster poster = new EasyPoster(width, height);
        poster.getConfig().setFontName("Dialog");
        poster.getConfig().setFontSize(18);
        poster.getConfig().setColor(new Color(32, 32, 32));
        return poster;
    }
}
