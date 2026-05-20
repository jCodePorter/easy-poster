package com.bytefuture.easy.poster.element.chart;

import com.bytefuture.easy.poster.element.chart.base.*;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.model.PosterContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

/**
 * 饼图元素，支持普通饼图、环形图和玫瑰图三种模式。
 */
public class PieChartElement extends AbstractChartElement<PieChartElement> {

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
     * 原始切片列表。
     */
    private final List<ChartData> slices = new ArrayList<>();

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
     * 是否显示扇区标签。
     */
    private boolean showLabel = true;
    /**
     * 是否显示标题。
     */
    private boolean showTitle = true;
    /**
     * 饼图渲染模式。
     */
    private PieChartMode mode = PieChartMode.PIE;
    /**
     * 图例文本展示模式。
     */
    private DisplayMode legendDisplayMode = DisplayMode.NAME;
    /**
     * 扇区标签展示模式。
     */
    private DisplayMode labelDisplayMode = DisplayMode.NAME_PERCENT;
    /**
     * 当前实例使用的调色板。
     */
    private List<Color> palette = new ArrayList<Color>(DEFAULT_PALETTE);

    private int titleFontSize = 18;

    private int legendFontSize = 12;

    private int labelFontSize = 12;

    private int legendItemGap = 18;

    private int legendMarkerSize = 10;

    private double startAngle = -90D;
    /**
     * 环形图内半径比例。
     */
    private double donutInnerRadiusRatio = 0.58D;
    /**
     * 玫瑰图基础内半径比例。
     */
    private double roseInnerRadiusRatio = 0.30D;
    /**
     * 可绘制内部标签的最小扇区角度。
     */
    private double minLabelAngle = 12D;
    /**
     * 可绘制内部标签的最小径向带宽。
     */
    private int minLabelBand = 18;

    /**
     * 创建指定宽高的饼图元素。
     */
    public PieChartElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 设置图表标题。
     */
    public PieChartElement setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置图表内边距。
     */
    public PieChartElement setPadding(Insets padding) {
        if (padding == null) {
            throw new PosterException("padding can not be null");
        }
        setPaddingInternal(padding);
        return this;
    }

    /**
     * 设置图表背景色。
     */
    public PieChartElement setBackgroundColor(Color backgroundColor) {
        setBackgroundColorInternal(backgroundColor);
        return this;
    }

    /**
     * 设置标题、图例和标签共用的文字颜色。
     */
    public PieChartElement setLabelColor(Color labelColor) {
        setLabelColorInternal(labelColor);
        return this;
    }

    /**
     * 设置是否显示图例。
     */
    public PieChartElement setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
        return this;
    }

    /**
     * 设置是否显示扇区标签。
     */
    public PieChartElement setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
        return this;
    }

    /**
     * 设置是否显示标题。
     */
    public PieChartElement setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    /**
     * 设置饼图渲染模式。
     */
    public PieChartElement setMode(PieChartMode mode) {
        if (mode == null) {
            throw new PosterException("mode can not be null");
        }
        this.mode = mode;
        return this;
    }

    /**
     * 设置图例文本展示模式。
     */
    public PieChartElement setLegendDisplayMode(DisplayMode legendDisplayMode) {
        if (legendDisplayMode == null) {
            throw new PosterException("legendDisplayMode can not be null");
        }
        this.legendDisplayMode = legendDisplayMode;
        return this;
    }

    /**
     * 设置扇区标签展示模式。
     */
    public PieChartElement setLabelDisplayMode(DisplayMode labelDisplayMode) {
        if (labelDisplayMode == null) {
            throw new PosterException("labelDisplayMode can not be null");
        }
        this.labelDisplayMode = labelDisplayMode;
        return this;
    }

    /**
     * 设置标题字号。
     */
    public PieChartElement setTitleFontSize(int titleFontSize) {
        if (titleFontSize <= 0) {
            throw new PosterException("titleFontSize must be greater than 0");
        }
        this.titleFontSize = titleFontSize;
        return this;
    }

    /**
     * 设置图例字号。
     */
    public PieChartElement setLegendFontSize(int legendFontSize) {
        if (legendFontSize <= 0) {
            throw new PosterException("legendFontSize must be greater than 0");
        }
        this.legendFontSize = legendFontSize;
        return this;
    }

    /**
     * 设置标签字号。
     */
    public PieChartElement setLabelFontSize(int labelFontSize) {
        if (labelFontSize <= 0) {
            throw new PosterException("labelFontSize must be greater than 0");
        }
        this.labelFontSize = labelFontSize;
        return this;
    }

    /**
     * 设置图例项之间的水平间距。
     */
    public PieChartElement setLegendItemGap(int legendItemGap) {
        if (legendItemGap < 0) {
            throw new PosterException("legendItemGap must be greater than or equal to 0");
        }
        this.legendItemGap = legendItemGap;
        return this;
    }

    /**
     * 设置图例色块尺寸。
     */
    public PieChartElement setLegendMarkerSize(int legendMarkerSize) {
        if (legendMarkerSize <= 0) {
            throw new PosterException("legendMarkerSize must be greater than 0");
        }
        this.legendMarkerSize = legendMarkerSize;
        return this;
    }

    /**
     * 设置起始绘制角度。
     */
    public PieChartElement setStartAngle(double startAngle) {
        this.startAngle = startAngle;
        return this;
    }

    /**
     * 设置环形图内半径比例。
     */
    public PieChartElement setDonutInnerRadiusRatio(double donutInnerRadiusRatio) {
        this.donutInnerRadiusRatio = donutInnerRadiusRatio;
        return this;
    }

    /**
     * 设置玫瑰图内半径比例。
     */
    public PieChartElement setRoseInnerRadiusRatio(double roseInnerRadiusRatio) {
        this.roseInnerRadiusRatio = roseInnerRadiusRatio;
        return this;
    }

    /**
     * 设置显示标签所需的最小扇区角度。
     */
    public PieChartElement setMinLabelAngle(double minLabelAngle) {
        if (minLabelAngle < 0D) {
            throw new PosterException("minLabelAngle must be greater than or equal to 0");
        }
        this.minLabelAngle = minLabelAngle;
        return this;
    }

    /**
     * 设置显示标签所需的最小径向带宽。
     */
    public PieChartElement setMinLabelBand(int minLabelBand) {
        if (minLabelBand <= 0) {
            throw new PosterException("minLabelBand must be greater than 0");
        }
        this.minLabelBand = minLabelBand;
        return this;
    }

    /**
     * 添加单个切片对象。
     */
    public PieChartElement addSlice(ChartData slice) {
        if (slice == null) {
            throw new PosterException("slice can not be null");
        }
        this.slices.add(slice);
        return this;
    }

    /**
     * 通过名称和值添加切片。
     */
    public PieChartElement addSlice(String name, Number value) {
        return addSlice(ChartData.of(name, value));
    }

    /**
     * 通过名称、值和颜色添加切片。
     */
    public PieChartElement addSlice(String name, Number value, Color color) {
        return addSlice(ChartData.of(name, value, color));
    }

    /**
     * 按标题、图例、主体三个区域依次完成饼图绘制。
     */
    @Override
    protected void renderChart(Graphics2D g, PosterContext context, ChartLayoutBox innerBox) {
        List<SliceRenderInfo> drawableSlices = resolveDrawableSlices();
        Font baseFont = resolveBaseFont(context);
        Font titleFont = baseFont.deriveFont(Font.BOLD, (float) titleFontSize);
        Font legendFont = baseFont.deriveFont(Font.PLAIN, (float) legendFontSize);
        Font sliceLabelFont = baseFont.deriveFont(Font.PLAIN, (float) labelFontSize);

        if (showTitle) {
            innerBox.shiftTop(drawTitle(g, innerBox, titleFont));
        }
        if (showLegend) {
            innerBox.shiftTop(drawLegend(g, innerBox, legendFont, drawableSlices));
        }
        drawSlices(g, innerBox, drawableSlices, sliceLabelFont);
    }

    /**
     * 校验绘图前置条件，避免布局和比例计算阶段出现非法输入。
     */
    @Override
    protected void validateChartData() {
        if (width <= 0 || height <= 0) {
            throw new PosterException("pie chart width and height must be greater than 0");
        }
        if (palette == null || palette.isEmpty()) {
            throw new PosterException("palette can not be empty");
        }
        // 环形图必须保留可见圆环，因此内半径比例只能落在开区间 (0, 1)。
        if (mode == PieChartMode.DONUT && (donutInnerRadiusRatio <= 0D || donutInnerRadiusRatio >= 1D)) {
            throw new PosterException("donutInnerRadiusRatio must be between 0 and 1");
        }
        // 玫瑰图允许从圆心开始，但不能让内半径大于等于外半径。
        if (mode == PieChartMode.ROSE && (roseInnerRadiusRatio < 0D || roseInnerRadiusRatio >= 1D)) {
            throw new PosterException("roseInnerRadiusRatio must be between 0 and 1");
        }
        if (showLegend && legendDisplayMode == null) {
            throw new PosterException("legendDisplayMode can not be null");
        }
        if (showLabel && labelDisplayMode == null) {
            throw new PosterException("labelDisplayMode can not be null");
        }
        if (slices.isEmpty()) {
            throw new PosterException("slices can not be empty");
        }
    }

    /**
     * 过滤无效切片并补齐渲染阶段需要的派生数据。
     */
    private List<SliceRenderInfo> resolveDrawableSlices() {
        List<SliceRenderInfo> drawableSlices = new ArrayList<SliceRenderInfo>();
        double total = 0D;
        double maxValue = 0D;
        int colorIndex = 0;
        for (ChartData slice : slices) {
            // null 或非正值切片不参与绘制，也不计入总和。
            if (slice == null) {
                continue;
            }
            if (slice.getValue() <= 0D) {
                continue;
            }
            Color resolvedColor = resolveSliceColor(slice, colorIndex);
            drawableSlices.add(new SliceRenderInfo(slice, resolvedColor));
            total += slice.getValue();
            maxValue = Math.max(maxValue, slice.getValue());
            colorIndex++;
        }
        if (drawableSlices.isEmpty() || total <= 0D) {
            throw new PosterException("pie chart requires at least one positive slice value");
        }
        for (SliceRenderInfo drawableSlice : drawableSlices) {
            drawableSlice.percent = drawableSlice.slice.getValue() / total * 100D;
            drawableSlice.maxValue = maxValue;
        }
        return drawableSlices;
    }

    /**
     * 解析单个切片最终使用的颜色。
     */
    private Color resolveSliceColor(ChartData slice, int colorIndex) {
        return Optional.ofNullable(slice.getColor()).orElse(palette.get(colorIndex % palette.size()));
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
    private int drawLegend(Graphics2D g, ChartLayoutBox innerBox, Font legendFont, List<SliceRenderInfo> drawableData) {
        return ChartLegendRenderer.drawLegend(
                g,
                innerBox,
                legendFont,
                toLegendItems(drawableData),
                legendMarkerSize,
                legendItemGap,
                getLabelColor()
        );
    }

    /**
     * 将切片信息转换成图例组件需要的数据结构。
     */
    private List<NamedColorValue> toLegendItems(List<SliceRenderInfo> drawableData) {
        List<NamedColorValue> items = new ArrayList<NamedColorValue>(drawableData.size());
        for (SliceRenderInfo sliceInfo : drawableData) {
            items.add(new NamedColorValue(
                    sliceInfo.slice.getName(),
                    sliceInfo.color,
                    formatDisplayText(sliceInfo, legendDisplayMode)
            ));
        }
        return items;
    }

    /**
     * 在可用区域内完成扇区绘制。
     */
    private void drawSlices(Graphics2D g, ChartLayoutBox innerBox, List<SliceRenderInfo> drawableSlices, Font labelFont) {
        ChartLayoutBox plotBox = resolvePlotBox(innerBox);
        double centerX = plotBox.getLeft() + plotBox.width() / 2D;
        double centerY = plotBox.getTop() + plotBox.height() / 2D;
        double maxOuterRadius = Math.min(plotBox.width(), plotBox.height()) / 2D;
        double defaultInnerRadius = resolveInnerRadius(maxOuterRadius);
        double angleCursor = startAngle;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (SliceRenderInfo sliceInfo : drawableSlices) {
            double angleExtent = sliceInfo.percent / 100D * 360D;
            double outerRadius = resolveOuterRadius(sliceInfo, maxOuterRadius);
            Shape sliceShape = createSliceShape(centerX, centerY, defaultInnerRadius, outerRadius, angleCursor, angleExtent);
            g.setColor(sliceInfo.color);
            g.fill(sliceShape);
            if (showLabel) {
                drawSliceLabel(g, labelFont, sliceInfo, centerX, centerY, defaultInnerRadius, outerRadius, angleCursor, angleExtent);
            }
            angleCursor += angleExtent;
        }
    }

    /**
     * 将可用矩形裁成正方形绘图区，避免饼图被拉伸。
     */
    private ChartLayoutBox resolvePlotBox(ChartLayoutBox innerBox) {
        int side = Math.max(1, Math.min(innerBox.width(), innerBox.height()));
        int horizontalOffset = Math.max(0, (innerBox.width() - side) / 2);
        int verticalOffset = Math.max(0, (innerBox.height() - side) / 2);
        return new ChartLayoutBox(
                innerBox.getLeft() + horizontalOffset,
                innerBox.getTop() + verticalOffset,
                innerBox.getLeft() + horizontalOffset + side,
                innerBox.getTop() + verticalOffset + side
        );
    }

    /**
     * 按当前模式计算基础内半径。
     */
    private double resolveInnerRadius(double maxOuterRadius) {
        if (mode == PieChartMode.DONUT) {
            return maxOuterRadius * donutInnerRadiusRatio;
        }
        if (mode == PieChartMode.ROSE) {
            return maxOuterRadius * roseInnerRadiusRatio;
        }
        return 0D;
    }

    /**
     * 计算当前切片的外半径。
     */
    private double resolveOuterRadius(SliceRenderInfo sliceInfo, double maxOuterRadius) {
        // 只有玫瑰图会按数据值改变外半径，其余模式统一使用最大半径。
        if (mode != PieChartMode.ROSE) {
            return maxOuterRadius;
        }
        double minOuterRadius = maxOuterRadius * Math.max(0D, roseInnerRadiusRatio);
        if (sliceInfo.maxValue <= 0D) {
            return minOuterRadius;
        }
        return minOuterRadius + (maxOuterRadius - minOuterRadius) * (sliceInfo.slice.getValue() / sliceInfo.maxValue);
    }

    /**
     * 根据半径和角度构建扇区形状。
     */
    private Shape createSliceShape(double centerX, double centerY, double innerRadius, double outerRadius, double start, double extent) {
        Arc2D outerArc = new Arc2D.Double(centerX - outerRadius, centerY - outerRadius,
                outerRadius * 2D, outerRadius * 2D, start, extent, Arc2D.PIE);
        if (innerRadius <= 0D) {
            return outerArc;
        }
        Area area = new Area(outerArc);
        Ellipse2D innerCircle = new Ellipse2D.Double(centerX - innerRadius, centerY - innerRadius,
                innerRadius * 2D, innerRadius * 2D);
        area.subtract(new Area(innerCircle));
        return area;
    }

    /**
     * 在扇区中部尝试绘制标签。
     */
    private void drawSliceLabel(Graphics2D g, Font labelFont, SliceRenderInfo sliceInfo, double centerX, double centerY,
                                double innerRadius, double outerRadius, double start, double extent) {
        String text = formatDisplayText(sliceInfo, labelDisplayMode);
        if (text == null || text.isEmpty()) {
            return;
        }
        g.setFont(labelFont);
        FontMetrics metrics = g.getFontMetrics();
        // 过小扇区直接跳过，避免文字压出扇区边界。
        if (!canDrawLabel(metrics, text, innerRadius, outerRadius, extent)) {
            return;
        }
        double angle = Math.toRadians(start + extent / 2D);
        double radius = innerRadius + (outerRadius - innerRadius) * 0.58D;
        double pointX = centerX + Math.cos(angle) * radius;
        double pointY = centerY + Math.sin(angle) * radius;
        int drawX = (int) Math.round(pointX - metrics.stringWidth(text) / 2D);
        int drawY = (int) Math.round(pointY + metrics.getAscent() / 2D);
        g.setColor(chooseReadableLabelColor(sliceInfo.color));
        g.drawString(text, drawX, drawY);
    }

    /**
     * 判断当前扇区是否具备绘制内部标签的空间。
     */
    private boolean canDrawLabel(FontMetrics metrics, String text, double innerRadius, double outerRadius, double extent) {
        if (extent < minLabelAngle) {
            return false;
        }
        double radius = innerRadius + (outerRadius - innerRadius) * 0.58D;
        double availableArc = Math.toRadians(extent) * radius;
        double availableBand = outerRadius - innerRadius;
        return availableArc >= metrics.stringWidth(text) + 6D
                && availableBand >= Math.max(minLabelBand, metrics.getHeight() + 2D);
    }

    /**
     * 按展示模式拼装扇区文案。
     */
    private String formatDisplayText(SliceRenderInfo sliceInfo, DisplayMode displayMode) {
        String name = Optional.ofNullable(sliceInfo.slice.getName()).orElse("");
        String value = decimalFormat.format(sliceInfo.slice.getValue());
        String percent = decimalFormat.format(sliceInfo.percent) + "%";
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
     * 批量替换切片数据。
     */
    public PieChartElement setSlices(List<ChartData> slices) {
        this.slices.clear();
        if (slices != null) {
            this.slices.addAll(slices);
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
     * 设置切片调色板。
     */
    public PieChartElement setPalette(List<Color> palette) {
        if (palette == null || palette.isEmpty()) {
            throw new PosterException("palette can not be empty");
        }
        this.palette = new ArrayList<Color>(palette);
        return this;
    }


    /**
     * 饼图渲染模式。
     */
    @Getter
    @AllArgsConstructor
    public enum PieChartMode {
        PIE("Pie"),
        DONUT("Donut"),
        ROSE("Rose");

        private final String desc;
    }


    /**
     * 名称、数值和百分比的展示模式。
     */
    @Getter
    @AllArgsConstructor
    public enum DisplayMode {
        NAME("Name"),
        VALUE("Value"),
        PERCENT("Percent"),
        NAME_VALUE("Name+Value"),
        NAME_PERCENT("Name+Percent");

        private final String desc;
    }


    /**
     * 渲染阶段缓存的切片派生数据。
     */
    private static class SliceRenderInfo {

        /**
         * 原始切片对象。
         */
        private final ChartData slice;

        /**
         * 当前切片最终使用的颜色。
         */
        private final Color color;

        /**
         * 当前切片占全部有效值的百分比。
         */
        private double percent;

        /**
         * 所有有效切片中的最大值。
         */
        private double maxValue;

        private SliceRenderInfo(ChartData slice, Color color) {
            this.slice = slice;
            this.color = color;
        }
    }
}
