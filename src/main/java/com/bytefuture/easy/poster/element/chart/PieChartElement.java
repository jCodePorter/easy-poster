package com.bytefuture.easy.poster.element.chart;

import com.bytefuture.easy.poster.element.AbstractDimensionElement;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 饼图元素。
 * <p>
 * 支持普通饼图、环形图以及玫瑰图/南丁格尔图三种模式，
 * 并提供图例、标签和颜色配置。
 * </p>
 *
 * @author biaoy
 * @since 2026/04/13
 */
public class PieChartElement extends AbstractDimensionElement<PieChartElement> {

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
     * 切片集合。
     */
    private final List<PieChartSlice> slices = new ArrayList<PieChartSlice>();

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
     * 是否显示切片标签。
     */
    private boolean showLabel = true;

    /**
     * 是否显示标题。
     */
    private boolean showTitle = true;

    /**
     * 图表模式。
     */
    private PieChartMode mode = PieChartMode.PIE;

    /**
     * 图例内容展示模式。
     */
    private DisplayMode legendDisplayMode = DisplayMode.NAME;

    /**
     * 切片标签内容展示模式。
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
     * 默认起始角度。
     */
    private double startAngle = -90D;

    /**
     * 环形图内径比例。
     */
    private double donutInnerRadiusRatio = 0.58D;

    /**
     * 玫瑰图最小半径比例。
     */
    private double roseInnerRadiusRatio = 0.30D;

    /**
     * 标签最小角度阈值。
     */
    private double minLabelAngle = 12D;

    /**
     * 标签最小环宽阈值。
     */
    private int minLabelBand = 18;

    /**
     * 构造图表元素。
     *
     * @param width  元素宽度
     * @param height 元素高度
     */
    public PieChartElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 设置图表标题。
     *
     * @param title 图表标题
     * @return 当前元素
     */
    public PieChartElement setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置图表内边距。
     *
     * @param padding 图表内边距
     * @return 当前元素
     */
    public PieChartElement setPadding(Insets padding) {
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
    public PieChartElement setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * 设置标签颜色。
     *
     * @param labelColor 标签颜色
     * @return 当前元素
     */
    public PieChartElement setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
        return this;
    }

    /**
     * 设置是否显示图例。
     *
     * @param showLegend 是否显示图例
     * @return 当前元素
     */
    public PieChartElement setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
        return this;
    }

    /**
     * 设置是否显示标签。
     *
     * @param showLabel 是否显示标签
     * @return 当前元素
     */
    public PieChartElement setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
        return this;
    }

    /**
     * 设置是否显示标题。
     *
     * @param showTitle 是否显示标题
     * @return 当前元素
     */
    public PieChartElement setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    /**
     * 设置图表模式。
     *
     * @param mode 图表模式
     * @return 当前元素
     */
    public PieChartElement setMode(PieChartMode mode) {
        if (mode == null) {
            throw new PosterException("mode can not be null");
        }
        this.mode = mode;
        return this;
    }

    /**
     * 设置图例展示模式。
     *
     * @param legendDisplayMode 图例展示模式
     * @return 当前元素
     */
    public PieChartElement setLegendDisplayMode(DisplayMode legendDisplayMode) {
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
    public PieChartElement setLabelDisplayMode(DisplayMode labelDisplayMode) {
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
    public PieChartElement setTitleFontSize(int titleFontSize) {
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
    public PieChartElement setLegendFontSize(int legendFontSize) {
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
    public PieChartElement setLabelFontSize(int labelFontSize) {
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
    public PieChartElement setLegendItemGap(int legendItemGap) {
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
    public PieChartElement setLegendMarkerSize(int legendMarkerSize) {
        if (legendMarkerSize <= 0) {
            throw new PosterException("legendMarkerSize must be greater than 0");
        }
        this.legendMarkerSize = legendMarkerSize;
        return this;
    }

    /**
     * 设置起始角度。
     *
     * @param startAngle 起始角度
     * @return 当前元素
     */
    public PieChartElement setStartAngle(double startAngle) {
        this.startAngle = startAngle;
        return this;
    }

    /**
     * 设置环形图内径比例。
     *
     * @param donutInnerRadiusRatio 环形图内径比例
     * @return 当前元素
     */
    public PieChartElement setDonutInnerRadiusRatio(double donutInnerRadiusRatio) {
        this.donutInnerRadiusRatio = donutInnerRadiusRatio;
        return this;
    }

    /**
     * 设置玫瑰图最小半径比例。
     *
     * @param roseInnerRadiusRatio 玫瑰图最小半径比例
     * @return 当前元素
     */
    public PieChartElement setRoseInnerRadiusRatio(double roseInnerRadiusRatio) {
        this.roseInnerRadiusRatio = roseInnerRadiusRatio;
        return this;
    }

    /**
     * 设置标签最小角度阈值。
     *
     * @param minLabelAngle 标签最小角度阈值
     * @return 当前元素
     */
    public PieChartElement setMinLabelAngle(double minLabelAngle) {
        if (minLabelAngle < 0D) {
            throw new PosterException("minLabelAngle must be greater than or equal to 0");
        }
        this.minLabelAngle = minLabelAngle;
        return this;
    }

    /**
     * 设置标签最小环宽阈值。
     *
     * @param minLabelBand 标签最小环宽阈值
     * @return 当前元素
     */
    public PieChartElement setMinLabelBand(int minLabelBand) {
        if (minLabelBand <= 0) {
            throw new PosterException("minLabelBand must be greater than 0");
        }
        this.minLabelBand = minLabelBand;
        return this;
    }

    /**
     * 设置调色板。
     *
     * @param palette 调色板
     * @return 当前元素
     */
    public PieChartElement setPalette(List<Color> palette) {
        if (palette == null || palette.isEmpty()) {
            throw new PosterException("palette can not be empty");
        }
        this.palette = new ArrayList<Color>(palette);
        return this;
    }

    /**
     * 设置切片集合。
     *
     * @param slices 切片集合
     * @return 当前元素
     */
    public PieChartElement setSlices(List<PieChartSlice> slices) {
        this.slices.clear();
        if (slices != null) {
            this.slices.addAll(slices);
        }
        return this;
    }

    /**
     * 添加切片。
     *
     * @param slice 切片对象
     * @return 当前元素
     */
    public PieChartElement addSlice(PieChartSlice slice) {
        if (slice == null) {
            throw new PosterException("slice can not be null");
        }
        this.slices.add(slice);
        return this;
    }

    /**
     * 添加切片。
     *
     * @param name  切片名称
     * @param value 切片数值
     * @return 当前元素
     */
    public PieChartElement addSlice(String name, Number value) {
        return addSlice(PieChartSlice.of(name, value));
    }

    /**
     * 添加带颜色的切片。
     *
     * @param name  切片名称
     * @param value 切片数值
     * @param color 切片颜色
     * @return 当前元素
     */
    public PieChartElement addSlice(String name, Number value, Color color) {
        return addSlice(PieChartSlice.of(name, value, color));
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
        List<SliceRenderInfo> drawableSlices = resolveDrawableSlices();
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
            Font sliceLabelFont = baseFont.deriveFont(Font.PLAIN, (float) labelFontSize);

            LayoutBox innerBox = resolveInnerBox(origin);
            if (showTitle) {
                innerBox.top += drawTitle(g, innerBox, titleFont);
            }
            if (showLegend) {
                innerBox.top += drawLegend(g, innerBox, legendFont, drawableSlices);
            }
            drawSlices(g, innerBox, drawableSlices, sliceLabelFont);
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
            throw new PosterException("pie chart width and height must be greater than 0");
        }
        if (palette == null || palette.isEmpty()) {
            throw new PosterException("palette can not be empty");
        }
        if (mode == PieChartMode.DONUT && (donutInnerRadiusRatio <= 0D || donutInnerRadiusRatio >= 1D)) {
            throw new PosterException("donutInnerRadiusRatio must be between 0 and 1");
        }
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
     * 解析可绘制切片。
     * <p>
     * 首版对非正数切片采用跳过策略，仅绘制正数值数据。
     * </p>
     *
     * @return 可绘制切片集合
     */
    private List<SliceRenderInfo> resolveDrawableSlices() {
        List<SliceRenderInfo> drawableSlices = new ArrayList<SliceRenderInfo>();
        double total = 0D;
        double maxValue = 0D;
        int colorIndex = 0;
        for (PieChartSlice slice : slices) {
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
     * 解析切片颜色。
     *
     * @param slice      切片对象
     * @param colorIndex 调色板索引
     * @return 最终颜色
     */
    private Color resolveSliceColor(PieChartSlice slice, int colorIndex) {
        return Optional.ofNullable(slice.getColor()).orElse(palette.get(colorIndex % palette.size()));
    }

    /**
     * 解析内部可用绘制区域。
     *
     * @param origin 元素原点
     * @return 内部布局区域
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
     *
     * @param g         画笔
     * @param innerBox  内部布局区域
     * @param titleFont 标题字体
     * @return 占用的高度
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
     *
     * @param g            画笔
     * @param innerBox     内部布局区域
     * @param legendFont   图例字体
     * @param drawableData 可绘制切片数据
     * @return 占用的高度
     */
    private int drawLegend(Graphics2D g, LayoutBox innerBox, Font legendFont, List<SliceRenderInfo> drawableData) {
        g.setFont(legendFont);
        g.setColor(labelColor);
        FontMetrics metrics = g.getFontMetrics();
        int rowHeight = Math.max(metrics.getHeight(), legendMarkerSize) + 6;
        int cursorX = innerBox.left;
        int cursorY = innerBox.top;
        int rows = 1;
        for (SliceRenderInfo sliceInfo : drawableData) {
            String text = formatDisplayText(sliceInfo, legendDisplayMode);
            int itemWidth = legendMarkerSize + 6 + metrics.stringWidth(text) + legendItemGap;
            if (cursorX > innerBox.left && cursorX + itemWidth > innerBox.right) {
                rows++;
                cursorX = innerBox.left;
                cursorY += rowHeight;
            }
            int baseline = cursorY + metrics.getAscent();
            int markerY = baseline - metrics.getAscent() + Math.max(0, (metrics.getHeight() - legendMarkerSize) / 2);
            g.setColor(sliceInfo.color);
            g.fillRoundRect(cursorX, markerY, legendMarkerSize, legendMarkerSize, 4, 4);
            g.setColor(labelColor);
            g.drawString(text, cursorX + legendMarkerSize + 6, baseline);
            cursorX += itemWidth;
        }
        return rows * rowHeight;
    }

    /**
     * 绘制切片区域。
     *
     * @param g              画笔
     * @param innerBox       内部布局区域
     * @param drawableSlices 可绘制切片
     * @param labelFont      标签字体
     */
    private void drawSlices(Graphics2D g, LayoutBox innerBox, List<SliceRenderInfo> drawableSlices, Font labelFont) {
        LayoutBox plotBox = resolvePlotBox(innerBox);
        double centerX = plotBox.left + plotBox.width() / 2D;
        double centerY = plotBox.top + plotBox.height() / 2D;
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
     * 解析图形绘制区域。
     *
     * @param innerBox 内部区域
     * @return 图形绘制区域
     */
    private LayoutBox resolvePlotBox(LayoutBox innerBox) {
        int side = Math.max(1, Math.min(innerBox.width(), innerBox.height()));
        int horizontalOffset = Math.max(0, (innerBox.width() - side) / 2);
        int verticalOffset = Math.max(0, (innerBox.height() - side) / 2);
        return new LayoutBox(
                innerBox.left + horizontalOffset,
                innerBox.top + verticalOffset,
                innerBox.left + horizontalOffset + side,
                innerBox.top + verticalOffset + side
        );
    }

    /**
     * 解析基础内半径。
     *
     * @param maxOuterRadius 最大外半径
     * @return 基础内半径
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
     * 解析切片外半径。
     *
     * @param sliceInfo      切片信息
     * @param maxOuterRadius 最大外半径
     * @return 切片外半径
     */
    private double resolveOuterRadius(SliceRenderInfo sliceInfo, double maxOuterRadius) {
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
     * 创建切片形状。
     *
     * @param centerX     圆心 X
     * @param centerY     圆心 Y
     * @param innerRadius 内半径
     * @param outerRadius 外半径
     * @param start       起始角度
     * @param extent      扩展角度
     * @return 切片形状
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
     * 绘制切片标签。
     *
     * @param g           画笔
     * @param labelFont   标签字体
     * @param sliceInfo   切片信息
     * @param centerX     圆心 X
     * @param centerY     圆心 Y
     * @param innerRadius 内半径
     * @param outerRadius 外半径
     * @param start       起始角度
     * @param extent      扩展角度
     */
    private void drawSliceLabel(Graphics2D g, Font labelFont, SliceRenderInfo sliceInfo, double centerX, double centerY,
                                double innerRadius, double outerRadius, double start, double extent) {
        String text = formatDisplayText(sliceInfo, labelDisplayMode);
        if (text == null || text.isEmpty()) {
            return;
        }
        g.setFont(labelFont);
        FontMetrics metrics = g.getFontMetrics();
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
     * 判断标签是否可以绘制。
     *
     * @param metrics     字体度量
     * @param text        标签文本
     * @param innerRadius 内半径
     * @param outerRadius 外半径
     * @param extent      扩展角度
     * @return 是否可以绘制
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
     * 选择易读的标签颜色。
     *
     * @param background 背景颜色
     * @return 标签颜色
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
     *
     * @param sliceInfo    切片信息
     * @param displayMode  展示模式
     * @return 显示文本
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
     * 获取切片集合。
     *
     * @return 切片集合
     */
    public List<PieChartSlice> getSlices() {
        return Collections.unmodifiableList(slices);
    }

    /**
     * 获取当前调色板。
     *
     * @return 当前调色板
     */
    public List<Color> getPalette() {
        return Collections.unmodifiableList(palette);
    }

    /**
     * 饼图模式。
     */
    @Getter
    @AllArgsConstructor
    public enum PieChartMode {
        /**
         * 普通饼图。
         */
        PIE("普通饼图"),
        /**
         * 环形图。
         */
        DONUT("环形图"),
        /**
         * 玫瑰图/南丁格尔图。
         */
        ROSE("玫瑰图");

        /**
         * 模式描述。
         */
        private final String desc;
    }

    /**
     * 内容展示模式。
     */
    @Getter
    @AllArgsConstructor
    public enum DisplayMode {
        /**
         * 仅名称。
         */
        NAME("名称"),
        /**
         * 仅数值。
         */
        VALUE("数值"),
        /**
         * 仅百分比。
         */
        PERCENT("百分比"),
        /**
         * 名称加数值。
         */
        NAME_VALUE("名称+数值"),
        /**
         * 名称加百分比。
         */
        NAME_PERCENT("名称+百分比");

        /**
         * 模式描述。
         */
        private final String desc;
    }

    /**
     * 内部布局矩形。
     */
    private static class LayoutBox {

        /**
         * 左边界。
         */
        private final int left;

        /**
         * 上边界。
         */
        private int top;

        /**
         * 右边界。
         */
        private final int right;

        /**
         * 下边界。
         */
        private final int bottom;

        /**
         * 构造内部布局矩形。
         *
         * @param left   左边界
         * @param top    上边界
         * @param right  右边界
         * @param bottom 下边界
         */
        private LayoutBox(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        /**
         * 计算宽度。
         *
         * @return 宽度
         */
        private int width() {
            return Math.max(0, right - left);
        }

        /**
         * 计算高度。
         *
         * @return 高度
         */
        private int height() {
            return Math.max(0, bottom - top);
        }
    }

    /**
     * 切片绘制信息。
     */
    private static class SliceRenderInfo {

        /**
         * 原始切片。
         */
        private final PieChartSlice slice;

        /**
         * 解析后的颜色。
         */
        private final Color color;

        /**
         * 当前切片百分比。
         */
        private double percent;

        /**
         * 当前数据集最大值。
         */
        private double maxValue;

        /**
         * 构造切片绘制信息。
         *
         * @param slice 原始切片
         * @param color 解析后的颜色
         */
        private SliceRenderInfo(PieChartSlice slice, Color color) {
            this.slice = slice;
            this.color = color;
        }
    }
}
