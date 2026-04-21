package com.bytefuture.easy.poster.ui.v2;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.TextOverflowStrategy;
import org.junit.Test;

import java.awt.Color;
import java.awt.Font;

/**
 * Visual UI tests for HTML -> rich text rendering in V2 TextElement.
 * Each test writes a PNG for manual inspection.
 */
public class V2TextElementHtmlPngTest {

    @Test
    public void shouldRenderInlineHtmlStylesToPng() {
        EasyPoster poster = newPoster(760, 240);
        addLabel(poster, "inline html styles", 40, 36);

        poster.addElement(TextElement.builderHtml(
                        "<span style='color:#e74c3c'>Red</span>"
                                + " / "
                                + "<strong>Bold</strong>"
                                + " / "
                                + "<em>Italic</em>"
                                + " / "
                                + "<u>Underline</u>"
                                + " / "
                                + "<s>Strike</s>")
                .font("Dialog", Font.PLAIN, 30)
                .position(AbsolutePosition.of(Point.of(40, 92), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_html_inline_styles.png");
    }

    @Test
    public void shouldRenderSpanStyleAttributesToPng() {
        EasyPoster poster = newPoster(760, 260);
        addLabel(poster, "span style attributes", 40, 36);

        poster.addElement(TextElement.builderHtml(
                        "<span style='color:#2ecc71;font-size:20px'>green 20px</span><br/>"
                                + "<span style='font-weight:700;color:#2980b9'>bold 700 blue</span><br/>"
                                + "<span style='font-style:italic;color:#8e44ad'>italic purple</span><br/>"
                                + "<span style='text-decoration:underline line-through;color:#d35400'>underline + strike</span>")
                .font("Dialog", Font.PLAIN, 26)
                .lineHeight(34)
                .position(AbsolutePosition.of(Point.of(40, 72), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_html_span_styles.png");
    }

    @Test
    public void shouldRenderParagraphAndBreakStructureToPng() {
        EasyPoster poster = newPoster(660, 320);
        addLabel(poster, "paragraph and break", 40, 36);

        poster.addElement(TextElement.builderHtml(
                        "<p><strong>Paragraph 1</strong><br/>line 2 in paragraph 1</p>"
                                + "<p><span style='color:#2980b9'>Paragraph 2</span> with inline text.</p>"
                                + "<div><u>Div block</u> tail</div>")
                .font("Dialog", Font.PLAIN, 24)
                .lineHeight(34)
                .textBackground(new Color(246, 249, 255), 14)
                .textBackgroundArc(14)
                .position(AbsolutePosition.of(Point.of(40, 72), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_html_blocks_and_breaks.png");
    }

    @Test
    public void shouldRenderWrappedHtmlRichTextToPng() {
        EasyPoster poster = newPoster(700, 320);
        addLabel(poster, "wrapped html rich text", 40, 36);

        poster.addElement(TextElement.builderHtml(
                        "<span style='color:#c0392b'>Rich HTML</span> "
                                + "<strong>should wrap</strong> "
                                + "<span style='color:#2c3e50'>with the existing V2 rich text engine, preserving inline colors and styles across lines.</span>")
                .font("Dialog", Font.PLAIN, 24)
                .autoWordWrap(320)
                .lineHeight(36)
                .textBackground(new Color(244, 248, 255), 14)
                .textBackgroundArc(16)
                .position(AbsolutePosition.of(Point.of(40, 72), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_html_wrap.png");
    }

    @Test
    public void shouldRenderHtmlEllipsisToPng() {
        EasyPoster poster = newPoster(680, 240);
        addLabel(poster, "html ellipsis", 40, 36);

        poster.addElement(TextElement.builderHtml(
                        "<span style='color:#e74c3c'>This rich html line </span>"
                                + "<span style='color:#2980b9'>should be ellipsized within the configured width.</span>")
                .font("Dialog", Font.PLAIN, 26)
                .layoutWidth(280)
                .overflowStrategy(TextOverflowStrategy.ELLIPSIS)
                .textBackground(new Color(245, 247, 250), 12)
                .position(AbsolutePosition.of(Point.of(40, 96), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_html_ellipsis.png");
    }

    @Test
    public void shouldRenderFontTagCompatibilityToPng() {
        EasyPoster poster = newPoster(760, 240);
        addLabel(poster, "font tag compatibility", 40, 36);

        poster.addElement(TextElement.builderHtml(
                        "<font color='#e74c3c' size='5'>font color size=5</font>"
                                + " / "
                                + "<font color='blue' size='3'>blue size=3</font>")
                .font("Dialog", Font.PLAIN, 22)
                .position(AbsolutePosition.of(Point.of(40, 96), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_html_font_tag.png");
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
}
