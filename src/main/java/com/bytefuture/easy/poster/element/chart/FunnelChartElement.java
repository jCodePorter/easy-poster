package com.bytefuture.easy.poster.element.chart;

import com.bytefuture.easy.poster.element.AbstractDimensionElement;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 漏斗图元素。
 * <p>
 * 用于在海报中绘制漏斗图，展示阶段性的数据递减过程，
 * 支持自定义颜色、标签、图例和标题配置。
 * </p>
 *
 * @author biaoy
 * @since 2026/04/13
 */
public class FunnelChartElement extends AbstractDimensionElement<FunnelChartElement> {

    /**
     * 默认调色板。
     */
    private static final List<Color> DEFAULT_PALETTE = Arrays.asList(
            new Color(72, 133, 237),
            new Color(234, 67, 53),
            new Color(52, 168, 83),
            new Color(251, 188, 5),
            new Color(123, 97, 255),
            new Color(0, 172, 193)
    );

    /**
     * 阶段集合。
     */
    private final List<FunnelChartStage> stages = new ArrayList<FunnelChartStage>();

    /**
     * 数值格式化器。
     */
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");

    /**
     * 图表内边距。
     */
    private Insets padding = new Insets(24, 24, 24, 24);

    /**
     * 图表背景色。
     */
    private Color backgroundColor;

    /**
     * 标题、标签、图例文字颜色。
     */
    private Color labelColor = new Color(71, 77, 92);

    /**
     * 图表标题。
     */
    private String title;

    /**
     * 是否显示图例。
     */
    private boolean showLegend = true;

    /**
     * 是否显示阶段标签。
     */
    private boolean showLabel = true;

    /**
     * 是否显示标题。
     */
    private boolean showTitle = true;

    /**
     * 图例内容展示模式。
     */
    private DisplayMode legendDisplayMode = DisplayMode.NAME_VALUE;

    /**
     * 阶段标签内容展示模式。
     */
    private DisplayMode labelDisplayMode = DisplayMode.NAME_PERCENT;

    /**
     * 自定义调色板。
     */
    private List<Color> palette = new ArrayList<Color>(DEFAULT_PALETTE);

    /**
     * 标题字号。
     */
    private int titleFontSize = 18;

    /**
     * 图例字号。
     */
    private int legendFontSize = 12;

    /**
     * 标签字号。
     */
    private int labelFontSize = 12;

    /**
     * 图例项之间的间距。
     */
    private int legendItemGap = 18;

    /**
     * 图例色块尺寸。
     */
    private int legendMarkerSize = 10;

    /**
     * 标签最小高度阈值，低于此值时标签将绘制在外部。
     */
    private int minLabelHeight = 18;

    /**
     * 外部标签与阶段之间的间距。
     */
    private int externalLabelGap = 4;

    /**
     * 阶段之间的间距。
     */
    private int stageGap = 8;

    /**
     * 构造图表元素。
     *
     * @param width  元素宽度
     * @param height 元素高度
     */
    public FunnelChartElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 设置图表标题。
     *
     * @param title 图表标题
     * @return 当前元素
     */
    public FunnelChartElement setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置图表内边距。
     *
     * @param padding 图表内边距
     * @return 当前元素
     */
    public FunnelChartElement setPadding(Insets padding) {
        if (padding == null) {
            throw new PosterException("padding can not be null");
        }
        this.padding = padding;
        return this;
    }

    /**
     * 设置图表背景色。
     *
     * @param backgroundColor 背景色
     * @return 当前元素
     */
    public FunnelChartElement setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * 设置标签颜色。
     *
     * @param labelColor 标签颜色
     * @return 当前元素
     */
    public FunnelChartElement setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
        return this;
    }

    /**
     * 设置是否显示图例。
     *
     * @param showLegend 是否显示图例
     * @return 当前元素
     */
    public FunnelChartElement setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
        return this;
    }

    /**
     * 设置是否显示标签。
     *
     * @param showLabel 是否显示标签
     * @return 当前元素
     */
    public FunnelChartElement setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
        return this;
    }

    /**
     * 设置是否显示标题。
     *
     * @param showTitle 是否显示标题
     * @return 当前元素
     */
    public FunnelChartElement setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    /**
     * 设置图例展示模式。
     *
     * @param legendDisplayMode 图例展示模式
     * @return 当前元素
     */
    public FunnelChartElement setLegendDisplayMode(DisplayMode legendDisplayMode) {
        if (legendDisplayMode == null) {
            throw new PosterException("legendDisplayMode can not be null");
        }
        this.legendDisplayMode = legendDisplayMode;
        return this;
    }

    /**
     * 设置标签展示模式。
     *
     * @param labelDisplayMode 标签展示模式
     * @return 当前元素
     */
    public FunnelChartElement setLabelDisplayMode(DisplayMode labelDisplayMode) {
        if (labelDisplayMode == null) {
            throw new PosterException("labelDisplayMode can not be null");
        }
        this.labelDisplayMode = labelDisplayMode;
        return this;
    }

    /**
     * 设置标题字号。
     *
     * @param titleFontSize 标题字号
     * @return 当前元素
     */
    public FunnelChartElement setTitleFontSize(int titleFontSize) {
        if (titleFontSize <= 0) {
            throw new PosterException("titleFontSize must be greater than 0");
        }
        this.titleFontSize = titleFontSize;
        return this;
    }

    /**
     * 设置图例字号。
     *
     * @param legendFontSize 图例字号
     * @return 当前元素
     */
    public FunnelChartElement setLegendFontSize(int legendFontSize) {
        if (legendFontSize <= 0) {
            throw new PosterException("legendFontSize must be greater than 0");
        }
        this.legendFontSize = legendFontSize;
        return this;
    }

    /**
     * 设置标签字号。
     *
     * @param labelFontSize 标签字号
     * @return 当前元素
     */
    public FunnelChartElement setLabelFontSize(int labelFontSize) {
        if (labelFontSize <= 0) {
            throw new PosterException("labelFontSize must be greater than 0");
        }
        this.labelFontSize = labelFontSize;
        return this;
    }

    /**
     * 设置图例项间距。
     *
     * @param legendItemGap 图例项间距
     * @return 当前元素
     */
    public FunnelChartElement setLegendItemGap(int legendItemGap) {
        if (legendItemGap < 0) {
            throw new PosterException("legendItemGap must be greater than or equal to 0");
        }
        this.legendItemGap = legendItemGap;
        return this;
    }

    /**
     * 设置图例色块尺寸。
     *
     * @param legendMarkerSize 图例色块尺寸
     * @return 当前元素
     */
    public FunnelChartElement setLegendMarkerSize(int legendMarkerSize) {
        if (legendMarkerSize <= 0) {
            throw new PosterException("legendMarkerSize must be greater than 0");
        }
        this.legendMarkerSize = legendMarkerSize;
        return this;
    }

    /**
     * 设置调色板。
     *
     * @param palette 调色板
     * @return 当前元素
     */
    public FunnelChartElement setPalette(List<Color> palette) {
        if (palette == null || palette.isEmpty()) {
            throw new PosterException("palette can not be empty");
        }
        this.palette = new ArrayList<Color>(palette);
        return this;
    }

    /**
     * 设置阶段集合。
     *
     * @param stages 阶段集合
     * @return 当前元素
     */
    public FunnelChartElement setStages(List<FunnelChartStage> stages) {
        this.stages.clear();
        if (stages != null) {
            this.stages.addAll(stages);
        }
        return this;
    }

    /**
     * 添加阶段。
     *
     * @param stage 阶段对象
     * @return 当前元素
     */
    public FunnelChartElement addStage(FunnelChartStage stage) {
        if (stage == null) {
            throw new PosterException("stage can not be null");
        }
        this.stages.add(stage);
        return this;
    }

    /**
     * 添加阶段。
     *
     * @param name  阶段名称
     * @param value 阶段数值
     * @return 当前元素
     */
    public FunnelChartElement addStage(String name, Number value) {
        return addStage(FunnelChartStage.of(name, value));
    }

    /**
     * 添加带颜色的阶段。
     *
     * @param name  阶段名称
     * @param value 阶段数值
     * @param color 阶段颜色
     * @return 当前元素
     */
    public FunnelChartElement addStage(String name, Number value, Color color) {
        return addStage(FunnelChartStage.of(name, value, color));
    }

    /**
     * 执行图表绘制。
     *
     * @param context      海报上下文
     * @param dimension    当前元素尺寸
     * @param posterWidth  画布宽度
     * @param posterHeight 画布高度
     * @return 元素左上角坐标
     */
    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        validateConfig();
        List<StageRenderInfo> renderStages = resolveRenderStages();
        Graphics2D graphics = context.getGraphics();
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            Point origin = dimension.getPoint();
            if (backgroundColor != null) {
                g.setColor(backgroundColor);
                g.fillRect(origin.getX(), origin.getY(), width, height);
            }

            Font baseFont = Optional.ofNullable(context.getConfig().getFont()).orElse(
                    new Font(context.getConfig().getFontName(), context.getConfig().getFontStyle(), context.getConfig().getFontSize())
            );
            Font titleFont = baseFont.deriveFont(Font.BOLD, (float) titleFontSize);
            Font legendFont = baseFont.deriveFont(Font.PLAIN, (float) legendFontSize);
            Font labelFont = baseFont.deriveFont(Font.PLAIN, (float) labelFontSize);

            LayoutBox innerBox = resolveInnerBox(origin);
            if (showTitle) {
                innerBox.top += drawTitle(g, innerBox, titleFont);
            }
            if (showLegend) {
                innerBox.top += drawLegend(g, innerBox, legendFont, renderStages);
            }
            drawStages(g, innerBox, renderStages, labelFont);
            return origin;
        } finally {
            g.dispose();
        }
    }

    /**
     * 校验配置。
     */
    private void validateConfig() {
        if (width <= 0 || height <= 0) {
            throw new PosterException("funnel chart width and height must be greater than 0");
        }
        if (palette == null || palette.isEmpty()) {
            throw new PosterException("palette can not be empty");
        }
        if (showLegend && legendDisplayMode == null) {
            throw new PosterException("legendDisplayMode can not be null");
        }
        if (showLabel && labelDisplayMode == null) {
            throw new PosterException("labelDisplayMode can not be null");
        }
        if (stages.isEmpty()) {
            throw new PosterException("stages can not be empty");
        }
        for (FunnelChartStage stage : stages) {
            if (stage == null) {
                throw new PosterException("stage can not be null");
            }
            if (stage.getValue() <= 0D) {
                throw new PosterException("funnel chart requires all stage values to be positive. Invalid stage: " + stage.getName());
            }
        }
    }

    /**
     * 解析可渲染阶段。
     */
    private List<StageRenderInfo> resolveRenderStages() {
        List<StageRenderInfo> renderStages = new ArrayList<StageRenderInfo>();
        double total = 0D;
        double maxValue = 0D;
        int colorIndex = 0;

        for (FunnelChartStage stage : stages) {
            Color resolvedColor = resolveStageColor(stage, colorIndex);
            renderStages.add(new StageRenderInfo(stage, resolvedColor));
            total += stage.getValue();
            maxValue = Math.max(maxValue, stage.getValue());
            colorIndex++;
        }

        if (total <= 0D) {
            throw new PosterException("funnel chart requires at least one positive stage value");
        }

        for (StageRenderInfo renderStage : renderStages) {
            renderStage.percent = renderStage.stage.getValue() / total * 100D;
            renderStage.maxValue = maxValue;
        }
        return renderStages;
    }

    /**
     * 解析阶段颜色。
     */
    private Color resolveStageColor(FunnelChartStage stage, int colorIndex) {
        return Optional.ofNullable(stage.getColor()).orElse(palette.get(colorIndex % palette.size()));
    }

    /**
     * 解析内部可用绘制区域。
     */
    private LayoutBox resolveInnerBox(Point origin) {
        return new LayoutBox(
                origin.getX() + padding.left,
                origin.getY() + padding.top,
                origin.getX() + width - padding.right,
                origin.getY() + height - padding.bottom
        );
    }

    /**
     * 绘制标题。
     */
    private int drawTitle(Graphics2D g, LayoutBox innerBox, Font titleFont) {
        if (title == null || title.trim().isEmpty()) {
            return 0;
        }
        g.setFont(titleFont);
        g.setColor(labelColor);
        FontMetrics metrics = g.getFontMetrics();
        String displayTitle = title.trim();
        int textWidth = metrics.stringWidth(displayTitle);
        int availableWidth = Math.max(1, innerBox.width());
        int drawX = innerBox.left + Math.max(0, (availableWidth - textWidth) / 2);
        int baseline = innerBox.top + metrics.getAscent();
        g.drawString(displayTitle, drawX, baseline);
        return metrics.getHeight() + 8;
    }

    /**
     * 绘制图例。
     */
    private int drawLegend(Graphics2D g, LayoutBox innerBox, Font legendFont, List<StageRenderInfo> renderStages) {
        g.setFont(legendFont);
        g.setColor(labelColor);
        FontMetrics metrics = g.getFontMetrics();
        int rowHeight = Math.max(metrics.getHeight(), legendMarkerSize) + 6;
        int cursorX = innerBox.left;
        int cursorY = innerBox.top;
        int rows = 1;
        for (StageRenderInfo stageInfo : renderStages) {
            String text = formatDisplayText(stageInfo, legendDisplayMode);
            int itemWidth = legendMarkerSize + 6 + metrics.stringWidth(text) + legendItemGap;
            if (cursorX > innerBox.left && cursorX + itemWidth > innerBox.right) {
                rows++;
                cursorX = innerBox.left;
                cursorY += rowHeight;
            }
            int baseline = cursorY + metrics.getAscent();
            int markerY = baseline - metrics.getAscent() + Math.max(0, (metrics.getHeight() - legendMarkerSize) / 2);
            g.setColor(stageInfo.color);
            g.fillRoundRect(cursorX, markerY, legendMarkerSize, legendMarkerSize, 4, 4);
            g.setColor(labelColor);
            g.drawString(text, cursorX + legendMarkerSize + 6, baseline);
            cursorX += itemWidth;
        }
        return rows * rowHeight;
    }

    /**
     * 绘制阶段区域。
     */
    private void drawStages(Graphics2D g, LayoutBox innerBox, List<StageRenderInfo> renderStages, Font labelFont) {
        int stageCount = renderStages.size();
        if (stageCount == 0) return;

        int availableHeight = innerBox.height() - (stageCount - 1) * stageGap;
        int stageHeight = Math.max(1, availableHeight / stageCount);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int plotWidth = innerBox.width();
        int plotLeft = innerBox.left;
        int currentTop = innerBox.top;

        for (int i = 0; i < stageCount; i++) {
            StageRenderInfo stageInfo = renderStages.get(i);
            double widthRatio = stageInfo.stage.getValue() / stageInfo.maxValue;
            int stageWidth = (int) Math.round(plotWidth * widthRatio);
            int stageLeft = plotLeft + (plotWidth - stageWidth) / 2;

            // Draw trapezoid shape
            Path2D path = createTrapezoidPath(stageLeft, currentTop, stageWidth, stageHeight);
            g.setColor(stageInfo.color);
            g.fill(path);

            // Draw label
            if (showLabel) {
                drawStageLabel(g, labelFont, stageInfo, stageLeft, currentTop, stageWidth, stageHeight);
            }

            currentTop += stageHeight + stageGap;
        }
    }

    /**
     * 创建梯形路径。
     */
    private Path2D createTrapezoidPath(int left, int top, int width, int height) {
        Path2D path = new Path2D.Double();
        int inset = Math.min(10, height / 4);
        path.moveTo(left + inset, top);
        path.lineTo(left + width - inset, top);
        path.lineTo(left + width - 2 * inset, top + height);
        path.lineTo(left + 2 * inset, top + height);
        path.closePath();
        return path;
    }

    /**
     * 绘制阶段标签。
     */
    private void drawStageLabel(Graphics2D g, Font labelFont, StageRenderInfo stageInfo,
                                int stageLeft, int stageTop, int stageWidth, int stageHeight) {
        String text = formatDisplayText(stageInfo, labelDisplayMode);
        if (text == null || text.isEmpty()) {
            return;
        }

        g.setFont(labelFont);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();

        // Choose readable label color based on stage color brightness
        Color labelColor = chooseReadableLabelColor(stageInfo.color);

        if (stageHeight >= Math.max(minLabelHeight, textHeight + 4)) {
            // Draw inside the stage
            int labelX = stageLeft + (stageWidth - textWidth) / 2;
            int labelY = stageTop + (stageHeight - textHeight) / 2 + metrics.getAscent();
            g.setColor(labelColor);
            g.drawString(text, labelX, labelY);
        } else {
            // Draw outside the stage with leader line
            drawExternalLabel(g, labelFont, text, stageLeft, stageTop, stageWidth, stageHeight, labelColor);
        }
    }

    /**
     * 绘制外部标签。
     */
    private void drawExternalLabel(Graphics2D g, Font font, String text,
                                   int stageLeft, int stageTop, int stageWidth, int stageHeight, Color labelColor) {
        g.setFont(font);
        g.setColor(labelColor);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();

        // Position label to the right of the stage
        int labelX = stageLeft + stageWidth + externalLabelGap;
        int labelY = stageTop + (stageHeight - textHeight) / 2 + metrics.getAscent();

        // Draw leader line
        g.setColor(Color.GRAY);
        g.draw(new Line2D.Double(
                stageLeft + stageWidth, stageTop + stageHeight / 2.0,
                labelX - 2, stageTop + stageHeight / 2.0
        ));

        // Draw label
        g.setColor(labelColor);
        g.drawString(text, labelX, labelY);
    }

    /**
     * 选择易读的标签颜色。
     */
    private Color chooseReadableLabelColor(Color background) {
        if (background == null) {
            return Color.WHITE;
        }
        int brightness = (background.getRed() * 299 + background.getGreen() * 587 + background.getBlue() * 114) / 1000;
        return brightness < 150 ? Color.WHITE : new Color(33, 37, 41);
    }

    /**
     * 格式化显示文本。
     */
    private String formatDisplayText(StageRenderInfo stageInfo, DisplayMode displayMode) {
        String name = Optional.ofNullable(stageInfo.stage.getName()).orElse("");
        String value = decimalFormat.format(stageInfo.stage.getValue());
        String percent = decimalFormat.format(stageInfo.percent) + "%";
        if (displayMode == DisplayMode.NAME) {
            return name;
        }
        if (displayMode == DisplayMode.VALUE) {
            return value;
        }
        if (displayMode == DisplayMode.PERCENT) {
            return percent;
        }
        if (displayMode == DisplayMode.NAME_VALUE) {
            return name + "(" + value + ")";
        }
        if (displayMode == DisplayMode.NAME_PERCENT) {
            return name + "(" + percent + ")";
        }
        return name;
    }

    /**
     * 获取阶段集合。
     */
    public List<FunnelChartStage> getStages() {
        return Collections.unmodifiableList(stages);
    }

    /**
     * 获取当前调色板。
     */
    public List<Color> getPalette() {
        return Collections.unmodifiableList(palette);
    }

    /**
     * 内容展示模式。
     */
    public enum DisplayMode {
        NAME("name"),
        VALUE("value"),
        PERCENT("percent"),
        NAME_VALUE("name+value"),
        NAME_PERCENT("name+percent");

        private final String desc;

        DisplayMode(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 内部布局矩形。
     */
    private static class LayoutBox {

        private final int left;
        private int top;
        private final int right;
        private final int bottom;

        private LayoutBox(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        private int width() {
            return Math.max(0, right - left);
        }

        private int height() {
            return Math.max(0, bottom - top);
        }
    }

    /**
     * 阶段绘制信息。
     */
    private static class StageRenderInfo {

        private final FunnelChartStage stage;
        private final Color color;
        private double percent;
        private double maxValue;

        private StageRenderInfo(FunnelChartStage stage, Color color) {
            this.stage = stage;
            this.color = color;
        }
    }
}
