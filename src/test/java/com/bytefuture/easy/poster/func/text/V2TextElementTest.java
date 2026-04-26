package com.bytefuture.easy.poster.func.text;

import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.element.v2.text.layout.TextLine;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.Config;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextSpan;
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
                .setAutoWordWrap(80)
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
                .setAutoWordWrap(200)
                .setTextAlign(TextAlign.LEFT)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        TextElement center = TextElement.of("align")
                .setFontName("Dialog")
                .setFontSize(18)
                .setAutoWordWrap(200)
                .setTextAlign(TextAlign.CENTER)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));
        TextElement right = TextElement.of("align")
                .setFontName("Dialog")
                .setFontSize(18)
                .setAutoWordWrap(200)
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
                .setAutoWordWrap(80)
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
                .setAutoWordWrap(120)
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
                .setLayoutWidth(180)
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
