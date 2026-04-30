package com.bytefuture.easy.poster.func.showcase;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.advance.ComposeElement;
import com.bytefuture.easy.poster.element.advance.RepeatElement;
import com.bytefuture.easy.poster.element.basic.CircleElement;
import com.bytefuture.easy.poster.element.basic.LineElement;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.element.chart.FunnelChartElement;
import com.bytefuture.easy.poster.element.special.QrCodeElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextSpan;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 复杂示例图测试构建工具
 *
 * @author Codex
 * @since 2026/04/29
 */
public final class ComplexShowcaseFixtures {

    /**
     * 活动海报强调色
     */
    public static final Color EVENT_ACCENT = new Color(255, 196, 92);

    /**
     * 活动海报次强调色
     */
    public static final Color EVENT_SECONDARY = new Color(255, 119, 87);

    /**
     * 看板主强调色
     */
    public static final Color DASHBOARD_PRIMARY = new Color(72, 133, 237);

    /**
     * 看板次强调色
     */
    public static final Color DASHBOARD_SECONDARY = new Color(52, 168, 83);

    private ComplexShowcaseFixtures() {
    }

    /**
     * 渲染活动邀请示例图
     *
     * @return 渲染后的图片
     * @throws IOException 图片读取异常
     */
    public static BufferedImage renderEventInviteShowcase() throws IOException {
        return renderPoster(createEventInvitePoster());
    }

    /**
     * 渲染运营看板示例图
     *
     * @return 渲染后的图片
     * @throws IOException 图片读取异常
     */
    public static BufferedImage renderDashboardShowcase() throws IOException {
        return renderPoster(createDashboardPoster());
    }

    /**
     * 输出活动邀请示例图
     *
     * @param filePath 输出路径
     */
    public static void writeEventInviteShowcase(String filePath) {
        createEventInvitePoster().asFile("png", filePath);
    }

    /**
     * 输出运营看板示例图
     *
     * @param filePath 输出路径
     */
    public static void writeDashboardShowcase(String filePath) {
        createDashboardPoster().asFile("png", filePath);
    }

    /**
     * 统计近似颜色像素数量
     *
     * @param image 图片
     * @param target 目标颜色
     * @param tolerance 容差
     * @return 像素数量
     */
    public static int countColorLikePixels(BufferedImage image, Color target, int tolerance) {
        int count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color current = new Color(image.getRGB(x, y), true);
                if (current.getAlpha() == 0) {
                    continue;
                }
                if (Math.abs(current.getRed() - target.getRed()) <= tolerance
                        && Math.abs(current.getGreen() - target.getGreen()) <= tolerance
                        && Math.abs(current.getBlue() - target.getBlue()) <= tolerance) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 创建活动邀请海报
     *
     * @return 海报对象
     */
    private static EasyPoster createEventInvitePoster() {
        EasyPoster poster = createPoster(1400, 1880, new Color(248, 239, 228));

        poster.addElement(new RectangleElement(1400, 540)
                .setColor(new Color(31, 38, 57))
                .setPosition(RelativePosition.of(Direction.TOP_LEFT)));

        poster.addElement(new RectangleElement(1240, 1540)
                .setColor(new Color(255, 252, 246))
                .setArc(44)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(80, 280, 0, 0))));

        poster.addElement(new RectangleElement(1080, 250)
                .setColor(new Color(255, 255, 255))
                .setAlpha(0.06F)
                .setArc(120)
                .setRotate(-6)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(220, 94, 0, 0))));

        poster.addElement(new CircleElement(220)
                .setColor(EVENT_SECONDARY)
                .setAlpha(0.12F)
                .setPosition(RelativePosition.of(Direction.TOP_RIGHT, Margin.of(0, 86, 110, 0))));

        poster.addElement(new CircleElement(108)
                .setColor(EVENT_ACCENT)
                .setAlpha(0.18F)
                .setPosition(RelativePosition.of(Direction.TOP_RIGHT, Margin.of(0, 210, 290, 0))));

        poster.addElement(new RectangleElement(146, 10)
                .setColor(EVENT_ACCENT)
                .setArc(10)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(96, 214, 0, 0))));

        poster.addElement(new RepeatElement(
                new CircleElement(14)
                        .setColor(new Color(255, 255, 255))
                        .setAlpha(0.15F)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .setLayout(8, 2, Margin.of(122, 116, 122, 54)));

        poster.addElement(new RepeatElement(
                new RectangleElement(56, 12)
                        .setArc(10)
                        .setColor(EVENT_ACCENT)
                        .setAlpha(0.28F)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .setLayout(4, 2, Margin.of(1110, 1210, 120, 144)));

        poster.addElement(buildBrandLockup().setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(96, 84, 0, 0))));
        poster.addElement(buildEventHero());
        poster.addElement(buildSpeakerCard().setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(96, 980, 0, 0))));
        poster.addElement(com.bytefuture.easy.poster.element.v2.TextElement.of(
                        TextSpan.of("Agenda ")
                                .setColor(new Color(40, 42, 50)),
                        TextSpan.of("Moments")
                                .setBackgroundColor(new Color(255, 232, 210))
                                .setBackgroundPadding(7)
                                .setBackgroundRadius(12)
                                .setFontStyle(Font.BOLD))
                .setFontName("Dialog")
                .setFontStyle(Font.BOLD)
                .setFontSize(28)
                .setColor(new Color(45, 46, 55))
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(780, 980, 0, 0))));
        poster.addElement(new RepeatElement(buildAgendaCard())
                .setLayout(2, 2, Margin.of(780, 1040, 80, 420))
                .setInterval(20, 20));

        poster.addElement(buildQrPanel().setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(860, 1520, 0, 0))));

        poster.addElement(new LineElement(Point.of(96, 1490), Point.of(1304, 1490))
                .setColor(new Color(227, 216, 201))
                .setBorderSize(2));

        poster.addElement(com.bytefuture.easy.poster.element.v2.TextElement.of(
                        TextSpan.of("Presented by "),
                        TextSpan.of("Easy Poster Lab")
                                .setColor(EVENT_SECONDARY)
                                .setFontStyle(Font.BOLD))
                .setFontName("Dialog")
                .setFontSize(24)
                .setColor(new Color(114, 100, 86))
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(96, 1540, 0, 0))));

        poster.addElement(com.bytefuture.easy.poster.element.v2.TextElement.of("Shanghai | 2026.06.18 | Limited 180 seats")
                .setFontName("Dialog")
                .setFontSize(24)
                .setColor(new Color(114, 100, 86))
                .setLetterSpacing(2)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(96, 1750, 0, 0))));
        return poster;
    }

    /**
     * 创建运营看板海报
     *
     * @return 海报对象
     */
    private static EasyPoster createDashboardPoster() {
        EasyPoster poster = createPoster(1600, 1860, new Color(237, 243, 250));

        poster.addElement(new RectangleElement(1600, 260)
                .setColor(new Color(16, 24, 40))
                .setPosition(RelativePosition.of(Direction.TOP_LEFT)));

        poster.addElement(new RectangleElement(1440, 1480)
                .setColor(new Color(248, 251, 255))
                .setArc(40)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(80, 250, 0, 0))));

        poster.addElement(new RectangleElement(1180, 150)
                .setColor(new Color(255, 255, 255))
                .setAlpha(0.05F)
                .setArc(90)
                .setRotate(-4)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(250, 74, 0, 0))));

        poster.addElement(new CircleElement(180)
                .setColor(new Color(94, 205, 177))
                .setAlpha(0.10F)
                .setPosition(RelativePosition.of(Direction.TOP_RIGHT, Margin.of(0, 98, 124, 0))));

        poster.addElement(new RepeatElement(
                new CircleElement(10)
                        .setColor(Color.WHITE)
                        .setAlpha(0.18F)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .setLayout(10, 2, Margin.of(110, 70, 110, 70)));

        poster.addElement(buildDashboardHeader().setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(96, 66, 0, 0))));
        poster.addElement(buildSummaryPanel().setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(96, 270, 0, 0))));

        poster.addElement(new RectangleElement(792, 462)
                .setColor(new Color(255, 255, 255))
                .setArc(34)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(80, 454, 0, 0))));
        poster.addElement(new RectangleElement(472, 462)
                .setColor(new Color(255, 255, 255))
                .setArc(34)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(1032, 454, 0, 0))));

        poster.addLineChartElement(760, 430)
                .setTitle("7-Day Traffic Trend")
                .setBackgroundColor(new Color(255, 255, 255))
                .setCategories(Arrays.asList("04-23", "04-24", "04-25", "04-26", "04-27", "04-28", "04-29"))
                .addSeries("Impression", Arrays.asList(128, 166, 188, 212, 226, 248, 272), DASHBOARD_PRIMARY)
                .addSeries("Conversion", Arrays.asList(22, 28, 31, 38, 44, 47, 54), DASHBOARD_SECONDARY)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(96, 470, 0, 0)));

        poster.addFunnelChartElement(440, 430)
                .setTitle("Campaign Funnel")
                .setBackgroundColor(new Color(255, 255, 255))
                .setLegendDisplayMode(FunnelChartElement.DisplayMode.NAME_VALUE)
                .setLabelDisplayMode(FunnelChartElement.DisplayMode.NAME_PERCENT)
                .addStage("Exposure", 184000, DASHBOARD_PRIMARY)
                .addStage("Click", 26300, new Color(98, 176, 255))
                .addStage("Lead", 5240, new Color(94, 205, 177))
                .addStage("Deal", 860, new Color(255, 180, 92))
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(1048, 470, 0, 0)));

        poster.addElement(com.bytefuture.easy.poster.element.v2.TextElement.of(
                        TextSpan.of("KPI "),
                        TextSpan.of("Pulse")
                                .setBackgroundColor(new Color(218, 235, 255))
                                .setBackgroundPadding(7)
                                .setBackgroundRadius(12)
                                .setFontStyle(Font.BOLD))
                .setFontName("Dialog")
                .setFontStyle(Font.BOLD)
                .setFontSize(28)
                .setColor(new Color(38, 48, 66))
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(96, 980, 0, 0))));
        poster.addElement(new RepeatElement(buildKpiCard())
                .setLayout(2, 2, Margin.of(96, 1038, 392, 620)));
        poster.addElement(buildInsightPanel().setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(96, 1450, 0, 0))));
        return poster;
    }

    /**
     * 渲染海报为图片
     *
     * @param poster 海报对象
     * @return 图片
     * @throws IOException 图片读取异常
     */
    private static BufferedImage renderPoster(EasyPoster poster) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(poster.asBytes("png")));
    }

    /**
     * 创建基础海报
     *
     * @param width 宽度
     * @param height 高度
     * @param defaultColor 默认文字颜色
     * @return 海报对象
     */
    private static EasyPoster createPoster(int width, int height, Color defaultColor) {
        EasyPoster poster = new EasyPoster(width, height);
        poster.getConfig().setFontName("Dialog");
        poster.getConfig().setFontSize(18);
        poster.getConfig().setColor(defaultColor);
        return poster;
    }

    /**
     * 构建品牌区
     *
     * @return 组合元素
     */
    private static ComposeElement buildBrandLockup() {
        return ComposeElement.of(new CircleElement(22)
                        .setColor(EVENT_ACCENT)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .right(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("EASY ")
                                        .setColor(Color.WHITE)
                                        .setFontStyle(Font.BOLD),
                                TextSpan.of("POSTER")
                                        .setColor(EVENT_ACCENT)
                                        .setFontStyle(Font.BOLD))
                        .setFontSize(34)
                        .setPosition(RelativePosition.of(Direction.LEFT_CENTER, Margin.of(20, 0, 0, 0))))
                .bottom(com.bytefuture.easy.poster.element.v2.TextElement.of("Product Launch Session 2026")
                        .setFontSize(18)
                        .setColor(new Color(214, 221, 236))
                        .setLetterSpacing(3)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(0, 18, 0, 0))));
    }

    /**
     * 构建活动主视觉区
     *
     * @return 组合元素
     */
    private static ComposeElement buildEventHero() {
        return ComposeElement.of(new RectangleElement(1240, 580)
                        .setColor(new Color(255, 252, 246))
                        .setArc(44)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(80, 300, 0, 0))))
                .in(new RectangleElement(470, 14)
                        .setColor(EVENT_ACCENT)
                        .setArc(10)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(72, 36, 0, 0))))
                .in(new CircleElement(150)
                        .setColor(new Color(255, 236, 206))
                        .setPosition(RelativePosition.of(Direction.TOP_RIGHT, Margin.of(0, 42, 82, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("NEXT "),
                                TextSpan.of("WORKFLOWS")
                                        .setFontStyle(Font.BOLD)
                                        .setBackgroundColor(EVENT_ACCENT)
                                        .setBackgroundPadding(12)
                                        .setBackgroundRadius(20))
                        .setFontSize(92)
                        .setFontStyle(Font.BOLD)
                        .setColor(new Color(36, 37, 43))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(72, 54, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("Launch assets that feel editorial, modular and conversion-ready")
                                        .setColor(new Color(82, 82, 92)),
                                TextSpan.of("  /  ")
                                        .setColor(new Color(150, 150, 160)),
                                TextSpan.of("Compose + Repeat + Rich Text")
                                        .setColor(EVENT_SECONDARY)
                                        .setFontStyle(Font.BOLD))
                        .setFontName("Dialog")
                        .setFontSize(26)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(76, 188, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("06.18")
                                        .setFontStyle(Font.BOLD)
                                        .setBackgroundColor(new Color(31, 38, 57))
                                        .setBackgroundPadding(12)
                                        .setBackgroundRadius(16)
                                        .setColor(Color.WHITE),
                                TextSpan.of("  Shanghai Innovation Port")
                                        .setColor(new Color(31, 38, 57))
                                        .setFontStyle(Font.BOLD))
                        .setFontName("Dialog")
                        .setFontSize(30)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(76, 278, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("Editorial launch system")
                                        .setBackgroundColor(new Color(255, 236, 214))
                                        .setBackgroundPadding(7)
                                        .setBackgroundRadius(12),
                                TextSpan.of("  speaker deck  "),
                                TextSpan.of("agenda rhythm")
                                        .setBackgroundColor(new Color(255, 229, 169))
                                        .setBackgroundPadding(7)
                                        .setBackgroundRadius(12))
                        .setFontName("Dialog")
                        .setFontSize(22)
                        .setColor(new Color(74, 74, 84))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(76, 356, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of("Built as a single poster story: hero statement, timed agenda, speaker identity and a conversion-ready signup zone all live inside one bright editorial composition")
                        .setFontName("Dialog")
                        .setFontSize(29)
                        .setColor(new Color(88, 79, 72))
                        .setAutoWordWrap(640)
                        .setLineHeight(46)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(76, 410, 0, 0))))
                .in(new CircleElement(88)
                        .setColor(EVENT_SECONDARY)
                        .setAlpha(0.18F)
                        .setPosition(RelativePosition.of(Direction.TOP_RIGHT, Margin.of(0, 226, 190, 0))))
                .in(new RectangleElement(356, 392)
                        .setColor(new Color(255, 244, 233))
                        .setArc(36)
                        .setPosition(RelativePosition.of(Direction.TOP_RIGHT, Margin.of(0, 252, 72, 0))))
                .in(new RectangleElement(92, 12)
                        .setColor(EVENT_SECONDARY)
                        .setArc(12)
                        .setPosition(RelativePosition.of(Direction.TOP_RIGHT, Margin.of(0, 282, 300, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("Seats left")
                                        .setColor(new Color(115, 95, 74)),
                                TextSpan.of("\n180")
                                        .setColor(new Color(36, 37, 43))
                                        .setFontStyle(Font.BOLD)
                                        .setFontSize(94))
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setLineHeight(84)
                        .setPosition(RelativePosition.of(Direction.TOP_RIGHT, Margin.of(0, 302, 176, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of("Open workshop,\nlive build session\nand operator review")
                        .setFontName("Dialog")
                        .setFontSize(24)
                        .setColor(new Color(102, 94, 88))
                        .setLineHeight(36)
                        .setPosition(RelativePosition.of(Direction.TOP_RIGHT, Margin.of(0, 484, 112, 0))));
    }

    /**
     * 构建讲师卡片
     *
     * @return 组合元素
     */
    private static ComposeElement buildSpeakerCard() {
        return ComposeElement.of(new RectangleElement(520, 420)
                        .setColor(new Color(31, 38, 57))
                        .setArc(36)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .in(new RectangleElement(440, 10)
                        .setColor(EVENT_ACCENT)
                        .setArc(10)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(40, 30, 0, 0))))
                .in(new CircleElement(82)
                        .setColor(EVENT_ACCENT)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(40, 52, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("Annie Xu")
                                        .setFontStyle(Font.BOLD)
                                        .setColor(Color.WHITE),
                                TextSpan.of("\nProduct Narrative Director")
                                        .setColor(new Color(216, 221, 233)))
                        .setFontName("Dialog")
                        .setFontSize(36)
                        .setLineHeight(44)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(68, 78, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of("Shapes launch systems for product stories, campaign rhythm and editorial conversion across launch assets")
                        .setFontName("Dialog")
                        .setFontSize(22)
                        .setColor(new Color(226, 230, 240))
                        .setAutoWordWrap(420)
                        .setLineHeight(34)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(40, 206, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("Talk")
                                        .setBackgroundColor(EVENT_ACCENT)
                                        .setBackgroundPadding(6)
                                        .setBackgroundRadius(10)
                                        .setFontStyle(Font.BOLD)
                                        .setColor(new Color(35, 35, 42)),
                                TextSpan.of("  One poster system for launch and conversion")
                                        .setColor(Color.WHITE))
                        .setFontName("Dialog")
                        .setFontSize(20)
                        .setAutoWordWrap(420)
                        .setLineHeight(30)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(40, 334, 0, 0))));
    }

    /**
     * 构建议程平铺区
     *
     * @return 组合元素
     */
    private static ComposeElement buildAgendaCard() {
        return ComposeElement.of(new RectangleElement(220, 190)
                        .setColor(new Color(255, 248, 240))
                        .setArc(28)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .in(new RectangleElement(220, 12)
                        .setColor(new Color(255, 229, 169))
                        .setArc(12)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("09:30")
                                        .setBackgroundColor(new Color(255, 236, 206))
                                        .setBackgroundPadding(6)
                                        .setBackgroundRadius(10)
                                        .setFontStyle(Font.BOLD))
                        .setFontName("Dialog")
                        .setFontSize(24)
                        .setColor(new Color(39, 39, 46))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(18, 26, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of("Editorial Sprint")
                        .setFontName("Dialog")
                        .setFontStyle(Font.BOLD)
                        .setFontSize(24)
                        .setColor(new Color(42, 51, 74))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(18, 78, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of("Narrative frame, stage timing and signup blocks")
                        .setFontName("Dialog")
                        .setFontSize(18)
                        .setColor(new Color(108, 95, 84))
                        .setAutoWordWrap(176)
                        .setLineHeight(26)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(18, 116, 0, 0))));
    }

    /**
     * 构建报名二维码区
     *
     * @return 组合元素
     */
    private static ComposeElement buildQrPanel() {
        return ComposeElement.of(new RectangleElement(444, 240)
                        .setColor(new Color(31, 38, 57))
                        .setArc(32)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .in(new RectangleElement(126, 10)
                        .setColor(EVENT_ACCENT)
                        .setArc(10)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(180, 28, 0, 0))))
                .in(new QrCodeElement("https://easy-poster.bytefuture.com/event/2026-launch", 132, 132)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(26, 26, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("Scan to register")
                                        .setColor(Color.WHITE)
                                        .setFontStyle(Font.BOLD),
                                TextSpan.of("\nEarly-bird closes 06.10")
                                        .setColor(new Color(217, 223, 236)))
                        .setFontName("Dialog")
                        .setFontSize(22)
                        .setLineHeight(34)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(180, 44, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("FREE PASS")
                                        .setBackgroundColor(EVENT_ACCENT)
                                        .setBackgroundPadding(7)
                                        .setBackgroundRadius(12)
                                        .setFontStyle(Font.BOLD)
                                        .setColor(new Color(31, 38, 57)))
                        .setFontName("Dialog")
                        .setFontSize(20)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(180, 132, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of("Join the live build session and see how modular poster blocks become one launch-ready page")
                        .setFontName("Dialog")
                        .setFontSize(16)
                        .setColor(new Color(217, 223, 236))
                        .setAutoWordWrap(236)
                        .setLineHeight(24)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(180, 166, 0, 0))));
    }

    /**
     * 构建看板页头
     *
     * @return 组合元素
     */
    private static ComposeElement buildDashboardHeader() {
        return ComposeElement.of(new RectangleElement(1408, 126)
                        .setColor(new Color(16, 24, 40))
                        .setAlpha(0F)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .in(new RectangleElement(168, 10)
                        .setColor(new Color(255, 209, 102))
                        .setArc(10)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("Growth "),
                                TextSpan.of("Control Room")
                                        .setFontStyle(Font.BOLD)
                                        .setBackgroundColor(new Color(255, 209, 102))
                                        .setBackgroundPadding(9)
                                        .setBackgroundRadius(16))
                        .setFontName("Dialog")
                        .setFontSize(60)
                        .setFontStyle(Font.BOLD)
                        .setColor(Color.WHITE)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(0, 18, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("Status")
                                        .setBackgroundColor(new Color(52, 168, 83))
                                        .setBackgroundPadding(7)
                                        .setBackgroundRadius(12)
                                        .setColor(Color.WHITE)
                                        .setFontStyle(Font.BOLD),
                                TextSpan.of("  Live campaign / 2026-04-29")
                                        .setColor(new Color(210, 219, 237)))
                        .setFontName("Dialog")
                        .setFontSize(24)
                        .setPosition(RelativePosition.of(Direction.TOP_RIGHT, Margin.of(0, 30, 0, 0))));
    }

    /**
     * 构建摘要区
     *
     * @return 组合元素
     */
    private static ComposeElement buildSummaryPanel() {
        return ComposeElement.of(new RectangleElement(1408, 150)
                        .setColor(new Color(255, 255, 255))
                        .setArc(28)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("Campaign overview"),
                                TextSpan.of("  Live pulse")
                                        .setBackgroundColor(new Color(223, 244, 232))
                                        .setBackgroundPadding(6)
                                        .setBackgroundRadius(10)
                                        .setColor(DASHBOARD_SECONDARY)
                                        .setFontStyle(Font.BOLD))
                        .setFontName("Dialog")
                        .setFontSize(26)
                        .setFontStyle(Font.BOLD)
                        .setColor(new Color(38, 48, 66))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(30, 20, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of("Traffic volume is rising with stable conversion efficiency. The top row acts like a product dashboard hero, while the KPI matrix below demonstrates repeated shells with nested metric composition")
                        .setFontName("Dialog")
                        .setFontSize(24)
                        .setColor(new Color(92, 104, 128))
                        .setAutoWordWrap(1320)
                        .setLineHeight(36)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(30, 62, 0, 0))));
    }

    /**
     * 构建 KPI 平铺区
     *
     * @return 组合元素
     */
    private static ComposeElement buildKpiCard() {
        return ComposeElement.of(new RectangleElement(300, 200)
                        .setColor(new Color(255, 255, 255))
                        .setArc(28)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .in(new RectangleElement(252, 12)
                        .setColor(DASHBOARD_PRIMARY)
                        .setArc(12)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 22, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("ACTIVE KPI")
                                        .setBackgroundColor(new Color(231, 240, 255))
                                        .setBackgroundPadding(6)
                                        .setBackgroundRadius(10)
                                        .setColor(DASHBOARD_PRIMARY)
                                        .setFontStyle(Font.BOLD))
                        .setFontName("Dialog")
                        .setFontSize(18)
                        .setFontStyle(Font.BOLD)
                        .setColor(new Color(38, 48, 66))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 44, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("128.4K")
                                        .setFontStyle(Font.BOLD)
                                        .setColor(new Color(22, 35, 58)),
                                TextSpan.of("\n+18.6% week over week")
                                        .setColor(DASHBOARD_SECONDARY))
                        .setFontName("Dialog")
                        .setFontSize(38)
                        .setLineHeight(42)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 72, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of("Unified card shell with stronger hierarchy, metric contrast and compact supporting copy")
                        .setFontName("Dialog")
                        .setFontSize(18)
                        .setColor(new Color(103, 115, 136))
                        .setAutoWordWrap(252)
                        .setLineHeight(26)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(24, 148, 0, 0))));
    }

    /**
     * 构建底部洞察区
     *
     * @return 组合元素
     */
    private static ComposeElement buildInsightPanel() {
        return ComposeElement.of(new RectangleElement(1408, 220)
                        .setColor(new Color(255, 255, 255))
                        .setArc(28)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of(
                                TextSpan.of("Key Insight")
                                        .setBackgroundColor(new Color(215, 232, 255))
                                        .setBackgroundPadding(8)
                                        .setBackgroundRadius(14)
                                        .setFontStyle(Font.BOLD),
                                TextSpan.of("  Compose cards, repeat shells, chart narratives")
                                        .setColor(new Color(38, 48, 66)))
                        .setFontName("Dialog")
                        .setFontSize(28)
                        .setColor(DASHBOARD_PRIMARY)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 26, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of("This board is refined to feel publishable rather than purely demonstrative: the header owns the stage, the charts sit in clear white modules, and the repeated KPI cards read like a product surface instead of raw test output")
                        .setFontName("Dialog")
                        .setFontSize(24)
                        .setColor(new Color(88, 102, 126))
                        .setAutoWordWrap(1340)
                        .setLineHeight(38)
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 84, 0, 0))))
                .in(com.bytefuture.easy.poster.element.v2.TextElement.of("summary / charts / repeated cards / refined hierarchy")
                        .setFontName("Dialog")
                        .setFontSize(20)
                        .setLetterSpacing(3)
                        .setTextAlign(TextAlign.LEFT)
                        .setColor(new Color(106, 120, 142))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(32, 176, 0, 0))));
    }
}
