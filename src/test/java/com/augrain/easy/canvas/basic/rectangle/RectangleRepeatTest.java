package com.augrain.easy.canvas.basic.rectangle;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.element.advance.RepeatElement;
import com.augrain.easy.canvas.element.basic.RectangleElement;
import com.augrain.easy.canvas.geometry.Margin;
import org.junit.Test;

import java.awt.*;

/**
 * 矩形基本测试
 *
 * @author biaoy
 * @since 2025/03/16
 */
public class RectangleRepeatTest {

    @Test
    public void testBasicRectangle() {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        RepeatElement repeatElement = new RepeatElement(new RectangleElement(100, 100)
                .setColor(Color.PINK)
                .setArc(30))
                .setPadding(20, 20)
                .setLayout(4, 4, Margin.of(10));

        canvas.addElement(repeatElement);
        canvas.asFile("png", "rectangle_tile.png");
    }
}
