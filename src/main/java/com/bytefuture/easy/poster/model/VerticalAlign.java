package com.bytefuture.easy.poster.model;

/**
 * 竖排列内对齐枚举
 *
 * @author biaoy
 * @since 2026/05/07
 */
public enum VerticalAlign {
    /** 列内顶部对齐（默认） */
    TOP {
        @Override
        public int offset(int columnHeight, int contentHeight) {
            return 0;
        }
    },
    /** 列内居中对齐 */
    CENTER {
        @Override
        public int offset(int columnHeight, int contentHeight) {
            return (columnHeight - contentHeight) / 2;
        }
    },
    /** 列内底部对齐 */
    BOTTOM {
        @Override
        public int offset(int columnHeight, int contentHeight) {
            return columnHeight - contentHeight;
        }
    };

    /**
     * 计算列内内容相对列起点的 Y 偏移
     *
     * @param columnHeight   列可用高度
     * @param contentHeight  列内容实际高度
     * @return Y 偏移量
     */
    public abstract int offset(int columnHeight, int contentHeight);
}