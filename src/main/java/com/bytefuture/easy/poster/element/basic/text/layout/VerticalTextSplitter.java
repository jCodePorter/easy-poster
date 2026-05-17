package com.bytefuture.easy.poster.element.basic.text.layout;

import com.bytefuture.easy.poster.element.basic.text.style.ResolvedTextSpan;
import com.bytefuture.easy.poster.element.basic.text.style.ResolvedTextStyle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 竖排文本拆分器
 * 将输入的 ResolvedTextSpan 逐字符拆分为 TextColumn
 *
 * @author biaoy
 * @since 2026/05/07
 */
public class VerticalTextSplitter {

    private final TextMeasurer measurer = new TextMeasurer();

    /**
     * 根据列高限制将文本逐字符拆分为多列
     *
     * @param graphics      图形上下文
     * @param resolvedSpans 已解析样式的文本片段
     * @param heightLimit   单列最大高度；null 或 <= 0 表示不限制
     * @return 按顺序生成的文本列
     */
    public List<TextColumn> splitColumns(Graphics2D graphics, List<ResolvedTextSpan> resolvedSpans, Integer heightLimit) {
        int heightConstraint = (heightLimit != null && heightLimit > 0) ? heightLimit : 0;

        List<CharCellBuilder> currentCells = new ArrayList<>();
        int currentHeight = 0;
        List<TextColumn> columns = new ArrayList<>();

        for (ResolvedTextSpan span : resolvedSpans) {
            String text = span.getText();
            ResolvedTextStyle style = span.getStyle();

            for (int i = 0; i < text.length(); ) {
                int codePoint = text.codePointAt(i);
                String ch = new String(Character.toChars(codePoint));
                i += Character.charCount(codePoint);

                // CRLF 场景下忽略 \r，只保留 \n
                if ("\r".equals(ch)) {
                    continue;
                }
                // 换行符触发换列
                if ("\n".equals(ch)) {
                    columns.add(buildColumn(currentCells, currentHeight));
                    currentCells = new ArrayList<>();
                    currentHeight = 0;
                    continue;
                }

                // 省略号单元识别：连续两个 U+2026 组合为一个单元
                if (codePoint == 0x2026 && i < text.length() && text.codePointAt(i) == 0x2026) {
                    i += Character.charCount(0x2026);
                    int ellipsisWidth = measurer.measureWidth(graphics, "……", style.getFont());
                    int singleCharHeight = measurer.getFontHeight(graphics, style.getFont());
                    int ellipsisHeight = singleCharHeight * 2;

                    if (heightConstraint > 0 && currentHeight + ellipsisHeight > heightConstraint && !currentCells.isEmpty()) {
                        columns.add(buildColumn(currentCells, currentHeight));
                        currentCells = new ArrayList<>();
                        currentHeight = 0;
                    }

                    currentCells.add(new CharCellBuilder("……", style, currentHeight, ellipsisWidth, ellipsisHeight, true));
                    currentHeight += ellipsisHeight;
                    continue;
                }

                int charWidth = measurer.measureWidth(graphics, ch, style.getFont());
                int charHeight = measurer.getFontHeight(graphics, style.getFont());

                // 列高约束：超过限制时当前列封口
                if (heightConstraint > 0 && currentHeight + charHeight > heightConstraint && !currentCells.isEmpty()) {
                    columns.add(buildColumn(currentCells, currentHeight));
                    currentCells = new ArrayList<>();
                    currentHeight = 0;
                }

                currentCells.add(new CharCellBuilder(ch, style, currentHeight, charWidth, charHeight));
                currentHeight += charHeight;
            }
        }

        // 尾列
        if (!currentCells.isEmpty() || columns.isEmpty()) {
            columns.add(buildColumn(currentCells, currentHeight));
        }

        return columns;
    }

    /** 将 CharCellBuilder 列表组装为 TextColumn */
    private TextColumn buildColumn(List<CharCellBuilder> builders, int totalHeight) {
        if (builders.isEmpty()) {
            return TextColumn.empty();
        }
        StringBuilder textBuilder = new StringBuilder();
        List<CharCell> cells = new ArrayList<>(builders.size());
        for (CharCellBuilder b : builders) {
            textBuilder.append(b.character);
            cells.add(new CharCell(b.character, b.style, b.offsetY, b.width, b.height, false, false, b.ellipsisUnit));
        }
        return new TextColumn(textBuilder.toString(), 0, totalHeight, 0, cells);
    }

    /** 构建期间的临时 CharCell 数据 */
    private static final class CharCellBuilder {
        final String character;
        final ResolvedTextStyle style;
        final int offsetY;
        final int width;
        final int height;
        final boolean ellipsisUnit;

        CharCellBuilder(String character, ResolvedTextStyle style, int offsetY, int width, int height) {
            this(character, style, offsetY, width, height, false);
        }

        CharCellBuilder(String character, ResolvedTextStyle style, int offsetY, int width, int height, boolean ellipsisUnit) {
            this.character = character;
            this.style = style;
            this.offsetY = offsetY;
            this.width = width;
            this.height = height;
            this.ellipsisUnit = ellipsisUnit;
        }
    }
}