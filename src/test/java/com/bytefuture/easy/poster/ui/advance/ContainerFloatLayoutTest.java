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
 * 容器浮动布局测试
 *
 * @author biaoy
 * @since 2026/05/14
 */
public class ContainerFloatLayoutTest {

    @Test
    public void testLeftFloatLayout() {
        EasyPoster poster = new EasyPoster(400, 300);

        ContainerElement container = ContainerElement.of(360, 260)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setBackgroundColor(new Color(245, 245, 245))
                .setBorderColor(new Color(120, 120, 120))
                .setBorderSize(2);

        // 第一个元素向左浮动
        container.addChild(new RectangleElement(80, 60)
                        .setColor(new Color(230, 120, 120))
                        .setFloatType(FloatType.LEFT));

        // 第二个元素向左浮动
        container.addChild(new RectangleElement(100, 70)
                        .setColor(new Color(100, 170, 230))
                        .setFloatType(FloatType.LEFT));

        // 第三个元素清除左浮动，换行显示
        container.addChild(new RectangleElement(120, 50)
                        .setColor(new Color(120, 190, 120))
                        .setClearType(ClearType.LEFT));

        poster.addElement(container);
        poster.asFile("png", "out_container_float_left.png");
    }

    @Test
    public void testRightFloatLayout() {
        EasyPoster poster = new EasyPoster(400, 300);

        ContainerElement container = ContainerElement.of(360, 260)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setBackgroundColor(new Color(245, 245, 245))
                .setBorderColor(new Color(120, 120, 120))
                .setBorderSize(2);

        // 第一个元素向右浮动
        container.addChild(new RectangleElement(80, 60)
                        .setColor(new Color(230, 120, 120))
                        .setFloatType(FloatType.RIGHT));

        // 第二个元素向右浮动
        container.addChild(new RectangleElement(100, 70)
                        .setColor(new Color(100, 170, 230))
                        .setFloatType(FloatType.RIGHT));

        // 第三个元素清除右浮动，换行显示
        container.addChild(new RectangleElement(120, 50)
                        .setColor(new Color(120, 190, 120))
                        .setClearType(ClearType.RIGHT));

        poster.addElement(container);
        poster.asFile("png", "out_container_float_right.png");
    }

    @Test
    public void testMixedFloatLayout() {
        EasyPoster poster = new EasyPoster(400, 300);

        ContainerElement container = ContainerElement.of(360, 260)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setBackgroundColor(new Color(245, 245, 245))
                .setBorderColor(new Color(120, 120, 120))
                .setBorderSize(2);

        // 第一个元素向左浮动
        container.addChild(new RectangleElement(80, 60)
                        .setColor(new Color(230, 120, 120))
                        .setFloatType(FloatType.LEFT));

        // 第二个元素向右浮动
        container.addChild(new RectangleElement(100, 70)
                        .setColor(new Color(100, 170, 230))
                        .setFloatType(FloatType.RIGHT));

        // 第三个元素不浮动，在下一行
        container.addChild(new RectangleElement(120, 50)
                        .setColor(new Color(120, 190, 120)));

        poster.addElement(container);
        poster.asFile("png", "out_container_float_mixed.png");
    }

    @Test
    public void testClearBothFloat() {
        EasyPoster poster = new EasyPoster(400, 300);

        ContainerElement container = ContainerElement.of(360, 260)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setBackgroundColor(new Color(245, 245, 245))
                .setBorderColor(new Color(120, 120, 120))
                .setBorderSize(2);

        // 第一个元素向左浮动
        container.addChild(new RectangleElement(80, 60)
                        .setColor(new Color(230, 120, 120))
                        .setFloatType(FloatType.LEFT));

        // 第二个元素向右浮动
        container.addChild(new RectangleElement(100, 70)
                        .setColor(new Color(100, 170, 230))
                        .setFloatType(FloatType.RIGHT));

        // 第三个元素清除所有浮动
        container.addChild(new RectangleElement(200, 50)
                        .setColor(new Color(120, 190, 120))
                        .setClearType(ClearType.BOTH));

        poster.addElement(container);
        poster.asFile("png", "out_container_float_clear_both.png");
    }

    @Test
    public void testFloatWithText() {
        EasyPoster poster = new EasyPoster(500, 300);

        ContainerElement container = ContainerElement.of(460, 260)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setBackgroundColor(new Color(245, 245, 245))
                .setBorderColor(new Color(120, 120, 120))
                .setBorderSize(2);

        // 左侧浮动图片区域
        container.addChild(new RectangleElement(100, 100)
                        .setColor(new Color(100, 170, 230))
                        .setFloatType(FloatType.LEFT));

        // 右侧文本内容
        container.addChild(TextElement.of("这是一段测试文本，用于演示浮动布局中文本环绕图片的效果。当图片向左浮动时，文本会自动环绕在图片右侧。")
                        .setColor(new Color(60, 60, 60))
                        .setFontSize(14));

        poster.addElement(container);
        poster.asFile("png", "out_container_float_text.png");
    }
}
