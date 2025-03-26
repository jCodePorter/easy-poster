package com.augrain.easy.canvas.basic.text;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.geometry.AbsolutePosition;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Positions;
import com.augrain.easy.canvas.geometry.RelativePosition;
import com.augrain.easy.canvas.model.Gradient;
import com.augrain.easy.canvas.model.GradientDirection;
import org.junit.Test;

import java.awt.*;

/**
 * 文本渲染测试
 *
 * @author biaoy
 * @since 2025/03/12
 */
public class TextBasicTest {

    @Test
    public void testBasic() {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        canvas.addTextElement("正常文字")
                .setFontSize(25)
                .setFontColor(Color.red)
                .setPosition(AbsolutePosition.of(CoordinatePoint.of(30, 0), Positions.TOP_LEFT));

        canvas.addTextElement("旋转文字")
                .setFontSize(25)
                .setFontColor(Color.red)
                .setRotate(-30)
                .setPosition(AbsolutePosition.of(CoordinatePoint.of(30, 50), Positions.TOP_LEFT));

        canvas.addTextElement("透明度为50%")
                .setFontSize(25)
                .setFontColor(Color.red)
                .setAlpha(0.5f)
                .setPosition(AbsolutePosition.of(CoordinatePoint.of(30, 100), Positions.TOP_LEFT));

        canvas.asFile("png", "text_basic.png");
    }

    // .setGradient(Gradient.of(new String[]{"#74A5FF", "#CEFF7E"}, GradientDirection.TOP_BOTTOM))

    @Test
    public void testGradient() {
        EasyCanvas canvas = new EasyCanvas(800, 100);

        canvas.addTextElement("叮有鱼由叮叮智能科技进行孵化，专注于无人自助场景全套解决方案")
                .setFontSize(25)
                .setGradient(Gradient.of(new String[]{"#74A5FF", "#CEFF7E"}, GradientDirection.LEFT_RIGHT))
                .setPosition(RelativePosition.of(Positions.CENTER));

        canvas.asFile("png", "text_gradient.png");
    }
}

