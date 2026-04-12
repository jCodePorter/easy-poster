package com.bytefuture.easy.poster.text.metrics;

import com.bytefuture.easy.poster.text.layout.TextDecorationInsets;
import com.bytefuture.easy.poster.text.layout.TextRenderSpec;
import com.bytefuture.easy.poster.text.wrap.RichLine;
import com.bytefuture.easy.poster.text.wrap.RichTextFragment;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.util.List;

public final class DecorationMetricsResolver {

    public TextDecorationInsets resolveTextInsets(TextRenderSpec spec, Graphics2D graphics,
                                                  FontMetrics fontMetrics, int lineHeight,
                                                  int baselineOffset) {
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;

        if (spec.getStroke() != null) {
            // 描边会向四周扩展半个线宽。
            int strokeInset = (int) Math.ceil(spec.getStroke().getWidth() / 2.0d);
            left = Math.max(left, strokeInset);
            right = Math.max(right, strokeInset);
            top = Math.max(top, strokeInset);
            bottom = Math.max(bottom, strokeInset);
        }

        if (spec.getShadow() != null) {
            // 阴影偏移可能把可见区域推到文本外部任一方向。
            left = Math.max(left, Math.max(0, -spec.getShadow().getOffsetX()));
            right = Math.max(right, Math.max(0, spec.getShadow().getOffsetX()));
            top = Math.max(top, Math.max(0, -spec.getShadow().getOffsetY()));
            bottom = Math.max(bottom, Math.max(0, spec.getShadow().getOffsetY()));
        }

        LineMetrics lineMetrics = fontMetrics.getLineMetrics(resolveMetricsSampleText(spec.getText()), graphics);
        if (spec.isUnderline()) {
            // 下划线基于 baseline 绘制，需要计算其是否超出行框上下边界。
            top = Math.max(top, resolveDecorationTopOverflow(lineMetrics.getUnderlineOffset(),
                    lineMetrics.getUnderlineThickness(), baselineOffset));
            bottom = Math.max(bottom, resolveDecorationBottomOverflow(lineMetrics.getUnderlineOffset(),
                    lineMetrics.getUnderlineThickness(), baselineOffset, lineHeight));
        }
        if (spec.isStrikeThrough()) {
            // 删除线同样可能超出标准行框。
            top = Math.max(top, resolveDecorationTopOverflow(lineMetrics.getStrikethroughOffset(),
                    lineMetrics.getStrikethroughThickness(), baselineOffset));
            bottom = Math.max(bottom, resolveDecorationBottomOverflow(lineMetrics.getStrikethroughOffset(),
                    lineMetrics.getStrikethroughThickness(), baselineOffset, lineHeight));
        }

        return new TextDecorationInsets(left, top, right, bottom);
    }

    public TextDecorationInsets resolveRichTextInsets(TextRenderSpec spec, Graphics2D graphics,
                                                      Font baseFont, int lineHeight,
                                                      int baselineOffset,
                                                      List<RichLine> richLines) {
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;

        if (spec.getStroke() != null) {
            int strokeInset = (int) Math.ceil(spec.getStroke().getWidth() / 2.0d);
            left = Math.max(left, strokeInset);
            right = Math.max(right, strokeInset);
            top = Math.max(top, strokeInset);
            bottom = Math.max(bottom, strokeInset);
        }

        if (spec.getShadow() != null) {
            left = Math.max(left, Math.max(0, -spec.getShadow().getOffsetX()));
            right = Math.max(right, Math.max(0, spec.getShadow().getOffsetX()));
            top = Math.max(top, Math.max(0, -spec.getShadow().getOffsetY()));
            bottom = Math.max(bottom, Math.max(0, spec.getShadow().getOffsetY()));
        }

        boolean hasUnderlineDecoration = false;
        boolean hasStrikeThroughDecoration = false;
        for (RichLine richLine : richLines) {
            for (RichTextFragment fragment : richLine.getFragments()) {
                hasUnderlineDecoration = hasUnderlineDecoration || fragment.isUnderline();
                hasStrikeThroughDecoration = hasStrikeThroughDecoration || fragment.isStrikeThrough();
            }
        }

        if (!hasUnderlineDecoration && !hasStrikeThroughDecoration) {
            // 富文本没有任何装饰线时，无需再计算 lineMetrics。
            return new TextDecorationInsets(left, top, right, bottom);
        }

        LineMetrics lineMetrics = graphics.getFontMetrics(baseFont)
                .getLineMetrics(resolveMetricsSampleText(resolveRichMetricsSampleText(richLines)), graphics);
        if (hasUnderlineDecoration) {
            top = Math.max(top, resolveDecorationTopOverflow(lineMetrics.getUnderlineOffset(),
                    lineMetrics.getUnderlineThickness(), baselineOffset));
            bottom = Math.max(bottom, resolveDecorationBottomOverflow(lineMetrics.getUnderlineOffset(),
                    lineMetrics.getUnderlineThickness(), baselineOffset, lineHeight));
        }
        if (hasStrikeThroughDecoration) {
            top = Math.max(top, resolveDecorationTopOverflow(lineMetrics.getStrikethroughOffset(),
                    lineMetrics.getStrikethroughThickness(), baselineOffset));
            bottom = Math.max(bottom, resolveDecorationBottomOverflow(lineMetrics.getStrikethroughOffset(),
                    lineMetrics.getStrikethroughThickness(), baselineOffset, lineHeight));
        }
        return new TextDecorationInsets(left, top, right, bottom);
    }

    private int resolveDecorationTopOverflow(float decorationOffset, float thickness, int baselineOffset) {
        int minY = (int) Math.floor(baselineOffset + decorationOffset - thickness / 2.0f);
        return Math.max(0, -minY);
    }

    private int resolveDecorationBottomOverflow(float decorationOffset, float thickness,
                                                int baselineOffset, int lineHeight) {
        int maxY = (int) Math.ceil(baselineOffset + decorationOffset + thickness / 2.0f);
        return Math.max(0, maxY - lineHeight);
    }

    private String resolveMetricsSampleText(String content) {
        if (content == null || content.isEmpty()) {
            return "Ag";
        }
        return content;
    }

    private String resolveRichMetricsSampleText(List<RichLine> lines) {
        for (RichLine line : lines) {
            if (!line.getText().isEmpty()) {
                return line.getText();
            }
        }
        return "Ag";
    }
}
