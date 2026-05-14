package com.bytefuture.easy.poster.ui.advance;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.advance.ContainerElement;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.ClearType;
import com.bytefuture.easy.poster.model.FloatType;
import org.junit.Test;

import java.awt.*;

/**
 * CSS浮动布局经典场景测试
 * 测试类似于HTML/CSS中float属性的各种浮动效果
 *
 * @author biaoy
 * @since 2026/05/14
 */
public class ContainerFloatClassicTest {

    /**
     * 场景1：不浮动的框（默认情况）
     * 三个框垂直排列
     */
    @Test
    public void testNoFloat() {
        EasyPoster poster = new EasyPoster(200, 280);

        ContainerElement container = ContainerElement.of(160, 240)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(20)))
                .setBackgroundColor(new Color(250, 250, 240))
                .setBorderColor(new Color(150, 150, 150))
                .setBorderSize(1)
                .setGap(10);

        // 框1 - 不浮动
        container.addChild(createBox("框1", new Color(245, 245, 220)));
        // 框2 - 不浮动
        container.addChild(createBox("框2", new Color(245, 245, 220)));
        // 框3 - 不浮动
        container.addChild(createBox("框3", new Color(245, 245, 220)));

        poster.addElement(container);
        poster.asFile("png", "out_float_no_float.png");
    }

    /**
     * 场景2：框1向右浮动
     * 框1靠右，框2和框3在左侧
     */
    @Test
    public void testFirstRightFloat() {
        EasyPoster poster = new EasyPoster(200, 280);

        ContainerElement container = ContainerElement.of(160, 240)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(20)))
                .setBackgroundColor(new Color(250, 250, 240))
                .setBorderColor(new Color(150, 150, 150))
                .setBorderSize(1)
                .setGap(10);

        // 框1 - 向右浮动
        container.addChild(createBox("框1", new Color(245, 245, 220)).setFloatType(FloatType.RIGHT));
        // 框2 - 不浮动
        container.addChild(createBox("框2", new Color(245, 245, 220)));
        // 框3 - 不浮动
        container.addChild(createBox("框3", new Color(245, 245, 220)));

        poster.addElement(container);
        poster.asFile("png", "out_float_first_right.png");
    }

    /**
     * 场景3：框1向左浮动
     * 框1靠左，框2隐藏在框1下面，框3在框1下方
     */
    @Test
    public void testFirstLeftFloat() {
        EasyPoster poster = new EasyPoster(200, 280);

        ContainerElement container = ContainerElement.of(160, 240)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(20)))
                .setBackgroundColor(new Color(250, 250, 240))
                .setBorderColor(new Color(150, 150, 150))
                .setBorderSize(1)
                .setGap(10);

        // 框1 - 向左浮动
        container.addChild(createBox("框1", new Color(245, 245, 220)).setFloatType(FloatType.LEFT));
        // 框2 - 不浮动（会尝试填充框1右侧空间）
        container.addChild(createBox("框2", new Color(245, 245, 220)));
        // 框3 - 不浮动
        container.addChild(createBox("框3", new Color(245, 245, 220)));

        poster.addElement(container);
        poster.asFile("png", "out_float_first_left.png");
    }

    /**
     * 场景4：所有三个框向左浮动
     * 三个框水平排列
     */
    @Test
    public void testAllLeftFloat() {
        EasyPoster poster = new EasyPoster(200, 280);

        ContainerElement container = ContainerElement.of(160, 240)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(20)))
                .setBackgroundColor(new Color(250, 250, 240))
                .setBorderColor(new Color(150, 150, 150))
                .setBorderSize(1)
                .setGap(10);

        // 框1 - 向左浮动
        container.addChild(createBox("框1", new Color(245, 245, 220)).setFloatType(FloatType.LEFT));
        // 框2 - 向左浮动
        container.addChild(createBox("框2", new Color(245, 245, 220)).setFloatType(FloatType.LEFT));
        // 框3 - 向左浮动
        container.addChild(createBox("框3", new Color(245, 245, 220)).setFloatType(FloatType.LEFT));

        poster.addElement(container);
        poster.asFile("png", "out_float_all_left.png");
    }

    /**
     * 场景5：框1、框2向左浮动，框3清除左浮动
     * 框1和框2水平排列，框3换行
     */
    @Test
    public void testTwoLeftFloatOneClear() {
        EasyPoster poster = new EasyPoster(200, 280);

        ContainerElement container = ContainerElement.of(160, 240)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(20)))
                .setBackgroundColor(new Color(250, 250, 240))
                .setBorderColor(new Color(150, 150, 150))
                .setBorderSize(1)
                .setGap(10);

        // 框1 - 向左浮动
        container.addChild(createBox("框1", new Color(245, 245, 220)).setFloatType(FloatType.LEFT));
        // 框2 - 向左浮动
        container.addChild(createBox("框2", new Color(245, 245, 220)).setFloatType(FloatType.LEFT));
        // 框3 - 清除左浮动，换行显示
        container.addChild(createBox("框3", new Color(245, 245, 220)).setClearType(ClearType.LEFT));

        poster.addElement(container);
        poster.asFile("png", "out_float_two_left_one_clear.png");
    }

    /**
     * 场景6：框1向左浮动，框2和框3在右侧下方
     * 演示浮动元素被"卡住"的效果
     */
    @Test
    public void testFloatWrapEffect() {
        EasyPoster poster = new EasyPoster(250, 280);

        ContainerElement container = ContainerElement.of(210, 240)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(20)))
                .setBackgroundColor(new Color(250, 250, 240))
                .setBorderColor(new Color(150, 150, 150))
                .setBorderSize(1)
                .setGap(10);

        // 框1 - 向左浮动（较大）
        container.addChild(new RectangleElement(90, 80)
                .setColor(new Color(245, 245, 220))
                .setBorderSize(2)
                .setFloatType(FloatType.LEFT));
        
        // 框2 - 向左浮动
        container.addChild(new RectangleElement(50, 60)
                .setColor(new Color(245, 245, 220))
                .setBorderSize(2)
                .setFloatType(FloatType.LEFT));
        
        // 框3 - 向左浮动（会被框1卡住）
        container.addChild(new RectangleElement(60, 70)
                .setColor(new Color(245, 245, 220))
                .setBorderSize(2)
                .setFloatType(FloatType.LEFT));

        poster.addElement(container);
        poster.asFile("png", "out_float_wrap_effect.png");
    }

    /**
     * 创建标准测试框
     *
     * @param label 标签文字
     * @param color 背景色
     * @return 矩形元素
     */
    private RectangleElement createBox(String label, Color color) {
        return new RectangleElement(50, 60)
                .setColor(color)
                .setBorderSize(2);
    }
}
