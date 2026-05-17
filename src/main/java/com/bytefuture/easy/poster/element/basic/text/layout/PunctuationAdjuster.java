package com.bytefuture.easy.poster.element.basic.text.layout;

import com.bytefuture.easy.poster.model.PunctuationType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 标点避头尾调整器
 * 在 VerticalTextSplitter 产出原始列后执行，分三步后处理
 *
 * @author biaoy
 * @since 2026/05/11
 */
public class PunctuationAdjuster {

    private final PunctuationClassifier classifier = new PunctuationClassifier();

    /**
     * 对原始列进行避头尾调整
     *
     * @param columns 拆分器产出的原始列
     * @return 调整后的列
     */
    public List<TextColumn> adjust(List<TextColumn> columns) {
        if (columns.isEmpty()) {
            return columns;
        }

        List<MutableColumn> mutableColumns = toMutable(columns);

        // 步骤1：避头调整
        adjustAvoidHead(mutableColumns);

        // 步骤2：避尾调整
        adjustAvoidTail(mutableColumns);

        // 步骤3：括号配对优化（当前版本暂不实现合并，保持挤压状态）
        optimizeBracketPairs(mutableColumns);

        return toImmutable(mutableColumns);
    }

    /** 步骤1：将列首的避头标点挤回上一列末尾 */
    private void adjustAvoidHead(List<MutableColumn> columns) {
        for (int i = 1; i < columns.size(); i++) {
            MutableColumn current = columns.get(i);
            if (current.cells.isEmpty()) {
                continue;
            }

            CharCell firstCell = current.cells.get(0);
            PunctuationType type = classifier.classifyByCodePoint(firstCell.getCharacter().codePointAt(0));

            if (type == PunctuationType.AVOID_HEAD || type == PunctuationType.CLOSE_BRACKET) {
                MutableColumn previous = columns.get(i - 1);
                int targetOffsetY = findLastNonSqueezedOffsetY(previous);
                CharCell squeezed = firstCell.withSqueezed(true).withOffsetY(targetOffsetY);

                previous.cells.add(squeezed);
                current.cells.remove(0);
            }
        }

        rebuildAll(columns);
    }

    /** 步骤2：将列尾的避尾标点挤到下一列开头 */
    private void adjustAvoidTail(List<MutableColumn> columns) {
        for (int i = 0; i < columns.size() - 1; i++) {
            MutableColumn current = columns.get(i);
            if (current.cells.isEmpty()) {
                continue;
            }

            CharCell lastCell = current.cells.get(current.cells.size() - 1);
            PunctuationType type = classifier.classifyByCodePoint(lastCell.getCharacter().codePointAt(0));

            if (type == PunctuationType.AVOID_TAIL || type == PunctuationType.OPEN_BRACKET) {
                MutableColumn next = columns.get(i + 1);
                CharCell squeezed = lastCell.withSqueezed(true).withOffsetY(0);

                next.cells.add(0, squeezed);
                current.cells.remove(current.cells.size() - 1);
            }
        }

        rebuildAll(columns);
    }

    /** 步骤3：括号配对优化（当前版本暂不实现合并） */
    private void optimizeBracketPairs(List<MutableColumn> columns) {
        // 后续版本可根据需要增强此步骤
    }

    /** 找到列中最后一个非 squeezed 字符的 offsetY */
    private int findLastNonSqueezedOffsetY(MutableColumn column) {
        for (int i = column.cells.size() - 1; i >= 0; i--) {
            if (!column.cells.get(i).isSqueezed()) {
                return column.cells.get(i).getOffsetY();
            }
        }
        return 0;
    }

    /** 重建所有列：重新计算 offsetY（从0累加）、text、height */
    private void rebuildAll(List<MutableColumn> columns) {
        for (MutableColumn mc : columns) {
            StringBuilder textBuilder = new StringBuilder();
            int totalHeight = 0;
            int nextOffsetY = 0;
            List<CharCell> recalculatedCells = new ArrayList<>(mc.cells.size());

            for (CharCell cell : mc.cells) {
                textBuilder.append(cell.getCharacter());
                if (cell.isSqueezed()) {
                    // squeezed 字符保持其 offsetY（由 adjuster 设定）
                    recalculatedCells.add(cell);
                } else {
                    // 非 squeezed 字符重新计算 offsetY（从0累加）
                    recalculatedCells.add(cell.withOffsetY(nextOffsetY));
                    nextOffsetY += cell.getHeight();
                    totalHeight += cell.getHeight();
                }
            }

            mc.text = textBuilder.toString();
            mc.height = totalHeight;
            mc.cells = recalculatedCells;
        }
    }

    /** 转为可变副本 */
    private List<MutableColumn> toMutable(List<TextColumn> columns) {
        List<MutableColumn> result = new ArrayList<>(columns.size());
        for (TextColumn col : columns) {
            result.add(new MutableColumn(col));
        }
        return result;
    }

    /** 转为不可变结果 */
    private List<TextColumn> toImmutable(List<MutableColumn> columns) {
        List<TextColumn> result = new ArrayList<>(columns.size());
        for (MutableColumn mc : columns) {
            result.add(new TextColumn(mc.text, mc.width, mc.height, mc.offsetX,
                    Collections.unmodifiableList(new ArrayList<>(mc.cells))));
        }
        return result;
    }

    /** 可变列（用于调整过程中的临时修改） */
    private static final class MutableColumn {
        String text;
        int width;
        int height;
        int offsetX;
        List<CharCell> cells;

        MutableColumn(TextColumn column) {
            this.text = column.getText();
            this.width = column.getWidth();
            this.height = column.getHeight();
            this.offsetX = column.getOffsetX();
            this.cells = new ArrayList<>(column.getCharacters());
        }
    }
}