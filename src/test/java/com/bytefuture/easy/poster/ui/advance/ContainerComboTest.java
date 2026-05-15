package com.bytefuture.easy.poster.ui.advance;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.advance.ComposeElement;
import com.bytefuture.easy.poster.element.advance.ContainerElement;
import com.bytefuture.easy.poster.element.basic.CircleElement;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.element.special.QrCodeElement;
import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.LocalAbsolutePosition;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.FloatType;
import com.bytefuture.easy.poster.model.TextSpan;
import org.junit.Test;

import java.awt.*;

/**
 * 容器 + 其他元素组合测试
 *
 * @author biaoy
 * @since 2026/05/15
 */
public class ContainerComboTest {

    /**
     * 嵌套容器：外层容器（大布局）包含内层容器（小卡片）
     * 演示：ContainerElement 内嵌 ContainerElement，外层浮动 + 内层块级布局
     */
    @Test
    public void testNestedContainer() {
        EasyPoster poster = new EasyPoster(700, 500);

        // 外层容器
        ContainerElement outerContainer = ContainerElement.of(620, 420)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setGap(14)
                .setBackgroundColor(new Color(245, 248, 255))
                .setBorderColor(new Color(80, 110, 180))
                .setBorderSize(2)
                .setArc(24);

        // 外层左浮动：标题区
        ContainerElement headerCard = ContainerElement.of(200, 120)
                .setPadding(Margin.of(16))
                .setGap(8)
                .setBackgroundColor(new Color(66, 135, 245))
                .setArc(16)
                .setFloatType(FloatType.LEFT);

        headerCard.addChild(new CircleElement(30)
                .setColor(Color.WHITE)
                .setFloatType(FloatType.LEFT));
        headerCard.addChild(TextElement.of("数据\n报告")
                .setColor(Color.WHITE)
                .setFontSize(16)
                .setFontStyle(Font.BOLD));

        // 外层右浮动：统计数字卡片
        ContainerElement statsCard = ContainerElement.of(200, 120)
                .setPadding(Margin.of(16))
                .setGap(6)
                .setBackgroundColor(new Color(255, 252, 245))
                .setBorderColor(new Color(200, 180, 140))
                .setBorderSize(1)
                .setArc(12)
                .setFloatType(FloatType.RIGHT);

        statsCard.addChild(TextElement.of(
                        TextSpan.of("92.5%").setColor(new Color(220, 80, 80)).setFontSize(24).setFontStyle(Font.BOLD),
                        TextSpan.of("\n"),
                        TextSpan.of("转化率").setColor(new Color(120, 120, 120)).setFontSize(13))
                .setLetterSpacing(2));

        // 外层 NONE 块级：内容区域
        ContainerElement contentCard = ContainerElement.of(0, 160)
                .setPadding(Margin.of(16))
                .setGap(10)
                .setBackgroundColor(new Color(255, 255, 255))
                .setBorderColor(new Color(220, 220, 220))
                .setBorderSize(1)
                .setArc(8);

        contentCard.addChild(TextElement.of("月度数据概览")
                .setColor(new Color(50, 50, 50))
                .setFontSize(16)
                .setFontStyle(Font.BOLD));
        contentCard.addChild(TextElement.of("本月数据显示用户增长稳定，核心指标均达到预期目标。下月将重点关注留存率的提升。")
                .setColor(new Color(90, 90, 90))
                .setFontSize(14)
                .setLetterSpacing(1));

        outerContainer.addChild(headerCard);
        outerContainer.addChild(statsCard);
        outerContainer.addChild(contentCard);

        poster.addElement(outerContainer);
        poster.asFile("png", "out_combo_nested_container.png");
    }

    /**
     * ComposeElement + QrCodeElement 在容器中
     * 演示：ComposeElement 组合元素作为 ContainerElement 子元素
     */
    @Test
    public void testContainerWithComposeAndQr() {
        EasyPoster poster = new EasyPoster(600, 400);

        ContainerElement container = ContainerElement.of(480, 320)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(24))
                .setGap(14)
                .setBackgroundColor(new Color(250, 250, 250))
                .setBorderColor(new Color(150, 150, 150))
                .setBorderSize(1)
                .setArc(16);

        // 左浮动：ComposeElement（头像 + 姓名）
        ComposeElement avatarWithName = ComposeElement.of(
                        new CircleElement(50).setColor(new Color(100, 170, 230)))
                .right(TextElement.of("创意工作室")
                        .setColor(new Color(50, 50, 50))
                        .setFontSize(16)
                        .setFontStyle(Font.BOLD)
                        .setPosition(RelativePosition.of(Direction.CENTER)));
        avatarWithName.setFloatType(FloatType.LEFT);

        container.addChild(avatarWithName);

        // NONE 块级：描述文字
        container.addChild(TextElement.of("专注品牌设计、海报制作、视觉传达。让每一张海报都成为品牌故事的一部分。")
                .setColor(new Color(90, 90, 90))
                .setFontSize(14)
                .setLetterSpacing(1));

        // NONE 块级：ComposeElement（二维码 + 标签）
        ComposeElement qrWithLabel = ComposeElement.of(
                        new QrCodeElement("https://easy-poster.bytefuture.com", 100, 100))
                .right(TextElement.of("扫码了解更多")
                        .setColor(new Color(100, 100, 100))
                        .setFontSize(12)
                        .setPosition(LocalAbsolutePosition.of(Point.of(10, 40))));
        container.addChild(qrWithLabel);

        poster.addElement(container);
        poster.asFile("png", "out_combo_compose_qr.png");
    }

    /**
     * 装饰性元素在浮动容器中：CircleElement 圆点 + LineElement 分隔线
     * 演示：特殊形状元素参与浮动布局
     */
    @Test
    public void testContainerWithDecorations() {
        EasyPoster poster = new EasyPoster(600, 440);

        ContainerElement container = ContainerElement.of(500, 360)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setGap(10)
                .setBackgroundColor(new Color(255, 252, 245))
                .setBorderColor(new Color(200, 180, 140))
                .setBorderSize(2)
                .setArc(20);

        // 左浮动装饰圆点
        container.addChild(new CircleElement(16)
                .setColor(new Color(255, 80, 80))
                .setFloatType(FloatType.LEFT));

        container.addChild(new CircleElement(16)
                .setColor(new Color(100, 170, 230))
                .setFloatType(FloatType.LEFT));

        container.addChild(new CircleElement(16)
                .setColor(new Color(120, 190, 120))
                .setFloatType(FloatType.LEFT));

        // NONE 块级：标题
        container.addChild(TextElement.of("装饰性元素示例")
                .setColor(new Color(50, 50, 50))
                .setFontSize(18)
                .setFontStyle(Font.BOLD)
                .setLetterSpacing(2));

        // NONE 块级：分隔线（LineElement 模拟）
        container.addChild(new RectangleElement(460, 2)
                .setColor(new Color(200, 180, 140)));

        // NONE 块级：内容描述
        container.addChild(TextElement.of("CircleElement 圆点作为浮动装饰排列在标题行，分隔线作为块级元素分割内容区域。浮动布局让装饰元素与内容自然融合。")
                .setColor(new Color(80, 80, 80))
                .setFontSize(14)
                .setLetterSpacing(1));

        // NONE 块级：底部圆点行
        container.addChild(new CircleElement(24)
                .setColor(new Color(66, 135, 245)));

        poster.addElement(container);
        poster.asFile("png", "out_combo_decorations.png");
    }
}