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

        Assert.assertEquals(2, line.getRuns().size());
        Assert.assertEquals(Color.BLUE, line.getRuns().get(0).getStyle().getColor());
        Assert.assertEquals(Font.BOLD, line.getRuns().get(0).getStyle().getFont().getStyle());
        Assert.assertEquals(20, line.getRuns().get(0).getStyle().getFont().getSize());
        Assert.assertEquals("Dialog", line.getRuns().get(0).getStyle().getFont().getFamily());

        Assert.assertEquals(Color.RED, line.getRuns().get(1).getStyle().getColor());
        Assert.assertEquals(Font.BOLD, line.getRuns().get(1).getStyle().getFont().getStyle());
        Assert.assertEquals(24, line.getRuns().get(1).getStyle().getFont().getSize());
        Assert.assertEquals("Dialog", line.getRuns().get(1).getStyle().getFont().getFamily());
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
                plainLayout.getLines().get(0).getRuns().get(0).getStyle().getFont(),
                richLayout.getLines().get(0).getRuns().get(0).getStyle().getFont()
        );
        Assert.assertEquals(
                plainLayout.getLines().get(0).getRuns().get(0).getStyle().getColor(),
                richLayout.getLines().get(0).getRuns().get(0).getStyle().getColor()
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

    private static final class ContextHolder {
        private final BufferedImage image;
        private final PosterContext context;

        private ContextHolder(BufferedImage image, PosterContext context) {
            this.image = image;
            this.context = context;
        }
    }
}
