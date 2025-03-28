package com.augrain.easy.canvas.basic.circle;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.geometry.Positions;
import com.augrain.easy.canvas.geometry.RelativePosition;
import com.augrain.easy.canvas.model.Gradient;
import com.augrain.easy.canvas.model.GradientDirection;
import org.junit.Test;

import java.awt.*;

/**
 * 圆形基本测试
 *
 * @author biaoy
 * @since 2025/03/17
 */
public class CircleBasicTest {

    /**
     * 圆
     */
    @Test
    public void testBasicCircle() {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        canvas.addCircleElement(100)
                .setColor(Color.PINK)
                .setBorderSize(40)
                .setPosition(RelativePosition.of(Positions.CENTER));

        canvas.asFile("png", "circle_basic.png");
    }

    /**
     * 椭圆
     */
    @Test
    public void testBasicOvalCircle() {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        canvas.addOvalElement(100, 200)
                .setColor(Color.PINK)
                .setGradient(Gradient.of(new String[]{"#74A5FF", "#CEFF7E"}, GradientDirection.TOP_BOTTOM))
                .setPosition(RelativePosition.of(Positions.CENTER));

        canvas.asFile("png", "oval_basic.png");
    }

}
