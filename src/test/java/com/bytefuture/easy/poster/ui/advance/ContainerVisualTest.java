package com.bytefuture.easy.poster.ui.advance;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.advance.ContainerElement;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.element.basic.TextElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.FloatType;
import org.junit.Test;

import java.awt.*;

/**
 * 容器视觉特性测试
 *
 * @author biaoy
 * @since 2026/05/15
 */
public class ContainerVisualTest {

    /**
     * 圆角容器 + clipContent：裁剪溢出内容到圆角内容区
     * 子元素超出容器边界时被圆角内容区裁剪
     */
    @Test
    public void testRoundedClipContainer() {
        EasyPoster poster = new EasyPoster(600, 400);

        ContainerElement container = ContainerElement.of(360, 260)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(24))
                .setArc(48)
                .setClipContent(true)
                .setBackgroundColor(new Color(245, 248, 255))
                .setBorderColor(new Color(80, 110, 180))
                .setBorderSize(3)
                .setGap(10);

        // 左浮动大矩形（超出内容区但被裁剪）
        container.addChild(new RectangleElement(280, 200)
                .setColor(new Color(255, 120, 120))
                .setFloatType(FloatType.LEFT));

        // NONE 文字
        container.addChild(new TextElement("圆角裁剪容器")
                .setColor(new Color(30, 50, 90))
                .setFontSize(20));

        poster.addElement(container);
        poster.asFile("png", "out_visual_rounded_clip.png");
    }

    /**
     * 自动高度容器：height=0 时容器根据子元素自动计算高度
     * 配合 padding + border 展示完整视觉效果
     */
    @Test
    public void testAutoHeightContainer() {
        EasyPoster poster = new EasyPoster(600, 600);

        ContainerElement container = ContainerElement.of(400, 0)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(40, 60, 0, 0)))
                .setPadding(Margin.of(20))
                .setGap(12)
                .setBackgroundColor(new Color(255, 252, 245))
                .setBorderColor(new Color(200, 180, 140))
                .setBorderSize(2)
                .setArc(16);

        // 左浮动头像区
        container.addChild(new RectangleElement(60, 60)
                .setColor(new Color(100, 170, 230))
                .setArc(30)
                .setFloatType(FloatType.LEFT));

        // NONE 文字块（容器会自动增长高度）
        container.addChild(new TextElement("自动高度容器")
                .setColor(new Color(50, 50, 50))
                .setFontSize(18));

        container.addChild(new TextElement("容器高度设为0时，会根据子元素的总高度自动计算。padding和border也计入容器总尺寸。适用于动态内容的场景。")
                .setColor(new Color(90, 90, 90))
                .setFontSize(14));

        // 再加一些内容验证高度继续增长
        container.addChild(new RectangleElement(340, 80)
                .setColor(new Color(240, 240, 240)));

        container.addChild(new TextElement("底部附加内容区")
                .setColor(new Color(70, 70, 70))
                .setFontSize(14));

        poster.addElement(container);
        poster.asFile("png", "out_visual_auto_height.png");
    }

    /**
     * 子元素 Margin：不同子元素设置不同的外边距
     * 展示 childMargin 对浮动布局的影响
     */
    @Test
    public void testChildMarginLayout() {
        EasyPoster poster = new EasyPoster(600, 400);

        ContainerElement container = ContainerElement.of(400, 300)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(16))
                .setGap(8)
                .setBackgroundColor(new Color(246, 246, 246))
                .setBorderColor(new Color(120, 120, 120))
                .setBorderSize(1);

        // 左浮动 + margin: 上12 右8 下0 左4
        container.addChild(new RectangleElement(80, 80)
                .setColor(new Color(230, 100, 100)), Margin.of(12, 8, 0, 4));

        // 左浮动 + margin: 上0 右12 下8 左0
        container.addChild(new RectangleElement(80, 80)
                .setColor(new Color(100, 170, 230))
                .setFloatType(FloatType.LEFT), Margin.of(0, 12, 8, 0));

        // NONE 块级 + margin: 上10 右0 下0 左10
        container.addChild(new RectangleElement(200, 40)
                .setColor(new Color(120, 190, 120)), Margin.of(10, 0, 0, 10));

        poster.addElement(container);
        poster.asFile("png", "out_visual_child_margin.png");
    }
}