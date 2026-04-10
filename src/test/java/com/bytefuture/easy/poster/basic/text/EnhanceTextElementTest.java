package com.bytefuture.easy.poster.basic.text;

import com.bytefuture.easy.poster.element.advance.ComposeElement;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.element.basic.EnhanceTextElement;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.Config;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextOverflowStrategy;
import com.bytefuture.easy.poster.model.TextSpan;
import com.bytefuture.easy.poster.text.TextSplitRequest;
import com.bytefuture.easy.poster.text.TextSplitResult;
import com.bytefuture.easy.poster.text.TextSplitterSimpleImpl;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.List;

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
        Assert.assertEquals(splitResult.getLines().size() * 28, dimension.getHeight());
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

        Assert.assertEquals(78, dimension.getHeight());
    }

    @Test
    public void shouldProvideCompleteHeightToComposeLayout() {
        PosterContext context = createContext();

        RectangleElement basic = new RectangleElement(80, 40)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        EnhanceTextElement text = EnhanceTextElement.of("Compose layout should respect multiline height.")
                .setFontName("Dialog")
                .setFontSize(18)
                .setLineHeight(24)
                .setAutoWrapText(80)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension textDimension = text.calculateDimension(context, 80, 200);
        Dimension composeDimension = ComposeElement.of(basic)
                .bottom(text)
                .calculateDimension(context, 300, 300);

        Assert.assertTrue(textDimension.getHeight() > 24);
        Assert.assertEquals(40 + textDimension.getHeight(), composeDimension.getHeight());
    }

    @Test
    public void shouldCenterAlignLinesInsideMeasuredBlock() throws Exception {
        PosterContext context = createContext();

        EnhanceTextElement element = EnhanceTextElement.of("alpha beta gamma delta epsilon zeta eta theta")
                .setFontName("Dialog")
                .setFontSize(18)
                .setAutoWrapText(120)
                .setTextAlign(TextAlign.CENTER)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Object layout = measureLayout(element, context, 400, 300);
        int layoutWidth = (Integer) invoke(layout, "getWidth");
        List<?> lines = (List<?>) invoke(layout, "getLines");

        Assert.assertTrue(lines.size() > 1);

        boolean foundShorterLine = false;
        for (Object line : lines) {
            int lineWidth = (Integer) invoke(line, "getWidth");
            if (lineWidth < layoutWidth) {
                Point point = (Point) invoke(line, "getPoint");
                Assert.assertEquals((layoutWidth - lineWidth) / 2, point.getX());
                foundShorterLine = true;
                break;
            }
        }
        Assert.assertTrue(foundShorterLine);
        Assert.assertEquals(TextAlign.CENTER, invoke(layout, "getTextAlign"));
    }

    @Test
    public void shouldInferCenterAlignmentFromRelativeCenterPosition() throws Exception {
        PosterContext context = createContext();

        EnhanceTextElement element = EnhanceTextElement.of("center inferred alignment sample text")
                .setFontName("Dialog")
                .setFontSize(18)
                .setAutoWrapText(120)
                .setPosition(RelativePosition.of(Direction.CENTER));

        Object layout = measureLayout(element, context, 400, 300);

        Assert.assertEquals(TextAlign.CENTER, invoke(layout, "getTextAlign"));
    }

    @Test
    public void shouldApplyMaxLinesAndEllipsisDuringLayout() throws Exception {
        PosterContext context = createContext();

        EnhanceTextElement element = EnhanceTextElement.of("This is a long text block that should wrap into several lines and then be truncated.")
                .setFontName("Dialog")
                .setFontSize(18)
                .setLineHeight(24)
                .setAutoWrapText(120)
                .setMaxLines(2)
                .setEllipsis("...")
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension dimension = element.calculateDimension(context, 400, 300);
        Object layout = measureLayout(element, context, 400, 300);
        List<?> lines = (List<?>) invoke(layout, "getLines");
        Object lastLine = lines.get(lines.size() - 1);
        String lastText = (String) invoke(lastLine, "getText");
        int lastWidth = (Integer) invoke(lastLine, "getWidth");

        Assert.assertEquals(2, lines.size());
        Assert.assertTrue(lastText.endsWith("..."));
        Assert.assertTrue((Boolean) invoke(layout, "isTruncated"));
        Assert.assertEquals(48, dimension.getHeight());
        Assert.assertTrue(lastWidth <= dimension.getWidth());
    }

    @Test
    public void shouldJustifyWrappedIntermediateLine() throws Exception {
        PosterContext context = createContext();

        EnhanceTextElement element = EnhanceTextElement.of("justify makes intermediate lines stretch across the configured width")
                .setFontName("Dialog")
                .setFontSize(18)
                .setAutoWrapText(180)
                .setTextAlign(TextAlign.JUSTIFY)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Object layout = measureLayout(element, context, 400, 300);
        int layoutWidth = (Integer) invoke(layout, "getWidth");
        List<?> lines = (List<?>) invoke(layout, "getLines");
        Object firstLine = lines.get(0);
        Object lastLine = lines.get(lines.size() - 1);

        Assert.assertTrue(lines.size() > 1);
        Assert.assertEquals(TextAlign.JUSTIFY, invoke(layout, "getTextAlign"));
        Assert.assertTrue((Boolean) invoke(firstLine, "isJustified"));
        Assert.assertEquals(layoutWidth, invoke(firstLine, "getRenderWidth"));
        Assert.assertFalse((Boolean) invoke(lastLine, "isJustified"));
    }

    @Test
    public void shouldEllipsizeSingleLineWhenOverflowStrategyIsEllipsis() throws Exception {
        PosterContext context = createContext();

        EnhanceTextElement element = EnhanceTextElement.of("This single line should be shortened with an ellipsis.")
                .setFontName("Dialog")
                .setFontSize(18)
                .setLayoutWidth(120)
                .setOverflowStrategy(TextOverflowStrategy.ELLIPSIS)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension dimension = element.calculateDimension(context, 400, 300);
        Object layout = measureLayout(element, context, 400, 300);
        List<?> lines = (List<?>) invoke(layout, "getLines");
        Object firstLine = lines.get(0);
        String text = (String) invoke(firstLine, "getText");
        int width = (Integer) invoke(firstLine, "getWidth");

        Assert.assertEquals(1, lines.size());
        Assert.assertTrue(text.endsWith("..."));
        Assert.assertEquals(120, dimension.getWidth());
        Assert.assertTrue(width <= 120);
    }

    @Test
    public void shouldClipSingleLineWhenOverflowStrategyIsClip() throws Exception {
        PosterContext context = createContext();

        EnhanceTextElement element = EnhanceTextElement.of("This single line should keep its text but clip its visible width.")
                .setFontName("Dialog")
                .setFontSize(18)
                .setLayoutWidth(120)
                .setOverflowStrategy(TextOverflowStrategy.CLIP)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension dimension = element.calculateDimension(context, 400, 300);
        Object layout = measureLayout(element, context, 400, 300);
        List<?> lines = (List<?>) invoke(layout, "getLines");
        Object firstLine = lines.get(0);
        String text = (String) invoke(firstLine, "getText");
        int naturalWidth = (Integer) invoke(firstLine, "getWidth");
        int renderWidth = (Integer) invoke(firstLine, "getRenderWidth");

        Assert.assertEquals("This single line should keep its text but clip its visible width.", text);
        Assert.assertEquals(120, dimension.getWidth());
        Assert.assertTrue(naturalWidth > 120);
        Assert.assertEquals(120, renderWidth);
        Assert.assertTrue((Boolean) invoke(layout, "isClipOverflow"));
    }

    @Test
    public void shouldExpandMeasuredBoundsWhenShadowAndStrokeEnabled() throws Exception {
        PosterContext context = createContext();

        EnhanceTextElement plain = EnhanceTextElement.of("Styled text")
                .setFontName("Dialog")
                .setFontSize(28)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        EnhanceTextElement styled = EnhanceTextElement.of("Styled text")
                .setFontName("Dialog")
                .setFontSize(28)
                .setStroke(Color.RED, 4f)
                .setShadow(Color.GRAY, -3, 5)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension plainDimension = plain.calculateDimension(context, 400, 300);
        Dimension styledDimension = styled.calculateDimension(context, 400, 300);
        Object layout = measureLayout(styled, context, 400, 300);
        Object decorationInsets = invoke(layout, "getDecorationInsets");

        Assert.assertTrue(styledDimension.getWidth() > plainDimension.getWidth());
        Assert.assertTrue(styledDimension.getHeight() > plainDimension.getHeight());
        Assert.assertEquals(3, invoke(decorationInsets, "getLeft"));
        Assert.assertEquals(5, invoke(decorationInsets, "getBottom"));
    }

    @Test
    public void shouldRenderUnderlineAsAdditionalPixels() {
        EnhanceTextElement plain = EnhanceTextElement.of("Underline sample")
                .setFontName("Dialog")
                .setFontSize(28)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        EnhanceTextElement underline = EnhanceTextElement.of("Underline sample")
                .setFontName("Dialog")
                .setFontSize(28)
                .setUnderline(true)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        BufferedImage plainImage = renderElement(plain, 300, 120);
        BufferedImage underlineImage = renderElement(underline, 300, 120);

        Assert.assertTrue(countNonTransparentPixels(underlineImage) > countNonTransparentPixels(plainImage));
    }

    @Test
    public void shouldRenderShadowAndStrokeWithoutBreakingTextOutput() {
        EnhanceTextElement plain = EnhanceTextElement.of("Shadow stroke")
                .setFontName("Dialog")
                .setFontSize(28)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        EnhanceTextElement styled = EnhanceTextElement.of("Shadow stroke")
                .setFontName("Dialog")
                .setFontSize(28)
                .setStroke(Color.BLUE, 3f)
                .setShadow(new Color(0, 0, 0, 120), 3, 3)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        BufferedImage plainImage = renderElement(plain, 320, 140);
        BufferedImage styledImage = renderElement(styled, 320, 140);

        Assert.assertTrue(countNonTransparentPixels(styledImage) > countNonTransparentPixels(plainImage));
    }

    @Test
    public void shouldIncreaseMeasuredWidthWhenLetterSpacingEnabled() {
        PosterContext context = createContext();
        String text = "ABCD";

        EnhanceTextElement plain = EnhanceTextElement.of(text)
                .setFontName("Dialog")
                .setFontSize(24)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        EnhanceTextElement spaced = EnhanceTextElement.of(text)
                .setFontName("Dialog")
                .setFontSize(24)
                .setLetterSpacing(5)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension plainDimension = plain.calculateDimension(context, 400, 300);
        Dimension spacedDimension = spaced.calculateDimension(context, 400, 300);

        Assert.assertEquals((text.length() - 1) * 5, spacedDimension.getWidth() - plainDimension.getWidth());
        Assert.assertEquals(plainDimension.getHeight(), spacedDimension.getHeight());
    }

    @Test
    public void shouldWrapLongTokenWhenLetterSpacingConsumesAvailableWidth() throws Exception {
        PosterContext context = createContext();
        String text = "ABCD";

        EnhanceTextElement plain = EnhanceTextElement.of(text)
                .setFontName("Dialog")
                .setFontSize(24)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        int plainWidth = plain.calculateDimension(context, 400, 300).getWidth();

        EnhanceTextElement spaced = EnhanceTextElement.of(text)
                .setFontName("Dialog")
                .setFontSize(24)
                .setLetterSpacing(6)
                .setLineHeight(30)
                .setAutoWrapText(plainWidth)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension dimension = spaced.calculateDimension(context, 400, 300);
        Object layout = measureLayout(spaced, context, 400, 300);
        List<?> lines = (List<?>) invoke(layout, "getLines");

        Assert.assertTrue(lines.size() > 1);
        Assert.assertEquals(lines.size() * 30, dimension.getHeight());
    }

    @Test
    public void shouldExpandBoundsAndOffsetsWhenTextPaddingEnabled() {
        PosterContext context = createContext();

        EnhanceTextElement plain = EnhanceTextElement.of("Padded text")
                .setFontName("Dialog")
                .setFontSize(24)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        EnhanceTextElement padded = EnhanceTextElement.of("Padded text")
                .setFontName("Dialog")
                .setFontSize(24)
                .setTextBackground(Color.YELLOW, Margin.of(6, 4))
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension plainDimension = plain.calculateDimension(context, 400, 300);
        Dimension paddedDimension = padded.calculateDimension(context, 400, 300);

        Assert.assertEquals(plainDimension.getWidth() + 12, paddedDimension.getWidth());
        Assert.assertEquals(plainDimension.getHeight() + 8, paddedDimension.getHeight());
        Assert.assertEquals(6, paddedDimension.getXOffset());
        Assert.assertEquals(plainDimension.getYOffset() + 4, paddedDimension.getYOffset());
    }

    @Test
    public void shouldRenderTextBackgroundInsidePaddingArea() {
        EnhanceTextElement element = EnhanceTextElement.of("BG")
                .setFontName("Dialog")
                .setFontSize(24)
                .setTextBackground(Color.RED, 10)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        BufferedImage image = renderElement(element, 160, 100);
        Color color = new Color(image.getRGB(2, 2), true);

        Assert.assertEquals(255, color.getAlpha());
        Assert.assertEquals(Color.RED.getRed(), color.getRed());
        Assert.assertEquals(Color.RED.getGreen(), color.getGreen());
        Assert.assertEquals(Color.RED.getBlue(), color.getBlue());
    }

    @Test
    public void shouldMeasureRichTextAcrossExplicitNewLines() throws Exception {
        PosterContext context = createContext();

        EnhanceTextElement element = EnhanceTextElement.richText(
                        TextSpan.of("Red").setColor(Color.RED),
                        TextSpan.of("\n"),
                        TextSpan.of("Bold").setFontStyle(Font.BOLD))
                .setFontName("Dialog")
                .setFontSize(24)
                .setLineHeight(32)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension dimension = element.calculateDimension(context, 400, 300);
        Object layout = measureLayout(element, context, 400, 300);
        List<?> lines = (List<?>) invoke(layout, "getLines");
        Object firstLine = lines.get(0);

        Assert.assertEquals(2, lines.size());
        Assert.assertEquals(64, dimension.getHeight());
        Assert.assertNotNull(invoke(firstLine, "getRichFragments"));
    }

    @Test
    public void shouldRenderRichTextSpansWithDifferentColors() {
        EnhanceTextElement element = EnhanceTextElement.richText(
                        TextSpan.of("Red ").setColor(Color.RED),
                        TextSpan.of("Blue").setColor(Color.BLUE))
                .setFontName("Dialog")
                .setFontSize(26)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        BufferedImage image = renderElement(element, 220, 100);

        Assert.assertTrue(countColorLikePixels(image, Color.RED, 40) > 0);
        Assert.assertTrue(countColorLikePixels(image, Color.BLUE, 40) > 0);
    }

    @Test
    public void shouldWrapRichTextAcrossStyledSpans() throws Exception {
        PosterContext context = createContext();

        EnhanceTextElement element = EnhanceTextElement.richText(
                        TextSpan.of("Rich ").setColor(Color.RED),
                        TextSpan.of("text ").setFontStyle(Font.BOLD),
                        TextSpan.of("wrap support is now enabled across spans.").setColor(Color.BLUE))
                .setFontName("Dialog")
                .setFontSize(24)
                .setLineHeight(30)
                .setAutoWrapText(120)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT))
                ;

        Dimension dimension = element.calculateDimension(context, 400, 300);
        Object layout = measureLayout(element, context, 400, 300);
        List<?> lines = (List<?>) invoke(layout, "getLines");
        Object firstLine = lines.get(0);

        Assert.assertTrue(lines.size() > 1);
        Assert.assertEquals(lines.size() * 30, dimension.getHeight());
        Assert.assertNotNull(invoke(firstLine, "getRichFragments"));
        Assert.assertFalse((Boolean) invoke(layout, "isClipOverflow"));
    }

    @Test
    public void shouldEllipsizeRichTextWithinConfiguredWidth() throws Exception {
        PosterContext context = createContext();

        EnhanceTextElement element = EnhanceTextElement.richText(
                        TextSpan.of("Rich ").setColor(Color.RED),
                        TextSpan.of("ellipsis ").setFontStyle(Font.BOLD),
                        TextSpan.of("should keep span-aware rendering.").setColor(Color.BLUE))
                .setFontName("Dialog")
                .setFontSize(22)
                .setLayoutWidth(120)
                .setOverflowStrategy(TextOverflowStrategy.ELLIPSIS)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension dimension = element.calculateDimension(context, 400, 300);
        Object layout = measureLayout(element, context, 400, 300);
        List<?> lines = (List<?>) invoke(layout, "getLines");
        Object firstLine = lines.get(0);

        Assert.assertEquals(1, lines.size());
        Assert.assertEquals(120, dimension.getWidth());
        Assert.assertTrue(((String) invoke(firstLine, "getText")).endsWith("..."));
        Assert.assertTrue((Integer) invoke(firstLine, "getWidth") <= 120);
    }

    @Test
    public void shouldApplyRichMaxLinesAfterWrap() throws Exception {
        PosterContext context = createContext();

        EnhanceTextElement element = EnhanceTextElement.richText(
                        TextSpan.of("This ").setColor(Color.RED),
                        TextSpan.of("rich ").setFontStyle(Font.BOLD),
                        TextSpan.of("text block should wrap and then truncate at the configured max lines.").setColor(Color.BLUE))
                .setFontName("Dialog")
                .setFontSize(20)
                .setLineHeight(28)
                .setAutoWrapText(120)
                .setMaxLines(2)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        Dimension dimension = element.calculateDimension(context, 400, 300);
        Object layout = measureLayout(element, context, 400, 300);
        List<?> lines = (List<?>) invoke(layout, "getLines");
        Object lastLine = lines.get(lines.size() - 1);

        Assert.assertEquals(2, lines.size());
        Assert.assertEquals(56, dimension.getHeight());
        Assert.assertTrue(((String) invoke(lastLine, "getText")).endsWith("..."));
        Assert.assertTrue((Boolean) invoke(layout, "isTruncated"));
    }

    private Object measureLayout(EnhanceTextElement element, PosterContext context, int posterWidth, int posterHeight) throws Exception {
        Method method = EnhanceTextElement.class.getDeclaredMethod("measureLayout", PosterContext.class, int.class, int.class);
        method.setAccessible(true);
        return method.invoke(element, context, posterWidth, posterHeight);
    }

    private Object invoke(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method.invoke(target);
    }

    private PosterContext createContext() {
        BufferedImage image = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setFont(new Font("Dialog", Font.PLAIN, 18));

        PosterContext context = new PosterContext();
        Config config = new Config();
        config.setFontName("Dialog");
        config.setFontSize(18);
        context.setConfig(config);
        context.setGraphics(graphics);
        return context;
    }

    private BufferedImage renderElement(EnhanceTextElement element, int posterWidth, int posterHeight) {
        BufferedImage image = new BufferedImage(posterWidth, posterHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setFont(new Font("Dialog", Font.PLAIN, 18));

        PosterContext context = new PosterContext();
        Config config = new Config();
        config.setFontName("Dialog");
        config.setFontSize(18);
        context.setConfig(config);
        context.setGraphics(graphics);

        element.render(context, posterWidth, posterHeight);
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

    private int countColorLikePixels(BufferedImage image, Color expected, int tolerance) {
        int count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color actual = new Color(image.getRGB(x, y), true);
                if (actual.getAlpha() == 0) {
                    continue;
                }
                if (Math.abs(actual.getRed() - expected.getRed()) <= tolerance
                        && Math.abs(actual.getGreen() - expected.getGreen()) <= tolerance
                        && Math.abs(actual.getBlue() - expected.getBlue()) <= tolerance) {
                    count++;
                }
            }
        }
        return count;
    }
}
