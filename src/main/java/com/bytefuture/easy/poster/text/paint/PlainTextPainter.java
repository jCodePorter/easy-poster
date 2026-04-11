package com.bytefuture.easy.poster.text.paint;

import com.bytefuture.easy.poster.element.basic.EnhanceTextElement;
import com.bytefuture.easy.poster.model.TextStroke;
import com.bytefuture.easy.poster.text.layout.LayoutLine;
import com.bytefuture.easy.poster.text.layout.TextLayoutResult;
import com.bytefuture.easy.poster.text.metrics.TextMetricsService;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PlainTextPainter {

    private final TextMetricsService textMetricsService;
    private final DecorationPainter decorationPainter;

    public PlainTextPainter(TextMetricsService textMetricsService, DecorationPainter decorationPainter) {
        this.textMetricsService = textMetricsService;
        this.decorationPainter = decorationPainter;
    }

    public void paint(EnhanceTextElement element, Graphics2D graphics,
                      LayoutLine line, TextLayoutResult layout, int startX, int startY) {
        List<TextFragment> fragments = resolveTextFragments(element, graphics, line, layout, startX);
        Paint fillPaint = graphics.getPaint();
        Stroke savedStroke = graphics.getStroke();

        if (element.getShadow() != null) {
            drawFragmentsFill(element, graphics, fragments, startY + element.getShadow().getOffsetY(),
                    element.getShadow().getOffsetX(), element.getShadow().getColor());
        }
        if (element.getStroke() != null) {
            drawFragmentsStroke(element, graphics, fragments, startY, element.getStroke());
        }

        drawFragmentsFill(element, graphics, fragments, startY, 0, fillPaint);
        this.decorationPainter.paintPlainDecorations(element, graphics, line, startX, startY, fillPaint);
        graphics.setStroke(savedStroke);
        graphics.setPaint(fillPaint);
    }

    private List<TextFragment> resolveTextFragments(EnhanceTextElement element, Graphics2D graphics,
                                                    LayoutLine line, TextLayoutResult layout, int startX) {
        if (!line.isJustified()) {
            return Collections.singletonList(new TextFragment(line.getText(), startX));
        }

        JustifySegments segments = splitJustifySegments(line.getText());
        if (segments.words.size() < 2 || line.getWidth() >= layout.getContentWidth()) {
            return Collections.singletonList(new TextFragment(line.getText(), startX));
        }

        int extraWidth = layout.getContentWidth() - line.getWidth();
        int gapCount = segments.spaceRunLengths.size();
        if (gapCount <= 0) {
            return Collections.singletonList(new TextFragment(line.getText(), startX));
        }

        FontMetrics fontMetrics = graphics.getFontMetrics();
        int extraPerGap = extraWidth / gapCount;
        int remainder = extraWidth % gapCount;
        int cursorX = startX;
        List<TextFragment> fragments = new ArrayList<TextFragment>(segments.words.size());

        fragments.add(new TextFragment(segments.words.get(0), cursorX));
        cursorX += measureLineWidth(element, segments.words.get(0), fontMetrics, graphics);

        for (int i = 0; i < gapCount; i++) {
            int gapWidth = measureGapWidth(element, segments.spaceRunLengths.get(i), fontMetrics, graphics)
                    + extraPerGap + (i < remainder ? 1 : 0);
            cursorX += gapWidth;
            fragments.add(new TextFragment(segments.words.get(i + 1), cursorX));
            cursorX += measureLineWidth(element, segments.words.get(i + 1), fontMetrics, graphics);
        }
        return fragments;
    }

    private void drawFragmentsFill(EnhanceTextElement element, Graphics2D graphics, List<TextFragment> fragments,
                                   int baselineY, int xOffset, Paint paint) {
        Paint savedPaint = graphics.getPaint();
        graphics.setPaint(paint);
        for (TextFragment fragment : fragments) {
            if (fragment.getText().isEmpty()) {
                continue;
            }
            drawFragmentText(element, graphics, fragment.getText(), fragment.getX() + xOffset, baselineY);
        }
        graphics.setPaint(savedPaint);
    }

    private void drawFragmentsStroke(EnhanceTextElement element, Graphics2D graphics, List<TextFragment> fragments,
                                     int baselineY, TextStroke textStroke) {
        Paint savedPaint = graphics.getPaint();
        Stroke savedStroke = graphics.getStroke();
        graphics.setPaint(textStroke.getColor());
        graphics.setStroke(new BasicStroke(textStroke.getWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (TextFragment fragment : fragments) {
            if (fragment.getText().isEmpty()) {
                continue;
            }
            drawFragmentStroke(element, graphics, fragment.getText(), fragment.getX(), baselineY);
        }

        graphics.setStroke(savedStroke);
        graphics.setPaint(savedPaint);
    }

    private void drawFragmentText(EnhanceTextElement element, Graphics2D graphics,
                                  String textValue, int startX, int baselineY) {
        if (element.getLetterSpacing() == 0 || textValue.length() <= 1) {
            graphics.drawString(textValue, startX, baselineY);
            return;
        }

        FontMetrics fontMetrics = graphics.getFontMetrics();
        int cursorX = startX;
        for (int i = 0; i < textValue.length(); i++) {
            String currentChar = String.valueOf(textValue.charAt(i));
            graphics.drawString(currentChar, cursorX, baselineY);
            if (i < textValue.length() - 1) {
                cursorX += measureBaseStringWidth(currentChar, fontMetrics, graphics) + element.getLetterSpacing();
            }
        }
    }

    private void drawFragmentStroke(EnhanceTextElement element, Graphics2D graphics,
                                    String textValue, int startX, int baselineY) {
        if (element.getLetterSpacing() == 0 || textValue.length() <= 1) {
            Shape outline = graphics.getFont().createGlyphVector(graphics.getFontRenderContext(), textValue)
                    .getOutline(startX, baselineY);
            graphics.draw(outline);
            return;
        }

        FontMetrics fontMetrics = graphics.getFontMetrics();
        int cursorX = startX;
        for (int i = 0; i < textValue.length(); i++) {
            String currentChar = String.valueOf(textValue.charAt(i));
            Shape outline = graphics.getFont().createGlyphVector(graphics.getFontRenderContext(), currentChar)
                    .getOutline(cursorX, baselineY);
            graphics.draw(outline);
            if (i < textValue.length() - 1) {
                cursorX += measureBaseStringWidth(currentChar, fontMetrics, graphics) + element.getLetterSpacing();
            }
        }
    }

    private JustifySegments splitJustifySegments(String lineText) {
        List<String> words = new ArrayList<String>();
        List<Integer> spaceRunLengths = new ArrayList<Integer>();
        StringBuilder currentWord = new StringBuilder();
        int currentSpaceRun = 0;

        for (int i = 0; i < lineText.length(); i++) {
            char current = lineText.charAt(i);
            if (current == ' ') {
                if (currentWord.length() > 0) {
                    words.add(currentWord.toString());
                    currentWord.setLength(0);
                    currentSpaceRun = 1;
                } else if (!words.isEmpty()) {
                    currentSpaceRun++;
                }
                continue;
            }

            if (currentSpaceRun > 0) {
                spaceRunLengths.add(Integer.valueOf(currentSpaceRun));
                currentSpaceRun = 0;
            }
            currentWord.append(current);
        }

        if (currentWord.length() > 0) {
            words.add(currentWord.toString());
        }
        if (spaceRunLengths.size() >= words.size() && !spaceRunLengths.isEmpty()) {
            spaceRunLengths.remove(spaceRunLengths.size() - 1);
        }
        return new JustifySegments(words, spaceRunLengths);
    }

    private int measureLineWidth(EnhanceTextElement element, String text, FontMetrics fontMetrics, Graphics2D graphics) {
        return this.textMetricsService.measureLineWidth(text, fontMetrics, graphics, element.getLetterSpacing());
    }

    private int measureGapWidth(EnhanceTextElement element, int spaceRunLength, FontMetrics fontMetrics, Graphics2D graphics) {
        return this.textMetricsService.measureGapWidth(spaceRunLength, fontMetrics, graphics, element.getLetterSpacing());
    }

    private int measureBaseStringWidth(String text, FontMetrics fontMetrics, Graphics2D graphics) {
        return this.textMetricsService.measureBaseStringWidth(text, fontMetrics, graphics);
    }

    private static final class JustifySegments {
        private final List<String> words;
        private final List<Integer> spaceRunLengths;

        private JustifySegments(List<String> words, List<Integer> spaceRunLengths) {
            this.words = words;
            this.spaceRunLengths = spaceRunLengths;
        }
    }

    private static final class TextFragment {
        private final String text;
        private final int x;

        private TextFragment(String text, int x) {
            this.text = text;
            this.x = x;
        }

        private String getText() {
            return this.text;
        }

        private int getX() {
            return this.x;
        }
    }
}
