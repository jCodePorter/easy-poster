package com.bytefuture.easy.poster.func.text;

import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLine;
import com.bytefuture.easy.poster.element.v2.text.style.TextOverflow;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

public class V2TextElementTest {

    @Test
    public void shouldMergeBlockDefaultsWithSpanOverrides() {
        TextElement element = TextElement.of(
                        TextSpan.of("Hello"),
                        TextSpan.of("World").setColor(Color.RED).setFontSize(24))
                .setFontName("Dialog")
                .setFontStyle(Font.BOLD)
                .setFontSize(20)
                .setColor(Color.BLUE)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(element, 300, 120);
        TextLayoutResult layout = element.getLastLayout();
        TextLine line = layout.getLines().get(0);

        Assert.assertEquals(2, line.getSegments().size());
        Assert.assertEquals(Color.BLUE, line.getSegments().get(0).getStyle().getColor());
        Assert.assertEquals(Font.BOLD, line.getSegments().get(0).getStyle().getFont().getStyle());
        Assert.assertEquals(20, line.getSegments().get(0).getStyle().getFont().getSize());
        Assert.assertEquals("Dialog", line.getSegments().get(0).getStyle().getFont().getFamily());

        Assert.assertEquals(Color.RED, line.getSegments().get(1).getStyle().getColor());
        Assert.assertEquals(Font.BOLD, line.getSegments().get(1).getStyle().getFont().getStyle());
        Assert.assertEquals(24, line.getSegments().get(1).getStyle().getFont().getSize());
        Assert.assertEquals("Dialog", line.getSegments().get(1).getStyle().getFont().getFamily());
    }

    @Test
    public void shouldConvertPlainTextIntoEquivalentRichTextRun() {
        TextElement plain = TextElement.of("plain text")
                .setFontName("Dialog")
                .setFontSize(18)
                .setColor(Color.BLACK)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        TextElement rich = TextElement.of(TextSpan.of("plain text"))
                .setFontName("Dialog")
                .setFontSize(18)
                .setColor(Color.BLACK)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(plain, 300, 120);
        measure(rich, 300, 120);

        TextLayoutResult plainLayout = plain.getLastLayout();
        TextLayoutResult richLayout = rich.getLastLayout();

        Assert.assertEquals(plainLayout.getWidth(), richLayout.getWidth());
        Assert.assertEquals(plainLayout.getHeight(), richLayout.getHeight());
        Assert.assertEquals(plainLayout.getLines().size(), richLayout.getLines().size());
        Assert.assertEquals(plainLayout.getLines().get(0).getText(), richLayout.getLines().get(0).getText());
        Assert.assertEquals(
                plainLayout.getLines().get(0).getSegments().get(0).getStyle().getFont(),
                richLayout.getLines().get(0).getSegments().get(0).getStyle().getFont()
        );
        Assert.assertEquals(
                plainLayout.getLines().get(0).getSegments().get(0).getStyle().getColor(),
                richLayout.getLines().get(0).getSegments().get(0).getStyle().getColor()
        );
    }

    @Test
    public void shouldWrapHorizontalTextWithinConfiguredWidth() {
        TextElement element = TextElement.of("alpha beta gamma delta epsilon")
                .setFontName("Dialog")
                .setFontSize(18)
                .maxTextWidth(80)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(element, 300, 200);
        TextLayoutResult layout = element.getLastLayout();

        Assert.assertTrue(layout.getLines().size() > 1);
        Assert.assertEquals(80, layout.getWidth());
        for (TextLine line : layout.getLines()) {
            Assert.assertTrue(line.getWidth() <= 80);
        }
    }

    @Test
    public void shouldApplyAlignmentInsideLayoutWidth() {
        TextElement left = TextElement.of("align")
                .setFontName("Dialog")
                .setFontSize(18)
                .maxTextWidth(200)
                .setTextAlign(TextAlign.LEFT)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        TextElement center = TextElement.of("align")
                .setFontName("Dialog")
                .setFontSize(18)
                .maxTextWidth(200)
                .setTextAlign(TextAlign.CENTER)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        TextElement right = TextElement.of("align")
                .setFontName("Dialog")
                .setFontSize(18)
                .maxTextWidth(200)
                .setTextAlign(TextAlign.RIGHT)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(left, 300, 120);
        measure(center, 300, 120);
        measure(right, 300, 120);

        int leftOffset = left.getLastLayout().getLines().get(0).getOffsetX();
        int centerOffset = center.getLastLayout().getLines().get(0).getOffsetX();
        int rightOffset = right.getLastLayout().getLines().get(0).getOffsetX();

        Assert.assertEquals(0, leftOffset);
        Assert.assertTrue(centerOffset > 0);
        Assert.assertTrue(rightOffset > centerOffset);
    }

    @Test
    public void shouldUseBlockLevelLineHeightForMultiLineLayout() {
        TextElement element = TextElement.of("alpha beta gamma delta epsilon")
                .setFontName("Dialog")
                .setFontSize(18)
                .maxTextWidth(80)
                .setLineHeight(40)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(element, 300, 200);
        TextLayoutResult layout = element.getLastLayout();

        Assert.assertTrue(layout.getLines().size() > 1);
        Assert.assertEquals(40, layout.getLineHeight());
        Assert.assertEquals(layout.getLines().size() * 40, layout.getHeight());
        Assert.assertTrue(layout.getBaselineOffset() < layout.getLineHeight());
    }

    @Test
    public void shouldMergeUnderlineAndStrikeThroughFromBlockAndSpanStyles() {
        TextElement element = TextElement.of(
                        TextSpan.of("underlined"),
                        TextSpan.of(" plain").setUnderline(false),
                        TextSpan.of(" deleted").setStrikeThrough(true))
                .setFontName("Dialog")
                .setFontSize(20)
                .setUnderline(true)
                .setStrikeThrough(false)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(element, 320, 120);
        TextLine line = element.getLastLayout().getLines().get(0);

        TextLine.Segment underlined = findSegmentContaining(line, "underlined");
        TextLine.Segment plain = findSegmentContaining(line, "plain");
        TextLine.Segment deleted = findSegmentContaining(line, "deleted");

        Assert.assertTrue(underlined.getStyle().isUnderline());
        Assert.assertFalse(underlined.getStyle().isStrikeThrough());
        Assert.assertFalse(plain.getStyle().isUnderline());
        Assert.assertFalse(plain.getStyle().isStrikeThrough());
        Assert.assertTrue(deleted.getStyle().isUnderline());
        Assert.assertTrue(deleted.getStyle().isStrikeThrough());
    }

    @Test
    public void shouldJustifyAutoWrappedLinesWithinLayoutWidth() {
        TextElement element = TextElement.of("alpha beta gamma delta epsilon")
                .setFontName("Dialog")
                .setFontSize(18)
                .maxTextWidth(120)
                .setTextAlign(TextAlign.JUSTIFY)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(element, 320, 200);
        TextLayoutResult layout = element.getLastLayout();

        Assert.assertTrue(layout.getLines().size() > 1);
        for (TextLine line : layout.getLines()) {
            if (containsStretchableSpace(line)) {
                int occupiedWidth = resolveOccupiedWidth(line);
                Assert.assertEquals(layout.getWidth(), occupiedWidth);
            }
        }
    }

    @Test
    public void shouldJustifyExplicitBreakLinesWithinLayoutWidth() {
        TextElement element = TextElement.of("alpha beta\ngamma delta")
                .setFontName("Dialog")
                .setFontSize(18)
                .maxTextWidth(180)
                .setTextAlign(TextAlign.JUSTIFY)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(element, 320, 200);
        TextLayoutResult layout = element.getLastLayout();

        Assert.assertEquals(2, layout.getLines().size());
        for (TextLine line : layout.getLines()) {
            int occupiedWidth = resolveOccupiedWidth(line);
            Assert.assertEquals(layout.getWidth(), occupiedWidth);
        }
    }

    @Test
    public void shouldRenderBasicRichTextColors() {
        TextElement element = TextElement.of(
                        TextSpan.of("RED").setColor(Color.RED),
                        TextSpan.of(" BLUE").setColor(Color.BLUE))
                .setFontName("Dialog")
                .setFontSize(24)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        BufferedImage image = render(element, 240, 80);
        Assert.assertTrue(countColorLikePixels(image, Color.RED, 80) > 0);
        Assert.assertTrue(countColorLikePixels(image, Color.BLUE, 80) > 0);
    }

    @Test
    public void shouldRenderUnderlineAndStrikeThroughText() {
        TextElement element = TextElement.of(
                        TextSpan.of("UNDER").setUnderline(true).setColor(Color.RED),
                        TextSpan.of(" STRIKE").setStrikeThrough(true).setColor(Color.BLUE))
                .setFontName("Dialog")
                .setFontSize(24)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        BufferedImage image = render(element, 280, 100);
        Assert.assertTrue(countColorLikePixels(image, Color.RED, 80) > 0);
        Assert.assertTrue(countColorLikePixels(image, Color.BLUE, 80) > 0);
    }

    @Test
    public void shouldResolveSpanBackgroundStyle() {
        TextElement element = TextElement.of(
                        TextSpan.of("plain"),
                        TextSpan.of(" mark")
                                .setBackgroundColor(Color.YELLOW)
                                .setBackgroundPadding(6)
                                .setBackgroundRadius(8))
                .setFontName("Dialog")
                .setFontSize(20)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(element, 320, 120);
        TextLine line = element.getLastLayout().getLines().get(0);
        TextLine.Segment plain = findSegmentContaining(line, "plain");
        TextLine.Segment mark = findSegmentContaining(line, "mark");

        Assert.assertNull(plain.getStyle().getBackgroundColor());
        Assert.assertEquals(Color.YELLOW, mark.getStyle().getBackgroundColor());
        Assert.assertEquals(6, mark.getStyle().getBackgroundPadding());
        Assert.assertEquals(8, mark.getStyle().getBackgroundRadius());
    }

    @Test
    public void shouldApplyDefaultSpanBackgroundBoxValues() {
        TextElement element = TextElement.of(
                        TextSpan.of("mark").setBackgroundColor(Color.YELLOW))
                .setFontName("Dialog")
                .setFontSize(20)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(element, 240, 120);
        TextLine.Segment mark = element.getLastLayout().getLines().get(0).getSegments().get(0);

        Assert.assertEquals(2, mark.getStyle().getBackgroundPadding());
        Assert.assertEquals(0, mark.getStyle().getBackgroundRadius());
    }

    @Test
    public void shouldRejectNegativeSpanBackgroundPadding() {
        try {
            TextSpan.of("bad").setBackgroundPadding(-1);
            Assert.fail("expected PosterException");
        } catch (PosterException ex) {
            Assert.assertEquals("backgroundPadding must be greater than or equal to 0", ex.getMessage());
        }
    }

    @Test
    public void shouldRejectNegativeSpanBackgroundRadius() {
        try {
            TextSpan.of("bad").setBackgroundRadius(-1);
            Assert.fail("expected PosterException");
        } catch (PosterException ex) {
            Assert.assertEquals("backgroundRadius must be greater than or equal to 0", ex.getMessage());
        }
    }

    @Test
    public void shouldRenderSpanBackgroundPixels() {
        TextElement element = TextElement.of(
                        TextSpan.of(" highlight ")
                                .setBackgroundColor(Color.YELLOW)
                                .setBackgroundPadding(4))
                .setFontName("Dialog")
                .setFontSize(24)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        BufferedImage image = render(element, 260, 100);
        Assert.assertTrue(countColorLikePixels(image, Color.YELLOW, 40) > 80);
    }

    @Test
    public void shouldRenderWrappedSpanBackgroundAcrossLines() {
        TextElement element = TextElement.of(
                        TextSpan.of("alpha beta gamma delta epsilon")
                                .setBackgroundColor(Color.CYAN)
                                .setBackgroundPadding(3)
                                .setBackgroundRadius(6))
                .setFontName("Dialog")
                .setFontSize(22)
                .maxTextWidth(110)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        BufferedImage image = render(element, 220, 180);
        Assert.assertTrue(element.getLastLayout().getLines().size() > 1);
        Assert.assertTrue(countColorLikePixels(image, Color.CYAN, 40) > 120);
    }

    @Test
    public void shouldClampWrappedTextToMaxLines() {
        TextElement element = TextElement.of("alpha beta gamma delta epsilon zeta eta theta iota kappa lambda")
                .setFontName("Dialog")
                .setFontSize(18)
                .maxTextWidth(90)
                .setMaxLines(2)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(element, 320, 200);
        TextLayoutResult layout = element.getLastLayout();

        Assert.assertEquals(2, layout.getLines().size());
        Assert.assertEquals(layout.getLineHeight() * 2, layout.getHeight());
    }

    @Test
    public void shouldAppendEllipsisWhenOverflowExceedsMaxLines() {
        TextElement element = TextElement.of("alpha beta gamma delta epsilon zeta eta theta iota kappa lambda")
                .setFontName("Dialog")
                .setFontSize(18)
                .maxTextWidth(90)
                .setMaxLines(2)
                .setTextOverflow(TextOverflow.ELLIPSIS)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(element, 320, 200);
        TextLine line = element.getLastLayout().getLines().get(1);

        Assert.assertTrue(line.getText().endsWith("."));
        Assert.assertTrue(line.getWidth() <= 90);
    }

    @Test
    public void shouldClipWithoutEllipsisWhenOverflowModeIsClip() {
        TextElement element = TextElement.of("alpha beta gamma delta epsilon zeta eta theta iota kappa lambda")
                .setFontName("Dialog")
                .setFontSize(18)
                .maxTextWidth(90)
                .setMaxLines(2)
                .setTextOverflow(TextOverflow.CLIP)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(element, 320, 200);
        TextLine line = element.getLastLayout().getLines().get(1);

        Assert.assertFalse(line.getText().endsWith("."));
        Assert.assertTrue(line.getWidth() <= 90);
    }

    @Test
    public void shouldNotJustifyTruncatedLastLine() {
        TextElement element = TextElement.of("alpha beta gamma delta epsilon zeta eta theta iota kappa lambda")
                .setFontName("Dialog")
                .setFontSize(18)
                .maxTextWidth(120)
                .setMaxLines(2)
                .setTextOverflow(TextOverflow.ELLIPSIS)
                .setTextAlign(TextAlign.JUSTIFY)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(element, 320, 200);
        TextLayoutResult layout = element.getLastLayout();
        TextLine lastLine = layout.getLines().get(layout.getLines().size() - 1);

        Assert.assertTrue(resolveOccupiedWidth(lastLine) < layout.getWidth());
    }

    @Test
    public void shouldKeepEllipsisStyleConsistentWithLastVisibleSegment() {
        TextElement element = TextElement.of(
                        TextSpan.of("alpha beta gamma ").setColor(Color.BLACK),
                        TextSpan.of("delta epsilon zeta eta theta iota kappa lambda")
                                .setColor(Color.RED)
                                .setUnderline(true))
                .setFontName("Dialog")
                .setFontSize(18)
                .maxTextWidth(120)
                .setMaxLines(2)
                .setTextOverflow(TextOverflow.ELLIPSIS)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        measure(element, 320, 200);
        TextLine lastLine = element.getLastLayout().getLines().get(1);
        TextLine.Segment lastSegment = lastLine.getSegments().get(lastLine.getSegments().size() - 1);

        Assert.assertTrue(lastSegment.getText().endsWith("."));
        Assert.assertEquals(Color.RED, lastSegment.getStyle().getColor());
        Assert.assertTrue(lastSegment.getStyle().isUnderline());
    }

    @Test
    public void shouldRenderBlockGradientForSegmentsWithoutSpanColorOverride() {
        TextElement element = TextElement.of("Gradient Text")
                .setFontName("Dialog")
                .setFontSize(30)
                .setGradient(Gradient.of(new Color[]{Color.RED, Color.BLUE}, GradientDirection.LEFT_RIGHT))
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        BufferedImage image = render(element, 320, 120);
        Assert.assertTrue(countColorLikePixels(image, Color.RED, 90) > 20);
        Assert.assertTrue(countColorLikePixels(image, Color.BLUE, 90) > 20);
    }

    @Test
    public void shouldKeepSpanColorOverrideWhenBlockGradientPresent() {
        TextElement element = TextElement.of(
                        TextSpan.of("Gradient "),
                        TextSpan.of("GREEN").setColor(Color.GREEN))
                .setFontName("Dialog")
                .setFontSize(30)
                .setGradient(Gradient.of(new Color[]{Color.RED, Color.BLUE}, GradientDirection.LEFT_RIGHT))
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        BufferedImage image = render(element, 360, 120);
        int gradientPixels = countColorLikePixels(image, Color.RED, 90) + countColorLikePixels(image, Color.BLUE, 90);
        Assert.assertTrue(gradientPixels > 20);
        Assert.assertTrue(countColorLikePixels(image, Color.GREEN, 70) > 20);
    }

    @Test
    public void shouldMergeAdjacentSpanBackgroundsOnSameLine() {
        TextElement element = TextElement.of(
                        TextSpan.of("AB")
                                .setColor(Color.BLACK)
                                .setBackgroundColor(Color.ORANGE)
                                .setBackgroundPadding(6)
                                .setBackgroundRadius(12),
                        TextSpan.of("CD")
                                .setColor(Color.BLUE)
                                .setBackgroundColor(Color.ORANGE)
                                .setBackgroundPadding(6)
                                .setBackgroundRadius(12))
                .setFontName("Dialog")
                .setFontSize(32)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        BufferedImage image = render(element, 260, 120);
        TextLine line = element.getLastLayout().getLines().get(0);
        TextLine.Segment first = line.getSegments().get(0);
        TextLine.Segment second = line.getSegments().get(1);

        int padding = first.getStyle().getBackgroundPadding();
        int seamX = first.getOffsetX() + first.getWidth();
        int baselineY = element.getLastLayout().toDimension(0).getPoint().getY()
                + element.getLastLayout().toDimension(0).getYOffset();
        int backgroundTopY = resolveBackgroundTopY(first, baselineY, element.getLastLayout().getLineHeight());
        Color seamPixel = new Color(image.getRGB(seamX, backgroundTopY + 1), true);

        Assert.assertEquals(Color.ORANGE.getRed(), seamPixel.getRed());
        Assert.assertEquals(Color.ORANGE.getGreen(), seamPixel.getGreen());
        Assert.assertEquals(Color.ORANGE.getBlue(), seamPixel.getBlue());
        Assert.assertTrue(second.getOffsetX() - seamX <= padding);
    }

    private TextLayoutResult measure(TextElement element, int width, int height) {
        PosterContext context = createContext(width, height).context;
        element.calculateDimension(context, width, height);
        return element.getLastLayout();
    }

    private BufferedImage render(TextElement element, int width, int height) {
        ContextHolder holder = createContext(width, height);
        element.render(holder.context, width, height);
        return holder.image;
    }

    private ContextHolder createContext(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        Config config = new Config();
        config.setFontName("Dialog");
        config.setFontSize(18);
        config.setColor(Color.BLACK);

        PosterContext context = new PosterContext();
        context.setGraphics(graphics);
        context.setConfig(config);
        return new ContextHolder(image, context);
    }

    private int countColorLikePixels(BufferedImage image, Color target, int tolerance) {
        int count = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color actual = new Color(image.getRGB(x, y), true);
                if (actual.getAlpha() == 0) {
                    continue;
                }
                if (Math.abs(actual.getRed() - target.getRed()) <= tolerance
                        && Math.abs(actual.getGreen() - target.getGreen()) <= tolerance
                        && Math.abs(actual.getBlue() - target.getBlue()) <= tolerance) {
                    count++;
                }
            }
        }
        return count;
    }

    private int resolveBackgroundTopY(TextLine.Segment segment, int baselineY, int lineHeight) {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setFont(segment.getStyle().getFont());
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int padding = segment.getStyle().getBackgroundPadding();
        int desiredHeight = fontMetrics.getHeight() + padding * 2;
        int backgroundHeight = Math.min(desiredHeight, lineHeight);
        int backgroundY = baselineY - fontMetrics.getAscent() - padding;
        if (backgroundHeight < desiredHeight) {
            backgroundY += (desiredHeight - backgroundHeight) / 2;
        }
        graphics.dispose();
        return backgroundY;
    }

    private boolean containsStretchableSpace(TextLine line) {
        for (TextLine.Segment segment : line.getSegments()) {
            if (segment.isStretchableSpace()) {
                return true;
            }
        }
        return false;
    }

    private int resolveOccupiedWidth(TextLine line) {
        int maxRight = 0;
        for (TextLine.Segment segment : line.getSegments()) {
            maxRight = Math.max(maxRight, segment.getOffsetX() + segment.getWidth());
        }
        return line.getOffsetX() + maxRight;
    }

    private TextLine.Segment findSegmentContaining(TextLine line, String expectedText) {
        for (TextLine.Segment segment : line.getSegments()) {
            if (segment.getText().contains(expectedText)) {
                return segment;
            }
        }
        Assert.fail("segment not found: " + expectedText);
        return null;
    }

    private static final class ContextHolder {
        private final BufferedImage image;
        private final PosterContext context;

        private ContextHolder(BufferedImage image, PosterContext context) {
            this.image = image;
            this.context = context;
        }
    }
}
