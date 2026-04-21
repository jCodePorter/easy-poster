package com.bytefuture.easy.poster.func.text;

import com.bytefuture.easy.poster.element.advance.ComposeElement;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.element.advance.RepeatElement;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.Config;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextOverflowStrategy;
import com.bytefuture.easy.poster.model.TextShadow;
import com.bytefuture.easy.poster.model.TextSpan;
import com.bytefuture.easy.poster.model.TextStroke;
import com.bytefuture.easy.poster.text.layout.LayoutLine;
import com.bytefuture.easy.poster.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.text.split.TextSplitRequest;
import com.bytefuture.easy.poster.text.split.TextSplitResult;
import com.bytefuture.easy.poster.text.split.TextSplitterSimpleImpl;
import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class V2TextElementTest {

    @Test
    public void shouldReportWrappedTextTotalHeight() {
        PosterContext context = createContext();
        String text = "TextElementUpgrade should report the full wrapped height.";
        TextElement element = TextElement.builder(text)
                .font("Dialog", Font.PLAIN, 18)
                .lineHeight(28)
                .autoWordWrap(120)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        Dimension dimension = element.calculateDimension(context, 400, 300);
        FontMetrics fontMetrics = context.getGraphics().getFontMetrics(new Font("Dialog", Font.PLAIN, 18));
        TextSplitResult splitResult = new TextSplitterSimpleImpl().split(TextSplitRequest.of(text, 120, fontMetrics));

        Assert.assertTrue(splitResult.getLines().size() > 1);
        Assert.assertEquals(splitResult.getLines().size() * 28L, dimension.getHeight());
    }

    @Test
    public void shouldNotMutateConfiguredFontObjectDuringAutoFit() {
        PosterContext context = createContext();
        Font font = new Font("Dialog", Font.PLAIN, 32);
        TextElement element = TextElement.builder("Auto fit sample text")
                .font(font)
                .autoFitText(120, 10)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        Dimension dimension = element.calculateDimension(context, 400, 300);

        Assert.assertEquals(font, element.getConfig().getFont());
        Assert.assertTrue(dimension.getWidth() <= 120);
        Assert.assertEquals(dimension.getWidth(), element.calculateDimension(context, 400, 300).getWidth());
    }

    @Test
    public void shouldPreserveExplicitNewLinesWithoutAutoWrap() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("hello\n\nworld")
                .font("Dialog", Font.PLAIN, 18)
                .lineHeight(26)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        Dimension dimension = element.calculateDimension(context, 400, 300);

        Assert.assertEquals(78L, dimension.getHeight());
    }

    @Test
    public void shouldProvideCompleteHeightToComposeLayout() {
        PosterContext context = createContext();
        RectangleElement header = (RectangleElement) new RectangleElement(80, 40)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        TextElement content = TextElement.builder("Compose layout should respect multiline height.")
                .font("Dialog", Font.PLAIN, 18)
                .lineHeight(24)
                .autoWordWrap(80)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        Dimension textDimension = content.calculateDimension(context, 80, 200);
        Dimension composeDimension = ComposeElement.of(header)
                .bottom(content)
                .calculateDimension(context, 300, 300);

        Assert.assertTrue(textDimension.getHeight() > 24);
        Assert.assertEquals(40L + textDimension.getHeight(), composeDimension.getHeight());
    }

    @Test
    public void shouldIncreaseMeasuredWidthWhenLetterSpacingEnabled() {
        PosterContext context = createContext();
        TextElement compact = TextElement.builder("spacing")
                .font("Dialog", Font.PLAIN, 18)
                .position(AbsolutePosition.of(Point.of(20, 20), Direction.TOP_LEFT))
                .build();
        TextElement expanded = TextElement.builder("spacing")
                .font("Dialog", Font.PLAIN, 18)
                .letterSpacing(4)
                .position(AbsolutePosition.of(Point.of(20, 20), Direction.TOP_LEFT))
                .build();

        Assert.assertTrue(expanded.calculateDimension(context, 400, 300).getWidth()
                > compact.calculateDimension(context, 400, 300).getWidth());
    }

    @Test
    public void shouldHonorAbsoluteTopCenterAnchorDirection() {
        PosterContext context = createContext();
        Point anchor = Point.of(200, 80);
        TextElement element = TextElement.builder("center")
                .font("Dialog", Font.PLAIN, 18)
                .baseLine(BaseLine.TOP)
                .position(AbsolutePosition.of(anchor, Direction.TOP_CENTER))
                .build();

        Dimension dimension = element.calculateDimension(context, 400, 300);
        Point expected = AbsolutePosition.of(anchor, Direction.TOP_CENTER)
                .calculate(400, 300, dimension.getWidth(), dimension.getHeight());

        Assert.assertEquals(expected.getX(), dimension.getPoint().getX());
        Assert.assertEquals(expected.getY(), dimension.getPoint().getY());
    }

    @Test
    public void shouldRespectRichTextFontSizeWithoutExplicitFontName() {
        PosterContext context = createContext();
        TextElement defaultRich = TextElement.builder(
                        TextSpan.of("A").setColor(java.awt.Color.RED),
                        TextSpan.of("B").setColor(java.awt.Color.BLUE))
                .position(AbsolutePosition.of(Point.of(20, 20), Direction.TOP_LEFT))
                .build();
        TextElement largerRich = TextElement.builder(
                        TextSpan.of("A").setColor(java.awt.Color.RED),
                        TextSpan.of("B").setColor(java.awt.Color.BLUE))
                .fontSize(30)
                .position(AbsolutePosition.of(Point.of(20, 20), Direction.TOP_LEFT))
                .build();

        Dimension defaultDimension = defaultRich.calculateDimension(context, 400, 300);
        Dimension largerDimension = largerRich.calculateDimension(context, 400, 300);

        Assert.assertTrue(largerDimension.getWidth() > defaultDimension.getWidth());
        Assert.assertTrue(largerDimension.getHeight() > defaultDimension.getHeight());
    }

    @Test
    public void shouldTreatCenterBaselineAnchorAsLineBoxMidpoint() {
        PosterContext context = createContext();
        Point anchor = Point.of(200, 120);
        TextElement element = TextElement.builder("center")
                .font("Dialog", Font.PLAIN, 18)
                .baseLine(BaseLine.CENTER)
                .position(AbsolutePosition.of(anchor, Direction.TOP_LEFT))
                .build();

        Dimension dimension = element.calculateDimension(context, 400, 300);

        Assert.assertEquals(anchor.getY(), dimension.getPoint().getY() + dimension.getHeight() / 2);
    }

    @Test
    public void shouldRespectPerSpanFontSizeInV2RichTextLayout() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(
                        TextSpan.of("small").setFontSize(14).setColor(java.awt.Color.RED),
                        TextSpan.of(" big").setFontSize(32).setColor(java.awt.Color.BLUE))
                .font("Dialog", Font.PLAIN, 20)
                .position(AbsolutePosition.of(Point.of(20, 20), Direction.TOP_LEFT))
                .build();

        Dimension dimension = element.calculateDimension(context, 400, 300);

        Assert.assertTrue(dimension.getWidth() > 0);
        Assert.assertTrue(dimension.getHeight() >= 32);
    }

    @Test
    public void shouldInferCenterAlignmentFromRelativeCenterPosition() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("center aligned")
                .font("Dialog", Font.PLAIN, 18)
                .position(RelativePosition.of(Direction.CENTER))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(TextAlign.CENTER, layout.getTextAlign());
    }

    @Test
    public void shouldInferRightAlignmentFromRelativeRightPosition() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("right aligned")
                .font("Dialog", Font.PLAIN, 18)
                .position(RelativePosition.of(Direction.TOP_RIGHT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(TextAlign.RIGHT, layout.getTextAlign());
    }

    @Test
    public void shouldPreferExplicitAlignmentOverRelativePositionInference() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("forced left")
                .font("Dialog", Font.PLAIN, 18)
                .textAlign(TextAlign.LEFT)
                .position(RelativePosition.of(Direction.CENTER))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(TextAlign.LEFT, layout.getTextAlign());
    }

    @Test
    public void shouldCenterAlignLinesInsideMeasuredBlock() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("alpha beta gamma delta epsilon zeta eta theta")
                .font("Dialog", Font.PLAIN, 18)
                .autoWordWrap(120)
                .textAlign(TextAlign.CENTER)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);
        LayoutLine firstLine = layout.getLines().get(0);

        Assert.assertEquals(TextAlign.CENTER, layout.getTextAlign());
        Assert.assertTrue(layout.getLines().size() > 1);
        Assert.assertTrue(firstLine.getPoint().getX() > layout.getPoint().getX());
    }

    @Test
    public void shouldJustifyWrappedIntermediateLine() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("alpha beta gamma delta epsilon zeta eta theta")
                .font("Dialog", Font.PLAIN, 18)
                .autoWordWrap(120)
                .textAlign(TextAlign.JUSTIFY)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertTrue(layout.getLines().size() > 1);
        Assert.assertTrue(layout.getLines().get(0).isJustified());
        Assert.assertFalse(layout.getLines().get(layout.getLines().size() - 1).isJustified());
        Assert.assertEquals(layout.getContentWidth(), layout.getLines().get(0).getRenderWidth());
    }

    @Test
    public void shouldApplyMaxLinesAndEllipsisDuringLayout() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("This plain text block should wrap and then truncate at the configured max lines.")
                .font("Dialog", Font.PLAIN, 20)
                .lineHeight(28)
                .autoWordWrap(120)
                .maxLines(2)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(2, layout.getLines().size());
        Assert.assertEquals(56, layout.getHeight());
        Assert.assertTrue(layout.getLines().get(1).getText().endsWith("..."));
        Assert.assertTrue(layout.isTruncated());
    }

    @Test
    public void shouldExposeExplicitLayoutWidthForSingleLineEllipsis() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("This is a single line that should be ellipsized.")
                .font("Dialog", Font.PLAIN, 20)
                .layoutWidth(120)
                .overflowStrategy(TextOverflowStrategy.ELLIPSIS)
                .position(AbsolutePosition.of(Point.of(20, 20), Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(1, layout.getLines().size());
        Assert.assertEquals(120, layout.getWidth());
        Assert.assertTrue(layout.getLines().get(0).getText().endsWith("..."));
        Assert.assertTrue(layout.getLines().get(0).getWidth() <= 120);
    }

    @Test
    public void shouldExposeExplicitLayoutWidthForSingleLineClip() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("This is a single line that should be clipped.")
                .font("Dialog", Font.PLAIN, 20)
                .layoutWidth(120)
                .overflowStrategy(TextOverflowStrategy.CLIP)
                .position(AbsolutePosition.of(Point.of(20, 20), Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(1, layout.getLines().size());
        Assert.assertTrue(layout.isClipOverflow());
        Assert.assertFalse(layout.isTruncated());
        Assert.assertEquals(120, layout.getWidth());
        Assert.assertEquals(120, layout.getLines().get(0).getRenderWidth());
    }

    @Test
    public void shouldKeepSingleLineWhenOnlyLayoutWidthIsConfiguredWithoutImplicitClipping() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("alpha beta gamma delta epsilon")
                .font("Dialog", Font.PLAIN, 20)
                .layoutWidth(120)
                .position(AbsolutePosition.of(Point.of(20, 20), Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(1, layout.getLines().size());
        Assert.assertEquals(TextOverflowStrategy.VISIBLE, layout.getOverflowStrategy());
        Assert.assertFalse(layout.isClipOverflow());
        Assert.assertFalse(layout.isTruncated());
        Assert.assertTrue(layout.getWidth() > 120);
    }

    @Test
    public void shouldWrapLongTokenWhenLetterSpacingConsumesAvailableWidth() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("abcdefghij")
                .font("Dialog", Font.PLAIN, 18)
                .letterSpacing(4)
                .autoWordWrap(60)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertTrue(layout.getLines().size() > 1);
        Assert.assertEquals(60, layout.getContentWidth());
    }

    @Test
    public void shouldExposeShadowStrokeArcAndSpanCollectionOnBuilder() {
        TextShadow shadow = TextShadow.of(java.awt.Color.GRAY, 2, 3);
        TextStroke stroke = TextStroke.of(java.awt.Color.BLACK, 1.5f);
        TextSpan spanA = TextSpan.of("A").setColor(java.awt.Color.RED);
        TextSpan spanB = TextSpan.of("B").setColor(java.awt.Color.BLUE);

        TextElement element = TextElement.builder("builder")
                .shadow(shadow)
                .stroke(stroke)
                .textBackgroundArc(10, 12)
                .textSpans(Arrays.asList(spanA, spanB))
                .build();

        Assert.assertEquals(shadow, element.getConfig().getShadow());
        Assert.assertEquals(stroke, element.getConfig().getStroke());
        Assert.assertEquals(10, element.getConfig().getTextBackgroundArcWidth());
        Assert.assertEquals(12, element.getConfig().getTextBackgroundArcHeight());
        Assert.assertEquals(2, element.getConfig().getTextSpans().size());
        Assert.assertEquals("A", element.getConfig().getTextSpans().get(0).getText());
        Assert.assertEquals("B", element.getConfig().getTextSpans().get(1).getText());
    }

    @Test
    public void shouldSupportFourSideTextPaddingOnBuilder() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("padding")
                .font("Dialog", Font.PLAIN, 20)
                .textBackground(java.awt.Color.YELLOW)
                .textPadding(10, 6, 8, 4)
                .position(AbsolutePosition.of(Point.of(20, 20), Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(10, layout.getTextPadding().getLeft());
        Assert.assertEquals(6, layout.getTextPadding().getTop());
        Assert.assertEquals(8, layout.getTextPadding().getRight());
        Assert.assertEquals(4, layout.getTextPadding().getBottom());
    }

    @Test
    public void shouldRejectNegativeFourSideTextPadding() {
        try {
            TextElement.builder("padding")
                    .textPadding(1, 2, 3, -1);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("padding cannot be negative", ex.getMessage());
        }
    }

    @Test
    public void shouldSupportDirectFontObjectOnBuilder() {
        PosterContext context = createContext();
        Font font = new Font("Dialog", Font.BOLD, 26);

        TextElement element = TextElement.builder("font object")
                .font(font)
                .position(AbsolutePosition.of(Point.of(20, 20), Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(font, element.getConfig().getFont());
        Assert.assertEquals(Font.BOLD, layout.getFont().getStyle());
        Assert.assertEquals(26, layout.getFont().getSize());
    }

    @Test
    public void shouldExpandMeasuredBoundsWhenShadowAndStrokeEnabled() {
        PosterContext context = createContext();
        TextElement plain = TextElement.builder("decorate")
                .font("Dialog", Font.PLAIN, 24)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();
        TextElement decorated = TextElement.builder("decorate")
                .font("Dialog", Font.PLAIN, 24)
                .shadow(Color.GRAY, 4, 5)
                .stroke(Color.BLACK, 2f)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        Dimension plainDimension = plain.calculateDimension(context, 400, 300);
        Dimension decoratedDimension = decorated.calculateDimension(context, 400, 300);

        Assert.assertTrue(decoratedDimension.getWidth() >= plainDimension.getWidth());
        Assert.assertTrue(decoratedDimension.getHeight() >= plainDimension.getHeight());
    }

    @Test
    public void shouldRenderUnderlineAsAdditionalPixels() {
        TextElement plain = TextElement.builder("underline")
                .font("Dialog", Font.PLAIN, 24)
                .color(Color.BLACK)
                .position(AbsolutePosition.of(Point.of(20, 40), Direction.TOP_LEFT))
                .build();
        TextElement underlined = TextElement.builder("underline")
                .font("Dialog", Font.PLAIN, 24)
                .color(Color.BLACK)
                .underline(true)
                .position(AbsolutePosition.of(Point.of(20, 40), Direction.TOP_LEFT))
                .build();

        BufferedImage plainImage = renderElement(plain, 220, 100);
        BufferedImage underlineImage = renderElement(underlined, 220, 100);

        Assert.assertTrue(countNonTransparentPixels(underlineImage) > countNonTransparentPixels(plainImage));
    }

    @Test
    public void shouldRenderTextBackgroundInsidePaddingArea() {
        TextElement element = TextElement.builder("bg")
                .font("Dialog", Font.PLAIN, 24)
                .textBackground(new Color(255, 220, 120), Margin.of(12))
                .position(AbsolutePosition.of(Point.of(20, 50), Direction.TOP_LEFT))
                .build();

        BufferedImage image = renderElement(element, 180, 120);

        Assert.assertTrue(countColorLikePixels(image, new Color(255, 220, 120), 8) > 0);
    }

    @Test
    public void shouldRenderShadowAndStrokeWithoutBreakingTextOutput() {
        TextElement element = TextElement.builder("shadow")
                .font("Dialog", Font.PLAIN, 28)
                .color(Color.BLUE)
                .shadow(new Color(80, 80, 80), 4, 4)
                .stroke(Color.BLACK, 2f)
                .position(AbsolutePosition.of(Point.of(20, 50), Direction.TOP_LEFT))
                .build();

        BufferedImage image = renderElement(element, 260, 120);

        Assert.assertTrue(countNonTransparentPixels(image) > 0);
        Assert.assertTrue(countColorLikePixels(image, Color.BLACK, 20) > 0);
    }

    @Test
    public void shouldWrapRichTextAcrossStyledSpans() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(
                        TextSpan.of("This ").setColor(Color.RED),
                        TextSpan.of("rich ").setFontStyle(Font.BOLD),
                        TextSpan.of("text block should wrap across spans.").setColor(Color.BLUE))
                .font("Dialog", Font.PLAIN, 20)
                .lineHeight(28)
                .autoWordWrap(120)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertTrue(layout.getLines().size() > 1);
        Assert.assertTrue(layout.getLines().get(0).hasRichFragments());
    }

    @Test
    public void shouldMeasureRichTextAcrossExplicitNewLines() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(
                        TextSpan.of("hello\n"),
                        TextSpan.of("\n"),
                        TextSpan.of("world").setColor(Color.BLUE))
                .font("Dialog", Font.PLAIN, 18)
                .lineHeight(26)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(3, layout.getLines().size());
        Assert.assertEquals(78, layout.getHeight());
    }

    @Test
    public void shouldEllipsizeRichTextWithinConfiguredWidth() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(
                        TextSpan.of("This ").setColor(Color.RED),
                        TextSpan.of("rich text line should be ellipsized.").setColor(Color.BLUE))
                .font("Dialog", Font.PLAIN, 20)
                .layoutWidth(120)
                .overflowStrategy(TextOverflowStrategy.ELLIPSIS)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(1, layout.getLines().size());
        Assert.assertTrue(layout.getLines().get(0).getText().endsWith("..."));
        Assert.assertTrue(layout.getLines().get(0).getWidth() <= 120);
    }

    @Test
    public void shouldApplyRichMaxLinesAfterWrap() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(
                        TextSpan.of("This").setColor(Color.RED),
                        TextSpan.of(" rich").setFontStyle(Font.BOLD),
                        TextSpan.of(" text block should wrap and then truncate at the configured max lines.").setColor(Color.PINK))
                .font("Dialog", Font.PLAIN, 20)
                .lineHeight(40)
                .autoWordWrap(240)
                .maxLines(2)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);
        Dimension dimension = element.calculateDimension(context, 400, 300);

        Assert.assertEquals(2, layout.getLines().size());
        Assert.assertEquals(80, dimension.getHeight());
        Assert.assertTrue(layout.getLines().get(1).getText().endsWith("..."));
        Assert.assertTrue(layout.isTruncated());
    }

    @Test
    public void shouldParseHtmlIntoStyledTextSpans() {
        TextElement element = TextElement.builderHtml(
                        "<span style='color:#ff0000'>Red</span>"
                                + "<strong>Bold</strong>"
                                + "<u>Line</u>"
                                + "<span style='font-size:24px'>Big</span>")
                .build();

        Assert.assertEquals(4, element.getConfig().getTextSpans().size());

        TextSpan red = element.getConfig().getTextSpans().get(0);
        Assert.assertEquals("Red", red.getText());
        Assert.assertEquals(new Color(255, 0, 0), red.getColor());

        TextSpan bold = element.getConfig().getTextSpans().get(1);
        Assert.assertEquals("Bold", bold.getText());
        Assert.assertEquals(Integer.valueOf(Font.BOLD), bold.getFontStyle());

        TextSpan underline = element.getConfig().getTextSpans().get(2);
        Assert.assertEquals("Line", underline.getText());
        Assert.assertEquals(Boolean.TRUE, underline.getUnderline());

        TextSpan big = element.getConfig().getTextSpans().get(3);
        Assert.assertEquals("Big", big.getText());
        Assert.assertEquals(Integer.valueOf(24), big.getFontSize());
    }

    @Test
    public void shouldConvertHtmlBreaksAndBlocksIntoRichTextLines() {
        PosterContext context = createContext();
        TextElement element = TextElement.builderHtml("<p>hello<br/>world</p><p>again</p>")
                .font("Dialog", Font.PLAIN, 18)
                .lineHeight(26)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(3, layout.getLines().size());
        Assert.assertEquals("hello", layout.getLines().get(0).getText());
        Assert.assertEquals("world", layout.getLines().get(1).getText());
        Assert.assertEquals("again", layout.getLines().get(2).getText());
    }

    @Test
    public void shouldRenderHtmlUsingRichTextFragments() {
        PosterContext context = createContext();
        TextElement element = TextElement.builderHtml(
                        "<span style='color:#ff0000'>R</span><span style='color:#0000ff'>B</span>")
                .font("Dialog", Font.PLAIN, 28)
                .position(AbsolutePosition.of(Point.of(20, 40), Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);
        BufferedImage image = renderElement(element, 160, 100);

        Assert.assertTrue(layout.getLines().get(0).hasRichFragments());
        Assert.assertTrue(countColorLikePixels(image, Color.RED, 20) > 0);
        Assert.assertTrue(countColorLikePixels(image, Color.BLUE, 20) > 0);
    }

    @Test
    public void shouldSupportAbsolutePositionAndBaselineOffsets() {
        PosterContext context = createContext();
        Point anchor = Point.of(80, 90);

        TextLayoutResult topLayout = measureLayout(TextElement.builder("anchor")
                .font("Dialog", Font.PLAIN, 18)
                .baseLine(BaseLine.TOP)
                .position(AbsolutePosition.of(anchor, Direction.TOP_LEFT))
                .build(), context, 400, 300);
        TextLayoutResult centerLayout = measureLayout(TextElement.builder("anchor")
                .font("Dialog", Font.PLAIN, 18)
                .baseLine(BaseLine.CENTER)
                .position(AbsolutePosition.of(anchor, Direction.TOP_LEFT))
                .build(), context, 400, 300);
        TextLayoutResult bottomLayout = measureLayout(TextElement.builder("anchor")
                .font("Dialog", Font.PLAIN, 18)
                .baseLine(BaseLine.BOTTOM)
                .position(AbsolutePosition.of(anchor, Direction.TOP_LEFT))
                .build(), context, 400, 300);
        TextLayoutResult baseLineLayout = measureLayout(TextElement.builder("anchor")
                .font("Dialog", Font.PLAIN, 18)
                .baseLine(BaseLine.BASE_LINE)
                .position(AbsolutePosition.of(anchor, Direction.TOP_LEFT))
                .build(), context, 400, 300);

        Assert.assertEquals(anchor.getX(), topLayout.getPoint().getX());
        Assert.assertEquals(anchor.getY(), topLayout.getPoint().getY());
        Assert.assertTrue(centerLayout.getPoint().getY() < topLayout.getPoint().getY());
        Assert.assertTrue(bottomLayout.getPoint().getY() < centerLayout.getPoint().getY());
        Assert.assertTrue(baseLineLayout.getPoint().getY() <= topLayout.getPoint().getY());
    }

    @Test
    public void shouldRejectRichTextAutoFitWithPosterException() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(TextSpan.of("rich"))
                .font("Dialog", Font.PLAIN, 18)
                .autoFitText(100, 10)
                .position(AbsolutePosition.of(Point.of(20, 20), Direction.TOP_LEFT))
                .build();

        try {
            measureLayout(element, context, 400, 300);
            Assert.fail("Expected PosterException");
        } catch (PosterException ex) {
            Assert.assertEquals("rich text span does not support autoFitText yet", ex.getMessage());
        }
    }

    @Test
    public void shouldRejectRichTextJustifyWithPosterException() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(TextSpan.of("rich"))
                .font("Dialog", Font.PLAIN, 18)
                .textAlign(TextAlign.JUSTIFY)
                .position(AbsolutePosition.of(Point.of(20, 20), Direction.TOP_LEFT))
                .build();

        try {
            measureLayout(element, context, 400, 300);
            Assert.fail("Expected PosterException");
        } catch (PosterException ex) {
            Assert.assertEquals("rich text span does not support justify yet", ex.getMessage());
        }
    }

    @Test
    public void shouldBeCompatibleWithRepeatElement() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("repeat")
                .font("Dialog", Font.PLAIN, 18)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        RepeatElement repeatElement = new RepeatElement(element);
        Assert.assertEquals(com.bytefuture.easy.poster.geometry.Point.ORIGIN_COORDINATE,
                repeatElement.render(context, 120, 80));
    }

    @Test
    public void shouldValidateBuilderArgumentsConsistently() {
        expectIllegalArgument("fontSize must be positive", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").fontSize(0);
            }
        });
        expectIllegalArgument("lineHeight must be positive", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").lineHeight(0);
            }
        });
        expectIllegalArgument("maxWidth must be positive", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").autoWordWrap(0);
            }
        });
        expectIllegalArgument("layoutWidth must be positive", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").layoutWidth(0);
            }
        });
        expectIllegalArgument("targetWidth must be positive", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").autoFitText(0, 1);
            }
        });
        expectIllegalArgument("minFontSize must be positive", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").autoFitText(10, 0);
            }
        });
        expectIllegalArgument("textAlign cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").textAlign(null);
            }
        });
        expectIllegalArgument("maxLines must be positive", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").maxLines(0);
            }
        });
        expectIllegalArgument("ellipsis cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").ellipsis(null);
            }
        });
        expectIllegalArgument("shadow color cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").shadow((Color) null, 1, 1);
            }
        });
        expectIllegalArgument("shadow cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").shadow((TextShadow) null);
            }
        });
        expectIllegalArgument("stroke color cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").stroke((Color) null, 1f);
            }
        });
        expectIllegalArgument("stroke width must be positive", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").stroke(Color.BLACK, 0f);
            }
        });
        expectIllegalArgument("stroke cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").stroke((TextStroke) null);
            }
        });
        expectIllegalArgument("textBackgroundColor cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").textBackground(null);
            }
        });
        expectIllegalArgument("textPadding cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").textBackground(Color.WHITE, (Margin) null);
            }
        });
        expectIllegalArgument("padding cannot be negative", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").textPadding(-1);
            }
        });
        expectIllegalArgument("padding cannot be negative", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").textPadding(-1, 1);
            }
        });
        expectIllegalArgument("arc cannot be negative", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").textBackgroundArc(-1);
            }
        });
        expectIllegalArgument("arc cannot be negative", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").textBackgroundArc(1, -1);
            }
        });
        expectIllegalArgument("textSplitter cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").textSplitter(null);
            }
        });
        expectIllegalArgument("textSpan cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").textSpan(null);
            }
        });
        expectIllegalArgument("font cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").font((Font) null);
            }
        });
    }

    private TextLayoutResult measureLayout(TextElement element, PosterContext context, int posterWidth, int posterHeight) {
        return new com.bytefuture.easy.poster.element.v2.TextLayoutEngine()
                .layout(element.getConfig(), element.getPosition(), element.getRotate(), context, posterWidth, posterHeight);
    }

    private PosterContext createContext() {
        BufferedImage image = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setFont(new Font("Dialog", Font.PLAIN, 18));

        PosterContext context = new PosterContext();
        Config config = new Config();
        config.setFontName("Dialog");
        config.setFontSize(18);
        config.setFont(new Font("Dialog", Font.PLAIN, 18));
        context.setConfig(config);
        context.setGraphics(graphics);
        return context;
    }

    private BufferedImage renderElement(TextElement element, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setFont(new Font("Dialog", Font.PLAIN, 18));

        PosterContext context = new PosterContext();
        Config config = new Config();
        config.setFontName("Dialog");
        config.setFontSize(18);
        config.setFont(new Font("Dialog", Font.PLAIN, 18));
        context.setConfig(config);
        context.setGraphics(graphics);

        element.render(context, width, height);
        graphics.dispose();
        return image;
    }

    private int countNonTransparentPixels(BufferedImage image) {
        int count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (((image.getRGB(x, y) >>> 24) & 0xFF) > 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countColorLikePixels(BufferedImage image, Color target, int tolerance) {
        int count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color current = new Color(image.getRGB(x, y), true);
                if (current.getAlpha() == 0) {
                    continue;
                }
                if (Math.abs(current.getRed() - target.getRed()) <= tolerance
                        && Math.abs(current.getGreen() - target.getGreen()) <= tolerance
                        && Math.abs(current.getBlue() - target.getBlue()) <= tolerance) {
                    count++;
                }
            }
        }
        return count;
    }

    private void expectIllegalArgument(String expectedMessage, Runnable runnable) {
        try {
            runnable.run();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals(expectedMessage, ex.getMessage());
        }
    }
}
