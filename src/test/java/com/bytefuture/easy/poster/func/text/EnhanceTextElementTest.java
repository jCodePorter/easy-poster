package com.bytefuture.easy.poster.func.text;

import com.bytefuture.easy.poster.element.advance.ComposeElement;
import com.bytefuture.easy.poster.element.basic.EnhanceTextElement;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.*;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.*;
import com.bytefuture.easy.poster.text.layout.LayoutLine;
import com.bytefuture.easy.poster.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.text.layout.TextRenderSpec;
import com.bytefuture.easy.poster.text.layout.TextRenderSpecFactory;
import com.bytefuture.easy.poster.text.split.TextSplitRequest;
import com.bytefuture.easy.poster.text.split.TextSplitResult;
import com.bytefuture.easy.poster.text.split.TextSplitterSimpleImpl;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class EnhanceTextElementTest {

    @Test
    public void shouldReportWrappedTextTotalHeight() {
        PosterContext context = createContext();
        String text = "TextElementUpgrade should report the full wrapped height.";
        EnhanceTextElement element = EnhanceTextElement.of(text)
                .setFontName("Dialog")
                .setFontSize(18)
                .setLineHeight(28)
                .setAutoWrapText(120)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension dimension = element.calculateDimension(context, 400, 300);
        FontMetrics fontMetrics = context.getGraphics().getFontMetrics(new Font("Dialog", Font.PLAIN, 18));
        TextSplitResult splitResult = new TextSplitterSimpleImpl().split(TextSplitRequest.of(text, 120, fontMetrics));

        Assert.assertTrue(splitResult.getLines().size() > 1);
        Assert.assertEquals(splitResult.getLines().size() * 28L, dimension.getHeight());
    }

    @Test
    public void shouldNotMutateFontSizeDuringAutoFit() {
        PosterContext context = createContext();
        EnhanceTextElement element = EnhanceTextElement.of("Auto fit sample text")
                .setFontName("Dialog")
                .setFontSize(32)
                .setAutoFitText(120, 10)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension dimension = element.calculateDimension(context, 400, 300);

        Assert.assertEquals(Integer.valueOf(32), element.getFontSize());
        Assert.assertTrue(dimension.getWidth() <= 120);
        Assert.assertEquals(dimension.getWidth(), element.calculateDimension(context, 400, 300).getWidth());
    }

    @Test
    public void shouldPreserveExplicitNewLinesWithoutAutoWrap() {
        PosterContext context = createContext();
        EnhanceTextElement element = EnhanceTextElement.of("hello\n\nworld")
                .setFontName("Dialog")
                .setFontSize(18)
                .setLineHeight(26)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension dimension = element.calculateDimension(context, 400, 300);

        Assert.assertEquals(78L, dimension.getHeight());
    }

    @Test
    public void shouldProvideCompleteHeightToComposeLayout() {
        PosterContext context = createContext();
        RectangleElement header = (RectangleElement) new RectangleElement(80, 40)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        EnhanceTextElement content = EnhanceTextElement.of("Compose layout should respect multiline height.")
                .setFontName("Dialog")
                .setFontSize(18)
                .setLineHeight(24)
                .setAutoWrapText(80)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension textDimension = content.calculateDimension(context, 80, 200);
        Dimension composeDimension = ComposeElement.of(header)
                .bottom(content)
                .calculateDimension(context, 300, 300);

        Assert.assertTrue(textDimension.getHeight() > 24);
        Assert.assertEquals(40L + textDimension.getHeight(), composeDimension.getHeight());
    }

    @Test
    public void shouldCenterAlignLinesInsideMeasuredBlock() {
        PosterContext context = createContext();
        EnhanceTextElement element = EnhanceTextElement.of("alpha beta gamma delta epsilon zeta eta theta")
                .setFontName("Dialog")
                .setFontSize(18)
                .setAutoWrapText(120)
                .setTextAlign(TextAlign.CENTER)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        TextLayoutResult layout = measureLayout(element, context, 400, 300);
        LayoutLine firstLine = layout.getLines().get(0);

        Assert.assertEquals(TextAlign.CENTER, layout.getTextAlign());
        Assert.assertTrue(layout.getLines().size() > 1);
        Assert.assertTrue(firstLine.getPoint().getX() > layout.getPoint().getX());
    }

    @Test
    public void shouldInferCenterAlignmentFromRelativeCenterPosition() {
        PosterContext context = createContext();
        EnhanceTextElement element = EnhanceTextElement.of("center aligned")
                .setFontName("Dialog")
                .setFontSize(18)
                .setPosition(RelativePosition.of(Direction.CENTER));

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(TextAlign.CENTER, layout.getTextAlign());
    }

    @Test
    public void shouldJustifyWrappedIntermediateLine() {
        PosterContext context = createContext();
        EnhanceTextElement element = EnhanceTextElement.of("alpha beta gamma delta epsilon zeta eta theta")
                .setFontName("Dialog")
                .setFontSize(18)
                .setAutoWrapText(120)
                .setTextAlign(TextAlign.JUSTIFY)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertTrue(layout.getLines().size() > 1);
        Assert.assertTrue(layout.getLines().get(0).isJustified());
        Assert.assertFalse(layout.getLines().get(layout.getLines().size() - 1).isJustified());
        Assert.assertEquals(layout.getContentWidth(), layout.getLines().get(0).getRenderWidth());
    }

    @Test
    public void shouldApplyMaxLinesAndEllipsisDuringLayout() {
        PosterContext context = createContext();
        EnhanceTextElement element = EnhanceTextElement.of("This plain text block should wrap and then truncate at the configured max lines.")
                .setFontName("Dialog")
                .setFontSize(20)
                .setLineHeight(28)
                .setAutoWrapText(120)
                .setMaxLines(2)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(2, layout.getLines().size());
        Assert.assertEquals(56, layout.getHeight());
        Assert.assertTrue(layout.getLines().get(1).getText().endsWith("..."));
        Assert.assertTrue(layout.isTruncated());
    }

    @Test
    public void shouldEllipsizeSingleLineWhenOverflowStrategyIsEllipsis() {
        PosterContext context = createContext();
        EnhanceTextElement element = EnhanceTextElement.of("This is a single line that should be ellipsized.")
                .setFontName("Dialog")
                .setFontSize(20)
                .setLayoutWidth(120)
                .setOverflowStrategy(TextOverflowStrategy.ELLIPSIS)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        TextLayoutResult layout = measureLayout(element, context, 400, 300);
        LayoutLine line = layout.getLines().get(0);

        Assert.assertEquals(1, layout.getLines().size());
        Assert.assertEquals(120, layout.getWidth());
        Assert.assertTrue(line.getText().endsWith("..."));
        Assert.assertTrue(line.getWidth() <= 120);
    }

    @Test
    public void shouldClipSingleLineWhenOverflowStrategyIsClip() {
        PosterContext context = createContext();
        EnhanceTextElement element = EnhanceTextElement.of("This is a single line that should be clipped.")
                .setFontName("Dialog")
                .setFontSize(20)
                .setLayoutWidth(120)
                .setOverflowStrategy(TextOverflowStrategy.CLIP)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertTrue(layout.isClipOverflow());
        Assert.assertFalse(layout.isTruncated());
        Assert.assertEquals(120, layout.getLines().get(0).getRenderWidth());
    }

    @Test
    public void shouldIncreaseMeasuredWidthWhenLetterSpacingEnabled() {
        PosterContext context = createContext();
        EnhanceTextElement compact = EnhanceTextElement.of("spacing")
                .setFontName("Dialog")
                .setFontSize(18)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        EnhanceTextElement expanded = EnhanceTextElement.of("spacing")
                .setFontName("Dialog")
                .setFontSize(18)
                .setLetterSpacing(4)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Assert.assertTrue(compact.calculateDimension(context, 400, 300).getWidth()
                < expanded.calculateDimension(context, 400, 300).getWidth());
    }

    @Test
    public void shouldWrapLongTokenWhenLetterSpacingConsumesAvailableWidth() {
        PosterContext context = createContext();
        EnhanceTextElement element = EnhanceTextElement.of("abcdefghij")
                .setFontName("Dialog")
                .setFontSize(18)
                .setLetterSpacing(4)
                .setAutoWrapText(60)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertTrue(layout.getLines().size() > 1);
        Assert.assertEquals(60, layout.getContentWidth());
    }

    @Test
    public void shouldExpandMeasuredBoundsWhenShadowAndStrokeEnabled() {
        PosterContext context = createContext();
        EnhanceTextElement plain = EnhanceTextElement.of("decorate")
                .setFontName("Dialog")
                .setFontSize(24)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        EnhanceTextElement decorated = EnhanceTextElement.of("decorate")
                .setFontName("Dialog")
                .setFontSize(24)
                .setShadow(Color.GRAY, 4, 5)
                .setStroke(Color.BLACK, 2f)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension plainDimension = plain.calculateDimension(context, 400, 300);
        Dimension decoratedDimension = decorated.calculateDimension(context, 400, 300);

        Assert.assertTrue(decoratedDimension.getWidth() >= plainDimension.getWidth());
        Assert.assertTrue(decoratedDimension.getHeight() >= plainDimension.getHeight());
    }

    @Test
    public void shouldExpandBoundsAndOffsetsWhenTextPaddingEnabled() {
        PosterContext context = createContext();
        EnhanceTextElement base = EnhanceTextElement.of("padding")
                .setFontName("Dialog")
                .setFontSize(20)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        EnhanceTextElement padded = EnhanceTextElement.of("padding")
                .setFontName("Dialog")
                .setFontSize(20)
                .setTextBackground(new Color(255, 240, 200), Margin.of(10, 6, 8, 4))
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        TextLayoutResult layout = measureLayout(padded, context, 400, 300);
        Dimension baseDimension = base.calculateDimension(context, 400, 300);
        Dimension paddedDimension = padded.calculateDimension(context, 400, 300);

        Assert.assertEquals(10, layout.getTextPadding().getLeft());
        Assert.assertEquals(6, layout.getTextPadding().getTop());
        Assert.assertTrue(paddedDimension.getWidth() > baseDimension.getWidth());
        Assert.assertTrue(paddedDimension.getHeight() > baseDimension.getHeight());
    }

    @Test
    public void shouldRenderUnderlineAsAdditionalPixels() {
        EnhanceTextElement plain = EnhanceTextElement.of("underline")
                .setFontName("Dialog")
                .setFontSize(24)
                .setColor(Color.BLACK)
                .setPosition(AbsolutePosition.of(Point.of(20, 40), Direction.TOP_LEFT));
        EnhanceTextElement underlined = EnhanceTextElement.of("underline")
                .setFontName("Dialog")
                .setFontSize(24)
                .setColor(Color.BLACK)
                .setUnderline(true)
                .setPosition(AbsolutePosition.of(Point.of(20, 40), Direction.TOP_LEFT));

        BufferedImage plainImage = renderElement(plain, 220, 100);
        BufferedImage underlineImage = renderElement(underlined, 220, 100);

        Assert.assertTrue(countNonTransparentPixels(underlineImage) > countNonTransparentPixels(plainImage));
    }

    @Test
    public void shouldRenderTextBackgroundInsidePaddingArea() {
        EnhanceTextElement element = EnhanceTextElement.of("bg")
                .setFontName("Dialog")
                .setFontSize(24)
                .setTextBackground(new Color(255, 220, 120), Margin.of(12))
                .setPosition(AbsolutePosition.of(Point.of(20, 50), Direction.TOP_LEFT));

        BufferedImage image = renderElement(element, 180, 120);

        Assert.assertTrue(countColorLikePixels(image, new Color(255, 220, 120), 8) > 0);
    }

    @Test
    public void shouldRenderShadowAndStrokeWithoutBreakingTextOutput() {
        EnhanceTextElement element = EnhanceTextElement.of("shadow")
                .setFontName("Dialog")
                .setFontSize(28)
                .setColor(Color.BLUE)
                .setShadow(new Color(80, 80, 80), 4, 4)
                .setStroke(Color.BLACK, 2f)
                .setPosition(AbsolutePosition.of(Point.of(20, 50), Direction.TOP_LEFT));

        BufferedImage image = renderElement(element, 260, 120);

        Assert.assertTrue(countNonTransparentPixels(image) > 0);
        Assert.assertTrue(countColorLikePixels(image, Color.BLACK, 20) > 0);
    }

    @Test
    public void shouldRenderRichTextSpansWithDifferentColors() {
        EnhanceTextElement element = EnhanceTextElement.richText(
                        TextSpan.of("Red").setColor(Color.RED),
                        TextSpan.of("Blue").setColor(Color.BLUE))
                .setFontName("Dialog")
                .setFontSize(24)
                .setPosition(AbsolutePosition.of(Point.of(20, 50), Direction.TOP_LEFT));

        BufferedImage image = renderElement(element, 260, 120);

        Assert.assertTrue(countColorLikePixels(image, Color.RED, 20) > 0);
        Assert.assertTrue(countColorLikePixels(image, Color.BLUE, 20) > 0);
    }

    @Test
    public void shouldWrapRichTextAcrossStyledSpans() {
        PosterContext context = createContext();
        EnhanceTextElement element = EnhanceTextElement.richText(
                        TextSpan.of("This ").setColor(Color.RED),
                        TextSpan.of("rich ").setFontStyle(Font.BOLD),
                        TextSpan.of("text block should wrap across spans.").setColor(Color.BLUE))
                .setFontName("Dialog")
                .setFontSize(20)
                .setLineHeight(28)
                .setAutoWrapText(120)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertTrue(layout.getLines().size() > 1);
        Assert.assertTrue(layout.getLines().get(0).hasRichFragments());
    }

    @Test
    public void shouldMeasureRichTextAcrossExplicitNewLines() {
        PosterContext context = createContext();
        EnhanceTextElement element = EnhanceTextElement.richText(
                        TextSpan.of("hello\n"),
                        TextSpan.of("\n"),
                        TextSpan.of("world").setColor(Color.BLUE))
                .setFontName("Dialog")
                .setFontSize(18)
                .setLineHeight(26)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(3, layout.getLines().size());
        Assert.assertEquals(78, layout.getHeight());
    }

    @Test
    public void shouldEllipsizeRichTextWithinConfiguredWidth() {
        PosterContext context = createContext();
        EnhanceTextElement element = EnhanceTextElement.richText(
                        TextSpan.of("This ").setColor(Color.RED),
                        TextSpan.of("rich text line should be ellipsized.").setColor(Color.BLUE))
                .setFontName("Dialog")
                .setFontSize(20)
                .setLayoutWidth(120)
                .setOverflowStrategy(TextOverflowStrategy.ELLIPSIS)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        TextLayoutResult layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(1, layout.getLines().size());
        Assert.assertTrue(layout.getLines().get(0).getText().endsWith("..."));
        Assert.assertTrue(layout.getLines().get(0).getWidth() <= 120);
    }

    @Test
    public void shouldApplyRichMaxLinesAfterWrap() {
        PosterContext context = createContext();
        EnhanceTextElement element = EnhanceTextElement.richText(
                        TextSpan.of("This").setColor(Color.RED),
                        TextSpan.of(" rich").setFontStyle(Font.BOLD),
                        TextSpan.of(" text block should wrap and then truncate at the configured max lines.").setColor(Color.pink))
                .setFontName("Dialog")
                .setFontSize(20)
                .setLineHeight(40)
                .setAutoWrapText(240)
                .setMaxLines(2)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        TextLayoutResult layout = measureLayout(element, context, 400, 300);
        Dimension dimension = element.calculateDimension(context, 400, 300);

        Assert.assertEquals(2, layout.getLines().size());
        Assert.assertEquals(80, dimension.getHeight());
        Assert.assertTrue(layout.getLines().get(1).getText().endsWith("..."));
        Assert.assertTrue(layout.isTruncated());
    }

    @Test
    public void shouldSupportAbsolutePositionAndBaselineOffsets() {
        PosterContext context = createContext();
        Point anchor = Point.of(80, 90);

        TextLayoutResult topLayout = measureLayout(EnhanceTextElement.of("anchor")
                .setFontName("Dialog")
                .setFontSize(18)
                .setBaseLine(BaseLine.TOP)
                .setPosition(AbsolutePosition.of(anchor, Direction.TOP_LEFT)), context, 400, 300);
        TextLayoutResult centerLayout = measureLayout(EnhanceTextElement.of("anchor")
                .setFontName("Dialog")
                .setFontSize(18)
                .setBaseLine(BaseLine.CENTER)
                .setPosition(AbsolutePosition.of(anchor, Direction.TOP_LEFT)), context, 400, 300);
        TextLayoutResult bottomLayout = measureLayout(EnhanceTextElement.of("anchor")
                .setFontName("Dialog")
                .setFontSize(18)
                .setBaseLine(BaseLine.BOTTOM)
                .setPosition(AbsolutePosition.of(anchor, Direction.TOP_LEFT)), context, 400, 300);
        TextLayoutResult baseLineLayout = measureLayout(EnhanceTextElement.of("anchor")
                .setFontName("Dialog")
                .setFontSize(18)
                .setBaseLine(BaseLine.BASE_LINE)
                .setPosition(AbsolutePosition.of(anchor, Direction.TOP_LEFT)), context, 400, 300);

        Assert.assertEquals(anchor.getX(), topLayout.getPoint().getX());
        Assert.assertEquals(anchor.getY(), topLayout.getPoint().getY());
        Assert.assertTrue(centerLayout.getPoint().getY() < topLayout.getPoint().getY());
        Assert.assertTrue(bottomLayout.getPoint().getY() < centerLayout.getPoint().getY());
        Assert.assertTrue(baseLineLayout.getPoint().getY() <= topLayout.getPoint().getY());
    }

    @Test
    public void shouldValidateSetterArgumentsAndRichTextRestrictions() {
        final EnhanceTextElement element = EnhanceTextElement.of("validate");

        expectPosterException("fontSize must be greater than 0", new Runnable() {
            @Override
            public void run() {
                element.setFontSize(0);
            }
        });
        expectPosterException("lineHeight must be greater than 0", new Runnable() {
            @Override
            public void run() {
                element.setLineHeight(0);
            }
        });
        expectPosterException("maxTextWidth must be greater than 0", new Runnable() {
            @Override
            public void run() {
                element.setAutoWrapText(0);
            }
        });
        expectPosterException("layoutWidth must be greater than 0", new Runnable() {
            @Override
            public void run() {
                element.setLayoutWidth(0);
            }
        });
        expectPosterException("targetWidth must be greater than 0", new Runnable() {
            @Override
            public void run() {
                element.setAutoFitText(0, 1);
            }
        });
        expectPosterException("minFontSize must be greater than 0", new Runnable() {
            @Override
            public void run() {
                element.setAutoFitText(10, 0);
            }
        });
        expectPosterException("textAlign can not be null", new Runnable() {
            @Override
            public void run() {
                element.setTextAlign(null);
            }
        });
        expectPosterException("overflowStrategy can not be null", new Runnable() {
            @Override
            public void run() {
                element.setOverflowStrategy(null);
            }
        });
        expectPosterException("maxLines must be greater than 0", new Runnable() {
            @Override
            public void run() {
                element.setMaxLines(0);
            }
        });
        expectPosterException("ellipsis can not be null", new Runnable() {
            @Override
            public void run() {
                element.setEllipsis(null);
            }
        });
        expectPosterException("shadow color can not be null", new Runnable() {
            @Override
            public void run() {
                element.setShadow((Color) null, 1, 1);
            }
        });
        expectPosterException("shadow can not be null", new Runnable() {
            @Override
            public void run() {
                element.setShadow((com.bytefuture.easy.poster.model.TextShadow) null);
            }
        });
        expectPosterException("stroke color can not be null", new Runnable() {
            @Override
            public void run() {
                element.setStroke((Color) null, 1f);
            }
        });
        expectPosterException("stroke width must be greater than 0", new Runnable() {
            @Override
            public void run() {
                element.setStroke(Color.BLACK, 0f);
            }
        });
        expectPosterException("stroke can not be null", new Runnable() {
            @Override
            public void run() {
                element.setStroke((com.bytefuture.easy.poster.model.TextStroke) null);
            }
        });
        expectPosterException("textBackgroundColor can not be null", new Runnable() {
            @Override
            public void run() {
                element.setTextBackground(null);
            }
        });
        expectPosterException("textPadding must be greater than or equal to 0", new Runnable() {
            @Override
            public void run() {
                element.setTextPadding(-1);
            }
        });
        expectPosterException("paddingLeftRight must be greater than or equal to 0", new Runnable() {
            @Override
            public void run() {
                element.setTextPadding(-1, 1);
            }
        });
        expectPosterException("paddingBottom must be greater than or equal to 0", new Runnable() {
            @Override
            public void run() {
                element.setTextPadding(1, 1, 1, -1);
            }
        });
        expectPosterException("textPadding can not be null", new Runnable() {
            @Override
            public void run() {
                element.setTextPadding((Margin) null);
            }
        });
        expectPosterException("paddingLeft must be greater than or equal to 0", new Runnable() {
            @Override
            public void run() {
                element.setTextPadding(Margin.of(-1, 0, 0, 0));
            }
        });
        expectPosterException("textBackgroundArc must be greater than or equal to 0", new Runnable() {
            @Override
            public void run() {
                element.setTextBackgroundArc(-1);
            }
        });
        expectPosterException("textBackgroundArcHeight must be greater than or equal to 0", new Runnable() {
            @Override
            public void run() {
                element.setTextBackgroundArc(1, -1);
            }
        });
        expectPosterException("textSpan can not be null", new Runnable() {
            @Override
            public void run() {
                element.appendTextSpan(null);
            }
        });
        expectPosterException("textSplitter can not be null", new Runnable() {
            @Override
            public void run() {
                element.setTextSplitter(null);
            }
        });
        expectPosterException("font can not be null", new Runnable() {
            @Override
            public void run() {
                element.setFont((Font) null);
            }
        });

        final PosterContext context = createContext();
        final EnhanceTextElement autoFitRich = EnhanceTextElement.richText(TextSpan.of("rich"))
                .setFontName("Dialog")
                .setFontSize(18)
                .setAutoFitText(100, 10)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        expectPosterException("rich text span does not support autoFitText yet", new Runnable() {
            @Override
            public void run() {
                measureLayout(autoFitRich, context, 400, 300);
            }
        });

        final EnhanceTextElement justifyRich = EnhanceTextElement.richText(TextSpan.of("rich"))
                .setFontName("Dialog")
                .setFontSize(18)
                .setTextAlign(TextAlign.JUSTIFY)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        expectPosterException("rich text span does not support justify yet", new Runnable() {
            @Override
            public void run() {
                measureLayout(justifyRich, context, 400, 300);
            }
        });
    }

    @Test
    public void shouldSupportSpanCollectionAndBackgroundArcSetters() {
        EnhanceTextElement element = EnhanceTextElement.of("setter")
                .setFont("Dialog", Font.BOLD, 20)
                .setFont(new Font("Dialog", Font.ITALIC, 22))
                .setStrikeThrough(true)
                .setUnderline(true)
                .setLetterSpacing(2)
                .setTextBackground(new Color(240, 240, 240), 6)
                .setTextBackgroundArc(8)
                .setTextBackgroundArc(10, 12)
                .setTextSpans(Arrays.asList(TextSpan.of("A"), TextSpan.of("B")))
                .setTextSpans(null);

        element.appendTextSpan(TextSpan.of("C"));
        element.setTextBackground(new Color(230, 230, 230), Margin.of(4, 2));
        element.setTextSplitter(new TextSplitterSimpleImpl());
        element.beforeRender(createContext());
        element.debug(createContext(), null);

        Assert.assertTrue(element.isStrikeThrough());
        Assert.assertTrue(element.isUnderline());
        Assert.assertEquals(2, element.getLetterSpacing());
        Assert.assertEquals(10, element.getTextBackgroundArcWidth());
        Assert.assertEquals(12, element.getTextBackgroundArcHeight());
        Assert.assertEquals(1, element.getTextSpans().size());
        Assert.assertEquals("C", element.getTextSpans().get(0).getText());
    }

    private TextLayoutResult measureLayout(EnhanceTextElement element, PosterContext context, int posterWidth, int posterHeight) {
        TextRenderSpec spec = TextRenderSpecFactory.from(element, context.getConfig());
        return element.measureLayoutInternal(spec, context, posterWidth, posterHeight);
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

    private BufferedImage renderElement(EnhanceTextElement element, int width, int height) {
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

    private void expectPosterException(String expectedMessage, Runnable runnable) {
        try {
            runnable.run();
            Assert.fail("Expected PosterException");
        } catch (PosterException ex) {
            Assert.assertEquals(expectedMessage, ex.getMessage());
        }
    }
}
