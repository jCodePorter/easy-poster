package com.bytefuture.easy.poster.ui.v2;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.Gradient;
import com.bytefuture.easy.poster.model.GradientDirection;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextSpan;
import com.bytefuture.easy.poster.model.TextStroke;
import org.junit.Test;

import java.awt.Color;
import java.awt.Font;

/**
 * Visual UI tests for recent V2 rich-text expansion features.
 * Each test writes a PNG for manual inspection.
 */
public class V2TextElementRichExpansionPngTest {

    @Test
    public void shouldRenderRichJustifyToPng() {
        EasyPoster poster = newPoster(820, 300);
        addLabel(poster, "rich justify", 40, 36);

        poster.addElement(TextElement.builder(
                        TextSpan.of("alpha ").setColor(new Color(192, 57, 43)),
                        TextSpan.of("beta ").setFontStyle(Font.BOLD).setColor(new Color(41, 128, 185)),
                        TextSpan.of("gamma delta epsilon zeta eta theta iota kappa")
                                .setColor(new Color(44, 62, 80)))
                .font("Dialog", Font.PLAIN, 24)
                .autoWordWrap(340)
                .lineHeight(38)
                .textAlign(TextAlign.JUSTIFY)
                .textBackground(new Color(246, 249, 255), 14)
                .textBackgroundArc(16)
                .position(AbsolutePosition.of(Point.of(40, 82), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_rich_justify.png");
    }

    @Test
    public void shouldRenderRichAutoFitToPng() {
        EasyPoster poster = newPoster(760, 240);
        addLabel(poster, "rich auto fit", 40, 36);

        poster.addElement(TextElement.builder(
                        TextSpan.of("BIG").setFontSize(40).setColor(new Color(192, 57, 43)),
                        TextSpan.of(" / ").setFontSize(24).setFontStyle(Font.BOLD).setColor(new Color(127, 140, 141)),
                        TextSpan.of("small").setFontSize(16).setColor(new Color(41, 128, 185)))
                .font("Dialog", Font.PLAIN, 28)
                .autoFitText(220, 12)
                .textBackground(new Color(245, 250, 246), 14)
                .textBackgroundArc(14)
                .position(AbsolutePosition.of(Point.of(40, 96), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_rich_auto_fit.png");
    }

    @Test
    public void shouldRenderRichTextBlockGradientToPng() {
        EasyPoster poster = newPoster(840, 340);
        addPanel(poster, 40, 40, 320, 240, new Color(246, 249, 255));
        addPanel(poster, 440, 40, 320, 240, new Color(255, 249, 242));

        addLabel(poster, "rich span fills", 60, 64);
        addLabel(poster, "rich block gradient", 460, 64);

        poster.addElement(TextElement.builder(
                        TextSpan.of("Gradient ").setColor(new Color(192, 57, 43)).setFontStyle(Font.BOLD),
                        TextSpan.of("should ").setColor(new Color(41, 128, 185)),
                        TextSpan.of("flow through the whole wrapped rich block instead of restarting on each span.")
                                .setColor(new Color(39, 174, 96)).setUnderline(true))
                .font("Dialog", Font.PLAIN, 24)
                .autoWordWrap(240)
                .lineHeight(38)
                .textBackground(new Color(255, 255, 255), 18)
                .textBackgroundArc(18)
                .position(AbsolutePosition.of(Point.of(76, 106), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder(
                        TextSpan.of("Gradient ").setColor(new Color(192, 57, 43)).setFontStyle(Font.BOLD),
                        TextSpan.of("should ").setColor(new Color(41, 128, 185)),
                        TextSpan.of("flow through the whole wrapped rich block instead of restarting on each span.")
                                .setColor(new Color(39, 174, 96)).setUnderline(true))
                .font("Dialog", Font.PLAIN, 24)
                .gradient(Gradient.of(new String[]{"#ff6b6b", "#4dabf7"}, GradientDirection.LEFT_RIGHT))
                .autoWordWrap(240)
                .lineHeight(38)
                .textBackground(new Color(255, 255, 255), 18)
                .textBackgroundArc(18)
                .position(AbsolutePosition.of(Point.of(476, 106), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_rich_block_gradient.png");
    }

    @Test
    public void shouldRenderSpanLevelStyleOverridesToPng() {
        EasyPoster poster = newPoster(820, 260);
        addLabel(poster, "span-level background / shadow / stroke / baseline shift", 40, 36);

        poster.addElement(TextElement.builder(
                        TextSpan.of("code")
                                .setFontName("Monospaced")
                                .setBackgroundColor(new Color(255, 238, 170))
                                .setShadow(com.bytefuture.easy.poster.model.TextShadow.of(new Color(120, 120, 120), 2, 2))
                                .setStroke(TextStroke.of(Color.BLACK, 1f))
                                .setBaselineShift(-3)
                                .setColor(new Color(41, 128, 185)),
                        TextSpan.of(" + ").setColor(new Color(120, 120, 120)),
                        TextSpan.of("normal").setColor(new Color(44, 62, 80)))
                .font("Dialog", Font.PLAIN, 28)
                .position(AbsolutePosition.of(Point.of(40, 110), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_span_style_overrides.png");
    }

    @Test
    public void shouldRenderVerticalRichTextToPng() {
        EasyPoster poster = newPoster(420, 360);
        addLabel(poster, "vertical rich text", 40, 36);

        poster.addElement(TextElement.builder(
                        TextSpan.of("春").setColor(new Color(192, 57, 43)).setFontSize(34),
                        TextSpan.of("夏").setColor(new Color(39, 174, 96)).setFontStyle(Font.BOLD),
                        TextSpan.of("秋").setColor(new Color(41, 128, 185)),
                        TextSpan.of("冬").setColor(new Color(142, 68, 173)).setUnderline(true))
                .font("Microsoft YaHei", Font.PLAIN, 30)
                .textLayoutMode(com.bytefuture.easy.poster.model.TextLayoutMode.VERTICAL)
                .layoutHeight(180)
                .columnSpacing(18)
                .textBackground(new Color(249, 243, 255), 16)
                .textBackgroundArc(16)
                .position(AbsolutePosition.of(Point.of(110, 90), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_vertical_rich.png");
    }

    @Test
    public void shouldRenderVerticalTextBlockGradientToPng() {
        EasyPoster poster = newPoster(620, 400);
        addPanel(poster, 40, 40, 220, 280, new Color(246, 249, 255));
        addPanel(poster, 320, 40, 220, 280, new Color(255, 249, 242));

        addLabel(poster, "solid vertical", 60, 64);
        addLabel(poster, "vertical block gradient", 340, 64);

        poster.addElement(TextElement.builder("unused")
                .vertical("天地玄黄宇宙洪荒")
                .font("Microsoft YaHei", Font.BOLD, 30)
                .color(new Color(44, 62, 80))
                .lineHeight(36)
                .layoutHeight(190)
                .columnSpacing(16)
                .textBackground(new Color(255, 255, 255), 18)
                .textBackgroundArc(18)
                .position(AbsolutePosition.of(Point.of(118, 104), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder("unused")
                .vertical("天地玄黄宇宙洪荒")
                .font("Microsoft YaHei", Font.BOLD, 30)
                .lineHeight(36)
                .layoutHeight(190)
                .columnSpacing(16)
                .gradient(Gradient.of(new String[]{"#ff6b6b", "#4dabf7"}, GradientDirection.TOP_BOTTOM))
                .textBackground(new Color(255, 255, 255), 18)
                .textBackgroundArc(18)
                .position(AbsolutePosition.of(Point.of(398, 104), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_vertical_block_gradient.png");
    }

    @Test
    public void shouldRenderExpandedHtmlStylesToPng() {
        EasyPoster poster = newPoster(860, 280);
        addLabel(poster, "html background / font-family / sup / sub / small / code", 40, 36);

        poster.addElement(TextElement.builderHtml(
                        "<span style='background-color:#ffeeaa;font-family:Monospaced'>code</span>"
                                + " = x<sup>2</sup> + y<sub>i</sub>"
                                + " <small>tiny</small>"
                                + " <code>mono</code>")
                .font("Dialog", Font.PLAIN, 28)
                .lineHeight(40)
                .position(AbsolutePosition.of(Point.of(40, 104), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_html_expanded_styles.png");
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
