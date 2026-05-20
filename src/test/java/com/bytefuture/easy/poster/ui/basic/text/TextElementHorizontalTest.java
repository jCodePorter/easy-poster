package com.bytefuture.easy.poster.ui.basic.text;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.basic.TextElement;
import com.bytefuture.easy.poster.element.basic.text.style.TextOverflow;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.Gradient;
import com.bytefuture.easy.poster.model.GradientDirection;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextSpan;
import org.junit.Test;

import java.awt.*;

public class TextElementHorizontalTest {


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

        poster.asFile("png", "out_text_rich_text.png");
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

        poster.asFile("png", "out_text_span_override.png");
    }

    @Test
    public void shouldOutputAlignmentPreviewPng() {
        EasyPoster poster = createPoster(560, 320);

        poster.addElement(
                TextElement.of("LEFT")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setMaxTextWidth(260)
                        .setTextAlign(TextAlign.LEFT)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 24, 0, 0)))
        );

        poster.addElement(
                TextElement.of("CENTER")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setMaxTextWidth(260)
                        .setTextAlign(TextAlign.CENTER)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 110, 0, 0)))
        );

        poster.addElement(
                TextElement.of("RIGHT")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setMaxTextWidth(260)
                        .setTextAlign(TextAlign.RIGHT)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 196, 0, 0)))
        );

        poster.asFile("png", "out_text_alignment.png");
    }

    @Test
    public void shouldOutputCustomLineHeightPreviewPng() {
        EasyPoster poster = createPoster(560, 320);

        poster.addElement(
                TextElement.of("alpha beta gamma delta epsilon zeta eta theta iota kappa")
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setMaxTextWidth(220)
                        .setLineHeight(80)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_text_line_height.png");
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

        poster.asFile("png", "out_text_decoration.png");
    }

    @Test
    public void shouldOutputGradientPreviewPng() {
        EasyPoster poster = createPoster(640, 220);

        poster.addElement(
                TextElement.of(TextSpan.of("Gradient "),
                                TextSpan.of("Color"))
                        .setFontStyle(Font.BOLD)
                        .setFontSize(42)
                        .setGradient(Gradient.of(
                                new Color[]{new Color(255, 96, 72), new Color(86, 92, 255)},
                                GradientDirection.LEFT_RIGHT))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_text_gradient.png");
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
                        .setMaxTextWidth(320)
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
                        .setMaxTextWidth(320)
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
                        .setMaxTextWidth(320)
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
                        .setMaxTextWidth(320)
                        .setTextAlign(TextAlign.CENTER)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 622, 0, 0)))
        );

        poster.asFile("png", "out_text_alignment_comparison.png");
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

        poster.asFile("png", "out_text_span_background.png");
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

        poster.asFile("png", "out_text_span_background_rounded.png");
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

        poster.asFile("png", "out_text_span_background_mixed.png");
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
                        .setMaxTextWidth(220)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_text_span_background_wrap.png");
    }

    @Test
    public void shouldOutputMaxLinesEllipsisPreviewPng() {
        EasyPoster poster = createPoster(560, 260);

        poster.addElement(
                TextElement.of("这是一段超长文本的测试，验证自动换行并进行缩略。这是一段超长文本的测试，验证自动换行并进行缩略。这是一段超长文本的测试，验证自动换行并进行缩略。")
                        .setFontSize(20)
                        .setMaxTextWidth(220)
                        .setMaxLines(2)
                        .setTextOverflow(TextOverflow.ELLIPSIS)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_text_max_lines_ellipsis.png");
    }

    @Test
    public void shouldOutputMaxLinesClipPreviewPng() {
        EasyPoster poster = createPoster(560, 260);

        poster.addElement(
                TextElement.of("这是一段超长文本的测试，验证自动换行并进行缩略。这是一段超长文本的测试，验证自动换行并进行缩略。这是一段超长文本的测试，验证自动换行并进行缩略。")
                        .setFontName("Dialog")
                        .setFontSize(20)
                        .setMaxTextWidth(220)
                        .setMaxLines(2)
                        .setTextOverflow(TextOverflow.CLIP)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24)))
        );

        poster.asFile("png", "out_text_max_lines_clip.png");
    }

    /**
     * 自适应文本：单 TextSpan 自动缩小字体以适应目标宽度
     */
    @Test
    public void shouldOutputAutoFitPreviewPng() {
        EasyPoster poster = createPoster(720, 480);

        poster.addElement(
                TextElement.of("AutoFit / 自适应文本")
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(28)
                        .setColor(new Color(40, 40, 40))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 24, 0, 0)))
        );

        // 场景1：长文本，目标宽度 400px，最小字体 12pt → 字体应被缩小
        poster.addElement(
                TextElement.of("HelloWorldThisIsALongEnglishTextThatNeedsAutoFitToShrinkFontSize")
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(36)
                        .setColor(new Color(220, 56, 56))
                        .setAutoFitText(400, 12)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 80, 0, 0)))
        );

        poster.addElement(
                TextElement.of("↑ autoFit(400, 12) | 原始字号 36pt")
                        .setFontName("Dialog")
                        .setFontSize(16)
                        .setColor(new Color(140, 140, 140))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(444, 80, 0, 0)))
        );

        // 场景2：短文本，目标宽度充裕 → 字体不变
        poster.addElement(
                TextElement.of("Short")
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(36)
                        .setColor(new Color(35, 110, 235))
                        .setAutoFitText(400, 12)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 160, 0, 0)))
        );

        poster.addElement(
                TextElement.of("↑ autoFit(400, 12) | 原始字号 36pt，文本短无需缩放")
                        .setFontName("Dialog")
                        .setFontSize(16)
                        .setColor(new Color(140, 140, 140))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(444, 160, 0, 0)))
        );

        // 场景3：中文长文本，最小字体兜底后触发自动换行
        poster.addElement(
                TextElement.of("这是一段非常长的中文文本内容用于测试自适应换行兜底机制")
                        .setFontName("Dialog")
                        .setFontSize(30)
                        .setColor(new Color(20, 160, 90))
                        .setAutoFitText(300, 20)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 240, 0, 0)))
        );

        poster.addElement(
                TextElement.of("↑ autoFit(300, 20) | 最小字号下仍超宽，触发自动换行")
                        .setFontName("Dialog")
                        .setFontSize(16)
                        .setColor(new Color(140, 140, 140))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 340, 0, 0)))
        );

        // 场景4：多 TextSpan 富文本 → autoFit 不生效
        poster.addElement(
                TextElement.of(
                                TextSpan.of("Red ").setColor(Color.RED),
                                TextSpan.of("Green ").setColor(new Color(20, 160, 90)),
                                TextSpan.of("Blue").setColor(new Color(35, 110, 235)))
                        .setFontName("Dialog")
                        .setFontSize(36)
                        .setAutoFitText(200, 12)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 400, 0, 0)))
        );

        poster.addElement(
                TextElement.of("↑ 多 TextSpan autoFit 不生效，保持原始字号 36pt")
                        .setFontName("Dialog")
                        .setFontSize(16)
                        .setColor(new Color(140, 140, 140))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(444, 400, 0, 0)))
        );

        poster.asFile("png", "out_text_autofit.png");
    }

    private EasyPoster createPoster(int width, int height) {
        EasyPoster poster = new EasyPoster(width, height);
        poster.getConfig().setFontName("Dialog");
        poster.getConfig().setFontSize(18);
        poster.getConfig().setColor(new Color(32, 32, 32));
        return poster;
    }
}
