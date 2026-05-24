package com.bytefuture.easy.poster.ui.basic.text;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Point;
import org.junit.Test;

import java.awt.*;

/**
 * 测试自动换行
 *
 * @author biaoy
 * @since 2025/03/16
 */
public class AutoWrapTextTest {

    @Test
    public void testAutoWrapText() {
        EasyPoster poster = new EasyPoster(500, 200);

        poster.addTextElement("这是一段测试的超长文本，用来测试自动换行，目前是simple implementation 一版，用于验证自动换行功能逻辑，当前功能趋于完善，基本生产可用")
                .setFontSize(15)
                .setColor(Color.red)
                .setLineHeight(50)
                .setMaxTextWidth(400)
                .setPosition(AbsolutePosition.of(Point.of(30, 50), Direction.TOP_LEFT));

        poster.addRectangleElement(400, 300)
                .setBorderSize(3)
                .setColor(Color.BLACK)
                .setPosition(Point.of(30, 50));

        poster.asFile("png", "out_text_auto_split.png");
    }
}
