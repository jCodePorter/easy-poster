package com.bytefuture.easy.poster.element.v2.text.wrap;

import com.bytefuture.easy.poster.element.v2.text.split.SplitTextInfo;
import com.bytefuture.easy.poster.element.v2.text.layout.TextRenderSpec;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EllipsisProcessor {

    public interface PlainTextMeasurer {
        int measureLineWidth(String text, FontMetrics fontMetrics, Graphics2D graphics);
    }

    public interface RichTextMeasurer {
        int measureBaseStringWidth(String text, FontMetrics fontMetrics, Graphics2D graphics);

        int measureRichGlyphsWidth(List<RichGlyph> glyphs, int letterSpacing);
    }

    public List<SplitTextInfo> applyPlainWidthEllipsis(TextRenderSpec spec, List<SplitTextInfo> rawLines, int widthLimit,
                                                       FontMetrics fontMetrics, Graphics2D graphics,
                                                       PlainTextMeasurer measurer) {
        List<SplitTextInfo> ellipsized = new ArrayList<SplitTextInfo>(rawLines.size());
        for (SplitTextInfo line : rawLines) {
            if (line.getWidth() <= widthLimit) {
                ellipsized.add(line);
            } else {
                ellipsized.add(appendPlainEllipsis(spec, line, widthLimit, fontMetrics, graphics, measurer));
            }
        }
        return ellipsized;
    }

    public SplitTextInfo appendPlainEllipsis(TextRenderSpec spec, SplitTextInfo originalLine, int maxWidth,
                                             FontMetrics fontMetrics, Graphics2D graphics, PlainTextMeasurer measurer) {
        String suffix = fitPlainSuffixToWidth(spec.getEllipsis(), maxWidth, fontMetrics, graphics, measurer);
        if (suffix.isEmpty()) {
            return originalLine;
        }

        String baseText = originalLine.getText() == null ? "" : originalLine.getText();
        if (!suffix.isEmpty() && baseText.endsWith(suffix)) {
            baseText = baseText.substring(0, baseText.length() - suffix.length());
        }
        if (maxWidth == Integer.MAX_VALUE) {
            String merged = baseText + suffix;
            return SplitTextInfo.of(merged, measurer.measureLineWidth(merged, fontMetrics, graphics));
        }

        String candidate = baseText + suffix;
        while (!candidate.isEmpty() && measurer.measureLineWidth(candidate, fontMetrics, graphics) > maxWidth) {
            if (baseText.isEmpty()) {
                candidate = suffix;
                break;
            }
            baseText = baseText.substring(0, baseText.length() - 1);
            candidate = baseText + suffix;
        }

        if (measurer.measureLineWidth(candidate, fontMetrics, graphics) > maxWidth) {
            candidate = suffix;
        }
        return SplitTextInfo.of(candidate, measurer.measureLineWidth(candidate, fontMetrics, graphics));
    }

    public List<RichLine> applyRichWidthEllipsis(TextRenderSpec spec, List<RichLine> rawLines, int widthLimit,
                                                 Font baseFont, Color defaultColor, Graphics2D graphics,
                                                 RichTextMeasurer measurer) {
        List<RichLine> ellipsized = new ArrayList<RichLine>(rawLines.size());
        for (RichLine line : rawLines) {
            if (line.getWidth() <= widthLimit) {
                ellipsized.add(line);
            } else {
                ellipsized.add(appendRichEllipsis(spec, line, widthLimit, baseFont, defaultColor, graphics, measurer));
            }
        }
        return ellipsized;
    }

    public RichLine appendRichEllipsis(TextRenderSpec spec, RichLine originalLine, int maxWidth,
                                       Font baseFont, Color defaultColor, Graphics2D graphics,
                                       RichTextMeasurer measurer) {
        List<RichGlyph> originalGlyphs = new ArrayList<RichGlyph>(originalLine.getGlyphs());
        List<RichGlyph> suffixGlyphs = fitRichSuffixToWidth(
                buildEllipsisGlyphs(spec, resolveEllipsisStyleGlyph(spec, originalGlyphs, baseFont, defaultColor,
                        graphics, measurer), graphics, measurer),
                maxWidth, spec.getLetterSpacing(), measurer);
        if (suffixGlyphs.isEmpty()) {
            return originalLine;
        }

        if (!spec.getEllipsis().isEmpty() && originalLine.getText().endsWith(spec.getEllipsis())
                && originalGlyphs.size() >= spec.getEllipsis().length()) {
            originalGlyphs = new ArrayList<RichGlyph>(
                    originalGlyphs.subList(0, originalGlyphs.size() - spec.getEllipsis().length()));
        }

        while (!originalGlyphs.isEmpty()
                && measurer.measureRichGlyphsWidth(joinRichGlyphs(originalGlyphs, suffixGlyphs), spec.getLetterSpacing()) > maxWidth) {
            originalGlyphs.remove(originalGlyphs.size() - 1);
        }

        List<RichGlyph> candidateGlyphs = joinRichGlyphs(originalGlyphs, suffixGlyphs);
        if (measurer.measureRichGlyphsWidth(candidateGlyphs, spec.getLetterSpacing()) > maxWidth) {
            candidateGlyphs = new ArrayList<RichGlyph>(suffixGlyphs);
        }
        return createRichLineFromGlyphs(candidateGlyphs, spec.getLetterSpacing());
    }

    private String fitPlainSuffixToWidth(String suffix, int maxWidth, FontMetrics fontMetrics,
                                         Graphics2D graphics, PlainTextMeasurer measurer) {
        if (suffix.isEmpty() || maxWidth == Integer.MAX_VALUE) {
            return suffix;
        }

        String candidate = suffix;
        while (!candidate.isEmpty() && measurer.measureLineWidth(candidate, fontMetrics, graphics) > maxWidth) {
            candidate = candidate.substring(0, candidate.length() - 1);
        }
        return candidate;
    }

    private List<RichGlyph> buildEllipsisGlyphs(TextRenderSpec spec, RichGlyph templateGlyph,
                                                Graphics2D graphics, RichTextMeasurer measurer) {
        if (spec.getEllipsis().isEmpty()) {
            return Collections.emptyList();
        }

        List<RichGlyph> suffixGlyphs = new ArrayList<RichGlyph>(spec.getEllipsis().length());
        FontMetrics fontMetrics = graphics.getFontMetrics(templateGlyph.getFont());
        for (int i = 0; i < spec.getEllipsis().length(); i++) {
            String current = String.valueOf(spec.getEllipsis().charAt(i));
            suffixGlyphs.add(new RichGlyph(current,
                    measurer.measureBaseStringWidth(current, fontMetrics, graphics),
                    templateGlyph.getFont(), templateGlyph.getColor(), templateGlyph.getBackgroundColor(),
                    templateGlyph.getShadow(), templateGlyph.getStroke(), templateGlyph.getBaselineShift(),
                    templateGlyph.isUnderline(), templateGlyph.isStrikeThrough()));
        }
        return suffixGlyphs;
    }

    private RichGlyph resolveEllipsisStyleGlyph(TextRenderSpec spec, List<RichGlyph> originalGlyphs,
                                                Font baseFont, Color defaultColor, Graphics2D graphics,
                                                RichTextMeasurer measurer) {
        if (!originalGlyphs.isEmpty()) {
            return originalGlyphs.get(originalGlyphs.size() - 1);
        }
        FontMetrics fontMetrics = graphics.getFontMetrics(baseFont);
        return new RichGlyph(".",
                measurer.measureBaseStringWidth(".", fontMetrics, graphics),
                baseFont, defaultColor, null, spec.getShadow(), spec.getStroke(), 0,
                spec.isUnderline(), spec.isStrikeThrough());
    }

    private List<RichGlyph> fitRichSuffixToWidth(List<RichGlyph> suffixGlyphs, int maxWidth,
                                                 int letterSpacing, RichTextMeasurer measurer) {
        if (maxWidth == Integer.MAX_VALUE) {
            return suffixGlyphs;
        }
        List<RichGlyph> fitted = new ArrayList<RichGlyph>(suffixGlyphs);
        while (!fitted.isEmpty() && measurer.measureRichGlyphsWidth(fitted, letterSpacing) > maxWidth) {
            fitted.remove(fitted.size() - 1);
        }
        return fitted;
    }

    private List<RichGlyph> joinRichGlyphs(List<RichGlyph> leftGlyphs, List<RichGlyph> rightGlyphs) {
        List<RichGlyph> merged = new ArrayList<RichGlyph>(leftGlyphs.size() + rightGlyphs.size());
        merged.addAll(leftGlyphs);
        merged.addAll(rightGlyphs);
        return merged;
    }

    private RichLine createRichLineFromGlyphs(List<RichGlyph> glyphs, int letterSpacing) {
        if (glyphs.isEmpty()) {
            return new RichLine("", 0, Collections.<RichTextFragment>emptyList(), Collections.<RichGlyph>emptyList());
        }

        StringBuilder textBuilder = new StringBuilder();
        List<RichTextFragment> fragments = new ArrayList<RichTextFragment>();
        List<RichGlyph> copiedGlyphs = new ArrayList<RichGlyph>(glyphs);
        StringBuilder fragmentText = new StringBuilder();
        RichGlyph fragmentStyle = null;
        int fragmentStartX = 0;
        int fragmentWidth = 0;
        int currentX = 0;
        boolean firstGlyph = true;

        for (RichGlyph glyph : glyphs) {
            if (!firstGlyph) {
                currentX += letterSpacing;
            }

            if (fragmentStyle == null || !fragmentStyle.hasSameStyle(glyph)) {
                if (fragmentStyle != null) {
                    fragments.add(new RichTextFragment(fragmentText.toString(), fragmentStartX,
                            fragmentWidth, fragmentStyle.getFont(), fragmentStyle.getColor(),
                            fragmentStyle.getBackgroundColor(), fragmentStyle.getShadow(),
                            fragmentStyle.getStroke(), fragmentStyle.getBaselineShift(),
                            fragmentStyle.isUnderline(), fragmentStyle.isStrikeThrough()));
                }
                fragmentStyle = glyph;
                fragmentText = new StringBuilder();
                fragmentStartX = currentX;
                fragmentWidth = 0;
            }

            textBuilder.append(glyph.getText());
            fragmentText.append(glyph.getText());
            fragmentWidth = currentX + glyph.getWidth() - fragmentStartX;
            currentX += glyph.getWidth();
            firstGlyph = false;
        }

        if (fragmentStyle != null) {
            fragments.add(new RichTextFragment(fragmentText.toString(), fragmentStartX,
                    fragmentWidth, fragmentStyle.getFont(), fragmentStyle.getColor(),
                    fragmentStyle.getBackgroundColor(), fragmentStyle.getShadow(),
                    fragmentStyle.getStroke(), fragmentStyle.getBaselineShift(),
                    fragmentStyle.isUnderline(), fragmentStyle.isStrikeThrough()));
        }
        return new RichLine(textBuilder.toString(), currentX, fragments, copiedGlyphs);
    }
}
