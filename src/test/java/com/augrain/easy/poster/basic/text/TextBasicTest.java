package com.augrain.easy.poster.basic.text;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.geometry.AbsolutePosition;
import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.geometry.Direction;
import com.augrain.easy.poster.geometry.RelativePosition;
import com.augrain.easy.poster.model.Gradient;
import com.augrain.easy.poster.model.GradientDirection;
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
        EasyPoster poster = new EasyPoster(500, 500);

        poster.addTextElement("正常文字")
                .setFontSize(25)
                .setColor(Color.red)
                .setPosition(AbsolutePosition.of(Point.of(30, 0), Direction.TOP_LEFT));

        poster.addTextElement("旋转文字")
                .setFontSize(25)
                .setColor(Color.red)
                .setRotate(-30)
                .setPosition(AbsolutePosition.of(Point.of(30, 50), Direction.TOP_LEFT));

        poster.addTextElement("透明度为50%")
                .setFontSize(25)
                .setColor(Color.red)
                .setAlpha(0.5f)
                .setPosition(AbsolutePosition.of(Point.of(30, 100), Direction.TOP_LEFT));

        poster.asFile("png", "out_text_basic.png");
    }

    @Test
    public void testGradient() {
        EasyPoster poster = new EasyPoster(800, 100);

        poster.addTextElement("叮有鱼由叮叮智能科技进行孵化，专注于无人自助场景全套解决方案")
                .setFontSize(25)
                .setStrikeThrough(true)
                .setGradient(Gradient.of(new String[]{"#74A5FF", "#CEFF7E"}, GradientDirection.LEFT_RIGHT))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.asFile("png", "out_text_gradient.png");
    }

    @Test
    public void getGlobal() {
        EasyPoster poster = new EasyPoster(500, 500);
        poster.getConfig().setColor(Color.blue);
        poster.getConfig().setFontSize(25);

        poster.addTextElement("正常文字")
                .setPosition(AbsolutePosition.of(Point.of(30, 0)));

        poster.addTextElement("旋转文字")
                .setRotate(-30)
                .setColor(Color.red)
                .setPosition(AbsolutePosition.of(Point.of(30, 50)));

        poster.addTextElement("透明度为50%")
                .setAlpha(0.5f)
                .setPosition(AbsolutePosition.of(Point.of(30, 100)));

        poster.asFile("png", "out_text_global.png");
    }
}

