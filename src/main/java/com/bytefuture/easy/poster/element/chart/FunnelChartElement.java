package com.bytefuture.easy.poster.element.chart;

import com.bytefuture.easy.poster.element.chart.base.AbstractChartElement;
import com.bytefuture.easy.poster.element.chart.base.ChartLayoutBox;
import com.bytefuture.easy.poster.element.chart.base.ChartLegendRenderer;
import com.bytefuture.easy.poster.element.chart.base.NamedColorValue;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.model.PosterContext;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

/**
 * 漏斗图元素，用于展示分阶段转化数据。
 */
public class FunnelChartElement extends AbstractChartElement<FunnelChartElement> {

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
     * 原始阶段列表。
     */
    private final List<FunnelChartStage> stages = new ArrayList<FunnelChartStage>();

    /**
     * 数值格式化器。
     */
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");
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
     * 图例文本展示模式。
     */
    private DisplayMode legendDisplayMode = DisplayMode.NAME_VALUE;
    /**
     * 阶段标签展示模式。
     */
    private DisplayMode labelDisplayMode = DisplayMode.NAME_PERCENT;
    /**
     * 当前实例使用的调色板。
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
     * 阶段标签绘制在内部所需的最小高度。
     */
    private int minLabelHeight = 18;
    /**
     * 外置标签与漏斗块之间的间距。
     */
    private int externalLabelGap = 4;
    /**
     * 各阶段之间的垂直间距。
     */
    private int stageGap = 8;

    /**
     * 创建指定宽高的漏斗图元素。
     */
    public FunnelChartElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 设置图表标题。
     */
    public FunnelChartElement setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置图表内边距。
     */
    public FunnelChartElement setPadding(Insets padding) {
        if (padding == null) {
            throw new PosterException("padding can not be null");
        }
        setPaddingInternal(padding);
        return this;
    }

    /**
     * 设置图表背景色。
     */
    public FunnelChartElement setBackgroundColor(Color backgroundColor) {
        setBackgroundColorInternal(backgroundColor);
        return this;
    }

    /**
     * 设置标题、图例和标签共用的文字颜色。
     */
    public FunnelChartElement setLabelColor(Color labelColor) {
        setLabelColorInternal(labelColor);
        return this;
    }

    /**
     * 设置是否显示图例。
     */
    public FunnelChartElement setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
        return this;
    }

    /**
     * 设置是否显示阶段标签。
     */
    public FunnelChartElement setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
        return this;
    }

    /**
     * 设置是否显示标题。
     */
    public FunnelChartElement setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    /**
     * 设置图例文本展示模式。
     */
    public FunnelChartElement setLegendDisplayMode(DisplayMode legendDisplayMode) {
        if (legendDisplayMode == null) {
            throw new PosterException("legendDisplayMode can not be null");
        }
        this.legendDisplayMode = legendDisplayMode;
        return this;
    }

    /**
     * 设置阶段标签展示模式。
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
     */
    public FunnelChartElement setLabelFontSize(int labelFontSize) {
        if (labelFontSize <= 0) {
            throw new PosterException("labelFontSize must be greater than 0");
        }
        this.labelFontSize = labelFontSize;
        return this;
    }

    /**
     * 设置图例项之间的水平间距。
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
     */
    public FunnelChartElement setLegendMarkerSize(int legendMarkerSize) {
        if (legendMarkerSize <= 0) {
            throw new PosterException("legendMarkerSize must be greater than 0");
        }
        this.legendMarkerSize = legendMarkerSize;
        return this;
    }

    /**
     * 添加单个阶段对象。
     */
    public FunnelChartElement addStage(FunnelChartStage stage) {
        if (stage == null) {
            throw new PosterException("stage can not be null");
        }
        this.stages.add(stage);
        return this;
    }

    /**
     * 通过名称和值添加阶段。
     */
    public FunnelChartElement addStage(String name, Number value) {
        return addStage(FunnelChartStage.of(name, value));
    }

    /**
     * 通过名称、值和颜色添加阶段。
     */
    public FunnelChartElement addStage(String name, Number value, Color color) {
        return addStage(FunnelChartStage.of(name, value, color));
    }

    /**
     * 按标题、图例、主体三个区域依次完成漏斗图绘制。
     */
    @Override
    protected void renderChart(Graphics2D g, PosterContext context, ChartLayoutBox innerBox) {
        List<StageRenderInfo> renderStages = resolveRenderStages();
        Font baseFont = resolveBaseFont(context);
        Font titleFont = baseFont.deriveFont(Font.BOLD, (float) titleFontSize);
        Font legendFont = baseFont.deriveFont(Font.PLAIN, (float) legendFontSize);
        Font labelFont = baseFont.deriveFont(Font.PLAIN, (float) labelFontSize);

        if (showTitle) {
            innerBox.shiftTop(drawTitle(g, innerBox, titleFont));
        }
        if (showLegend) {
            innerBox.shiftTop(drawLegend(g, innerBox, legendFont, renderStages));
        }
        drawStages(g, innerBox, renderStages, labelFont);
    }

    /**
     * 校验绘图前置条件，确保阶段数据和展示模式合法。
     */
    @Override
    protected void validateChartData() {
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
            // 漏斗图要求每个阶段都可比较，非正值会破坏宽度比例。
            if (stage.getValue() <= 0D) {
                throw new PosterException("funnel chart requires all stage values to be positive. Invalid stage: " + stage.getName());
            }
        }
    }

    /**
     * 预计算阶段渲染所需的颜色、百分比和最大值。
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
     * 解析单个阶段最终使用的颜色。
     */
    private Color resolveStageColor(FunnelChartStage stage, int colorIndex) {
        return Optional.ofNullable(stage.getColor()).orElse(palette.get(colorIndex % palette.size()));
    }

    /**
     * 绘制标题并返回其占用高度。
     */
    private int drawTitle(Graphics2D g, ChartLayoutBox innerBox, Font titleFont) {
        if (title == null || title.trim().isEmpty()) {
            return 0;
        }
        g.setFont(titleFont);
        g.setColor(getLabelColor());
        FontMetrics metrics = g.getFontMetrics();
        String displayTitle = title.trim();
        int textWidth = metrics.stringWidth(displayTitle);
        int availableWidth = Math.max(1, innerBox.width());
        int drawX = innerBox.getLeft() + Math.max(0, (availableWidth - textWidth) / 2);
        int baseline = innerBox.getTop() + metrics.getAscent();
        g.drawString(displayTitle, drawX, baseline);
        return metrics.getHeight() + 8;
    }

    /**
     * 绘制图例并返回其占用高度。
     */
    private int drawLegend(Graphics2D g, ChartLayoutBox innerBox, Font legendFont, List<StageRenderInfo> renderStages) {
        return ChartLegendRenderer.drawLegend(
                g,
                innerBox,
                legendFont,
                toLegendItems(renderStages),
                legendMarkerSize,
                legendItemGap,
                getLabelColor()
        );
    }

    /**
     * 将阶段信息转换成图例组件需要的数据结构。
     */
    private List<NamedColorValue> toLegendItems(List<StageRenderInfo> renderStages) {
        List<NamedColorValue> items = new ArrayList<NamedColorValue>(renderStages.size());
        for (StageRenderInfo stageInfo : renderStages) {
            items.add(new NamedColorValue(
                    stageInfo.stage.getName(),
                    stageInfo.color,
                    formatDisplayText(stageInfo, legendDisplayMode)
            ));
        }
        return items;
    }

    /**
     * 按阶段最大值缩放宽度，并自上而下绘制漏斗块。
     */
    private void drawStages(Graphics2D g, ChartLayoutBox innerBox, List<StageRenderInfo> renderStages, Font labelFont) {
        int stageCount = renderStages.size();
        if (stageCount == 0) return;

        int availableHeight = innerBox.height() - (stageCount - 1) * stageGap;
        int stageHeight = Math.max(1, availableHeight / stageCount);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int plotWidth = innerBox.width();
        int plotLeft = innerBox.getLeft();
        int currentTop = innerBox.getTop();

        for (int i = 0; i < stageCount; i++) {
            StageRenderInfo stageInfo = renderStages.get(i);
            double widthRatio = stageInfo.stage.getValue() / stageInfo.maxValue;
            int stageWidth = (int) Math.round(plotWidth * widthRatio);
            int stageLeft = plotLeft + (plotWidth - stageWidth) / 2;

            // 上窄下更窄的梯形让相邻阶段视觉上自然衔接。
            Path2D path = createTrapezoidPath(stageLeft, currentTop, stageWidth, stageHeight);
            g.setColor(stageInfo.color);
            g.fill(path);

            if (showLabel) {
                drawStageLabel(g, labelFont, stageInfo, stageLeft, currentTop, stageWidth, stageHeight);
            }

            currentTop += stageHeight + stageGap;
        }
    }

    /**
     * 构造单个阶段使用的梯形路径。
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
     * 优先将标签绘制在阶段内部，空间不足时再切换到外侧引导线布局。
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

        // 根据背景色亮度选择深浅文本颜色，保证标签可读性。
        Color labelColor = chooseReadableLabelColor(stageInfo.color);

        if (stageHeight >= Math.max(minLabelHeight, textHeight + 4)) {
            int labelX = stageLeft + (stageWidth - textWidth) / 2;
            int labelY = stageTop + (stageHeight - textHeight) / 2 + metrics.getAscent();
            g.setColor(labelColor);
            g.drawString(text, labelX, labelY);
        } else {
            // 过矮的阶段无法容纳文本，改为右侧外置标签。
            drawExternalLabel(g, labelFont, text, stageLeft, stageTop, stageWidth, stageHeight, labelColor);
        }
    }

    /**
     * 在阶段右侧绘制外部标签和引导线。
     */
    private void drawExternalLabel(Graphics2D g, Font font, String text,
                                   int stageLeft, int stageTop, int stageWidth, int stageHeight, Color labelColor) {
        g.setFont(font);
        g.setColor(labelColor);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();

        // 外部标签统一落在右侧，避免遮挡漏斗主体。
        int labelX = stageLeft + stageWidth + externalLabelGap;
        int labelY = stageTop + (stageHeight - textHeight) / 2 + metrics.getAscent();

        // 引导线把标签与阶段中心对齐，减少阅读跳转成本。
        g.setColor(Color.GRAY);
        g.draw(new Line2D.Double(
                stageLeft + stageWidth, stageTop + stageHeight / 2.0,
                labelX - 2, stageTop + stageHeight / 2.0
        ));

        g.setColor(labelColor);
        g.drawString(text, labelX, labelY);
    }

    /**
     * 按展示模式拼装阶段文案。
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
     * 返回只读阶段列表。
     */
    public List<FunnelChartStage> getStages() {
        return Collections.unmodifiableList(stages);
    }

    /**
     * 批量替换阶段数据。
     */
    public FunnelChartElement setStages(List<FunnelChartStage> stages) {
        this.stages.clear();
        if (stages != null) {
            this.stages.addAll(stages);
        }
        return this;
    }

    /**
     * 返回只读调色板。
     */
    public List<Color> getPalette() {
        return Collections.unmodifiableList(palette);
    }

    /**
     * 设置阶段调色板。
     */
    public FunnelChartElement setPalette(List<Color> palette) {
        if (palette == null || palette.isEmpty()) {
            throw new PosterException("palette can not be empty");
        }
        this.palette = new ArrayList<Color>(palette);
        return this;
    }


    /**
     * 名称、数值和百分比的展示模式。
     */
    public enum DisplayMode {
        NAME("name"),
        VALUE("value"),
        PERCENT("percent"),
        NAME_VALUE("name+value"),
        NAME_PERCENT("name+percent");

        /**
         * 展示模式说明。
         */
        private final String desc;

        DisplayMode(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }


    /**
     * 渲染阶段缓存的派生数据。
     */
    private static class StageRenderInfo {


        /**
         * 原始阶段对象。
         */
        private final FunnelChartStage stage;

        /**
         * 当前阶段最终使用的颜色。
         */
        private final Color color;

        /**
         * 当前阶段占全部值的百分比。
         */
        private double percent;

        /**
         * 所有阶段中的最大值。
         */
        private double maxValue;

        private StageRenderInfo(FunnelChartStage stage, Color color) {
            this.stage = stage;
            this.color = color;
        }
    }
}
