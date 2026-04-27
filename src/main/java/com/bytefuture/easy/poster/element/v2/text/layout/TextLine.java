package com.bytefuture.easy.poster.element.v2.text.layout;

import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextStyle;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 单行文本布局结果。
 *
 * @author biaoy
 * @since 2026/04/26
 */
@Getter
public class TextLine {
    /**
     * 行文本内容
     */
    private final String text;

    /**
     * 行宽度
     */
    private final int width;

    /**
     * 相对布局起点的 X 轴偏移
     */
    private final int offsetX;

    /**
     * 行内已定位文本片段
     */
    private final List<Segment> segments;

    /**
     * 创建文本行。
     *
     * @param text     行文本内容
     * @param width    行宽度
     * @param offsetX  行整体偏移
     * @param segments 行内已定位片段
     */
    public TextLine(String text, int width, int offsetX, List<Segment> segments) {
        this.text = text;
        this.width = width;
        this.offsetX = offsetX;
        this.segments = Collections.unmodifiableList(new ArrayList<Segment>(segments));
    }

    /**
     * 返回仅修改整体偏移和片段坐标的新文本行。
     *
     * @param offsetX  新的整体偏移
     * @param segments 新的片段布局
     * @return 新文本行
     */
    public TextLine withLayout(int offsetX, List<Segment> segments) {
        return new TextLine(this.text, this.width, offsetX, segments);
    }

    /**
     * 创建空文本行。
     *
     * @return 空文本行
     */
    public static TextLine empty() {
        return new TextLine("", 0, 0, Collections.<Segment>emptyList());
    }

    /**
     * 行内已定位文本片段
     *
     * @author biaoy
     * @since 2026/04/26
     */
    @Getter
    public static final class Segment {
        /**
         * 片段文本
         */
        private final String text;

        /**
         * 片段最终样式
         */
        private final ResolvedTextStyle style;

        /**
         * 相对当前行起点的 X 轴偏移
         */
        private final int offsetX;

        /**
         * 片段宽度
         */
        private final int width;

        /**
         * 是否可作为分散对齐的拉伸空隙
         */
        private final boolean stretchableSpace;

        /**
         * 创建行内片段。
         *
         * @param text             片段文本
         * @param style            片段样式
         * @param offsetX          片段相对 X 偏移
         * @param width            片段宽度
         * @param stretchableSpace 是否可拉伸
         */
        public Segment(String text, ResolvedTextStyle style, int offsetX, int width, boolean stretchableSpace) {
            this.text = text;
            this.style = style;
            this.offsetX = offsetX;
            this.width = width;
            this.stretchableSpace = stretchableSpace;
        }

        /**
         * 返回一个仅修改偏移量的新片段
         *
         * @param offsetX 新偏移
         * @return 新片段
         */
        public Segment withOffsetX(int offsetX) {
            return new Segment(this.text, this.style, offsetX, this.width, this.stretchableSpace);
        }
    }
}
