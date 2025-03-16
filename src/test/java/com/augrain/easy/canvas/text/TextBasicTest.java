package com.augrain.easy.canvas.text;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.geometry.AbsolutePosition;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Positions;
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
}
