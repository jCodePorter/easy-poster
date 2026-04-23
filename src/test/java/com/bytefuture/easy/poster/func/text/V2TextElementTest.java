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
import com.bytefuture.easy.poster.model.Gradient;
import com.bytefuture.easy.poster.model.GradientDirection;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextLayoutMode;
import com.bytefuture.easy.poster.model.TextOverflowStrategy;
import com.bytefuture.easy.poster.model.TextShadow;
import com.bytefuture.easy.poster.model.TextSpan;
import com.bytefuture.easy.poster.model.TextStroke;
import com.bytefuture.easy.poster.text.layout.LayoutLine;
import com.bytefuture.easy.poster.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.text.layout.VerticalGlyph;
import com.bytefuture.easy.poster.text.wrap.RichTextFragment;
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
import java.util.List;

import static com.bytefuture.easy.poster.model.VerticalAlign.BOTTOM;
import static com.bytefuture.easy.poster.model.VerticalAlign.JUSTIFY;
import static com.bytefuture.easy.poster.model.VerticalAlign.MIDDLE;
import static com.bytefuture.easy.poster.model.VerticalAlign.TOP;
import static com.bytefuture.easy.poster.model.VerticalDirection.LEFT_TO_RIGHT;
import static com.bytefuture.easy.poster.model.VerticalDirection.RIGHT_TO_LEFT;

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
    public void shouldExposeVerticalBuilderConvenienceMethods() {
        TextElement stringElement = TextElement.builder("unused")
                .vertical("纵向排版")
                .textLayoutMode(TextLayoutMode.VERTICAL)
                .layoutHeight(120)
                .columnSpacing(8)
                .verticalDirection(RIGHT_TO_LEFT)
                .verticalAlign(BOTTOM)
                .build();
        TextElement listElement = TextElement.builder("unused")
                .vertical(Arrays.asList("春眠", "不觉晓"))
                .build();

        Assert.assertEquals(TextLayoutMode.VERTICAL, stringElement.getConfig().getTextLayoutMode());
        Assert.assertEquals("纵向排版", stringElement.getConfig().getVerticalText());
        Assert.assertTrue(stringElement.getConfig().getVerticalColumns().isEmpty());
        Assert.assertEquals(120, stringElement.getConfig().getLayoutHeight());
        Assert.assertEquals(8, stringElement.getConfig().getColumnSpacing());
        Assert.assertEquals(RIGHT_TO_LEFT, stringElement.getConfig().getVerticalDirection());
        Assert.assertEquals(BOTTOM, stringElement.getConfig().getVerticalAlign());

        Assert.assertEquals(TextLayoutMode.VERTICAL, listElement.getConfig().getTextLayoutMode());
        Assert.assertEquals(Arrays.asList("春眠", "不觉晓"), listElement.getConfig().getVerticalColumns());
        Assert.assertNull(listElement.getConfig().getVerticalText());
    }

    @Test
    public void shouldSplitVerticalStringIntoMultipleColumnsWhenLayoutHeightIsConstrained() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("unused")
                .font("Dialog", Font.PLAIN, 20)
                .vertical("天地玄黄宇宙洪荒")
                .lineHeight(24)
                .layoutHeight(72)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(TextLayoutMode.VERTICAL, layout.getTextLayoutMode());
        Assert.assertTrue(layout.getLines().size() > 1);
        Assert.assertEquals(72, layout.getContentHeight());
        Assert.assertEquals("天地玄", layout.getLines().get(0).getText());
        Assert.assertEquals("黄宇宙", layout.getLines().get(1).getText());
    }

    @Test
    public void shouldPlaceVerticalColumnsFromLeftToRight() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("unused")
                .font("Dialog", Font.PLAIN, 20)
                .vertical(Arrays.asList("甲乙", "丙丁"))
                .layoutHeight(120)
                .columnSpacing(12)
                .verticalDirection(LEFT_TO_RIGHT)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(2, layout.getLines().size());
        Assert.assertTrue(layout.getLines().get(1).getPoint().getX() > layout.getLines().get(0).getPoint().getX());
    }

    @Test
    public void shouldPlaceVerticalColumnsFromRightToLeft() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder("unused")
                .font("Dialog", Font.PLAIN, 20)
                .vertical(Arrays.asList("甲乙", "丙丁"))
                .layoutHeight(120)
                .columnSpacing(12)
                .verticalDirection(RIGHT_TO_LEFT)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(2, layout.getLines().size());
        Assert.assertTrue(layout.getLines().get(1).getPoint().getX() < layout.getLines().get(0).getPoint().getX());
    }

    @Test
    public void shouldApplyVerticalAlignmentsInsideConfiguredHeight() {
        PosterContext context = createContext();
        TextLayoutResult topLayout = measureLayout(TextElement.builder("unused")
                .font("Dialog", Font.PLAIN, 20)
                .vertical(Arrays.asList("甲乙"))
                .layoutHeight(120)
                .verticalAlign(TOP)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build(), context, 400, 300);
        TextLayoutResult middleLayout = measureLayout(TextElement.builder("unused")
                .font("Dialog", Font.PLAIN, 20)
                .vertical(Arrays.asList("甲乙"))
                .layoutHeight(120)
                .verticalAlign(MIDDLE)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build(), context, 400, 300);
        TextLayoutResult bottomLayout = measureLayout(TextElement.builder("unused")
                .font("Dialog", Font.PLAIN, 20)
                .vertical(Arrays.asList("甲乙"))
                .layoutHeight(120)
                .verticalAlign(BOTTOM)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build(), context, 400, 300);
        TextLayoutResult justifyLayout = measureLayout(TextElement.builder("unused")
                .font("Dialog", Font.PLAIN, 20)
                .vertical(Arrays.asList("甲乙"))
                .layoutHeight(120)
                .verticalAlign(JUSTIFY)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build(), context, 400, 300);

        List<VerticalGlyph> topGlyphs = topLayout.getLines().get(0).getVerticalGlyphs();
        List<VerticalGlyph> middleGlyphs = middleLayout.getLines().get(0).getVerticalGlyphs();
        List<VerticalGlyph> bottomGlyphs = bottomLayout.getLines().get(0).getVerticalGlyphs();
        List<VerticalGlyph> justifyGlyphs = justifyLayout.getLines().get(0).getVerticalGlyphs();

        Assert.assertEquals(2, topGlyphs.size());
        Assert.assertTrue(topGlyphs.get(0).getYOffset() < middleGlyphs.get(0).getYOffset());
        Assert.assertTrue(middleGlyphs.get(0).getYOffset() < bottomGlyphs.get(0).getYOffset());
        Assert.assertEquals(0, justifyGlyphs.get(0).getYOffset());
        Assert.assertEquals(justifyLayout.getContentHeight() - justifyLayout.getLineHeight(),
                justifyGlyphs.get(1).getYOffset());
    }

    @Test
    public void shouldLayoutVerticalRichTextColumns() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(
                        TextSpan.of("春").setColor(Color.RED).setFontSize(24),
                        TextSpan.of("夏").setColor(Color.GREEN).setFontStyle(Font.BOLD),
                        TextSpan.of("秋冬").setColor(Color.BLUE))
                .font("Dialog", Font.PLAIN, 20)
                .textLayoutMode(TextLayoutMode.VERTICAL)
                .layoutHeight(48)
                .columnSpacing(8)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 300, 300);

        Assert.assertTrue(layout.getLines().size() >= 2);
        Assert.assertTrue(layout.getLines().get(0).hasVerticalGlyphs());
        Assert.assertEquals(Color.RED, layout.getLines().get(0).getVerticalGlyphs().get(0).getColor());
    }

    @Test
    public void shouldRenderVerticalRichTextUsingSpanColors() {
        TextElement element = TextElement.builder(
                        TextSpan.of("春").setColor(Color.RED),
                        TextSpan.of("夏").setColor(Color.BLUE))
                .font("Dialog", Font.PLAIN, 28)
                .textLayoutMode(TextLayoutMode.VERTICAL)
                .position(AbsolutePosition.of(Point.of(20, 40), Direction.TOP_LEFT))
                .build();

        BufferedImage image = renderElement(element, 160, 160);

        Assert.assertTrue(countColorLikePixels(image, Color.RED, 20) > 0);
        Assert.assertTrue(countColorLikePixels(image, Color.BLUE, 20) > 0);
    }

    @Test
    public void shouldRejectNullVerticalInputsAndNegativeVerticalDimensions() {
        expectIllegalArgument("vertical text cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").vertical((String) null);
            }
        });
        expectIllegalArgument("vertical columns cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").vertical((List<String>) null);
            }
        });
        expectIllegalArgument("vertical column cannot be null", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").vertical(Arrays.asList("甲", null));
            }
        });
        expectIllegalArgument("layoutHeight must be positive", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").layoutHeight(0);
            }
        });
        expectIllegalArgument("columnSpacing cannot be negative", new Runnable() {
            @Override
            public void run() {
                TextElement.builder("validate").columnSpacing(-1);
            }
        });
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
    public void shouldExposeSpanLevelStyleOverridesOnRichFragments() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(
                        TextSpan.of("mono")
                                .setFontName("Monospaced")
                                .setBackgroundColor(Color.YELLOW)
                                .setShadow(TextShadow.of(Color.GRAY, 2, 1))
                                .setStroke(TextStroke.of(Color.BLACK, 1f))
                                .setBaselineShift(-3),
                        TextSpan.of("plain").setColor(Color.BLUE))
                .font("Dialog", Font.PLAIN, 20)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);
        RichTextFragment first = layout.getLines().get(0).getRichFragments().get(0);

        Assert.assertEquals("Monospaced", first.getFont().getFamily());
        Assert.assertEquals(Color.YELLOW, first.getBackgroundColor());
        Assert.assertEquals(Color.GRAY, first.getShadow().getColor());
        Assert.assertEquals(Color.BLACK, first.getStroke().getColor());
        Assert.assertEquals(-3, first.getBaselineShift());
    }

    @Test
    public void shouldRenderSpanLevelBackgroundShadowAndStroke() {
        TextElement element = TextElement.builder(
                        TextSpan.of("fx")
                                .setColor(Color.BLUE)
                                .setBackgroundColor(Color.YELLOW)
                                .setShadow(TextShadow.of(Color.GRAY, 2, 2))
                                .setStroke(TextStroke.of(Color.BLACK, 1f))
                                .setBaselineShift(-2))
                .font("Dialog", Font.PLAIN, 28)
                .position(AbsolutePosition.of(Point.of(20, 50), Direction.TOP_LEFT))
                .build();

        BufferedImage image = renderElement(element, 220, 120);

        Assert.assertTrue(countColorLikePixels(image, Color.YELLOW, 20) > 0);
        Assert.assertTrue(countColorLikePixels(image, Color.GRAY, 20) > 0);
        Assert.assertTrue(countColorLikePixels(image, Color.BLACK, 20) > 0);
        Assert.assertTrue(countColorLikePixels(image, Color.BLUE, 20) > 0);
    }

    @Test
    public void shouldRenderEmojiWithoutBreakingSurrogatePairsWhenLetterSpacingIsEnabled() {
        PosterContext context = createContext();
        TextElement compact = TextElement.builder("A🙂B")
                .font("Dialog", Font.PLAIN, 24)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();
        TextElement spaced = TextElement.builder("A🙂B")
                .font("Dialog", Font.PLAIN, 24)
                .letterSpacing(6)
                .stroke(Color.BLACK, 1f)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult compactLayout = measureLayout(compact, context, 300, 200);
        TextLayoutResult spacedLayout = measureLayout(spaced, context, 300, 200);
        BufferedImage image = renderElement(spaced, 220, 120);

        Assert.assertTrue(spacedLayout.getContentWidth() > compactLayout.getContentWidth());
        Assert.assertTrue(countNonTransparentPixels(image) > 0);
        Assert.assertTrue(countColorLikePixels(image, Color.BLACK, 20) > 0);
    }

    @Test
    public void shouldMeasureEmojiWidthUsingRenderableUnitsForLetterSpacing() {
        PosterContext context = createContext();
        TextElement compact = TextElement.builder("🙂🙂")
                .font("Dialog", Font.PLAIN, 24)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();
        TextElement spaced = TextElement.builder("🙂🙂")
                .font("Dialog", Font.PLAIN, 24)
                .letterSpacing(8)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult compactLayout = measureLayout(compact, context, 300, 200);
        TextLayoutResult spacedLayout = measureLayout(spaced, context, 300, 200);

        Assert.assertEquals(compactLayout.getContentWidth() + 8, spacedLayout.getContentWidth());
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
    public void shouldParseHtmlBackgroundFontFamilyAndBaselineTagsIntoStyledSpans() {
        TextElement element = TextElement.builderHtml(
                        "<span style='background-color:#ffeeaa;font-family:Monospaced'>A</span>"
                                + "<sup>2</sup>"
                                + "<sub>i</sub>"
                                + "<small>tiny</small>"
                                + "<code>x</code>")
                .build();

        Assert.assertEquals(5, element.getConfig().getTextSpans().size());

        TextSpan background = element.getConfig().getTextSpans().get(0);
        Assert.assertEquals(new Color(255, 238, 170), background.getBackgroundColor());
        Assert.assertEquals("Monospaced", background.getFontName());

        TextSpan superscript = element.getConfig().getTextSpans().get(1);
        Assert.assertEquals(Integer.valueOf(-6), superscript.getBaselineShift());

        TextSpan subscript = element.getConfig().getTextSpans().get(2);
        Assert.assertEquals(Integer.valueOf(6), subscript.getBaselineShift());

        TextSpan small = element.getConfig().getTextSpans().get(3);
        Assert.assertTrue(small.getFontSize() <= 16);

        TextSpan code = element.getConfig().getTextSpans().get(4);
        Assert.assertEquals("Monospaced", code.getFontName());
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
    public void shouldRenderPlainTextGradientWithReducedSolidColorDominanceComparedToSolidRenderForTask1() {
        TextElement solidElement = TextElement.builder("gradient block")
                .font("Dialog", Font.PLAIN, 28)
                .color(Color.BLACK)
                .position(AbsolutePosition.of(Point.of(20, 50), Direction.TOP_LEFT))
                .build();
        TextElement gradientElement = TextElement.builder("gradient block")
                .font("Dialog", Font.PLAIN, 28)
                .gradient(Gradient.of(new String[]{"#ff6b6b", "#4dabf7"}, GradientDirection.LEFT_RIGHT))
                .position(AbsolutePosition.of(Point.of(20, 50), Direction.TOP_LEFT))
                .build();

        BufferedImage solidImage = renderElement(solidElement, 240, 120);
        BufferedImage gradientImage = renderElement(gradientElement, 240, 120);

        Assert.assertTrue(countNonTransparentPixels(solidImage) > 0);
        Assert.assertTrue(countNonTransparentPixels(gradientImage) > 0);
        Assert.assertTrue(countColorLikePixels(gradientImage, Color.BLACK, 8)
                < countColorLikePixels(solidImage, Color.BLACK, 8));
    }

    @Test
    public void shouldRenderRichTextGradientWithReducedSpanColorDominanceComparedToSolidRichRenderForTask1() {
        PosterContext context = createContext();
        TextElement solidElement = TextElement.builder(
                        TextSpan.of("Red").setColor(Color.RED),
                        TextSpan.of(" Blue").setColor(Color.BLUE).setFontStyle(Font.BOLD))
                .font("Dialog", Font.PLAIN, 28)
                .position(AbsolutePosition.of(Point.of(20, 50), Direction.TOP_LEFT))
                .build();
        TextElement gradientElement = TextElement.builder(
                        TextSpan.of("Red").setColor(Color.RED),
                        TextSpan.of(" Blue").setColor(Color.BLUE).setFontStyle(Font.BOLD))
                .font("Dialog", Font.PLAIN, 28)
                .gradient(Gradient.of(new String[]{"#ff6b6b", "#4dabf7"}, GradientDirection.LEFT_RIGHT))
                .position(AbsolutePosition.of(Point.of(20, 50), Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(gradientElement, context, 400, 300);
        BufferedImage solidImage = renderElement(solidElement, 240, 120);
        BufferedImage gradientImage = renderElement(gradientElement, 240, 120);
        int solidOpaquePixels = countNonTransparentPixels(solidImage);
        int gradientOpaquePixels = countNonTransparentPixels(gradientImage);
        int solidSpanColorPixels = countColorLikePixels(solidImage, Color.RED, 24)
                + countColorLikePixels(solidImage, Color.BLUE, 24);
        int gradientSpanColorPixels = countColorLikePixels(gradientImage, Color.RED, 24)
                + countColorLikePixels(gradientImage, Color.BLUE, 24);
        Assert.assertFalse(layout.getLines().isEmpty());
        LayoutLine firstLine = layout.getLines().get(0);

        Assert.assertEquals(1, layout.getLines().size());
        Assert.assertTrue(firstLine.hasRichFragments());
        Assert.assertFalse(firstLine.getRichFragments().isEmpty());
        Assert.assertEquals(2, firstLine.getRichFragments().size());
        Assert.assertEquals("Red", firstLine.getRichFragments().get(0).getText());
        Assert.assertEquals(" Blue", firstLine.getRichFragments().get(1).getText());
        Assert.assertTrue(layout.getContentWidth() > 0);
        Assert.assertTrue(layout.getContentHeight() > 0);
        Assert.assertTrue(solidOpaquePixels > 0);
        Assert.assertTrue(gradientOpaquePixels > 0);
        Assert.assertTrue("gradient-configured rich text should reduce dominance of exact span colors in the rendered output",
                (double) gradientSpanColorPixels / gradientOpaquePixels
                        < (double) solidSpanColorPixels / solidOpaquePixels);
    }

    @Test
    public void shouldPreserveRichNonFillStyleMetadataInLayoutWhenGradientEnabled() {
        PosterContext context = createContext();
        TextShadow shadow = TextShadow.of(Color.GRAY, 2, 1);
        TextStroke stroke = TextStroke.of(Color.BLACK, 1f);
        TextElement element = TextElement.builder(
                        TextSpan.of("mono")
                                .setFontName("Monospaced")
                                .setBackgroundColor(Color.YELLOW)
                                .setShadow(shadow)
                                .setStroke(stroke)
                                .setBaselineShift(-3)
                                .setUnderline(true),
                        TextSpan.of("plain").setColor(Color.BLUE))
                .font("Dialog", Font.PLAIN, 20)
                .gradient(Gradient.of(new String[]{"#ff6b6b", "#4dabf7"}, GradientDirection.LEFT_RIGHT))
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);
        BufferedImage image = renderElement(element, 240, 120);
        Assert.assertFalse(layout.getLines().isEmpty());
        LayoutLine firstLine = layout.getLines().get(0);

        Assert.assertTrue(firstLine.hasRichFragments());
        Assert.assertFalse(firstLine.getRichFragments().isEmpty());
        RichTextFragment first = firstLine.getRichFragments().get(0);
        Assert.assertEquals("Monospaced", first.getFont().getFamily());
        Assert.assertEquals(Color.YELLOW, first.getBackgroundColor());
        Assert.assertEquals(shadow, first.getShadow());
        Assert.assertEquals(stroke, first.getStroke());
        Assert.assertEquals(-3, first.getBaselineShift());
        Assert.assertTrue(first.isUnderline());
        Assert.assertTrue(countNonTransparentPixels(image) > 0);
    }

    @Test
    public void shouldRenderVerticalTextGradientWithReducedSolidColorDominanceComparedToSolidRenderForTask1() {
        PosterContext context = createContext();
        TextElement solidElement = TextElement.builder("unused")
                .font("Dialog", Font.PLAIN, 28)
                .vertical("天地玄黄")
                .layoutHeight(90)
                .color(Color.BLACK)
                .position(AbsolutePosition.of(Point.of(30, 20), Direction.TOP_LEFT))
                .build();
        TextElement gradientElement = TextElement.builder("unused")
                .font("Dialog", Font.PLAIN, 28)
                .vertical("天地玄黄")
                .layoutHeight(90)
                .gradient(Gradient.of(new String[]{"#ff6b6b", "#4dabf7"}, GradientDirection.TOP_BOTTOM))
                .position(AbsolutePosition.of(Point.of(30, 20), Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(gradientElement, context, 240, 200);
        BufferedImage solidImage = renderElement(solidElement, 240, 200);
        BufferedImage gradientImage = renderElement(gradientElement, 240, 200);

        Assert.assertEquals(TextLayoutMode.VERTICAL, layout.getTextLayoutMode());
        Assert.assertFalse(layout.getLines().isEmpty());
        Assert.assertTrue(layout.getLines().get(0).hasVerticalGlyphs());
        Assert.assertTrue(countNonTransparentPixels(solidImage) > 0);
        Assert.assertTrue(countNonTransparentPixels(gradientImage) > 0);
        Assert.assertTrue(countColorLikePixels(gradientImage, Color.BLACK, 8)
                < countColorLikePixels(solidImage, Color.BLACK, 8));
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
    public void shouldAutoFitRichTextByScalingAllSpanSizesProportionally() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(
                        TextSpan.of("BIG").setFontSize(30).setColor(Color.RED),
                        TextSpan.of(" / ").setFontSize(18).setFontStyle(Font.BOLD),
                        TextSpan.of("small").setFontSize(12).setColor(Color.BLUE))
                .font("Dialog", Font.PLAIN, 24)
                .autoFitText(100, 10)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);
        List<RichTextFragment> fragments = layout.getLines().get(0).getRichFragments();

        Assert.assertTrue(layout.getContentWidth() <= 100);
        Assert.assertTrue(fragments.get(0).getFont().getSize() > fragments.get(1).getFont().getSize());
        Assert.assertTrue(fragments.get(1).getFont().getSize() > fragments.get(2).getFont().getSize());
    }

    @Test
    public void shouldHonorRichTextAutoFitMinimumFontSize() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(
                        TextSpan.of("Wide").setFontSize(26),
                        TextSpan.of(" rich ").setFontSize(16),
                        TextSpan.of("text").setFontSize(12))
                .font("Dialog", Font.PLAIN, 22)
                .autoFitText(40, 10)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);
        List<RichTextFragment> fragments = layout.getLines().get(0).getRichFragments();

        Assert.assertTrue(layout.getContentWidth() > 40);
        Assert.assertTrue(fragments.get(0).getFont().getSize() >= 10);
        Assert.assertTrue(fragments.get(1).getFont().getSize() >= 10);
        Assert.assertTrue(fragments.get(2).getFont().getSize() >= 10);
    }

    @Test
    public void shouldJustifyWrappedRichIntermediateLine() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(
                        TextSpan.of("alpha ").setColor(Color.RED),
                        TextSpan.of("beta ").setFontStyle(Font.BOLD),
                        TextSpan.of("gamma delta epsilon zeta").setColor(Color.BLUE))
                .font("Dialog", Font.PLAIN, 20)
                .autoWordWrap(120)
                .textAlign(TextAlign.JUSTIFY)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertTrue(layout.getLines().size() > 1);
        Assert.assertTrue(layout.getLines().get(0).isJustified());
        Assert.assertEquals(layout.getContentWidth(), layout.getLines().get(0).getRenderWidth());
        Assert.assertFalse(layout.getLines().get(layout.getLines().size() - 1).isJustified());
    }

    @Test
    public void shouldNotJustifyRichLineWithoutSpaces() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(
                        TextSpan.of("abcdefghij").setColor(Color.RED),
                        TextSpan.of("klmnopqrst").setColor(Color.BLUE))
                .font("Dialog", Font.PLAIN, 20)
                .autoWordWrap(90)
                .textAlign(TextAlign.JUSTIFY)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertTrue(layout.getLines().size() > 1);
        for (LayoutLine line : layout.getLines()) {
            Assert.assertFalse(line.isJustified());
            Assert.assertEquals(line.getWidth(), line.getRenderWidth());
        }
    }

    @Test
    public void shouldPreserveRichFragmentOrderWhenJustifyingAcrossSpanBoundaries() {
        PosterContext context = createContext();
        TextElement element = TextElement.builder(
                        TextSpan.of("left ").setColor(Color.RED),
                        TextSpan.of("mid ").setColor(Color.GREEN),
                        TextSpan.of("right tail").setColor(Color.BLUE))
                .font("Dialog", Font.PLAIN, 20)
                .autoWordWrap(120)
                .textAlign(TextAlign.JUSTIFY)
                .position(RelativePosition.of(Direction.TOP_LEFT))
                .build();

        TextLayoutResult layout = measureLayout(element, context, 400, 300);
        LayoutLine firstLine = layout.getLines().get(0);

        Assert.assertTrue(firstLine.hasRichFragments());
        Assert.assertTrue(firstLine.isJustified());
        Assert.assertTrue(firstLine.getRichFragments().size() >= 2);
        Assert.assertEquals(Color.RED, firstLine.getRichFragments().get(0).getColor());
        Assert.assertTrue(firstLine.getRichFragments().get(1).getXOffset()
                >= firstLine.getRichFragments().get(0).getXOffset() + firstLine.getRichFragments().get(0).getWidth());
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

        element.calculateDimension(context, width, height);
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
