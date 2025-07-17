package com.augrain.easy.poster.basic.rectangle;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.geometry.Direction;
import com.augrain.easy.poster.geometry.RelativePosition;
import com.augrain.easy.poster.model.Gradient;
import com.augrain.easy.poster.model.GradientDirection;
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
        EasyPoster poster = new EasyPoster(500, 500);

        poster.addRectangleElement(100, 100)
                .setColor(Color.PINK)
                .setArc(20)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT));

        poster.asFile("png", "out_rectangle_basic.png");
    }

    @Test
    public void testGradientRectangle() {
        EasyPoster poster = new EasyPoster(500, 500);

        poster.addRectangleElement(500, 500)
                // 双色渐变
                // .setGradient(Gradient.of(new String[]{"#74A5FF", "#CEFF7E"}, GradientDirection.TOP_BOTTOM))
                // 三色渐变
                .setGradient(Gradient.of(new String[]{"#EA9381", "#F0B09F", "#FFDBBD"}, GradientDirection.TOP_BOTTOM))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_rectangle_gradient.png");
    }

}
