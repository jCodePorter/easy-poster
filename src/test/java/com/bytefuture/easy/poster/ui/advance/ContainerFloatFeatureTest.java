package com.bytefuture.easy.poster.ui.advance;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.advance.ContainerElement;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.element.basic.TextElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.ClearType;
import com.bytefuture.easy.poster.model.FloatType;
import org.junit.Test;

import java.awt.*;

/**
 * 容器浮动布局核心特性测试
 *
 * @author biaoy
 * @since 2026/05/15
 */
public class ContainerFloatFeatureTest {

    /**
     * 左浮动 + 块级元素：模拟图文并排
     * 左侧浮动矩形（模拟图片区域），右侧文字块自动排列
     */
    @Test
    public void testLeftFloatWithBlock() {
        EasyPoster poster = new EasyPoster(600, 400);

        ContainerElement container = ContainerElement.of(500, 300)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setGap(10)
                .setBackgroundColor(new Color(248, 248, 248))
                .setBorderColor(new Color(160, 160, 160))
                .setBorderSize(1);

        // 左浮动"图片区"
        container.addChild(new RectangleElement(120, 160)
                .setColor(new Color(100, 170, 230))
                .setBorderSize(2)
                .setArc(12)
                .setFloatType(FloatType.LEFT));

        // NONE 块级文字元素（自动排在浮动元素旁边）
        container.addChild(new TextElement("图文并排示例")
                .setColor(new Color(40, 40, 40))
                .setFontSize(18));

        // NONE 块级文字元素（排在下一行）
        container.addChild(new TextElement("这是通过浮动布局实现的经典图文排版效果。左浮动元素占据左侧空间，右侧的文字块自动排列在其旁边。")
                .setColor(new Color(80, 80, 80))
                .setFontSize(14));

        poster.addElement(container);
        poster.asFile("png", "out_float_left_with_block.png");
    }

    /**
     * 右浮动 + 块级元素：文字在左，图片在右
     * 右侧浮动矩形，左侧文字块自动排列
     */
    @Test
    public void testRightFloatWithBlock() {
        EasyPoster poster = new EasyPoster(600, 400);

        ContainerElement container = ContainerElement.of(500, 300)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setGap(10)
                .setBackgroundColor(new Color(255, 252, 245))
                .setBorderColor(new Color(200, 180, 140))
                .setBorderSize(1);

        // 右浮动"图片区"
        container.addChild(new RectangleElement(140, 180)
                .setColor(new Color(220, 160, 100))
                .setBorderSize(2)
                .setArc(8)
                .setFloatType(FloatType.RIGHT));

        // NONE 块级文字
        container.addChild(new TextElement("文字在左图片在右")
                .setColor(new Color(50, 50, 50))
                .setFontSize(18));

        container.addChild(new TextElement("右浮动元素占据右侧空间，左侧的文字自动排列。这种布局适用于产品介绍、新闻摘要等场景。")
                .setColor(new Color(90, 90, 90))
                .setFontSize(14));

        poster.addElement(container);
        poster.asFile("png", "out_float_right_with_block.png");
    }

    /**
     * 左右浮动混合：LEFT + RIGHT 占据两端，中间 NONE 块
     */
    @Test
    public void testMixedFloatSameRow() {
        EasyPoster poster = new EasyPoster(600, 400);

        ContainerElement container = ContainerElement.of(500, 300)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setGap(10)
                .setBackgroundColor(new Color(245, 248, 255))
                .setBorderColor(new Color(80, 110, 180))
                .setBorderSize(1);

        // 左浮动（模拟导航图标区）
        container.addChild(new RectangleElement(80, 80)
                .setColor(new Color(66, 135, 245))
                .setArc(40)
                .setFloatType(FloatType.LEFT));

        // 右浮动（模拟操作按钮区）
        container.addChild(new RectangleElement(80, 80)
                .setColor(new Color(230, 80, 80))
                .setArc(40)
                .setFloatType(FloatType.RIGHT));

        // NONE 块级中间内容
        container.addChild(new TextElement("中间内容区域")
                .setColor(new Color(30, 50, 90))
                .setFontSize(20));

        container.addChild(new TextElement("左右浮动元素占据两端，中间的文字内容自动填充剩余空间。适用于导航栏、信息卡片等布局。")
                .setColor(new Color(60, 80, 120))
                .setFontSize(13));

        poster.addElement(container);
        poster.asFile("png", "out_float_mixed_same_row.png");
    }

    /**
     * ClearType 效果对比：LEFT / RIGHT / BOTH 三种清除浮动
     */
    @Test
    public void testClearEffect() {
        // 场景1：ClearType.LEFT
        EasyPoster poster1 = new EasyPoster(240, 320);
        ContainerElement container1 = ContainerElement.of(200, 280)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(20)))
                .setGap(8)
                .setBackgroundColor(new Color(250, 250, 240))
                .setBorderColor(new Color(150, 150, 150))
                .setBorderSize(1);

        container1.addChild(new RectangleElement(60, 40)
                .setColor(new Color(100, 170, 230))
                .setFloatType(FloatType.LEFT));
        container1.addChild(new RectangleElement(60, 40)
                .setColor(new Color(230, 120, 120))
                .setFloatType(FloatType.LEFT));
        container1.addChild(new RectangleElement(120, 40)
                .setColor(new Color(120, 190, 120))
                .setClearType(ClearType.LEFT));

        poster1.addElement(container1);
        poster1.asFile("png", "out_float_clear_left.png");

        // 场景2：ClearType.RIGHT
        EasyPoster poster2 = new EasyPoster(240, 320);
        ContainerElement container2 = ContainerElement.of(200, 280)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(20)))
                .setGap(8)
                .setBackgroundColor(new Color(250, 250, 240))
                .setBorderColor(new Color(150, 150, 150))
                .setBorderSize(1);

        container2.addChild(new RectangleElement(60, 40)
                .setColor(new Color(100, 170, 230))
                .setFloatType(FloatType.RIGHT));
        container2.addChild(new RectangleElement(60, 40)
                .setColor(new Color(230, 120, 120))
                .setFloatType(FloatType.LEFT));
        container2.addChild(new RectangleElement(120, 40)
                .setColor(new Color(120, 190, 120))
                .setClearType(ClearType.RIGHT));

        poster2.addElement(container2);
        poster2.asFile("png", "out_float_clear_right.png");

        // 场景3：ClearType.BOTH
        EasyPoster poster3 = new EasyPoster(240, 320);
        ContainerElement container3 = ContainerElement.of(200, 280)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(20)))
                .setGap(8)
                .setBackgroundColor(new Color(250, 250, 240))
                .setBorderColor(new Color(150, 150, 150))
                .setBorderSize(1);

        container3.addChild(new RectangleElement(60, 40)
                .setColor(new Color(100, 170, 230))
                .setFloatType(FloatType.LEFT));
        container3.addChild(new RectangleElement(60, 40)
                .setColor(new Color(230, 120, 120))
                .setFloatType(FloatType.RIGHT));
        container3.addChild(new RectangleElement(120, 40)
                .setColor(new Color(120, 190, 120))
                .setClearType(ClearType.BOTH));

        poster3.addElement(container3);
        poster3.asFile("png", "out_float_clear_both.png");
    }

    /**
     * gap 间距效果对比：gap=0 和 gap=20 的差异
     */
    @Test
    public void testGapEffect() {
        // 场景1：gap=0
        EasyPoster poster1 = new EasyPoster(280, 240);
        ContainerElement container1 = ContainerElement.of(240, 200)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(20)))
                .setBackgroundColor(new Color(248, 248, 248))
                .setBorderColor(new Color(120, 120, 120))
                .setBorderSize(1);

        container1.addChild(new RectangleElement(60, 60)
                .setColor(new Color(230, 120, 120))
                .setFloatType(FloatType.LEFT));
        container1.addChild(new RectangleElement(60, 60)
                .setColor(new Color(100, 170, 230))
                .setFloatType(FloatType.LEFT));
        container1.addChild(new RectangleElement(60, 60)
                .setColor(new Color(120, 190, 120))
                .setFloatType(FloatType.LEFT));

        poster1.addElement(container1);
        poster1.asFile("png", "out_float_gap_0.png");

        // 场景2：gap=20
        EasyPoster poster2 = new EasyPoster(320, 280);
        ContainerElement container2 = ContainerElement.of(280, 240)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(20)))
                .setGap(20)
                .setBackgroundColor(new Color(248, 248, 248))
                .setBorderColor(new Color(120, 120, 120))
                .setBorderSize(1);

        container2.addChild(new RectangleElement(60, 60)
                .setColor(new Color(230, 120, 120))
                .setFloatType(FloatType.LEFT));
        container2.addChild(new RectangleElement(60, 60)
                .setColor(new Color(100, 170, 230))
                .setFloatType(FloatType.LEFT));
        container2.addChild(new RectangleElement(60, 60)
                .setColor(new Color(120, 190, 120))
                .setFloatType(FloatType.LEFT));

        poster2.addElement(container2);
        poster2.asFile("png", "out_float_gap_20.png");
    }
}