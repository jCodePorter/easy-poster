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

    @Test
    public void shouldOutputJustifyPreviewPng() {
        EasyPoster poster = createPoster(560, 320);

        poster.addElement(
                TextElement.of("alpha beta gamma delta epsilon zeta eta theta")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setAutoWordWrap(260)
                        .setTextAlign(TextAlign.JUSTIFY)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 24, 0, 0)))
        );

        poster.addElement(
                TextElement.of("alpha beta\ngamma delta")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setLayoutWidth(260)
                        .setTextAlign(TextAlign.JUSTIFY)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 180, 0, 0)))
        );

        poster.asFile("png", "out_v2_text_justify.png");
    }

    @Test
    public void shouldOutputAlignmentComparisonPreviewPng() {
        EasyPoster poster = createPoster(720, 760);
        String sample = "alpha beta gamma delta epsilon zeta eta theta iota kappa lambda mu";

        poster.addElement(
                TextElement.of("LEFT")
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(24)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 24, 0, 0)))
        );
        poster.addElement(
                TextElement.of(sample)
                        .setFontName("Dialog")
                        .setFontSize(26)
                        .setAutoWordWrap(320)
                        .setTextAlign(TextAlign.LEFT)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 64, 0, 0)))
        );

        poster.addElement(
                TextElement.of("RIGHT")
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(24)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 210, 0, 0)))
        );
        poster.addElement(
                TextElement.of(sample)
                        .setFontName("Dialog")
                        .setFontSize(26)
                        .setAutoWordWrap(320)
                        .setTextAlign(TextAlign.RIGHT)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 250, 0, 0)))
        );

        poster.addElement(
                TextElement.of("JUSTIFY")
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(24)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 396, 0, 0)))
        );
        poster.addElement(
                TextElement.of(sample)
                        .setFontName("Dialog")
                        .setFontSize(26)
                        .setAutoWordWrap(320)
                        .setTextAlign(TextAlign.JUSTIFY)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 436, 0, 0)))
        );

        poster.addElement(
                TextElement.of("CENTER")
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(24)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 582, 0, 0)))
        );
        poster.addElement(
                TextElement.of(sample)
                        .setFontName("Dialog")
                        .setFontSize(26)
                        .setAutoWordWrap(320)
                        .setTextAlign(TextAlign.CENTER)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 622, 0, 0)))
        );

        poster.asFile("png", "out_v2_text_alignment_comparison.png");
    }

    @Test
    public void shouldOutputSpanBackgroundPreviewPng() {
        EasyPoster poster = createPoster(560, 220);

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Normal "),
                                TextSpan.of("Highlight")
                                        .setBackgroundColor(new Color(255, 220, 120))
                                        .setBackgroundPadding(6),
                                TextSpan.of(" Text"))
                        .setFontName("Dialog")
                        .setFontSize(30)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_text_span_background.png");
    }

    @Test
    public void shouldOutputRoundedSpanBackgroundPreviewPng() {
        EasyPoster poster = createPoster(560, 220);

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Rounded Label")
                                        .setBackgroundColor(new Color(255, 190, 190))
                                        .setBackgroundPadding(8)
                                        .setBackgroundRadius(12))
                        .setFontName("Dialog")
                        .setFontSize(30)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_text_span_background_rounded.png");
    }

    @Test
    public void shouldOutputMixedSpanBackgroundPreviewPng() {
        EasyPoster poster = createPoster(560, 220);

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Gold").setBackgroundColor(new Color(255, 220, 120)).setBackgroundPadding(5),
                                TextSpan.of(" Plain "),
                                TextSpan.of("Blue").setBackgroundColor(new Color(160, 210, 255)).setBackgroundPadding(5).setBackgroundRadius(10))
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_text_span_background_mixed.png");
    }

    @Test
    public void shouldOutputWrappedSpanBackgroundPreviewPng() {
        EasyPoster poster = createPoster(560, 280);

        poster.addElement(
                TextElement.of(
                                TextSpan.of("alpha beta gamma delta epsilon zeta eta theta")
                                        .setBackgroundColor(new Color(180, 235, 255))
                                        .setBackgroundPadding(4)
                                        .setBackgroundRadius(6))
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setAutoWordWrap(220)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_v2_text_span_background_wrap.png");
    }

    /**
     * 输出 v2 TextElement 对外展示图
     */
    @Test
    public void shouldOutputV2TextShowcasePng() {
        EasyPoster poster = createPoster(1400, 1880);

        poster.addElement(
                TextElement.of(
                                TextSpan.of("V2 ").setColor(new Color(40, 40, 40)).setFontStyle(Font.BOLD),
                                TextSpan.of("TextElement")
                                        .setColor(new Color(20, 20, 20))
                                        .setFontStyle(Font.BOLD)
                                        .setBackgroundColor(new Color(255, 225, 140))
                                        .setBackgroundPadding(10)
                                        .setBackgroundRadius(18))
                        .setFontName("Dialog")
                        .setFontSize(72)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(72, 64, 0, 0)))
        );

        poster.addElement(
                TextElement.of("海报渲染中的下一代富文本引擎  |  Next-Gen Rich Text for Poster Rendering")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setColor(new Color(88, 88, 88))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 156, 0, 0)))
        );

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Rich Span / 富文本片段")
                                        .setBackgroundColor(new Color(205, 230, 255))
                                        .setBackgroundPadding(8)
                                        .setBackgroundRadius(16))
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(30)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 260, 0, 0)))
        );

        poster.addElement(
                TextElement.of(
                                TextSpan.of("中文 ").setColor(new Color(45, 45, 45)),
                                TextSpan.of("English ").setColor(new Color(35, 110, 235)).setFontStyle(Font.BOLD),
                                TextSpan.of("Bold ").setColor(new Color(220, 70, 70)).setFontStyle(Font.BOLD).setFontSize(42),
                                TextSpan.of("Light ").setColor(new Color(120, 120, 120)).setFontSize(24),
                                TextSpan.of("混排").setColor(new Color(20, 160, 90)).setFontSize(38))
                        .setFontName("Dialog")
                        .setFontSize(34)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 320, 0, 0)))
        );

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Decoration / 文本装饰")
                                        .setBackgroundColor(new Color(255, 218, 218))
                                        .setBackgroundPadding(8)
                                        .setBackgroundRadius(16))
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(30)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 430, 0, 0)))
        );

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Underline").setUnderline(true).setColor(new Color(220, 56, 56)),
                                TextSpan.of("   "),
                                TextSpan.of("Strike").setStrikeThrough(true).setColor(new Color(40, 120, 255)),
                                TextSpan.of("   "),
                                TextSpan.of("Both").setUnderline(true).setStrikeThrough(true).setColor(new Color(20, 160, 90)))
                        .setFontName("Dialog")
                        .setFontSize(34)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 492, 0, 0)))
        );

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Background Highlight / 背景高亮")
                                        .setBackgroundColor(new Color(223, 245, 221))
                                        .setBackgroundPadding(8)
                                        .setBackgroundRadius(16))
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(30)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 610, 0, 0)))
        );

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Rounded")
                                        .setBackgroundColor(new Color(255, 208, 120))
                                        .setBackgroundPadding(8)
                                        .setBackgroundRadius(16),
                                TextSpan.of(" "),
                                TextSpan.of("continuous")
                                        .setBackgroundColor(new Color(255, 208, 120))
                                        .setBackgroundPadding(8)
                                        .setBackgroundRadius(16),
                                TextSpan.of(" "),
                                TextSpan.of("highlight")
                                        .setBackgroundColor(new Color(255, 208, 120))
                                        .setBackgroundPadding(8)
                                        .setBackgroundRadius(16),
                                TextSpan.of("  +  "),
                                TextSpan.of("Blue Pill")
                                        .setBackgroundColor(new Color(188, 225, 255))
                                        .setBackgroundPadding(8)
                                        .setBackgroundRadius(16))
                        .setFontName("Dialog")
                        .setFontSize(32)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 674, 0, 0)))
        );

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Wrap & Line Height / 自动换行与行高")
                                        .setBackgroundColor(new Color(236, 226, 255))
                                        .setBackgroundPadding(8)
                                        .setBackgroundRadius(16))
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(30)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 800, 0, 0)))
        );

        poster.addElement(
                TextElement.of("同一套文字样式可以直接控制 layout width、自动换行与更舒展的 line height，让中英混排在海报中保持稳定节奏与清晰层次")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setColor(new Color(52, 52, 52))
                        .setAutoWordWrap(560)
                        .setLineHeight(54)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 864, 0, 0)))
        );

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Letter Spacing / 字间距")
                                        .setBackgroundColor(new Color(255, 236, 206))
                                        .setBackgroundPadding(8)
                                        .setBackgroundRadius(16))
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(30)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 1090, 0, 0)))
        );

        poster.addElement(
                TextElement.of("NORMAL TRACKING")
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(26)
                        .setColor(new Color(44, 44, 44))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 1152, 0, 0)))
        );

        poster.addElement(
                TextElement.of("EXPANDED TRACKING")
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(26)
                        .setLetterSpacing(10)
                        .setColor(new Color(44, 44, 44))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 1200, 0, 0)))
        );

        poster.addElement(
                TextElement.of(
                                TextSpan.of("Alignment / 对齐方式")
                                        .setBackgroundColor(new Color(214, 239, 240))
                                        .setBackgroundPadding(8)
                                        .setBackgroundRadius(16))
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(30)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(760, 260, 0, 0)))
        );

        poster.addElement(
                TextElement.of("LEFT\n内容起笔清晰，适合信息说明")
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(24)
                        .setAutoWordWrap(520)
                        .setTextAlign(TextAlign.LEFT)
                        .setLineHeight(42)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(760, 320, 0, 0)))
        );

        poster.addElement(
                TextElement.of("CENTER\n视觉居中，适合标题与口号")
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(24)
                        .setAutoWordWrap(520)
                        .setTextAlign(TextAlign.CENTER)
                        .setLineHeight(42)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(760, 426, 0, 0)))
        );

        poster.addElement(
                TextElement.of("RIGHT\n数字、标签与辅助信息更利落")
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(24)
                        .setAutoWordWrap(520)
                        .setTextAlign(TextAlign.RIGHT)
                        .setLineHeight(42)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(760, 532, 0, 0)))
        );

        poster.addElement(
                TextElement.of("JUSTIFY\n让多行正文在给定宽度内形成更整齐的版面节奏，适合长段落说明与卡片文案展示")
                        .setFontName("Dialog")
                        .setFontSize(26)
                        .setAutoWordWrap(520)
                        .setTextAlign(TextAlign.JUSTIFY)
                        .setLineHeight(48)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(760, 648, 0, 0)))
        );

        poster.addElement(
                TextElement.of(
                                TextSpan.of("One Engine, Many Voices")
                                        .setBackgroundColor(new Color(255, 225, 140))
                                        .setBackgroundPadding(10)
                                        .setBackgroundRadius(18))
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(36)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(760, 940, 0, 0)))
        );

        poster.addElement(
                TextElement.of("从品牌标题到说明正文，从高亮标签到节奏化长文案，v2 TextElement 让海报文本拥有统一而灵活的表达方式")
                        .setFontName("Dialog")
                        .setFontSize(30)
                        .setColor(new Color(50, 50, 50))
                        .setAutoWordWrap(520)
                        .setLineHeight(52)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(760, 1012, 0, 0)))
        );

        poster.addElement(
                TextElement.of(
                                TextSpan.of("中文  English  Bold  Underline  Highlight  Wrap  Justify")
                                        .setBackgroundColor(new Color(232, 232, 232))
                                        .setBackgroundPadding(10)
                                        .setBackgroundRadius(20))
                        .setFontName("Dialog")
                        .setFontSize(24)
                        .setLetterSpacing(2)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(760, 1244, 0, 0)))
        );

        poster.addElement(
                TextElement.of("Built for expressive poster typography")
                        .setFontName("Dialog")
                        .setFontSize(32)
                        .setFontStyle(Font.BOLD)
                        .setColor(new Color(36, 36, 36))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 1700, 0, 0)))
        );

        poster.addElement(
                TextElement.of("v2 TextElement covers rich span styling, decoration, alignment, wrapping, line height, letter spacing and span-level background highlight in one consistent rendering pipeline")
                        .setFontName("Dialog")
                        .setFontSize(24)
                        .setColor(new Color(92, 92, 92))
                        .setAutoWordWrap(1240)
                        .setLineHeight(40)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(74, 1750, 0, 0)))
        );

        poster.asFile("png", "out_v2_text_showcase.png");
    }

    private EasyPoster createPoster(int width, int height) {
        EasyPoster poster = new EasyPoster(width, height);
        poster.getConfig().setFontName("Dialog");
        poster.getConfig().setFontSize(18);
        poster.getConfig().setColor(new Color(32, 32, 32));
        return poster;
    }
}
