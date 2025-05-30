package com.augrain.easy.poster.basic.circle;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.geometry.PositionDirection;
import com.augrain.easy.poster.geometry.RelativePosition;
import com.augrain.easy.poster.model.Gradient;
import com.augrain.easy.poster.model.GradientDirection;
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
        EasyPoster poster = new EasyPoster(500, 500);

        poster.addCircleElement(100)
                .setColor(Color.PINK)
                .setBorderSize(40)
                .setPosition(RelativePosition.of(PositionDirection.TOP_LEFT));

        poster.asFile("png", "circle_basic.png");
    }

    /**
     * 椭圆
     */
    @Test
    public void testBasicOvalCircle() {
        EasyPoster poster = new EasyPoster(500, 500);

        poster.addOvalElement(100, 200)
                .setColor(Color.PINK)
                .setGradient(Gradient.of(new String[]{"#74A5FF", "#CEFF7E"}, GradientDirection.TOP_BOTTOM))
                .setPosition(RelativePosition.of(PositionDirection.CENTER));

        poster.asFile("png", "oval_basic.png");
    }

}
