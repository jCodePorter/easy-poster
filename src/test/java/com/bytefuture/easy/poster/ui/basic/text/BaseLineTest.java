package com.bytefuture.easy.poster.ui.basic.text;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.BaseLine;
import org.junit.Test;

import java.awt.*;

/**
 * 测试文本基线
 *
 * @author biaoy
 * @since 2025/03/14
 */
public class BaseLineTest {

    @Test
    public void testAbsoluteBaseLine() {
        EasyPoster poster = new EasyPoster(500, 200);

        Font font = new Font("华文新魏", Font.PLAIN, 25);
        poster.addTextElement("中心对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.CENTER)
                .setPosition(AbsolutePosition.of(Point.of(0, 100), Direction.LEFT_CENTER));

        poster.addTextElement("顶部对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.TOP)
                .setPosition(AbsolutePosition.of(Point.of(120, 100), Direction.LEFT_CENTER));

        poster.addTextElement("底部对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.BOTTOM)
                .setPosition(AbsolutePosition.of(Point.of(240, 100), Direction.LEFT_CENTER));

        poster.addTextElement("基线对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.BASE_LINE)
                .setPosition(AbsolutePosition.of(Point.of(360, 100), Direction.LEFT_CENTER));

        poster.addLineElement(Point.of(0, 100), Point.of(500, 100))
                .setColor(Color.BLUE);

        poster.asFile("png", "out_text_absolute_base_line.png");
    }

}
