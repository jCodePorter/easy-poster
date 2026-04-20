package com.bytefuture.easy.poster.ui.v2;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.advance.ComposeElement;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.geometry.*;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.*;
import org.junit.Test;

import java.awt.*;
import java.util.Arrays;

/**
 * Visual UI tests for v2 text rendering.
 * Each test writes a PNG for manual inspection instead of asserting pixels.
 */
public class V2TextElementPngTest {

    /**
     * 自动换行
     */
    @Test
    public void shouldRenderWrappedTextBlockToPng() {
        EasyPoster poster = newPoster(520, 260);
        poster.addElement(TextElement.builder("Wrapped text should break into multiple lines inside the card and keep a stable line height for manual review.")
                .font("Dialog", Font.PLAIN, 22)
                .color(new Color(44, 62, 80))
                .autoWordWrap(260)
                .lineHeight(34)
                .textBackground(new Color(232, 244, 255), 16)
                .textBackgroundArc(18)
                .position(AbsolutePosition.of(Point.of(36, 44), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_auto_wrap.png");
    }

    /**
     * 自动缩放
     */
    @Test
    public void shouldRenderAutoFitTextToPng() {
        EasyPoster poster = newPoster(520, 240);
        poster.addElement(TextElement.builder("Auto fit should shrink this headline into the target width without breaking layout.")
                .font("Dialog", Font.BOLD, 38)
                .color(new Color(24, 120, 84))
                .autoFitText(320, 16)
                .textAlign(TextAlign.CENTER)
                .textBackground(new Color(224, 246, 236), 14)
                .textBackgroundArc(14, 14)
                .position(AbsolutePosition.of(Point.of(260, 120), Direction.CENTER))
                .build());
        poster.asFile("png", "out_v2_text_auto_fit.png");
    }

    /**
     * 显式换行
     */
    @Test
    public void shouldRenderExplicitNewLinesToPng() {
        EasyPoster poster = newPoster(520, 240);
        poster.addElement(TextElement.builder("Line one\n\nLine three after a blank row")
                .font("Dialog", Font.PLAIN, 22)
                .color(new Color(52, 73, 94))
                .lineHeight(34)
                .position(AbsolutePosition.of(Point.of(36, 40), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_explicit_new_lines.png");
    }

    /**
     * Compose 布局联动高度
     */
    @Test
    public void shouldRenderComposeLayoutWithWrappedTextToPng() {
        RectangleElement header = (RectangleElement) new RectangleElement(110, 52)
                .setColor(new Color(222, 235, 247))
                .setArc(14)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        TextElement content = TextElement.builder("Compose layout should place this wrapped paragraph below the header using the measured multiline height.")
                .font("Dialog", Font.PLAIN, 18)
                .color(new Color(60, 72, 88))
                .lineHeight(28)
                .autoWordWrap(160)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        EasyPoster poster = newPoster(360, 280);
        poster.addElement(ComposeElement.of(header).bottom(content)
                .setPosition(AbsolutePosition.of(Point.of(36, 32), Direction.TOP_LEFT)));
        poster.asFile("png", "out_v2_text_compose_layout.png");
    }

    /**
     * 左对齐/右对齐/居中/两端对齐
     */
    @Test
    public void shouldRenderAlignmentVariantsToPng() {
        EasyPoster poster = new EasyPoster(900, 700);
        addPanel(poster, 40, 40, 360, 260, new Color(246, 249, 255));
        addPanel(poster, 500, 40, 360, 260, new Color(255, 249, 242));
        addPanel(poster, 40, 360, 360, 260, new Color(245, 252, 247));
        addPanel(poster, 500, 360, 360, 260, new Color(252, 245, 248));

        addLabel(poster, "LEFT", 60, 64);
        addLabel(poster, "RIGHT", 520, 64);
        addLabel(poster, "CENTER", 60, 384);
        addLabel(poster, "JUSTIFY", 520, 384);

        poster.addElement(buildAlignedBlock(TextAlign.LEFT, 70, 104));
        poster.addElement(buildAlignedBlock(TextAlign.RIGHT, 530, 104));
        poster.addElement(buildAlignedBlock(TextAlign.CENTER, 70, 424));
        poster.addElement(buildAlignedBlock(TextAlign.JUSTIFY, 530, 424));
        poster.asFile("png", "out_v2_text_alignments.png");
    }

    /**
     * 多行截断省略
     */
    @Test
    public void shouldRenderMaxLinesEllipsisToPng() {
        EasyPoster poster = newPoster(520, 260);
        poster.addElement(TextElement.builder("This wrapped text should stop after two lines and show an ellipsis at the end of the visible content block.")
                .font("Dialog", Font.PLAIN, 22)
                .color(new Color(85, 60, 42))
                .autoWordWrap(250)
                .lineHeight(34)
                .maxLines(2)
                .textBackground(new Color(255, 241, 222), 14)
                .textBackgroundArc(14)
                .position(AbsolutePosition.of(Point.of(36, 40), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_max_lines_ellipsis.png");
    }

    /**
     * 单行省略
     */
    @Test
    public void shouldRenderSingleLineEllipsisToPng() {
        EasyPoster poster = newPoster(520, 200);
        poster.addElement(TextElement.builder("This single line should be ellipsized when layout width is smaller than the rendered text width.")
                .font("Dialog", Font.PLAIN, 24)
                .color(new Color(44, 62, 80))
                .layoutWidth(220)
                .overflowStrategy(TextOverflowStrategy.ELLIPSIS)
                .textBackground(new Color(234, 243, 255), 12)
                .position(AbsolutePosition.of(Point.of(36, 70), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_single_line_ellipsis.png");
    }

    /**
     * 单行裁切
     */
    @Test
    public void shouldRenderSingleLineClipToPng() {
        EasyPoster poster = newPoster(520, 200);
        poster.addElement(TextElement.builder("This single line should be clipped at the configured layout width without adding ellipsis.")
                .font("Dialog", Font.PLAIN, 24)
                .color(new Color(70, 70, 70))
                .layoutWidth(220)
                .overflowStrategy(TextOverflowStrategy.CLIP)
                .textBackground(new Color(245, 245, 245), 12)
                .position(AbsolutePosition.of(Point.of(36, 70), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_single_line_clip.png");
    }

    /**
     * 字距
     */
    @Test
    public void shouldRenderLetterSpacingToPng() {
        EasyPoster poster = newPoster(540, 240);
        addLabel(poster, "default", 40, 34);
        addLabel(poster, "letterSpacing=4", 40, 118);

        poster.addElement(TextElement.builder("spacing sample")
                .font("Dialog", Font.PLAIN, 28)
                .color(new Color(40, 40, 40))
                .position(AbsolutePosition.of(Point.of(40, 58), Direction.TOP_LEFT))
                .build());
        poster.addElement(TextElement.builder("spacing sample")
                .font("Dialog", Font.PLAIN, 28)
                .color(new Color(40, 40, 40))
                .letterSpacing(4)
                .position(AbsolutePosition.of(Point.of(40, 144), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_letter_spacing.png");
    }

    /**
     * 背景色 + 内边距 + 圆角
     */
    @Test
    public void shouldRenderBackgroundPaddingAndArcToPng() {
        EasyPoster poster = newPoster(560, 240);
        poster.addElement(TextElement.builder("Background, custom padding, and rounded corners should all be visible.")
                .font("Dialog", Font.PLAIN, 22)
                .color(new Color(57, 57, 57))
                .textBackground(new Color(255, 233, 186))
                .textPadding(24, 14, 18, 10)
                .textBackgroundArc(24, 18)
                .position(AbsolutePosition.of(Point.of(40, 72), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_background_padding_arc.png");
    }

    /**
     * 下划线
     */
    @Test
    public void shouldRenderUnderlineToPng() {
        EasyPoster poster = newPoster(520, 220);
        addLabel(poster, "plain", 40, 36);
        addLabel(poster, "underline", 40, 118);

        poster.addElement(TextElement.builder("Underline preview")
                .font("Dialog", Font.PLAIN, 28)
                .color(new Color(38, 50, 56))
                .position(AbsolutePosition.of(Point.of(40, 62), Direction.TOP_LEFT))
                .build());
        poster.addElement(TextElement.builder("Underline preview")
                .font("Dialog", Font.PLAIN, 28)
                .color(new Color(38, 50, 56))
                .underline(true)
                .position(AbsolutePosition.of(Point.of(40, 144), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_underline.png");
    }

    /**
     * 阴影 + 描边
     */
    @Test
    public void shouldRenderShadowAndStrokeToPng() {
        EasyPoster poster = newPoster(560, 220);
        poster.addElement(TextElement.builder("Shadow + Stroke")
                .font("Dialog", Font.BOLD, 34)
                .color(new Color(59, 89, 152))
                .shadow(TextShadow.of(new Color(40, 40, 40, 120), 4, 4))
                .stroke(TextStroke.of(Color.BLACK, 2f))
                .position(AbsolutePosition.of(Point.of(40, 88), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_shadow_stroke.png");
    }

    /**
     * 富文本颜色/粗体/下划线组合
     */
    @Test
    public void shouldRenderRichTextColorsToPng() {
        EasyPoster poster = newPoster(620, 220);
        poster.addElement(TextElement.builder(
                        TextSpan.of("Red").setColor(Color.RED),
                        TextSpan.of(" / ").setColor(new Color(140, 140, 140)),
                        TextSpan.of("Bold Blue").setColor(Color.BLUE).setFontStyle(Font.BOLD),
                        TextSpan.of(" / ").setColor(new Color(140, 140, 140)),
                        TextSpan.of("Green Underline").setColor(new Color(42, 125, 74)).setUnderline(true))
                .font("Dialog", Font.PLAIN, 28)
                .position(AbsolutePosition.of(Point.of(40, 92), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_rich_colors.png");
    }

    /**
     * 富文本自动换行
     */
    @Test
    public void shouldRenderRichTextWrapToPng() {
        EasyPoster poster = newPoster(560, 280);
        poster.addElement(TextElement.builder(
                        TextSpan.of("Rich ").setColor(new Color(192, 57, 43)).setFontStyle(Font.BOLD),
                        TextSpan.of("text ").setColor(new Color(41, 128, 185)),
                        TextSpan.of("should wrap across multiple styled spans for visual inspection.")
                                .setColor(new Color(44, 62, 80)))
                .font("Dialog", Font.PLAIN, 22)
                .lineHeight(34)
                .autoWordWrap(280)
                .textBackground(new Color(244, 248, 255), 14)
                .position(AbsolutePosition.of(Point.of(40, 44), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_rich_wrap.png");
    }

    /**
     * 富文本显式换行
     */
    @Test
    public void shouldRenderRichTextExplicitNewLinesToPng() {
        EasyPoster poster = newPoster(520, 260);
        poster.addElement(TextElement.builder(
                        TextSpan.of("Top line\n").setColor(new Color(192, 57, 43)),
                        TextSpan.of("\n"),
                        TextSpan.of("Bottom line").setColor(new Color(41, 128, 185)).setFontStyle(Font.BOLD))
                .font("Dialog", Font.PLAIN, 24)
                .lineHeight(34)
                .position(AbsolutePosition.of(Point.of(40, 48), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_rich_new_lines.png");
    }

    /**
     * 富文本单行省略
     */
    @Test
    public void shouldRenderRichTextEllipsisToPng() {
        EasyPoster poster = newPoster(560, 220);
        poster.addElement(TextElement.builder(
                        TextSpan.of("This ").setColor(new Color(192, 57, 43)),
                        TextSpan.of("rich text line ").setColor(new Color(41, 128, 185)),
                        TextSpan.of("should be ellipsized within the configured width.").setColor(new Color(39, 174, 96)))
                .font("Dialog", Font.PLAIN, 24)
                .layoutWidth(240)
                .overflowStrategy(TextOverflowStrategy.ELLIPSIS)
                .textBackground(new Color(245, 247, 250), 12)
                .position(AbsolutePosition.of(Point.of(40, 78), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_rich_ellipsis.png");
    }

    /**
     * 富文本多行截断
     */
    @Test
    public void shouldRenderRichTextMaxLinesToPng() {
        EasyPoster poster = newPoster(600, 300);
        poster.addElement(TextElement.builder(
                        TextSpan.of("Rich ").setColor(new Color(192, 57, 43)).setUnderline(true),
                        TextSpan.of("content ").setColor(new Color(41, 128, 185)).setFontStyle(Font.BOLD),
                        TextSpan.of("should wrap, respect max lines, and end with ellipsis in the second line.")
                                .setColor(new Color(142, 68, 173)).setStrikeThrough(true))
                .font("Dialog", Font.PLAIN, 22)
                .lineHeight(36)
                .autoWordWrap(300)
                .maxLines(2)
                .textBackground(new Color(249, 243, 255), 14)
                .position(AbsolutePosition.of(Point.of(40, 48), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_rich_max_lines.png");
    }

    /**
     * 基线定位
     */
    @Test
    public void shouldRenderBaselineAnchorsToPng() {
        EasyPoster poster = newPoster(760, 260);
        int centerY = 150;
        poster.getConfig().setDebug(true);
        poster.addLineElement(Point.of(40, centerY), Point.of(720, centerY))
                .setColor(new Color(52, 152, 219))
                .setBorderSize(1);

        addBaselineSample(poster, "TOP", BaseLine.TOP, 70, centerY);
        addBaselineSample(poster, "CENTER", BaseLine.CENTER, 240, centerY);
        addBaselineSample(poster, "BOTTOM", BaseLine.BOTTOM, 430, centerY);
        addBaselineSample(poster, "BASE_LINE", BaseLine.BASE_LINE, 610, centerY);
        poster.asFile("png", "out_v2_text_baselines.png");
    }

    @Test
    public void shouldRenderBuilderSurfaceShowcaseToPng() {
        EasyPoster poster = newPoster(700, 260);
        poster.addElement(TextElement.builder("Builder surface showcase")
                .font(new Font("Dialog", Font.ITALIC, 26))
                .color(new Color(52, 73, 94))
                .shadow(new Color(0, 0, 0, 70), 2, 2)
                .stroke(new Color(255, 255, 255), 1.2f)
                .letterSpacing(2)
                .textBackground(new Color(232, 244, 255))
                .textPadding(16)
                .textBackgroundArc(18)
                .position(AbsolutePosition.of(Point.of(40, 48), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder("A")
                .textSpans(Arrays.asList(
                        TextSpan.of("S").setColor(new Color(192, 57, 43)),
                        TextSpan.of("P").setColor(new Color(41, 128, 185)),
                        TextSpan.of("A").setColor(new Color(39, 174, 96)),
                        TextSpan.of("N").setColor(new Color(142, 68, 173))))
                .fontSize(40)
                .position(AbsolutePosition.of(Point.of(40, 136), Direction.TOP_LEFT))
                .build());
        poster.asFile("png", "out_v2_text_builder_surface.png");
    }

    private EasyPoster newPoster(int width, int height) {
        EasyPoster poster = new EasyPoster(width, height);
        poster.addRectangleElement(width, height)
                .setColor(Color.WHITE)
                .setPosition(AbsolutePosition.of(Point.ORIGIN_COORDINATE, Direction.TOP_LEFT));
        return poster;
    }

    private TextElement buildAlignedBlock(TextAlign align, int x, int y) {
        return TextElement.builder("alpha beta gamma delta epsilon zeta eta theta")
                .font("Dialog", Font.PLAIN, 20)
                .color(new Color(60, 72, 88))
                .autoWordWrap(240)
                .lineHeight(32)
                .textAlign(align)
                .textBackground(new Color(255, 255, 255), 12)
                .textBackgroundArc(12)
                .position(AbsolutePosition.of(Point.of(x, y), Direction.TOP_LEFT))
                .build();
    }

    private void addBaselineSample(EasyPoster poster, String label, BaseLine baseLine, int x, int y) {
        poster.addLineElement(Point.of(x - 20, y), Point.of(x + 100, y))
                .setColor(new Color(231, 76, 60, 150))
                .setBorderSize(1);
        poster.addCircleElement(4)
                .setColor(new Color(231, 76, 60))
                .setPosition(AbsolutePosition.of(Point.of(x, y), Direction.CENTER));
        poster.addElement(TextElement.builder(label)
                .font("Dialog", Font.PLAIN, 14)
                .color(new Color(120, 120, 120))
                .position(AbsolutePosition.of(Point.of(x, y - 48), Direction.TOP_LEFT))
                .build());
        poster.addElement(TextElement.builder("Anchor")
                .font("Dialog", Font.BOLD, 24)
                .color(new Color(39, 174, 96))
                .baseLine(baseLine)
                .position(AbsolutePosition.of(Point.of(x, y), Direction.TOP_LEFT))
                .build());
    }

    private void addLabel(EasyPoster poster, String text, int x, int y) {
        poster.addElement(TextElement.builder(text)
                .font("Dialog", Font.BOLD, 16)
                .color(new Color(100, 100, 100))
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

    private static void example6_poster() {
        System.out.println("=== 示例6：海报集成 ===");

        EasyPoster poster = new EasyPoster(800, 600);

        // 标题
        TextElement title = TextElement.builder("海报标题")
                .font("Microsoft YaHei", Font.BOLD, 48)
                .color(Color.BLACK)
                .position(RelativePosition.of(Direction.TOP_CENTER, Margin.of(0, 80, 0, 0)))
                .shadow(Color.GRAY, 3, 3)
                .build();

        // 正文
        TextElement content = TextElement.builder(
                        "这是海报的正文内容，支持自动换行。\n" +
                                "V2架构完全独立设计，不依赖V1。"
                ).font("Microsoft YaHei", Font.PLAIN, 20)
                .color(new Color(80, 80, 80))
                .autoWordWrap(600)
                .lineHeight(32)
                .position(RelativePosition.of(Direction.CENTER))
                .build();

        // 底部
        TextElement footer = TextElement.builder("© 2025 ByteFuture")
                .fontName("Arial")
                .fontSize(14)
                .color(Color.GRAY)
                .position(RelativePosition.of(Direction.BOTTOM_CENTER, Margin.of(0, 0, 50, 0)))
                .build();

        // 添加元素
        poster.addElement(title);
        poster.addElement(content);
        poster.addElement(footer);

        poster.asFile("png", "out_v2_text_poster_style.png");
    }

    @Test
    public void renderV2TextConfigPreviewToPng() {
        EasyPoster poster = new EasyPoster(1200, 900);

        addPanel(poster, 40, 40, 520, 260, new Color(248, 250, 255));
        addPanel(poster, 640, 40, 520, 260, new Color(250, 248, 243));
        addPanel(poster, 40, 340, 520, 240, new Color(244, 250, 246));
        addPanel(poster, 640, 340, 520, 240, new Color(250, 245, 248));

        poster.addElement(TextElement.builder("V2 基础样式")
                .font("Microsoft YaHei", Font.BOLD, 24)
                .color(new Color(33, 37, 41))
                .position(AbsolutePosition.of(Point.of(70, 80), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder("阴影 + 描边 + 字距 + 背景")
                .font("Microsoft YaHei", Font.BOLD, 34)
                .color(new Color(32, 67, 136))
                .shadow(new Color(0, 0, 0, 80), 3, 3)
                .stroke(Color.WHITE, 1.5f)
                .letterSpacing(4)
                .underline(true)
                .textBackground(new Color(220, 234, 255), 18)
                .textBackgroundArc(18)
                .position(AbsolutePosition.of(Point.of(70, 150), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder("V2 自动换行")
                .font("Microsoft YaHei", Font.BOLD, 24)
                .color(new Color(33, 37, 41))
                .position(AbsolutePosition.of(Point.of(670, 80), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder(
                        "这一段用于观察自动换行、行高、背景内边距和最大宽度的实际表现。"
                                + "如果参数生效，文本会稳定落在浅色卡片内，并且每行间距保持一致。")
                .font("Microsoft YaHei", Font.PLAIN, 22)
                .color(new Color(74, 63, 53))
                .autoWordWrap(420)
                .lineHeight(36)
                .textAlign(TextAlign.LEFT)
                .textBackground(new Color(255, 241, 219), 16)
                .textBackgroundArc(14)
                .position(AbsolutePosition.of(Point.of(670, 145), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder("V2 自动缩放")
                .font("Microsoft YaHei", Font.BOLD, 24)
                .color(new Color(33, 37, 41))
                .position(AbsolutePosition.of(Point.of(70, 380), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder("这一行初始字号很大，但会被压缩到 300px 宽度以内")
                .font("Microsoft YaHei", Font.BOLD, 42)
                .color(new Color(27, 110, 72))
                .autoFitText(300, 16)
                .textAlign(TextAlign.CENTER)
                .textBackground(new Color(220, 245, 231), 14)
                .textBackgroundArc(12)
                .position(AbsolutePosition.of(Point.of(300, 475), Direction.TOP_CENTER))
                .build());

        poster.addElement(TextElement.builder("V2 富文本")
                .font("Microsoft YaHei", Font.BOLD, 24)
                .color(new Color(33, 37, 41))
                .position(AbsolutePosition.of(Point.of(670, 380), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.rich(
                        TextSpan.of("指定字号").setFontSize(40).setColor(Color.ORANGE),
                        TextSpan.of(" / ").setColor(new Color(120, 120, 120)),
                        TextSpan.of("加粗").setFontStyle(Font.BOLD).setColor(new Color(187, 38, 73)),
                        TextSpan.of(" / ").setColor(new Color(120, 120, 120)),
                        TextSpan.of("斜体").setFontStyle(Font.ITALIC).setColor(new Color(74, 90, 224)),
                        TextSpan.of(" / ").setColor(new Color(120, 120, 120)),
                        TextSpan.of("下划线").setUnderline(true).setColor(new Color(35, 124, 86)),
                        TextSpan.of(" / ").setColor(new Color(120, 120, 120)),
                        TextSpan.of("删除线").setStrikeThrough(true).setColor(new Color(125, 78, 168)))
                .setPosition(AbsolutePosition.of(Point.of(670, 470), Direction.TOP_LEFT))
                .setColor(new Color(60, 60, 60)));

        poster.addElement(TextElement.builder("输出文件: out_v2_text_config_preview.png")
                .font("Microsoft YaHei", Font.PLAIN, 16)
                .color(new Color(120, 120, 120))
                .position(AbsolutePosition.of(Point.of(40, 845), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_config_preview.png");
    }
}
