package com.augrain.easy.canvas.circle;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.geometry.Positions;
import com.augrain.easy.canvas.geometry.RelativePosition;
import org.junit.Test;

import java.awt.*;

/**
 * 圆形基本测试
 *
 * @author biaoy
 * @since 2025/03/17
 */
public class CircleBasicTest {

    @Test
    public void testBasicCircle() {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        canvas.addCircleElement(100)
                .setColor(Color.PINK)
                .setBorderSize(40)
                .setPosition(RelativePosition.of(Positions.CENTER));

        canvas.asFile("png", "circle_basic.png");
    }

}
