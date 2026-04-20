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
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 濠曞繑鏋熼崶鎯у帗缁辩姰鈧?
 * <p>
 * 閻劋绨崷銊︽崳閹躲儰鑵戠紒妯哄煑濠曞繑鏋熼崶鎾呯礉鐏炴洜銇氶梼鑸殿唽閹呮畱閺佺増宓侀柅鎺戝櫤鏉╁洨鈻奸敍?
 * 閺€顖涘瘮閼奉亜鐣炬稊澶愵杹閼瑰眰鈧焦鐖ｇ粵淇扁偓浣告禈娓氬鎷伴弽鍥暯闁板秶鐤嗛妴?
 * </p>
 *
 * @author biaoy
 * @since 2026/04/13
 */
public class FunnelChartElement extends AbstractChartElement<FunnelChartElement> {

    /**
     * 姒涙顓荤拫鍐閺夎￥鈧?
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
     * 闂冭埖顔岄梿鍡楁値閵?
     */
    private final List<FunnelChartStage> stages = new ArrayList<FunnelChartStage>();

    /**
     * 閺佹澘鈧吋鐗稿蹇撳閸ｃ劊鈧?
     */
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");

    /**
     * 閸ユ崘銆冮弽鍥暯閵?
     */
    private String title;

    /**
     * 閺勵垰鎯侀弰鍓с仛閸ュ彞绶ラ妴?
     */
    private boolean showLegend = true;

    /**
     * 閺勵垰鎯侀弰鍓с仛闂冭埖顔岄弽鍥╊劮閵?
     */
    private boolean showLabel = true;

    /**
     * 閺勵垰鎯侀弰鍓с仛閺嶅洭顣介妴?
     */
    private boolean showTitle = true;

    /**
     * 閸ュ彞绶ラ崘鍛啇鐏炴洜銇氬Ο鈥崇础閵?
     */
    private DisplayMode legendDisplayMode = DisplayMode.NAME_VALUE;

    /**
     * 闂冭埖顔岄弽鍥╊劮閸愬懎顔愮仦鏇犮仛濡€崇础閵?
     */
    private DisplayMode labelDisplayMode = DisplayMode.NAME_PERCENT;

    /**
     * 閼奉亜鐣炬稊澶庣殶閼瑰弶婢橀妴?
     */
    private List<Color> palette = new ArrayList<Color>(DEFAULT_PALETTE);

    /**
     * 閺嶅洭顣界€涙褰块妴?
     */
    private int titleFontSize = 18;

    /**
     * 閸ュ彞绶ョ€涙褰块妴?
     */
    private int legendFontSize = 12;

    /**
     * 閺嶅洨顒风€涙褰块妴?
     */
    private int labelFontSize = 12;

    /**
     * 閸ュ彞绶ユい閫涚闂傚娈戦梻纾嬬獩閵?
     */
    private int legendItemGap = 18;

    /**
     * 閸ュ彞绶ラ懝鎻掓健鐏忓搫顕妴?
     */
    private int legendMarkerSize = 10;

    /**
     * 閺嶅洨顒烽張鈧亸蹇涚彯鎼达箓妲囬崐纭风礉娴ｅ簼绨銈呪偓鍏兼閺嶅洨顒风亸鍡欑帛閸掕泛婀径鏍劥閵?
     */
    private int minLabelHeight = 18;

    /**
     * 婢舵牠鍎撮弽鍥╊劮娑撳酣妯佸▓鍏哥闂傚娈戦梻纾嬬獩閵?
     */
    private int externalLabelGap = 4;

    /**
     * 闂冭埖顔屾稊瀣？閻ㄥ嫰妫跨捄婵勨偓?
     */
    private int stageGap = 8;

    /**
     * 閺嬪嫰鈧姴娴樼悰銊ュ帗缁辩姰鈧?
     *
     * @param width  閸忓啰绀岀€硅棄瀹?
     * @param height 閸忓啰绀屾妯哄
     */
    public FunnelChartElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 鐠佸墽鐤嗛崶鎹愩€冮弽鍥暯閵?
     *
     * @param title 閸ユ崘銆冮弽鍥暯
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崶鎹愩€冮崘鍛扮珶鐠烘縿鈧?
     *
     * @param padding 閸ユ崘銆冮崘鍛扮珶鐠?
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setPadding(Insets padding) {
        if (padding == null) {
            throw new PosterException("padding can not be null");
        }
        setPaddingInternal(padding);
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崶鎹愩€冮懗灞炬珯閼瑰眰鈧?
     *
     * @param backgroundColor 閼冲本娅欓懝?
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setBackgroundColor(Color backgroundColor) {
        setBackgroundColorInternal(backgroundColor);
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弽鍥╊劮妫版粏澹婇妴?
     *
     * @param labelColor 閺嶅洨顒锋０婊嗗
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setLabelColor(Color labelColor) {
        setLabelColorInternal(labelColor);
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弰顖氭儊閺勫墽銇氶崶鍙ョ伐閵?
     *
     * @param showLegend 閺勵垰鎯侀弰鍓с仛閸ュ彞绶?
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弰顖氭儊閺勫墽銇氶弽鍥╊劮閵?
     *
     * @param showLabel 閺勵垰鎯侀弰鍓с仛閺嶅洨顒?
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弰顖氭儊閺勫墽銇氶弽鍥暯閵?
     *
     * @param showTitle 閺勵垰鎯侀弰鍓с仛閺嶅洭顣?
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崶鍙ョ伐鐏炴洜銇氬Ο鈥崇础閵?
     *
     * @param legendDisplayMode 閸ュ彞绶ョ仦鏇犮仛濡€崇础
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setLegendDisplayMode(DisplayMode legendDisplayMode) {
        if (legendDisplayMode == null) {
            throw new PosterException("legendDisplayMode can not be null");
        }
        this.legendDisplayMode = legendDisplayMode;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弽鍥╊劮鐏炴洜銇氬Ο鈥崇础閵?
     *
     * @param labelDisplayMode 閺嶅洨顒风仦鏇犮仛濡€崇础
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setLabelDisplayMode(DisplayMode labelDisplayMode) {
        if (labelDisplayMode == null) {
            throw new PosterException("labelDisplayMode can not be null");
        }
        this.labelDisplayMode = labelDisplayMode;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弽鍥暯鐎涙褰块妴?
     *
     * @param titleFontSize 閺嶅洭顣界€涙褰?
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setTitleFontSize(int titleFontSize) {
        if (titleFontSize <= 0) {
            throw new PosterException("titleFontSize must be greater than 0");
        }
        this.titleFontSize = titleFontSize;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崶鍙ョ伐鐎涙褰块妴?
     *
     * @param legendFontSize 閸ュ彞绶ョ€涙褰?
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setLegendFontSize(int legendFontSize) {
        if (legendFontSize <= 0) {
            throw new PosterException("legendFontSize must be greater than 0");
        }
        this.legendFontSize = legendFontSize;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弽鍥╊劮鐎涙褰块妴?
     *
     * @param labelFontSize 閺嶅洨顒风€涙褰?
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setLabelFontSize(int labelFontSize) {
        if (labelFontSize <= 0) {
            throw new PosterException("labelFontSize must be greater than 0");
        }
        this.labelFontSize = labelFontSize;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崶鍙ョ伐妞ゅ綊妫跨捄婵勨偓?
     *
     * @param legendItemGap 閸ュ彞绶ユい褰掓？鐠?
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setLegendItemGap(int legendItemGap) {
        if (legendItemGap < 0) {
            throw new PosterException("legendItemGap must be greater than or equal to 0");
        }
        this.legendItemGap = legendItemGap;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崶鍙ョ伐閼规彃娼＄亸鍝勵嚟閵?
     *
     * @param legendMarkerSize 閸ュ彞绶ラ懝鎻掓健鐏忓搫顕?
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setLegendMarkerSize(int legendMarkerSize) {
        if (legendMarkerSize <= 0) {
            throw new PosterException("legendMarkerSize must be greater than 0");
        }
        this.legendMarkerSize = legendMarkerSize;
        return this;
    }

    /**
     * 鐠佸墽鐤嗙拫鍐閺夎￥鈧?
     *
     * @param palette 鐠嬪啳澹婇弶?
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setPalette(List<Color> palette) {
        if (palette == null || palette.isEmpty()) {
            throw new PosterException("palette can not be empty");
        }
        this.palette = new ArrayList<Color>(palette);
        return this;
    }

    /**
     * 鐠佸墽鐤嗛梼鑸殿唽闂嗗棗鎮庨妴?
     *
     * @param stages 闂冭埖顔岄梿鍡楁値
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement setStages(List<FunnelChartStage> stages) {
        this.stages.clear();
        if (stages != null) {
            this.stages.addAll(stages);
        }
        return this;
    }

    /**
     * 濞ｈ濮為梼鑸殿唽閵?
     *
     * @param stage 闂冭埖顔岀€电钖?
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement addStage(FunnelChartStage stage) {
        if (stage == null) {
            throw new PosterException("stage can not be null");
        }
        this.stages.add(stage);
        return this;
    }

    /**
     * 濞ｈ濮為梼鑸殿唽閵?
     *
     * @param name  闂冭埖顔岄崥宥囆?
     * @param value 闂冭埖顔岄弫鏉库偓?
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement addStage(String name, Number value) {
        return addStage(FunnelChartStage.of(name, value));
    }

    /**
     * 濞ｈ濮炵敮锕傤杹閼硅尙娈戦梼鑸殿唽閵?
     *
     * @param name  闂冭埖顔岄崥宥囆?
     * @param value 闂冭埖顔岄弫鏉库偓?
     * @param color 闂冭埖顔屾０婊嗗
     * @return 瑜版挸澧犻崗鍐
     */
    public FunnelChartElement addStage(String name, Number value, Color color) {
        return addStage(FunnelChartStage.of(name, value, color));
    }

    /**
     * 閹笛嗩攽閸ユ崘銆冪紒妯哄煑閵?
     *
     * @param context      濞撮攱濮ゆ稉濠佺瑓閺?
     * @param dimension    瑜版挸澧犻崗鍐鐏忓搫顕?
     * @param posterWidth  閻㈣绔风€硅棄瀹?
     * @param posterHeight 閻㈣绔锋妯哄
     * @return 閸忓啰绀屽锔跨瑐鐟欐帒娼楅弽?
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
     * 閺嶏繝鐛欓柊宥囩枂閵?
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
            if (stage.getValue() <= 0D) {
                throw new PosterException("funnel chart requires all stage values to be positive. Invalid stage: " + stage.getName());
            }
        }
    }

    /**
     * 鐟欙絾鐎介崣顖涜閺屾捇妯佸▓鐐光偓?
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
     * 鐟欙絾鐎介梼鑸殿唽妫版粏澹婇妴?
     */
    private Color resolveStageColor(FunnelChartStage stage, int colorIndex) {
        return Optional.ofNullable(stage.getColor()).orElse(palette.get(colorIndex % palette.size()));
    }

    /**
     * 鐟欙絾鐎介崘鍛村劥閸欘垳鏁ょ紒妯哄煑閸栧搫鐓欓妴?
     */

    /**
     * 缂佹ê鍩楅弽鍥暯閵?
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
     * 缂佹ê鍩楅崶鍙ョ伐閵?
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
     * 缂佹ê鍩楅梼鑸殿唽閸栧搫鐓欓妴?
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
     * 閸掓稑缂撳顖氳埌鐠侯垰绶為妴?
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
     * 缂佹ê鍩楅梼鑸殿唽閺嶅洨顒烽妴?
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
     * 缂佹ê鍩楁径鏍劥閺嶅洨顒烽妴?
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
     * 閺嶇厧绱￠崠鏍ㄦ▔缁€鐑樻瀮閺堫兙鈧?
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
     * 閼惧嘲褰囬梼鑸殿唽闂嗗棗鎮庨妴?
     */
    public List<FunnelChartStage> getStages() {
        return Collections.unmodifiableList(stages);
    }

    /**
     * 閼惧嘲褰囪ぐ鎾冲鐠嬪啳澹婇弶瑁も偓?
     */
    public List<Color> getPalette() {
        return Collections.unmodifiableList(palette);
    }

    /**
     * 閸愬懎顔愮仦鏇犮仛濡€崇础閵?
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
     * 闂冭埖顔岀紒妯哄煑娣団剝浼呴妴?
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
