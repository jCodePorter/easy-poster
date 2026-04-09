package com.bytefuture.easy.poster.basic.text;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import org.junit.Test;

import java.awt.*;

/**
 * 测试自适应调整文本大小
 *
 * @author biaoy
 * @since 2025/04/09
 */
public class AutoFitTextTest {

    @Test
    public void testAutoFitTextWithinLimit() {
        EasyPoster poster = new EasyPoster(500, 200);

        // 文本可以适应目标宽度，字体大小会自动缩小
        poster.addTextElement("这是一段可以适应宽度的文本")
                .setFontSize(30)
                .setColor(Color.blue)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setAutoFitText(200, 8);

        poster.asFile("png", "out_text_auto_fit_within_limit.png");
    }

    @Test
    public void testAutoFitTextAtMinLimit() {
        EasyPoster poster = new EasyPoster(500, 200);

        // 文本很长，即使达到最小字体大小也无法单行显示，会启用自动换行
        poster.addTextElement("这是一段非常非常非常非常非常非常非常非常非常非常非常非常非常非常长的文本，即使最小字体也无法单行显示")
                .setFontSize(30)
                .setColor(Color.red)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setAutoFitText(200, 20);

        poster.asFile("png", "out_text_auto_fit_min_limit.png");
    }

    @Test
    public void testAutoFitTextNoNeedToShrink() {
        EasyPoster poster = new EasyPoster(500, 200);

        // 文本宽度小于目标宽度，不需要调整字体大小
        poster.addTextElement("短文本")
                .setFontSize(20)
                .setColor(Color.green)
                .setPosition(AbsolutePosition.of(Point.of(30, 50), Direction.TOP_LEFT))
                .setAutoFitText(200, 8);

        poster.asFile("png", "out_text_auto_fit_no_shrink.png");
    }
}