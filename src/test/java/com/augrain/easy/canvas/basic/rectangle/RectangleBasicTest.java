package com.augrain.easy.canvas.basic.rectangle;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.geometry.Positions;
import com.augrain.easy.canvas.geometry.RelativePosition;
import com.augrain.easy.canvas.model.Gradient;
import com.augrain.easy.canvas.model.GradientDirection;
import org.junit.Test;

import java.awt.*;

/**
 * 矩形基本测试
 *
 * @author biaoy
 * @since 2025/03/16
 */
public class RectangleBasicTest {

    @Test
    public void testBasicRectangle() {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        canvas.addRectangleElement(100, 100)
                .setColor(Color.PINK)
                .setRoundCorner(30)
                .setPosition(RelativePosition.of(Positions.CENTER));

        canvas.asFile("png", "rectangle_basic.png");
    }

    @Test
    public void testGradientRectangle() {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        canvas.addRectangleElement(500, 500)
                // 双色渐变
                // .setGradient(Gradient.of(new String[]{"#74A5FF", "#CEFF7E"}, GradientDirection.TOP_BOTTOM))
                // 三色渐变
                .setGradient(Gradient.of(new String[]{"#EA9381", "#F0B09F", "#FFDBBD"}, GradientDirection.TOP_BOTTOM))
                .setPosition(RelativePosition.of(Positions.CENTER));

        canvas.asFile("png", "rectangle_gradient.png");
    }

}
