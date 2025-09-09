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
        EasyPoster poster = new EasyPoster(500, 300);

        poster.addTextElement("正常文字")
                .setFontSize(25)
                .setColor(Color.red)
                .setPosition(AbsolutePosition.of(Point.of(30, 50), Direction.TOP_LEFT));

        poster.addTextElement("旋转文字")
                .setFontSize(25)
                .setColor(Color.blue)
                .setRotate(-30)
                .setPosition(AbsolutePosition.of(Point.of(30, 100), Direction.TOP_LEFT));

        poster.addTextElement("透明度为50%")
                .setFontSize(25)
                .setColor(Color.red)
                .setAlpha(0.5f)
                .setPosition(AbsolutePosition.of(Point.of(30, 180), Direction.TOP_LEFT));

        poster.addTextElement("这是一段渐变色同时加上删除线的文字")
                .setFontSize(25)
                .setStrikeThrough(true)
                .setPosition(AbsolutePosition.of(Point.of(30, 250), Direction.TOP_LEFT))
                .setGradient(Gradient.of(new String[]{"#74A5FF", "#CEFF7E"}, GradientDirection.LEFT_RIGHT));

        poster.asFile("png", "out_text_basic.png");
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

