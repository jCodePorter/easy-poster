package com.bytefuture.easy.poster.element.chart;

import com.bytefuture.easy.poster.element.chart.base.AbstractChartElement;
import com.bytefuture.easy.poster.element.chart.base.ChartLayoutBox;
import com.bytefuture.easy.poster.element.chart.base.ChartLegendRenderer;
import com.bytefuture.easy.poster.element.chart.base.NamedColorValue;
import com.bytefuture.easy.poster.exception.PosterException;
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
 * 妤楃厧娴橀崗鍐閵? * <p>
 * 閺€顖涘瘮閺咁噣鈧岸銈奸崶淇扁偓浣哄箚瑜般垹娴樻禒銉ュ挤閻滎偆鎳撻崶?閸楁ぞ绔甸弽鐓庣毜閸ュ彞绗佺粔宥喣佸蹇ョ礉
 * 楠炶埖褰佹笟娑樻禈娓氬鈧焦鐖ｇ粵鎯ф嫲妫版粏澹婇柊宥囩枂閵? * </p>
 *
 * @author biaoy
 * @since 2026/04/13
 */
public class PieChartElement extends AbstractChartElement<PieChartElement> {

    /**
     * 姒涙顓荤拫鍐閺夎￥鈧?     */
    private static final List<Color> DEFAULT_PALETTE = Arrays.asList(
            new Color(72, 133, 237),
            new Color(234, 67, 53),
            new Color(52, 168, 83),
            new Color(251, 188, 5),
            new Color(123, 97, 255),
            new Color(0, 172, 193)
    );

    /**
     * 閸掑洨澧栭梿鍡楁値閵?     */
    private final List<PieChartSlice> slices = new ArrayList<PieChartSlice>();

    /**
     * 閺佹澘鈧吋鐗稿蹇撳閸ｃ劊鈧?     */
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");

    /**
     * 閸ユ崘銆冮弽鍥暯閵?     */
    private String title;

    /**
     * 閺勵垰鎯侀弰鍓с仛閸ュ彞绶ラ妴?     */
    private boolean showLegend = true;

    /**
     * 閺勵垰鎯侀弰鍓с仛閸掑洨澧栭弽鍥╊劮閵?     */
    private boolean showLabel = true;

    /**
     * 閺勵垰鎯侀弰鍓с仛閺嶅洭顣介妴?     */
    private boolean showTitle = true;

    /**
     * 閸ユ崘銆冨Ο鈥崇础閵?     */
    private PieChartMode mode = PieChartMode.PIE;

    /**
     * 閸ュ彞绶ラ崘鍛啇鐏炴洜銇氬Ο鈥崇础閵?     */
    private DisplayMode legendDisplayMode = DisplayMode.NAME;

    /**
     * 閸掑洨澧栭弽鍥╊劮閸愬懎顔愮仦鏇犮仛濡€崇础閵?     */
    private DisplayMode labelDisplayMode = DisplayMode.NAME_PERCENT;

    /**
     * 閼奉亜鐣炬稊澶庣殶閼瑰弶婢橀妴?     */
    private List<Color> palette = new ArrayList<Color>(DEFAULT_PALETTE);

    /**
     * 閺嶅洭顣界€涙褰块妴?     */
    private int titleFontSize = 18;

    /**
     * 閸ュ彞绶ョ€涙褰块妴?     */
    private int legendFontSize = 12;

    /**
     * 閺嶅洨顒风€涙褰块妴?     */
    private int labelFontSize = 12;

    /**
     * 閸ュ彞绶ユい閫涚闂傚娈戦梻纾嬬獩閵?     */
    private int legendItemGap = 18;

    /**
     * 閸ュ彞绶ラ懝鎻掓健鐏忓搫顕妴?     */
    private int legendMarkerSize = 10;

    /**
     * 姒涙顓荤挧宄邦潗鐟欐帒瀹抽妴?     */
    private double startAngle = -90D;

    /**
     * 閻滎垰鑸伴崶鎯у敶瀵板嫭鐦笟瀣ㄢ偓?     */
    private double donutInnerRadiusRatio = 0.58D;

    /**
     * 閻滎偆鎳撻崶鐐付鐏忓繐宕愬鍕槷娓氬鈧?     */
    private double roseInnerRadiusRatio = 0.30D;

    /**
     * 閺嶅洨顒烽張鈧亸蹇氼潡鎼达箓妲囬崐绗衡偓?     */
    private double minLabelAngle = 12D;

    /**
     * 閺嶅洨顒烽張鈧亸蹇曞箚鐎逛粙妲囬崐绗衡偓?     */
    private int minLabelBand = 18;

    /**
     * 閺嬪嫰鈧姴娴樼悰銊ュ帗缁辩姰鈧?     *
     * @param width  閸忓啰绀岀€硅棄瀹?
     * @param height 閸忓啰绀屾妯哄
     */
    public PieChartElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 鐠佸墽鐤嗛崶鎹愩€冮弽鍥暯閵?     *
     * @param title 閸ユ崘銆冮弽鍥暯
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崶鎹愩€冮崘鍛扮珶鐠烘縿鈧?     *
     * @param padding 閸ユ崘銆冮崘鍛扮珶鐠?     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setPadding(Insets padding) {
        if (padding == null) {
            throw new PosterException("padding can not be null");
        }
        setPaddingInternal(padding);
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崶鎹愩€冮懗灞炬珯閼瑰眰鈧?     *
     * @param backgroundColor 閼冲本娅欓懝?     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setBackgroundColor(Color backgroundColor) {
        setBackgroundColorInternal(backgroundColor);
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弽鍥╊劮妫版粏澹婇妴?     *
     * @param labelColor 閺嶅洨顒锋０婊嗗
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setLabelColor(Color labelColor) {
        setLabelColorInternal(labelColor);
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弰顖氭儊閺勫墽銇氶崶鍙ョ伐閵?     *
     * @param showLegend 閺勵垰鎯侀弰鍓с仛閸ュ彞绶?
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弰顖氭儊閺勫墽銇氶弽鍥╊劮閵?     *
     * @param showLabel 閺勵垰鎯侀弰鍓с仛閺嶅洨顒?
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弰顖氭儊閺勫墽銇氶弽鍥暯閵?     *
     * @param showTitle 閺勵垰鎯侀弰鍓с仛閺嶅洭顣?
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崶鎹愩€冨Ο鈥崇础閵?     *
     * @param mode 閸ユ崘銆冨Ο鈥崇础
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setMode(PieChartMode mode) {
        if (mode == null) {
            throw new PosterException("mode can not be null");
        }
        this.mode = mode;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崶鍙ョ伐鐏炴洜銇氬Ο鈥崇础閵?     *
     * @param legendDisplayMode 閸ュ彞绶ョ仦鏇犮仛濡€崇础
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setLegendDisplayMode(DisplayMode legendDisplayMode) {
        if (legendDisplayMode == null) {
            throw new PosterException("legendDisplayMode can not be null");
        }
        this.legendDisplayMode = legendDisplayMode;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弽鍥╊劮鐏炴洜銇氬Ο鈥崇础閵?     *
     * @param labelDisplayMode 閺嶅洨顒风仦鏇犮仛濡€崇础
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setLabelDisplayMode(DisplayMode labelDisplayMode) {
        if (labelDisplayMode == null) {
            throw new PosterException("labelDisplayMode can not be null");
        }
        this.labelDisplayMode = labelDisplayMode;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弽鍥暯鐎涙褰块妴?     *
     * @param titleFontSize 閺嶅洭顣界€涙褰?
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setTitleFontSize(int titleFontSize) {
        if (titleFontSize <= 0) {
            throw new PosterException("titleFontSize must be greater than 0");
        }
        this.titleFontSize = titleFontSize;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崶鍙ョ伐鐎涙褰块妴?     *
     * @param legendFontSize 閸ュ彞绶ョ€涙褰?
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setLegendFontSize(int legendFontSize) {
        if (legendFontSize <= 0) {
            throw new PosterException("legendFontSize must be greater than 0");
        }
        this.legendFontSize = legendFontSize;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弽鍥╊劮鐎涙褰块妴?     *
     * @param labelFontSize 閺嶅洨顒风€涙褰?
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setLabelFontSize(int labelFontSize) {
        if (labelFontSize <= 0) {
            throw new PosterException("labelFontSize must be greater than 0");
        }
        this.labelFontSize = labelFontSize;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崶鍙ョ伐妞ゅ綊妫跨捄婵勨偓?     *
     * @param legendItemGap 閸ュ彞绶ユい褰掓？鐠?     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setLegendItemGap(int legendItemGap) {
        if (legendItemGap < 0) {
            throw new PosterException("legendItemGap must be greater than or equal to 0");
        }
        this.legendItemGap = legendItemGap;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崶鍙ョ伐閼规彃娼＄亸鍝勵嚟閵?     *
     * @param legendMarkerSize 閸ュ彞绶ラ懝鎻掓健鐏忓搫顕?
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setLegendMarkerSize(int legendMarkerSize) {
        if (legendMarkerSize <= 0) {
            throw new PosterException("legendMarkerSize must be greater than 0");
        }
        this.legendMarkerSize = legendMarkerSize;
        return this;
    }

    /**
     * 鐠佸墽鐤嗙挧宄邦潗鐟欐帒瀹抽妴?     *
     * @param startAngle 鐠у嘲顫愮憴鎺戝
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setStartAngle(double startAngle) {
        this.startAngle = startAngle;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛悳顖氳埌閸ユ儳鍞村鍕槷娓氬鈧?     *
     * @param donutInnerRadiusRatio 閻滎垰鑸伴崶鎯у敶瀵板嫭鐦笟?     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setDonutInnerRadiusRatio(double donutInnerRadiusRatio) {
        this.donutInnerRadiusRatio = donutInnerRadiusRatio;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛悳顐ゆ嚀閸ョ偓娓剁亸蹇撳磹瀵板嫭鐦笟瀣ㄢ偓?     *
     * @param roseInnerRadiusRatio 閻滎偆鎳撻崶鐐付鐏忓繐宕愬鍕槷娓?     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setRoseInnerRadiusRatio(double roseInnerRadiusRatio) {
        this.roseInnerRadiusRatio = roseInnerRadiusRatio;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弽鍥╊劮閺堚偓鐏忓繗顫楁惔锕傛閸婄鈧?     *
     * @param minLabelAngle 閺嶅洨顒烽張鈧亸蹇氼潡鎼达箓妲囬崐?     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setMinLabelAngle(double minLabelAngle) {
        if (minLabelAngle < 0D) {
            throw new PosterException("minLabelAngle must be greater than or equal to 0");
        }
        this.minLabelAngle = minLabelAngle;
        return this;
    }

    /**
     * 鐠佸墽鐤嗛弽鍥╊劮閺堚偓鐏忓繒骞嗙€逛粙妲囬崐绗衡偓?     *
     * @param minLabelBand 閺嶅洨顒烽張鈧亸蹇曞箚鐎逛粙妲囬崐?     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setMinLabelBand(int minLabelBand) {
        if (minLabelBand <= 0) {
            throw new PosterException("minLabelBand must be greater than 0");
        }
        this.minLabelBand = minLabelBand;
        return this;
    }

    /**
     * 鐠佸墽鐤嗙拫鍐閺夎￥鈧?     *
     * @param palette 鐠嬪啳澹婇弶?     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setPalette(List<Color> palette) {
        if (palette == null || palette.isEmpty()) {
            throw new PosterException("palette can not be empty");
        }
        this.palette = new ArrayList<Color>(palette);
        return this;
    }

    /**
     * 鐠佸墽鐤嗛崚鍥╁闂嗗棗鎮庨妴?     *
     * @param slices 閸掑洨澧栭梿鍡楁値
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement setSlices(List<PieChartSlice> slices) {
        this.slices.clear();
        if (slices != null) {
            this.slices.addAll(slices);
        }
        return this;
    }

    /**
     * 濞ｈ濮為崚鍥╁閵?     *
     * @param slice 閸掑洨澧栫€电钖?
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement addSlice(PieChartSlice slice) {
        if (slice == null) {
            throw new PosterException("slice can not be null");
        }
        this.slices.add(slice);
        return this;
    }

    /**
     * 濞ｈ濮為崚鍥╁閵?     *
     * @param name  閸掑洨澧栭崥宥囆?
     * @param value 閸掑洨澧栭弫鏉库偓?     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement addSlice(String name, Number value) {
        return addSlice(PieChartSlice.of(name, value));
    }

    /**
     * 濞ｈ濮炵敮锕傤杹閼硅尙娈戦崚鍥╁閵?     *
     * @param name  閸掑洨澧栭崥宥囆?
     * @param value 閸掑洨澧栭弫鏉库偓?     * @param color 閸掑洨澧栨０婊嗗
     * @return 瑜版挸澧犻崗鍐
     */
    public PieChartElement addSlice(String name, Number value, Color color) {
        return addSlice(PieChartSlice.of(name, value, color));
    }

    /**
     * 閹笛嗩攽閸ユ崘銆冪紒妯哄煑閵?     *
     * @param context      濞撮攱濮ゆ稉濠佺瑓閺?     * @param dimension    瑜版挸澧犻崗鍐鐏忓搫顕?
     * @param posterWidth  閻㈣绔风€硅棄瀹?
     * @param posterHeight 閻㈣绔锋妯哄
     * @return 閸忓啰绀屽锔跨瑐鐟欐帒娼楅弽?     */
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
     * 閺嶏繝鐛欓柊宥囩枂閵?     */
    @Override
    protected void validateChartData() {
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
     * 鐟欙絾鐎介崣顖滅帛閸掕泛鍨忛悧鍥モ偓?     * <p>
     * 妫ｆ牜澧楃€靛綊娼锝嗘殶閸掑洨澧栭柌鍥╂暏鐠哄疇绻冪粵鏍殣閿涘奔绮庣紒妯哄煑濮濓絾鏆熼崐鍏兼殶閹诡喓鈧?     * </p>
     *
     * @return 閸欘垳绮崚璺哄瀼閻楀洭娉﹂崥?     */
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
     * 鐟欙絾鐎介崚鍥╁妫版粏澹婇妴?     *
     * @param slice      閸掑洨澧栫€电钖?
     * @param colorIndex 鐠嬪啳澹婇弶璺ㄥ偍瀵?     * @return 閺堚偓缂佸牓顤侀懝?     */
    private Color resolveSliceColor(PieChartSlice slice, int colorIndex) {
        return Optional.ofNullable(slice.getColor()).orElse(palette.get(colorIndex % palette.size()));
    }

    /**
     * 鐟欙絾鐎介崘鍛村劥閸欘垳鏁ょ紒妯哄煑閸栧搫鐓欓妴?     *
     * @param origin 閸忓啰绀岄崢鐔哄仯
     * @return 閸愬懘鍎寸敮鍐ㄧ湰閸栧搫鐓?
     */

    /**
     * 缂佹ê鍩楅弽鍥暯閵?     *
     * @param g         閻㈣崵鐟?
     * @param innerBox  閸愬懘鍎寸敮鍐ㄧ湰閸栧搫鐓?
     * @param titleFont 閺嶅洭顣界€涙ぞ缍?
     * @return 閸楃姷鏁ら惃鍕彯鎼?     */
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
     * 缂佹ê鍩楅崶鍙ョ伐閵?     *
     * @param g            閻㈣崵鐟?
     * @param innerBox     閸愬懘鍎寸敮鍐ㄧ湰閸栧搫鐓?
     * @param legendFont   閸ュ彞绶ョ€涙ぞ缍?
     * @param drawableData 閸欘垳绮崚璺哄瀼閻楀洦鏆熼幑?     * @return 閸楃姷鏁ら惃鍕彯鎼?     */
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
     * 缂佹ê鍩楅崚鍥╁閸栧搫鐓欓妴?     *
     * @param g              閻㈣崵鐟?
     * @param innerBox       閸愬懘鍎寸敮鍐ㄧ湰閸栧搫鐓?
     * @param drawableSlices 閸欘垳绮崚璺哄瀼閻?     * @param labelFont      閺嶅洨顒风€涙ぞ缍?
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
     * 鐟欙絾鐎介崶鎯ц埌缂佹ê鍩楅崠鍝勭厵閵?     *
     * @param innerBox 閸愬懘鍎撮崠鍝勭厵
     * @return 閸ユ儳鑸扮紒妯哄煑閸栧搫鐓?
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
     * 鐟欙絾鐎介崺铏诡攨閸愬懎宕愬鍕┾偓?     *
     * @param maxOuterRadius 閺堚偓婢堆冾樆閸楀﹤绶?
     * @return 閸╄櫣顢呴崘鍛磹瀵?     */
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
     * 鐟欙絾鐎介崚鍥╁婢舵牕宕愬鍕┾偓?     *
     * @param sliceInfo      閸掑洨澧栨穱鈩冧紖
     * @param maxOuterRadius 閺堚偓婢堆冾樆閸楀﹤绶?
     * @return 閸掑洨澧栨径鏍у磹瀵?     */
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
     * 閸掓稑缂撻崚鍥╁瑜般垻濮搁妴?     *
     * @param centerX     閸﹀棗绺?X
     * @param centerY     閸﹀棗绺?Y
     * @param innerRadius 閸愬懎宕愬?     * @param outerRadius 婢舵牕宕愬?     * @param start       鐠у嘲顫愮憴鎺戝
     * @param extent      閹碘晛鐫嶇憴鎺戝
     * @return 閸掑洨澧栬ぐ銏㈠Ц
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
     * 缂佹ê鍩楅崚鍥╁閺嶅洨顒烽妴?     *
     * @param g           閻㈣崵鐟?
     * @param labelFont   閺嶅洨顒风€涙ぞ缍?
     * @param sliceInfo   閸掑洨澧栨穱鈩冧紖
     * @param centerX     閸﹀棗绺?X
     * @param centerY     閸﹀棗绺?Y
     * @param innerRadius 閸愬懎宕愬?     * @param outerRadius 婢舵牕宕愬?     * @param start       鐠у嘲顫愮憴鎺戝
     * @param extent      閹碘晛鐫嶇憴鎺戝
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
     * 閸掋倖鏌囬弽鍥╊劮閺勵垰鎯侀崣顖欎簰缂佹ê鍩楅妴?     *
     * @param metrics     鐎涙ぞ缍嬫惔锕傚櫤
     * @param text        閺嶅洨顒烽弬鍥ㄦ拱
     * @param innerRadius 閸愬懎宕愬?     * @param outerRadius 婢舵牕宕愬?     * @param extent      閹碘晛鐫嶇憴鎺戝
     * @return 閺勵垰鎯侀崣顖欎簰缂佹ê鍩?
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
     * 闁瀚ㄩ弰鎾诡嚢閻ㄥ嫭鐖ｇ粵楣冾杹閼瑰眰鈧?     *
     * @param background 閼冲本娅欐０婊嗗
     * @return 閺嶅洨顒锋０婊嗗
     */

    /**
     * 閺嶇厧绱￠崠鏍ㄦ▔缁€鐑樻瀮閺堫兙鈧?     *
     * @param sliceInfo    閸掑洨澧栨穱鈩冧紖
     * @param displayMode  鐏炴洜銇氬Ο鈥崇础
     * @return 閺勫墽銇氶弬鍥ㄦ拱
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
     * 閼惧嘲褰囬崚鍥╁闂嗗棗鎮庨妴?     *
     * @return 閸掑洨澧栭梿鍡楁値
     */
    public List<PieChartSlice> getSlices() {
        return Collections.unmodifiableList(slices);
    }

    /**
     * 閼惧嘲褰囪ぐ鎾冲鐠嬪啳澹婇弶瑁も偓?     *
     * @return 瑜版挸澧犵拫鍐閺?     */
    public List<Color> getPalette() {
        return Collections.unmodifiableList(palette);
    }

    /**
     * 妤楃厧娴樺Ο鈥崇础閵?     */
    @Getter
    @AllArgsConstructor
    public enum PieChartMode {
        /**
         * 閺咁噣鈧岸銈奸崶淇扁偓?         */
        PIE("Pie"),
        /**
         * 閻滎垰鑸伴崶淇扁偓?         */
        DONUT("Donut"),
        /**
         * 閻滎偆鎳撻崶?閸楁ぞ绔甸弽鐓庣毜閸ヤ勘鈧?         */
        ROSE("Rose");

        /**
         * 濡€崇础閹诲繗鍫妴?         */
        private final String desc;
    }

    /**
     * 閸愬懎顔愮仦鏇犮仛濡€崇础閵?     */
    @Getter
    @AllArgsConstructor
    public enum DisplayMode {
        /**
         * 娴犲懎鎮曠粔鑸偓?         */
        NAME("Name"),
        /**
         * 娴犲懏鏆熼崐绗衡偓?         */
        VALUE("Value"),
        /**
         * 娴犲懐娅ㄩ崚鍡樼槷閵?         */
        PERCENT("Percent"),
        /**
         * 閸氬秶袨閸旂姵鏆熼崐绗衡偓?         */
        NAME_VALUE("Name+Value"),
        /**
         * 閸氬秶袨閸旂姷娅ㄩ崚鍡樼槷閵?         */
        NAME_PERCENT("Name+Percent");

        /**
         * 濡€崇础閹诲繗鍫妴?         */
        private final String desc;
    }


    /**
     * 閸掑洨澧栫紒妯哄煑娣団剝浼呴妴?     */
    private static class SliceRenderInfo {

        /**
         * 閸樼喎顫愰崚鍥╁閵?         */
        private final PieChartSlice slice;

        /**
         * 鐟欙絾鐎介崥搴ｆ畱妫版粏澹婇妴?         */
        private final Color color;

        /**
         * 瑜版挸澧犻崚鍥╁閻ф儳鍨庡В鏂烩偓?         */
        private double percent;

        /**
         * 瑜版挸澧犻弫鐗堝祦闂嗗棙娓舵径褍鈧鈧?         */
        private double maxValue;

        /**
         * 閺嬪嫰鈧姴鍨忛悧鍥╃帛閸掓湹淇婇幁顖樷偓?         *
         * @param slice 閸樼喎顫愰崚鍥╁
         * @param color 鐟欙絾鐎介崥搴ｆ畱妫版粏澹?
         */
        private SliceRenderInfo(PieChartSlice slice, Color color) {
            this.slice = slice;
            this.color = color;
        }
    }
}
