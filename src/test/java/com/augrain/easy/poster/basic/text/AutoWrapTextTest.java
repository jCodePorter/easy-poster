package com.augrain.easy.poster.basic.text;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.geometry.AbsolutePosition;
import com.augrain.easy.poster.geometry.CoordinatePoint;
import com.augrain.easy.poster.geometry.PositionDirection;
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
        EasyPoster poster = new EasyPoster(500, 500);

        poster.addTextElement("这是一段测试的超长文本，用来测试自动换行，目前是简单实现一版，用于验证自动换行功能逻辑，处于Alpha版")
                .setFontSize(15)
                .setFontColor(Color.red)
                .setLineHeight(50)
                .setAutoWrapText(400)
                .setPosition(AbsolutePosition.of(CoordinatePoint.of(30, 0), PositionDirection.TOP_LEFT));

        poster.asFile("png", "text_auto_split.png");
    }
}
