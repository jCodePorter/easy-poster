package com.augrain.easy.poster.basic.rectangle;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.element.advance.RepeatElement;
import com.augrain.easy.poster.element.basic.RectangleElement;
import com.augrain.easy.poster.geometry.Margin;
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
        EasyPoster poster = new EasyPoster(500, 500);

        RepeatElement repeatElement = new RepeatElement(new RectangleElement(100, 100)
                .setColor(Color.PINK)
                .setArc(30))
                .setPadding(20, 20)
                .setLayout(4, 4, Margin.of(10));

        poster.addElement(repeatElement);
        poster.asFile("png", "rectangle_tile.png");
    }
}
